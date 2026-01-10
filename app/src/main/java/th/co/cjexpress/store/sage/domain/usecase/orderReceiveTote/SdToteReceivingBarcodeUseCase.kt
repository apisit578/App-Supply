package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import th.co.cjexpress.store.sage.constants.OrderReceiveToteDiscountType
import th.co.cjexpress.store.sage.constants.OrderReceiveToteLotType.LOTDUMMY
import th.co.cjexpress.store.sage.domain.livedata.orderReceiveTote.OrderReceiveToteLiveData
import th.co.cjexpress.store.sage.domain.model.QRFreshDecodeDataModel
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteReceivingBarcodeModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeExpiryUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat
import th.co.cjexpress.store.sage.utils.ResourceUtils
import timber.log.Timber
import java.util.Date

class SdToteReceivingBarcodeUseCase(
    private val orderReceiveToteLiveData: OrderReceiveToteLiveData,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    val submitBarcodeFlowLD = orderReceiveToteLiveData.sdToteSubmitUIModel
    val docsDetailFLowLD = orderReceiveToteLiveData.sdToteDocsDetailUIModel
//    val barcodeDetailFlowLD = orderReceiveToteLiveData.sdToteDocsDetailBarcodeUIModel

    fun searchBarcode(barcode: String): SdToteDocsDetailBarcodeUIModel? = orderReceiveToteLiveData.searchBarcodeInTote(barcode)

    fun searchBarcodeByLotNo(
        barcode: String,
        lotNo: String,
        expiryDate: Date,
    ): SdToteDocsDetailBarcodeUIModel? {
        // ถ้าหาบาร์โค้ดไม่เจอ ให้ return null
        val barcodeDetail = orderReceiveToteLiveData.searchBarcodeInTote(barcode) ?: return null

        // หา lotNo และ expiryDate ใน expList
        val matchingLot =
            barcodeDetail.expList.firstOrNull {
                it.lotNo == lotNo &&
                    it.expDate?.toStringDDMMYYYYFormat() == expiryDate.toStringDDMMYYYYFormat()
            }
        Timber.tag("STATE").i("searchBarcodeByLot, matchingLot = $matchingLot")
        matchingLot?.let {
            // ถ้าเจอ lotNo และ expiryDate ให้ return ข้อมูลที่หาเจอ
            return barcodeDetail
        } ?: run {
            // ถ้าไม่เจอ lotNo และ expiryDate ให้เพิ่มข้อมูลใหม่เข้าไปใน expList
            barcodeDetail.expList.add(
                SdToteDocsDetailBarcodeExpiryUIModel(
                    actualQty = 1,
                    expDate = expiryDate,
                    lotNo = lotNo,
                    qty = 0,
                ),
            )
            return barcodeDetail
        }
    }

    fun createSubmitBarcode(
        isNewBarcode: Boolean,
        receivingBarcode: ISdToteReceivingBarcodeModel,
        actualQty: Int,
        expList: MutableList<ISdToteExpiryDateModel>,
        qrFreshDecodedData: QRFreshDecodeDataModel? = null,
    ): SdToteSubmitUIModel =
        SdToteSubmitUIModel(
            sdNo = orderReceiveToteLiveData.getSdToteDocNo(),
            toteCode = orderReceiveToteLiveData.getSdToteCode(),
            receivedDate = orderReceiveToteLiveData.getReceivedDate(),
            barcode = receivingBarcode.barcode,
            skuCode = receivingBarcode.skuCode,
            discountType = receivingBarcode.discountType,
            noOfExp = receivingBarcode.noOfExp,
            actualQty = actualQty,
            expList =
                expList
                    .sortedBy { it.expDate }
                    .map { it.clone() as ISdToteExpiryDateModel }
                    .toMutableStateList(),
        ).apply {
            this.isNewBarcode = isNewBarcode

            if (qrFreshDecodedData != null) {
                val matchingLot =
                    this.expList
                        .firstOrNull {
                            it.expDate?.toStringDDMMYYYYFormat() == qrFreshDecodedData.expDate.toStringDDMMYYYYFormat() &&
                                it.lotNo == qrFreshDecodedData.lotNo
                        }

                this@apply.expList =
                    if (matchingLot != null) {
                        mutableStateListOf(
                            matchingLot.apply {
                                this.actualQty += 1
                            },
                        )
                    } else {
                        mutableStateListOf(
                            SdToteDocsDetailBarcodeExpiryUIModel(
                                actualQty = 1,
                                expDate = qrFreshDecodedData.expDate,
                                lotNo = qrFreshDecodedData.lotNo,
                                qty = 0,
                            ),
                        )
                    }
            }
        }

    fun setToteSubmitUIModel(receivingBarcode: SdToteSubmitUIModel) {
        Timber
            .tag("STATE")
            .i("SdToteReceivingBarcodeUseCase setToteSubmitUIModel receivingBarcode = $receivingBarcode")
        orderReceiveToteLiveData.setToteSubmitUIModel(receivingBarcode)
    }

    fun addNewReceivingBarcode(newBarcode: SdToteDocsDetailBarcodeUIModel) {
        orderReceiveToteLiveData.addNewReceivingBarcode(newBarcode)
    }

    fun updateReceivingBarcode(
        barcode: String,
        actualQty: Int,
        expList: List<ISdToteExpiryDateModel>?,
    ) {
        orderReceiveToteLiveData.updateReceivingBarcode(
            barcode = barcode,
            actualQty = actualQty,
            expList =
                expList
                    ?.map { exp ->
                        SdToteDocsDetailBarcodeExpiryUIModel(
                            expDate = exp.expDate,
                            actualQty = exp.actualQty,
                            qty = exp.qty,
                            lotNo = exp.lotNo,
                        )
                    }?.toMutableStateList() ?: mutableStateListOf(),
        )
    }

//    fun setSdToteDocsDetailBarcodeUIModel(data: SdToteDocsDetailBarcodeUIModel) {
//        orderReceiveToteLiveData.setSdToteDocsDetailBarcodeUIModel(data)
//    }

//    fun resetSdToteDocsDetailBarcodeUIModel() {
//        orderReceiveToteLiveData.resetSdToteDocsDetailBarcodeUIModel()
//    }

    fun resetSdToteSubmitUIModel() {
        orderReceiveToteLiveData.resetSdToteSubmitUIModel()
    }

    fun resetSdToteDocsDetailUIModel() {
        orderReceiveToteLiveData.resetSdToteDocsDetailUIModel()
    }

//    fun updateOrAddExpiryDate(expiryDate: Date) {
//        orderReceiveToteLiveData.updateOrAddExpiryDate(expiryDate)
//    }

    fun updateOrAddSubmitExpiryDate(
        expiryDate: Date,
        isNoQR: Boolean,
    ) {
        orderReceiveToteLiveData.updateOrAddSubmitExpiryDate(expiryDate, isNoQR)
    }

    fun incrementFreshLifeExpiryDate(
        expiryDate: Date?,
        isLotDummy: Boolean,
    ) {
        orderReceiveToteLiveData.incrementFreshLifeExpiryDate(expiryDate, isLotDummy)
    }

    fun decreaseFreshLifeExpiryDate(
        expiryDate: Date?,
        isLotDummy: Boolean,
    ) {
        orderReceiveToteLiveData.decreaseFreshLifeExpiryDate(expiryDate, isLotDummy)
    }

    fun moveExpiryDateLotToFirst(expiryDate: Date) {
        orderReceiveToteLiveData.moveExpiryDateLotToFirst(expiryDate)
    }

//    fun sumActualQtyByExpiryDateLot() {
//        orderReceiveToteLiveData.sumActualQtyByExpiryDateLot()
//    }

    fun getSdToteDocNo(): String = orderReceiveToteLiveData.getSdToteDocNo()

    fun getSdToteCode(): String = orderReceiveToteLiveData.getSdToteCode()

//    fun setExpiryDateToSubmitLD(expiryDate: Date) {
//        orderReceiveToteLiveData.setExpiryDateToSubmitLD(expiryDate)
//    }

    fun removeSubmitExpiryDate(
        expiryDate: Date,
        isLotDummy: Boolean,
    ) {
        orderReceiveToteLiveData.removeSubmitExpiryDate(expiryDate, isLotDummy)
    }

    fun updateSubmitActualQty(actualQty: Int) {
        orderReceiveToteLiveData.updateSubmitActualQty(actualQty)
    }

    fun updateIsAddLotState(isAddLot: Boolean) {
        orderReceiveToteLiveData.updateIsAddLotState(isAddLot)
    }

    fun getExpiryDateActualQty(expiryDate: Date): Int = orderReceiveToteLiveData.getExpiryDateActualQty(expiryDate)

    fun getIncompleteItems(): List<SdToteDocsDetailBarcodeUIModel> = orderReceiveToteLiveData.getIncompleteItems()

    fun groupBarcodeByExpiryDate(input: List<SdToteDocsDetailBarcodeUIModel>): List<SdToteDocsDetailBarcodeUIModel> =
        input
            .flatMap { barcodeList ->
                when (OrderReceiveToteDiscountType.valueOf(barcodeList.discountType)) {
                    OrderReceiveToteDiscountType.NORMAL -> {
                        listOf(barcodeList)
                    }

                    OrderReceiveToteDiscountType.FRESHLIFE -> {
                        barcodeList.expList
                            .takeIf { it.isNotEmpty() }
                            ?.groupBy { it.expDate?.toStringDDMMYYYYFormat() }
                            ?.map { (_, groupedExpList) ->
                                val clonedList =
                                    groupedExpList.map { it.clone() as ISdToteExpiryDateModel }
                                barcodeList.copy(
                                    actualQty = clonedList.sumOf { it.actualQty },
                                    qty = clonedList.sumOf { it.qty },
                                    expList = clonedList.toMutableStateList(),
                                    qrCodePrint =
                                        clonedList
                                            .filter { it.lotNo == LOTDUMMY.name }
                                            .sumOf { it.actualQty },
                                )
                            } ?: listOf(barcodeList)
                    }
                }
            }.filter { it.qty != it.actualQty }

    fun updateOrAddSubmitDummyLot(expiryDate: Date) {
        orderReceiveToteLiveData.updateOrAddSubmitDummyLot(expiryDate)
    }

    fun updateOrAddSubmitManualAdjustLot(expiryDate: Date) {
        orderReceiveToteLiveData.updateOrAddSubmitManualAdjustLot(expiryDate)
    }
}
