package th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.analytics.ProcessStatusType
import th.co.cjexpress.store.sage.analytics.ProcessTimeTrackingType
import th.co.cjexpress.store.sage.analytics.usecase.TrackTimeEventUseCase
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.ScanBarcodeButtonType
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentItemRequest
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckItemQtyMoreRefQtyUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckLimitBatchUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckProductInDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckTotePrefixUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.OrderReceiveCheckSkuUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentByBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentByBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentByBarcodeBackState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentByBarcodeCheckState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentByBarcodeStartOverState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class OrderReceiveItemsViewModel
    @Inject
    constructor(
        private val shipmentDocumentUseCase: ShipmentDocumentUseCase,
        private val shipmentDocumentByBarcodeUseCase: ShipmentDocumentByBarcodeUseCase,
        private val checkLimitBatchUseCase: CheckLimitBatchUseCase,
        private val checkProductInDocumentUseCase: CheckProductInDocumentUseCase,
        private val checkItemQtyMoreRefQtyUseCase: CheckItemQtyMoreRefQtyUseCase,
        private val orderReceiveCheckSkuUseCase: OrderReceiveCheckSkuUseCase,
        private val checkTotePrefixUseCase: CheckTotePrefixUseCase,
        private val trackTimeEventUseCase: TrackTimeEventUseCase,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        sealed class UIState {
            data object NavToItemDetail : UIState()
        }

        val shipmentSummaryLiveData =
            MutableStateFlow(shipmentDocumentUseCase.shipmentSummaryLiveData.value)

        val shipmentBatchLiveData =
            MutableStateFlow(shipmentDocumentUseCase.shipmentBatchLiveData.value)

        private val _countActualQty = MutableStateFlow<Int?>(null)
        val countActualQty: StateFlow<Int?> = _countActualQty

        private val _startOverCount = MutableStateFlow(1)
        val startOverCount: StateFlow<Int> = _startOverCount

        fun checkLimitBatch(barcode: String) {
            flow { emit(checkLimitBatchUseCase.execute(barcode = barcode)) }
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            checkProductInDocument(barcode)
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
                                    onPositiveClick = { resetBaseScreenState() },
                                ),
                            )
                        }
                    }
                }.launchIn(viewModelScope)
        }

        fun checkProductInDocument(barcode: String) {
            flow { emit(checkProductInDocumentUseCase.execute(barcode)) }
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            checkItemQtyMoreRefQty(barcode)
                        }

                        is Result.Error -> {
                            checkTotePrefix(barcode)
                        }
                    }
                }.launchIn(viewModelScope)
        }

        fun checkTotePrefix(barcode: String) {
            flow { emit(checkTotePrefixUseCase.execute(barcode)) }
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            checkSku(barcode)
                        }

                        is Result.Error -> {
                            if (result.data != null) {
                                promptAlertExtraItem(
                                    ShipmentDataItemUIModel(
                                        barcode = barcode,
                                        actualQty = 1,
                                        isTote = true,
                                        productName = resourceUtils.getString(R.string.text_tote),
                                        qty = 0,
                                        toteCode = barcode,
                                        unitFactor = 1,
                                        unitName = resourceUtils.getString(R.string.text_tote),
                                        overQty = 1,
                                    ),
                                )
                            } else {
                                promptAlertExtraItem(null, allowExtraItems = false)
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        }

        fun checkSku(barcode: String) {
            logEventScanBarcode(barcode)
            orderReceiveCheckSkuUseCase
                .execute(barcode)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            // เนื่องจากหลังจากเช็ค sku ผ่านแล้วไม่มี popup แสดงการแจ้งเตือนว่าสินค้าเกิน / นอก LD
                            promptAlertExtraItem(result.data)
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
                                            checkSku(barcode)
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

        fun promptAlertExtraItem(
            data: ShipmentDataItemUIModel?,
            allowExtraItems: Boolean = true,
        ) {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.WARNING,
                    titleText =
                        resourceUtils.getString(R.string.text_title_no_barcode_in_shipment_order),
                    messageText =
                        resourceUtils.getString(
                            R.string.text_detail_no_barcode_in_shipment_order,
                            shipmentDocumentUseCase.getShipmentLDNo(),
                        ),
                    positiveTextRes = R.string.text_close,
                    onPositiveClick = {
                        resetBaseScreenState()
                        if (allowExtraItems) {
                            upsertItemDocument(data?.barcode.orEmpty(), data)
                        }
                    },
                ),
            )
        }

        fun checkItemQtyMoreRefQty(barcode: String) {
            flow { emit(checkItemQtyMoreRefQtyUseCase.execute(barcode)) }
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            upsertItemDocument(barcode, result.data)
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
                                        if (result.data != null) {
                                            upsertItemDocument(barcode, result.data)
                                        } else {
                                            resetBaseScreenState()
                                        }
                                    },
                                ),
                            )
                        }
                    }
                }.launchIn(viewModelScope)
        }

        fun upsertItemDocument(
            barcode: String,
            dataSubmit: ShipmentDataItemUIModel? = null,
        ) {
            saveShipmentDocumentByBarcode(
                barcode,
                dataSubmit = dataSubmit,
                dataRequest =
                    shipmentDocumentUseCase.setIncreaseShipmentDocumentByBarcode(
                        barcode,
                    ),
                isIncrease = true,
            )
        }

        fun saveShipmentDocumentByBarcode(
            barcode: String,
            dataSubmit: ShipmentDataItemUIModel? = null,
            dataRequest: List<ShipmentItemRequest>,
            isIncrease: Boolean = true,
        ) {
            shipmentDocumentByBarcodeUseCase
                .execute(
                    data = dataRequest,
                ).flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val updatedData =
                                uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(result.data)
                            shipmentDocumentUseCase.updateShipmentSummary(updatedData)

                            val batchItem = shipmentDocumentUseCase.searchShipmentBatchData(barcode)
                            batchItem?.let {
                                shipmentDocumentUseCase.updateShipmentBatch(
                                    barcode,
                                    isIncrease,
                                )
                            }
                                ?: dataSubmit?.let { shipmentDocumentUseCase.addShipmentBatchItem(it) }
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
                                    onNegativeClick = { resetBaseScreenState() },
                                    positiveTextRes = if (result.canRetry) R.string.text_retry else R.string.text_close,
                                    onPositiveClick = {
                                        resetBaseScreenState()
                                        if (result.canRetry) {
                                            saveShipmentDocumentByBarcode(
                                                barcode,
                                                dataSubmit,
                                                dataRequest,
                                                isIncrease,
                                            )
                                        }
                                    },
                                    ref = result.cause.ref,
                                ),
                            )
                        }
                    }
                }.catch { e -> handleSaveShipmentDocumentByBarcodeException(e) }
                .launchIn(viewModelScope)
        }

        fun removeShipmentBatchItem(data: ShipmentDataItemUIModel) {
            logEventRemoveItem(data)
            saveShipmentDocumentByBarcode(
                barcode = data.barcode,
                dataRequest = shipmentDocumentUseCase.setReduceShipmentDocumentByBarcode(data.barcode),
                isIncrease = false,
            )
        }

        fun setCountActualQty(data: Int) {
            _countActualQty.value = data
        }

        fun increaseStartOverCount() {
            _startOverCount.value += 1
        }

        fun dialogConfirmBackOrch() {
            logEventOnBack(true)
            setBaseScreenSuccessState(ShipmentDocumentByBarcodeBackState())
        }

        fun confirmSaveOrch() {
            logEventOnSaveOrder(true)
        }

        fun dialogDismissSaveOrch() {
            logEventOnSaveOrder(false)
            setBaseScreenSuccessState(ShipmentDocumentByBarcodeBackState())
        }

        fun logEventScanBarcode(barcode: String) {
            logUtils.logInfo(
                action = LogAction.SCAN_BARCODE,
                message = barcode,
            )
        }

        fun logEventSubmitSummaryOrder() {
            logUtils.logInfo(
                action = LogAction.SUBMIT_SUMMARY_ORDER,
                message = LogAction.SUBMIT_SUMMARY_ORDER.name,
            )
        }

        fun logEventOnBack(state: Boolean) {
            logUtils.logInfo(
                action = if (state) LogAction.ON_CONFIRM_BACK_STATE else LogAction.ON_CANCEL_BACK_STATE,
                message = if (state) LogAction.ON_CONFIRM_BACK_STATE.name else LogAction.ON_CANCEL_BACK_STATE.name,
            )
        }

        fun logEventOnSaveOrder(state: Boolean) {
            logUtils.logInfo(
                action = if (state) LogAction.ON_CONFIRM_SAVE_ORDER else LogAction.ON_CANCEL_SAVE_ORDER,
                message = if (state) LogAction.ON_CONFIRM_SAVE_ORDER.name else LogAction.ON_CANCEL_SAVE_ORDER.name,
            )
        }

        fun logEventRemoveItem(data: ShipmentDataItemUIModel) {
            logUtils.logInfo(
                action = LogAction.REMOVE_ITEM,
                message = data.toString(),
            )
        }

        private fun handleSaveShipmentDocumentByBarcodeException(e: Throwable) {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.ERROR,
                    titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                    showF1Message = true,
                    messageText = e.cause?.message.orEmpty(),
                    onPositiveClick = { resetBaseScreenState() },
                ),
            )
        }

        fun mapTypeButtonToState(typeButton: ScanBarcodeButtonType) =
            when (typeButton) {
                ScanBarcodeButtonType.CHECK -> {
                    logEventSubmitSummaryOrder()
                    setBaseScreenSuccessState(ShipmentDocumentByBarcodeCheckState(null))
                }

                ScanBarcodeButtonType.START_OVER ->
                    if (shipmentBatchLiveData.value?.items?.isEmpty() == true) {
                        setBaseScreenErrorState(
                            TachyonAlertDialogUIModel(
                                type = ErrorType.WARNING,
                                titleText = resourceUtils.getString(R.string.text_title_error_not_found_order),
                                messageText = resourceUtils.getString(R.string.text_description_error_not_found_order),
                                positiveTextRes = R.string.text_close,
                                onPositiveClick = {
                                    logEventOnBackMainMenu()
                                    resetBaseScreenState()
                                },
                            ),
                        )
                    } else {
                        setBaseScreenSuccessState(
                            ShipmentDocumentByBarcodeStartOverState(
                                data =
                                    ShipmentDocumentByBarcodeUIModel(
                                        shipmentBatchLiveData.value?.sumOfActualQty ?: 0,
                                    ),
                            ),
                        )
                    }

                ScanBarcodeButtonType.BACK ->
                    setBaseScreenSuccessState(ShipmentDocumentByBarcodeBackState(null))
            }

        fun setShipmentDocumentItemDetail(item: ShipmentDataItemUIModel) {
            shipmentDocumentUseCase.setShipmentDocumentItemDetail(item)
        }

        fun removeShipmentBatchModel() {
            shipmentDocumentUseCase.removeShipmentBatchModel()
        }

        fun promptConfirmBack() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_confirm_exit),
                    messageText = resourceUtils.getString(R.string.text_unsaved_items_back_warning),
                    onNegativeClick = { resetBaseScreenState() },
                    negativeTextRes = R.string.text_no,
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = {
                        val items = shipmentDocumentUseCase.shipmentBatchLiveData.value?.items
                        if (items != null && items.isEmpty()) {
                            setBaseScreenSuccessState(ShipmentDocumentByBarcodeBackState())
                        } else {
                            promptConfirmSave()
                        }
                    },
                ),
            )
        }

        fun promptConfirmSave() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_confirm_to_save),
                    messageText = resourceUtils.getString(R.string.text_please_recheck_before_save),
                    negativeTextRes = R.string.text_not_save,
                    onNegativeClick = { dialogDismissSaveOrch() },
                    positiveTextRes = R.string.text_save,
                    onPositiveClick = { confirmSaveOrch() },
                ),
            )
        }

        private fun getDialogButtonRes(canRetry: Boolean): Pair<Int?, Int> =
            if (canRetry) {
                R.string.text_close to R.string.text_retry
            } else {
                null to R.string.text_close
            }

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
