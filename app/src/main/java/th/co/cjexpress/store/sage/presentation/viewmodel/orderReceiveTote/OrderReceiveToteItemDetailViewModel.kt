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
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteLowestFactorUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteReceivingBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitAdjustUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitUseCase
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeExpiryUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteBarcodeEditingSubmitState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteLowestFactorState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class OrderReceiveToteItemDetailViewModel
    @Inject
    constructor(
        private val sdToteSubmitUseCase: SdToteSubmitUseCase,
        private val sdToteLowestFactorUseCase: SdToteLowestFactorUseCase,
        private val sdToteReceivingBarcodeUseCase: SdToteReceivingBarcodeUseCase,
        private val sdToteSubmitAdjustUseCase: SdToteSubmitAdjustUseCase,
        private val dispatcherProvider: DispatcherProvider,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        val submitBarcodeFlowLD =
            MutableStateFlow(sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.value!!)

        private val _inputActualQtyState = MutableStateFlow(submitBarcodeFlowLD.value.actualQty)
        val inputActualQtyState: StateFlow<Int> = _inputActualQtyState

        private val _canEditState = MutableStateFlow(false)
        val canEditState: StateFlow<Boolean> = _canEditState

        private val _removingExpiryDateState = MutableStateFlow<Pair<Date?, Boolean?>>(Pair(null, null))
        val removingExpiryDateState: StateFlow<Pair<Date?, Boolean?>> = _removingExpiryDateState

        fun submitBarcode(receivingBarcode: SdToteSubmitUIModel) {
            logEventSubmitEditItem(receivingBarcode)
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
                                SdToteBarcodeEditingSubmitState(receivingBarcode),
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

        fun setInputActualQtyState(actualQty: Int) {
            _inputActualQtyState.value = actualQty
        }

        fun updateSubmitActualQtyLD(actualQty: Int) {
            sdToteReceivingBarcodeUseCase.updateSubmitActualQty(actualQty)
        }

        fun setCanEditState(canEdit: Boolean) {
            _canEditState.value = canEdit
        }

        fun validateBarcode(barcode: String): Boolean {
            setBaseScreenLoadingState()
            return sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.value?.barcode == barcode
        }

        fun getLowestFactor(
            sdNo: String,
            itemBarcode: String,
            barcode: String,
        ) {
            sdToteLowestFactorUseCase
                .execute(
                    sdNo = sdNo,
                    itemBarcode = itemBarcode,
                    barcode = barcode,
                ).flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> setBaseScreenSuccessState(SdToteLowestFactorState(isSuccess = true))

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
                                            getLowestFactor(sdNo, itemBarcode, barcode)
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
                            onPositiveClick = {
                                resetBaseScreenState()
                            },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun getSdToteDocNoLD(): String = sdToteReceivingBarcodeUseCase.getSdToteDocNo()

        fun resetSubmitBarcodeLD() {
            sdToteReceivingBarcodeUseCase.resetSdToteSubmitUIModel()
        }

        fun incrementFreshLifeExpiryDate(
            expiryDate: Date?,
            isLotDummy: Boolean,
        ) {
            logEventAdjustItemQuantityFreshLife(
                submitBarcodeLDState = submitBarcodeFlowLD.value,
                expiryDate = expiryDate,
                isAdd = true,
            )
            sdToteReceivingBarcodeUseCase.incrementFreshLifeExpiryDate(expiryDate, isLotDummy)
        }

        fun decrementFreshLifeExpiryDate(
            expiryDate: Date?,
            isLotDummy: Boolean,
        ) {
            logEventAdjustItemQuantityFreshLife(
                submitBarcodeLDState = submitBarcodeFlowLD.value,
                expiryDate = expiryDate,
                isAdd = true,
            )
            sdToteReceivingBarcodeUseCase.decreaseFreshLifeExpiryDate(expiryDate, isLotDummy)
        }

        fun removeSubmitExpiryDateLD(
            expiryDate: Date,
            isLotDummy: Boolean,
        ) {
            sdToteReceivingBarcodeUseCase.removeSubmitExpiryDate(expiryDate, isLotDummy)
        }

        fun updateIsAddLotStateLD(isAddLot: Boolean) {
            sdToteReceivingBarcodeUseCase.updateIsAddLotState(isAddLot)
        }

        fun setRemovingExpiryDateState(
            expiryDate: Date?,
            isLotDummy: Boolean?,
        ) {
            _removingExpiryDateState.value = Pair(expiryDate, isLotDummy)
        }

        fun sdToteSubmitAdjust(receivingBarcode: SdToteSubmitUIModel) {
            sdToteSubmitAdjustUseCase
                .execute(receivingBarcode)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            updateReceivingBarcodeLD(receivingBarcode)
                            setBaseScreenSuccessState(
                                SdToteBarcodeEditingSubmitState(receivingBarcode),
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
                                            sdToteSubmitAdjust(receivingBarcode)
                                        } else {
                                            if (result.cause.errorCode == ErrorCodes.INVALID_BODY) {
                                                setBaseScreenSuccessState(
                                                    SdToteBarcodeEditingSubmitState(null),
                                                )
                                            }
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

        fun logEventSubmitBarcode(barcode: String) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_BARCODE_EDIT_STATE,
                message = barcode,
            )
        }

        fun logEventAddNewExpiryDate(submitBarcodeLDState: SdToteSubmitUIModel) {
            logUtils.logInfo(
                action = LogAction.ADD_NEW_EXPIRED_DATE,
                message = submitBarcodeLDState.toString(),
            )
        }

        fun logEventAdjustItemQuantityFreshLife(
            submitBarcodeLDState: SdToteSubmitUIModel,
            expiryDate: Date?,
            isAdd: Boolean,
        ) {
            logUtils.logInfo(
                action = if (isAdd) LogAction.INCREASE_ITEM_QUANTITY else LogAction.DECREASE_ITEM_QUANTITY,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_adjust_quantity_fresh_life),
                        submitBarcodeLDState.productName,
                        submitBarcodeLDState.barcode,
                        expiryDate,
                    ),
            )
        }

        fun logEventAdjustItemQuantity(submitBarcodeLDState: SdToteSubmitUIModel) {
            logUtils.logInfo(
                action = LogAction.ADJUST_ITEM_QUANTITY,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_adjust_quantity_normal),
                        submitBarcodeLDState.productName,
                        submitBarcodeLDState.barcode,
                    ),
            )
        }

        fun logEventSubmitEditItem(submitBarcodeLDState: SdToteSubmitUIModel) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_EDIT_ITEM_DETAIL,
                message = submitBarcodeLDState.toString(),
            )
        }
    }
