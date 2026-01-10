package th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.OrderReceiveToteLotType
import th.co.cjexpress.store.sage.domain.model.QRFreshDecodeDataModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteReceivingBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitAndGetLatestBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteReceivingBarcodeModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeExpiryUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteInputExpiryDateSubmitState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.LogUtils
import th.co.cjexpress.store.sage.utils.ResourceUtils
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class OrderReceiveToteInputExpireViewModel
    @Inject
    constructor(
        private val sdToteSubmitUseCase: SdToteSubmitUseCase,
        private val sdToteSubmitAndGetLatestBarcodeUseCase: SdToteSubmitAndGetLatestBarcodeUseCase,
        private val sdToteReceivingBarcodeUseCase: SdToteReceivingBarcodeUseCase,
        private val uiModelMapper: UiModelMapper,
        private val resourceUtils: ResourceUtils,
        private val dispatcherProvider: DispatcherProvider,
        private val logsUtils: LogUtils,
    ) : BaseViewModel() {
        //    val submitBarcodeFlowLD = sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.asFlow()
        val submitBarcodeFlowLD =
            MutableStateFlow(sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.value!!)

        private val _inputExpiryDateState =
            MutableStateFlow(
                submitBarcodeFlowLD.value.getExpiryDate(),
//        sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.value?.getExpiryDate() ?: Date()
            )
        val inputExpiryDateState: StateFlow<Date> = _inputExpiryDateState

        fun submitBarcode(receivingBarcode: SdToteSubmitUIModel) {
            sdToteSubmitUseCase
                .execute(
                    sdNo = receivingBarcode.sdNo,
                    toteCode = receivingBarcode.toteCode,
                    barcode = receivingBarcode.barcode,
                    skuCode = receivingBarcode.skuCode,
                    discountType = receivingBarcode.discountType,
                    actualQty = receivingBarcode.actualQty,
                    expList = receivingBarcode.expList.toList(),
                ).flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            updateReceivingBarcodeLD(receivingBarcode)
                            setBaseScreenSuccessState(
                                SdToteInputExpiryDateSubmitState(receivingBarcode),
                            )
                        }

                        is Result.Error -> {
                            setBaseScreenErrorState(
                                TachyonAlertDialogUIModel(
                                    type = result.cause.type,
                                    showF1Message = (result.cause.type == ErrorType.ERROR),
                                    titleText =
                                        result.cause.title
                                            ?: resourceUtils.getString(R.string.text_unable_to_continue),
                                    messageText =
                                        result.cause.message
                                            ?: resourceUtils.getString(R.string.text_service_failed),
                                    negativeTextRes = if (result.canRetry) R.string.text_close else null,
                                    onNegativeClick = { resetBaseScreenState() },
                                    positiveTextRes = if (result.canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        if (result.canRetry) {
                                            submitBarcode(receivingBarcode)
                                        }
                                    },
                                    ref = result.cause.ref,
                                ),
                            )
                        }
                    }
                }.catch { e ->
                    setBaseScreenErrorState(
                        TachyonAlertDialogUIModel(
                            type = ErrorType.ERROR,
                            titleText =
                                resourceUtils.getString(
                                    R.string.text_unable_to_continue,
                                ),
                            messageText = e.cause?.message ?: "",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun submitAndGetLatestBarcode(receivingBarcode: SdToteSubmitUIModel) {
            sdToteSubmitAndGetLatestBarcodeUseCase
                .execute(
                    sdNo = receivingBarcode.sdNo,
                    toteCode = receivingBarcode.toteCode,
                    barcode = receivingBarcode.barcode,
                    skuCode = receivingBarcode.skuCode,
                    discountType = receivingBarcode.discountType,
                    actualQty = receivingBarcode.actualQty,
                    expList = receivingBarcode.expList.toList(),
                ).flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val newBarcodeData =
                                uiModelMapper.sdToteDocDetailUIMapper.toUIModel(result.data.latestBarcode)
                            addNewReceivingBarcodeLD(newBarcodeData)
                            setBaseScreenSuccessState(
                                SdToteInputExpiryDateSubmitState(receivingBarcode),
                            )
                        }

                        is Result.Error ->
                            setBaseScreenErrorState(
                                TachyonAlertDialogUIModel(
                                    type = result.cause.type,
                                    showF1Message = (result.cause.type == ErrorType.ERROR),
                                    titleText =
                                        result.cause.title
                                            ?: resourceUtils.getString(R.string.text_unable_to_continue),
                                    messageText =
                                        result.cause.message
                                            ?: resourceUtils.getString(R.string.text_service_failed),
                                    negativeTextRes = if (result.canRetry) R.string.text_close else null,
                                    onNegativeClick = { resetBaseScreenState() },
                                    positiveTextRes = if (result.canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        if (result.canRetry) {
                                            submitAndGetLatestBarcode(receivingBarcode)
                                        }
                                    },
                                    ref = result.cause.ref,
                                ),
                            )
                    }
                }.catch { e ->
                    setBaseScreenErrorState(
                        TachyonAlertDialogUIModel(
                            type = ErrorType.ERROR,
                            titleText =
                                resourceUtils.getString(
                                    R.string.text_unable_to_continue,
                                ),
                            messageText = e.cause?.message ?: "",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun setInputExpiryDateState(date: Date) {
            logEventAdjustExpiryDate(date.toString())
            _inputExpiryDateState.value = date
        }

        fun resetSubmitBarcodeLD() {
            sdToteReceivingBarcodeUseCase.resetSdToteSubmitUIModel()
        }

        fun updateOrAddSubmitExpiryDateLD(
            expiryDate: Date,
            isNoQR: Boolean,
        ) {
            logEventConfirmAdjustExpiryDate(expiryDate.toString(), isNoQR)
            sdToteReceivingBarcodeUseCase.updateOrAddSubmitExpiryDate(expiryDate, isNoQR)
        }

        fun moveExpiryDateLotToFirstLD(expiryDate: Date) {
            sdToteReceivingBarcodeUseCase.moveExpiryDateLotToFirst(expiryDate)
        }

        private fun addNewReceivingBarcodeLD(newBarcode: SdToteDocsDetailBarcodeUIModel) {
            sdToteReceivingBarcodeUseCase.addNewReceivingBarcode(newBarcode)
        }

        private fun updateReceivingBarcodeLD(receivingBarcode: SdToteSubmitUIModel) {
            sdToteReceivingBarcodeUseCase.updateReceivingBarcode(
                barcode = receivingBarcode.barcode,
                actualQty = receivingBarcode.actualQty,
                expList =
                    receivingBarcode.expList.map {
                        SdToteDocsDetailBarcodeExpiryUIModel(
                            expDate = it.expDate,
                            actualQty = it.actualQty,
                            qty = it.qty,
                            lotNo = it.lotNo,
                        )
                    },
            )
        }

        fun createSubmitBarcodeLD(
            isNewBarcode: Boolean,
            barcodeData: ISdToteReceivingBarcodeModel,
            actualQty: Int,
            expList: MutableList<ISdToteExpiryDateModel>,
            qrFreshDecodedData: QRFreshDecodeDataModel? = null,
        ): SdToteSubmitUIModel {
            Timber.tag("STATE").i("viewmodel createSubmitBarcodeLD")
            return sdToteReceivingBarcodeUseCase.createSubmitBarcode(
                isNewBarcode = isNewBarcode,
                receivingBarcode = barcodeData,
                actualQty = actualQty,
                expList = expList,
                qrFreshDecodedData = qrFreshDecodedData,
            )
        }

        fun getExpiryDateActualQtyLD(expiryDate: Date): Int = sdToteReceivingBarcodeUseCase.getExpiryDateActualQty(expiryDate)

        fun addNewLotOrch(
            isNoQR: Boolean,
            expiryDate: Date,
        ) {
            if (isNoQR) {
                sdToteReceivingBarcodeUseCase.updateOrAddSubmitDummyLot(expiryDate)
            } else {
                sdToteReceivingBarcodeUseCase.updateOrAddSubmitManualAdjustLot(expiryDate)
            }
            sdToteReceivingBarcodeUseCase.moveExpiryDateLotToFirst(expiryDate)

//            updateOrAddSubmitExpiryDateLD(
//                expiryDate = _inputExpiryDateState.value,
//                isNoQR = isNoQR,
//            )
//            moveExpiryDateLotToFirstLD(_inputExpiryDateState.value)
        }

        fun submitDummyLotExpiryDateOrch() =
            submitBarcodeFlowLD.value.let {
                sdToteReceivingBarcodeUseCase
                    .createSubmitBarcode(
                        isNewBarcode = true,
                        receivingBarcode = it,
                        actualQty = it.actualQty,
                        expList = it.expList,
                        qrFreshDecodedData =
                            QRFreshDecodeDataModel(
                                lotNo = OrderReceiveToteLotType.LOTDUMMY.name,
                                barcode = it.barcode,
                                expDate = _inputExpiryDateState.value,
                            ),
                    ).let { submitData ->
                        if (submitData.isNewBarcode) {
                            submitAndGetLatestBarcode(submitData)
                        } else {
                            submitBarcode(submitData)
                        }
                    }
            }

        fun logEventDefaultExpiryDate(
            barcode: String,
            expiryDate: String,
        ) {
            logsUtils.logInfo(
                action = LogAction.DEFAULT_EXPIRED_DATE,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_default_expired_date),
                        barcode,
                        expiryDate,
                    ),
            )
        }

        fun logEventAdjustExpiryDate(expiryDate: String) {
            logsUtils.logInfo(
                action = LogAction.ADJUST_EXPIRED_DATE,
                message = expiryDate,
            )
        }

        fun logEventConfirmAdjustExpiryDate(
            expiryDate: String,
            qrPrint: Boolean,
        ) {
            logsUtils.logInfo(
                action = LogAction.CONFIRM_ADJUST_EXPIRED_DATE,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_confirm_expired_date),
                        submitBarcodeFlowLD.value.barcode,
                        expiryDate,
                        if (qrPrint) {
                            resourceUtils.getString(R.string.text_checkbox_qr_expire)
                        } else {
                            resourceUtils.getString(
                                R.string.log_message_unchecked_qr_print,
                            )
                        },
                    ),
            )
        }
    }
