package th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc.RequestDiscountLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.BdDocumentValidateUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentNoDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.ValidateBarcodeForBdUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountItemDetailViewModel.UIState.ValidateBarcodeBdState
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.ResourceUtils
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RequestDiscountItemDetailViewModel
    @Inject
    constructor(
        private val requestDiscountLiveData: RequestDiscountLiveData,
        private val resourceUtils: ResourceUtils,
        private val bdDocumentValidateUseCase: BdDocumentValidateUseCase,
        private val uiModelMapper: UiModelMapper,
        private val dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel() {
        sealed class UIState {
            data class ValidateBarcodeBdState(
                val data: ValidateBarcodeForBdUIModel? = null,
            ) : UIState()
        }

        val editingProductFlow = MutableStateFlow(bdDocumentValidateUseCase.editingProductFlow.value)
        var removeProductState by mutableStateOf(false)

        private val _inputExpiryDateState =
            MutableStateFlow(bdDocumentValidateUseCase.editingProductFlow.value?.expiredDate)
        val inputExpiryDateState: StateFlow<Date?> = _inputExpiryDateState

        private val _inputActualQtyState =
            MutableStateFlow(bdDocumentValidateUseCase.editingProductFlow.value?.numberOfDiscounted)
        val inputActualQtyState: StateFlow<Int?> = _inputActualQtyState

        fun validateBarcode(
            isNewItem: Boolean,
            discountType: String,
            barcode: String,
            expiryDate: Date,
            requestQty: Int,
        ) {
            logEventConfirmEdit(barcode)
            bdDocumentValidateUseCase
                .execute(
                    discountType = discountType,
                    barcode = barcode,
                    expiryDate = expiryDate,
                ).flowOn(dispatcherProvider.io)
                .onStart {
                    setBaseScreenLoadingState()
                }.onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            editingProductFlow.value?.let { product ->
                                val addingItem =
                                    product.copy(
                                        expiredDate = expiryDate,
                                        numberOfDiscounted = requestQty,
                                    )
                                requestDiscountLiveData.updateDocsProducts(isNewItem, addingItem)
                                setBaseScreenSuccessState(
                                    ValidateBarcodeBdState(
                                        result.data.data?.let {
                                            uiModelMapper.bdDocumentValidateUIMapper.toUIModel(
                                                it,
                                            )
                                        },
                                    ),
                                )
                            }
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
                                        if (canRetry) {
                                            validateBarcode(
                                                isNewItem = isNewItem,
                                                discountType = discountType,
                                                barcode = barcode,
                                                expiryDate = expiryDate,
                                                requestQty = requestQty,
                                            )
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

        fun getBdDetailLD(): BdDocumentNoDetailUIModel? = requestDiscountLiveData.docsData.value

        fun resetEditingProductLD() {
            requestDiscountLiveData.setIsNewProduct(false)
            requestDiscountLiveData.setEditingProduct(null)
        }

        fun getEditingProductLD(): BdDetailProductUIModel? = requestDiscountLiveData.getEditingProduct()

        fun getIsNewItemLD(): Boolean = requestDiscountLiveData.isNewItem.value ?: false

        fun removeProductFromBdDetailLD(barcode: String) {
            logEventDeleteItem(barcode)
            requestDiscountLiveData.removeProductFromBdDetail(barcode)
            removeProductState = true
        }

        fun resetValidateBarcodeForBdState() {
            setBaseScreenSuccessState(ValidateBarcodeBdState(null))
        }

        fun setInputExpiryDateState(date: Date) {
            _inputExpiryDateState.value = date
        }

        fun setInputActualQtyState(qty: Int) {
            _inputActualQtyState.value = qty
        }

        fun logEventConfirmEdit(barcode: String) {
            logUtils.logInfo(
                action = LogAction.CONFIRM_EDIT_REQUEST_DISCOUNT_ITEM,
                message = barcode,
            )
        }

        fun logEventDeleteItem(barcode: String) {
            logUtils.logInfo(
                action = LogAction.DELETE_REQUEST_DISCOUNT_ITEM,
                message = barcode,
            )
        }
    }
