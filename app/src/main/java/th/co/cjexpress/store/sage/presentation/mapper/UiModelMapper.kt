package th.co.cjexpress.store.sage.presentation.mapper

import th.co.cjexpress.store.sage.presentation.mapper.barcodeCodec.BarcodeCodecResultMapper
import th.co.cjexpress.store.sage.presentation.mapper.orderReceive.OrderReceiveSummaryUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.orderReceive.ShipmentDocumentByBarcodeUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.orderReceive.ShipmentDocumentListUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.orderReceive.ShipmentDocumentUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.orderReceiveTote.SdToteDiscountsConfigUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.orderReceiveTote.SdToteDocDetailsUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.orderReceiveTote.SdToteExpMapper
import th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice.PurchaseBarcodeFindUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice.PurchaseInvoiceCalculateUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice.PurchaseInvoicePIUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice.PurchaseInvoicePOUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice.PurchaseInvoiceSupplierListUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice.PurchaseInvoiceSupplierUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.reprintQrFresh.ReprintQrFreshSubmitUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.requestDiscountRtc.BdDocumentNoDetailUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.requestDiscountRtc.BdDocumentSubmitUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.requestDiscountRtc.BdDocumentUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.requestDiscountRtc.BdDocumentValidateUIMapper
import th.co.cjexpress.store.sage.presentation.mapper.requestDiscountRtc.BdSearchTypeUIMapper

class UiModelMapper {
    val sdToteExpMapper = SdToteExpMapper()
    val sdToteDocDetailUIMapper = SdToteDocDetailsUIMapper()
    val sdToteDiscountsConfigUIMapper = SdToteDiscountsConfigUIMapper()
    val barcodeCodecMapper = BarcodeCodecResultMapper()
    val shipmentDocumentUIMapper = ShipmentDocumentUIMapper()
    val shipmentDocumentListUIMapper = ShipmentDocumentListUIMapper()
    val orderReceiveSummaryUIMapper = OrderReceiveSummaryUIMapper()
    val shipmentDocumentByBarcodeUIMapper = ShipmentDocumentByBarcodeUIMapper()
    val purchaseInvoiceSupplierListUIMapper = PurchaseInvoiceSupplierListUIMapper()
    val purchaseInvoiceSupplierUIMapper = PurchaseInvoiceSupplierUIMapper()
    val purchaseInvoicePIUIMapper = PurchaseInvoicePIUIMapper()
    val purchaseInvoicePOUIMapper = PurchaseInvoicePOUIMapper()
    val purchaseInvoiceCalculateUIMapper = PurchaseInvoiceCalculateUIMapper()
    val bdDocumentUIMapper = BdDocumentUIMapper()
    val bdDocumentNoDetailUIMapper = BdDocumentNoDetailUIMapper()
    val bdSearchTypeUIMapper = BdSearchTypeUIMapper()
    val bdDocumentSubmitUIMapper = BdDocumentSubmitUIMapper()
    val bdDocumentValidateUIMapper = BdDocumentValidateUIMapper()
    val purchaseBarcodeFindUIMapper = PurchaseBarcodeFindUIMapper()
    val reprintQrFreshSubmitUIMapper = ReprintQrFreshSubmitUIMapper()
}
