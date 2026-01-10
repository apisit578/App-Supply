package th.co.cjexpress.store.sage.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.OrderReceiveToteDiscountType
import th.co.cjexpress.store.sage.domain.model.QRFreshDecodeDataModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItem
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteDiscountsConfigUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteReceivingBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitAndGetLatestBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.component.TachyonBarcodeInputFieldUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteReceivingBarcodeModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDiscountsConfigUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteBarcodeEditingSubmitState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteInputExpiryDateSubmitState
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteItemsViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.DateUtils.toYYYYMMDDDate
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers
import java.util.Date

class OrderReceiveToteItemsViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var sdToteSubmitUseCase: SdToteSubmitUseCase

    @RelaxedMockK
    lateinit var sdToteSubmitAndGetLatestBarcodeUseCase: SdToteSubmitAndGetLatestBarcodeUseCase

    @RelaxedMockK
    lateinit var sdToteDiscountsConfigUseCase: SdToteDiscountsConfigUseCase

    @RelaxedMockK
    lateinit var sdToteReceivingBarcodeUseCase: SdToteReceivingBarcodeUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: OrderReceiveToteItemsViewModel

    override fun setUp() {
        super.setUp()
        every { sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.value } returns mockk(relaxed = true)
        every { sdToteReceivingBarcodeUseCase.docsDetailFLowLD.value } returns mockk(relaxed = true)
        viewModel =
            OrderReceiveToteItemsViewModel(
                sdToteSubmitUseCase = sdToteSubmitUseCase,
                sdToteSubmitAndGetLatestBarcodeUseCase = sdToteSubmitAndGetLatestBarcodeUseCase,
                sdToteDiscountsConfigUseCase = sdToteDiscountsConfigUseCase,
                sdToteReceivingBarcodeUseCase = sdToteReceivingBarcodeUseCase,
                uiModelMapper = uiModelMapper,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun test_addNewBarcode_when_discountType_is_FRESHLIFE_expect_update_ui_state_success() =
        runTest {
            // given
            val mockData =
                mockk<SdToteSubmitUIModel> {
                    every { discountType } returns OrderReceiveToteDiscountType.FRESHLIFE.name
                    every { expList } returns mutableStateListOf()
                }

            // when
            viewModel.addNewBarcode(mockData)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(SdToteInputExpiryDateSubmitState(mockData)),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_addNewBarcode_when_discountType_is_FRESHLIFE_and_expList_is_not_empty_expect_update_ui_state_success() =
        runTest {
            // given
            val mockData =
                mockk<SdToteSubmitUIModel> {
                    every { sdNo } returns ""
                    every { toteCode } returns ""
                    every { receivedDate } returns Date()
                    every { barcode } returns ""
                    every { skuCode } returns ""
                    every { actualQty } returns 1
                    every { discountType } returns OrderReceiveToteDiscountType.FRESHLIFE.name
                    every { expList } returns mutableStateListOf(mockk())
                }

            // when
            viewModel.addNewBarcode(mockData)

            // then
            verify {
                sdToteSubmitAndGetLatestBarcodeUseCase.execute(any(), any(), any(), any(), any(), any(), any())
            }
        }

    @Test
    fun test_addNewBarcode_when_discountType_is_NORMAL_then_call_sdToteSubmitAndGetLatestBarcodeUseCase() =
        runTest {
            // given
            val mockData =
                mockk<SdToteSubmitUIModel> {
                    every { sdNo } returns ""
                    every { toteCode } returns ""
                    every { receivedDate } returns Date()
                    every { barcode } returns ""
                    every { skuCode } returns ""
                    every { actualQty } returns 1
                    every { expList } returns SnapshotStateList()
                    every { discountType } returns OrderReceiveToteDiscountType.NORMAL.name
                }
            val mockResponse = mockk<SdToteDetailItem>(relaxed = true)
            val mockUIModel = mockk<SdToteDocsDetailBarcodeUIModel>()

            coEvery {
                sdToteSubmitAndGetLatestBarcodeUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
                flowOf(
                    Result.Success(mockk(relaxed = true)),
                )

            every { uiModelMapper.sdToteDocDetailUIMapper.toUIModel(mockResponse) } returns mockUIModel

            // when
            viewModel.addNewBarcode(mockData)

            // then
            verify { sdToteReceivingBarcodeUseCase.addNewReceivingBarcode(any()) }
        }

    @Test
    fun test_updateExistingBarcode_when_discountType_is_FRESHLIFE_expect_update_ui_state_success() =
        runTest {
            // given
            val mockData =
                mockk<SdToteSubmitUIModel> {
                    every { discountType } returns OrderReceiveToteDiscountType.FRESHLIFE.name
                }

            // when
            viewModel.updateExistingBarcode(mockData, false)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(SdToteInputExpiryDateSubmitState(mockData)),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_updateExistingBarcode_when_discountType_is_FRESHLIFE_and_isQRFresh_expect_update_ui_state_success() =
        runTest {
            // given
            val mockData =
                mockk<SdToteSubmitUIModel> {
                    every { sdNo } returns ""
                    every { toteCode } returns ""
                    every { receivedDate } returns Date()
                    every { barcode } returns ""
                    every { skuCode } returns ""
                    every { actualQty } returns 1
                    every { expList } returns SnapshotStateList()
                    every { discountType } returns OrderReceiveToteDiscountType.FRESHLIFE.name
                }

            coEvery {
                sdToteSubmitUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
                flowOf(
                    Result.Success(mockk(relaxed = true)),
                )

            // when
            viewModel.updateExistingBarcode(mockData, true)

            // then
            verify {
                sdToteSubmitUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            }
        }

    @Test
    fun test_updateExistingBarcode_when_discountType_is_NORMAL_then_call_sdToteSubmitUseCase() =
        runTest {
            // given
            val mockData =
                mockk<SdToteSubmitUIModel> {
                    every { sdNo } returns ""
                    every { toteCode } returns ""
                    every { receivedDate } returns Date()
                    every { barcode } returns ""
                    every { skuCode } returns ""
                    every { actualQty } returns 1
                    every { expList } returns SnapshotStateList()
                    every { discountType } returns OrderReceiveToteDiscountType.NORMAL.name
                }

            coEvery {
                sdToteSubmitUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
                flowOf(
                    Result.Success(mockk(relaxed = true)),
                )

            // when
            viewModel.updateExistingBarcode(mockData, false)

            // then
            verify {
                sdToteSubmitUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            }
        }

    @Test
    fun test_getDiscountsConfigByBarcode_success_expect_update_ui_state_success() =
        runTest {
            // given
            val fakeDecodeModel =
                TachyonBarcodeInputFieldUIModel(
                    barcode = "1234567890123",
                    data =
                        QRFreshDecodeDataModel(
                            lotNo = "LOT123",
                            barcode = "1234567890123",
                            expDate = "20241231".toYYYYMMDDDate(),
                        ),
                    prefix = "L",
                )
            val mockResponse =
                mockk<SdToteDiscountsConfigModel> {
                    every { barcode } returns ""
                    every { discountType } returns ""
                    every { noOfExp } returns 1
                }
            val mockUIModel =
                SdToteDiscountsConfigUIModel(
                    barcode = "",
                    discountType = "",
                    noOfExp = 1,
                    skuName = "",
                    unitName = "",
                    skuCode = "",
                )
            coEvery {
                sdToteDiscountsConfigUseCase.execute(any())
            } returns
                flowOf(
                    Result.Success(mockResponse),
                )
            every { uiModelMapper.sdToteDiscountsConfigUIMapper.toUIModel(mockResponse) } returns mockUIModel

            // when
            viewModel.getDiscountsConfigByBarcode(fakeDecodeModel)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(mockUIModel),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getDiscountsConfigByBarcode_error_expect_update_ui_state_error() =
        runTest {
            // given
            val fakeDecodeModel =
                TachyonBarcodeInputFieldUIModel(
                    barcode = "1234567890123",
                    data =
                        QRFreshDecodeDataModel(
                            lotNo = "LOT123",
                            barcode = "1234567890123",
                            expDate = "20241231".toYYYYMMDDDate(),
                        ),
                    prefix = "L",
                )
            coEvery {
                sdToteDiscountsConfigUseCase.execute(any())
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

            // when
            viewModel.getDiscountsConfigByBarcode(fakeDecodeModel)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getDiscountsConfigByBarcode_with_decodedData_is_not_QrFresh() = runTest {
        // given
        val fakeDecodeModel =
            TachyonBarcodeInputFieldUIModel(
                barcode = "1234567890123",
                data = "1234",
                prefix = "L",
            )
        val mockResponse =
            mockk<SdToteDiscountsConfigModel> {
                every { barcode } returns ""
                every { discountType } returns ""
                every { noOfExp } returns 1
            }
        val mockUIModel =
            SdToteDiscountsConfigUIModel(
                barcode = "",
                discountType = "",
                noOfExp = 1,
                skuName = "",
                unitName = "",
                skuCode = "",
            )
        coEvery {
            sdToteDiscountsConfigUseCase.execute(any())
        } returns
                flowOf(
                    Result.Success(mockResponse),
                )
        every { uiModelMapper.sdToteDiscountsConfigUIMapper.toUIModel(mockResponse) } returns mockUIModel

        // when
        viewModel.getDiscountsConfigByBarcode(fakeDecodeModel)

        // then
        viewModel.baseScreenState.test {
            val item = awaitItem()
            assertEquals(
                BaseScreenState.Success(mockUIModel),
                item,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun test_getDiscountsConfigByBarcode_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            val fakeDecodeModel =
                TachyonBarcodeInputFieldUIModel(
                    barcode = "1234567890123",
                    data =
                        QRFreshDecodeDataModel(
                            lotNo = "LOT123",
                            barcode = "1234567890123",
                            expDate = "20241231".toYYYYMMDDDate(),
                        ),
                    prefix = "L",
                )
            coEvery {
                sdToteDiscountsConfigUseCase.execute(any())
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

            // when
            viewModel.getDiscountsConfigByBarcode(fakeDecodeModel)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_createSubmitBarcodeLD_expect_correct_return_from_usecase() =
        runTest {
            // given
            val barcodeModel = mockk<ISdToteReceivingBarcodeModel>()
            val expList = mutableListOf<ISdToteExpiryDateModel>(mockk())
            val expected = mockk<SdToteSubmitUIModel>()

            every {
                sdToteReceivingBarcodeUseCase.createSubmitBarcode(true, barcodeModel, 1, expList)
            } returns expected

            // when
            val result = viewModel.createSubmitBarcodeLD(true, barcodeModel, 1, expList)

            // then
            assertEquals(expected, result)
        }

    @Test
    fun test_searchBarcodeLD_expect_correct_return_from_usecase() =
        runTest {
            // given
            val expected = mockk<SdToteDocsDetailBarcodeUIModel>()
            every { sdToteReceivingBarcodeUseCase.searchBarcode("123") } returns expected

            // when
            val result = viewModel.searchBarcodeLD("123")

            // then
            assertEquals(expected, result)
        }

    @Test
    fun test_barcodeSelected_success_expect_update_ui_state_error() =
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
            viewModel.setSubmitBarcodeData(data)

            // then
            verify { sdToteReceivingBarcodeUseCase.setToteSubmitUIModel(data) }
        }

    @Test
    fun test_searchBarcodeLDByDecodeData_when_searchBarcodeByLotNo_not_null_expect_return_not_null() {
        // given
        val fakeDecodeModel =
            TachyonBarcodeInputFieldUIModel(
                barcode = "1234567890123",
                data =
                    QRFreshDecodeDataModel(
                        lotNo = "LOT123",
                        barcode = "1234567890123",
                        expDate = "20241231".toYYYYMMDDDate(),
                    ),
                prefix = "L",
            )
        every {
            sdToteReceivingBarcodeUseCase.searchBarcodeByLotNo(
                barcode = "1234567890123",
                lotNo = "LOT123",
                expiryDate = "20241231".toYYYYMMDDDate(),
            )
        } returns mockk()

        // when
        val result = viewModel.searchBarcodeLDByDecodeData(fakeDecodeModel)

        // then
        assertNotNull(result)
    }

    @Test
    fun test_searchBarcodeLDByDecodeData_with_data_type_string_expect_should_call_searchBarcodeLD() {
        // given
        val fakeDecodeModel =
            TachyonBarcodeInputFieldUIModel(
                barcode = "1234567890123",
                data = "123.30",
                prefix = "A0",
            )
        every {
            sdToteReceivingBarcodeUseCase.searchBarcodeByLotNo(
                barcode = "1234567890123",
                lotNo = "LOT123",
                expiryDate = "20241231".toYYYYMMDDDate(),
            )
        } returns mockk()

        // when
        viewModel.searchBarcodeLDByDecodeData(fakeDecodeModel)

        // then
        verify { viewModel.searchBarcodeLD("1234567890123") }
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
    fun `logEventScanBarcode should logInfo for QR code`() =
        runTest {
            // given
            val barcode = "L11111|12345|20250101"

            // when
            viewModel.logEventScanBarcode(isQRFresh = true, barcode = barcode)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SCAN_QR_CODE,
                    message = barcode,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventScanBarcode should logInfo for regular barcode`() =
        runTest {
            // given
            val barcode = "BARCODE12345"

            // when
            viewModel.logEventScanBarcode(isQRFresh = false, barcode = barcode)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SCAN_BARCODE,
                    message = barcode,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventUpdateExistingItem should logInfo`() =
        runTest {
            // given
            val mockData = mockk<SdToteSubmitUIModel>()
            every { resourceUtils.getString(R.string.log_message_update_item) } returns "Update item: %s"

            // when
            viewModel.logEventUpdateExistingItem(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.UPDATE_EXISTING_ITEM,
                    message = "Update item: $mockData",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventAddNewItem should logInfo`() =
        runTest {
            // given
            val mockData = mockk<SdToteSubmitUIModel>()
            every { resourceUtils.getString(R.string.log_message_update_item) } returns "Add new item: %s"

            // when
            viewModel.logEventAddNewItem(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.ADD_NEW_ITEM,
                    message = "Add new item: $mockData",
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
    fun `logEventCheckTote should logInfo`() =
        runTest {
            // when
            viewModel.logEventCheckTote()

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CHECK_TOTE,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `updateExistingBarcode - submitBarcode error with canRetry true then onPositiveClick retries`() =
        runTest {
            // given
            val submitData =
                mockk<SdToteSubmitUIModel> {
                    every { sdNo } returns ""
                    every { toteCode } returns ""
                    every { receivedDate } returns Date()
                    every { barcode } returns "BC1"
                    every { skuCode } returns "SKU1"
                    every { actualQty } returns 1
                    every { expList } returns SnapshotStateList()
                    every { discountType } returns OrderReceiveToteDiscountType.NORMAL.name
                }

            val err =
                Result.Error(
                    data = null,
                    cause =
                        BusinessException(
                            errorCode = "UNKNOWN",
                            type = ErrorType.ERROR,
                            message = null,
                        ),
                    canRetry = true,
                )

            coEvery {
                sdToteSubmitUseCase.execute(any(), any(), any(), any(), any(), any(), any())
            } returnsMany
                listOf(
                    flowOf(err),
                    flowOf(Result.Success(mockk(relaxed = true))),
                )

            // when
            viewModel.updateExistingBarcode(submitData, isQRFresh = false)

            // then
            viewModel.baseScreenState.test {
                val error = awaitItem()
                assert(error is BaseScreenState.Error)
                val dialog = (error as BaseScreenState.Error).errorData

                assertEquals(R.string.text_retry, dialog.positiveTextRes)
                assertEquals(R.string.text_close, dialog.negativeTextRes)

                // click retry
                dialog.onPositiveClick?.invoke()

                verify(exactly = 2) {
                    sdToteSubmitUseCase.execute(any(), any(), any(), any(), any(), any(), any())
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `updateExistingBarcode - submitBarcode error with canRetry false then onPositiveClick only closes`() =
        runTest {
            // given
            val submitData =
                mockk<SdToteSubmitUIModel> {
                    every { sdNo } returns ""
                    every { toteCode } returns ""
                    every { receivedDate } returns Date()
                    every { barcode } returns "BC1"
                    every { skuCode } returns "SKU1"
                    every { actualQty } returns 1
                    every { expList } returns SnapshotStateList()
                    every { discountType } returns OrderReceiveToteDiscountType.NORMAL.name
                }

            val err =
                Result.Error(
                    data = null,
                    cause =
                        BusinessException(
                            errorCode = "UNKNOWN",
                            type = ErrorType.ERROR,
                            message = null,
                        ),
                    canRetry = false,
                )

            coEvery {
                sdToteSubmitUseCase.execute(any(), any(), any(), any(), any(), any(), any())
            } returns flowOf(err)

            // when
            viewModel.updateExistingBarcode(submitData, isQRFresh = false)

            // then
            viewModel.baseScreenState.test {
                val error = awaitItem()
                assert(error is BaseScreenState.Error)
                val dialog = (error as BaseScreenState.Error).errorData

                assertEquals(R.string.text_close, dialog.positiveTextRes)
                assertEquals(null, dialog.negativeTextRes)

                dialog.onPositiveClick?.invoke()

                verify(exactly = 1) {
                    sdToteSubmitUseCase.execute(any(), any(), any(), any(), any(), any(), any())
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `addNewBarcode - submitAndGetLatestBarcode error with canRetry true then onPositiveClick retries`() =
        runTest {
            // given
            val submitData =
                mockk<SdToteSubmitUIModel> {
                    every { sdNo } returns ""
                    every { toteCode } returns ""
                    every { receivedDate } returns Date()
                    every { barcode } returns "BC2"
                    every { skuCode } returns "SKU2"
                    every { actualQty } returns 1
                    every { expList } returns SnapshotStateList()
                    every { discountType } returns OrderReceiveToteDiscountType.NORMAL.name
                }

            val err =
                Result.Error(
                    data = null,
                    cause =
                        BusinessException(
                            errorCode = "UNKNOWN",
                            type = ErrorType.ERROR,
                            message = null,
                        ),
                    canRetry = true,
                )

            coEvery {
                sdToteSubmitAndGetLatestBarcodeUseCase.execute(any(), any(), any(), any(), any(), any(), any())
            } returnsMany
                listOf(
                    flowOf(err),
                    flowOf(Result.Success(mockk(relaxed = true))),
                )

            // when
            viewModel.addNewBarcode(submitData)

            // then
            viewModel.baseScreenState.test {
                val error = awaitItem()
                assert(error is BaseScreenState.Error)
                val dialog = (error as BaseScreenState.Error).errorData

                assertEquals(R.string.text_retry, dialog.positiveTextRes)
                assertEquals(R.string.text_close, dialog.negativeTextRes)

                dialog.onPositiveClick?.invoke()

                verify(exactly = 2) {
                    sdToteSubmitAndGetLatestBarcodeUseCase.execute(any(), any(), any(), any(), any(), any(), any())
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getDiscountsConfigByBarcode - NOT_FOUND should use specific title and message`() =
        runTest {
            // given
            val fakeDecodeModel =
                TachyonBarcodeInputFieldUIModel(
                    barcode = "1234567890123",
                    data =
                        QRFreshDecodeDataModel(
                            lotNo = "LOT123",
                            barcode = "1234567890123",
                            expDate = "20241231".toYYYYMMDDDate(),
                        ),
                    prefix = "L",
                )
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_barcode_or_qr_mismatch) } returns "Barcode mismatch"

            val err =
                Result.Error(
                    data = null,
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.ERROR,
                            title = null,
                            message = null,
                        ),
                    canRetry = false,
                )

            coEvery { sdToteDiscountsConfigUseCase.execute(any()) } returns flowOf(err)

            // when
            viewModel.getDiscountsConfigByBarcode(fakeDecodeModel)

            // then
            viewModel.baseScreenState.test {
                val error = awaitItem()
                assert(error is BaseScreenState.Error)
                val dialog = (error as BaseScreenState.Error).errorData
                assertEquals("Unable", dialog.titleText)
                assertEquals("Barcode mismatch", dialog.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getDiscountsConfigByBarcode - error canRetry true then onPositiveClick retries and success`() =
        runTest {
            // given
            val fakeDecodeModel =
                TachyonBarcodeInputFieldUIModel(
                    barcode = "1234567890123",
                    data =
                        QRFreshDecodeDataModel(
                            lotNo = "LOT123",
                            barcode = "1234567890123",
                            expDate = "20241231".toYYYYMMDDDate(),
                        ),
                    prefix = "L",
                )
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            val err =
                Result.Error(
                    data = null,
                    cause =
                        BusinessException(
                            errorCode = "ANY",
                            type = ErrorType.ERROR,
                            title = null,
                            message = null,
                        ),
                    canRetry = true,
                )

            val model = mockk<SdToteDiscountsConfigModel>(relaxed = true)
            val uiModel = mockk<SdToteDiscountsConfigUIModel>(relaxed = true)
            every { uiModelMapper.sdToteDiscountsConfigUIMapper.toUIModel(model) } returns uiModel

            coEvery { sdToteDiscountsConfigUseCase.execute(any()) } returnsMany
                listOf(
                    flowOf(err),
                    flowOf(Result.Success(model)),
                )

            // when
            viewModel.getDiscountsConfigByBarcode(fakeDecodeModel)

            // then
            viewModel.baseScreenState.test {
                val error = awaitItem()
                val dialog = (error as BaseScreenState.Error).errorData

                dialog.onPositiveClick?.invoke()

                var success: BaseScreenState.Success<*>? = null
                repeat(3) {
                    val next = awaitItem()
                    if (next is BaseScreenState.Success<*>) {
                        success = next
                        return@repeat
                    }
                }
                assertNotNull(success)
                verify(exactly = 2) { sdToteDiscountsConfigUseCase.execute(any()) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getDiscountsConfigByBarcode - upstream throws exception then catch produces error dialog with message`() =
        runTest {
            // given
            val fakeDecodeModel =
                TachyonBarcodeInputFieldUIModel(
                    barcode = "1234567890123",
                    data =
                        QRFreshDecodeDataModel(
                            lotNo = "LOT123",
                            barcode = "1234567890123",
                            expDate = "20241231".toYYYYMMDDDate(),
                        ),
                    prefix = "L",
                )
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            val boom = RuntimeException("Boom!")

            coEvery { sdToteDiscountsConfigUseCase.execute(any()) } returns
                kotlinx.coroutines.flow.flow { throw boom }

            // when
            viewModel.getDiscountsConfigByBarcode(fakeDecodeModel)

            // then
            viewModel.baseScreenState.test {
                val error = awaitItem()
                assert(error is BaseScreenState.Error)
                val dialog = (error as BaseScreenState.Error).errorData
                assertEquals("Unable", dialog.titleText)
                assertEquals("Boom!", dialog.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `searchDecodedBarcodeOrch when existing barcode AND isQRFresh should createSubmit`() =
        runTest {
            val spyVM = spyk(viewModel)

            // given
            val existing =
                mockk<SdToteDocsDetailBarcodeUIModel> {
                    every { actualQty } returns 2
                    every { expList } returns SnapshotStateList()
                }
            every { spyVM.searchBarcodeLD("123") } returns existing

            val decoded =
                TachyonBarcodeInputFieldUIModel(
                    barcode = "123",
                    data = QRFreshDecodeDataModel(lotNo = "LOT", barcode = "123", expDate = Date()),
                    prefix = "L",
                )

            val submit = mockk<SdToteSubmitUIModel>()
            every {
                spyVM.createSubmitBarcodeLD(
                    isNewBarcode = false,
                    barcodeData = existing,
                    actualQty = 3,
                    expList = existing.expList,
                    qrFreshDecodedData = decoded.data as QRFreshDecodeDataModel,
                )
            } returns submit

            every { spyVM.updateExistingBarcode(submit, true) } returns Unit

            // when
            spyVM.searchDecodedBarcodeOrch(decoded)

            // then
            verify {
                spyVM.createSubmitBarcodeLD(false, existing, 3, existing.expList, decoded.data as QRFreshDecodeDataModel)
                spyVM.updateExistingBarcode(submit, true)
            }
        }

    @Test
    fun `searchDecodedBarcodeOrch when not found should fetch discounts config by barcode`() =
        runTest {
            val spyVM = spyk(viewModel)

            // given
            every { spyVM.searchBarcodeLD("999") } returns null
            every { spyVM.getDiscountsConfigByBarcode(any()) } returns Unit

            val decoded =
                TachyonBarcodeInputFieldUIModel(
                    barcode = "999",
                    data = "NOT_QR_FRESH",
                    prefix = "A0",
                )

            // when
            spyVM.searchDecodedBarcodeOrch(decoded)

            // then
            verify { spyVM.getDiscountsConfigByBarcode(any()) }
            verify(exactly = 0) { spyVM.updateExistingBarcode(any(), any()) }
        }

    @Test
    fun `selectedBarcodeToEditOrch should createSubmit and then barcodeSelected`() =
        runTest {
            val spyVM = spyk(viewModel)

            val src =
                mockk<SdToteDocsDetailBarcodeUIModel> {
                    every { actualQty } returns 5
                    every { expList } returns SnapshotStateList()
                    every { productName } returns "PN"
                    every { unitName } returns "U"
                    every { qty } returns 10
                }
            every { spyVM.searchBarcodeLD("ABC") } returns src

            val submit = mockk<SdToteSubmitUIModel>(relaxed = true)
            every {
                spyVM.createSubmitBarcodeLD(
                    isNewBarcode = false,
                    barcodeData = src,
                    actualQty = 5,
                    expList = src.expList,
                )
            } returns submit

            every { spyVM.barcodeSelected(submit) } returns Unit

            // when
            spyVM.selectedBarcodeToEditOrch("ABC")

            // then
            verify {
                spyVM.createSubmitBarcodeLD(false, src, 5, src.expList)
                submit.productName = "PN"
                submit.unitName = "U"
                submit.qty = 10
                spyVM.barcodeSelected(submit)
            }
        }

    @Test
    fun `createNewBarcodeOrch should createSubmit with isNew true qty 1 and addNewBarcode`() =
        runTest {
            val spyVM = spyk(viewModel)

            val cfg = mockk<SdToteDiscountsConfigUIModel>()
            val submit = mockk<SdToteSubmitUIModel>()
            every { cfg.expList } returns mutableStateListOf()
            every {
                spyVM.createSubmitBarcodeLD(
                    isNewBarcode = true,
                    barcodeData = cfg,
                    actualQty = 1,
                    expList = any<MutableList<ISdToteExpiryDateModel>>(),
                )
            } returns submit
            every { spyVM.addNewBarcode(submit) } returns Unit

            // when
            spyVM.createNewBarcodeOrch(cfg)

            // then
            verify {
                spyVM.createSubmitBarcodeLD(true, cfg, 1, any<MutableList<ISdToteExpiryDateModel>>())
                spyVM.addNewBarcode(submit)
            }
        }

    @Test
    fun `prepareInputExpiryDateOrch should setSubmitBarcodeData and resetBaseScreenState`() =
        runTest {
            val spyVM = spyk(viewModel)

            val submit = mockk<SdToteSubmitUIModel>()
            every { spyVM.setSubmitBarcodeData(submit) } returns Unit
            every { spyVM.resetBaseScreenState() } returns Unit

            // when
            spyVM.prepareInputExpiryDateOrch(submit)

            // then
            verify {
                spyVM.setSubmitBarcodeData(submit)
                spyVM.resetBaseScreenState()
            }
        }

    @Test
    fun `prepareEditingBarcodeOrch should setSubmitBarcodeData turn on addLot and resetBaseScreenState`() =
        runTest {
            val spyVM = spyk(viewModel)

            val submit = mockk<SdToteSubmitUIModel>()
            every { spyVM.setSubmitBarcodeData(submit) } returns Unit
            every { spyVM.updateIsAddLotStateLD(true) } returns Unit
            every { spyVM.resetBaseScreenState() } returns Unit

            // when
            spyVM.prepareEditingBarcodeOrch(submit)

            // then
            verify {
                spyVM.setSubmitBarcodeData(submit)
                spyVM.updateIsAddLotStateLD(true)
                spyVM.resetBaseScreenState()
            }
        }

    @Test
    fun `buildDiscountsConfigErrorDialog falls back to default title and message when title and message are null`() {
        // given
        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
        every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

        val err =
            Result.Error(
                data = null,
                cause =
                    BusinessException(
                        errorCode = "ANY",
                        type = ErrorType.WARNING,
                        title = null,
                        message = null,
                    ),
                canRetry = false,
            )

        // when
        val dialog =
            viewModel.buildDiscountsConfigErrorDialog(
                error = err,
                onRetry = {},
                onClose = {},
                str = resourceUtils::getString,
            )

        // then
        assertEquals("Unable", dialog.titleText)
        assertEquals("Service failed", dialog.messageText)
    }

    @Test
    fun `buildDiscountsConfigErrorDialog sets showF1Message true when cause type is ERROR`() {
        // given
        val err =
            Result.Error(
                data = null,
                cause =
                    BusinessException(
                        errorCode = "X",
                        type = ErrorType.ERROR,
                        title = "T",
                        message = "M",
                    ),
                canRetry = false,
            )
        every { resourceUtils.getString(any()) } returns "fallback"

        // when
        val dialog =
            viewModel.buildDiscountsConfigErrorDialog(
                error = err,
                onRetry = {},
                onClose = {},
                str = resourceUtils::getString,
            )

        // then
        assertTrue(dialog.showF1Message)
    }

    @Test
    fun `buildDiscountsConfigErrorDialog invokes onRetry when canRetry is true and positive clicked`() {
        // given
        var retried = false
        var closed = false

        val err =
            Result.Error(
                data = null,
                cause =
                    BusinessException(
                        errorCode = "Y",
                        type = ErrorType.ERROR,
                        title = "T",
                        message = "M",
                    ),
                canRetry = true,
            )
        every { resourceUtils.getString(any()) } returns "fallback"

        // when
        val dialog =
            viewModel.buildDiscountsConfigErrorDialog(
                error = err,
                onRetry = { retried = true },
                onClose = { closed = true },
                str = resourceUtils::getString,
            )

        dialog.onPositiveClick?.invoke()

        // then
        assertTrue(closed)
        assertTrue(retried)
        assertEquals(R.string.text_retry, dialog.positiveTextRes)
        assertEquals(R.string.text_close, dialog.negativeTextRes)
    }

    @Test
    fun `buildDiscountsConfigErrorDialog does NOT invoke onRetry when canRetry is false and positive clicked`() {
        var retried = false
        var closed = false

        every { resourceUtils.getString(any()) } returns "fallback"

        val err =
            Result.Error(
                data = null,
                cause =
                    BusinessException(
                        errorCode = "Z",
                        type = ErrorType.ERROR,
                        title = "T",
                        message = "M",
                    ),
                canRetry = false,
            )

        val dialog =
            viewModel.buildDiscountsConfigErrorDialog(
                error = err,
                onRetry = { retried = true },
                onClose = { closed = true },
                str = resourceUtils::getString,
            )

        assertNotNull(dialog.onPositiveClick)
        dialog.onPositiveClick!!.invoke()

        assertTrue(closed)
        assertFalse(retried)
        assertEquals(R.string.text_close, dialog.positiveTextRes)
        assertNull(dialog.negativeTextRes)
    }

    @Test
    fun `searchDecodedBarcodeOrch with QRFresh decodeData should pass decodedData to createSubmit`() {
        val spyVM = spyk(viewModel)

        // given
        val existing =
            mockk<SdToteDocsDetailBarcodeUIModel> {
                every { actualQty } returns 5
                every { expList } returns SnapshotStateList()
            }
        every { spyVM.searchBarcodeLD("111") } returns existing

        val qrFresh = QRFreshDecodeDataModel(lotNo = "LOT1", barcode = "111", expDate = Date())
        val decoded =
            TachyonBarcodeInputFieldUIModel(
                barcode = "111",
                data = qrFresh,
                prefix = "L",
            )

        val submit = mockk<SdToteSubmitUIModel>()
        every {
            spyVM.createSubmitBarcodeLD(
                isNewBarcode = false,
                barcodeData = existing,
                actualQty = 6,
                expList = existing.expList,
                qrFreshDecodedData = qrFresh,
            )
        } returns submit
        every { spyVM.updateExistingBarcode(submit, true) } returns Unit

        // when
        spyVM.searchDecodedBarcodeOrch(decoded)

        // then → branch isQRFresh = true
        verify {
            spyVM.createSubmitBarcodeLD(false, existing, 6, existing.expList, qrFresh)
            spyVM.updateExistingBarcode(submit, true)
        }
    }

    @Test
    fun `searchDecodedBarcodeOrch with non QRFresh decodeData should pass null qrFreshDecodedData`() {
        val spyVM = spyk(viewModel)

        // given
        val existing =
            mockk<SdToteDocsDetailBarcodeUIModel> {
                every { actualQty } returns 1
                every { expList } returns SnapshotStateList()
            }
        every { spyVM.searchBarcodeLD("222") } returns existing

        val decoded =
            TachyonBarcodeInputFieldUIModel(
                barcode = "222",
                data = "STRING_DATA_NOT_QRFRESH",
                prefix = "A0",
            )

        val submit = mockk<SdToteSubmitUIModel>()
        every {
            spyVM.createSubmitBarcodeLD(
                isNewBarcode = false,
                barcodeData = existing,
                actualQty = 2,
                expList = existing.expList,
                qrFreshDecodedData = null,
            )
        } returns submit
        every { spyVM.updateExistingBarcode(submit, false) } returns Unit

        // when
        spyVM.searchDecodedBarcodeOrch(decoded)

        // then → branch isQRFresh = false
        verify {
            spyVM.createSubmitBarcodeLD(false, existing, 2, existing.expList, null)
            spyVM.updateExistingBarcode(submit, false)
        }
    }

    @Test
    fun `selectedBarcodeToEditOrch when searchBarcodeLD returns null should do nothing`() =
        runTest {
            val spyVM = spyk(viewModel)

            // given
            every { spyVM.searchBarcodeLD("NOPE") } returns null

            // when
            spyVM.selectedBarcodeToEditOrch("NOPE")

            // then
            verify(exactly = 1) { spyVM.searchBarcodeLD("NOPE") }

            verify(exactly = 0) {
                spyVM.createSubmitBarcodeLD(
                    isNewBarcode = false,
                    barcodeData = any(),
                    actualQty = any(),
                    expList = any(),
                )
            }
            verify(exactly = 0) { spyVM.barcodeSelected(any()) }
        }
}
