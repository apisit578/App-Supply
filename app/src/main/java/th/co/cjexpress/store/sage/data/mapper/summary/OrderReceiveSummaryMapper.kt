package th.co.cjexpress.store.sage.data.mapper.summary

import th.co.cjexpress.store.sage.data.model.orderReceive.OrderReceiveSummaryItemResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.OrderReceiveSummaryResponse
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryItemModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel

class OrderReceiveSummaryMapper {
    fun map(orderReceiveSummaryResponse: OrderReceiveSummaryResponse): OrderReceiveSummaryModel =
        OrderReceiveSummaryModel(
            code = orderReceiveSummaryResponse.code,
            data =
                OrderReceiveSummaryDataModel(
                    items =
                        orderReceiveSummaryResponse.data
                            ?.items
                            ?.map { it.toOrderReceiveSummaryDataModel() }
                            ?.toMutableList()
                            ?: mutableListOf(),
                    sdNo = orderReceiveSummaryResponse.data?.sdNo ?: "",
                    createdDate = orderReceiveSummaryResponse.data?.createdDate ?: "",
                ),
            message = orderReceiveSummaryResponse.message,
            ref = orderReceiveSummaryResponse.ref,
        )

    private fun OrderReceiveSummaryItemResponse.toOrderReceiveSummaryDataModel(): OrderReceiveSummaryItemModel =
        OrderReceiveSummaryItemModel(
            actualQty = this.actualQty,
            barcode = this.barcode,
            diffQty = this.diffQty,
            isTote = this.isTote,
            productName = this.productName,
            qty = this.qty,
            toteCode = this.toteCode,
            unitName = this.unitName,
            unitFactor = this.unitFactor,
        )
}
