package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentItemModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceItemPIUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDocumentPOUIModel
import th.co.cjexpress.store.sage.utils.CurrencyUtils
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat

class PurchaseInvoicePIUIMapper {
    fun mapDocumentListToItemsPIUIModel(
        purchaseInvoiceDocumentListModel: PurchaseInvoiceDocumentListModel,
    ): List<PurchaseInvoiceItemPIUIModel> =
        purchaseInvoiceDocumentListModel.data
            .sortedByDescending { it.piNo }
            .map {
                PurchaseInvoiceItemPIUIModel(
                    piNo = it.piNo,
                    poNo = it.poNo,
                    createdDate = it.createdDate.toStringDDMMYYYYFormat(),
                )
            }

    fun mapOrderDocumentItemsToSupplierDocumentsPOUI(
        purchaseOrderDocumentItemsModel: List<PurchaseOrderDocumentItemModel>,
    ): List<PurchaseInvoiceSupplierDocumentPOUIModel> {
        val purchaseInvoiceSupplierDocumentUIModel =
            purchaseOrderDocumentItemsModel.map {
                PurchaseInvoiceSupplierDocumentPOUIModel(
                    docNo = it.docNo,
                    shipmentDate = it.shipmentDate,
                    grandTotalAmount = it.grandTotalAmount,
                    grandTotalAmountText = CurrencyUtils.formatCurrency(it.grandTotalAmount),
                )
            }

        return purchaseInvoiceSupplierDocumentUIModel
    }

    fun mapPurchaseInvoiceDetailToUIModel(purchaseInvoiceDetailModel: PurchaseInvoiceDetailModel): PurchaseInvoiceDetailUIModel =
        PurchaseInvoiceDetailUIModel(
            supplierCode = purchaseInvoiceDetailModel.supplierCode,
            supplierName = purchaseInvoiceDetailModel.supplierName,
            poNo = purchaseInvoiceDetailModel.poNo,
            piNo = purchaseInvoiceDetailModel.piNo,
            billNo = purchaseInvoiceDetailModel.billNo,
            entries =
                purchaseInvoiceDetailModel.entries
                    .map { entry ->
                        PurchaseInvoiceEntryUIModel(
                            barcode = entry.barcode,
                            actualQty = entry.actualQty,
                            productName = entry.productName,
                            unitName = entry.unitName,
                            unitFactor = entry.unitFactor,
                            qty = entry.qty,
                            salePrice = entry.salePrice,
                        )
                    }.sortPurchaseInvoiceEntries()
                    .toMutableStateList(),
            isEdit = true,
        )

    fun mapPurchaseInvoiceCreateModelToUIModel(model: PurchaseInvoiceCreateModel): PurchaseInvoiceDetailUIModel =
        PurchaseInvoiceDetailUIModel(
            supplierCode = model.supplierCode,
            supplierName = model.supplierName,
            poNo = model.poNo,
            piNo = model.piNo,
            billNo = model.billNo,
            entries =
                mutableStateListOf<PurchaseInvoiceEntryUIModel>().apply {
                    addAll(
                        model.entries
                            .map { entry ->
                                PurchaseInvoiceEntryUIModel(
                                    barcode = entry.barcode,
                                    actualQty = entry.actualQty,
                                    productName = entry.productName,
                                    unitName = entry.unitName,
                                    unitFactor = entry.unitFactor,
                                    qty = entry.qty,
                                    salePrice = entry.salePrice,
                                )
                            }.sortPurchaseInvoiceEntries(),
                    )
                },
            isEdit = false,
        )

    private fun List<PurchaseInvoiceEntryUIModel>.sortPurchaseInvoiceEntries() =
        this
            .groupBy { entry ->
                when {
                    entry.actualQty == 0 -> 0
                    // ขาด
                    entry.actualQty > 0 && entry.actualQty < entry.qty -> 1
                    // เกิน
                    entry.actualQty > entry.qty -> 2
                    // ครบ
                    else -> 3
                }
            }.toSortedMap()
            .flatMap { (_, entries) -> entries.sortedBy { it.barcode } }
}
