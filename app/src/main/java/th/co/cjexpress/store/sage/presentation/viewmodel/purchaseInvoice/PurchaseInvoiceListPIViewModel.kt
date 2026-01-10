package th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice

import androidx.compose.runtime.snapshots.SnapshotStateList
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
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetPurchaseInvoiceDetailUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetPurchaseInvoiceDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceItemPIUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DateUtils.getTimestampUnix
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class PurchaseInvoiceListPIViewModel
    @Inject
    constructor(
        private val getPurchaseInvoiceDocumentUseCase: GetPurchaseInvoiceDocumentUseCase,
        private val getPurchaseInvoiceDetailUseCase: GetPurchaseInvoiceDetailUseCase,
        private val purchaseInvoiceUseCase: PurchaseInvoiceUseCase,
        private val purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        sealed class UIState {
            data object NavToPOList : UIState()

            data object NavToPIDetail : UIState()
        }

        private val _piList = MutableStateFlow(emptyList<PurchaseInvoiceItemPIUIModel>())
        val piList: StateFlow<List<PurchaseInvoiceItemPIUIModel>> = _piList

        private val _showInvoiceInputDialogState = MutableStateFlow(false)
        val showInvoiceInputDialogState: StateFlow<Boolean> = _showInvoiceInputDialogState

        val supplierCode: String
            get() = purchaseInvoiceUseCase.getInvoiceSupplierCode()

        fun getPurchaseInvoiceDetail(piNo: String) {
            getPurchaseInvoiceDetailUseCase
                .execute(piNo = piNo)
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val data =
                                uiModelMapper.purchaseInvoicePIUIMapper
                                    .mapPurchaseInvoiceDetailToUIModel(
                                        purchaseInvoiceDetailModel = result.data,
                                    )

                            purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceDetail(data)

                            purchaseInvoiceDetailLiveDataUseCase.setUpdatedTimestamp(getTimestampUnix(result.data.timestamp))
                            setBaseScreenSuccessState(UIState.NavToPIDetail)
                            logEventSelectPI(data)
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
                                    onNegativeClick = ::resetBaseScreenState,
                                    positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        if (canRetry) getPurchaseInvoiceDetail(piNo)
                                    },
                                    ref = cause.ref,
                                ),
                            )
                        }
                    }
                }.catch { e ->
                    setBaseScreenErrorState(
                        TachyonAlertDialogUIModel(
                            type = ErrorType.ERROR,
                            showF1Message = true,
                            titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                            messageText =
                                e.message
                                    ?: resourceUtils.getString(R.string.text_service_failed),
                            negativeTextRes = R.string.text_close,
                            onNegativeClick = {
                                resetBaseScreenState()
                            },
                            positiveTextRes = R.string.text_retry,
                            onPositiveClick = {
                                resetBaseScreenState()
                                getPurchaseInvoiceDetail(piNo)
                            },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun loadPurchaseInvoice() {
            getPurchaseInvoiceDocumentUseCase
                .execute(supplierCode = purchaseInvoiceUseCase.getInvoiceSupplierCode())
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val data =
                                uiModelMapper.purchaseInvoicePIUIMapper
                                    .mapDocumentListToItemsPIUIModel(
                                        purchaseInvoiceDocumentListModel = result.data,
                                    )
                            _piList.value = data
                            setBaseScreenSuccessState(null)
                            logEventGetPIList(data)
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
                                            loadPurchaseInvoice()
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

        fun onStartReceiveClick() {
            val supplierData = purchaseInvoiceUseCase.getPurchaseInvoiceSupplier()
            logEventStartOrderReceive(supplierData)
            if (supplierData.isRefPo) {
                setBaseScreenSuccessState(UIState.NavToPOList)
            } else {
                setShowInvoiceInputDialogState(true)
                setPurchaseInvoiceDetail(
                    data =
                        PurchaseInvoiceDetailUIModel(
                            supplierCode = supplierData.supplierCode,
                            supplierName = supplierData.supplierCode,
                            billNo = "",
                            piNo = "",
                            poNo = "",
                            entries = SnapshotStateList(),
                            isEdit = true,
                        ),
                )
            }
        }

        fun onBackClick(onBackConfirm: () -> Unit) {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_confirm_exit),
                    messageText = resourceUtils.getString(R.string.text_confirm_leave_page),
                    negativeTextRes = R.string.text_no,
                    onNegativeClick = ::resetBaseScreenState,
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = onBackConfirm,
                ),
            )
        }

        fun onDismissClick() {
            setBaseScreenSuccessState(null)
        }

        fun setShowInvoiceInputDialogState(state: Boolean) {
            _showInvoiceInputDialogState.value = state
        }

        fun updatePurchaseInvoiceDetailBillNo(billNo: String) {
            purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceDetailBillNo(billNo)
            setBaseScreenSuccessState(UIState.NavToPIDetail)
        }

        private fun setPurchaseInvoiceDetail(data: PurchaseInvoiceDetailUIModel) {
            purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceDetail(data)
        }

        fun logEventGetPIList(data: List<PurchaseInvoiceItemPIUIModel>) {
            logUtils.logInfo(
                action = LogAction.GET_PI_LIST,
                message = data.toString(),
            )
        }

        fun logEventSelectPI(data: PurchaseInvoiceDetailUIModel) {
            logUtils.logInfo(
                action = LogAction.SELECT_PI,
                message = data.toString(),
            )
        }

        fun logEventStartOrderReceive(data: PurchaseInvoiceSupplierDetailUIModel) {
            logUtils.logInfo(
                action = LogAction.START_ORDER_RECEIVE,
                message = data.toString(),
            )
        }
    }
