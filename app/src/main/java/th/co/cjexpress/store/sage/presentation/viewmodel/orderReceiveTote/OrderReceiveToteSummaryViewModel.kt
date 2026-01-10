package th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote

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
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteApproveModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteApproveUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteReceivingBarcodeUseCase
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteReceivingBarcodeModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteBarcodeEditingSubmitState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OrderReceiveToteSummaryViewModel
    @Inject
    constructor(
        private val sdToteApproveUseCase: SdToteApproveUseCase,
        private val sdToteReceivingBarcodeUseCase: SdToteReceivingBarcodeUseCase,
        private val dispatcherProvider: DispatcherProvider,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        sealed class UIState {
            data object NavToMain : UIState()

            data object NavBack : UIState()

            data object NavToToteDocs : UIState()

            data object NavToToteItemDetail : UIState()
        }

        val docsDetailFlowLD = MutableStateFlow(sdToteReceivingBarcodeUseCase.docsDetailFLowLD.value!!)

        fun sdToteApprove(sdNo: String) {
            sdToteApproveUseCase
                .execute(sdNo)
                .flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            result.data.data?.let { data ->
                                showSdToteApproveDialog(data)
                            } ?: run {
                                val errorDialog =
                                    TachyonAlertDialogUIModel(
                                        type = ErrorType.ERROR,
                                        showF1Message = true,
                                        titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                                        messageText = resourceUtils.getString(R.string.text_service_failed),
                                        onPositiveClick = { resetBaseScreenState() },
                                    )
                                setBaseScreenErrorState(errorDialog)
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
                                            sdToteApprove(sdNo)
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

        private fun showSdToteApproveDialog(data: SdToteApproveModel) {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.SUCCESS,
                    titleText = resourceUtils.getString(R.string.text_confirm_document_success),
                    messageText =
                        buildString {
                            appendLine(
                                resourceUtils.getString(
                                    R.string.text_title_tote_code,
                                    getToteCodeLD(),
                                ),
                            )
                            appendLine(
                                String.format(
                                    resourceUtils.getString(
                                        R.string.text_title_doc_no,
                                        data.sdNo,
                                    ),
                                ),
                            )
                        }.trimEnd(),
                    onPositiveClick = {
                        navToToteDocs()
                    },
                ),
            )
        }

        fun setShowApproveConfirmDialog() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = resourceUtils.getString(R.string.text_confirm_to_save_document),
                    messageText = resourceUtils.getString(R.string.text_confirm_to_approve_sd_tote_document),
                    negativeTextRes = R.string.text_no,
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = {
                        sdToteApprove(getSdToteDocNoLD())
                    },
                ),
            )
        }

        fun validateBeforeApproveLD() {
            val result = sdToteReceivingBarcodeUseCase.getIncompleteItems().isEmpty()
            logEventSubmitSaveDocument(sdToteReceivingBarcodeUseCase.getIncompleteItems())
            handleSdToteValidateBeforeApprove(result)
        }

        fun getSdToteDocNoLD(): String = sdToteReceivingBarcodeUseCase.getSdToteDocNo()

        private fun handleSdToteValidateBeforeApprove(canApprove: Boolean) {
            if (canApprove) {
                resetBaseScreenState()
                setShowApproveConfirmDialog()
            } else {
                setApproveWarningStateMessage()
            }
        }

        fun setApproveWarningStateMessage() {
            setBaseScreenErrorState(
                TachyonAlertDialogUIModel(
                    type = ErrorType.WARNING,
                    titleText = resourceUtils.getString(R.string.text_unable_to_continue),
                    messageText =
                        buildString {
                            appendLine(
                                resourceUtils.getString(
                                    R.string.text_sd_tote_approve_fail_incomplete_items,
                                    getSdToteDocNoLD(),
                                ),
                            )
                        }.trimEnd(),
                    onPositiveClick = {
                        navToToteDocs()
                    },
                ),
            )
        }

        fun getToteCodeLD(): String = sdToteReceivingBarcodeUseCase.getSdToteCode()

        fun resetSdToteDocsDetailLD() {
            sdToteReceivingBarcodeUseCase.resetSdToteDocsDetailUIModel()
        }

        fun searchBarcodeLD(barcode: String): SdToteDocsDetailBarcodeUIModel? = sdToteReceivingBarcodeUseCase.searchBarcode(barcode)

        fun createSubmitBarcodeLD(
            isNewBarcode: Boolean,
            barcodeData: ISdToteReceivingBarcodeModel,
            actualQty: Int,
            expList: MutableList<ISdToteExpiryDateModel>,
        ): SdToteSubmitUIModel {
            Timber.tag("STATE").i("viewmodel createSubmitBarcodeLD")
            return sdToteReceivingBarcodeUseCase.createSubmitBarcode(
                isNewBarcode = isNewBarcode,
                receivingBarcode = barcodeData,
                actualQty = actualQty,
                expList = expList,
            )
        }

        fun barcodeSelected(editingBarcodeData: SdToteSubmitUIModel) {
            Timber.tag("STATE").i("selectBarcodeToEdit editingBarcodeData = $editingBarcodeData")
            logEventSelectItem(editingBarcodeData)
            setBaseScreenSuccessState(SdToteBarcodeEditingSubmitState(data = editingBarcodeData))
        }

        fun setSubmitBarcodeDataLD(submitBarcodeData: SdToteSubmitUIModel) {
            sdToteReceivingBarcodeUseCase.setToteSubmitUIModel(submitBarcodeData)
        }

        fun getIncompleteItems(barcodeList: List<SdToteDocsDetailBarcodeUIModel>): List<SdToteDocsDetailBarcodeUIModel> =
            barcodeList.let { data ->
                val insufficientItems = data.filter { it.actualQty < it.qty }.sortedBy { it.barcode }
                val overItems = data.filter { it.actualQty > it.qty }.sortedBy { it.barcode }
                val items = insufficientItems + overItems
                items
            }

        fun groupBarcodeByExpiryDate(input: List<SdToteDocsDetailBarcodeUIModel>): List<SdToteDocsDetailBarcodeUIModel> =
            sdToteReceivingBarcodeUseCase.groupBarcodeByExpiryDate(input)

        fun updateIsAddLotStateLD(isAddLot: Boolean) {
            sdToteReceivingBarcodeUseCase.updateIsAddLotState(isAddLot)
            resetBaseScreenState()
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

        fun logEventSubmitSaveDocument(listItem: List<SdToteDocsDetailBarcodeUIModel>) {
            logUtils.logInfo(
                action = LogAction.SUBMIT_SAVE_TOTE_DOCS,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_incomplete_item),
                        listItem,
                    ),
            )
        }

        fun navBack() {
            setBaseScreenSuccessState(UIState.NavBack)
        }

        fun navToMainMenu() {
            setBaseScreenSuccessState(UIState.NavToMain)
        }

        fun navToToteDocs() {
            setBaseScreenSuccessState(UIState.NavToToteDocs)
        }

        fun navToToteItemDetail() {
            setBaseScreenSuccessState(UIState.NavToToteItemDetail)
        }
    }
