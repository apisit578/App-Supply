package th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.FilterSupplierUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetFrequencySupplierUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetSupplierByCodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierListUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierUIModel
import th.co.cjexpress.store.sage.presentation.state.purchaseInvoice.PurchaseInvoiceSupplierListState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class PurchaseInvoiceSupplierListViewModel
    @Inject
    constructor(
        private val purchaseInvoiceUseCase: PurchaseInvoiceUseCase,
        private val getFrequencySupplierUseCase: GetFrequencySupplierUseCase,
        private val filterSupplierUseCase: FilterSupplierUseCase,
        private val getSupplierByCodeUseCase: GetSupplierByCodeUseCase,
        private val uiModelMapper: UiModelMapper,
        private val resourceUtils: ResourceUtils,
        private val dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel() {
        sealed class UIState {
            data object NavToPIList : UIState()
        }

        private val _inputPurchaseInvoiceState =
            MutableStateFlow(TextFieldValue(text = "", selection = TextRange(0)))
        val inputPurchaseInvoiceState = _inputPurchaseInvoiceState.asStateFlow()

        private val _purchaseInvoiceSupplierListState =
            MutableStateFlow(PurchaseInvoiceSupplierListState())
        val purchaseInvoiceSupplierListState: StateFlow<PurchaseInvoiceSupplierListState> =
            _purchaseInvoiceSupplierListState

        fun getPurchaseInvoiceSupplierList() {
            getFrequencySupplierUseCase
                .execute()
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            _purchaseInvoiceSupplierListState.value =
                                PurchaseInvoiceSupplierListState(
                                    uiModelMapper.purchaseInvoiceSupplierListUIMapper.toUIModel(result.data),
                                )
                            logEventGetSupplierList(_purchaseInvoiceSupplierListState.value.data)
                            resetBaseScreenState()
                        }

                        is Result.Error ->
                            setBaseScreenErrorState(
                                TachyonAlertDialogUIModel(
                                    type = result.cause.type,
                                    titleText =
                                        result.cause.title ?: resourceUtils.getString(
                                            R.string.text_unable_to_continue,
                                        ),
                                    messageText = result.cause.message ?: "",
                                    showF1Message = result.cause.type == ErrorType.ERROR,
                                    negativeTextRes =
                                        if (result.canRetry) {
                                            R.string.text_close
                                        } else {
                                            null
                                        },
                                    onNegativeClick = {
                                        resetBaseScreenState()
                                    },
                                    positiveTextRes =
                                        if (result.canRetry) {
                                            R.string.text_retry
                                        } else {
                                            R.string.text_close
                                        },
                                    onPositiveClick = {
                                        if (result.canRetry) {
                                            resetBaseScreenState()
                                            getPurchaseInvoiceSupplierList()
                                        } else {
                                            resetBaseScreenState()
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
                            showF1Message = true,
                            messageText = e.cause?.message ?: "",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun getFilterSupplierList(searchKey: String) {
            logEventFilterSupplier(searchKey)
            filterSupplierUseCase
                .execute(searchKey)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            _purchaseInvoiceSupplierListState.value =
                                PurchaseInvoiceSupplierListState(
                                    uiModelMapper.purchaseInvoiceSupplierListUIMapper.toUIModel(result.data),
                                )
                            logEventGetSupplierList(_purchaseInvoiceSupplierListState.value.data)
                            resetBaseScreenState()
                        }

                        is Result.Error ->
                            setBaseScreenErrorState(
                                TachyonAlertDialogUIModel(
                                    type = result.cause.type,
                                    titleText =
                                        result.cause.title ?: resourceUtils.getString(
                                            R.string.text_unable_to_continue,
                                        ),
                                    messageText = result.cause.message ?: "",
                                    showF1Message = result.cause.type == ErrorType.ERROR,
                                    negativeTextRes =
                                        if (result.canRetry) {
                                            R.string.text_close
                                        } else {
                                            null
                                        },
                                    onNegativeClick = {
                                        clearInputPurchaseInvoiceState()
                                        resetBaseScreenState()
                                    },
                                    positiveTextRes =
                                        if (result.canRetry) {
                                            R.string.text_retry
                                        } else {
                                            R.string.text_close
                                        },
                                    onPositiveClick = {
                                        if (result.canRetry) {
                                            resetBaseScreenState()
                                            getPurchaseInvoiceSupplierList()
                                        } else {
                                            clearInputPurchaseInvoiceState()
                                            resetBaseScreenState()
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
                            showF1Message = true,
                            messageText = e.cause?.message ?: "",
                            onPositiveClick = {
                                clearInputPurchaseInvoiceState()
                                resetBaseScreenState()
                            },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun getSupplierByCode(searchCode: String) {
            logEventSelectSupplier(searchCode)
            getSupplierByCodeUseCase
                .execute(searchCode)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val supplier =
                                uiModelMapper.purchaseInvoiceSupplierUIMapper.toUIModel(result.data)
                            setupPurchaseInvoiceLiveData(supplier)
                            setBaseScreenSuccessState(UIState.NavToPIList)
                        }

                        is Result.Error ->
                            setBaseScreenErrorState(
                                TachyonAlertDialogUIModel(
                                    type = result.cause.type,
                                    titleText =
                                        result.cause.title ?: resourceUtils.getString(
                                            R.string.text_unable_to_continue,
                                        ),
                                    messageText = result.cause.message ?: "",
                                    showF1Message = result.cause.type == ErrorType.ERROR,
                                    negativeTextRes =
                                        if (result.canRetry) {
                                            R.string.text_close
                                        } else {
                                            null
                                        },
                                    onNegativeClick = {
                                        resetBaseScreenState()
                                    },
                                    positiveTextRes =
                                        if (result.canRetry) {
                                            R.string.text_retry
                                        } else {
                                            R.string.text_close
                                        },
                                    onPositiveClick = {
                                        if (result.canRetry) {
                                            resetBaseScreenState()
                                            getSupplierByCode(searchCode)
                                        } else {
                                            resetBaseScreenState()
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
                            showF1Message = true,
                            messageText = e.cause?.message ?: "",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun setInputPurchaseInvoiceState(value: TextFieldValue) {
            _inputPurchaseInvoiceState.value = value
        }

        fun clearInputPurchaseInvoiceState() {
            logEventClearInput()
            _inputPurchaseInvoiceState.value = TextFieldValue(text = "", selection = TextRange(0))
        }

        fun setupPurchaseInvoiceLiveData(data: PurchaseInvoiceSupplierUIModel) {
            purchaseInvoiceUseCase.setPurchaseInvoiceSupplier(data)
        }

        fun resetPurchaseInvoiceLiveData() {
            purchaseInvoiceUseCase.resetPurchaseInvoiceSupplier()
        }

        fun logEventGetSupplierList(data: PurchaseInvoiceSupplierListUIModel?) {
            logUtils.logInfo(
                action = LogAction.GET_SUPPLIER_LIST,
                message = data.toString(),
            )
        }

        fun logEventFilterSupplier(searchKey: String) {
            logUtils.logInfo(
                action = LogAction.FILTER_SUPPLIER,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_filter_supplier),
                        searchKey,
                    ),
            )
        }

        fun logEventSelectSupplier(searchKey: String) {
            logUtils.logInfo(
                action = LogAction.SELECT_SUPPLIER,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_select_supplier),
                        searchKey,
                    ),
            )
        }

        fun logEventClearInput() {
            logUtils.logInfo(
                action = LogAction.CLEAR_INPUT_FILTER_SUPPLIER,
                message = LogAction.CLEAR_INPUT_FILTER_SUPPLIER.name,
            )
        }
    }
