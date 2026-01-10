package th.co.cjexpress.store.sage.presentation.viewmodel.reprintQrFresh

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.OrderReceiveToteDiscountType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigModel
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteDiscountsConfigUseCase
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.ReprintQrFreshLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.ShipmentQrDocumentSubmitUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class ReprintQrFreshItemsViewModel
    @Inject
    constructor(
        private val sdToteDiscountsConfigUseCase: SdToteDiscountsConfigUseCase,
        private val shipmentQrDocumentSubmitUseCase: ShipmentQrDocumentSubmitUseCase,
        private val reprintQrFreshLiveDataUseCase: ReprintQrFreshLiveDataUseCase,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        val documentDetailLD =
            MutableStateFlow(reprintQrFreshLiveDataUseCase.productQrFreshDetail.value)

        sealed class UIState {
            data object NavToMainMenu : UIState()
        }

        fun searchBarcode(barcode: String) {
            logEventScanBarcode(barcode)
            reprintQrFreshLiveDataUseCase.getProductQrFreshItem(barcode)?.let {
                setBaseScreenSuccessState(
                    ReprintQrFreshItemUIModel(
                        barcode = it.barcode,
                        skuName = it.skuName,
                        expireDate = "",
                        qrPrintCount = 1,
                    ),
                )
            } ?: run {
                getProductDiscountConfigs(barcode)
            }
        }

        fun getProductDiscountConfigs(barcode: String) {
            sdToteDiscountsConfigUseCase
                .execute(barcode)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> validateDiscountType(result.data)
                        is Result.Error -> handleDisplayError(result, barcode)
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
                                getProductDiscountConfigs(barcode)
                            },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun validateDiscountType(data: SdToteDiscountsConfigModel) {
            if (data.discountType == OrderReceiveToteDiscountType.FRESHLIFE.name) {
                setBaseScreenSuccessState(
                    ReprintQrFreshItemUIModel(
                        barcode = data.barcode,
                        skuName = data.skuName,
                        expireDate = "",
                        qrPrintCount = 1,
                    ),
                )
            } else {
                setBaseScreenErrorState(
                    TachyonAlertDialogUIModel(
                        type = ErrorType.WARNING,
                        titleText = resourceUtils.getString(R.string.text_title_barcode_mismatch),
                        messageText = resourceUtils.getString(R.string.text_please_check_and_scan_again),
                        onPositiveClick = {
                            resetBaseScreenState()
                        },
                    ),
                )
            }
        }

        fun handleDisplayError(
            result: Result.Error<SdToteDiscountsConfigModel>,
            barcode: String,
        ) {
            val canRetry = result.canRetry
            val negativeTextRes = if (canRetry) R.string.text_close else null
            val positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close
            val title: String
            val message: String
            when (result.cause.errorCode) {
                ErrorCodes.NOT_FOUND -> {
                    title = resourceUtils.getString(R.string.text_data_not_found)
                    message = resourceUtils.getString(R.string.text_purchase_order_barcode, barcode)
                }
                else -> {
                    title = result.cause.title ?: resourceUtils.getString(R.string.text_unable_to_continue)
                    message =
                        if (result.cause.errorCode == ErrorCodes.INVALID_BODY) {
                            resourceUtils.getString(R.string.text_scan_or_enter_barcode)
                        } else {
                            result.cause.message ?: ""
                        }
                }
            }
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = result.cause.type,
                    showF1Message = (result.cause.type == ErrorType.ERROR),
                    titleText = title,
                    messageText = message,
                    ref = result.cause.ref,
                    negativeTextRes = negativeTextRes,
                    onNegativeClick = ::resetBaseScreenState,
                    positiveTextRes = positiveTextRes,
                    onPositiveClick = {
                        resetBaseScreenState()
                        if (canRetry) getProductDiscountConfigs(barcode)
                    },
                ),
            )
        }

        fun prepareEditingItem(item: ReprintQrFreshItemUIModel) {
            reprintQrFreshLiveDataUseCase.setQrFreshItem(item)
            resetBaseScreenState()
        }

        fun resetLiveData() {
            reprintQrFreshLiveDataUseCase.resetQrFreshItemList()
            reprintQrFreshLiveDataUseCase.resetQrFreshItem()
        }

        fun performActionToMainMenu() {
            val items = reprintQrFreshLiveDataUseCase.productQrFreshDetail.value?.items
            if (!items.isNullOrEmpty()) {
                promptBeforeBackToMainMenu()
            } else {
                setBaseScreenSuccessState(UIState.NavToMainMenu)
            }
        }

        fun promptBeforeBackToMainMenu() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_confirm_exit_title_dialog),
                    messageText = resourceUtils.getString(R.string.text_confirm_exit_detail_dialog),
                    positiveTextRes = R.string.text_confirm,
                    onPositiveClick = {
                        logEventOnBackMainMenu()
                        resetLiveData()
                        setBaseScreenSuccessState(UIState.NavToMainMenu)
                    },
                    negativeTextRes = R.string.text_cancel,
                    onNegativeClick = { resetBaseScreenState() },
                ),
            )
        }

        fun promptBeforeSubmit() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_confirm_to_save_document),
                    messageText = resourceUtils.getString(R.string.text_question_confirm_to_save_qr_document),
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = {
                        submitQrDocument(
                            QrDocumentSubmitModel(
                                lotNumber = "L000000000",
                                data =
                                    uiModelMapper.reprintQrFreshSubmitUIMapper.toQrDocumentSubmitItems(
                                        reprintQrFreshLiveDataUseCase.productQrFreshDetail.value
                                            ?.items
                                            .orEmpty(),
                                    ),
                            ),
                        )
                    },
                    negativeTextRes = R.string.text_no,
                    onNegativeClick = { resetBaseScreenState() },
                ),
            )
        }

        fun submitQrDocument(documentData: QrDocumentSubmitModel) {
            logEventSubmitReprintQrDocument(documentData)
            shipmentQrDocumentSubmitUseCase
                .execute(documentData)
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            setBaseScreenErrorState(
                                TachyonAlertDialogUIModel(
                                    type = ErrorType.SUCCESS,
                                    titleText = resourceUtils.getString(R.string.text_save_success),
                                    messageText =
                                        String.format(
                                            resourceUtils.getString(R.string.text_go_to_reprint_qr_menu_on_posadmin),
                                            result.data.documentNumber,
                                        ),
                                    onPositiveClick = {
                                        resetLiveData()
                                        setBaseScreenSuccessState(UIState.NavToMainMenu)
                                    },
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
                                    messageText = cause.message ?: "",
                                    ref = cause.ref,
                                    negativeTextRes = if (canRetry) R.string.text_close else null,
                                    onNegativeClick = ::resetBaseScreenState,
                                    positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        if (canRetry) submitQrDocument(documentData)
                                    },
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
                                submitQrDocument(documentData)
                            },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun logEventScanBarcode(barcode: String) {
            logUtils.logInfo(
                action = LogAction.SCAN_BARCODE,
                message = barcode,
            )
        }

        fun logEventSubmitReprintQrDocument(documentData: QrDocumentSubmitModel) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_REPRINT_QR_DOCUMENT,
                message = documentData.toString(),
            )
        }

        fun logEventSelectItem(data: ReprintQrFreshItemUIModel) {
            logUtils.logInfo(
                action = LogAction.SELECT_ITEM,
                message = data.toString(),
            )
        }
    }
