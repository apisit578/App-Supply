package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceValidateBillDTOResponse
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceValidateBillModel

class PurchaseInvoiceValidateBillMapper {
    fun map(apiResponse: PurchaseInvoiceValidateBillDTOResponse): PurchaseInvoiceValidateBillModel =
        PurchaseInvoiceValidateBillModel(
            isValid = apiResponse.data.isValid,
            code = apiResponse.code,
            ref = apiResponse.ref,
            message = apiResponse.message,
            timestamp = apiResponse.timestamp,
        )
}
