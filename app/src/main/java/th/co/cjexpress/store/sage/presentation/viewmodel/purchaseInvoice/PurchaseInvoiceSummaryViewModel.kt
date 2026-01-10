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
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceValidateBillModel
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceCalculateUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.SaveDraftPurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.ValidatePurchaseInvoiceBillUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceCalculateUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class PurchaseInvoiceSummaryViewModel
    @Inject
    constructor(
        private val resourceUtils: ResourceUtils,
        private val purchaseInvoiceUseCase: PurchaseInvoiceUseCase,
        private val purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase,
        private val purchaseInvoiceCalculateUseCase: PurchaseInvoiceCalculateUseCase,
        private val validatePurchaseInvoiceBillUseCase: ValidatePurchaseInvoiceBillUseCase,
        private val saveDraftPurchaseInvoiceUseCase: SaveDraftPurchaseInvoiceUseCase,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel() {
        sealed class UIState {
            data object NavBack : UIState()

            data class SaveDraftSuccess(
                val piNo: String,
            ) : UIState()
        }

        val supplierLD: PurchaseInvoiceSupplierDetailUIModel
            get() {
                return purchaseInvoiceUseCase.getPurchaseInvoiceSupplier()
            }

        val isRefPo: Boolean
            get() {
                return purchaseInvoiceUseCase.isRefPo()
            }

        private val purchaseInvoiceDetail: PurchaseInvoiceDetailUIModel
            get() {
                return purchaseInvoiceDetailLiveDataUseCase.getPIDetail()
            }
        private val _piDetailState = MutableStateFlow(purchaseInvoiceDetail)
        val piDetailState: StateFlow<PurchaseInvoiceDetailUIModel> = _piDetailState

        private val purchaseInvoiceBillNo: String
            get() {
                return purchaseInvoiceDetailLiveDataUseCase.getPurchaseInvoiceDetailBillNo()
            }

        private val _billNoState = MutableStateFlow(purchaseInvoiceBillNo)
        val billNoState: StateFlow<String> = _billNoState

        private val _purchaseInvoiceCalculatedState =
            MutableStateFlow<PurchaseInvoiceCalculateUIModel?>(null)
        val purchaseInvoiceCalculatedState: StateFlow<PurchaseInvoiceCalculateUIModel?> =
            _purchaseInvoiceCalculatedState

        private val _showInvoiceInputDialogState = MutableStateFlow(false)
        val showInvoiceInputDialogState: StateFlow<Boolean> = _showInvoiceInputDialogState

        private val _confirmBackMainMenuDialogState = MutableStateFlow(false)
        val confirmBackMainMenuDialogState: StateFlow<Boolean> = _confirmBackMainMenuDialogState

        private val _confirmBackDialogState = MutableStateFlow(false)
        val confirmBackDialogState: StateFlow<Boolean> = _confirmBackDialogState

        private val _showConfirmSaveDialogState = MutableStateFlow(false)
        val showConfirmSaveDialogState: StateFlow<Boolean> = _showConfirmSaveDialogState

        fun setConfirmSaveDialogState(state: Boolean) {
            _showConfirmSaveDialogState.value = state
        }

        fun setConfirmBackMainMenuDialogState(state: Boolean) {
            _confirmBackMainMenuDialogState.value = state
        }

        fun setConfirmBackDialogState(state: Boolean) {
            _confirmBackDialogState.value = state
        }

        fun setShowInvoiceInputDialogState(state: Boolean) {
            _showInvoiceInputDialogState.value = state
        }

        fun hideKeyboard(context: Context) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        fun calculatePurchaseInvoice() {
            purchaseInvoiceCalculateUseCase
                .execute()
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            _purchaseInvoiceCalculatedState.value =
                                uiModelMapper.purchaseInvoiceCalculateUIMapper.mapUIModel(result.data.data)
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
                                    onNegativeClick = { setBaseScreenSuccessState(data = UIState.NavBack) },
                                    positiveTextRes = if (result.canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        if (result.canRetry) {
                                            calculatePurchaseInvoice()
                                        } else {
                                            setBaseScreenSuccessState(data = UIState.NavBack)
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
                            onNegativeClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun validatePurchaseInvoiceBill(
            billNo: String,
            piNo: String,
            supplierCode: String,
        ) {
            val retry = { validatePurchaseInvoiceBill(billNo, piNo, supplierCode) }

            validatePurchaseInvoiceBillUseCase
                .execute(
                    billNo = billNo,
                    piNo = piNo,
                    supplierCode = supplierCode,
                ).flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> handleValidationSuccess(result)
                        is Result.Error -> showResultError(result, onRetry = retry)
                    }
                }.catch { e -> showThrowableError(e, onRetry = retry) }
                .launchIn(viewModelScope)
        }

        private fun handleValidationSuccess(result: Result.Success<PurchaseInvoiceValidateBillModel>) {
            if (!result.data.isValid) {
                showDuplicateBillWarning()
                return
            }

            purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value?.let { detail ->
                saveDraftPurchaseInvoice(detail.entries.toList())
            }
        }

        fun showDuplicateBillWarning() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.WARNING,
                    titleText = resourceUtils.getString(R.string.text_error_duplicate_bill_no),
                    messageText = resourceUtils.getString(R.string.text_error_check_information),
                    onPositiveClick = { resetBaseScreenState() },
                ),
            )
        }

        fun showResultError(
            result: Result.Error<PurchaseInvoiceValidateBillModel>,
            onRetry: () -> Unit,
        ) {
            val canRetry = result.canRetry
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
                    negativeTextRes = if (canRetry) R.string.text_close else null,
                    onNegativeClick = { resetBaseScreenState() },
                    positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                    onPositiveClick = {
                        resetBaseScreenState()
                        if (canRetry) onRetry()
                    },
                    ref = result.cause.ref,
                ),
            )
        }

        private fun showThrowableError(
            e: Throwable,
            onRetry: () -> Unit,
        ) {
            setBaseScreenErrorState(
                createRetryAlertDialogData(
                    message = e.message,
                    onConfirmRetry = onRetry,
                ),
            )
        }

        fun createRetryAlertDialogData(
            message: String?,
            onConfirmRetry: () -> Unit,
        ) = TachyonAlertDialogUIModel(
            type = ErrorType.ERROR,
            showF1Message = true,
            titleText = resourceUtils.getString(R.string.text_unable_to_continue),
            messageText =
                message
                    ?: resourceUtils.getString(
                        R.string.text_service_failed,
                    ),
            negativeTextRes = R.string.text_close,
            onNegativeClick = {
                resetBaseScreenState()
            },
            positiveTextRes = R.string.text_retry,
            onPositiveClick = onConfirmRetry,
        )

        fun saveDraftPurchaseInvoice(products: List<IPurchaseInvoiceProductsModel>) {
            saveDraftPurchaseInvoiceUseCase
                .executeList(products)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            setBaseScreenSuccessState(UIState.SaveDraftSuccess(piNo = result.data.data.piNo))
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
                                            saveDraftPurchaseInvoice(products)
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

        fun updatePurchaseInvoiceDetailBillNo(billNo: String) {
            purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceDetailBillNo(billNo)
            _billNoState.value = billNo
        }

        fun logEventEditInvoiceNumber(billNo: String) {
            logUtils.logInfo(
                action = LogAction.EDIT_INVOICE_NUMBER,
                message = billNo,
            )
        }

        fun logEventSavePurchaseInvoice(state: Boolean) {
            logUtils.logInfo(
                action = if (state) LogAction.CONFIRM_SAVE_PURCHASE_INVOICE else LogAction.CANCEL_SAVE_PURCHASE_INVOICE,
                message = if (state) LogAction.CONFIRM_SAVE_PURCHASE_INVOICE.name else LogAction.CANCEL_SAVE_PURCHASE_INVOICE.name,
            )
        }
    }
