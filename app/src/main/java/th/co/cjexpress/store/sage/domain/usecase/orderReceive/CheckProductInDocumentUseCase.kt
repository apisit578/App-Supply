package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CheckProductInDocumentUseCase(
    private val shipmentDocumentUseCase: ShipmentDocumentUseCase,
    private val resourceUtils: ResourceUtils,
) {
    fun execute(barcode: String): Result<Unit> {
        val summaryData = shipmentDocumentUseCase.searchShipmentData(barcode)

        return if (summaryData != null && summaryData.qty != 0) {
            Result.Success(Unit)
        } else {
            // return error but do nothing just get error
            Result.Error(
                cause =
                    BusinessException(
                        errorCode = "",
                        type = ErrorType.WARNING,
                        title = resourceUtils.getString(R.string.text_not_found_data),
                        message = "",
                    ),
            )
        }
    }
}
