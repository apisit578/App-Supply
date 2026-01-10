package th.co.cjexpress.store.sage.domain.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateListOf
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceDetailLiveData
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.utils.MainDispatcherRule
import th.co.cjexpress.store.sage.utils.getOrAwaitValue

class PurchaseInvoiceDetailLiveDataTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var purchaseInvoiceDetailLiveData: PurchaseInvoiceDetailLiveData

    @Before
    fun setUp() {
        purchaseInvoiceDetailLiveData = PurchaseInvoiceDetailLiveData()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_setPurchaseInvoiceDetail_expect_not_null() =
        runTest {
            // given
            val dataMock = mockk<PurchaseInvoiceDetailUIModel>(relaxed = true)

            // when
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(dataMock)

            // then
            assertNotNull(purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue())
        }

    @Test
    fun test_resetPurchaseInvoiceDetail_expect_null() =
        runTest {
            // when
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()

            // then
            assertNull(purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue())
        }

    @Test
    fun test_setPurchaseInvoiceItem_expect_not_null() =
        runTest {
            // given
            val dataMock = mockk<PurchaseInvoiceEntryUIModel>(relaxed = true)

            // when
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceItem(dataMock)

            // then
            assertNotNull(purchaseInvoiceDetailLiveData.purchaseInvoiceItemUIModel.getOrAwaitValue())
        }

    @Test
    fun test_resetPurchaseInvoiceItem_expect_null() =
        runTest {
            // when
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceItem()

            // then
            assertNull(purchaseInvoiceDetailLiveData.purchaseInvoiceItemUIModel.getOrAwaitValue())
        }

    @Test
    fun test_getPurchaseInvoiceOrder_expect_return_item() =
        runTest {
            // given
            val dataMock =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "1234567890",
                                actualQty = 0,
                                productName = "test product",
                                unitName = "แพค",
                                unitFactor = 1,
                                qty = 10,
                                salePrice = 0.0,
                            ),
                        ),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(dataMock)

            // when
            val result = purchaseInvoiceDetailLiveData.getPurchaseInvoiceOrder("1234567890")

            // then
            assertEquals(result?.barcode, "1234567890")
        }

    @Test
    fun test_getPurchaseInvoiceOrder_when_data_null_expect_return_null() =
        runTest {
            // given
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()

            // when
            val result = purchaseInvoiceDetailLiveData.getPurchaseInvoiceOrder("1234567890")

            // then
            assertEquals(result, null)
        }

    @Test
    fun test_getPurchaseInvoiceOrder_when_barcode_not_found_expect_return_item() =
        runTest {
            // given
            val dataMock =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "1234567890",
                                actualQty = 0,
                                productName = "test product",
                                unitName = "แพค",
                                unitFactor = 1,
                                qty = 10,
                                salePrice = 0.0,
                            ),
                        ),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(dataMock)

            // when
            val result = purchaseInvoiceDetailLiveData.getPurchaseInvoiceOrder("111111")

            // then
            assertEquals(result, null)
        }

    @Test
    fun test_updatePurchaseInvoiceProduct_expect_update_item() =
        runTest {
            // given
            val itemMock =
                PurchaseInvoiceEntryUIModel(
                    barcode = "1234567890",
                    actualQty = 2,
                    productName = "test product",
                    unitName = "แพค",
                    unitFactor = 1,
                    qty = 10,
                    salePrice = 0.0,
                )
            val dataMock =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "1234567890",
                                actualQty = 1,
                                productName = "test product",
                                unitName = "แพค",
                                unitFactor = 1,
                                qty = 10,
                                salePrice = 0.0,
                            ),
                            PurchaseInvoiceEntryUIModel(
                                barcode = "1111111",
                                actualQty = 1,
                                productName = "test product2",
                                unitName = "แพค",
                                unitFactor = 1,
                                qty = 10,
                                salePrice = 0.0,
                            ),
                        ),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(dataMock)

            // when
            purchaseInvoiceDetailLiveData.updatePurchaseInvoiceProduct(listOf(itemMock))
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()

            // then
            assertEquals(2, result?.entries?.size)
            assertEquals(2, result?.entries?.get(0)?.actualQty)
        }

    @Test
    fun test_updatePurchaseInvoiceProduct_when_data_null_expect_not_update_item() =
        runTest {
            // given
            val itemMock =
                PurchaseInvoiceEntryUIModel(
                    barcode = "54321",
                    actualQty = 1,
                    productName = "test product",
                    unitName = "แพค",
                    unitFactor = 1,
                    qty = 10,
                    salePrice = 0.0,
                )
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()

            // when
            purchaseInvoiceDetailLiveData.updatePurchaseInvoiceProduct(listOf(itemMock))
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()

            // then
            assertEquals(null, result)
        }

    @Test
    fun test_updatePurchaseInvoiceProduct_when_item_null_expect_not_update_item() =
        runTest {
            // given
            val itemMock =
                PurchaseInvoiceEntryUIModel(
                    barcode = "54321",
                    actualQty = 6,
                    productName = "test product",
                    unitName = "แพค",
                    unitFactor = 1,
                    qty = 10,
                    salePrice = 0.0,
                )
            val dataMock =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "1234567890",
                                actualQty = 5,
                                productName = "test product",
                                unitName = "แพค",
                                unitFactor = 1,
                                qty = 10,
                                salePrice = 0.0,
                            ),
                        ),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(dataMock)

            // when
            purchaseInvoiceDetailLiveData.updatePurchaseInvoiceProduct(listOf(itemMock))
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.entries?.size)
            assertEquals(5, result?.entries?.get(0)?.actualQty)
        }

    @Test
    fun test_updatePurchaseInvoiceItemActualQty_updates_actualQty_in_itemUIModel() =
        runTest {
            // given
            val initialItem =
                PurchaseInvoiceEntryUIModel(
                    barcode = "ABC",
                    actualQty = 1,
                    productName = "P",
                    unitName = "U",
                    unitFactor = 1,
                    qty = 5,
                    salePrice = 10.0,
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceItem(initialItem)

            // when
            purchaseInvoiceDetailLiveData.updatePurchaseInvoiceItemActualQty(7)
            val result = purchaseInvoiceDetailLiveData.purchaseInvoiceItemUIModel.getOrAwaitValue()

            // then
            assertEquals(7, result?.actualQty)
        }

    @Test
    fun test_updatePurchaseInvoiceDetailBillNo_updates_billNo_in_detailUIModel() =
        runTest {
            // given
            val detail =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries = mutableStateListOf(),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)

            // when
            purchaseInvoiceDetailLiveData.updatePurchaseInvoiceDetailBillNo("B123")
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()

            // then
            assertEquals("B123", result?.billNo)
        }

    @Test
    fun test_updatePurchaseInvoiceItem_finds_and_replaces_matching_entry() =
        runTest {
            // given
            val entry =
                PurchaseInvoiceEntryUIModel(
                    barcode = "XYZ",
                    actualQty = 2,
                    productName = "P",
                    unitName = "U",
                    unitFactor = 1,
                    qty = 10,
                    salePrice = 5.0,
                )
            val detail =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries = mutableStateListOf(entry),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)

            // when
            val updatedModel: IPurchaseInvoiceProductsModel = entry.copy(actualQty = 9)
            purchaseInvoiceDetailLiveData.updatePurchaseInvoiceItem(updatedModel)
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.entries?.size)
            assertEquals(9, result?.entries?.first()?.actualQty)
        }

    @Test
    fun test_addPurchaseInvoiceProduct_adds_new_item_to_front() =
        runTest {
            // given
            val detail =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries = mutableStateListOf(),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)

            // when
            val newEntry =
                PurchaseInvoiceEntryUIModel(
                    barcode = "NEW",
                    actualQty = 3,
                    productName = "P",
                    unitName = "U",
                    unitFactor = 1,
                    qty = 5,
                    salePrice = 2.0,
                )
            purchaseInvoiceDetailLiveData.addPurchaseInvoiceProduct(newEntry)
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.entries?.size)
            assertEquals("NEW", result?.entries?.first()?.barcode)
        }

    @Test
    fun test_addPurchaseInvoiceProduct_existing_increments_and_moves_to_front() =
        runTest {
            // given
            val existing =
                PurchaseInvoiceEntryUIModel(
                    barcode = "EX",
                    actualQty = 2,
                    productName = "P",
                    unitName = "U",
                    unitFactor = 1,
                    qty = 5,
                    salePrice = 2.0,
                )
            val detail =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries = mutableStateListOf(existing),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)

            // when
            val toAdd =
                PurchaseInvoiceEntryUIModel(
                    barcode = "EX",
                    actualQty = 4,
                    productName = "",
                    unitName = "",
                    unitFactor = 1,
                    qty = 0,
                    salePrice = 0.0,
                )
            purchaseInvoiceDetailLiveData.addPurchaseInvoiceProduct(toAdd)
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.entries?.size)
            // 2 + 4 = 6
            assertEquals(6, result?.entries?.first()?.actualQty)
        }

    @Test
    fun test_removePurchaseInvoiceProductItem_removes_all_matching_barcodes() =
        runTest {
            // given
            val keep = PurchaseInvoiceEntryUIModel("A", 1, "", "", 1, 0, 0.0)
            val remove1 = PurchaseInvoiceEntryUIModel("B", 1, "", "", 1, 0, 0.0)
            val remove2 = PurchaseInvoiceEntryUIModel("B", 2, "", "", 1, 0, 0.0)
            val detail =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries = mutableStateListOf(keep, remove1, remove2),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)

            // when
            purchaseInvoiceDetailLiveData.removePurchaseInvoiceProductItem("B")
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.entries?.size)
            assertEquals("A", result?.entries?.first()?.barcode)
        }

    @Test
    fun test_updatePurchaseInvoiceItem_no_matching_barcode_nothing_changes() =
        runTest {
            // given
            val entry = PurchaseInvoiceEntryUIModel("A", 1, "", "", 1, 0, 0.0)
            val detail = PurchaseInvoiceDetailUIModel("", "", "", "", "", mutableStateListOf(entry))
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)
            val notMatchModel = entry.copy(barcode = "NOT_FOUND")

            // when
            purchaseInvoiceDetailLiveData.updatePurchaseInvoiceItem(notMatchModel)
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.entries?.size)
            assertEquals("A", result?.entries?.first()?.barcode)
        }

    @Test
    fun test_updatePurchaseInvoiceItem_when_detail_null_nothing_happens() =
        runTest {
            val mockModel = mockk<IPurchaseInvoiceProductsModel>(relaxed = true)
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()
            // Should not throw
            purchaseInvoiceDetailLiveData.updatePurchaseInvoiceItem(mockModel)
        }

    @Test
    fun test_addPurchaseInvoiceProduct_when_detail_null_nothing_happens() =
        runTest {
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()
            purchaseInvoiceDetailLiveData.addPurchaseInvoiceProduct(
                PurchaseInvoiceEntryUIModel("B", 1, "", "", 1, 0, 0.0),
            )
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()
            assertEquals(null, result)
        }

    @Test
    fun test_removePurchaseInvoiceProductItem_barcode_not_found() =
        runTest {
            val entry = PurchaseInvoiceEntryUIModel("A", 1, "", "", 1, 0, 0.0)
            val detail = PurchaseInvoiceDetailUIModel("", "", "", "", "", mutableStateListOf(entry))
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)

            // when
            purchaseInvoiceDetailLiveData.removePurchaseInvoiceProductItem("NOT_FOUND")
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()

            // then
            assertEquals(1, result?.entries?.size)
            assertEquals("A", result?.entries?.first()?.barcode)
        }

    @Test
    fun test_removePurchaseInvoiceProductItem_when_detail_null_nothing_happens() =
        runTest {
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()
            purchaseInvoiceDetailLiveData.removePurchaseInvoiceProductItem("ANY")
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()
            assertEquals(null, result)
        }

    @Test
    fun test_updatePurchaseInvoiceDetailBillNo_when_detail_null_nothing_happens() =
        runTest {
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()
            purchaseInvoiceDetailLiveData.updatePurchaseInvoiceDetailBillNo("TEST_BILL")
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()
            assertEquals(null, result)
        }

    @Test
    fun test_updatePurchaseInvoiceItemActualQty_when_item_null_nothing_happens() =
        runTest {
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceItem()
            purchaseInvoiceDetailLiveData.updatePurchaseInvoiceItemActualQty(99)
            val result = purchaseInvoiceDetailLiveData.purchaseInvoiceItemUIModel.getOrAwaitValue()
            assertEquals(null, result)
        }

    @Test
    fun test_addPurchaseInvoiceProduct_when_detail_is_null_nothing_happens() =
        runTest {
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()
            purchaseInvoiceDetailLiveData.addPurchaseInvoiceProduct(
                PurchaseInvoiceEntryUIModel("B", 1, "", "", 1, 0, 0.0),
            )
            val result =
                purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()
            assertEquals(null, result)
        }

    @Test
    fun test_setUpdatedTimestamp_expect_update_item() =
        runTest {
            // given
            val listTimestamp = 150000L

            // when
            purchaseInvoiceDetailLiveData.setUpdatedTimestamp(listTimestamp)
            val result = purchaseInvoiceDetailLiveData.updatedTimestamp.getOrAwaitValue()

            // then
            assertEquals(150000L, result)
        }

    // 1) getPurchaseInvoiceDetailBillNo()

    @Test
    fun test_getPurchaseInvoiceDetailBillNo_returns_value_when_detail_set() =
        runTest {
            val detail =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "B-INV-001",
                    entries = mutableStateListOf(),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)

            val bill = purchaseInvoiceDetailLiveData.getPurchaseInvoiceDetailBillNo()
            assertEquals("B-INV-001", bill)
        }

    @Test
    fun test_getPurchaseInvoiceDetailBillNo_returns_empty_when_detail_null() =
        runTest {
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()

            val bill = purchaseInvoiceDetailLiveData.getPurchaseInvoiceDetailBillNo()
            assertEquals("", bill)
        }

// 2) getPIDetail()

    @Test
    fun test_getPIDetail_returns_existing_detail_when_set() =
        runTest {
            val entry = PurchaseInvoiceEntryUIModel("A", 1, "", "", 1, 0, 0.0)
            val detail =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "SUP",
                    supplierCode = "001",
                    poNo = "PO1",
                    piNo = "PI1",
                    billNo = "BILL1",
                    entries = mutableStateListOf(entry),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)

            val got = purchaseInvoiceDetailLiveData.getPIDetail()
            assertEquals("SUP", got.supplierName)
            assertEquals("001", got.supplierCode)
            assertEquals("PO1", got.poNo)
            assertEquals("PI1", got.piNo)
            assertEquals("BILL1", got.billNo)
            assertEquals(1, got.entries.size)
            assertEquals("A", got.entries.first().barcode)
        }

    @Test
    fun test_getPIDetail_returns_default_when_detail_null() =
        runTest {
            purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()

            val got = purchaseInvoiceDetailLiveData.getPIDetail()
            // ค่า default ต้องเป็นค่าว่างทั้งหมด และ entries ว่าง
            assertEquals("", got.supplierName)
            assertEquals("", got.supplierCode)
            assertEquals("", got.poNo)
            assertEquals("", got.piNo)
            assertEquals("", got.billNo)
            assertEquals(0, got.entries.size)
        }

// 3) existing = detail.entries.find { it.barcode == products.barcode }
//    — เคสเจอ (existing != null) และไม่เจอ (existing == null)

    @Test
    fun test_addPurchaseInvoiceProduct_existing_found_increments_qty_and_moves_to_front() =
        runTest {
            val exist = PurchaseInvoiceEntryUIModel("EX", 2, "P", "U", 1, 5, 0.0)
            val other = PurchaseInvoiceEntryUIModel("OT", 1, "P2", "U", 1, 5, 0.0)
            val detail =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries = mutableStateListOf(other, exist), // existing ไม่ได้อยู่หน้าสุด
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)

            // products.barcode เหมือน "EX" => existing ถูก find เจอ
            val toAdd = PurchaseInvoiceEntryUIModel("EX", 4, "", "", 1, 0, 0.0)
            purchaseInvoiceDetailLiveData.addPurchaseInvoiceProduct(toAdd)

            val got = purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()
            // ควรถูกย้ายมาหน้าแรก และ actualQty = 2 + 4 = 6
            assertEquals(2, got?.entries?.size)
            assertEquals("EX", got?.entries?.first()?.barcode)
            assertEquals(6, got?.entries?.first()?.actualQty)
        }

    @Test
    fun test_addPurchaseInvoiceProduct_existing_not_found_adds_new_to_front() =
        runTest {
            val exist = PurchaseInvoiceEntryUIModel("EX", 2, "P", "U", 1, 5, 0.0)
            val detail =
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries = mutableStateListOf(exist),
                )
            purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(detail)

            // products.barcode ไม่ซ้ำ => existing == null
            val toAdd = PurchaseInvoiceEntryUIModel("NEW", 3, "", "", 1, 0, 0.0)
            purchaseInvoiceDetailLiveData.addPurchaseInvoiceProduct(toAdd)

            val got = purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.getOrAwaitValue()
            assertEquals(2, got?.entries?.size)
            assertEquals("NEW", got?.entries?.first()?.barcode) // แทรกหน้าแรก
            assertEquals(3, got?.entries?.first()?.actualQty)
        }
}
