package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseBarcodeFindDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseBarcodeFindDataDTOResponse
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseBarcodeFindItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseBarcodeFindListModel

class PurchaseBarcodeFindMapper {
    fun map(apiResponse: PurchaseBarcodeFindDTOResponse): PurchaseBarcodeFindListModel =
        PurchaseBarcodeFindListModel(
            code = apiResponse.code,
            ref = apiResponse.ref,
            message = apiResponse.message,
            data = apiResponse.data.map { it.toPurchaseBarcodeFindItemModel() },
            timestamp = apiResponse.timestamp,
        )

    private fun PurchaseBarcodeFindDataDTOResponse.toPurchaseBarcodeFindItemModel() =
        PurchaseBarcodeFindItemModel(
            isAllowBuy = isAllowBuy,
            barFactor = barFactor,
            barcode = barcode,
            isFreshLife = isFreshLife,
            productName = productName,
            unitCode = unitCode,
            unitName = unitName,
        )
}
