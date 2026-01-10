package th.co.cjexpress.store.sage.presentation.viewmodel.component

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.BarcodeCodecUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

class TachyonBarcodeInputFieldViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var barcodeCodecUseCase: BarcodeCodecUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: TachyonBarcodeInputFieldViewModel

    @Before
    fun setup() {
        viewModel =
            TachyonBarcodeInputFieldViewModel(
                barcodeCodecUseCase = barcodeCodecUseCase,
                uiModelMapper = uiModelMapper,
                resourceUtils = resourceUtils,
            )
    }

    @Test
    fun test_setInputBarcodeState_expect_stateUpdated() =
        runTest {
            // given
            val input = TextFieldValue(text = "ABC123", selection = TextRange(6))

            // when
            viewModel.setInputBarcodeState(input)

            // then
            val state = viewModel.inputBarcodeState.value
            assertEquals("ABC123", state.text)
            assertEquals(TextRange(6), state.selection)
        }

    @Test
    fun test_clearInputBarcodeState_expect_emptyTextAndSelectionAtZero() =
        runTest {
            // given
            viewModel.setInputBarcodeState(TextFieldValue(text = "SomeText", selection = TextRange(4)))

            // when
            viewModel.clearInputBarcodeState()

            // then
            val state = viewModel.inputBarcodeState.value
            assertEquals("", state.text)
            assertEquals(TextRange(0), state.selection)
        }

    @Test
    fun test_getDecodeData_expect_correct_UIModel() {
        // given
        val testBarcode = "9876543210987"

        // when
        val result = viewModel.getDecodeData(testBarcode)

        // then
        assertNotNull(result)
        assert(result.barcode == testBarcode)
        assertNull(result.data)
        assertNull(result.prefix)
    }

    @Test
    fun test_decodeBarcode_error_should_trigger_resetBaseScreenState_onPositiveClick() =
        runTest {
            // given
            val error =
                BusinessException(
                    message = "Error",
                    errorCode = "123",
                    type = ErrorType.ERROR,
                )
            every { barcodeCodecUseCase.execute(barcode = any()) } returns Result.Error(cause = error)
            every { resourceUtils.getString(any()) } returns "Fake String"

            // spy viewModel เพื่อจับการ call ภายใน
            val viewModel =
                spyk(
                    TachyonBarcodeInputFieldViewModel(
                        barcodeCodecUseCase = barcodeCodecUseCase,
                        uiModelMapper = uiModelMapper,
                        resourceUtils = resourceUtils,
                    ),
                )

            // จับ argument ที่ส่งเข้าไปใน setBaseScreenErrorState()
            val slot = slot<TachyonAlertDialogUIModel>()
            every { viewModel["setBaseScreenErrorState"](capture(slot)) } answers {}
            every { viewModel["resetBaseScreenState"]() } answers {}

            // when
            viewModel.decodeBarcode("1234567890123")

            // then
            val capturedDialog = slot.captured
            capturedDialog.onPositiveClick?.invoke()

            verify { viewModel["resetBaseScreenState"]() }
        }
}
