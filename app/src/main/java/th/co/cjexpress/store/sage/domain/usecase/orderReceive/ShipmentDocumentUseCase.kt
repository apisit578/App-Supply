package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentItemRequest
import th.co.cjexpress.store.sage.domain.livedata.orderReceive.OrderReceiveLiveData
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel

class ShipmentDocumentUseCase(
    private val orderReceiveLiveData: OrderReceiveLiveData,
) {
    val shipmentBatchLiveData = orderReceiveLiveData.shipmentBatchUIModel
    val shipmentSummaryLiveData = orderReceiveLiveData.shipmentSummaryUIModel

    fun resetShipmentBatchModel() {
        orderReceiveLiveData.resetShipmentBatchModel()
    }

    fun resetShipmentSummaryUIModel() {
        orderReceiveLiveData.resetShipmentSummaryUIModel()
    }

    fun setShipmentBatchModel(data: ShipmentDocumentUIModel) {
        orderReceiveLiveData.setShipmentBatchModel(data)
    }

    fun setShipmentSummaryUIModel(data: ShipmentDocumentUIModel) {
        orderReceiveLiveData.setShipmentSummaryUIModel(data)
    }

    fun searchShipmentData(barcode: String): ShipmentDataItemUIModel? = orderReceiveLiveData.searchShipmentData(barcode)

    fun searchShipmentBatchData(barcode: String): ShipmentDataItemUIModel? = orderReceiveLiveData.searchShipmentBatchData(barcode)

    fun getShipmentLDNo(): String = orderReceiveLiveData.getShipmentLDNo()

    fun updateShipmentBatch(
        barcode: String,
        isIncrease: Boolean,
    ) {
        orderReceiveLiveData.updateShipmentBatch(barcode, isIncrease)
    }

    fun updateShipmentBatchFromReceiveAdjust(barcode: String, receiveAdjust: Int) {
        orderReceiveLiveData.updateShipmentBatchFromReceiveAdjust(barcode, receiveAdjust)
    }

    fun addShipmentBatchItem(data: ShipmentDataItemUIModel) {
        orderReceiveLiveData.addShipmentBatchItem(data)
    }

    fun removeShipmentBatchItem(data: ShipmentDataItemUIModel) {
        orderReceiveLiveData.removeShipmentBatchItem(data)
    }

    fun setShipmentDocumentByBarcode(data: String): List<ShipmentItemRequest> =
        orderReceiveLiveData.setIncreaseShipmentDocumentByBarcode(data)

    fun removeShipmentBatchModel() {
        orderReceiveLiveData.removeShipmentBatchModel()
    }

    fun setIncreaseShipmentDocumentByBarcode(barcode: String): List<ShipmentItemRequest> =
        orderReceiveLiveData.setIncreaseShipmentDocumentByBarcode(barcode)

    fun setReduceShipmentDocumentByBarcode(barcode: String): List<ShipmentItemRequest> =
        orderReceiveLiveData.setReduceShipmentDocumentByBarcode(barcode)

    fun updateShipmentSummary(updatedShipment: ShipmentDocumentUIModel) {
        orderReceiveLiveData.updateShipmentSummary(updatedShipment)
    }

    fun getSummaryBatchQty(): Int = orderReceiveLiveData.getSummaryBatchQty()

    fun setShipmentDocumentItemDetail(item: ShipmentDataItemUIModel) {
        orderReceiveLiveData.setShipmentDocumentItemDetail(item)
    }

    fun removeShipmentDocumentItemDetail() {
        orderReceiveLiveData.removeShipmentDocumentItemDetail()
    }

    fun getShipmentDocumentItemDetail() = orderReceiveLiveData.getShipmentDocumentItemDetail()
}
