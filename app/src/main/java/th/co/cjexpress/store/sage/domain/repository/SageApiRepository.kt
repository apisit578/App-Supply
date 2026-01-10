package th.co.cjexpress.store.sage.domain.repository

import kotlinx.coroutines.flow.Flow
import th.co.cjexpress.store.sage.constants.MasterMapping
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentItemRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustRequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseBarcodeFindDTORequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCalculateDTORequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftItemRequest
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.request.QrDocumentSubmitDTORequest
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.masterMapping.BdSearchTypeMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.SkuCheckModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteApproveMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteListMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteLowestFactorMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitAdjustModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseBarcodeFindListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceValidateBillModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentListModel
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateMappingModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import java.util.Date

interface SageApiRepository {
    fun getSdToteList(): Flow<Result<SdToteListMappingModel>>

    fun openTote(toteCode: String): Flow<Result<SdToteDetailMappingModel>>

    fun searchToteBySdNo(sdNo: String): Flow<Result<SdToteDetailMappingModel>>

    fun sdToteSubmit(
        sdNo: String,
        toteCode: String,
        barcode: String,
        skuCode: String,
        discountType: String,
        actualQty: Int,
        expList: List<ISdToteExpiryDateModel>? = null,
    ): Flow<Result<SdToteSubmitMappingModel>>

    fun sdToteLowestFactor(
        sdNo: String,
        barcode: String,
    ): Flow<Result<SdToteLowestFactorMappingModel>>

    fun sdToteApprove(sdNo: String): Flow<Result<SdToteApproveMappingModel>>

    fun sdToteDiscountsConfig(barcode: String): Flow<Result<SdToteDiscountsConfigMappingModel>>

    fun bdDocument(): Flow<Result<BdDocumentMappingModel>>

    fun bdDocumentNoDetail(docNo: String): Flow<Result<BdDocumentNoDetailMappingModel>>

    fun bdDocumentSubmit(
        documentNumber: String,
        discountType: String,
        items: List<BdDetailProductUIModel>,
    ): Flow<Result<BdDocumentSubmitMappingModel>>

    fun bdDocumentSearchType(type: MasterMapping): Flow<Result<BdSearchTypeMappingModel>>

    fun bdDocumentValidate(
        discountType: String,
        barcode: String,
        expiryDate: Date? = null,
    ): Flow<Result<BdDocumentValidateMappingModel>>

    fun getSdDocList(): Flow<Result<DocumentSDModel>>

    fun skuCheck(barcode: String): Flow<Result<SkuCheckModel>>

    fun getShipmentDocumentBySDNo(sdNo: String): Flow<Result<ShipmentDocumentModel>>

    fun shipmentDocumentOpen(ldNo: String): Flow<Result<ShipmentDocumentModel>>

    fun getShipmentDocumentSummaryBySd(sdNo: String): Flow<Result<OrderReceiveSummaryModel>>

    fun shipmentDocumentByBarcode(
        sdNo: String,
        data: List<ShipmentItemRequest>,
    ): Flow<Result<ShipmentDocumentByBarcodeModel>>

    fun getFrequencySupplier(perPage: String): Flow<Result<PurchaseInvoiceSupplierListModel>>

    fun filterSupplier(searchKey: String): Flow<Result<PurchaseInvoiceSupplierListModel>>

    fun getSupplierByCode(searchCode: String): Flow<Result<PurchaseInvoiceSupplierModel>>

    fun getPurchaseInvoiceDocument(supplierCode: String): Flow<Result<PurchaseInvoiceDocumentListModel>>

    fun getPurchaseOrderDocument(supplierCode: String): Flow<Result<PurchaseOrderDocumentListModel>>

    fun purchaseInvoiceCalculate(data: PurchaseInvoiceCalculateDTORequest): Flow<Result<PurchaseInvoiceCalculateModel>>

    fun saveDraftPurchaseInvoice(
        piNo: String,
        docNo: String,
        flagPO: Int,
        supplierCode: String,
        billNo: String,
        timestamp: Long,
        items: List<PurchaseInvoiceDraftItemRequest>,
    ): Flow<Result<PurchaseInvoiceDraftModel>>

    fun getPurchaseOrderDetailByDocNo(
        docNo: String,
        supplierCode: String,
    ): Flow<Result<PurchaseOrderDetailModel>>

    fun validatePurchaseInvoiceBill(
        billNo: String,
        piNo: String,
        supplierCode: String,
    ): Flow<Result<PurchaseInvoiceValidateBillModel>>

    fun createPurchaseInvoice(
        poNo: String,
        billNo: String,
        supplierCode: String,
    ): Flow<Result<PurchaseInvoiceCreateModel>>

    fun getPurchaseInvoiceDetail(piNo: String): Flow<Result<PurchaseInvoiceDetailModel>>

    fun purchaseBarcodeFind(data: PurchaseBarcodeFindDTORequest): Flow<Result<PurchaseBarcodeFindListModel>>

    fun qrDocumentSubmit(data: QrDocumentSubmitDTORequest): Flow<Result<QrDocumentSubmitMappingModel>>

    fun shipmentToteDocumentSubmitAdjust(data: SdToteSubmitAdjustRequest): Flow<Result<SdToteSubmitAdjustModel>>
}
