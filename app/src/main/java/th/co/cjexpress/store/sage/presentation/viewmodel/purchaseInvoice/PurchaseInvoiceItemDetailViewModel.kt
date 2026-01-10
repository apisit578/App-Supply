package th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice

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
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.SaveDraftPurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.presentation.state.purchaseInvoice.PurchaseInvoiceUpdateItemState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DateUtils.getTimestampUnix
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class PurchaseInvoiceItemDetailViewModel
    @Inject
    constructor(
        private val purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase,
        private val saveDraftPurchaseInvoiceUseCase: SaveDraftPurchaseInvoiceUseCase,
        private val dispatcherProvider: DispatcherProvider,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        val purchaseInvoiceItemState =
            MutableStateFlow(purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceItem.value)

        private val _confirmBackDialogState = MutableStateFlow(false)
        val confirmBackDialogState: StateFlow<Boolean> = _confirmBackDialogState

        private val _inputActualQtyState =
            MutableStateFlow(
                purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceItem.value?.actualQty ?: 0,
            )
        val inputActualQtyState: StateFlow<Int> = _inputActualQtyState

        private val _canEditState = MutableStateFlow(false)
        val canEditState: StateFlow<Boolean> = _canEditState

        private val _showNotMatchDialogState = MutableStateFlow(false)
        val showNotMatchDialogState: StateFlow<Boolean> = _showNotMatchDialogState

        private val _showQtyEqualZeroDialogState = MutableStateFlow(false)
        val showQtyEqualZeroDialogState: StateFlow<Boolean> = _showQtyEqualZeroDialogState

        fun setInputActualQtyState(inputActualQty: Int) {
            _inputActualQtyState.value = inputActualQty
        }

        fun validateBarcode(barcode: String): Boolean {
            logEventSubmitBarcode(barcode)
            setBaseScreenLoadingState()
            return purchaseInvoiceDetailLiveDataUseCase.getPurchaseInvoiceOrder(barcode) != null
        }

        fun setCanEditState(canEdit: Boolean) {
            _canEditState.value = canEdit
            resetBaseScreenState()
        }

        fun handleCheckIsRefPO(model: PurchaseInvoiceEntryUIModel) {
            purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceItem.value?.qty?.let { qty ->
                logEventSubmitEditItem(model)
                val isRefPo = qty != 0
                if (isRefPo) {
                    saveDraftPurchaseInvoice(model)
                } else {
                    if (model.actualQty == 0) {
                        setShowQtyEqualZeroDialogState(true)
                    } else {
                        updatePurchaseInvoiceLiveDataItem(model)
                    }
                }
            }
        }

        fun updatePurchaseInvoiceLiveDataItem(item: IPurchaseInvoiceProductsModel) {
            purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceItem(item)
            setBaseScreenSuccessState(PurchaseInvoiceUpdateItemState())
        }

        fun updatePurchaseInvoiceItem(actualQty: Int) {
            logEventAdjustItemQuantity(actualQty)
            purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceItemActualQty(actualQty)
        }

        fun resetPurchaseInvoiceItem() {
            purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceItem()
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
                            setBaseScreenSuccessState(PurchaseInvoiceUpdateItemState(data = result.data.data))
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
                                            saveDraftPurchaseInvoice(product)
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

        fun setConfirmBackDialogState(state: Boolean) {
            _confirmBackDialogState.value = state
        }

        fun setShowNotMatchDialogState(state: Boolean) {
            resetBaseScreenState()
            _showNotMatchDialogState.value = state
        }

        fun setShowQtyEqualZeroDialogState(state: Boolean) {
            _showQtyEqualZeroDialogState.value = state
        }

        fun showDialogBarcodeInvalid() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.WARNING,
                    titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                    messageText = resourceUtils.getString(R.string.text_scan_or_enter_barcode),
                    onPositiveClick = { resetBaseScreenState() },
                ),
            )
        }

        fun logEventSubmitBarcode(barcode: String) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_BARCODE_EDIT_STATE,
                message = barcode,
            )
        }

        fun logEventAdjustItemQuantity(actualQty: Int) {
            logUtils.logInfo(
                action = LogAction.ADJUST_ITEM_QUANTITY,
                message = actualQty.toString(),
            )
        }

        fun logEventSubmitEditItem(data: PurchaseInvoiceEntryUIModel) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_EDIT_ITEM_DETAIL,
                message = data.toString(),
            )
        }
    }
