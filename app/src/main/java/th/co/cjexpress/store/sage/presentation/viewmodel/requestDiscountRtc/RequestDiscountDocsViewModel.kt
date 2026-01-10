package th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc

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
import th.co.cjexpress.store.sage.constants.MasterMapping
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc.RequestDiscountLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.masterMapping.GetBdSearchTypeUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.GetBdDocumentNoDetailUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.GetBdDocumentUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentItemUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentNoDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdSearchTypeUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class RequestDiscountDocsViewModel
    @Inject
    constructor(
        private val requestDiscountLiveData: RequestDiscountLiveData,
        private val resourceUtils: ResourceUtils,
        private val getBdDocumentUseCase: GetBdDocumentUseCase,
        private val getBdDocumentNoDetailUseCase: GetBdDocumentNoDetailUseCase,
        private val getBdSearchTypeUseCase: GetBdSearchTypeUseCase,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel() {
        sealed class UIState {
            data class GetBdDocumentState(
                val data: BdDocumentUIModel? = null,
            ) : UIState()

            data class GetBdDocumentNoDetailBdState(
                val data: BdDocumentNoDetailUIModel? = null,
            ) : UIState()

            data class GetBdSearchTypeUIState(
                val data: List<BdSearchTypeUIModel>? = null,
            ) : UIState()
        }

        private val _getBdListState = MutableStateFlow(UIState.GetBdDocumentState())
        val getBdListState: StateFlow<UIState.GetBdDocumentState> = _getBdListState

        private val _getMasterMappingState = MutableStateFlow(UIState.GetBdSearchTypeUIState())
        val getMasterMappingState: MutableStateFlow<UIState.GetBdSearchTypeUIState> =
            _getMasterMappingState

        private val _getBdDetailState = MutableStateFlow(UIState.GetBdDocumentNoDetailBdState())
        val getBdDetailState: StateFlow<UIState.GetBdDocumentNoDetailBdState> = _getBdDetailState

        fun getBdList() {
            getBdDocumentUseCase
                .execute()
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            _getBdListState.value =
                                UIState.GetBdDocumentState(
                                    uiModelMapper.bdDocumentUIMapper.toUIModel(result.data),
                                )
                            resetBaseScreenState()
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
                                    onNegativeClick = { resetBaseScreenState() },
                                    positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        if (canRetry) {
                                            getBdList()
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

        fun getBdDetail(documentNumber: String) {
            getBdDocumentNoDetailUseCase
                .execute(documentNumber)
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            setBaseScreenSuccessState(
                                UIState.GetBdDocumentNoDetailBdState(
                                    uiModelMapper.bdDocumentNoDetailUIMapper.toUIModel(result.data),
                                ),
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
                                    onNegativeClick = { resetBaseScreenState() },
                                    positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        if (canRetry) {
                                            getBdDetail(documentNumber)
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
                            messageText = "Network error: ${e.cause?.message}",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun getCampaignDiscountType() {
            getBdSearchTypeUseCase
                .execute(MasterMapping.CAMPAIGN_DISCOUNT_TYPE)
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            _getMasterMappingState.value =
                                UIState.GetBdSearchTypeUIState(
                                    uiModelMapper.bdSearchTypeUIMapper.toUIModel(result.data.types),
                                )
                            resetBaseScreenState()
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
                                    onNegativeClick = { resetBaseScreenState() },
                                    positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        if (canRetry) {
                                            getCampaignDiscountType()
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
                            messageText = "Network error: ${e.cause?.message}",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun resetGetMasterMappingState() {
            _getMasterMappingState.value = UIState.GetBdSearchTypeUIState()
        }

        fun setDocsDataLD(docsData: BdDocumentNoDetailUIModel) {
            requestDiscountLiveData.setDocsData(docsData)
        }

        fun setDiscountTypeLD(discountType: RequestDiscountType) {
            requestDiscountLiveData.setDiscountType(discountType)
        }

        fun resetDocsDataLD() {
            requestDiscountLiveData.resetDocsData()
        }

        fun logEventOpenRequestDiscountDocsDialog() {
            logUtils.logInfo(
                action = LogAction.OPEN_CREATE_REQUEST_DISCOUNT_DOCS_DIALOG,
                message = resourceUtils.getString(R.string.text_create_doc),
            )
        }

        fun logEventSubmitDiscountType(type: String) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_DISCOUNT_TYPE,
                message = type,
            )
        }

        fun logEventSelectRequestDiscountDoc(document: BdDocumentItemUIModel) {
            logUtils.logInfo(
                action = LogAction.SELECT_REQUEST_DISCOUNT_DOC,
                message = document.toString(),
            )
        }
    }
