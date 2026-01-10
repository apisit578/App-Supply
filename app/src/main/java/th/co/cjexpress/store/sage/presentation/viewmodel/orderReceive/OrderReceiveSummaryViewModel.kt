package th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.analytics.ProcessStatusType
import th.co.cjexpress.store.sage.analytics.ProcessTimeTrackingType
import th.co.cjexpress.store.sage.analytics.usecase.TrackTimeEventUseCase
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.livedata.orderReceive.OrderReceiveLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryItemModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.OrderReceiveSummaryUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryCompleteUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryUIModel
import th.co.cjexpress.store.sage.presentation.state.orderReceive.summary.OrderReceiveSummaryState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.summary.SummaryDisplayScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class OrderReceiveSummaryViewModel
    @Inject
    constructor(
        private val orderReceiveSummaryUseCase: OrderReceiveSummaryUseCase,
        private val trackTimeEventUseCase: TrackTimeEventUseCase,
        private val orderReceiveLiveData: OrderReceiveLiveData,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        sealed class UIState {
            data object NavBack : UIState()
        }

        // State
        private val _summaryState = MutableStateFlow(OrderReceiveSummaryState())
        val summaryState = _summaryState.asStateFlow()

        // Custom Property
        val sdNo: String
            get() {
                return orderReceiveLiveData.getSdNo()
            }

        fun loadSummary() {
            orderReceiveSummaryUseCase
                .execute(sdNo)
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach(::handleResult)
                .catch { e -> handleException(e) }
                .launchIn(viewModelScope)
        }

        //region event
        fun onButtonConfirmClick(sdNo: String) {
            setBaseScreenSuccessState(
                OrderReceiveSummaryCompleteUIModel(
                    sdNo = sdNo,
                ),
            )
        }

        fun onButtonDialogConfirmCancelClick() {
            logEventCompleteOrder(false)
            setBaseScreenSuccessState(data = summaryState.value)
        }

        fun onButtonDialogConfirmCompleteClick(
            sdNo: String,
            isComplete: Boolean,
        ) {
            logEventCompleteOrder(true)
            setBaseScreenSuccessState(
                uiModelMapper.orderReceiveSummaryUIMapper.mapToOrderReceiveSummaryCompleteUIModel(
                    sdNo = sdNo,
                    isComplete = isComplete,
                ),
            )
        }
        //endregion

        fun logEventGetSummary(data: OrderReceiveSummaryUIModel) {
            logUtils.logInfo(
                action = LogAction.GET_SUMMARY_DATA,
                message = data.toString(),
            )
        }

        fun logEventCompleteOrder(isComplete: Boolean) {
            logUtils.logInfo(
                action = if (isComplete) LogAction.CONFIRM_COMPLETED_SUMMARY_ORDER else LogAction.CANCEL_COMPLETED_SUMMARY_ORDER,
                message = if (isComplete) LogAction.CONFIRM_COMPLETED_SUMMARY_ORDER.name else LogAction.CANCEL_COMPLETED_SUMMARY_ORDER.name,
            )
        }

        private fun handleResult(result: Result<OrderReceiveSummaryModel>) {
            when (result) {
                is Result.Success -> handleSuccess(result)
                is Result.Error -> handleError(result)
            }
        }

        private fun handleSuccess(result: Result.Success<OrderReceiveSummaryModel>) {
            val uiModel =
                uiModelMapper.orderReceiveSummaryUIMapper
                    .mapToOrderReceiveSummaryUIModel(result.data)
                    .copy(displayScreenState = mapToDisplayState(result.data.data.items))

            _summaryState.update { it.copy(data = uiModel) }
            logEventGetSummary(uiModel)
            setBaseScreenSuccessState(null)
        }

        private fun handleError(result: Result.Error<*>) {
            setBaseScreenErrorState(
                buildErrorDialog(
                    result = result,
                    retryAction = { loadSummary() },
                    backAction = { setBaseScreenSuccessState(UIState.NavBack) },
                ),
            )
        }

        private fun handleException(e: Throwable) {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.ERROR,
                    titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                    messageText =
                        e.cause?.message
                            ?: resourceUtils.getString(R.string.text_service_failed),
                    negativeTextRes = R.string.text_close,
                    onNegativeClick = { resetBaseScreenState() },
                    positiveTextRes = R.string.text_retry,
                    onPositiveClick = {
                        resetBaseScreenState()
                        loadSummary()
                    },
                ),
            )
        }

        private fun mapToDisplayState(items: List<OrderReceiveSummaryItemModel>): SummaryDisplayScreenState =
            if (items.isNotEmpty()) {
                SummaryDisplayScreenState.SummaryListItemScreen
            } else {
                SummaryDisplayScreenState.SummaryEmptyScreen
            }

        private fun buildErrorDialog(
            result: Result.Error<*>,
            retryAction: () -> Unit,
            backAction: () -> Unit,
        ): TachyonAlertDialogUIModel {
            val (negativeRes, positiveRes) = getDialogButtonRes(result.canRetry)
            return TachyonAlertDialogUIModel(
                type = result.cause.type,
                showF1Message = result.cause.type == ErrorType.ERROR,
                titleText =
                    result.cause.title
                        ?: resourceUtils.getString(R.string.text_unable_to_continue),
                messageText =
                    result.cause.message
                        ?: resourceUtils.getString(R.string.text_service_failed),
                negativeTextRes = negativeRes,
                onNegativeClick = { resetBaseScreenState() },
                positiveTextRes = positiveRes,
                onPositiveClick = {
                    resetBaseScreenState()
                    if (result.canRetry) retryAction() else backAction()
                },
                ref = result.cause.ref,
            )
        }

        private fun getDialogButtonRes(canRetry: Boolean): Pair<Int?, Int> =
            if (canRetry) {
                R.string.text_close to R.string.text_retry
            } else {
                null to R.string.text_close
            }

        // Tracking event
        fun trackTimeTracking(
            type: ProcessTimeTrackingType,
            status: ProcessStatusType,
        ) {
            trackTimeEventUseCase.track(type = type, status = status)
        }

        fun stopTimeTracking(type: ProcessTimeTrackingType) {
            trackTimeEventUseCase.stop(type = type)
        }
    }
