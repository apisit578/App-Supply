package th.co.cjexpress.store.sage.domain.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import th.co.cjexpress.store.sage.constants.OrderReceiveToteLotType
import th.co.cjexpress.store.sage.domain.livedata.orderReceiveTote.OrderReceiveToteLiveData
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeExpiryUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.utils.DateUtils.fromDDMMYYYYFormatToDate
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat
import th.co.cjexpress.store.sage.utils.MainDispatcherRule
import th.co.cjexpress.store.sage.utils.getOrAwaitValue
import java.util.Date

@ExperimentalCoroutinesApi
class OrderReceiveToteLiveDataTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var orderReceiveToteLiveData: OrderReceiveToteLiveData

    @Before
    fun setUp() {
        orderReceiveToteLiveData = OrderReceiveToteLiveData()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_resetSdToteDocsDetailUIModel_expect_null() {
        orderReceiveToteLiveData.resetSdToteDocsDetailUIModel()
        assertNull(orderReceiveToteLiveData.sdToteDocsDetailUIModel.getOrAwaitValue())
    }

    @Test
    fun test_setSdToteDocsDetailUIModel_expect_not_null() {
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        assertNotNull(orderReceiveToteLiveData.sdToteDocsDetailUIModel.getOrAwaitValue())
    }

    @Test
    fun test_setToteSubmitUIModel_expect_not_null() {
        val dataMock = mockk<SdToteSubmitUIModel>(relaxed = true)
        orderReceiveToteLiveData.setToteSubmitUIModel(dataMock)
        assertNotNull(orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue())
    }

    @Test
    fun test_resetSdToteSubmitUIModel_expect_null() {
        orderReceiveToteLiveData.resetSdToteSubmitUIModel()
        assertNull(orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue())
    }

    @Test
    fun test_searchBarcodeInTote_when_barcode_matched_expect_not_null() {
        // given
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        val itemMock = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        every { itemMock.barcode } returns "8888888888"
        val snaps = SnapshotStateList<SdToteDocsDetailBarcodeUIModel>()
        snaps.add(itemMock)
        every { dataMock.items } returns snaps
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        // when
        assertNotNull(orderReceiveToteLiveData.searchBarcodeInTote("8888888888"))
    }

    @Test
    fun test_searchBarcodeInTote_when_barcode_mismatched_expect_null() {
        // given
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        val itemMock = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        every { itemMock.barcode } returns "8888888888"
        val snaps = SnapshotStateList<SdToteDocsDetailBarcodeUIModel>()
        snaps.add(itemMock)
        every { dataMock.items } returns snaps
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        // when
        assertNull(orderReceiveToteLiveData.searchBarcodeInTote("9999999999"))
    }

    @Test
    fun test_getSdToteDocNo() {
        // given
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        every { dataMock.sdNo } returns "SD123456"
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        // when
        assertEquals("SD123456", orderReceiveToteLiveData.getSdToteDocNo())
    }

    @Test
    fun test_getSdToteDocNo_expect_empty() {
        assertEquals("", orderReceiveToteLiveData.getSdToteDocNo())
    }

    @Test
    fun test_getSdToteCode() {
        // given
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        every { dataMock.toteCode } returns "TL123456"
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        // when
        assertEquals("TL123456", orderReceiveToteLiveData.getSdToteCode())
    }

    @Test
    fun test_getSdToteCode_expect_empty() {
        assertEquals("", orderReceiveToteLiveData.getSdToteCode())
    }

    @Test
    fun test_getReceivedDate_expect_not_null() {
        // given
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        every { dataMock.receivedDate } returns Date()
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        // when
        assertNotNull(orderReceiveToteLiveData.getReceivedDate())
    }

//    @Test
//    fun test_getReceivedDate_expect_null() {
//        assertNull(orderReceiveToteLiveData.getReceivedDate())
//    }

    @Test
    fun test_getUpdateItemExpList_when_same_expire_day() {
        // given
        val expDate = Date()
        val qrPrint = true
        val itemProductMock = mockk<SdToteSubmitUIModel>(relaxed = true)
        val expItemMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        every { expItemMock.expDate } returns expDate
        every { expItemMock.actualQty } returns 2
        every { itemProductMock.expList } returns mutableStateListOf(expItemMock)
        // when
        orderReceiveToteLiveData.updateOrAddSubmitExpiryDate(expDate, qrPrint)
    }

    @Test
    fun test_getUpdateItemExpList_when_not_same_expire_day() {
        // given
        val itemProductMock = mockk<SdToteSubmitUIModel>(relaxed = true)
        val expItemMock = mockk<SdToteDocsDetailBarcodeExpiryUIModel>(relaxed = true)
        val qrPrint = true
        every { expItemMock.expDate } returns Date()
        every { itemProductMock.expList } returns mutableStateListOf(expItemMock)
        // when
        orderReceiveToteLiveData.updateOrAddSubmitExpiryDate("01/01/2025".fromDDMMYYYYFormatToDate(), qrPrint)
    }

//    @Test
//    fun test_resetItemProductSdToteExpUIModel() {
//        val itemProductMock = mockk<SdToteSubmitUIModel>(relaxed = true)
//        orderReceiveToteLiveData.resetItemProductSdToteExpUIModel(
//            itemProductMock, 1, currExpList = mutableListOf()
//        )
//    }

    @Test
    fun updateSdToteDetailDataAfterSubmitted() {
    }

    @Test
    fun test_updateReceivingBarcode() {
        // given
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        val itemMock = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        every { itemMock.barcode } returns "8888888888"
        val snaps = SnapshotStateList<SdToteDocsDetailBarcodeUIModel>()
        snaps.add(itemMock)
        every { dataMock.items } returns snaps
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        // when
        val expItemMock = mockk<SdToteDocsDetailBarcodeExpiryUIModel>(relaxed = true)
        orderReceiveToteLiveData.updateReceivingBarcode(
            barcode = "8888888888",
            actualQty = 1,
            expList = mutableListOf(expItemMock),
        )
    }

    @Test
    fun test_addNewReceivingBarcode() {
        // given
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        val itemMock = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        every { itemMock.barcode } returns "8888888888"
        val snaps = SnapshotStateList<SdToteDocsDetailBarcodeUIModel>()
        snaps.add(itemMock)
        every { dataMock.items } returns snaps
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        // when
        val receivingProduct = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        every { receivingProduct.barcode } returns "8888888888"
        orderReceiveToteLiveData.addNewReceivingBarcode(receivingProduct)
    }

    @Test
    fun updateActualQty() {
    }

    @Test
    fun test_updateExpiryDateActualQty_when_not_exist_and_qrPrint_true() {
        // given
        val expiryDate = Date()
        val expItemMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        val snapsItem = SnapshotStateList<ISdToteExpiryDateModel>()
        every { expItemMock.expDate } returns Date(1735689600)
        every { expItemMock.actualQty } returns 2
        every { expItemMock.lotNo } returns "L111"
        snapsItem.add(expItemMock)

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 2,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.updateOrAddSubmitExpiryDate(expiryDate = expiryDate, isNoQR = true)

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(2, updated?.expList?.size)
        assertEquals(OrderReceiveToteLotType.LOTDUMMY.name, updated?.expList?.get(1)?.lotNo)
        assertEquals(1, updated?.expList?.get(1)?.actualQty)
        assertEquals(0, updated?.expList?.get(0)?.qty)
    }

    @Test
    fun test_updateExpiryDateActualQty_when_not_exist_and_qrPrint_false() {
        // given
        val expiryDate = Date()
        val expItemMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        val snapsItem = SnapshotStateList<ISdToteExpiryDateModel>()
        every { expItemMock.expDate } returns Date(1735689600)
        every { expItemMock.actualQty } returns 2
        every { expItemMock.lotNo } returns "L111"
        snapsItem.add(expItemMock)

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 2,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.updateOrAddSubmitExpiryDate(expiryDate = expiryDate, isNoQR = false)

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(2, updated?.expList?.size)
        assertEquals("MANUAL_ADJUST", updated?.expList?.get(1)?.lotNo)
        assertEquals(1, updated?.expList?.get(1)?.actualQty)
        assertEquals(0, updated?.expList?.get(0)?.qty)
    }

    @Test
    fun test_updateExpiryDateActualQty_when_LOTDUMMY_exist() {
        // given
        val expiryDate = Date(1735689600)
        val expItemMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        val snapsItem = SnapshotStateList<ISdToteExpiryDateModel>()
        every { expItemMock.expDate } returns expiryDate
        every { expItemMock.lotNo } returns OrderReceiveToteLotType.LOTDUMMY.name
        snapsItem.add(expItemMock)

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 0,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.updateOrAddSubmitExpiryDate(expiryDate = expiryDate, isNoQR = true)

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(1, updated?.expList?.size)
        assertEquals(OrderReceiveToteLotType.LOTDUMMY.name, updated?.expList?.get(0)?.lotNo)
        assertEquals(0, updated?.expList?.get(0)?.qty)
    }

    @Test
    fun test_updateExpiryDateActualQty_when_LOTDUMMY_not_exist() {
        // given
        val expiryDate = Date(1735689600)
        val expItemMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        val snapsItem = SnapshotStateList<ISdToteExpiryDateModel>()
        every { expItemMock.expDate } returns expiryDate
        every { expItemMock.lotNo } returns "L011"
        snapsItem.add(expItemMock)

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 0,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.updateOrAddSubmitExpiryDate(expiryDate = expiryDate, isNoQR = true)

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(2, updated?.expList?.size)
        assertEquals(OrderReceiveToteLotType.LOTDUMMY.name, updated?.expList?.get(1)?.lotNo)
        assertEquals(0, updated?.expList?.get(1)?.qty)
    }

    @Test
    fun test_removeExpiryDate_when_expDate_mismatched() {
        // given
        val expiryDate = Date()
        val dataMock = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        val itemExpMock = mockk<SdToteDocsDetailBarcodeExpiryUIModel>(relaxed = true)
        val snapsExp = SnapshotStateList<ISdToteExpiryDateModel>() // SdToteDocsDetailBarcodeExpiryUIModel>()
        every { itemExpMock.expDate } returns expiryDate
        snapsExp.add(itemExpMock)
        every { dataMock.expList } returns snapsExp
//        orderReceiveToteLiveData.setSdToteDocsDetailBarcodeUIModel(dataMock)
        // when
        orderReceiveToteLiveData.removeSubmitExpiryDate(expiryDate, isLotDummy = true)
    }

    @Test
    fun test_getSdToteSummaryProducts_has_insufficientItems_expect_return_not_empty() {
        // given(มีสินค้าขาด)
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        val itemMock = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        val snapsItem = SnapshotStateList<SdToteDocsDetailBarcodeUIModel>()
        every { itemMock.actualQty } returns 2
        every { itemMock.qty } returns 3
        snapsItem.add(itemMock)
        every { dataMock.items } returns snapsItem
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        // when
        val results = orderReceiveToteLiveData.getIncompleteItems()
        // then
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun test_getSdToteSummaryProducts_has_overListItems_expect_return_not_empty() {
        // given(มีสินค้าเกิน)
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        val itemMock = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        val snapsItem = SnapshotStateList<SdToteDocsDetailBarcodeUIModel>()
        every { itemMock.actualQty } returns 2
        every { itemMock.qty } returns 1
        snapsItem.add(itemMock)
        every { dataMock.items } returns snapsItem
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        // when
        val results = orderReceiveToteLiveData.getIncompleteItems()
        // then
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun test_getSdToteSummaryProducts_expect_return_empty() {
        // given(ไม่มีสินค้าขาดและเกิน)
        val dataMock = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        val itemMock = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        val snapsItem = SnapshotStateList<SdToteDocsDetailBarcodeUIModel>()
        every { itemMock.actualQty } returns 1
        every { itemMock.qty } returns 1
        snapsItem.add(itemMock)
        every { dataMock.items } returns snapsItem
        orderReceiveToteLiveData.setSdToteDocsDetailUIModel(dataMock)
        // when
        val results = orderReceiveToteLiveData.getIncompleteItems()
        // then
        assertTrue(results.isEmpty())
    }

    @Test
    fun test_incrementFreshLifeExpiryDate() {
        // given
        val expiryDate = Date()

        val expItemSpy =
            spyk(
                object : ISdToteExpiryDateModel(
                    expDate = expiryDate,
                    actualQty = 0,
                    qty = 0,
                    lotNo = "",
                ) {
                    override var expDate: Date? = expiryDate
                    override var actualQty: Int = 2
                    override var lotNo: String = "L111"
                    override var qty: Int = 10
                },
            )
        val expItemSpyDummy =
            spyk(
                object : ISdToteExpiryDateModel(
                    expDate = expiryDate,
                    actualQty = 0,
                    qty = 0,
                    lotNo = "",
                ) {
                    override var expDate: Date? = expiryDate
                    override var actualQty: Int = 2
                    override var lotNo: String = OrderReceiveToteLotType.LOTDUMMY.name
                    override var qty: Int = 10
                },
            )

        val snapsItem =
            SnapshotStateList<ISdToteExpiryDateModel>().apply {
                add(expItemSpy)
                add(expItemSpyDummy)
            }

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 4,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.incrementFreshLifeExpiryDate(expiryDate, false)

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(5, updated?.actualQty)

        val formatted = expiryDate.toStringDDMMYYYYFormat()
        val updatedItem =
            updated?.expList?.firstOrNull {
                it.expDate?.toStringDDMMYYYYFormat() == formatted
            }
        assertEquals(3, updatedItem?.actualQty)
    }

    @Test
    fun test_incrementFreshLifeExpiryDate_when_is_lot_dummy() {
        // given
        val expiryDate = Date()

        val expItemSpy =
            spyk(
                object : ISdToteExpiryDateModel(
                    expDate = expiryDate,
                    actualQty = 0,
                    qty = 0,
                    lotNo = "",
                ) {
                    override var expDate: Date? = expiryDate
                    override var actualQty: Int = 2
                    override var lotNo: String = "L111"
                    override var qty: Int = 10
                },
            )
        val expItemSpyDummy =
            spyk(
                object : ISdToteExpiryDateModel(
                    expDate = expiryDate,
                    actualQty = 0,
                    qty = 0,
                    lotNo = "",
                ) {
                    override var expDate: Date? = expiryDate
                    override var actualQty: Int = 2
                    override var lotNo: String = OrderReceiveToteLotType.LOTDUMMY.name
                    override var qty: Int = 10
                },
            )

        val snapsItem =
            SnapshotStateList<ISdToteExpiryDateModel>().apply {
                add(expItemSpy)
                add(expItemSpyDummy)
            }

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 4,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.incrementFreshLifeExpiryDate(expiryDate, true)

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(5, updated?.actualQty)

        val formatted = expiryDate.toStringDDMMYYYYFormat()
        val updatedItem =
            updated?.expList?.firstOrNull {
                it.expDate?.toStringDDMMYYYYFormat() == formatted && it.lotNo == OrderReceiveToteLotType.LOTDUMMY.name
            }
        assertEquals(3, updatedItem?.actualQty)
    }

    @Test
    fun test_decreaseFreshLifeExpiryDate() {
        // given
        val expiryDate = Date()

        val expItemSpy =
            spyk(
                object : ISdToteExpiryDateModel(
                    expDate = expiryDate,
                    actualQty = 0,
                    qty = 0,
                    lotNo = "",
                ) {
                    override var expDate: Date? = expiryDate
                    override var actualQty: Int = 2
                    override var lotNo: String = "L111"
                    override var qty: Int = 10
                },
            )
        val expItemSpyDummy =
            spyk(
                object : ISdToteExpiryDateModel(
                    expDate = expiryDate,
                    actualQty = 0,
                    qty = 0,
                    lotNo = "",
                ) {
                    override var expDate: Date? = expiryDate
                    override var actualQty: Int = 2
                    override var lotNo: String = OrderReceiveToteLotType.LOTDUMMY.name
                    override var qty: Int = 10
                },
            )

        val snapsItem =
            SnapshotStateList<ISdToteExpiryDateModel>().apply {
                add(expItemSpy)
                add(expItemSpyDummy)
            }

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 4,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.decreaseFreshLifeExpiryDate(expiryDate, true)

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(3, updated?.actualQty)

        val formatted = expiryDate.toStringDDMMYYYYFormat()
        val updatedItem =
            updated?.expList?.firstOrNull {
                it.expDate?.toStringDDMMYYYYFormat() == formatted && it.lotNo == OrderReceiveToteLotType.LOTDUMMY.name
            }
        assertEquals(1, updatedItem?.actualQty)
    }

    @Test
    fun test_decreaseFreshLifeExpiryDate_when_is_lot_dummy() {
        // given
        val expiryDate = Date()

        val expItemSpy =
            spyk(
                object : ISdToteExpiryDateModel(
                    expDate = expiryDate,
                    actualQty = 0,
                    qty = 0,
                    lotNo = "",
                ) {
                    override var expDate: Date? = expiryDate
                    override var actualQty: Int = 2
                    override var lotNo: String = "L111"
                    override var qty: Int = 10
                },
            )
        val expItemSpyDummy =
            spyk(
                object : ISdToteExpiryDateModel(
                    expDate = expiryDate,
                    actualQty = 0,
                    qty = 0,
                    lotNo = "",
                ) {
                    override var expDate: Date? = expiryDate
                    override var actualQty: Int = 2
                    override var lotNo: String = OrderReceiveToteLotType.LOTDUMMY.name
                    override var qty: Int = 10
                },
            )

        val snapsItem =
            SnapshotStateList<ISdToteExpiryDateModel>().apply {
                add(expItemSpy)
                add(expItemSpyDummy)
            }

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 4,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.decreaseFreshLifeExpiryDate(expiryDate, false)

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(3, updated?.actualQty)

        val formatted = expiryDate.toStringDDMMYYYYFormat()
        val updatedItem =
            updated?.expList?.firstOrNull {
                it.expDate?.toStringDDMMYYYYFormat() == formatted && it.actualQty > 0
            }
        assertEquals(1, updatedItem?.actualQty)
    }

    @Test
    fun test_decreaseFreshLifeExpiryDate_when_is_lot_dummy_and_first_item_actual_qty_0() {
        // given
        val expiryDate = Date()

        val expItemSpy =
            spyk(
                object : ISdToteExpiryDateModel(
                    expDate = expiryDate,
                    actualQty = 0,
                    qty = 0,
                    lotNo = "",
                ) {
                    override var expDate: Date? = expiryDate
                    override var actualQty: Int = 0
                    override var lotNo: String = "L111"
                    override var qty: Int = 10
                },
            )
        val expItemSpyDummy =
            spyk(
                object : ISdToteExpiryDateModel(
                    expDate = expiryDate,
                    actualQty = 0,
                    qty = 0,
                    lotNo = "",
                ) {
                    override var expDate: Date? = expiryDate
                    override var actualQty: Int = 3
                    override var lotNo: String = "L2222"
                    override var qty: Int = 10
                },
            )

        val snapsItem =
            SnapshotStateList<ISdToteExpiryDateModel>().apply {
                add(expItemSpy)
                add(expItemSpyDummy)
            }

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 3,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.decreaseFreshLifeExpiryDate(expiryDate, false)

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(2, updated?.actualQty)

        val formatted = expiryDate.toStringDDMMYYYYFormat()
        val updatedItem =
            updated?.expList?.firstOrNull {
                it.expDate?.toStringDDMMYYYYFormat() == formatted && it.actualQty > 0
            }
        assertEquals(2, updatedItem?.actualQty)
    }

    @Test
    fun test_updateLotData_when_lotNo_is_match() {
        // given
        val expiryDate = Date()
        val expItemMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        val snapsItem = SnapshotStateList<ISdToteExpiryDateModel>()
        every { expItemMock.expDate } returns expiryDate
        every { expItemMock.actualQty } returns 2
        every { expItemMock.lotNo } returns ""
        snapsItem.add(expItemMock)

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 2,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.updateLotData(initialSubmitModel, expiryDate, lotNo = "")

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(1, updated?.expList?.size)
        assertEquals("", updated?.expList?.get(0)?.lotNo)
    }

    @Test
    fun test_updateLotData_when_lotNo_is_not_match() {
        // given
        val expiryDate = Date()
        val expItemMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        val snapsItem = SnapshotStateList<ISdToteExpiryDateModel>()
        every { expItemMock.expDate } returns expiryDate
        every { expItemMock.actualQty } returns 2
        every { expItemMock.lotNo } returns "L222"
        snapsItem.add(expItemMock)

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 2,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.updateLotData(initialSubmitModel, expiryDate, lotNo = "L111")

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(2, updated?.expList?.size)
        assertEquals("L111", updated?.expList?.get(1)?.lotNo)
    }

    @Test
    fun test_updateExistExpiryDate() {
        // given
        val expiryDate = Date()
        val expItemMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        val snapsItem = SnapshotStateList<ISdToteExpiryDateModel>()
        every { expItemMock.expDate } returns expiryDate
        every { expItemMock.actualQty } returns 2
        every { expItemMock.lotNo } returns "L222"
        snapsItem.add(expItemMock)

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 2,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.updateExistExpiryDate(initialSubmitModel, 0, add = true)

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(1, updated?.expList?.size)
        assertEquals("L222", updated?.expList?.get(0)?.lotNo)
    }

    @Test
    fun test_addExpiryDateByLot() {
        // given
        val expiryDate = Date()
        val expItemMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        val snapsItem = SnapshotStateList<ISdToteExpiryDateModel>()
        every { expItemMock.expDate } returns expiryDate
        every { expItemMock.actualQty } returns 2
        every { expItemMock.lotNo } returns "L222"
        snapsItem.add(expItemMock)

        val initialSubmitModel =
            SdToteSubmitUIModel(
                expList = snapsItem,
                actualQty = 2,
                toteCode = "",
                sdNo = "",
                receivedDate = Date(),
                noOfExp = 1,
                discountType = "",
                skuCode = "SKU123456",
                barcode = "123456",
            )
        orderReceiveToteLiveData.setToteSubmitUIModel(initialSubmitModel)

        // when
        orderReceiveToteLiveData.addExpiryDateByLot(initialSubmitModel, expiryDate, lotNo = "L111")

        // then
        val updated = orderReceiveToteLiveData.sdToteSubmitUIModel.getOrAwaitValue()
        assertEquals(2, updated?.expList?.size)
        assertEquals("L111", updated?.expList?.get(1)?.lotNo)
        assertEquals(1, updated?.expList?.get(1)?.actualQty)
    }
}
