package th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote

import android.content.Context
import android.view.inputmethod.InputMethodManager
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
import th.co.cjexpress.store.sage.domain.livedata.orderReceiveTote.OrderReceiveToteLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDataModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.GetSdToteListUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.OpenToteUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SearchToteByDocNoUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class OrderReceiveToteDocsViewModel
    @Inject
    constructor(
        private val getSdToteListUseCase: GetSdToteListUseCase,
        private val openToteUseCase: OpenToteUseCase,
        private val searchToteByDocNoUseCase: SearchToteByDocNoUseCase,
        private val orderReceiveToteLiveData: OrderReceiveToteLiveData,
        private val uiModelMapper: UiModelMapper,
        private val resourceUtils: ResourceUtils,
        private val dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel() {
        private val _sdToteDocsList = MutableStateFlow<List<SdToteDataModel>>(emptyList())
        val sdToteDocsList: StateFlow<List<SdToteDataModel>> = _sdToteDocsList

        private val _showScanNewToteDialogState = MutableStateFlow(false)
        val showScanNewToteDialogState: StateFlow<Boolean> = _showScanNewToteDialogState

        fun getSdToteDocsList() {
            getSdToteListUseCase
                .execute()
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            _sdToteDocsList.value = result.data.data
                            setBaseScreenSuccessState(null)
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
                                            getSdToteDocsList()
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

        fun openTote(toteCode: String) {
            logEventSubmitNewToteCode(toteCode)
            openToteUseCase
                .execute(toteCode)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success ->
                            setBaseScreenSuccessState(
                                data = uiModelMapper.sdToteDocDetailUIMapper.toUIModel(result.data.data),
                            )

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
                                            openTote(toteCode)
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
                                setShowScanNewToteDialogState(true)
                            },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun searchByDocNo(docNo: String) {
            logEventSelectToteDoc(docNo)
            searchToteByDocNoUseCase
                .execute(docNo)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success ->
                            setBaseScreenSuccessState(
                                data = uiModelMapper.sdToteDocDetailUIMapper.toUIModel(result.data.data),
                            )

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
                                            searchByDocNo(docNo)
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

        fun setShowScanNewToteDialogState(isShow: Boolean) {
            _showScanNewToteDialogState.value = isShow
            logEventScanNewToteDialog(isShow)
        }

        fun setSdToteDetailDataLD(data: SdToteDocsDetailUIModel) {
            orderReceiveToteLiveData.setSdToteDocsDetailUIModel(data)
        }

        fun resetSdToteLD() {
            orderReceiveToteLiveData.resetSdToteDocsDetailUIModel()
            orderReceiveToteLiveData.resetSdToteSubmitUIModel()
        }

        fun hideKeyboard(context: Context) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        fun logEventScanNewToteDialog(state: Boolean) {
            logUtils.logInfo(
                action = if (state) LogAction.OPEN_SCAN_NEW_TOTE_DIALOG else LogAction.CANCEL_SCAN_NEW_TOTE,
                message =
                    if (state) {
                        resourceUtils.getString(R.string.log_message_open_scan_tote_dialog)
                    } else {
                        resourceUtils.getString(
                            R.string.log_message_close_scan_tote_dialog,
                        )
                    },
            )
        }

        fun logEventSubmitNewToteCode(toteCode: String) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_OPEN_TOTE,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_tote_code),
                        toteCode,
                    ),
            )
        }

        fun logEventSelectToteDoc(docNo: String) {
            logUtils.logInfo(
                action = LogAction.SELECT_TOTE_DOC,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_doc_no),
                        docNo,
                    ),
            )
        }
    }
