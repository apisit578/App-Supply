package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class OrderReceiveCheckSkuUseCase(
    private val repository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(barcode: String): Flow<Result<ShipmentDataItemUIModel>> =
        repository.skuCheck(barcode).map { result ->
            when (result) {
                is Result.Success -> {
                    result.data.data.barcodes.find { it.barcode == barcode }?.let { b ->
                        Result.Success(
                            ShipmentDataItemUIModel(
                                barcode = barcode,
                                actualQty = 1,
                                isTote = false,
                                productName = result.data.data.skuName,
                                qty = 0,
                                toteCode = "",
                                unitFactor = b.barcodeFactor,
                                unitName = b.unitName,
                            ),
                        )
                    } ?: Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.NOT_FOUND,
                                type = ErrorType.WARNING,
                                title = resourceUtils.getString(R.string.text_title_barcode_mismatch),
                                message = resourceUtils.getString(R.string.text_please_check_and_scan_again),
                            ),
                    )
                }

                is Result.Error -> handleError(Result.Error(cause = result.cause))
            }
        }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            ErrorCodes.NOT_FOUND, ErrorCodes.INVALID_PARAMETER -> {
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
            else -> super.handleError(result)
        }
}
