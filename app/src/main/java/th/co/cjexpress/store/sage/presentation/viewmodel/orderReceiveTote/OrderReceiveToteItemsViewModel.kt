package th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote

import androidx.lifecycle.asFlow
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
import th.co.cjexpress.store.sage.domain.model.QRFreshDecodeDataModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteDiscountsConfigUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteReceivingBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitAndGetLatestBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.component.TachyonBarcodeInputFieldUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteReceivingBarcodeModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDiscountsConfigUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeExpiryUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteBarcodeEditingSubmitState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteInputExpiryDateSubmitState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OrderReceiveToteItemsViewModel
    @Inject
    constructor(
        private val sdToteSubmitUseCase: SdToteSubmitUseCase,
        private val sdToteSubmitAndGetLatestBarcodeUseCase: SdToteSubmitAndGetLatestBarcodeUseCase,
        private val sdToteDiscountsConfigUseCase: SdToteDiscountsConfigUseCase,
        private val sdToteReceivingBarcodeUseCase: SdToteReceivingBarcodeUseCase,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        val submitBarcodeFlowLD = sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.asFlow()
        val docsDetailFlowLD = MutableStateFlow(sdToteReceivingBarcodeUseCase.docsDetailFLowLD.value!!)

        private fun submitBarcode(receivingBarcode: SdToteSubmitUIModel) {
            sdToteSubmitUseCase
                .execute(
                    sdNo = receivingBarcode.sdNo,
                    toteCode = receivingBarcode.toteCode,
                    barcode = receivingBarcode.barcode,
                    skuCode = receivingBarcode.skuCode,
                    discountType = receivingBarcode.discountType,
                    actualQty = receivingBarcode.actualQty,
                    expList = receivingBarcode.expList.toList(),
                ).flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            updateReceivingBarcodeLD(receivingBarcode, result.data.data?.expList)
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
                                            submitBarcode(receivingBarcode)
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
                            messageText = e.cause?.message ?: "",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        private fun submitAndGetLatestBarcode(receivingBarcode: SdToteSubmitUIModel) {
            sdToteSubmitAndGetLatestBarcodeUseCase
                .execute(
                    sdNo = receivingBarcode.sdNo,
                    toteCode = receivingBarcode.toteCode,
                    barcode = receivingBarcode.barcode,
                    skuCode = receivingBarcode.skuCode,
                    discountType = receivingBarcode.discountType,
                    actualQty = receivingBarcode.actualQty,
                    expList = receivingBarcode.expList.toList(),
                ).flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val newBarcodeData =
                                uiModelMapper.sdToteDocDetailUIMapper.toUIModel(result.data.latestBarcode)
                            addNewReceivingBarcodeLD(newBarcodeData)
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
                                            submitAndGetLatestBarcode(receivingBarcode)
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
                            messageText = e.cause?.message ?: "",
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun getDiscountsConfigByBarcode(decodedData: TachyonBarcodeInputFieldUIModel) {
            sdToteDiscountsConfigUseCase
                .execute(decodedData.barcode)
                .flowOn(dispatcherProvider.io)
                .onStart { setBaseScreenLoadingState() }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val expList: MutableList<ISdToteExpiryDateModel> =
                                if (decodedData.data is QRFreshDecodeDataModel) {
                                    mutableListOf(
                                        SdToteDocsDetailBarcodeExpiryUIModel(
                                            expDate = decodedData.data.expDate,
                                            actualQty = 1,
                                            qty = 0,
                                            lotNo = decodedData.data.lotNo
                                        )
                                    )
                                } else {
                                    mutableListOf()
                                }

                            val ui = uiModelMapper.sdToteDiscountsConfigUIMapper.toUIModel(result.data)
                                .also { it.expList = expList }
                            setBaseScreenSuccessState(data = ui)
                        }
                        is Result.Error -> {
                            setBaseScreenErrorState(
                                buildDiscountsConfigErrorDialog(
                                    error = result,
                                    onRetry = { getDiscountsConfigByBarcode(decodedData) },
                                    onClose = ::resetBaseScreenState,
                                    str = resourceUtils::getString,
                                ),
                            )
                        }
                    }
                }.catch { e ->
                    setBaseScreenErrorState(
                        TachyonAlertDialogUIModel(
                            type = ErrorType.ERROR,
                            titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                            messageText = e.cause?.message.orEmpty(),
                            positiveTextRes = R.string.text_close,
                            onPositiveClick = ::resetBaseScreenState,
                        ),
                    )
                }.launchIn(viewModelScope)
        }

        fun buildDiscountsConfigErrorDialog(
            error: Result.Error<SdToteDiscountsConfigModel>,
            onRetry: () -> Unit,
            onClose: () -> Unit,
            str: (resId: Int) -> String,
        ): TachyonAlertDialogUIModel {
            val cause = error.cause
            val isNotFound = cause.errorCode == ErrorCodes.NOT_FOUND
            val (title, message) =
                if (isNotFound) {
                    str(R.string.text_unable_to_continue) to str(R.string.text_barcode_or_qr_mismatch)
                } else {
                    (cause.title ?: str(R.string.text_unable_to_continue)) to (cause.message ?: str(R.string.text_service_failed))
                }

            val canRetry = error.canRetry
            return TachyonAlertDialogUIModel(
                type = cause.type,
                showF1Message = (cause.type == ErrorType.ERROR),
                titleText = title,
                messageText = message,
                positiveTextRes = if (canRetry) R.string.text_retry else R.string.text_close,
                onPositiveClick = {
                    onClose()
                    if (canRetry) onRetry()
                },
                negativeTextRes = if (canRetry) R.string.text_close else null,
                onNegativeClick = onClose,
                ref = cause.ref,
            )
        }

        fun updateExistingBarcode(
            submitBarcodeData: SdToteSubmitUIModel,
            isQRFresh: Boolean,
        ) {
            if (submitBarcodeData.discountType == OrderReceiveToteDiscountType.FRESHLIFE.name && !isQRFresh) {
                Timber.tag("STATE").i("updateExistingBarcode setBaseScreenSuccessState")
                setBaseScreenSuccessState(SdToteInputExpiryDateSubmitState(data = submitBarcodeData))
            } else {
                submitBarcode(submitBarcodeData) // , submitBarcodeData.expList)
            }
        }

        fun addNewBarcode(newBarcodeData: SdToteSubmitUIModel) {
            Timber.tag("STATE").i("viewmodel addNewBarcode")
            logEventAddNewItem(newBarcodeData)
            if (newBarcodeData.discountType == OrderReceiveToteDiscountType.FRESHLIFE.name && newBarcodeData.expList.isEmpty()) {
                Timber.tag("STATE").i("addNewBarcode setBaseScreenSuccessState")
                setBaseScreenSuccessState(SdToteInputExpiryDateSubmitState(data = newBarcodeData))
            } else {
                submitAndGetLatestBarcode(newBarcodeData)
            }
        }

        fun searchBarcodeLD(barcode: String): SdToteDocsDetailBarcodeUIModel? = sdToteReceivingBarcodeUseCase.searchBarcode(barcode)

        fun searchBarcodeLDByDecodeData(decodeData: TachyonBarcodeInputFieldUIModel): SdToteDocsDetailBarcodeUIModel? {
            Timber.tag("STATE").i("searchBarcodeLD, decodeData = $decodeData")
            return if (decodeData.data is QRFreshDecodeDataModel) {
                sdToteReceivingBarcodeUseCase.searchBarcodeByLotNo(
                    barcode = decodeData.data.barcode,
                    lotNo = decodeData.data.lotNo,
                    expiryDate = decodeData.data.expDate,
                )
            } else {
                searchBarcodeLD(barcode = decodeData.barcode)
            }
        }

        fun barcodeSelected(editingBarcodeData: SdToteSubmitUIModel) {
            Timber.tag("STATE").i("selectBarcodeToEdit editingBarcodeData = $editingBarcodeData")
            logEventSelectItem(editingBarcodeData)
            setBaseScreenSuccessState(SdToteBarcodeEditingSubmitState(data = editingBarcodeData))
        }

        fun createSubmitBarcodeLD(
            isNewBarcode: Boolean,
            barcodeData: ISdToteReceivingBarcodeModel,
            actualQty: Int,
            expList: MutableList<ISdToteExpiryDateModel>,
            qrFreshDecodedData: QRFreshDecodeDataModel? = null,
        ): SdToteSubmitUIModel {
            Timber.tag("STATE").i("viewmodel createSubmitBarcodeLD")
            return sdToteReceivingBarcodeUseCase.createSubmitBarcode(
                isNewBarcode = isNewBarcode,
                receivingBarcode = barcodeData,
                actualQty = actualQty,
                expList = expList,
                qrFreshDecodedData = qrFreshDecodedData,
            )
        }

        private fun addNewReceivingBarcodeLD(newBarcode: SdToteDocsDetailBarcodeUIModel) {
            sdToteReceivingBarcodeUseCase.addNewReceivingBarcode(newBarcode)
        }

        private fun updateReceivingBarcodeLD(
            receivingBarcode: SdToteSubmitUIModel,
            expList: List<ISdToteExpiryDateModel>?,
        ) {
            logEventUpdateExistingItem(receivingBarcode)
            sdToteReceivingBarcodeUseCase.updateReceivingBarcode(
                barcode = receivingBarcode.barcode,
                actualQty = receivingBarcode.actualQty,
                expList = expList,
            )
        }

        fun setSubmitBarcodeData(submitBarcodeData: SdToteSubmitUIModel) {
            sdToteReceivingBarcodeUseCase.setToteSubmitUIModel(submitBarcodeData)
        }

        fun updateIsAddLotStateLD(isAddLot: Boolean) {
            sdToteReceivingBarcodeUseCase.updateIsAddLotState(isAddLot)
        }

        fun searchDecodedBarcodeOrch(decodedData: TachyonBarcodeInputFieldUIModel) {
            val isQRFresh = decodedData.data is QRFreshDecodeDataModel
            val existingBarcode = searchBarcodeLD(decodedData.barcode)

            if (existingBarcode != null) {
                createSubmitBarcodeLD(
                    isNewBarcode = false,
                    barcodeData = existingBarcode,
                    actualQty = existingBarcode.actualQty + 1,
                    expList = existingBarcode.expList,
                    qrFreshDecodedData =
                        if (isQRFresh) {
                            decodedData.data as QRFreshDecodeDataModel
                        } else {
                            null
                        },
                ).let { submitData ->
                    updateExistingBarcode(
                        submitData,
                        isQRFresh = isQRFresh,
                    )
                }
            } else {
                getDiscountsConfigByBarcode(decodedData)
            }
        }

        fun selectedBarcodeToEditOrch(barcode: String) {
            searchBarcodeLD(barcode)?.let {
                createSubmitBarcodeLD(
                    isNewBarcode = false,
                    barcodeData = it,
                    actualQty = it.actualQty,
                    expList = it.expList,
                ).apply {
                    this.productName = it.productName
                    this.unitName = it.unitName
                    this.qty = it.qty
                }.let { submitData ->
                    barcodeSelected(submitData)
                }
            }
        }

        fun createNewBarcodeOrch(newBarcode: SdToteDiscountsConfigUIModel) {
            createSubmitBarcodeLD(
                isNewBarcode = true,
                barcodeData = newBarcode,
                actualQty = 1,
                expList = newBarcode.expList
            ).let { newBarcodeData ->
                addNewBarcode(newBarcodeData)
            }
        }

        fun prepareInputExpiryDateOrch(submitData: SdToteSubmitUIModel) {
            setSubmitBarcodeData(submitData)
            resetBaseScreenState()
        }

        fun prepareEditingBarcodeOrch(submitData: SdToteSubmitUIModel) {
            setSubmitBarcodeData(submitData)
            updateIsAddLotStateLD(true)
            resetBaseScreenState()
        }

        fun logEventScanBarcode(
            isQRFresh: Boolean,
            barcode: String,
        ) {
            logUtils.logInfo(
                action = if (isQRFresh) LogAction.SCAN_QR_CODE else LogAction.SCAN_BARCODE,
                message = barcode,
            )
        }

        fun logEventUpdateExistingItem(receivingBarcode: SdToteSubmitUIModel) {
            logUtils.logInfo(
                action = LogAction.UPDATE_EXISTING_ITEM,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_update_item),
                        receivingBarcode,
                    ),
            )
        }

        fun logEventAddNewItem(newBarcode: SdToteSubmitUIModel) {
            logUtils.logInfo(
                action = LogAction.ADD_NEW_ITEM,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_update_item),
                        newBarcode,
                    ),
            )
        }

        fun logEventCheckTote() {
            logUtils.logInfo(
                action = LogAction.CHECK_TOTE,
                message = docsDetailFlowLD.value.toString(),
            )
        }

        fun logEventSelectItem(editingBarcodeData: SdToteSubmitUIModel) {
            logUtils.logInfo(
                action = LogAction.SELECT_TOTE_ORDER_ITEM,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_select_item),
                        editingBarcodeData,
                    ),
            )
        }
    }
