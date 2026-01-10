package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SdToteDiscountsConfigUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(barcode: String): Flow<Result<SdToteDiscountsConfigModel>> =
        if (barcode.isEmpty()) {
            flowOf(
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.INVALID_BODY,
                            type = ErrorType.WARNING,
                            message = getPleaseInputOrScanBarcodeMessage(),
                        ),
                ),
            )
        } else {
            sageApiRepository.sdToteDiscountsConfig(barcode).map { result ->
                when (result) {
                    is Result.Success -> validateBarcode(result, barcode)
                    is Result.Error ->
                        handleError(
                            Result.Error(cause = result.cause, data = null, canRetry = true),
                        )
                }
            }
        }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            ErrorCodes.NOT_FOUND -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_title_barcode_mismatch),
                            message = resourceUtils.getString(R.string.text_please_check_and_scan_again),
                        ),
                )
            }

            ErrorCodes.INVALID_PARAMETER -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.WARNING,
                            message = result.cause.message,
                        ),
                )
            }

            else -> super.handleError(result)
        }

    private fun validateBarcode(
        result: Result.Success<SdToteDiscountsConfigMappingModel>,
        barcode: String,
    ): Result<SdToteDiscountsConfigModel> {
        val validBarcode = result.data.data?.firstOrNull { it.barcode == barcode }
        return if (validBarcode != null) {
            Result.Success(data = validBarcode)
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
    }

    private fun getPleaseInputOrScanBarcodeMessage(): String = resourceUtils.getString(R.string.text_please_input_or_scan_barcode)

    private fun getBarcodeMismatchMessage(): String = resourceUtils.getString(R.string.text_barcode_or_qr_mismatch)
}
