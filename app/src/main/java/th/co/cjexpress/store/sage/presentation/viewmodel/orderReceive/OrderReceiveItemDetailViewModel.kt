package th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentItemRequest
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckItemQtyMoreRefQtyUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckLimitBatchUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckLimitBatchUseCase.Mode
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentByBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils

@HiltViewModel
class OrderReceiveItemDetailViewModel
    @Inject
    constructor(
        private val shipmentDocumentUseCase: ShipmentDocumentUseCase,
        private val shipmentDocumentByBarcodeUseCase: ShipmentDocumentByBarcodeUseCase,
        private val checkLimitBatchUseCase: CheckLimitBatchUseCase,
        private val checkItemQtyMoreRefQtyUseCase: CheckItemQtyMoreRefQtyUseCase,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        sealed class UIState {
            data object NavToItemsScreen : UIState()
        }

        val shipmentDocumentItemDetailLiveData: ShipmentDataItemUIModel
            get() {
                return shipmentDocumentUseCase.getShipmentDocumentItemDetail()
            }

        private val _shipmentDocumentItemDetail = MutableStateFlow(shipmentDocumentItemDetailLiveData)
        val shipmentDocumentItemDetail: StateFlow<ShipmentDataItemUIModel> = _shipmentDocumentItemDetail

        private val _inputActualQtyState = MutableStateFlow(shipmentDocumentItemDetailLiveData.actualQty)
        val inputActualQtyState: StateFlow<Int> = _inputActualQtyState

        fun promptBeforeBack() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_confirm_exit),
                    messageText = resourceUtils.getString(R.string.text_confirm_leave_page),
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = {
                        shipmentDocumentUseCase.removeShipmentDocumentItemDetail()
                        setBaseScreenSuccessState(UIState.NavToItemsScreen)
                    },
                    negativeTextRes = R.string.text_no,
                    onNegativeClick = { resetBaseScreenState() },
                ),
            )
        }

        fun submitQrDocument() {
            val currentBatchItem = shipmentDocumentUseCase.getShipmentDocumentItemDetail()
            val barcode = currentBatchItem.barcode
            // receiveAdjust ถ้าเป็นค่าบวกหมายถึงผลต่างจำนวนรับที่เพิ่มขึ้น
            // receiveAdjust ถ้าเป็นค่าลบหมายถึงผลต่างจำนวนรับที่ลดลง
            val receiveAdjust = _inputActualQtyState.value - currentBatchItem.actualQty
            val limitResult = checkLimitBatchUseCase.execute(receiveAdjust, barcode, Mode.ADJUST)
            val summaryOfItem =
                shipmentDocumentUseCase.searchShipmentData(barcode)
                    ?: return

            if (_inputActualQtyState.value == 0) {
                handleInputActualQtyEqualZero()
                return
            }

            if (limitResult is Result.Error) {
                handleCheckLimitBatchError(limitResult)
                return
            }

            val allowOverReceiveResult = checkItemQtyMoreRefQtyUseCase.execute(
                barcode,
                receiveAdjust,
                CheckItemQtyMoreRefQtyUseCase.Mode.ADJUST
            )
            if (allowOverReceiveResult is Result.Error) {
                handleAllowOverReceiveError(
                    error = allowOverReceiveResult,
                    receiveAdjust = receiveAdjust
                )
                return
            }

            saveShipmentDocumentByBarcode(
                barcode = barcode,
                actualQty = summaryOfItem.actualQty,
                receiveAdjust = receiveAdjust
            )
        }

        fun setInputActualQtyState(actualQty: Int) {
            _inputActualQtyState.value = actualQty
        }

        private fun saveShipmentDocumentByBarcode(barcode: String, actualQty: Int, receiveAdjust: Int) {
            val summaryActualQty = (actualQty + receiveAdjust).coerceAtLeast(0)
            val request = buildShipmentItemRequest(barcode, summaryActualQty)

            shipmentDocumentByBarcodeUseCase
                .execute(request)
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result -> handleSubmitQrDocumentResult(result, barcode, receiveAdjust) }
                .catch { e -> handleSubmitQrDocumentException(e) }
                .launchIn(viewModelScope)
        }

        private fun handleInputActualQtyEqualZero() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.WARNING,
                    titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                    messageText = resourceUtils.getString(R.string.text_qty_received_not_equal_zero),
                    positiveTextRes = R.string.text_close,
                    onPositiveClick = { resetBaseScreenState() },
                ),
            )
        }

        private fun handleCheckLimitBatchError(error: Result.Error<*>) {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = error.cause.type,
                    titleText =
                        error.cause.title
                            ?: resourceUtils.getString(R.string.text_unable_to_continue),
                    messageText =
                        error.cause.message
                            ?: resourceUtils.getString(R.string.text_service_failed),
                    positiveTextRes = R.string.text_close,
                    onPositiveClick = { resetBaseScreenState() },
                ),
            )
        }

        private fun handleAllowOverReceiveError(
            error: Result.Error<*>,
            receiveAdjust: Int
        ) {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = error.cause.type,
                    titleText =
                        error.cause.title
                            ?: resourceUtils.getString(R.string.text_unable_to_continue),
                    messageText =
                        error.cause.message
                            ?: resourceUtils.getString(R.string.text_service_failed),
                    positiveTextRes = R.string.text_close,
                    onPositiveClick = {
                        (error.data as? ShipmentDataItemUIModel)?.let {
                            saveShipmentDocumentByBarcode(
                                barcode = it.barcode,
                                actualQty = it.actualQty,
                                receiveAdjust = receiveAdjust
                            )
                        } ?: run {
                            resetBaseScreenState()
                        }
                    },
                ),
            )
        }

        private fun buildShipmentItemRequest(
            barcode: String,
            actualQty: Int,
        ): List<ShipmentItemRequest> = listOf(ShipmentItemRequest(barcode = barcode, actualQty = actualQty))

        private fun handleSubmitQrDocumentResult(
            result: Result<ShipmentDocumentByBarcodeModel>,
            barcode: String,
            receiveAdjust: Int,
        ) {
            when (result) {
                is Result.Success -> {
                    val updatedData =
                        uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(result.data)
                    shipmentDocumentUseCase.updateShipmentSummary(updatedData)
                    shipmentDocumentUseCase.updateShipmentBatchFromReceiveAdjust(
                        barcode,
                        receiveAdjust,
                    )
                    setBaseScreenSuccessState(UIState.NavToItemsScreen)
                }

                is Result.Error -> handleSubmitQrDocumentError(result)
            }
        }

        private fun handleSubmitQrDocumentError(result: Result.Error<*>) {
            val (negativeRes, positiveRes) = getDialogButtonRes(result.canRetry)
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
                    negativeTextRes = negativeRes,
                    onNegativeClick = { resetBaseScreenState() },
                    positiveTextRes = positiveRes,
                    onPositiveClick = {
                        resetBaseScreenState()
                        if (result.canRetry) {
                            submitQrDocument()
                        }
                    },
                    ref = result.cause.ref,
                ),
            )
        }

        private fun handleSubmitQrDocumentException(e: Throwable) {
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

        private fun getDialogButtonRes(canRetry: Boolean): Pair<Int?, Int> =
            if (canRetry) {
                R.string.text_close to R.string.text_retry
            } else {
                null to R.string.text_close
            }
    }
