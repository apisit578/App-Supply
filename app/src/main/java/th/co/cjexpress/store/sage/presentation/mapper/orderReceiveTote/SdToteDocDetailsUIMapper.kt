package th.co.cjexpress.store.sage.presentation.mapper.orderReceiveTote

import androidx.compose.runtime.toMutableStateList
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailData
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItem
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItemExpiryDateLot
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeExpiryUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailUIModel

class SdToteDocDetailsUIMapper {
    fun toUIModel(data: SdToteDetailData): SdToteDocsDetailUIModel {
        val groups = mutableListOf<SdToteDetailItem>()

        val notReceive = data.items.filter { it.actualQty == 0 }
        if (notReceive.isNotEmpty()) groups += notReceive.sortedBy { it.barcode }

        val insufficient = data.items.filter { it.actualQty > 0 && it.actualQty < it.qty }
        if (insufficient.isNotEmpty()) groups += insufficient.sortedBy { it.barcode }

        val over = data.items.filter { it.actualQty > it.qty }
        if (over.isNotEmpty()) groups += over.sortedBy { it.barcode }

        val match = data.items.filter { it.actualQty > 0 && it.actualQty == it.qty }
        if (match.isNotEmpty()) groups += match.sortedBy { it.barcode }

        return SdToteDocsDetailUIModel(
            sdNo = data.sdNo,
            toteCode = data.toteCode,
            receivedDate = data.receivedDate,
            items = groups.map { toUIModel(it) }.toMutableStateList(),
        )
    }

    fun toUIModel(data: SdToteDetailItem): SdToteDocsDetailBarcodeUIModel {
        val mappedExpList =
            when (val list = data.expList) {
                null -> mutableListOf()
                else ->
                    list
                        .sortedBy { it.expDate }
                        .map {
                            SdToteDocsDetailBarcodeExpiryUIModel(
                                expDate = it.expDate,
                                actualQty = it.actualQty,
                                qty = it.qty,
                                lotNo = it.lotNo,
                            )
                        }.toMutableList()
            }

        return SdToteDocsDetailBarcodeUIModel(
            actualQty = data.actualQty,
            barcode = data.barcode,
            barFactor = data.barFactor,
            discountType = data.discountType,
            noOfExp = data.noOfExp,
            productName = data.productName,
            qty = data.qty,
            unitName = data.unitName,
            skuCode = data.skuCode,
            expList = mappedExpList.toMutableStateList(),
        )
    }

    fun toUIModel(data: SdToteDetailItemExpiryDateLot): SdToteDocsDetailBarcodeExpiryUIModel =
        SdToteDocsDetailBarcodeExpiryUIModel(
            expDate = data.expDate,
            actualQty = data.actualQty,
            qty = data.qty,
            lotNo = data.lotNo,
        )
}
