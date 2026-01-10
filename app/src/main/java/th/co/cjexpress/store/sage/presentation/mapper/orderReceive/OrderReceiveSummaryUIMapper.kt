package th.co.cjexpress.store.sage.presentation.mapper.orderReceive

import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryCompleteUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryUIModel

class OrderReceiveSummaryUIMapper {
    companion object {
        const val CONST_STATUS_MISSING = "ขาด"
        const val CONST_STATUS_OVER = "เกิน"
        const val CONST_PRODUCT_NAME_TOTE = "Tote"
        const val CONST_DEFAULT_PRODUCT_UNIT_NAME = "ลัง"
    }

    fun mapToOrderReceiveSummaryUIModel(orderReceiveSummaryModel: OrderReceiveSummaryModel): OrderReceiveSummaryUIModel {
        val orderReceiveSummaryUIModel =
            OrderReceiveSummaryUIModel(
                sdNo = orderReceiveSummaryModel.data.sdNo,
                items =
                    orderReceiveSummaryModel.data.items.map { item ->
                        val status: Boolean =
                            item.actualQty < item.qty // check status check is overs or missing
                        val isTote: Boolean = item.isTote
                        val productName: String =
                            when (isTote) {
                                true -> {
                                    CONST_PRODUCT_NAME_TOTE
                                }

                                else -> {
                                    item.productName
                                }
                            }

                        OrderReceiveSummaryItemUIModel(
                            barcode = item.barcode,
                            productName = productName,
                            unitCount = "${item.actualQty}/${item.qty}",
                            unitName =
                                when (isTote) {
                                    true -> CONST_DEFAULT_PRODUCT_UNIT_NAME
                                    else -> item.unitName
                                },
                            chipText =
                                when (status) {
                                    true -> "$CONST_STATUS_MISSING ${item.qty - item.actualQty}"
                                    else -> "$CONST_STATUS_OVER ${item.actualQty - item.qty}"
                                },
                            isMissing = status,
                        )
                    },
            )

        return orderReceiveSummaryUIModel
    }

    fun mapToOrderReceiveSummaryCompleteUIModel(
        sdNo: String,
        isComplete: Boolean,
    ): OrderReceiveSummaryCompleteUIModel = (OrderReceiveSummaryCompleteUIModel(sdNo = sdNo, isComplete = isComplete))
}
