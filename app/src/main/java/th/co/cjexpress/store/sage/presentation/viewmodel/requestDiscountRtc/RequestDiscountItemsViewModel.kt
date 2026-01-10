package th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc

import androidx.lifecycle.asFlow
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
import th.co.cjexpress.store.sage.domain.model.QRFreshDecodeDataModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateModel
import th.co.cjexpress.store.sage.domain.usecase.BarcodeCodecUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.BdDocumentSubmitUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.BdDocumentValidateUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.RequestDiscountLiveDataUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.component.TachyonBarcodeInputFieldUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentNoDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentSubmitUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.ValidateBarcodeForBdUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountItemsViewModel.UIState.BdDocumentSubmitState
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountItemsViewModel.UIState.ValidateBarcodeForBdState
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RequestDiscountItemsViewModel
    @Inject
    constructor(
        private val requestDiscountLiveDataUseCase: RequestDiscountLiveDataUseCase,
        private val resourceUtils: ResourceUtils,
        private val bdDocumentValidateUseCase: BdDocumentValidateUseCase,
        private val bdDocumentSubmitUseCase: BdDocumentSubmitUseCase,
        private val barcodeCodecUseCase: BarcodeCodecUseCase,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel() {
        sealed class UIState {
            data class ValidateBarcodeForBdState(
                val data: ValidateBarcodeForBdUIModel? = null,
            ) : UIState()

            data class BdDocumentSubmitState(
                val data: BdDocumentSubmitUIModel? = null,
            ) : UIState()
        }

        val bdDetailModel = MutableStateFlow(requestDiscountLiveDataUseCase.docsData.value)

        val editingProductFlow = bdDocumentValidateUseCase.editingProductFlow.asFlow()

        private val _validateBarcodeForBdState = MutableStateFlow(UIState.ValidateBarcodeForBdState())
        val validateBarcodeForBdState: StateFlow<UIState.ValidateBarcodeForBdState> =
            _validateBarcodeForBdState

        private val _upsertBdState = MutableStateFlow(BdDocumentSubmitState())
        val upsertBdState: StateFlow<BdDocumentSubmitState> = _upsertBdState

        private val _showRemoveItemDialogState = MutableStateFlow<BdDetailProductUIModel?>(null)
        val showRemoveItemDialogState: StateFlow<BdDetailProductUIModel?> = _showRemoveItemDialogState

        fun validateBarcode(barcode: String) {
            when (val result = barcodeCodecUseCase.execute(barcode = barcode)) {
                is Result.Success -> {
                    validateBarcodeForBd(
                        barcode =
                            searchBarcodeLDByDecodeData(
                                uiModelMapper.barcodeCodecMapper.toBarcodeCodecUIModel(
                                    result.data,
                                ),
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
                            messageText =
                                cause.message
                                    ?: resourceUtils.getString(R.string.text_service_failed),
                            negativeTextRes = if (canRetry) R.string.text_close else null,
                            onNegativeClick = { resetBaseScreenState() },
                            positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                            onPositiveClick = {
                                resetBaseScreenState()
                                if (canRetry) validateBarcode(barcode)
                            },
                            ref = result.cause.ref,
                        ),
                    )
                }
            }
        }

        fun validateBarcodeForBd(barcode: String) {
            bdDocumentValidateUseCase
                .execute(
                    discountType =
                        bdDetailModel
                            .value
                            ?.discountType
                            ?.name
                            .orEmpty(),
                    barcode = barcode,
                    expiryDate = null,
                ).flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            convertValidateBarcodeToEditingProductLD(result.data.data!!)
                            _validateBarcodeForBdState.value =
                                UIState.ValidateBarcodeForBdState(
                                    uiModelMapper.bdDocumentValidateUIMapper.toUIModel(result.data.data),
                                )
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
                                        if (result.canRetry) validateBarcodeForBd(barcode)
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
                            messageText = e.cause?.message ?: e.message ?: "",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun bdDocumentSubmit() {
            val bdDetails = bdDetailModel.value
            logEventConfirmRequestDiscount(bdDetails)
            bdDocumentSubmitUseCase
                .execute(
                    documentNumber = bdDetails!!.documentNumber,
                    discountType = bdDetails.discountType.name,
                    items = bdDetails.products,
                ).flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            setBaseScreenSuccessState(
                                BdDocumentSubmitState(
                                    result.data.data?.let {
                                        uiModelMapper.bdDocumentSubmitUIMapper.toUIModel(
                                            it,
                                        )
                                    },
                                ),
                            )
                        }

                        is Result.Error -> {
                            setBaseScreenErrorState(
                                TachyonAlertDialogUIModel(
                                    type = result.cause.type,
                                    showF1Message = result.cause.errorCode != ErrorCodes.NOT_FOUND,
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
                                        if (result.canRetry) bdDocumentSubmit()
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
                            messageText = e.cause?.message ?: e.message ?: "",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun setEditingProductLD(product: BdDetailProductUIModel) {
            logEventSelectItem(product)
            requestDiscountLiveDataUseCase.setIsNewProduct(false)
            requestDiscountLiveDataUseCase.setEditingProduct(product)
        }

        fun convertValidateBarcodeToEditingProductLD(validateBarcode: BdDocumentValidateModel) {
            requestDiscountLiveDataUseCase.setIsNewProduct(true)
            requestDiscountLiveDataUseCase.setEditingProduct(
                BdDetailProductUIModel(
                    barcode = validateBarcode.barcode,
                    productName = validateBarcode.productName,
                    expiredDate = null,
                    numberOfDiscounted = null,
                    barFactor = validateBarcode.barFactor,
                    unitFactor = validateBarcode.unitName,
                ),
            )
        }

        fun resetValidateBarcodeForBdState() {
            ValidateBarcodeForBdState(null)
        }

        fun resetBdDocumentSubmitState() {
            BdDocumentSubmitState(null)
        }

        fun searchBarcodeLDByDecodeData(decodeData: TachyonBarcodeInputFieldUIModel): String {
            Timber.tag("STATE").i("searchBarcodeLD, decodeData = $decodeData")
            return if (decodeData.data is QRFreshDecodeDataModel) {
                logEventScanQRCode(decodeData.data.barcode)
                decodeData.data.barcode
            } else {
                logEventScanBarcode(decodeData.barcode)
                decodeData.barcode
            }
        }

        fun showRemoveItemDialog(productModel: BdDetailProductUIModel) {
            _showRemoveItemDialogState.value = productModel
        }

        fun hideRemoveItemDialog() {
            _showRemoveItemDialogState.value = null
        }

        fun removeProduct(product: BdDetailProductUIModel) {
            requestDiscountLiveDataUseCase.removeProductFromBdDetail(product.barcode)
            hideRemoveItemDialog()
        }

        fun logEventScanBarcode(barcode: String) {
            logUtils.logInfo(
                action = LogAction.SCAN_BARCODE,
                message = barcode,
            )
        }

        fun logEventScanQRCode(barcode: String) {
            logUtils.logInfo(
                action = LogAction.SCAN_QR_CODE,
                message = barcode,
            )
        }

        fun logEventSelectItem(product: BdDetailProductUIModel) {
            logUtils.logInfo(
                action = LogAction.SELECT_REQUEST_DISCOUNT_ITEM,
                message = product.toString(),
            )
        }

        fun logEventConfirmRequestDiscount(bdDetails: BdDocumentNoDetailUIModel?) {
            logUtils.logInfo(
                action =
                    if (bdDetailModel.value?.documentNumber?.isNotEmpty() == true) {
                        LogAction.CONFIRM_EDIT_REQUEST_DISCOUNT_DOC
                    } else {
                        LogAction.CONFIRM_CREATE_REQUEST_DISCOUNT_DOC
                    },
                message = bdDetails.toString(),
            )
        }
    }
