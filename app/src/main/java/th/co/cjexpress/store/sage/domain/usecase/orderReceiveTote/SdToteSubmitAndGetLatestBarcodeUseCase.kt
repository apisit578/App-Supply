package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitAndGetLatestBarcodeModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SdToteSubmitAndGetLatestBarcodeUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun execute(
        sdNo: String,
        toteCode: String,
        barcode: String,
        skuCode: String,
        discountType: String,
        actualQty: Int,
        expList: List<ISdToteExpiryDateModel>?,
    ): Flow<Result<SdToteSubmitAndGetLatestBarcodeModel>> =
        sageApiRepository
            .sdToteSubmit(
                sdNo = sdNo,
                toteCode = toteCode,
                barcode = barcode,
                skuCode = skuCode,
                discountType = discountType,
                actualQty = actualQty,
                expList = expList,
            ).flatMapMerge { submitResult ->
                when (submitResult) {
                    is Result.Success -> {
                        searchToteBySdNo(sdNo, barcode)
                    }

                    is Result.Error ->
                        flowOf(super.handleError(Result.Error(cause = submitResult.cause, data = null)))
                }
            }

    private fun searchToteBySdNo(
        sdNo: String,
        barcode: String,
    ): Flow<Result<SdToteSubmitAndGetLatestBarcodeModel>> =
        sageApiRepository
            .searchToteBySdNo(sdNo)
            .map { sdToteDetailResult ->
                when (sdToteDetailResult) {
                    is Result.Success ->
                        validateBarcode(sdToteDetailResult.data, barcode)

                    is Result.Error ->
                        super.handleError(
                            Result.Error(cause = sdToteDetailResult.cause, data = null),
                        )
                }
            }

    private fun validateBarcode(
        submitResult: SdToteDetailMappingModel,
        barcode: String,
    ): Result<SdToteSubmitAndGetLatestBarcodeModel> {
        val latestBarcode =
            submitResult.data.items
                .firstOrNull { it.barcode == barcode }
        return if (latestBarcode != null) {
            Result.Success(
                SdToteSubmitAndGetLatestBarcodeModel(
                    sdNo = submitResult.data.sdNo,
                    latestBarcode = latestBarcode,
                ),
            )
        } else {
            Result.Error(
                cause =
                    BusinessException(
                        errorCode = "",
                        type = ErrorType.ERROR,
                        message = getBarcodeMismatchMessage(),
                    ),
                data = null,
            )
        }
    }

    private fun getBarcodeMismatchMessage(): String = resourceUtils.getString(R.string.text_barcode_or_qr_mismatch)
}
