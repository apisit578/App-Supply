package th.co.cjexpress.store.sage.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc.RequestDiscountLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateModel
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.BdDocumentValidateUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentNoDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.ValidateBarcodeForBdUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountItemDetailViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers
import java.util.Date

class RequestDiscountItemDetailViewModelTest : BaseUnitTest() {
    // for setting realLiveData.setEditingProduct(....) ----setValue----
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @RelaxedMockK
    lateinit var requestDiscountLiveData: RequestDiscountLiveData

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    @RelaxedMockK
    lateinit var validateBarcodeForBdUseCase: BdDocumentValidateUseCase

    private lateinit var viewModel: RequestDiscountItemDetailViewModel

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    override fun setUp() {
        super.setUp()
        every { validateBarcodeForBdUseCase.editingProductFlow.value } returns mockk(relaxed = true)
        viewModel =
            RequestDiscountItemDetailViewModel(
                requestDiscountLiveData = requestDiscountLiveData,
                resourceUtils = resourceUtils,
                bdDocumentValidateUseCase = validateBarcodeForBdUseCase,
                uiModelMapper = uiModelMapper,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun test_validateBarcode_success_expect_update_state_success() =
        runTest {
            // given
            val mockBarcode = "1234567890"
            val mockExpiryDate = Date()
            val mockRequestQty = 10
            val mockDiscountType = "DISCOUNT"

            val mockDomainModel = mockk<BdDocumentValidateModel>(relaxed = true)
            val mockUIModel = mockk<ValidateBarcodeForBdUIModel>(relaxed = true)

            val mappingModel =
                BdDocumentValidateMappingModel(
                    code = "SUCCESS",
                    message = "SUCCESS",
                    ref = "123456",
                    timestamp = "11111",
                    data = mockDomainModel,
                )

            coEvery {
                validateBarcodeForBdUseCase.execute(mockDiscountType, mockBarcode, mockExpiryDate)
            } returns flowOf(Result.Success(mappingModel))

            every {
                uiModelMapper.bdDocumentValidateUIMapper.toUIModel(mockDomainModel)
            } returns mockUIModel

            // when
            viewModel.validateBarcode(
                isNewItem = true,
                discountType = mockDiscountType,
                barcode = mockBarcode,
                expiryDate = mockExpiryDate,
                requestQty = mockRequestQty,
            )

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(
                        RequestDiscountItemDetailViewModel.UIState.ValidateBarcodeBdState(
                            mockUIModel,
                        ),
                    ),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_validateBarcode_error_expect_update_state_error() =
        runTest {
            // given
            val mockBarcode = "1234567890"
            val mockExpiryDate = Date()
            val mockRequestQty = 10
            val mockDiscountType = "DISCOUNT"
            coEvery {
                validateBarcodeForBdUseCase.execute(any(), any(), any())
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = resourceUtils.getString(R.string.text_unable_to_continue),
                                type = ErrorType.ERROR,
                                message = resourceUtils.getString(R.string.text_service_failed),
                            ),
                        data = mockk(relaxed = true),
                    ),
                )

            // when
            viewModel.validateBarcode(
                isNewItem = true,
                discountType = mockDiscountType,
                barcode = mockBarcode,
                expiryDate = mockExpiryDate,
                requestQty = mockRequestQty,
            )

            // then
            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals(
                    resourceUtils.getString(R.string.text_unable_to_continue),
                    item.errorData.titleText,
                )
                assertEquals(
                    resourceUtils.getString(R.string.text_service_failed),
                    item.errorData.messageText,
                )
            }
        }

    @Test
    fun test_setInputExpiryDateState_should_update_date_state() =
        runTest {
            // given
            val date = Date()

            // when
            viewModel.setInputExpiryDateState(date)

            // then
            viewModel.inputExpiryDateState.test {
                assertEquals(date, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setInputActualQtyState_should_qty_state() =
        runTest {
            // given
            val qty = 123

            // when
            viewModel.setInputActualQtyState(qty)

            // then
            viewModel.inputActualQtyState.test {
                assertEquals(qty, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getBdDetailLD_expect_correct_return() {
        // given
        val mockData = mockk<BdDocumentNoDetailUIModel>()
        every { requestDiscountLiveData.docsData.value } returns mockData

        // when
        val result = viewModel.getBdDetailLD()

        // then
        assertEquals(mockData, result)
    }

    @Test
    fun test_resetEditingProductLD_should_reset_live_data() {
        // when
        viewModel.resetEditingProductLD()

        // then
        verify { requestDiscountLiveData.setIsNewProduct(false) }
        verify { requestDiscountLiveData.setEditingProduct(null) }
    }

    @Test
    fun test_removeProductFromBdDetailLD_should_update_state() {
        // given
        val mockBarcode = "1234567890"

        // when
        viewModel.removeProductFromBdDetailLD(mockBarcode)

        // then
        verify { requestDiscountLiveData.removeProductFromBdDetail(mockBarcode) }
        assertEquals(true, viewModel.removeProductState)
    }

    @Test
    fun test_resetValidateBarcodeForBdState_should_update_state() =
        runTest {
            // when
            viewModel.resetValidateBarcodeForBdState()

            // then
            viewModel.baseScreenState.test {
                val successItem = awaitItem() as BaseScreenState.Success<*>
                assertEquals(
                    RequestDiscountItemDetailViewModel.UIState.ValidateBarcodeBdState(null),
                    successItem.data,
                )
            }
        }

    @Test
    fun validateBarcode_error_emits_loading_then_error() =
        runTest {
            val messageError = "เนื่องจากการติดต่อ Service ล้มเหลว"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "เนื่องจากการติดต่อ Service ล้มเหลว"
            coEvery { validateBarcodeForBdUseCase.execute(any(), any(), any()) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = messageError,
                            ),
                    ),
                )

            viewModel.validateBarcode(
                isNewItem = true,
                discountType = "DISCOUNT",
                barcode = "123",
                expiryDate = Date(),
                requestQty = 1,
            )
            viewModel.baseScreenState.test {
                val errorItem = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, errorItem.errorData.type)
                assertEquals(messageError, errorItem.errorData.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun getIsNewItemLD_when_value_true_returns_true() {
        // given
        val live = MutableLiveData(true)
        every { requestDiscountLiveData.isNewItem } returns live

        // when
        val result = viewModel.getIsNewItemLD()

        // then
        assertEquals(true, result)
    }

    @Test
    fun getIsNewItemLD_when_value_null_returns_false() {
        // given
        val live = MutableLiveData<Boolean>()
        every { requestDiscountLiveData.isNewItem } returns live

        // when
        val result = viewModel.getIsNewItemLD()

        // then
        assertEquals(false, result)
    }

    @Test
    fun validateBarcode_error_when_cause_message_non_null_uses_direct_message() =
        runTest {
            val directMsg = "barcode invalid"
            val errorType = ErrorType.ERROR

            coEvery { validateBarcodeForBdUseCase.execute(any(), any(), any()) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "CODE",
                                type = errorType,
                                message = directMsg,
                            ),
                    ),
                )

            viewModel =
                RequestDiscountItemDetailViewModel(
                    requestDiscountLiveData,
                    resourceUtils,
                    validateBarcodeForBdUseCase,
                    uiModelMapper,
                    TestDispatchers(),
                )

            viewModel.validateBarcode(
                isNewItem = false,
                discountType = "Y",
                barcode = "123",
                expiryDate = Date(),
                requestQty = 5,
            )

            viewModel.baseScreenState.test {
                var found = false
                while (!found) {
                    when (val item = awaitItem()) {
                        is BaseScreenState.Error -> {
                            assertEquals(errorType, item.errorData.type)
                            assertEquals(directMsg, item.errorData.messageText)
                            found = true
                        }

                        else -> {}
                    }
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `validateBarcode success but editingProduct null emits only loading`() =
        runTest {
            // given
            every { validateBarcodeForBdUseCase.editingProductFlow.value } returns null
            coEvery { validateBarcodeForBdUseCase.execute(any(), any(), any()) } returns
                flowOf(
                    Result.Success(mockk()),
                )

            viewModel =
                RequestDiscountItemDetailViewModel(
                    requestDiscountLiveData,
                    resourceUtils,
                    validateBarcodeForBdUseCase,
                    uiModelMapper,
                    TestDispatchers(),
                )

            // when + then
            viewModel.baseScreenState.test {
                viewModel.validateBarcode(
                    isNewItem = true,
                    discountType = "X",
                    barcode = "000",
                    expiryDate = Date(),
                    requestQty = 1,
                )
                skipItems(2)
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
            verify(exactly = 0) { requestDiscountLiveData.updateDocsProducts(any(), any()) }
        }

    @Test
    fun `getEditingProductLD returns value from liveData`() {
        // given
        val realLiveData = RequestDiscountLiveData()
        val mockProd = mockk<BdDetailProductUIModel>()
        realLiveData.setEditingProduct(mockProd)
        val viewModel =
            RequestDiscountItemDetailViewModel(
                requestDiscountLiveData = realLiveData,
                resourceUtils = resourceUtils,
                bdDocumentValidateUseCase = validateBarcodeForBdUseCase,
                uiModelMapper,
                dispatcherProvider = TestDispatchers(),
            )

        // when
        val result = viewModel.getEditingProductLD()

        // then
        assertEquals(mockProd, result)
    }

    @Test
    fun test_inputStates_should_match_values_from_editingProductFlow() =
        runTest {
            val mockDate = Date()
            val mockQty = 9

            val mockProduct =
                mockk<BdDetailProductUIModel> {
                    every { expiredDate } returns mockDate
                    every { numberOfDiscounted } returns mockQty
                }

            every { validateBarcodeForBdUseCase.editingProductFlow.value } returns mockProduct

            val viewModel =
                RequestDiscountItemDetailViewModel(
                    requestDiscountLiveData,
                    resourceUtils,
                    validateBarcodeForBdUseCase,
                    uiModelMapper,
                    TestDispatchers(),
                )

            assertEquals(mockDate, viewModel.inputExpiryDateState.value)
            assertEquals(mockQty, viewModel.inputActualQtyState.value)
        }

    @Test
    fun test_inputExpiryDateState_should_match_expiredDate() =
        runTest {
            val mockDate = Date()
            val mockProduct =
                mockk<BdDetailProductUIModel> {
                    every { expiredDate } returns mockDate
                    every { numberOfDiscounted } returns 0
                }

            every { validateBarcodeForBdUseCase.editingProductFlow.value } returns mockProduct

            val viewModel =
                RequestDiscountItemDetailViewModel(
                    requestDiscountLiveData,
                    resourceUtils,
                    validateBarcodeForBdUseCase,
                    uiModelMapper,
                    TestDispatchers(),
                )

            assertEquals(mockDate, viewModel.inputExpiryDateState.value)
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
    fun `logEventConfirmEdit should logInfo`() =
        runTest {
            // when
            viewModel.logEventConfirmEdit("1234567890")

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CONFIRM_EDIT_REQUEST_DISCOUNT_ITEM,
                    message = "1234567890",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventDeleteItem should logInfo`() =
        runTest {
            // when
            viewModel.logEventDeleteItem("1234567890")

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.DELETE_REQUEST_DISCOUNT_ITEM,
                    message = "1234567890",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
