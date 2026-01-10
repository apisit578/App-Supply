package th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseBarcodeFindItemModel
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseBarcodeFindUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.SaveDraftPurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.ScanPurchaseInvoiceOrderUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DateUtils.getTimestampUnix
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class PurchaseInvoiceItemsViewModel
    @Inject
    constructor(
        private val purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase,
        private val saveDraftPurchaseInvoiceUseCase: SaveDraftPurchaseInvoiceUseCase,
        private val scanPurchaseInvoiceOrderUseCase: ScanPurchaseInvoiceOrderUseCase,
        private val purchaseInvoiceUseCase: PurchaseInvoiceUseCase,
        private val purchaseBarcodeFindUseCase: PurchaseBarcodeFindUseCase,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        sealed class UIState {
            data object NavBack : UIState()

            data object NavBackToMain : UIState()

            data class NavToSubmit(
                val data: PurchaseInvoiceEntryUIModel? = null,
            ) : UIState()

            data object NavToSummary : UIState()

            data object NavToListPI : UIState()

            data object NavToListPO : UIState()
        }

        val purchaseInvoiceDetailLiveData =
            MutableStateFlow(purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value)
        val purchaseInvoiceSupplierLiveData =
            MutableStateFlow(purchaseInvoiceUseCase.purchaseInvoiceSupplierLiveData.value)

        fun scanBarcode(barcode: String) {
            logEventScanBarcode(barcode)
            scanPurchaseInvoiceOrderUseCase
                .execute(
                    barcode,
                ).flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            if (purchaseInvoiceSupplierLiveData.value?.supplier?.isRefPo == false) {
                                purchaseBarcodeFind(barcode)
                            } else {
                                saveDraftPurchaseInvoice(result.data)
                            }
                        }

                        is Result.Error -> {
                            if (purchaseInvoiceSupplierLiveData.value?.supplier?.isRefPo == false) {
                                purchaseBarcodeFind(barcode)
                            } else {
                                setBaseScreenErrorState(
                                    TachyonAlertDialogUIModel(
                                        type = result.cause.type,
                                        titleText =
                                            result.cause.title ?: resourceUtils.getString(
                                                R.string.text_unable_to_continue,
                                            ),
                                        messageText = result.cause.message ?: "",
                                        showF1Message = result.cause.type == ErrorType.ERROR,
                                        positiveTextRes = R.string.text_close,
                                        onPositiveClick = {
                                            resetBaseScreenState()
                                        },
                                    ),
                                )
                            }
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

        fun saveDraftPurchaseInvoice(product: IPurchaseInvoiceProductsModel) {
            saveDraftPurchaseInvoiceUseCase
                .execute(product)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceProduct(
                                result.data.data.items,
                            )
                            purchaseInvoiceDetailLiveDataUseCase.setUpdatedTimestamp(
                                result.data.data.items
                                    .maxOfOrNull { it.hhTimestamp }
                                    ?: getTimestampUnix(result.data.timestamp),
                            )
                            setBaseScreenSuccessState(null)
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
                                        if (canRetry) saveDraftPurchaseInvoice(product)
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
                                e.cause?.message
                                    ?: resourceUtils.getString(R.string.text_service_failed),
                            negativeTextRes = R.string.text_close,
                            onNegativeClick = {
                                resetBaseScreenState()
                            },
                            positiveTextRes = R.string.text_retry,
                            onPositiveClick = {
                                resetBaseScreenState()
                                saveDraftPurchaseInvoice(product)
                            },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun productSelected(data: PurchaseInvoiceEntryUIModel) {
            logEventSelectItem(data)
            setBaseScreenSuccessState(UIState.NavToSubmit(data = data))
        }

        fun setPurchaseInvoiceItem(purchaseInvoiceItem: PurchaseInvoiceEntryUIModel) {
            purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceItem(purchaseInvoiceItem)
        }

        fun resetPurchaseInvoiceDetail() {
            purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceDetail()
        }

        fun purchaseBarcodeFind(barcode: String) {
            purchaseBarcodeFindUseCase
                .execute(barcode)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val (item, isSortFormBarFactor) =
                                checkAllowBuy(
                                    barcode,
                                    result.data.data,
                                )
                            when {
                                item != null -> {
                                    purchaseInvoiceDetailLiveDataUseCase.addPurchaseInvoiceProduct(
                                        item,
                                    )
                                    setBaseScreenSuccessState(null)
                                }

                                isSortFormBarFactor -> return@onEach
                                else -> {
                                    setBaseScreenErrorState(
                                        TachyonAlertDialogUIModel(
                                            type = ErrorType.WARNING,
                                            titleText = resourceUtils.getString(R.string.text_error_sku_inactive_title),
                                            messageText = resourceUtils.getString(R.string.text_please_check_and_scan_again),
                                            onPositiveClick = { resetBaseScreenState() },
                                        ),
                                    )
                                }
                            }
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
                                            purchaseBarcodeFind(barcode)
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

        fun checkAllowBuy(
            submittedBarcode: String,
            items: List<PurchaseBarcodeFindItemModel>,
        ): Pair<PurchaseInvoiceEntryUIModel?, Boolean> {
            val matched = items.firstOrNull { it.barcode == submittedBarcode }
            if (matched != null && matched.isAllowBuy) {
                return Pair(mapToUiModel(matched), false)
            }
            val suggestion = items.filter { it.isAllowBuy }.maxByOrNull { it.barFactor }
            return if (suggestion != null) {
                promptChangeBarcode(submittedBarcode, suggestion.barcode)
                Pair(null, true)
            } else {
                Pair(null, false)
            }
        }

        fun mapToUiModel(model: PurchaseBarcodeFindItemModel): PurchaseInvoiceEntryUIModel =
            uiModelMapper.purchaseBarcodeFindUIMapper
                .mapPurchaseInvoiceCreateModelToUIModel(model, 1)

        fun removePurchaseInvoiceProductItem(data: PurchaseInvoiceEntryUIModel) {
            logEventRemoveItem(data.barcode)
            purchaseInvoiceDetailLiveDataUseCase.removePurchaseInvoiceProductItem(data.barcode)
        }

        fun logEventGetItemsList(data: PurchaseInvoiceDetailUIModel?) {
            logUtils.logInfo(
                action = LogAction.GET_PURCHASE_INVOICE_ITEMS,
                message = data.toString(),
            )
        }

        fun logEventSelectItem(data: PurchaseInvoiceEntryUIModel) {
            logUtils.logInfo(
                action = LogAction.SELECT_PURCHASE_INVOICE_ITEM,
                message = data.toString(),
            )
        }

        fun logEventScanBarcode(barcode: String) {
            logUtils.logInfo(
                action = LogAction.SCAN_BARCODE,
                message = barcode,
            )
        }

        fun logEventCheckPurchaseInvoice() {
            logUtils.logInfo(
                action = LogAction.ON_CHECK_PURCHASE_INVOICE,
                message = LogAction.ON_CHECK_PURCHASE_INVOICE.name,
            )
        }

        fun logEventRemoveItem(barcode: String) {
            logUtils.logInfo(
                action = LogAction.REMOVE_ITEM,
                message = barcode,
            )
        }

        fun promptConfirmBackMainMenu() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_title_confirm_back_to_main_menu),
                    messageText =
                        if (purchaseInvoiceSupplierLiveData.value?.supplier?.isRefPo == true) {
                            resourceUtils.getString(R.string.text_confirm_back_to_main_menu)
                        } else {
                            resourceUtils.getString(R.string.text_unsaved_changes_warning_confirm_to_main)
                        },
                    negativeTextRes = R.string.text_no,
                    onNegativeClick = { resetBaseScreenState() },
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = {
                        logEventOnBackMainMenu()
                        resetPurchaseInvoiceDetail()
                        resetBaseScreenState()
                        setBaseScreenSuccessState(UIState.NavBackToMain)
                    },
                ),
            )
        }

        fun promptConfirmBack() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_confirm_exit),
                    messageText =
                        if (purchaseInvoiceSupplierLiveData.value?.supplier?.isRefPo == true) {
                            resourceUtils.getString(R.string.text_confirm_leave_page)
                        } else {
                            resourceUtils.getString(R.string.text_unsaved_changes_warning_confirm_leave_page)
                        },
                    negativeTextRes = R.string.text_no,
                    onNegativeClick = { resetBaseScreenState() },
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = {
                        logEventOnBackPressed()
                        resetPurchaseInvoiceDetail()
                        resetBaseScreenState()
                        setBaseScreenSuccessState(
                            when (purchaseInvoiceDetailLiveData.value?.isEdit) {
                                true -> UIState.NavToListPI
                                false -> UIState.NavToListPO
                                else -> UIState.NavBack
                            },
                        )
                    },
                ),
            )
        }

        fun promptRemoveItem(data: PurchaseInvoiceEntryUIModel) {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.ERROR,
                    titleText = resourceUtils.getString(R.string.text_confirm_to_delete),
                    messageText =
                        resourceUtils.getString(R.string.text_dialog_remove_barcode_desc, data.barcode),
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = {
                        removePurchaseInvoiceProductItem(data)
                        resetBaseScreenState()
                    },
                    negativeTextRes = R.string.text_no,
                    onNegativeClick = { resetBaseScreenState() },
                ),
            )
        }

        fun promptCheckState() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.WARNING,
                    titleText = resourceUtils.getString(R.string.text_title_error_not_found_order),
                    messageText = resourceUtils.getString(R.string.text_error_empty_barcode),
                    positiveTextRes = R.string.text_close,
                    onPositiveClick = { resetBaseScreenState() },
                ),
            )
        }

        fun promptChangeBarcode(
            barcode: String,
            suggestionBarcode: String,
        ) {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_dialog_change_barcode_title),
                    messageText =
                        resourceUtils.getString(
                            R.string.text_dialog_change_barcode_desc,
                            barcode,
                        ),
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = {
                        resetBaseScreenState()
                        purchaseBarcodeFind(suggestionBarcode)
                    },
                    negativeTextRes = R.string.text_no,
                    onNegativeClick = { resetBaseScreenState() },
                ),
            )
        }
    }
