package th.co.cjexpress.store.sage.domain.livedata.orderReceive

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentItemRequest
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderReceiveLiveData
    @Inject
    constructor() {
        private val _shipmentBatchUIModel = MutableLiveData<ShipmentDocumentUIModel?>()
        val shipmentBatchUIModel: LiveData<ShipmentDocumentUIModel?> get() = _shipmentBatchUIModel

        private val _shipmentSummaryUIModel = MutableLiveData<ShipmentDocumentUIModel?>()
        val shipmentSummaryUIModel: LiveData<ShipmentDocumentUIModel?> get() = _shipmentSummaryUIModel

        private val _shipmentDataItemUIModel = MutableLiveData<ShipmentDataItemUIModel?>()
        val shipmentDataItemUIModel: LiveData<ShipmentDataItemUIModel?> get() = _shipmentDataItemUIModel

        fun setShipmentBatchModel(data: ShipmentDocumentUIModel) {
            _shipmentBatchUIModel.value = data
        }

        fun resetShipmentBatchModel() {
            _shipmentBatchUIModel.value = null
        }

        fun setShipmentSummaryUIModel(data: ShipmentDocumentUIModel) {
            _shipmentSummaryUIModel.value = data
            setShipmentBatchModel(data.copy(items = mutableStateListOf()))
        }

        fun resetShipmentSummaryUIModel() {
            _shipmentSummaryUIModel.value = null
        }

        fun searchShipmentData(barcode: String): ShipmentDataItemUIModel? =
            _shipmentSummaryUIModel.value?.let { shipmentSummaryData ->
                shipmentSummaryData.items.find { it.barcode == barcode }
            }

        fun searchShipmentBatchData(barcode: String): ShipmentDataItemUIModel? =
            _shipmentBatchUIModel.value?.let { shipmentBatchData ->
                shipmentBatchData.items.find { it.barcode == barcode }
            }

        fun getShipmentLDNo() = _shipmentSummaryUIModel.value?.ldNo.orEmpty()

        fun calculateSummaryOverQty(
            barcode: String,
            actualQty: Int,
        ): Int =
            _shipmentSummaryUIModel.value
                ?.items
                ?.find { it.barcode == barcode }
                ?.let { summaryItem ->
                    summaryItem.actualQty - summaryItem.qty
                } ?: actualQty

        fun updateShipmentBatch(
            barcode: String,
            isIncrease: Boolean,
        ) {
            _shipmentBatchUIModel.value?.let { shipmentBatchData ->
                shipmentBatchData.items.find { it.barcode == barcode }?.let { batchItem ->
                    shipmentBatchData.items.remove(batchItem)
                    if (isIncrease) {
                        if (batchItem.isTote) {
                            shipmentBatchData.items.add(0, batchItem)
                        } else {
                            val updatingBatchItem =
                                batchItem
                                    .copy(actualQty = batchItem.actualQty + 1)
                                    .also { updated ->
                                        updated.overQty =
                                            calculateSummaryOverQty(
                                                barcode = barcode,
                                                actualQty = updated.actualQty,
                                            )
                                    }
                            shipmentBatchData.items.add(0, updatingBatchItem)
                            shipmentBatchData.sumOfActualQty++
                        }
                    } else {
                        val deduct = batchItem.actualQty.coerceAtLeast(0)
                        shipmentBatchData.sumOfActualQty =
                            (shipmentBatchData.sumOfActualQty - deduct).coerceAtLeast(0)
                    }
                }
            }
        }

        fun updateShipmentBatchFromReceiveAdjust(
            barcode: String,
            receiveAdjust: Int,
        ) {
            _shipmentBatchUIModel.value?.let { shipmentBatchData ->
                shipmentBatchData.items.find { it.barcode == barcode }?.let { batchItem ->
                    shipmentBatchData.items.remove(batchItem)

                    if (batchItem.isTote) {
                        shipmentBatchData.items.add(0, batchItem)
                    } else {
                        val updatingBatchItem =
                            batchItem.copy(actualQty = batchItem.actualQty + receiveAdjust)
                        updatingBatchItem.overQty =
                            calculateSummaryOverQty(
                                barcode = barcode,
                                actualQty = updatingBatchItem.actualQty,
                            )

                        shipmentBatchData.items.add(
                            index = 0,
                            updatingBatchItem,
                        )

                        shipmentBatchData.sumOfActualQty += receiveAdjust
                    }
                }
            }
        }

        fun addShipmentBatchItem(data: ShipmentDataItemUIModel) {
            val shipmentDataItem =
                ShipmentDataItemUIModel(
                    actualQty = 1,
                    barcode = data.barcode,
                    isTote = data.isTote,
                    toteCode = data.toteCode,
                    productName = data.productName,
                    qty = data.qty,
                    unitName = data.unitName,
                    unitFactor = data.unitFactor,
                ).apply {
                    this.overQty =
                        if (data.isTote) {
                            if (data.qty == 0) actualQty else 0
                        } else {
                            calculateSummaryOverQty(barcode = data.barcode, actualQty = this.actualQty)
                        }
                }

            _shipmentBatchUIModel.value?.let { shipmentBatchData ->
                shipmentBatchData.items.add(
                    index = 0,
                    shipmentDataItem,
                )
                shipmentBatchData.sumOfActualQty++
            }
//            if (data.qty == 0) {
//                _shipmentSummaryUIModel.value?.items?.add(
//                    index = 0,
//                    shipmentDataItem,
//                )
//            }
        }

        fun getSdNo(): String = shipmentSummaryUIModel.value?.sdNo ?: ""

        fun removeShipmentBatchItem(data: ShipmentDataItemUIModel) {
            _shipmentBatchUIModel.value?.let { shipmentBatchData ->

                val itemsToRemove = shipmentBatchData.items.filter { it.barcode == data.barcode }
                shipmentBatchData.items.removeAll(itemsToRemove)

                val totalRemovedQty = itemsToRemove.sumOf { it.actualQty }
                shipmentBatchData.sumOfActualQty -= totalRemovedQty
            }
        }

        fun setIncreaseShipmentDocumentByBarcode(barcode: String): List<ShipmentItemRequest> {
            val item =
                _shipmentSummaryUIModel.value
                    ?.items
                    ?.find { it.barcode == barcode }

            val sumActualQty =
                when {
                    item == null -> 1
                    item.isTote && item.actualQty == 1 -> 1
                    else -> item.actualQty + 1
                }

            return listOf(
                ShipmentItemRequest(
                    barcode = barcode,
                    actualQty = sumActualQty,
                ),
            )
        }

        fun setReduceShipmentDocumentByBarcode(barcode: String): List<ShipmentItemRequest> {
            val summaryActual =
                _shipmentSummaryUIModel.value
                    ?.items
                    ?.find { it.barcode == barcode }
                    ?.actualQty ?: 0

            val scannedInBatch =
                _shipmentBatchUIModel.value
                    ?.items
                    ?.find { it.barcode == barcode }
                    ?.actualQty ?: 0

            val reducedQty = (summaryActual - scannedInBatch)

            return listOf(
                ShipmentItemRequest(
                    barcode = barcode,
                    actualQty = reducedQty,
                ),
            )
        }

        fun removeShipmentBatchModel() {
            _shipmentBatchUIModel.value?.let { batchItem ->
                batchItem.items.clear()
                batchItem.sumOfActualQty = 0
            }
        }

        fun updateShipmentSummary(updatedShipment: ShipmentDocumentUIModel) {
            _shipmentSummaryUIModel.value?.let { summaryData ->
                updatedShipment.items.forEach { incomingItem ->
                    val matchedItem = summaryData.items.find { it.barcode == incomingItem.barcode }
                    matchedItem?.let { updateItem -> summaryData.items.remove(updateItem) }
                    summaryData.items.add(0, incomingItem)
                }
            }
        }

        fun getSummaryBatchQty(): Int =
            _shipmentBatchUIModel.value
                ?.items
                .orEmpty()
                .sumOf { it.actualQty }

        fun setShipmentDocumentItemDetail(item: ShipmentDataItemUIModel) {
            _shipmentDataItemUIModel.value = item
        }

        fun removeShipmentDocumentItemDetail() {
            _shipmentDataItemUIModel.value = null
        }

        fun getShipmentDocumentItemDetail() =
            shipmentDataItemUIModel.value ?: ShipmentDataItemUIModel(
                actualQty = 0,
                barcode = "",
                isTote = false,
                toteCode = "",
                productName = "",
                qty = 0,
                unitName = "",
                unitFactor = 0,
            )
    }
