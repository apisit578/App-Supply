package th.co.cjexpress.store.sage.presentation.viewmodel.component

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.PromotionType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.BarcodeCodecUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.component.TachyonBarcodeInputFieldUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class TachyonBarcodeInputFieldViewModel
    @Inject
    constructor(
        private val barcodeCodecUseCase: BarcodeCodecUseCase,
        private val uiModelMapper: UiModelMapper,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        private val _inputBarcodeState =
            MutableStateFlow(TextFieldValue(text = "", selection = TextRange(0)))
        val inputBarcodeState: StateFlow<TextFieldValue> = _inputBarcodeState

        fun setInputBarcodeState(value: TextFieldValue) {
            _inputBarcodeState.value = value
        }

        fun clearInputBarcodeState() {
            _inputBarcodeState.value = TextFieldValue(text = "", selection = TextRange(0))
        }

        fun decodeBarcode(
            barcode: String,
            acceptedPrefix: List<PromotionType>? = null,
        ) {
            val result =
                barcodeCodecUseCase.execute(
                    barcode = barcode,
                    acceptedPrefix = acceptedPrefix,
                )

            when (result) {
                is Result.Success -> {
                    setBaseScreenSuccessState(
                        uiModelMapper.barcodeCodecMapper.toBarcodeCodecUIModel(result.data),
                    )
                }

                is Result.Error -> {
                    setBaseScreenErrorState(
                        TachyonAlertDialogUIModel(
                            type = ErrorType.WARNING,
                            titleText =
                                resourceUtils.getString(
                                    R.string.text_title_barcode_mismatch,
                                ),
                            messageText = resourceUtils.getString(R.string.text_please_check_and_scan_again),
                            onPositiveClick = { resetBaseScreenState() },
                        ),
                    )
                }
            }
        }

        fun getDecodeData(barcode: String) = TachyonBarcodeInputFieldUIModel(barcode = barcode, null, null)

        fun setNoneResult(barcode: String) {
            setBaseScreenSuccessState(
                data = TachyonBarcodeInputFieldUIModel(barcode = barcode, null, null),
            )
        }
    }
