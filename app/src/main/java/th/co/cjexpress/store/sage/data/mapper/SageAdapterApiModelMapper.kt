package th.co.cjexpress.store.sage.data.mapper

import th.co.cjexpress.store.sage.data.mapper.master_mapping.BdSearchTypeMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceive.DocumentSDMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceive.ShipmentDocumentByBarcodeMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceive.ShipmentDocumentMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceiveTote.OpenToteMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceiveTote.SdToteApproveMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceiveTote.SdToteDiscountsConfigMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceiveTote.SdToteListMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceiveTote.SdToteLowestFactorMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceiveTote.SdToteSubmitAdjustMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceiveTote.SdToteSubmitMapper
import th.co.cjexpress.store.sage.data.mapper.orderReceiveTote.SearchToteMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseBarcodeFindMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceCalculateMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceCreateMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceDetailMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceDocumentMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceDraftMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceSupplierMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceValidateBillMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseOrderDetailMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseOrderDocumentMapper
import th.co.cjexpress.store.sage.data.mapper.reprintQrFresh.QrDocumentSubmitMapper
import th.co.cjexpress.store.sage.data.mapper.requestDiscountRtc.BdDocumentMapper
import th.co.cjexpress.store.sage.data.mapper.requestDiscountRtc.BdDocumentNoDetailMapper
import th.co.cjexpress.store.sage.data.mapper.requestDiscountRtc.BdDocumentSubmitMapper
import th.co.cjexpress.store.sage.data.mapper.requestDiscountRtc.BdValidateBarcodeMapper
import th.co.cjexpress.store.sage.data.mapper.stock.SkuCheckMapper
import th.co.cjexpress.store.sage.data.mapper.summary.OrderReceiveSummaryMapper

class SageAdapterApiModelMapper {
    val sdToteListMapper = SdToteListMapper()
    val openToteMapper = OpenToteMapper()
    val searchToteMapper = SearchToteMapper()
    val sdToteSubmitMapper = SdToteSubmitMapper()
    val sdToteLowestFactorMapper = SdToteLowestFactorMapper()
    val sdToteApproveMapper = SdToteApproveMapper()
    val sdToteDiscountsConfigMapping = SdToteDiscountsConfigMapper()
    val sdToteSubmitAdjustMapper = SdToteSubmitAdjustMapper()
    val bdDocumentMapper = BdDocumentMapper()
    val bdDocumentNoDetailMapper = BdDocumentNoDetailMapper()
    val bdDocumentSubmitMapper = BdDocumentSubmitMapper()
    val bdValidateBarcodeMapper = BdValidateBarcodeMapper()
    val bdSearchTypeMapper = BdSearchTypeMapper()
    val shipmentDocumentMapper = ShipmentDocumentMapper()
    val skuCheckMapper = SkuCheckMapper()
    val documentSDMapper = DocumentSDMapper()
    val summaryMapper = OrderReceiveSummaryMapper()
    val shipmentDocumentByBarcodeMapper = ShipmentDocumentByBarcodeMapper()
    val purchaseInvoiceSupplierMapper = PurchaseInvoiceSupplierMapper()
    val purchaseInvoiceDocumentMapper = PurchaseInvoiceDocumentMapper()
    val purchaseOrderDocumentMapper = PurchaseOrderDocumentMapper()
    val purchaseInvoiceCalculateMapper = PurchaseInvoiceCalculateMapper()
    val purchaseInvoiceDraftMapper = PurchaseInvoiceDraftMapper()
    val purchaseInvoiceCreateMapper = PurchaseInvoiceCreateMapper()
    val purchaseInvoiceValidateBillMapper = PurchaseInvoiceValidateBillMapper()
    val purchaseOrderDetailMapper = PurchaseOrderDetailMapper()
    val purchaseInvoiceDetailMapper = PurchaseInvoiceDetailMapper()
    val purchaseBarcodeFindMapper = PurchaseBarcodeFindMapper()
    val qrDocumentSubmitMapper = QrDocumentSubmitMapper()
}
