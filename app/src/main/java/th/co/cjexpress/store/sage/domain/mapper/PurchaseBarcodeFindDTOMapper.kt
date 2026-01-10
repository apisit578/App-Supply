package th.co.cjexpress.store.sage.domain.mapper

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseBarcodeFindDTORequest

class PurchaseBarcodeFindDTOMapper {
    fun toDTO(
        barcode: String,
        supplierCode: String,
    ) = PurchaseBarcodeFindDTORequest(
        barcode = barcode,
        supplierCode = supplierCode,
    )
}
