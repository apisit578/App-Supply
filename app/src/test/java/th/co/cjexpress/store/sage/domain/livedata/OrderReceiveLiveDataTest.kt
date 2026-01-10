package th.co.cjexpress.store.sage.domain.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateListOf
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import th.co.cjexpress.store.sage.domain.livedata.orderReceive.OrderReceiveLiveData
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel
import th.co.cjexpress.store.sage.utils.MainDispatcherRule
import th.co.cjexpress.store.sage.utils.getOrAwaitValue

@ExperimentalCoroutinesApi
class OrderReceiveLiveDataTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var orderReceiveLiveData: OrderReceiveLiveData

    @Before
    fun setUp() {
        orderReceiveLiveData = OrderReceiveLiveData()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_resetShipmentBatchModel_expect_null() =
        runTest {
            // when
            orderReceiveLiveData.resetShipmentBatchModel()

            // then
            assertNull(orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue())
        }

    @Test
    fun test_resetShipmentSummaryUIModel_expect_null() =
        runTest {
            // when
            orderReceiveLiveData.resetShipmentSummaryUIModel()

            // then
            assertNull(orderReceiveLiveData.shipmentSummaryUIModel.getOrAwaitValue())
        }

    @Test
    fun test_setShipmentBatchModel_expect_not_null() =
        runTest {
            // given
            val dataMock = mockk<ShipmentDocumentUIModel>(relaxed = true)

            // when
            orderReceiveLiveData.setShipmentBatchModel(dataMock)

            // then
            assertNotNull(orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue())
        }

    @Test
    fun test_setShipmentSummaryUIModel_expect_not_null() =
        runTest {
            // given
            val dataMock = mockk<ShipmentDocumentUIModel>(relaxed = true)

            // when
            orderReceiveLiveData.setShipmentSummaryUIModel(dataMock)

            // then
            assertNotNull(orderReceiveLiveData.shipmentSummaryUIModel.getOrAwaitValue())
        }

    @Test
    fun test_getShipmentLDNo_expect_return_ldNo() =
        runTest {
            // given
            val dataMock =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(dataMock)

            // when
            val result = orderReceiveLiveData.getShipmentLDNo()

            // then
            assertEquals(result, "LD123456")
        }

    @Test
    fun test_getShipmentLDNo_when_summaryModel_is_null_expect_empty_string() =
        runTest {
            // when
            val result = orderReceiveLiveData.getShipmentLDNo()

            // then
            assertEquals("", result)
        }

    @Test
    fun test_updateShipmentBatch_when_found_barcode_item_in_batch_expect_update() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    actualQty = 10,
                    barcode = "123456789",
                    isTote = false,
                    toteCode = "TOTE123",
                    productName = "Test Product",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val dataMock =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            itemMock,
                        ),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )
            orderReceiveLiveData.setShipmentBatchModel(dataMock)

            // when
            orderReceiveLiveData.updateShipmentBatch("123456789", isIncrease = true)
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.items?.size)
            assertEquals(11, result?.items?.get(0)?.actualQty)
        }

    @Test
    fun test_updateShipmentBatch_when_isTote_true_expect_update() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    actualQty = 1,
                    barcode = "123456789",
                    isTote = true,
                    toteCode = "TOTE123",
                    productName = "Test Product",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val dataMock =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            itemMock,
                        ),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )
            orderReceiveLiveData.setShipmentBatchModel(dataMock)

            // when
            orderReceiveLiveData.updateShipmentBatch("123456789", true)
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.items?.size)
            assertEquals(1, result?.items?.get(0)?.actualQty)
        }

    @Test
    fun test_addShipmentBatchItem_expect_add_new_item() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    actualQty = 10,
                    barcode = "123456789",
                    isTote = false,
                    toteCode = "TOTE123",
                    productName = "Test Product",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val dataMock =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            itemMock,
                        ),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )
            orderReceiveLiveData.setShipmentBatchModel(dataMock)

            // when
            orderReceiveLiveData.addShipmentBatchItem(
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "987654321",
                    isTote = false,
                    toteCode = "TOTE123",
                    productName = "Test Product2",
                    qty = 0,
                    unitName = "pcs",
                    unitFactor = 1,
                ),
            )
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()

            // then
            assertEquals(2, result?.items?.size)
            assertEquals(1, result?.items?.get(0)?.actualQty)
            assertEquals("Test Product2", result?.items?.get(0)?.productName)
            assertEquals(10, result?.items?.get(1)?.actualQty)
            assertEquals("Test Product", result?.items?.get(1)?.productName)
            assertEquals(11, result?.sumOfActualQty)
        }

    @Test
    fun test_addShipmentBatchItem_qty_not_zero_expect_not_add_to_summary() =
        runTest {
            // given
            val summaryModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 0,
                    sumOfActualQty = 0,
                )

            val newItem =
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "000000001",
                    isTote = false,
                    toteCode = "TOTE999",
                    productName = "New Product",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            orderReceiveLiveData.setShipmentBatchModel(summaryModel)

            // when
            orderReceiveLiveData.addShipmentBatchItem(newItem)

            // then
            val summaryResult = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
            assertEquals(1, summaryResult?.items?.size)
        }

    @Test
    fun test_addShipmentBatchItem_when_batchUIModel_is_null_expect_no_crash() =
        runTest {
            // then
            val newItem =
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "000000001",
                    isTote = false,
                    toteCode = "TOTE999",
                    productName = "New Product",
                    qty = 0,
                    unitName = "pcs",
                    unitFactor = 1,
                )

            // when
            orderReceiveLiveData.addShipmentBatchItem(newItem)
        }

    @Test
    fun test_addShipmentBatchItem_qty_not_zero_summary_should_remain_unchanged() =
        runTest {
            // given
            val summaryModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 0,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentBatchModel(summaryModel)
            orderReceiveLiveData.setShipmentSummaryUIModel(summaryModel)

            val newItem =
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "000000001",
                    isTote = false,
                    toteCode = "TOTE999",
                    productName = "New Product",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )

            // when
            orderReceiveLiveData.addShipmentBatchItem(newItem)

            // then
            val summaryResult = orderReceiveLiveData.shipmentSummaryUIModel.getOrAwaitValue()
            assertEquals(0, summaryResult?.items?.size)
        }

    @Test
    fun test_addShipmentBatchItem_when_shipmentBatchUIModel_is_null_expect_no_crash() =
        runTest {
            // given
            val item =
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "000",
                    isTote = false,
                    toteCode = "T0",
                    productName = "NoBatch",
                    qty = 0,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            // when
            orderReceiveLiveData.addShipmentBatchItem(item)
        }

    @Test
    fun test_addShipmentBatchItem_when_summaryUIModel_is_null_expect_no_crash() =
        runTest {
            val batchModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentBatchModel(batchModel)

            val item =
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "000",
                    isTote = false,
                    toteCode = "T0",
                    productName = "NoSummary",
                    qty = 0,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            orderReceiveLiveData.addShipmentBatchItem(item)
        }

    @Test
    fun test_addShipmentBatchItem_when_qty_is_not_zero_should_not_add_to_summary() =
        runTest {
            val summary =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(summary)

            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentBatchModel(batch)

            val item =
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "001",
                    isTote = false,
                    toteCode = "T01",
                    productName = "Test",
                    qty = 10, // <-- NOT zero
                    unitName = "PCS",
                    unitFactor = 1,
                )

            orderReceiveLiveData.addShipmentBatchItem(item)

            val summaryResult = orderReceiveLiveData.shipmentSummaryUIModel.getOrAwaitValue()
            assertTrue(summaryResult?.items?.isEmpty() == true)
        }

    @Test
    fun test_addShipmentBatchItem_with_existing_batch_expect_sumIncreased() =
        runTest {
            // given
            val batchModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 0,
                    sumOfActualQty = 5,
                )
            orderReceiveLiveData.setShipmentBatchModel(batchModel)

            val newItem =
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "ABC123",
                    isTote = false,
                    toteCode = "TOTE1",
                    productName = "Sum Check",
                    qty = 0,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            // when
            orderReceiveLiveData.addShipmentBatchItem(newItem)

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
            assertEquals(6, result?.sumOfActualQty)
            assertEquals("Sum Check", result?.items?.firstOrNull()?.productName)
        }

    @Test
    fun test_addShipmentBatchItem_when_qty_zero_and_both_models_not_null_expect_only_add_to_batch() =
        runTest {
            // given
            val batchModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2024-06-11",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 0,
                )
            val summaryModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2024-06-11",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentBatchModel(batchModel)
            orderReceiveLiveData.setShipmentSummaryUIModel(summaryModel)

            val item =
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "ABC123",
                    isTote = false,
                    toteCode = "TOTE01",
                    productName = "Product",
                    qty = 0,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            // when
            orderReceiveLiveData.addShipmentBatchItem(item)

            // then
            val batchResult = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
            val summaryResult = orderReceiveLiveData.shipmentSummaryUIModel.getOrAwaitValue()

            assertEquals(1, batchResult?.items?.size)
            assertEquals(1, batchResult?.sumOfActualQty)
            assertEquals("Product", batchResult?.items?.first()?.productName)

            assertEquals(0, summaryResult?.items?.size)
            assertEquals(emptyList<ShipmentDataItemUIModel>(), summaryResult?.items)
        }

    @Test
    fun test_searchShipmentData_when_found_data_expect_not_null() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    actualQty = 10,
                    barcode = "123456789",
                    isTote = false,
                    toteCode = "TOTE123",
                    productName = "Test Product",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val dataMock =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            itemMock,
                        ),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(dataMock)

            // when
            val result = orderReceiveLiveData.searchShipmentData("123456789")

            // then
            assertNotNull(result)
        }

    @Test
    fun test_searchShipmentData_when_not_found_data_expect_null() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    actualQty = 10,
                    barcode = "123456789",
                    isTote = false,
                    toteCode = "TOTE123",
                    productName = "Test Product",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val dataMock =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            itemMock,
                        ),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(dataMock)

            // when
            val result = orderReceiveLiveData.searchShipmentData("987")

            // then
            assertNull(result)
        }

    @Test
    fun test_searchShipmentBatchData_when_found_data_expect_not_null() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    actualQty = 10,
                    barcode = "123456789",
                    isTote = false,
                    toteCode = "TOTE123",
                    productName = "Test Product",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val dataMock =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            itemMock,
                        ),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )
            orderReceiveLiveData.setShipmentBatchModel(dataMock)

            // when
            val result = orderReceiveLiveData.searchShipmentBatchData("123456789")

            // then
            assertNotNull(result)
        }

    @Test
    fun test_searchShipmentBatchData_when_not_found_data_expect_null() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    actualQty = 10,
                    barcode = "123456789",
                    isTote = false,
                    toteCode = "TOTE123",
                    productName = "Test Product",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val dataMock =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            itemMock,
                        ),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )
            orderReceiveLiveData.setShipmentBatchModel(dataMock)

            // when
            val result = orderReceiveLiveData.searchShipmentBatchData("987")

            // then
            assertNull(result)
        }

    @Test
    fun test_removeShipmentBatchItem_expect_item_removed_by_barcode() =
        runTest {
            // given
            val mockItem1 =
                ShipmentDataItemUIModel(
                    actualQty = 10,
                    barcode = "123456789",
                    isTote = false,
                    toteCode = "TOTE123",
                    productName = "Test Product 1",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val mockItem2 =
                ShipmentDataItemUIModel(
                    actualQty = 5,
                    barcode = "987654321",
                    isTote = false,
                    toteCode = "TOTE456",
                    productName = "Test Product 2",
                    qty = 5,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val dataMock =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(mockItem1, mockItem2),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 15,
                )
            orderReceiveLiveData.setShipmentBatchModel(dataMock)

            orderReceiveLiveData.removeShipmentBatchItem(mockItem2)

            // when
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.items?.size)
            assertEquals("123456789", result?.items?.first()?.barcode)
            assertEquals(10, result?.sumOfActualQty)
        }

    @Test
    fun test_updateShipmentSummary_with_matching_barcode_should_update_actualQty() =
        runTest {
            // given
            val matchingBarcode = "123456789"

            val summaryItem =
                ShipmentDataItemUIModel(
                    barcode = matchingBarcode,
                    actualQty = 2,
                    isTote = false,
                    toteCode = "T123",
                    productName = "Test Product",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            val summaryUpdateItem =
                ShipmentDataItemUIModel(
                    barcode = matchingBarcode,
                    actualQty = 0,
                    isTote = false,
                    toteCode = "T123",
                    productName = "Test Product",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            val summaryUIModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(summaryItem),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )
            val summaryUpdate =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(summaryUpdateItem),
                    createdDate = "2024-06-06",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )

            orderReceiveLiveData.setShipmentSummaryUIModel(summaryUIModel)

            // when
            orderReceiveLiveData.updateShipmentSummary(summaryUpdate)

            val result = orderReceiveLiveData.shipmentSummaryUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.items?.size)
            assertEquals("123456789", result?.items?.first()?.barcode)
            assertEquals(0, result?.items?.first()?.actualQty)
            assertEquals(10, result?.sumOfActualQty)
        }

    @Test
    fun test_updateShipmentSummary_with_isTote_true_should_not_increase_actualQty() =
        runTest {
            // given
            val matchingBarcode = "987654321"

            val summaryItem =
                ShipmentDataItemUIModel(
                    barcode = matchingBarcode,
                    actualQty = 2,
                    isTote = true,
                    toteCode = "T999",
                    productName = "Tote Box",
                    qty = 1,
                    unitName = "BOX",
                    unitFactor = 1,
                )

            val incomingItem = summaryItem.copy(actualQty = 0)

            val summaryUIModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(summaryItem),
                    createdDate = "2024-01-01",
                    sdNo = "SD999",
                    ldNo = "LD999",
                    count = 1,
                    sumOfActualQty = 5,
                )

            val incomingModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(incomingItem),
                    createdDate = "2024-06-06",
                    sdNo = "SD999",
                    ldNo = "LD999",
                    count = 1,
                    sumOfActualQty = 1,
                )

            orderReceiveLiveData.setShipmentSummaryUIModel(summaryUIModel)

            // when
            orderReceiveLiveData.updateShipmentSummary(incomingModel)

            // then
            val result = orderReceiveLiveData.shipmentSummaryUIModel.getOrAwaitValue()

            assertEquals(1, result?.items?.size)
            assertEquals(0, result?.items?.first()?.actualQty)
            assertEquals(5, result?.sumOfActualQty)
        }

    @Test
    fun test_updateShipmentSummary_with_no_matching_barcode_should_add() =
        runTest {
            // given
            val summaryItem =
                ShipmentDataItemUIModel(
                    barcode = "111",
                    actualQty = 1,
                    isTote = false,
                    toteCode = "T1",
                    productName = "Prod A",
                    qty = 1,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            val summaryUIModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(summaryItem),
                    createdDate = "2023-01-01",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 1,
                )

            val unmatchedIncoming =
                ShipmentDataItemUIModel(
                    barcode = "999",
                    actualQty = 1,
                    isTote = false,
                    toteCode = "T999",
                    productName = "Prod B",
                    qty = 1,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            val incomingModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(unmatchedIncoming),
                    createdDate = "2024-06-06",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 1,
                )

            orderReceiveLiveData.setShipmentSummaryUIModel(summaryUIModel)

            // when
            orderReceiveLiveData.updateShipmentSummary(incomingModel)

            // then
            val result = orderReceiveLiveData.shipmentSummaryUIModel.getOrAwaitValue()
            assertEquals(2, result?.items?.size)
            assertEquals("999", result?.items?.first()?.barcode)
            assertEquals(1, result?.items?.first()?.actualQty)
            assertEquals(1, result?.sumOfActualQty)
        }

    @Test
    fun test_updateShipmentSummary_should_only_update_matched_item_in_list_of_multiple() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    barcode = "111111",
                    actualQty = 1,
                    isTote = false,
                    toteCode = "T1",
                    productName = "Prod A",
                    qty = 1,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            val mockData =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(itemMock),
                    createdDate = "2023-01-01",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 1,
                )

            val itemMock2 =
                ShipmentDataItemUIModel(
                    barcode = "22222",
                    actualQty = 1,
                    isTote = false,
                    toteCode = "T999",
                    productName = "Prod B",
                    qty = 1,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            val mockData2 =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(itemMock2),
                    createdDate = "2024-06-06",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 1,
                )

            orderReceiveLiveData.setShipmentSummaryUIModel(mockData)
            orderReceiveLiveData.updateShipmentSummary(mockData2)

            val result = orderReceiveLiveData.shipmentSummaryUIModel.getOrAwaitValue()
            if (result != null) {
                assertEquals(2, result.items.size)
                assertEquals("22222", result.items[0].barcode)
                assertEquals(1, result.items[0].actualQty)
                assertEquals(1, result.sumOfActualQty)
            }
        }

    @Test
    fun test_updateShipmentSummary_should_move_updated_item_to_top() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    barcode = "22222",
                    actualQty = 1,
                    isTote = false,
                    toteCode = "T999",
                    productName = "Prod B",
                    qty = 1,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            val mockData =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(itemMock),
                    createdDate = "2024-06-06",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 1,
                )

            val itemMock2 =
                ShipmentDataItemUIModel(
                    barcode = "111111",
                    actualQty = 1,
                    isTote = false,
                    toteCode = "T1",
                    productName = "Prod A",
                    qty = 1,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            val mockData2 =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(itemMock2),
                    createdDate = "2023-01-01",
                    sdNo = "SD001",
                    ldNo = "LD001",
                    count = 1,
                    sumOfActualQty = 1,
                )

            orderReceiveLiveData.setShipmentSummaryUIModel(mockData)
            orderReceiveLiveData.updateShipmentSummary(mockData2)

            val result = orderReceiveLiveData.shipmentSummaryUIModel.getOrAwaitValue()
            if (result != null) {
                assertEquals(2, result.items.size)
                assertEquals("111111", result.items[0].barcode)
            }
        }

    @Test
    fun `scan item barcode - returns single request and does not mutate state`() =
        runTest {
            // given
            val shipment1 =
                ShipmentDataItemUIModel(
                    actualQty = 1,
                    barcode = "12345",
                    isTote = false,
                    toteCode = "T123",
                    productName = "Test Product",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                )
            val shipment2 =
                ShipmentDataItemUIModel(
                    actualQty = 1,
                    barcode = "34567",
                    isTote = false,
                    toteCode = "T123",
                    productName = "Test Product",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                )
            val summary =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(shipment1, shipment2),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 2,
                    sumOfActualQty = 2,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(summary)

            // when
            val result = orderReceiveLiveData.setIncreaseShipmentDocumentByBarcode("12345")

            // then
            assertEquals(1, result.size)
            val req = result.single()
            assertEquals("12345", req.barcode)
            assertEquals(2, req.actualQty)
            val item12345 = orderReceiveLiveData.searchShipmentData("12345")
            val item34567 = orderReceiveLiveData.searchShipmentData("34567")
            assertEquals(1, item12345?.actualQty)
            assertEquals(1, item34567?.actualQty)
        }

    @Test
    fun test_removeShipmentBatchModel_expect_items_empty_and_sum_zero() =
        runTest {
            // give
            val shipmentMock =
                ShipmentDataItemUIModel(
                    barcode = "34567",
                    actualQty = 2,
                    isTote = false,
                    toteCode = "T123",
                    productName = "Test Product",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            val shipmentModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(shipmentMock),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )

            orderReceiveLiveData.setShipmentBatchModel(shipmentModel)

            // when
            orderReceiveLiveData.removeShipmentBatchModel()

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
            assertTrue(result?.items?.isEmpty() == true)
            assertEquals(0, result?.sumOfActualQty)
        }

    @Test
    fun test_removeShipmentBatchItem_when_value_is_null_expect_no_crash() =
        runTest {
            val item =
                ShipmentDataItemUIModel(
                    barcode = "999",
                    actualQty = 1,
                    isTote = false,
                    toteCode = "T1",
                    productName = "Dummy",
                    qty = 1,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            orderReceiveLiveData.removeShipmentBatchItem(item)
        }

    @Test
    fun test_removeShipmentBatchModel_expect_model_reset_values() =
        runTest {
            // given
            val shipmentMock =
                ShipmentDataItemUIModel(
                    barcode = "34567",
                    actualQty = 2,
                    isTote = false,
                    toteCode = "T123",
                    productName = "Test Product",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                )
            val shipmentModel =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(shipmentMock),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )

            orderReceiveLiveData.setShipmentBatchModel(shipmentModel)

            // when
            orderReceiveLiveData.removeShipmentBatchModel()

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
            assertEquals(0, result?.items?.size)
            assertEquals(0, result?.sumOfActualQty)
        }

    @Test
    fun test_removeShipmentBatchModel_when_value_is_null_expect_no_crash() =
        runTest {
            orderReceiveLiveData.removeShipmentBatchModel()
        }

    @Test
    fun test_updateShipmentBatch_when_barcode_not_found_expect_no_update() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    actualQty = 5,
                    barcode = "123456",
                    isTote = false,
                    toteCode = "T123",
                    productName = "Test",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val model =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(itemMock),
                    createdDate = "2023-10-01",
                    sdNo = "SD01",
                    ldNo = "LD01",
                    count = 1,
                    sumOfActualQty = 5,
                )
            orderReceiveLiveData.setShipmentBatchModel(model)

            // when
            orderReceiveLiveData.updateShipmentBatch("NOT_FOUND", isIncrease = true)

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
            assertEquals(1, result?.items?.size)
            assertEquals(5, result?.sumOfActualQty)
            assertEquals("123456", result?.items?.first()?.barcode)
        }

    @Test
    fun test_getSdNo_when_summaryModel_is_not_null_expect_return_sdNo() =
        runTest {
            // given
            val dataMock =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD123456",
                    ldNo = "LD123456",
                    count = 1,
                    sumOfActualQty = 10,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(dataMock)

            // when
            val result = orderReceiveLiveData.getSdNo()

            // then
            assertEquals("SD123456", result)
        }

    @Test
    fun test_getSdNo_when_summaryModel_is_null_expect_empty_string() =
        runTest {
            // when
            val result = orderReceiveLiveData.getSdNo()

            // then
            assertEquals("", result)
        }

    @Test
    fun test_getSummaryBatchQty_expect_return_sum_of_actualQty() =
        runTest {
            // given
            val itemMock =
                ShipmentDataItemUIModel(
                    actualQty = 5,
                    barcode = "123456",
                    isTote = false,
                    toteCode = "T123",
                    productName = "Test",
                    qty = 10,
                    unitName = "pcs",
                    unitFactor = 1,
                )
            val model =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(itemMock, itemMock),
                    createdDate = "2023-10-01",
                    sdNo = "SD01",
                    ldNo = "LD01",
                    count = 1,
                    sumOfActualQty = 10,
                )
            orderReceiveLiveData.setShipmentBatchModel(model)

            // when
            val result = orderReceiveLiveData.getSummaryBatchQty()

            // then
            assertEquals(10, result)
        }

    @Test
    fun test_getSummaryBatchQty_when_data_null_expect_return_0() =
        runTest {
            // when
            val result = orderReceiveLiveData.getSummaryBatchQty()

            // then
            assertEquals(0, result)
        }

    @Test
    fun test_setShipmentDocumentItemDetail_expect_value_set() =
        runTest {
            // given
            val item =
                ShipmentDataItemUIModel(
                    actualQty = 3,
                    barcode = "ABC123",
                    isTote = false,
                    toteCode = "T001",
                    productName = "Product A",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            // when
            orderReceiveLiveData.setShipmentDocumentItemDetail(item)

            // then
            val result = orderReceiveLiveData.shipmentDataItemUIModel.getOrAwaitValue()
            assertNotNull(result)
            assertEquals(item, result)
        }

    @Test
    fun test_removeShipmentDocumentItemDetail_expect_null() =
        runTest {
            // given
            val item =
                ShipmentDataItemUIModel(
                    actualQty = 1,
                    barcode = "X001",
                    isTote = false,
                    toteCode = "TT",
                    productName = "P",
                    qty = 1,
                    unitName = "PCS",
                    unitFactor = 1,
                )
            orderReceiveLiveData.setShipmentDocumentItemDetail(item)

            // when
            orderReceiveLiveData.removeShipmentDocumentItemDetail()

            // then
            val result = orderReceiveLiveData.shipmentDataItemUIModel.getOrAwaitValue()
            assertNull(result)
        }

    @Test
    fun test_getShipmentDocumentItemDetail_when_value_set_expect_return_same_item() =
        runTest {
            // given
            val item =
                ShipmentDataItemUIModel(
                    actualQty = 9,
                    barcode = "BAR999",
                    isTote = true,
                    toteCode = "T999",
                    productName = "Big Tote",
                    qty = 9,
                    unitName = "BOX",
                    unitFactor = 2,
                )
            orderReceiveLiveData.setShipmentDocumentItemDetail(item)

            // when
            val result = orderReceiveLiveData.getShipmentDocumentItemDetail()

            // then
            assertEquals(item, result)
        }

    @Test
    fun test_getShipmentDocumentItemDetail_when_value_null_expect_return_default_item() =
        runTest {
            // given (ensure null)
            orderReceiveLiveData.removeShipmentDocumentItemDetail()

            // when
            val result = orderReceiveLiveData.getShipmentDocumentItemDetail()

            // then (default values)
            assertEquals(0, result.actualQty)
            assertEquals("", result.barcode)
            assertEquals(false, result.isTote)
            assertEquals("", result.toteCode)
            assertEquals("", result.productName)
            assertEquals(0, result.qty)
            assertEquals("", result.unitName)
            assertEquals(0, result.unitFactor)
        }

    @Test
    fun `updateShipmentBatchFromReceiveAdjust should update actualQty and sumOfActualQty when not tote`() {
        // Arrange
        val item =
            ShipmentDataItemUIModel(
                actualQty = 2,
                barcode = "123",
                isTote = false,
                toteCode = "",
                productName = "Product A",
                qty = 5,
                unitName = "pcs",
                unitFactor = 1,
            )
        val batch =
            ShipmentDocumentUIModel(
                ldNo = "LD01",
                sdNo = "SD01",
                items = mutableStateListOf(item),
                sumOfActualQty = 2,
                createdDate = "",
                count = 1,
            )
        orderReceiveLiveData.setShipmentBatchModel(batch)

        // Act
        orderReceiveLiveData.updateShipmentBatchFromReceiveAdjust("123", 3)

        // Assert
        val updatedBatch = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
        val updatedItem = updatedBatch!!.items.first()

        assertEquals(updatedItem.actualQty, 5) // 2 + 3
        assertEquals(updatedBatch.sumOfActualQty, 5) // 2 + 3
    }

    @Test
    fun `updateShipmentBatchFromReceiveAdjust should not change qty when item is tote`() {
        // Arrange
        val toteItem =
            ShipmentDataItemUIModel(
                actualQty = 10,
                barcode = "TOTE-001",
                isTote = true,
                toteCode = "T001",
                productName = "Tote Box",
                qty = 0,
                unitName = "box",
                unitFactor = 1,
            )
        val batch =
            ShipmentDocumentUIModel(
                ldNo = "LD01",
                sdNo = "SD01",
                items = mutableStateListOf(toteItem),
                sumOfActualQty = 10,
                createdDate = "",
                count = 1,
            )
        orderReceiveLiveData.setShipmentBatchModel(batch)

        // Act
        orderReceiveLiveData.updateShipmentBatchFromReceiveAdjust("TOTE-001", 5)

        // Assert
        val updatedBatch = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
        val updatedItem = updatedBatch!!.items.first()

        assertEquals(updatedItem.actualQty, 10)
        assertEquals(updatedBatch.sumOfActualQty, 10)
    }

    @Test
    fun `updateShipmentBatchFromReceiveAdjust should do nothing if barcode not found`() {
        // Arrange
        val item =
            ShipmentDataItemUIModel(
                actualQty = 2,
                barcode = "123",
                isTote = false,
                toteCode = "",
                productName = "Product A",
                qty = 5,
                unitName = "pcs",
                unitFactor = 1,
            )
        val batch =
            ShipmentDocumentUIModel(
                ldNo = "LD01",
                sdNo = "SD01",
                items = mutableStateListOf(item),
                sumOfActualQty = 2,
                createdDate = "",
                count = 1,
            )
        orderReceiveLiveData.setShipmentBatchModel(batch)

        // Act
        orderReceiveLiveData.updateShipmentBatchFromReceiveAdjust("999", 5)

        // Assert
        val updatedBatch = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
        assertEquals(updatedBatch!!.sumOfActualQty, 2)
        assertEquals(updatedBatch.items.first().barcode, "123")
    }

    @Test
    fun test_setReduceShipmentDocumentByBarcode_expect_summary_minus_batch() =
        runTest {
            val summaryItem =
                ShipmentDataItemUIModel(
                    actualQty = 5,
                    barcode = "X1",
                    isTote = false,
                    toteCode = "T",
                    productName = "Prod",
                    qty = 10,
                    unitName = "PCS",
                    unitFactor = 1,
                )
            val summary =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(summaryItem),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 5,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(summary)

            val batchItem =
                ShipmentDataItemUIModel(
                    actualQty = 2,
                    barcode = "X1",
                    isTote = false,
                    toteCode = "T",
                    productName = "Prod",
                    qty = 10,
                    unitName = "PCS",
                    unitFactor = 1,
                )
            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(batchItem),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 2,
                )
            orderReceiveLiveData.setShipmentBatchModel(batch)

            // when
            val result = orderReceiveLiveData.setReduceShipmentDocumentByBarcode("X1")

            // then
            assertEquals(1, result.size)
            assertEquals("X1", result.single().barcode)
            assertEquals(3, result.single().actualQty)
        }

    @Test
    fun test_setReduceShipmentDocumentByBarcode_when_not_found_expect_zero() =
        runTest {
            // when
            val result = orderReceiveLiveData.setReduceShipmentDocumentByBarcode("NF")

            // then
            assertEquals(1, result.size)
            assertEquals("NF", result.single().barcode)
            assertEquals(0, result.single().actualQty)
        }

    @Test
    fun test_updateShipmentBatch_when_isIncrease_false_should_remove_and_decrease_sum() =
        runTest {
            // given
            val item1 =
                ShipmentDataItemUIModel(
                    actualQty = 3,
                    barcode = "ABC123",
                    isTote = false,
                    toteCode = "Tote1",
                    productName = "remove item",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                )
            val item2 =
                ShipmentDataItemUIModel(
                    actualQty = 2,
                    barcode = "BCD456",
                    isTote = false,
                    toteCode = "Tote2",
                    productName = "keep item",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                )
            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(item1, item2),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 5,
                )
            orderReceiveLiveData.setShipmentBatchModel(batch)

            // when
            orderReceiveLiveData.updateShipmentBatch("ABC123", isIncrease = false)

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()!!
            assertEquals(1, result.items.size)
            assertEquals("BCD456", result.items.first().barcode)
            assertEquals(2, result.sumOfActualQty)
        }

    @Test
    fun test_addShipmentBatchItem_when_isTote_and_qty_zero_expect_overQty_equals_actual() =
        runTest {
            // given
            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 0,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentBatchModel(batch)

            // when
            orderReceiveLiveData.addShipmentBatchItem(
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "TOTE-0",
                    isTote = true,
                    toteCode = "T0",
                    productName = "ToteZero",
                    qty = 0,
                    unitName = "PCS",
                    unitFactor = 1,
                ),
            )

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()!!
            val item = result.items.first()
            assertEquals(1, item.actualQty)
            assertEquals(1, item.overQty)
            assertEquals(1, result.sumOfActualQty)
        }

    @Test
    fun test_addShipmentBatchItem_when_isTote_and_qty_not_zero_expect_overQty_zero() =
        runTest {
            // given
            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 0,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentBatchModel(batch)

            // when
            orderReceiveLiveData.addShipmentBatchItem(
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "TOTE-5",
                    isTote = true,
                    toteCode = "T5",
                    productName = "ToteFive",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                ),
            )

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()!!
            val item = result.items.first()
            assertEquals(1, item.actualQty)
            assertEquals(0, item.overQty)
            assertEquals(1, result.sumOfActualQty)
        }

    @Test
    fun test_addShipmentBatchItem_when_summary_has_same_barcode_expect_overQty_from_summary() =
        runTest {
            val summaryItem =
                ShipmentDataItemUIModel(
                    actualQty = 2,
                    barcode = "ABC123",
                    isTote = false,
                    toteCode = "",
                    productName = "Ref",
                    qty = 5,
                    unitName = "PCS",
                    unitFactor = 1,
                )
            val summary =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(summaryItem),
                    createdDate = "2023-10-01",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 2,
                )
            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "2023-10-01",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 0,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(summary)
            orderReceiveLiveData.setShipmentBatchModel(batch)

            // when
            orderReceiveLiveData.addShipmentBatchItem(
                ShipmentDataItemUIModel(
                    actualQty = 0,
                    barcode = "ABC123",
                    isTote = false,
                    toteCode = "",
                    productName = "New",
                    qty = 0,
                    unitName = "PCS",
                    unitFactor = 1,
                ),
            )

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()!!
            val item = result.items.first()
            assertEquals(-3, item.overQty)
        }

    @Test
    fun test_setIncreaseShipmentDocumentByBarcode_when_not_found_expect_1() =
        runTest {
            val reqs = orderReceiveLiveData.setIncreaseShipmentDocumentByBarcode("ABC123")
            assertEquals(1, reqs.size)
            assertEquals("ABC123", reqs.single().barcode)
            assertEquals(1, reqs.single().actualQty)
        }

    @Test
    fun test_removeShipmentBatchItem_when_duplicate_barcodes_expect_remove_all_and_update_sum() =
        runTest {
            // given
            val d1 =
                ShipmentDataItemUIModel(2, "DUP_BARCODE", false, "T1", "A", 1, "PCS", 1)
            val d2 =
                ShipmentDataItemUIModel(3, "DUP_BARCODE", false, "T1", "B", 1, "PCS", 1)
            val other =
                ShipmentDataItemUIModel(4, "KEEP_BARCODE", false, "T2", "C", 1, "PCS", 1)

            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(d1, d2, other),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 2 + 3 + 4,
                )
            orderReceiveLiveData.setShipmentBatchModel(batch)

            // when
            orderReceiveLiveData.removeShipmentBatchItem(d1)

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()!!
            assertEquals(1, result.items.size)
            assertEquals("KEEP_BARCODE", result.items.first().barcode)
            assertEquals(4, result.sumOfActualQty)
        }

    @Test
    fun test_addShipmentBatchItem_when_no_summary_match_expect_overQty_equals_actual() =
        runTest {
            val summary =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            ShipmentDataItemUIModel(2, "OTHER", false, "", "Ref", 5, "PCS", 1),
                        ),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 2,
                )
            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 0,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(summary)
            orderReceiveLiveData.setShipmentBatchModel(batch)

            orderReceiveLiveData.addShipmentBatchItem(
                ShipmentDataItemUIModel(0, "NEW", false, "", "New", 0, "PCS", 1),
            )

            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()!!
            val item = result.items.first()
            assertEquals(1, item.actualQty)
            assertEquals(1, item.overQty)
        }

    @Test
    fun test_updateShipmentBatch_when_remove_more_than_sum_should_clamp_to_zero() =
        runTest {
            val item = ShipmentDataItemUIModel(5, "A", false, "", "X", 1, "PCS", 1)
            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(item),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 2,
                )
            orderReceiveLiveData.setShipmentBatchModel(batch)

            orderReceiveLiveData.updateShipmentBatch("A", isIncrease = false)

            val r = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()!!
            assertTrue(r.items.isEmpty())
            assertEquals(0, r.sumOfActualQty)
        }

    @Test
    fun test_setShipmentSummaryUIModel_should_reset_batch_items() =
        runTest {
            val batchBefore =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            ShipmentDataItemUIModel(1, "B1", false, "", "BatchItem", 0, "PCS", 1),
                        ),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 1,
                )
            orderReceiveLiveData.setShipmentBatchModel(batchBefore)

            val summary =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            ShipmentDataItemUIModel(1, "S1", false, "", "SummaryItem", 0, "PCS", 1),
                        ),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 1,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(summary)

            val newBatch = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
            assertTrue(newBatch?.items?.isEmpty() == true)
        }

    @Test
    fun test_setReduceShipmentDocumentByBarcode_when_summary_present_but_no_batch_expect_summary_qty() =
        runTest {
            val summary =
                ShipmentDocumentUIModel(
                    items =
                        mutableStateListOf(
                            ShipmentDataItemUIModel(4, "RED", false, "", "Prod", 10, "PCS", 1),
                        ),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 4,
                )
            orderReceiveLiveData.setShipmentSummaryUIModel(summary)

            val reqs = orderReceiveLiveData.setReduceShipmentDocumentByBarcode("RED")
            val req = reqs.single()
            assertEquals("RED", req.barcode)
            assertEquals(4, req.actualQty)
        }

    @Test
    fun test_searchShipmentData_when_summaryModel_is_null_expect_null() =
        runTest {
            // when
            val result = orderReceiveLiveData.searchShipmentData("123456")

            // then
            assertNull(result)
        }

    @Test
    fun test_searchShipmentBatchData_when_batchModel_is_null_expect_null() =
        runTest {
            // when
            val result = orderReceiveLiveData.searchShipmentBatchData("123456")

            // then
            assertNull(result)
        }

    @Test
    fun test_calculateSummaryOverQty_when_item_not_found_expect_actualQty() =
        runTest {
            // when
            val result = orderReceiveLiveData.calculateSummaryOverQty("123456", 5)

            // then
            assertEquals(5, result)
        }

    @Test
    fun test_updateShipmentBatch_when_barcode_not_found_expect_no_change() =
        runTest {
            // given
            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentBatchModel(batch)

            // when
            orderReceiveLiveData.updateShipmentBatch("NOT_FOUND", isIncrease = true)

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
            assertTrue(result?.items?.isEmpty() == true)
            assertEquals(0, result?.sumOfActualQty)
        }

    @Test
    fun test_removeShipmentBatchItem_when_item_not_found_expect_no_change() =
        runTest {
            // given
            val batch =
                ShipmentDocumentUIModel(
                    items = mutableStateListOf(),
                    createdDate = "",
                    sdNo = "SD",
                    ldNo = "LD",
                    count = 1,
                    sumOfActualQty = 0,
                )
            orderReceiveLiveData.setShipmentBatchModel(batch)

            val item =
                ShipmentDataItemUIModel(
                    actualQty = 1,
                    barcode = "NOT_FOUND",
                    isTote = false,
                    toteCode = "",
                    productName = "Dummy",
                    qty = 1,
                    unitName = "PCS",
                    unitFactor = 1,
                )

            // when
            orderReceiveLiveData.removeShipmentBatchItem(item)

            // then
            val result = orderReceiveLiveData.shipmentBatchUIModel.getOrAwaitValue()
            assertTrue(result?.items?.isEmpty() == true)
            assertEquals(0, result?.sumOfActualQty)
        }
}
