package th.co.cjexpress.store.sage.presentation.viewmodel.reprintQrFresh

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
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.CheckSubmitItemDetailUseCase
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.ReprintQrFreshLiveDataUseCase
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.component.TachyonDateInputFieldResultModel
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DateUtils.getCurrentYear
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class ReprintQrFreshItemDetailViewModel
    @Inject
    constructor(
        private val checkSubmitItemDetailUseCase: CheckSubmitItemDetailUseCase,
        private val reprintQrFreshLiveDataUseCase: ReprintQrFreshLiveDataUseCase,
        private val resourceUtils: ResourceUtils,
        private val dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel() {
        sealed class UIState {
            data object NavToItemsScreen : UIState()
        }

        val productQrFreshItem =
            MutableStateFlow(reprintQrFreshLiveDataUseCase.productQrFreshItem.value)
        val canEditState: StateFlow<Boolean> =
            MutableStateFlow(productQrFreshItem.value?.expireDate?.isEmpty() == true)
        val inputActualQtyState = MutableStateFlow(productQrFreshItem.value?.qrPrintCount ?: 1)

        val dateInputLD = MutableStateFlow(productQrFreshItem.value?.expireDate ?: "")
        private val _dateInputResult = MutableStateFlow<TachyonDateInputFieldResultModel?>(null)

        fun submitItemDetail(actualQty: Int) {
            validateDateInputState()?.let {
                setBaseScreenErrorState(it)
                return
            }

            checkSubmitItemDetailUseCase
                .execute(
                    item = productQrFreshItem.value,
                    actualQty = actualQty,
                    dateInput = _dateInputResult.value?.resultDate ?: "",
                ).flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            logEventSubmitUpdateItem(result.data)
                            updateProductQrFreshItem(
                                canEdit = canEditState.value,
                                productQrFreshItem = result.data,
                            )
                            setBaseScreenSuccessState(UIState.NavToItemsScreen)
                        }

                        is Result.Error -> {
                            setBaseScreenErrorState(
                                TachyonAlertDialogUIModel(
                                    type = result.cause.type,
                                    titleText =
                                        result.cause.title
                                            ?: resourceUtils.getString(R.string.text_unable_to_continue),
                                    messageText =
                                        result.cause.message
                                            ?: resourceUtils.getString(R.string.text_service_failed),
                                    positiveTextRes = R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
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
                            titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                            messageText =
                                e.cause?.message
                                    ?: resourceUtils.getString(R.string.text_service_failed),
                            positiveTextRes = R.string.text_close,
                            onPositiveClick = {
                                resetBaseScreenState()
                            },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun updateProductQrFreshItem(
            canEdit: Boolean,
            productQrFreshItem: ReprintQrFreshItemUIModel,
        ) {
            if (canEdit) {
                reprintQrFreshLiveDataUseCase.addQrFreshProduct(productQrFreshItem)
            } else {
                reprintQrFreshLiveDataUseCase.updateQrPrintCount(
                    barcode = productQrFreshItem.barcode,
                    expireDate = productQrFreshItem.expireDate,
                    newCount = productQrFreshItem.qrPrintCount,
                )
            }
        }

        fun promptBeforeBack() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_confirm_exit_title_dialog),
                    messageText = resourceUtils.getString(R.string.text_confirm_exit_detail_dialog),
                    positiveTextRes = R.string.text_confirm,
                    onPositiveClick = {
                        logEventOnBackPressed()
                        setBaseScreenSuccessState(UIState.NavToItemsScreen)
                    },
                    negativeTextRes = R.string.text_cancel,
                    onNegativeClick = { resetBaseScreenState() },
                ),
            )
        }

        fun validateDateInputState(): TachyonAlertDialogUIModel? =
            if (_dateInputResult.value != null && _dateInputResult.value?.errorCode == null) {
                null
            } else {
                _dateInputResult.value?.errorCode?.let {
                    TachyonAlertDialogUIModel(
                        type = ErrorType.WARNING,
                        titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                        messageText =
                            resourceUtils.getString(
                                id =
                                    when (it) {
                                        ErrorCodes.DateInputFieldValidationErrorCodes.INCOMPLETE_DATE ->
                                            R.string.text_please_input_expire_date

                                        ErrorCodes.DateInputFieldValidationErrorCodes.INVALID_DAY ->
                                            R.string.text_expiration_date_is_incorrect

                                        ErrorCodes.DateInputFieldValidationErrorCodes.INVALID_MONTH ->
                                            R.string.text_expiration_date_is_incorrect

                                        ErrorCodes.DateInputFieldValidationErrorCodes.INVALID_YEAR ->
                                            R.string.text_expiration_date_is_incorrect

                                        ErrorCodes.DateInputFieldValidationErrorCodes.DATE_NOT_EXIST ->
                                            R.string.text_expiration_date_is_incorrect

                                        ErrorCodes.DateInputFieldValidationErrorCodes.INVALID_DATE_FORMAT ->
                                            R.string.text_expiration_date_is_incorrect
                                    },
                            ),
                        positiveTextRes = R.string.text_close,
                        onPositiveClick = {
                            resetBaseScreenState()
                        },
                    )
                }
            }

        fun setDateInputResult(result: TachyonDateInputFieldResultModel) {
            _dateInputResult.value = result
        }

        fun getDefaultInitialDate(): String = dateInputLD.value.ifEmpty { "//${getCurrentYear()}" }

        fun logEventSubmitUpdateItem(productQrFreshItem: ReprintQrFreshItemUIModel) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_UPDATE_ITEM,
                message = productQrFreshItem.toString(),
            )
        }
    }
