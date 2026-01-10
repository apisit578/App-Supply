package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteLowestFactorMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SdToteLowestFactorUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private var barcode: String = ""

    fun execute(
        sdNo: String,
        itemBarcode: String,
        barcode: String,
    ): Flow<Result<SdToteLowestFactorMappingModel>> {
        this.barcode = barcode
        return if (barcode.isEmpty()) {
            flowOf(
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.WARNING,
                            message = getPleaseInputOrScanBarcodeMessage(),
                        ),
                ),
            )
        } else {
            sageApiRepository.sdToteLowestFactor(sdNo, itemBarcode).map { result ->
                when (result) {
                    is Result.Success -> validateBarcode(result, barcode)
                    is Result.Error -> handleError(result)
                }
            }
        }
    }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            "40004" ->
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.WARNING,
                            message = getBarcodeMismatchMessage(),
                        ),
                )

            else -> super.handleError(result)
        }

    private fun validateBarcode(
        result: Result.Success<SdToteLowestFactorMappingModel>,
        barcode: String,
    ): Result<SdToteLowestFactorMappingModel> =
        if (result.data.data?.barcode == barcode) {
            result
        } else {
            Result.Error(
                cause =
                    BusinessException(
                        errorCode = "",
                        type = ErrorType.WARNING,
                        message = getBarcodeMismatchMessage(),
                    ),
            )
        }

    private fun getPleaseInputOrScanBarcodeMessage(): String = resourceUtils.getString(R.string.text_please_input_or_scan_barcode)

    private fun getBarcodeMismatchMessage(): String = resourceUtils.getString(R.string.text_barcode_mismatch)
}
