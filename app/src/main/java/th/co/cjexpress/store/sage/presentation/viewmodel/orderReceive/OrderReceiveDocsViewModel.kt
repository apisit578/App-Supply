package th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.analytics.usecase.TrackPressStartReceiveEventUseCase
import th.co.cjexpress.store.sage.analytics.usecase.TrackPressStartReceiveEventUseCaseImpl
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.livedata.orderReceive.OrderReceiveLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.GetSdDocListUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.GetShipmentDocumentBySdNoUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentOpenUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel
import th.co.cjexpress.store.sage.presentation.state.auth.AuthState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentListState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class OrderReceiveDocsViewModel
    @Inject
    constructor(
        private val getSdDocListUseCase: GetSdDocListUseCase,
        private val getShipmentDocumentBySDNoUseCase: GetShipmentDocumentBySdNoUseCase,
        private val shipmentDocumentOpenUseCase: ShipmentDocumentOpenUseCase,
        private val trackPressStartReceiveEventUseCase: TrackPressStartReceiveEventUseCase,
        private val orderReceiveLiveData: OrderReceiveLiveData,
        private val uiModelMapper: UiModelMapper,
        private val resourceUtils: ResourceUtils,
        private val dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel() {
        private val _shipmentDocumentListState = MutableStateFlow(ShipmentDocumentListState())
        val shipmentDocumentListState: StateFlow<ShipmentDocumentListState> = _shipmentDocumentListState

        private val _showScanNewDocDialogState = MutableStateFlow(false)
        val showScanNewDocDialogState: StateFlow<Boolean> = _showScanNewDocDialogState

        private val _inputShipmentDocumentState =
            MutableStateFlow(TextFieldValue(text = "", selection = TextRange(0)))
        val inputShipmentDocumentState: StateFlow<TextFieldValue> = _inputShipmentDocumentState

        fun getSdDocsList() {
            getSdDocListUseCase
                .execute()
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            _shipmentDocumentListState.value =
                                ShipmentDocumentListState(
                                    uiModelMapper.shipmentDocumentListUIMapper.toUIModel(result.data),
                                )
                            resetBaseScreenState()
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
                                            getSdDocsList()
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
                            showF1Message = true,
                            messageText = e.cause?.message ?: "",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun shipmentDocumentBySDNo(sdNo: String) {
            logEventSelectDoc(sdNo)
            getShipmentDocumentBySDNoUseCase
                .execute(sdNo)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            setBaseScreenSuccessState(
                                data = uiModelMapper.shipmentDocumentUIMapper.toUIModel(data = result.data.data),
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
                                            shipmentDocumentBySDNo(sdNo)
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
                            showF1Message = true,
                            messageText = e.cause?.message ?: "",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun shipmentDocumentOpen(ldNo: String) {
            logEventSubmitNewDoc(ldNo)
            shipmentDocumentOpenUseCase
                .execute(ldNo)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            clearInputShipmentDocumentState()
                            setBaseScreenSuccessState(
                                data = uiModelMapper.shipmentDocumentUIMapper.toUIModel(data = result.data.data),
                            )
                        }

                        is Result.Error -> {
                            val cause = result.cause
                            val canRetry = result.canRetry
                            setBaseScreenErrorState(
                                TachyonAlertDialogUIModel(
                                    type = cause.type,
                                    showF1Message = (cause.type == ErrorType.ERROR),
                                    titleText =
                                        cause.title
                                            ?: resourceUtils.getString(R.string.text_unable_to_continue),
                                    messageText =
                                        cause.message
                                            ?: resourceUtils.getString(R.string.text_service_failed),
                                    negativeTextRes = if (canRetry) R.string.text_close else null,
                                    onNegativeClick = {
                                        setShowScanNewDocDialogState(true)
                                        resetBaseScreenState()
                                    },
                                    positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        handleShipmentDocumentOpenError(
                                            canRetry = canRetry,
                                            errorCode = cause.errorCode,
                                            ldNo = ldNo,
                                        )
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
                            showF1Message = true,
                            messageText = e.cause?.message ?: "",
                            onPositiveClick = {
                                resetBaseScreenState()
                                setShowScanNewDocDialogState(true)
                            },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun handleShipmentDocumentOpenError(
            canRetry: Boolean,
            errorCode: String?,
            ldNo: String,
        ) {
            when {
                canRetry -> {
                    shipmentDocumentOpen(ldNo)
                }

                errorCode == ErrorCodes.SESSION_TIMEOUT -> {
                    viewModelScope.launch {
                        AuthState.unauthorizedState
                            .filter { it == false }
                            .first()
                        setShowScanNewDocDialogState(true)
                    }
                }

                else -> {
                    setShowScanNewDocDialogState(true)
                }
            }
        }

        fun setShowScanNewDocDialogState(isShow: Boolean) {
            logEventScanNewDocDialog(isShow)
            _showScanNewDocDialogState.value = isShow
        }

        fun setupShipmentSummaryLiveData(data: ShipmentDocumentUIModel) {
            orderReceiveLiveData.setShipmentSummaryUIModel(data)
        }

        fun resetShipmentSummaryLiveData() {
            orderReceiveLiveData.resetShipmentSummaryUIModel()
        }

        fun setInputShipmentDocumentState(value: TextFieldValue) {
            _inputShipmentDocumentState.value = value
        }

        fun clearInputShipmentDocumentState() {
            _inputShipmentDocumentState.value = TextFieldValue(text = "", selection = TextRange(0))
        }

        fun hideKeyboard(context: Context) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        fun logEventScanNewDocDialog(state: Boolean) {
            logUtils.logInfo(
                action = if (state) LogAction.OPEN_SCAN_NEW_DOC_DIALOG else LogAction.CANCEL_SCAN_NEW_DOC,
                message =
                    if (state) {
                        resourceUtils.getString(R.string.log_message_open_scan_ld_dialog)
                    } else {
                        resourceUtils.getString(
                            R.string.log_message_close_scan_ld_dialog,
                        )
                    },
            )
        }

        fun logEventSubmitNewDoc(docNo: String) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_SCAN_NEW_DOC,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_ld_bt_no),
                        docNo,
                    ),
            )
        }

        fun logEventSelectDoc(docNo: String) {
            logUtils.logInfo(
                action = LogAction.SELECT_SD_DOC,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_sd_no),
                        docNo,
                    ),
            )
        }

        fun trackPressStartReceiveEvent() {
            trackPressStartReceiveEventUseCase.execute()
        }
    }
