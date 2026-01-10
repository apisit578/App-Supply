package th.co.cjexpress.store.sage.presentation.viewmodel.component

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
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.MenuPurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.ValidatePurchaseInvoiceBillUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class ValidateInvoiceNumberDialogViewModel
    @Inject
    constructor(
        private val validatePurchaseInvoiceBillUseCase: ValidatePurchaseInvoiceBillUseCase,
        menuPurchaseInvoiceUseCase: MenuPurchaseInvoiceUseCase,
        private val resourceUtils: ResourceUtils,
        private val dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel() {
        private val _showDialogState = MutableStateFlow(false)
        val showDialogState: StateFlow<Boolean> = _showDialogState.asStateFlow()

        private val _placeHolderState =
            MutableStateFlow(menuPurchaseInvoiceUseCase.getBillNoValidationPlaceholder())
        val placeHolderState: StateFlow<String> = _placeHolderState.asStateFlow()

        private val _invoiceNumberInputState =
            MutableStateFlow(TextFieldValue(text = "", selection = TextRange(0)))
        val invoiceNumberInputState: StateFlow<TextFieldValue> = _invoiceNumberInputState.asStateFlow()

        fun setDialogShowState(showState: Boolean) {
            logEventInvoiceInputDialog(showState)
            _showDialogState.value = showState
        }

        fun setInvoiceNumberInputState(value: TextFieldValue) {
            _invoiceNumberInputState.value = value.copy(text = value.text.uppercase())
        }

        fun validatePurchaseInvoiceBill(
            billNo: String,
            piNo: String,
            supplierCode: String,
        ) {
            logEventSubmitInputInvoice(
                billNo = billNo,
                piNo = piNo,
                supplierCode = supplierCode,
            )
            validatePurchaseInvoiceBillUseCase
                .execute(
                    billNo = billNo,
                    piNo = piNo,
                    supplierCode = supplierCode,
                ).flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data.isValid) {
                                setBaseScreenSuccessState(billNo)
                            } else {
                                setBaseScreenErrorState(
                                    errorData =
                                        TachyonAlertDialogUIModel(
                                            type = ErrorType.WARNING,
                                            titleText = resourceUtils.getString(R.string.text_error_duplicate_bill_no),
                                            messageText =
                                                resourceUtils.getString(
                                                    R.string.text_error_check_information,
                                                ),
                                            onPositiveClick = {
                                                resetBaseScreenState()
                                            },
                                        ),
                                )
                            }
                        }

                        is Result.Error -> {
                            setBaseScreenErrorState(
                                errorData =
                                    if (result.cause.errorCode == ErrorCodes.INVALID_PARAMETER) {
                                        TachyonAlertDialogUIModel(
                                            type = ErrorType.WARNING,
                                            titleText =
                                                result.cause.title
                                                    ?: resourceUtils.getString(R.string.text_unable_to_continue),
                                            messageText =
                                                result.cause.message
                                                    ?: resourceUtils.getString(R.string.text_please_verify_data),
                                            onPositiveClick = {
                                                resetBaseScreenState()
                                            },
                                            ref = result.cause.ref,
                                        )
                                    } else {
                                        throw result.cause
                                    },
                            )
                        }
                    }
                }.catch { e ->
                    setBaseScreenErrorState(
                        errorData =
                            createErrorDialogDataModel(
                                data = e as? BusinessException,
                                onConfirmRetry = {
                                    validatePurchaseInvoiceBill(
                                        billNo = billNo,
                                        piNo = piNo,
                                        supplierCode = supplierCode,
                                    )
                                },
                            ),
                    )
                }.launchIn(viewModelScope)
        }

        private fun createErrorDialogDataModel(
            data: BusinessException?,
            onConfirmRetry: () -> Unit,
        ): TachyonAlertDialogUIModel {
            val isSessionTimeout = data?.errorCode == ErrorCodes.SESSION_TIMEOUT

            return TachyonAlertDialogUIModel(
                type = if (isSessionTimeout) ErrorType.WARNING else ErrorType.ERROR,
                showF1Message = !isSessionTimeout,
                titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                messageText =
                    data?.message
                        ?: resourceUtils.getString(R.string.text_service_failed),
                negativeTextRes = if ((isSessionTimeout) && (data.type == ErrorType.WARNING)) null else R.string.text_close,
                onNegativeClick = { resetBaseScreenState() },
                positiveTextRes = if (!isSessionTimeout) R.string.text_retry else R.string.text_close,
                onPositiveClick =
                    if ((!isSessionTimeout) && (data?.type == ErrorType.ERROR)) {
                        onConfirmRetry
                    } else {
                        { resetBaseScreenState() }
                    },
                ref = data?.ref,
            )
        }

        fun clearInvoiceNumberInputState() {
            logEventClearInputInvoice()
            _invoiceNumberInputState.value = TextFieldValue(text = "", selection = TextRange(0))
        }

        fun onDismiss() {
            setDialogShowState(false)
        }

        fun logEventInvoiceInputDialog(state: Boolean) {
            logUtils.logInfo(
                action = if (state) LogAction.SHOW_INPUT_INVOICE_DIALOG else LogAction.CANCEL_INPUT_INVOICE,
                message = if (state) LogAction.SHOW_INPUT_INVOICE_DIALOG.name else LogAction.CANCEL_INPUT_INVOICE.name,
            )
        }

        fun logEventSubmitInputInvoice(
            billNo: String,
            piNo: String,
            supplierCode: String,
        ) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_INPUT_INVOICE,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_input_invoice_dialog),
                        billNo,
                        piNo,
                        supplierCode,
                    ),
            )
        }

        fun logEventClearInputInvoice() {
            logUtils.logInfo(
                action = LogAction.CLEAR_INPUT_INVOICE_NUMBER,
                message = LogAction.CLEAR_INPUT_INVOICE_NUMBER.name,
            )
        }
    }
