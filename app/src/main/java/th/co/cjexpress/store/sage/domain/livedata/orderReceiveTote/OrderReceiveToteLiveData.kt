package th.co.cjexpress.store.sage.domain.livedata.orderReceiveTote

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import th.co.cjexpress.store.sage.constants.OrderReceiveToteLotType
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeExpiryUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.firstOrNull

@Singleton
class OrderReceiveToteLiveData
    @Inject
    constructor() {
        private val _sdToteSubmitUIModel = MutableLiveData<SdToteSubmitUIModel?>(null)
        val sdToteSubmitUIModel: LiveData<SdToteSubmitUIModel?> get() = _sdToteSubmitUIModel

        private val _sdToteDocsDetailUIModel = MutableLiveData<SdToteDocsDetailUIModel?>()
        val sdToteDocsDetailUIModel: LiveData<SdToteDocsDetailUIModel?> get() = _sdToteDocsDetailUIModel

//    private val _sdToteDocsDetailBarcodeUIModel = MutableLiveData<SdToteDocsDetailBarcodeUIModel?>()
//    val sdToteDocsDetailBarcodeUIModel: LiveData<SdToteDocsDetailBarcodeUIModel?> get() = _sdToteDocsDetailBarcodeUIModel

        fun setToteSubmitUIModel(data: SdToteSubmitUIModel) {
            val beforeQtyMap =
                data.expList
                    .groupBy { it.expDate?.toStringDDMMYYYYFormat() }
                    .mapValues { (_, items) -> items.sumOf { it.actualQty } }
                    .filterKeys { it != null }
                    .mapKeys { (date, _) -> date }

            data.beforeQty = beforeQtyMap

            _sdToteSubmitUIModel.value = data
        }

        fun resetSdToteSubmitUIModel() {
            _sdToteSubmitUIModel.value = null
        }

        fun setSdToteDocsDetailUIModel(data: SdToteDocsDetailUIModel) {
            _sdToteDocsDetailUIModel.value = data
        }

        fun resetSdToteDocsDetailUIModel() {
            _sdToteDocsDetailUIModel.value = null
        }

        fun searchBarcodeInTote(barcode: String): SdToteDocsDetailBarcodeUIModel? =
            _sdToteDocsDetailUIModel.value?.items?.firstOrNull {
                it.barcode == barcode
            }

        fun getSdToteDocNo(): String = _sdToteDocsDetailUIModel.value?.sdNo ?: ""

        fun getSdToteCode(): String = _sdToteDocsDetailUIModel.value?.toteCode ?: ""

        fun getReceivedDate(): Date = _sdToteDocsDetailUIModel.value?.receivedDate ?: Date()

        fun moveExpiryDateLotToFirst(expiryDate: Date) {
            _sdToteSubmitUIModel.value?.let { submitData ->
                submitData.expList
                    .indexOfFirst {
                        it.expDate?.toStringDDMMYYYYFormat() == expiryDate.toStringDDMMYYYYFormat()
                    }.let { index ->
                        if (index > 0) {
                            val movingExpiryDateLot = submitData.expList.removeAt(index)
                            submitData.expList.add(0, movingExpiryDateLot)
                        }
                    }
            }
        }

        fun updateReceivingBarcode(
            barcode: String,
            actualQty: Int,
            expList: List<SdToteDocsDetailBarcodeExpiryUIModel>?,
        ) {
            _sdToteDocsDetailUIModel.value?.let { sdDetail ->
                val existingBarcode = sdDetail.items.firstOrNull { it.barcode == barcode }
                if (existingBarcode != null) {
                    sdDetail.items.remove(existingBarcode)

                    existingBarcode.actualQty = actualQty
                    existingBarcode.expList.clear()
                    if (expList != null) {
                        existingBarcode.expList.addAll(expList)
                    }
                    sdDetail.items.add(0, existingBarcode)
                }
            }
        }

        fun addNewReceivingBarcode(receivingProduct: SdToteDocsDetailBarcodeUIModel) {
            _sdToteDocsDetailUIModel.value?.let { sdDetail ->
                val existingProduct =
                    sdDetail.items.firstOrNull { it.barcode == receivingProduct.barcode }
                if (existingProduct != null) {
                    sdDetail.items.remove(existingProduct)
                }
                sdDetail.items.add(0, receivingProduct)
            }
        }

        fun updateOrAddSubmitExpiryDate(
            expiryDate: Date,
            isNoQR: Boolean,
        ) {
            _sdToteSubmitUIModel.value?.let { submitData ->
                val isMatchingDateFound =
                    submitData.expList.any {
                        it.expDate?.toStringDDMMYYYYFormat() == expiryDate.toStringDDMMYYYYFormat()
                    }
                val formattedDate = expiryDate.toStringDDMMYYYYFormat()
                val (expiredListByDate) =
                    submitData.expList
                        .partition {
                            it.expDate?.toStringDDMMYYYYFormat() == formattedDate
                        }.let {
                            it.first.partition { item ->
                                item.lotNo != OrderReceiveToteLotType.LOTDUMMY.name
                            } to it.second
                        }
                val expListQRCode = expiredListByDate.first
                val expListBarCode = expiredListByDate.second

                if (isNoQR) {
                    // กรณีสินค้ามี QR Code
                    if ((isMatchingDateFound && expListBarCode.isEmpty()) || !isMatchingDateFound) {
                        // "มีวันที่ตรงกันในเอกสาร และ ไม่มี Lot Dummy ในวันที่กำหนด" หรือ "ไม่มีวันที่ัในเอกสาร" -> เพิ่ม Lot LOTDUMMY
                        addExpiryDateByLot(
                            submitData = submitData,
                            expiryDate = expiryDate,
                            lotNo = OrderReceiveToteLotType.LOTDUMMY.name,
                        )
                    } else {
                        // มี Lot Dummy ในวันที่กำหนด -> อัพเดท Lot LOTDUMMY
                        updateLotData(
                            submitData = submitData,
                            expiryDate = expiryDate,
                            lotNo = OrderReceiveToteLotType.LOTDUMMY.name,
                        )
                    }
                } else {
                    // กรณีสินค้าไม่มี QR Code
                    if ((isMatchingDateFound && expListQRCode.isEmpty()) || !isMatchingDateFound) {
                        // "มีวันที่ตรงกันในเอกสาร และ ไม่มี Lot สินค้า ในวันที่กำหนด" หรือ "ไม่มีวันที่ัในเอกสาร" -> เพิ่ม Lot MANUAL_ADJUST
                        addExpiryDateByLot(
                            submitData = submitData,
                            expiryDate = expiryDate,
                            lotNo = OrderReceiveToteLotType.MANUAL_ADJUST.name,
                        )
                    } else {
                        // มี Lot สินค้า ในวันที่กำหนด -> อัพเดท Lot สินค้า
                        val submitDataSortedList = expiredListByDate.first.sortByAscending()
                        val item = submitDataSortedList.getIncrementLotData()
                        val updatedIndex = submitData.expList.indexOfFirst { it == item }
                        updateExistExpiryDate(
                            submitData = submitData,
                            index = updatedIndex,
                            add = true,
                        )
                    }
                }

                submitData.actualQty = submitData.expList.sumOf { it.actualQty }
                Timber.Forest.d("Update")
                Timber.Forest.d(submitData.expList.toString())
            }
        }

        fun incrementFreshLifeExpiryDate(
            expiryDate: Date?,
            isLotDummy: Boolean,
        ) {
            _sdToteSubmitUIModel.value?.let { submitData ->
                val formattedDate = expiryDate?.toStringDDMMYYYYFormat()
                val updateItem =
                    submitData.expList.firstOrNull { exp ->
                        exp.expDate?.toStringDDMMYYYYFormat() == formattedDate &&
                            exp.lotNo.isLotDummy() == isLotDummy
                    }
                if (updateItem != null) {
                    val index = submitData.expList.indexOf(updateItem)
                    submitData.expList.removeAt(index)
                    updateItem.actualQty += 1
                    submitData.expList.add(index, updateItem)
                }
                submitData.actualQty = submitData.expList.sumOf { it.actualQty }
            }
        }

        fun decreaseFreshLifeExpiryDate(
            expiryDate: Date?,
            isLotDummy: Boolean,
        ) {
            _sdToteSubmitUIModel.value?.let { submitData ->
                val formattedDate = expiryDate?.toStringDDMMYYYYFormat()
                val updateItem =
                    submitData.expList.firstOrNull { exp ->
                        exp.expDate?.toStringDDMMYYYYFormat() == formattedDate &&
                            exp.lotNo.isLotDummy() == isLotDummy &&
                            exp.actualQty > 0
                    }
                if (updateItem != null) {
                    val index = submitData.expList.indexOf(updateItem)
                    submitData.expList.removeAt(index)
                    updateItem.actualQty -= 1
                    submitData.expList.add(index, updateItem)
                }
                submitData.actualQty = submitData.expList.sumOf { it.actualQty }
            }
        }

        fun updateLotData(
            submitData: SdToteSubmitUIModel,
            expiryDate: Date?,
            lotNo: String,
        ) {
            val indexManualAdjust =
                submitData.expList.indexOfFirst {
                    it.lotNo == lotNo && it.expDate?.toStringDDMMYYYYFormat() == expiryDate?.toStringDDMMYYYYFormat()
                }
            if (indexManualAdjust != -1) {
                val index = submitData.expList.indexOf(submitData.expList[indexManualAdjust])
                val updatingExpiryDate = submitData.expList.removeAt(index)
                updatingExpiryDate.actualQty++
                submitData.expList.add(indexManualAdjust, updatingExpiryDate)
            } else {
                submitData.expList.add(
                    SdToteDocsDetailBarcodeExpiryUIModel(
                        actualQty = 1,
                        expDate = expiryDate,
                        lotNo = lotNo,
                        qty = 0,
                    ),
                )
            }
        }

        fun updateExistExpiryDate(
            submitData: SdToteSubmitUIModel,
            index: Int,
            add: Boolean,
        ) {
            val updatingExpiryDate = submitData.expList.removeAt(index)
            if (add) {
                updatingExpiryDate.actualQty++
            } else {
                updatingExpiryDate.actualQty--
            }
            submitData.expList.add(index, updatingExpiryDate)
        }

        fun addExpiryDateByLot(
            submitData: SdToteSubmitUIModel,
            expiryDate: Date,
            lotNo: String,
        ) {
            submitData.expList.add(
                SdToteDocsDetailBarcodeExpiryUIModel(
                    actualQty = 1,
                    expDate = expiryDate,
                    lotNo = lotNo,
                    qty = 0,
                ),
            )
        }

        fun removeSubmitExpiryDate(
            expiryDate: Date,
            isLotDummy: Boolean,
        ) {
            _sdToteSubmitUIModel.value?.let { submitData ->
                submitData.expList
                    .filter {
                        if (isLotDummy) {
                            it.expDate?.toStringDDMMYYYYFormat() == expiryDate.toStringDDMMYYYYFormat() && it.lotNo.isLotDummy()
                        } else {
                            it.expDate?.toStringDDMMYYYYFormat() == expiryDate.toStringDDMMYYYYFormat() && !it.lotNo.isLotDummy()
                        }
                    }.let {
                        submitData.expList.removeAll(it)
                        submitData.actualQty = submitData.expList.sumOf { lot -> lot.actualQty }
                    }
            }
        }

        fun updateSubmitActualQty(actualQty: Int) {
            _sdToteSubmitUIModel.value?.let { it.actualQty = actualQty }
        }

        fun updateIsAddLotState(isAddLot: Boolean) {
            _sdToteSubmitUIModel.value?.let { it.isAddLot = isAddLot }
        }

        fun getIncompleteItems(): List<SdToteDocsDetailBarcodeUIModel> {
            _sdToteDocsDetailUIModel.value?.let { docsDetail ->
                val insufficientItems =
                    docsDetail.items.filter { it.actualQty < it.qty }.sortedBy { it.barcode }
                val overItems =
                    docsDetail.items.filter { it.actualQty > it.qty }.sortedBy { it.barcode }
                val items = insufficientItems + overItems
                return items
            } ?: run {
                return emptyList()
            }
        }

        fun getExpiryDateActualQty(expiryDate: Date): Int =
            _sdToteSubmitUIModel.value
                ?.expList
                ?.firstOrNull {
                    it.expDate?.toStringDDMMYYYYFormat() == expiryDate.toStringDDMMYYYYFormat()
                }?.actualQty ?: 0

        private fun SnapshotStateList<ISdToteExpiryDateModel>.getIncrementLotData(): ISdToteExpiryDateModel {
            val filterItem = this.filter { it.qty != 0 }.ifEmpty { this }
            val updatedIndex =
                filterItem.indexOfFirst { item ->
                    item.actualQty != item.qty
                }
            return if (updatedIndex != -1) filterItem[updatedIndex] else filterItem.last()
        }

        private fun List<ISdToteExpiryDateModel>.sortByAscending(): SnapshotStateList<ISdToteExpiryDateModel> =
            this.sortedBy { it.lotNo }.toMutableStateList()

        private fun String.isLotDummy(): Boolean = this == OrderReceiveToteLotType.LOTDUMMY.name

        fun updateOrAddSubmitDummyLot(expiryDate: Date) {
            _sdToteSubmitUIModel.value?.let { submitData ->
                submitData.expList
                    .firstOrNull { exp ->
                        exp.expDate?.toStringDDMMYYYYFormat() == expiryDate.toStringDDMMYYYYFormat() &&
                            exp.lotNo == OrderReceiveToteLotType.LOTDUMMY.name
                    }?.let {
                        submitData.expList.remove(it)
                        it.actualQty += 1
                        submitData.expList.add(it)
                    } ?: run {
                    addExpiryDateByLot(
                        submitData = submitData,
                        expiryDate = expiryDate,
                        lotNo = OrderReceiveToteLotType.LOTDUMMY.name,
                    )
                }

                submitData.actualQty = submitData.expList.sumOf { it.actualQty }
            }
        }

        fun updateOrAddSubmitManualAdjustLot(expiryDate: Date) {
            _sdToteSubmitUIModel.value?.let { submitData ->
                submitData.expList
                    .firstOrNull { exp ->
                        exp.expDate?.toStringDDMMYYYYFormat() == expiryDate.toStringDDMMYYYYFormat() &&
                            exp.lotNo == OrderReceiveToteLotType.MANUAL_ADJUST.name
                    }?.let {
                        submitData.expList.remove(it)
                        it.actualQty += 1
                        submitData.expList.add(it)
                    } ?: run {
                    addExpiryDateByLot(
                        submitData = submitData,
                        expiryDate = expiryDate,
                        lotNo = OrderReceiveToteLotType.MANUAL_ADJUST.name,
                    )
                }

                submitData.actualQty = submitData.expList.sumOf { it.actualQty }
            }
        }
    }
