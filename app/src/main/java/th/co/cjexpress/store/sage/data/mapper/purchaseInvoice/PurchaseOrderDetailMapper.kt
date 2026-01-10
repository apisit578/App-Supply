package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDetailDTOResponse
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDetailModel

class PurchaseOrderDetailMapper {
    fun map(apiResponse: PurchaseOrderDetailDTOResponse): PurchaseOrderDetailModel =
        PurchaseOrderDetailModel(
            code = apiResponse.code,
            ref = apiResponse.ref,
            message = apiResponse.message,
            timestamp = apiResponse.timestamp,
        )
}
