package th.co.cjexpress.store.sage.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import th.co.cjexpress.store.sage.domain.model.BarcodeCodecResultModel
import th.co.cjexpress.store.sage.domain.model.QRFreshDecodeDataModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentSubmitModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateModel
import th.co.cjexpress.store.sage.domain.usecase.BarcodeCodecUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.BdDocumentSubmitUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.BdDocumentValidateUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.RequestDiscountLiveDataUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.component.TachyonBarcodeInputFieldUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentNoDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentSubmitUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.ValidateBarcodeForBdUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountItemsViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountItemsViewModel.UIState.BdDocumentSubmitState
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountItemsViewModel.UIState.ValidateBarcodeForBdState
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers
import java.util.Date

class RequestDiscountItemsViewModelTest : BaseUnitTest() {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    @RelaxedMockK
    lateinit var validateBarcodeForBdUseCase: BdDocumentValidateUseCase

    @RelaxedMockK
    lateinit var upsertBdUseCase: BdDocumentSubmitUseCase

    @RelaxedMockK
    lateinit var barcodeCodecUseCase: BarcodeCodecUseCase

    @RelaxedMockK
    lateinit var requestDiscountLiveDataUseCase: RequestDiscountLiveDataUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    private lateinit var viewModel: RequestDiscountItemsViewModel

    lateinit var docsLD: MutableLiveData<BdDocumentNoDetailUIModel>

    override fun setUp() {
        super.setUp()
        MockKAnnotations.init(this)
        docsLD =
            MutableLiveData<BdDocumentNoDetailUIModel>().apply {
                value = mockk(relaxed = true)
            }
        every { requestDiscountLiveDataUseCase.docsData } returns docsLD

        viewModel =
            RequestDiscountItemsViewModel(
                requestDiscountLiveDataUseCase,
                resourceUtils,
                validateBarcodeForBdUseCase,
                upsertBdUseCase,
                barcodeCodecUseCase,
                uiModelMapper,
                TestDispatchers(),
            )
    }

    @Test
    fun test_validateBarcode_error_expect_errorMessage() =
        runTest {
            // given
            every { barcodeCodecUseCase.execute(barcode = any()) } returns
                Result.Error(
                    cause =
                        BusinessException(
                            title = "NETWORK ERROR",
                            message = "ERROR!!!",
                            type = ErrorType.ERROR,
                            errorCode = "400",
                        ),
                )

            // when
            viewModel.validateBarcode("12345")

            // then
            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals(
                    "NETWORK ERROR",
                    item.errorData.titleText,
                )
                assertEquals(
                    "ERROR!!!",
                    item.errorData.messageText,
                )
            }
        }

    @Test
    fun test_setEditingProductLD_should_update_liveData() =
        runTest {
            // given
            val product = mockk<BdDetailProductUIModel>(relaxed = true)

            // when
            viewModel.setEditingProductLD(product)

            // then
            verify {
                requestDiscountLiveDataUseCase.setIsNewProduct(false)
                requestDiscountLiveDataUseCase.setEditingProduct(product)
            }
        }

    @Test
    fun test_resetValidateBarcodeForBdState_should_reset_state() =
        runTest {
            // when
            viewModel.resetValidateBarcodeForBdState()

            // then
            assertEquals(
                ValidateBarcodeForBdState(),
                viewModel.validateBarcodeForBdState.value,
            )
        }

    @Test
    fun test_searchBarcodeLDByDecodeData_when_data_is_QRFreshDecodeDataModel_should_return_barcode() =
        runTest {
            // given
            val model =
                TachyonBarcodeInputFieldUIModel(
                    barcode = "L0|1234567890|2025-12-31",
                    data =
                        QRFreshDecodeDataModel(
                            barcode = "1234567890",
                            lotNo = "L0",
                            expDate = Date(),
                        ),
                    prefix = null,
                )

            // when
            val result = viewModel.searchBarcodeLDByDecodeData(model)

            // then
            assertEquals("1234567890", result)
        }

    @Test
    fun test_searchBarcodeLDByDecodeData_when_data_is_not_QRFreshDecodeDataModel_should_return_barcode() =
        runTest {
            // given
            val model =
                TachyonBarcodeInputFieldUIModel(
                    barcode = "1234567890",
                    data = null,
                    prefix = null,
                )

            // when
            val result = viewModel.searchBarcodeLDByDecodeData(model)

            // then
            assertEquals("1234567890", result)
        }

    @Test
    fun test_resetBdDocumentSubmitState_success() =
        runTest {
            // when
            viewModel.resetBdDocumentSubmitState()

            // then
            assertEquals(BdDocumentSubmitState(), viewModel.upsertBdState.value)
        }

    @Test
    fun test_validateBarcodeForBd_error_with_data_should_set_state_after_retry() =
        runTest {
            val barcode = "123456"
            val bdDetail =
                mockk<BdDocumentNoDetailUIModel>(relaxed = true) {
                    every { discountType } returns mockk { every { name } returns "PROMO" }
                }

            val validateModel = mockk<BdDocumentValidateModel>(relaxed = true)
            val mappingModel =
                BdDocumentValidateMappingModel(
                    code = "ERR",
                    message = "fail",
                    ref = "x",
                    timestamp = "y",
                    data = validateModel,
                )
            val uiModel = mockk<ValidateBarcodeForBdUIModel>(relaxed = true)

            every { requestDiscountLiveDataUseCase.docsData } returns MutableLiveData(bdDetail)
            every { uiModelMapper.bdDocumentValidateUIMapper.toUIModel(validateModel) } returns uiModel

            // make it return error first time, then success on retry
            coEvery {
                validateBarcodeForBdUseCase.execute(any(), any(), any())
            } returnsMany
                listOf(
                    flowOf(
                        Result.Error(
                            data = mappingModel,
                            cause =
                                BusinessException(
                                    errorCode = ErrorCodes.NOT_FOUND,
                                    type = ErrorType.WARNING,
                                    title = "ไม่สามารถดำเนินการต่อได้",
                                    message = "การเชื่อมต่อผิดพลาด",
                                ),
                            canRetry = true,
                        ),
                    ),
                    flowOf(Result.Success(mappingModel)),
                )

            // ACT
            viewModel.validateBarcodeForBd(barcode)

            // simulate pressing retry
            val dialog = viewModel.baseScreenState.value as BaseScreenState.Error
            dialog.errorData.onPositiveClick?.invoke()

            // ASSERT
            viewModel.validateBarcodeForBdState.test {
                assertEquals(ValidateBarcodeForBdState(uiModel), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_upsertBd_success_should_emit_loading_and_success_and_update_upsertBdState() =
        runTest {
            // given
            val documentNumber = "BD123456"
            val upsertBdModel = BdDocumentSubmitModel(documentNumber = documentNumber)
            val upsertBdUIModel = mockk<BdDocumentSubmitUIModel>(relaxed = true)
            val upsertResult =
                BdDocumentSubmitMappingModel(
                    code = "SUCCESS",
                    message = "SUCCESS",
                    ref = "123456",
                    timestamp = "11111",
                    data = upsertBdModel,
                )
            val bdDetailModel = mockk<BdDocumentNoDetailUIModel>(relaxed = true)
            every { bdDetailModel.documentNumber } returns documentNumber
            every { bdDetailModel.discountType } returns RequestDiscountType.NORMAL
            every { bdDetailModel.products } returns mutableStateListOf()
            every { uiModelMapper.bdDocumentSubmitUIMapper.toUIModel(upsertBdModel) } returns upsertBdUIModel

            coEvery { requestDiscountLiveDataUseCase.docsData.value } returns bdDetailModel
            coEvery { upsertBdUseCase.execute(any(), any(), any()) } returns
                flowOf(
                    Result.Success(
                        upsertResult,
                    ),
                )
            viewModel.bdDetailModel.value = bdDetailModel

            // when
            viewModel.bdDocumentSubmit()

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(
                        BdDocumentSubmitState(
                            upsertBdUIModel,
                        ),
                    ),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_removeProduct_calls_remove_and_hide_dialog() =
        runTest {
            // given
            val product =
                BdDetailProductUIModel(
                    barcode = "123",
                    productName = "",
                    expiredDate = null,
                    numberOfDiscounted = null,
                    barFactor = 0,
                    unitFactor = "",
                )

            every { requestDiscountLiveDataUseCase.removeProductFromBdDetail("123") } just Runs

            // when
            viewModel.removeProduct(product)

            // then
            verify { requestDiscountLiveDataUseCase.removeProductFromBdDetail("123") }
            assertNull(viewModel.showRemoveItemDialogState.value)
        }

    @Test
    fun test_showRemoveItemDialog_and_hideRemoveItemDialog_update_state() =
        runTest {
            val product = mockk<BdDetailProductUIModel>()

            viewModel.showRemoveItemDialog(product)
            assertEquals(product, viewModel.showRemoveItemDialogState.value)

            viewModel.hideRemoveItemDialog()
            assertNull(viewModel.showRemoveItemDialogState.value)
        }

    @Test
    fun test_upsertBd() =
        runTest {
            // given
            val upsertResult =
                BdDocumentSubmitMappingModel(
                    code = "SUCCESS",
                    message = "SUCCESS",
                    ref = "123456",
                    timestamp = "11111",
                    data = BdDocumentSubmitModel(documentNumber = "BD123456"),
                )

            val mockBdDocumentNoDetailMappingModel =
                mockk<BdDocumentNoDetailUIModel>(relaxed = true)
            val mockUIModel = mockk<BdDocumentSubmitUIModel>(relaxed = true)

            coEvery { requestDiscountLiveDataUseCase.docsData.value } returns mockBdDocumentNoDetailMappingModel
            coEvery { upsertBdUseCase.execute(any(), any(), any()) } returns
                flowOf(
                    Result.Success(
                        upsertResult,
                    ),
                )
            every {
                uiModelMapper.bdDocumentSubmitUIMapper.toUIModel(
                    match { it.documentNumber == "BD123456" },
                )
            } returns mockUIModel
            viewModel =
                RequestDiscountItemsViewModel(
                    requestDiscountLiveDataUseCase,
                    resourceUtils,
                    validateBarcodeForBdUseCase,
                    upsertBdUseCase,
                    barcodeCodecUseCase,
                    uiModelMapper,
                    TestDispatchers(),
                )

            // when
            viewModel.bdDocumentSubmit()
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(
                        BdDocumentSubmitState(
                            mockUIModel,
                        ),
                    ),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_submitBarcode_error_expect_update_ui_state_error() =
        runTest {
            // given
            val barcode = "123456"
            val mockBdDocumentNoDetailMappingModel =
                mockk<BdDocumentNoDetailUIModel>(relaxed = true)
            coEvery { requestDiscountLiveDataUseCase.docsData.value } returns mockBdDocumentNoDetailMappingModel
            coEvery {
                validateBarcodeForBdUseCase.execute(
                    any(),
                    any(),
                    any(),
                )
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )

            viewModel =
                RequestDiscountItemsViewModel(
                    requestDiscountLiveDataUseCase,
                    resourceUtils,
                    validateBarcodeForBdUseCase,
                    upsertBdUseCase,
                    barcodeCodecUseCase,
                    uiModelMapper,
                    TestDispatchers(),
                )
            // when
            viewModel.validateBarcodeForBd(barcode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_validateBarcodeForBd_when_exception_should_trigger_catch_block() =
        runTest {
            // given
            val message = "Error!!!!!"
            val exception = RuntimeException(message)
            val mockBdDocumentNoDetailMappingModel =
                mockk<BdDocumentNoDetailUIModel>(relaxed = true)

            val liveData =
                MutableLiveData<BdDocumentNoDetailUIModel>().apply {
                    value = mockBdDocumentNoDetailMappingModel
                }

            every { requestDiscountLiveDataUseCase.docsData } returns liveData

            viewModel =
                RequestDiscountItemsViewModel(
                    requestDiscountLiveDataUseCase,
                    resourceUtils,
                    validateBarcodeForBdUseCase,
                    upsertBdUseCase,
                    barcodeCodecUseCase,
                    uiModelMapper,
                    TestDispatchers(),
                )

            coEvery { validateBarcodeForBdUseCase.execute(any(), any(), any()) } answers {
                flow { throw exception }
            }

            // when
            viewModel.validateBarcodeForBd("12345678")

            // then
            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals(
                    resourceUtils.getString(R.string.text_unable_to_continue),
                    item.errorData.titleText,
                )
                assertEquals(exception.message, item.errorData.messageText)
            }
        }

    @Test
    fun test_validateBarcode_when_barcode_is_empty() {
        runTest {
            // given
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_please_scan_barcode) } returns "Please scan barcode"

            // when
            viewModel.validateBarcode("")

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun test_validateBarcode_should_call_validateBarcodeForBd_when_decode_success() {
        runTest {
            // given
            val barcode = "123456"
            val decoded = mockk<BarcodeCodecResultModel>()
            val barcodeAfterMapping = "999888"
            val uiModel = mockk<TachyonBarcodeInputFieldUIModel>()

            every { barcodeCodecUseCase.execute(any(), barcode) } returns Result.Success(decoded)
            every { uiModelMapper.barcodeCodecMapper.toBarcodeCodecUIModel(decoded) } returns uiModel
            every { viewModel.searchBarcodeLDByDecodeData(uiModel) } returns barcodeAfterMapping

            val spiedViewModel = spyk(viewModel, recordPrivateCalls = true)
            every { spiedViewModel["validateBarcodeForBd"](barcodeAfterMapping) } returns Unit

            // when
            spiedViewModel.validateBarcode(barcode)

            // then
            verify { spiedViewModel["validateBarcodeForBd"](barcodeAfterMapping) }
        }
    }

    @Test
    fun test_validateBarcode_on_error() {
        runTest {
            // given
            val barcode = "123456"

            every { barcodeCodecUseCase.execute(any(), barcode) } answers {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.ERROR,
                            // TBC: ข้อความ
                            message = "Failed to parse QR code: อ่านไม่ออก",
                        ),
                )
            }
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"

            // when
            viewModel.validateBarcode(barcode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun editingProductFlow_emits_expected_value_when_LiveData_is_updated() =
        runTest {
            // Arrange
            val expectedProduct =
                BdDetailProductUIModel(
                    barcode = "1234567890",
                    productName = "Sample Product",
                    expiredDate = Date(),
                    numberOfDiscounted = 5,
                    barFactor = 1,
                    unitFactor = "pcs",
                )

            val liveData = MutableLiveData<BdDetailProductUIModel?>()
            every { validateBarcodeForBdUseCase.editingProductFlow } returns liveData

            // Act
            val viewModel =
                RequestDiscountItemsViewModel(
                    requestDiscountLiveDataUseCase = mockk(relaxed = true),
                    resourceUtils = mockk(relaxed = true),
                    bdDocumentValidateUseCase = validateBarcodeForBdUseCase,
                    bdDocumentSubmitUseCase = mockk(relaxed = true),
                    barcodeCodecUseCase = mockk(relaxed = true),
                    uiModelMapper = mockk(relaxed = true),
                    dispatcherProvider = TestDispatchers(),
                )

            // Assert
            viewModel.editingProductFlow.test {
                liveData.value = expectedProduct

                assertEquals(expectedProduct, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_convertValidateBarcodeToEditingProductLD_should_call_setIsNewProduct_and_setEditingProduct() =
        runTest {
            // given
            val validateBarcode =
                BdDocumentValidateModel(
                    barcode = "1234567890",
                    productName = "น้ำปลา",
                    barFactor = 2,
                    unitName = "ขวด",
                )

            val expectedProduct =
                BdDetailProductUIModel(
                    barcode = "1234567890",
                    productName = "น้ำปลา",
                    expiredDate = null,
                    numberOfDiscounted = null,
                    barFactor = 2,
                    unitFactor = "ขวด",
                )

            // when
            viewModel.convertValidateBarcodeToEditingProductLD(validateBarcode)

            // then
            verify(exactly = 1) { requestDiscountLiveDataUseCase.setIsNewProduct(true) }
            verify(exactly = 1) { requestDiscountLiveDataUseCase.setEditingProduct(expectedProduct) }
        }

    @Test
    fun test_validateBarcode_when_error_and_onConfirm_should_call_validateBarcode_again() =
        runTest {
            // given
            val barcode = "123456"
            val errorCause =
                BusinessException(
                    errorCode = "400",
                    title = "title",
                    message = "msg",
                    type = ErrorType.ERROR,
                )
            every { barcodeCodecUseCase.execute(barcode = barcode) } returns Result.Error(cause = errorCause)

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "title"

            viewModel.validateBarcode(barcode)

            viewModel.baseScreenState.test {
                val state = awaitItem() as BaseScreenState.Error
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_validateBarcode_barcodeCodec_error_expect_errorMessage() =
        runTest {
            // given
            every {
                barcodeCodecUseCase.execute(barcode = any())
            } returns
                Result.Error(
                    cause =
                        BusinessException(
                            title = "Barcode codec error",
                            message = "ERROR!!!",
                            type = ErrorType.ERROR,
                            errorCode = "400",
                        ),
                )

            // when
            viewModel.validateBarcode("12345")

            // then
            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals(
                    "Barcode codec error",
                    item.errorData.titleText,
                )
                assertEquals(
                    "ERROR!!!",
                    item.errorData.messageText,
                )
            }
        }

    @Test
    fun test_submitBarcode_success_expect_update_ui_state() =
        runTest {
            // given
            val barcode = "123456"
            val mockBdDocumentNoDetailMappingModel =
                mockk<BdDocumentNoDetailUIModel>(relaxed = true)
            val validateBarcodeForBdModel = mockk<BdDocumentValidateModel>(relaxed = true)
            val validateBarcodeForBdMappingModel =
                BdDocumentValidateMappingModel(
                    code = "SUCCESS",
                    message = "SUCCESS",
                    ref = "123456",
                    timestamp = "11111",
                    data = validateBarcodeForBdModel,
                )
            val validateBarcodeForBdUIModel = mockk<ValidateBarcodeForBdUIModel>(relaxed = true)

            coEvery { requestDiscountLiveDataUseCase.docsData.value } returns mockBdDocumentNoDetailMappingModel

            coEvery {
                validateBarcodeForBdUseCase.execute(
                    any(),
                    any(),
                    any(),
                )
            } returns
                flowOf(Result.Success(validateBarcodeForBdMappingModel))
            every { uiModelMapper.bdDocumentValidateUIMapper.toUIModel(validateBarcodeForBdModel) } returns validateBarcodeForBdUIModel

            viewModel =
                RequestDiscountItemsViewModel(
                    requestDiscountLiveDataUseCase,
                    resourceUtils,
                    validateBarcodeForBdUseCase,
                    upsertBdUseCase,
                    barcodeCodecUseCase,
                    uiModelMapper,
                    TestDispatchers(),
                )
            // when
            viewModel.validateBarcodeForBd(barcode)

            // Then
            viewModel.validateBarcodeForBdState.test {
                assertEquals(
                    ValidateBarcodeForBdState(validateBarcodeForBdUIModel),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun validateBarcodeForBd_when_bdDetailModel_is_ULL_sends_empty_discount_type() =
        runTest {
            viewModel.bdDetailModel.value = null

            val err =
                BusinessException(
                    errorCode = "X",
                    title = null,
                    type = ErrorType.ERROR,
                    message = "",
                )
            coEvery { validateBarcodeForBdUseCase.execute("", "ABC", null) } returns
                flowOf(Result.Error<BdDocumentValidateMappingModel>(cause = err))

            viewModel.validateBarcodeForBd("ABC")
            coVerify(exactly = 1) { validateBarcodeForBdUseCase.execute("", "ABC", null) }
        }

    @Test
    fun validateBarcodeForBd_exception_with_cause_takes_outer_message() =
        runTest {
            val ex = RuntimeException("outer‑msg", RuntimeException("inner‑msg"))
            coEvery { validateBarcodeForBdUseCase.execute(any(), any(), any()) } answers {
                flow { throw ex }
            }

            // act
            viewModel.validateBarcodeForBd("XYZ")

            viewModel.baseScreenState.test {
                val st = awaitItem() as BaseScreenState.Error
                assertEquals(ex.message, st.errorData.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun validateBarcodeForBd_catch_with_no_cause_no_message_falls_back_to_empty_string() =
        runTest {
            // arrange
            val ex = RuntimeException()
            coEvery { validateBarcodeForBdUseCase.execute(any(), any(), any()) } answers {
                flow { throw ex }
            }

            // act
            viewModel.validateBarcodeForBd("NO_MSG")

            // assert
            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("", err.errorData.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun validateBarcodeForBd_catch_with_empty_message_returns_blank() =
        runTest {
            // given
            val ex = RuntimeException()
            coEvery { validateBarcodeForBdUseCase.execute(any(), any(), any()) } answers {
                flow { throw ex }
            }

            // when
            viewModel.validateBarcodeForBd("ABC")

            // then
            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("", err.errorData.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun validateBarcode_warning_should_show_close_button_only() =
        runTest {
            every { barcodeCodecUseCase.execute(barcode = "123") } returns
                Result.Error(
                    cause =
                        BusinessException(
                            type = ErrorType.WARNING,
                            title = "Warn",
                            message = "Something",
                            errorCode = "999",
                        ),
                    canRetry = false,
                )
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Warn"

            viewModel.validateBarcode("123")

            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals(R.string.text_close, err.errorData.positiveTextRes)
                assertNull(err.errorData.negativeTextRes)
                assertFalse(err.errorData.showF1Message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun validateBarcode_erro_rwith_null_message_falls_back_to_blank() =
        runTest {
            every { barcodeCodecUseCase.execute(any(), "XYZ") } returns
                Result.Error(
                    cause =
                        BusinessException(
                            type = ErrorType.WARNING,
                            title = null,
                            message = null,
                            errorCode = "888",
                        ),
                )
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"

            viewModel.validateBarcode("XYZ")

            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("", err.errorData.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun validateBarcodeForBd_catch_with_no_cause_blank_message_returns_empty_string() =
        runTest {
            val ex = RuntimeException()
            coEvery {
                validateBarcodeForBdUseCase.execute(any(), any(), any())
            } answers { flow { throw ex } }

            // Act
            viewModel.validateBarcodeForBd("BRANCH‑C3")

            // Assert
            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("", err.errorData.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `logEventScreenView should logInfo`() =
        runTest {
            // when
            viewModel.logEventScreenView()

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SCREEN_VIEW,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventOnBackPressed should logInfo`() =
        runTest {
            // when
            viewModel.logEventOnBackPressed()

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.ON_BACK_PRESSED,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventScanBarcode should logInfo`() =
        runTest {
            // when
            viewModel.logEventScanBarcode("1111")

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SCAN_BARCODE,
                    message = "1111",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventScanQRCode should logInfo`() =
        runTest {
            // when
            viewModel.logEventScanQRCode("1111")

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SCAN_QR_CODE,
                    message = "1111",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSelectItem should logInfo`() =
        runTest {
            // given
            val mockDocsData = mockk<BdDetailProductUIModel>()
            // when
            viewModel.logEventSelectItem(mockDocsData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SELECT_REQUEST_DISCOUNT_ITEM,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventConfirmRequestDiscount when document exists should log edit action`() =
        runTest {
            // Given
            val bdDetails = mockk<BdDocumentNoDetailUIModel>()
            val existingDoc =
                mockk<BdDocumentNoDetailUIModel> {
                    every { documentNumber } returns "BD123456"
                }
            viewModel.bdDetailModel.value = existingDoc

            // When
            viewModel.logEventConfirmRequestDiscount(bdDetails)

            // Then
            verify {
                logUtils.logInfo(
                    action = LogAction.CONFIRM_EDIT_REQUEST_DISCOUNT_DOC,
                    message = bdDetails.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventConfirmRequestDiscount when document is new should log create action`() =
        runTest {
            // Given
            val bdDetails = mockk<BdDocumentNoDetailUIModel>()
            val newDoc =
                mockk<BdDocumentNoDetailUIModel> {
                    every { documentNumber } returns ""
                }
            viewModel.bdDetailModel.value = newDoc

            // When
            viewModel.logEventConfirmRequestDiscount(bdDetails)

            // Then
            verify {
                logUtils.logInfo(
                    action = LogAction.CONFIRM_CREATE_REQUEST_DISCOUNT_DOC,
                    message = bdDetails.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventConfirmRequestDiscount when bdDetailModel is null should log create action`() =
        runTest {
            // Given
            val bdDetails = mockk<BdDocumentNoDetailUIModel>()
            viewModel.bdDetailModel.value = null

            // When
            viewModel.logEventConfirmRequestDiscount(bdDetails)

            // Then
            verify {
                logUtils.logInfo(
                    action = LogAction.CONFIRM_CREATE_REQUEST_DISCOUNT_DOC,
                    message = bdDetails.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
