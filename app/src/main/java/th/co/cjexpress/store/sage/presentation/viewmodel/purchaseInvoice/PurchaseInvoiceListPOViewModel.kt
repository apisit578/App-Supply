package th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice

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
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentListModel
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.CheckPurchaseOrderExistsUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.CreatePurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetPurchaseOrderDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierPOUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DateUtils.getTimestampUnix
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class PurchaseInvoiceListPOViewModel
    @Inject
    constructor(
        private val getPurchaseOrderDocumentUseCase: GetPurchaseOrderDocumentUseCase,
        private val checkPurchaseOrderExistsUseCase: CheckPurchaseOrderExistsUseCase,
        private val createPurchaseInvoiceUseCase: CreatePurchaseInvoiceUseCase,
        private val purchaseInvoiceUseCase: PurchaseInvoiceUseCase,
        private val purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase,
        private val dispatcherProvider: DispatcherProvider,
        private val uiModelMapper: UiModelMapper,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        sealed class UIState {
            data object NavToSupplierList : UIState()

            data object NavToPIList : UIState()

            data object NavToPIDetail : UIState()
        }

        private val _poList =
            MutableStateFlow(emptyList<PurchaseInvoiceSupplierPOUIModel>())
        val poList: StateFlow<List<PurchaseInvoiceSupplierPOUIModel>> = _poList

        val supplierLD: PurchaseInvoiceSupplierDetailUIModel
            get() {
                return purchaseInvoiceUseCase.getPurchaseInvoiceSupplier()
            }

        private val _selectedPoNo = MutableStateFlow("")
        val selectedPoNo: StateFlow<String> = _selectedPoNo

        private val _showInvoiceInputDialogState = MutableStateFlow(false)
        val showInvoiceInputDialogState: StateFlow<Boolean> = _showInvoiceInputDialogState

        private val _showConfirmBackDialogState = MutableStateFlow(false)
        val showConfirmBackDialogState: StateFlow<Boolean> = _showConfirmBackDialogState

        private val _keepBillNo = MutableStateFlow<String>("")
        val keepBillNo: StateFlow<String> = _keepBillNo

        fun clearSelectedPo() {
            _selectedPoNo.value = ""
        }

        fun loadPurchaseOrderDocument() {
            getPurchaseOrderDocumentUseCase
                .execute(supplierCode = supplierLD.supplierCode)
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> handleLoadPurchaseOrderDocumentSuccess(result)
                        is Result.Error -> handleLoadPurchaseOrderDocumentError(result)
                    }
                }.catch { e -> handleLoadPurchaseOrderDocumentThrowable(e) }
                .launchIn(viewModelScope)
        }

        fun handleLoadPurchaseOrderDocumentSuccess(result: Result.Success<PurchaseOrderDocumentListModel>) {
            _poList.value =
                uiModelMapper.purchaseInvoicePOUIMapper.mapPOUIModel(
                    purchaseOrderDocumentItemModel = result.data.data,
                )
            setBaseScreenSuccessState(null)
        }

        fun handleLoadPurchaseOrderDocumentError(result: Result.Error<PurchaseOrderDocumentListModel>) {
            if (result.cause.errorCode == ErrorCodes.SESSION_TIMEOUT) {
                resetBaseScreenState()
                navigateToPIList()
                return
            }

            setBaseScreenErrorState(
                buildTachyonAlertDialog(
                    canRetry = result.canRetry,
                    type = result.cause.type,
                    title =
                        result.cause.title
                            ?: resourceUtils.getString(R.string.text_unable_to_continue),
                    message =
                        result.cause.message
                            ?: resourceUtils.getString(R.string.text_service_failed),
                    ref = result.cause.ref,
                    onRetry = { loadPurchaseOrderDocument() },
                    onClose = {
                        resetBaseScreenState()
                        navigateToPIList()
                    },
                ),
            )
        }

        fun handleLoadPurchaseOrderDocumentThrowable(e: Throwable) {
            setBaseScreenErrorState(
                buildTachyonAlertDialog(
                    canRetry = true,
                    type = ErrorType.ERROR,
                    title = resourceUtils.getString(R.string.text_unable_to_continue),
                    message = e.cause?.message ?: resourceUtils.getString(R.string.text_service_failed),
                    ref = null,
                    onRetry = { loadPurchaseOrderDocument() },
                    onClose = {
                        resetBaseScreenState()
                        navigateToPIList()
                    },
                ),
            )
        }

        fun buildTachyonAlertDialog(
            canRetry: Boolean,
            type: ErrorType,
            title: String,
            message: String,
            ref: String?,
            onRetry: () -> Unit,
            onClose: () -> Unit,
        ): TachyonAlertDialogUIModel =
            TachyonAlertDialogUIModel(
                type = type,
                showF1Message = (type == ErrorType.ERROR),
                titleText = title,
                messageText = message,
                negativeTextRes = if (canRetry) R.string.text_close else null,
                onNegativeClick = onClose,
                positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                onPositiveClick = {
                    onClose()
                    if (canRetry) onRetry()
                },
                ref = ref,
            )

        fun onSearchPo(poNo: String) {
            if (poNo.isEmpty()) {
                setBaseScreenErrorState(
                    TachyonAlertDialogUIModel(
                        type = ErrorType.WARNING,
                        titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                        messageText = resourceUtils.getString(R.string.text_pleas_fill_up_po_document),
                        positiveTextRes = R.string.text_close,
                        onPositiveClick = {
                            resetBaseScreenState()
                        },
                    ),
                )
                return
            }
            checkPurchaseOrderExistsUseCase
                .execute(
                    poNo = poNo,
                    supplierCode = supplierLD.supplierCode,
                ).flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            _selectedPoNo.value = poNo
                            resetBaseScreenState()
                            setShowInvoiceInputDialogState(true)
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
                                        if (canRetry) onSearchPo(poNo)
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
                                onSearchPo(poNo)
                            },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun navigateToPIList() = setBaseScreenSuccessState(UIState.NavToPIList)

        fun setShowInvoiceInputDialogState(state: Boolean) {
            _showInvoiceInputDialogState.value = state
        }

        fun createPurchaseInvoice(
            poNo: String,
            billNo: String,
            supplierCode: String,
        ) {
            createPurchaseInvoiceUseCase
                .execute(poNo = poNo, billNo = billNo, supplierCode = supplierCode)
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            uiModelMapper.purchaseInvoicePIUIMapper
                                .mapPurchaseInvoiceCreateModelToUIModel(result.data)
                                .let { dataDetail -> 
                                    purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceDetail(data = dataDetail.copy(isEdit = false))
                                }
                            purchaseInvoiceDetailLiveDataUseCase.setUpdatedTimestamp(
                                result.data.entries.maxOfOrNull { it.hhTimestamp } ?: getTimestampUnix(
                                    result.data.timestamp,
                                ),
                            )
                            setBaseScreenSuccessState(UIState.NavToPIDetail)
                        }

                        is Result.Error -> {
                            setBillNo(billNo)
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
                                        resetBaseScreenState()
                                        setShowInvoiceInputDialogState(true)
                                    },
                                    positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        if (canRetry) {
                                            createPurchaseInvoice(
                                                poNo = poNo,
                                                billNo = billNo,
                                                supplierCode = supplierCode,
                                            )
                                        }
                                    },
                                    ref = cause.ref,
                                ),
                            )
                        }
                    }
                }.catch { e ->
                    setBaseScreenErrorState(
                        errorData =
                            TachyonAlertDialogUIModel(
                                type = ErrorType.ERROR,
                                showF1Message = true,
                                titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                                messageText =
                                    e.message
                                        ?: resourceUtils.getString(
                                            R.string.text_service_failed,
                                        ),
                                negativeTextRes = R.string.text_close,
                                onNegativeClick = {
                                    resetBaseScreenState()
                                },
                                positiveTextRes = R.string.text_retry,
                                onPositiveClick = {
                                    createPurchaseInvoice(
                                        poNo = poNo,
                                        billNo = billNo,
                                        supplierCode = supplierCode,
                                    )
                                },
                            ),
                    )
                }.launchIn(viewModelScope)
        }

        fun setShowDialogConfirmBackState(state: Boolean) {
            _showConfirmBackDialogState.value = state
        }

        fun hideKeyboard(context: Context) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        fun logEventGetPOList(data: List<PurchaseInvoiceSupplierPOUIModel>) {
            logUtils.logInfo(
                action = LogAction.GET_PO_LIST,
                message = data.toString(),
            )
        }

        fun logEventSearchPO(poNo: String) {
            logUtils.logInfo(
                action = LogAction.ON_SEARCH_PO,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_po_no),
                        poNo,
                    ),
            )
        }

        fun setBillNo(billNo: String) {
            _keepBillNo.value = billNo
        }

        fun logEventCreatePO(
            poNo: String,
            billNo: String,
            supplierCode: String,
        ) {
            logUtils.logInfo(
                action = LogAction.ON_CREATE_PO,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_create_po),
                        billNo,
                        poNo,
                        supplierCode,
                    ),
            )
        }
    }
