package th.co.cjexpress.store.sage.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteApproveUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteReceivingBarcodeUseCase
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteReceivingBarcodeModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteBarcodeEditingSubmitState
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteSummaryViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class OrderReceiveToteSummaryViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var sdToteApproveUseCase: SdToteApproveUseCase

    @RelaxedMockK
    lateinit var sdToteReceivingBarcodeUseCase: SdToteReceivingBarcodeUseCase

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: OrderReceiveToteSummaryViewModel

    override fun setUp() {
        super.setUp()
        every { sdToteReceivingBarcodeUseCase.docsDetailFLowLD.value } returns mockk(relaxed = true)
        mockLogUtils()
        viewModel =
            OrderReceiveToteSummaryViewModel(
                sdToteApproveUseCase = sdToteApproveUseCase,
                sdToteReceivingBarcodeUseCase = sdToteReceivingBarcodeUseCase,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun test_sdToteApprove_error_expect_update_ui_state_error() =
        runTest {
            // given
            coEvery { sdToteApproveUseCase.execute("SD002") } returns
                flowOf(
                    Result.Error(
                        cause =
                            mockk {
                                every { type } returns ErrorType.ERROR
                                every { message } returns "Network Error"
                            },
                    ),
                )

            // when
            viewModel.sdToteApprove("SD002")

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getSdToteDocNoLD_should_return_expected() =
        runTest {
            // given
            every { sdToteReceivingBarcodeUseCase.getSdToteDocNo() } returns "DOC123"

            // when
            val result = viewModel.getSdToteDocNoLD()

            // then
            assertEquals("DOC123", result)
        }

    @Test
    fun test_getToteCodeLD_should_return_expected() =
        runTest {
            // given
            every { sdToteReceivingBarcodeUseCase.getSdToteCode() } returns "TOTE123"

            // then
            assertEquals("TOTE123", viewModel.getToteCodeLD())
        }

    @Test
    fun test_resetSdToteDocsDetailLD_should_call_usecase() =
        runTest {
            // when
            viewModel.resetSdToteDocsDetailLD()

            // then
            verify { sdToteReceivingBarcodeUseCase.resetSdToteDocsDetailUIModel() }
        }

    @Test
    fun test_searchBarcodeLD_should_return_expected() =
        runTest {
            // given
            val mockData = mockk<SdToteDocsDetailBarcodeUIModel>()
            every { sdToteReceivingBarcodeUseCase.searchBarcode("123") } returns mockData

            // then
            assertEquals(mockData, viewModel.searchBarcodeLD("123"))
        }

    @Test
    fun test_createSubmitBarcodeLD_should_return_expected() =
        runTest {
            // given
            val barcodeModel = mockk<ISdToteReceivingBarcodeModel>()
            val expList = mutableListOf<ISdToteExpiryDateModel>(mockk())
            val expected = mockk<SdToteSubmitUIModel>()
            every {
                sdToteReceivingBarcodeUseCase.createSubmitBarcode(true, barcodeModel, 1, expList)
            } returns expected

            // then
            assertEquals(expected, viewModel.createSubmitBarcodeLD(true, barcodeModel, 1, expList))
        }

    @Test
    fun test_barcodeSelected_should_update_ui_success_state() =
        runTest {
            // given
            val data = mockk<SdToteSubmitUIModel>()

            // when
            viewModel.barcodeSelected(data)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(SdToteBarcodeEditingSubmitState(data)),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setSubmitBarcodeData_should_call_setToteSubmitUIModel() =
        runTest {
            // given
            val data = mockk<SdToteSubmitUIModel>()

            // when
            viewModel.setSubmitBarcodeDataLD(data)

            // then
            verify { sdToteReceivingBarcodeUseCase.setToteSubmitUIModel(data) }
        }

    @Test
    fun test_getIncompleteItems_should_return_expected_result() =
        runTest {
            // given
            val item1 =
                mockk<SdToteDocsDetailBarcodeUIModel> {
                    every { actualQty } returns 1
                    every { qty } returns 2
                    every { barcode } returns "B1"
                    every { expList } returns mutableStateListOf()
                }
            val item2 =
                mockk<SdToteDocsDetailBarcodeUIModel> {
                    every { actualQty } returns 3
                    every { qty } returns 2
                    every { barcode } returns "B2"
                    every { expList } returns mutableStateListOf()
                }

            // when
            val result = viewModel.getIncompleteItems(listOf(item1, item2))

            // then
            assertEquals(listOf(item1, item2), result)
        }

    @Test
    fun test_groupBarcodeByExpiryDate_should_return_expected_result() =
        runTest {
            val input = listOf<SdToteDocsDetailBarcodeUIModel>(mockk(), mockk())
            val expectedResult = listOf<SdToteDocsDetailBarcodeUIModel>(mockk())

            every { sdToteReceivingBarcodeUseCase.groupBarcodeByExpiryDate(input) } returns expectedResult

            val result = viewModel.groupBarcodeByExpiryDate(input)

            assertEquals(expectedResult, result)
            verify(exactly = 1) { sdToteReceivingBarcodeUseCase.groupBarcodeByExpiryDate(input) }
        }

    @Test
    fun test_updateIsAddLotStateLD_should_call_updateIsAddLotState() =
        runTest {
            // given
            val mockData = true

            // when
            viewModel.updateIsAddLotStateLD(mockData)

            // then
            verify { sdToteReceivingBarcodeUseCase.updateIsAddLotState(mockData) }
        }

    @Test
    fun `logEventSelectItem should logInfo with correct action and message`() =
        runTest {
            // given
            val mockData = mockk<SdToteSubmitUIModel>()
            every { resourceUtils.getString(R.string.log_message_select_item) } returns "Select item: %s"

            // when
            viewModel.logEventSelectItem(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SELECT_TOTE_ORDER_ITEM,
                    message = String.format("Select item: %s", mockData),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSubmitSaveDocument should logInfo with correct action and message`() =
        runTest {
            // given
            val mockList = listOf(mockk<SdToteDocsDetailBarcodeUIModel>(), mockk())
            every { resourceUtils.getString(R.string.log_message_incomplete_item) } returns "Incomplete items: %s"

            // when
            viewModel.logEventSubmitSaveDocument(mockList)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SUBMIT_SAVE_TOTE_DOCS,
                    message = String.format("Incomplete items: %s", mockList),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
