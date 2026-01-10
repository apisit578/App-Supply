package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.OrderReceiveToteDiscountType
import th.co.cjexpress.store.sage.domain.livedata.orderReceiveTote.OrderReceiveToteLiveData
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeExpiryUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.utils.DateUtils.toYYYYMMDDDate
import th.co.cjexpress.store.sage.utils.ResourceUtils
import java.util.Date

class SdToteReceivingBarcodeUseCaseTest {
    @RelaxedMockK
    lateinit var orderReceiveToteLiveData: OrderReceiveToteLiveData

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var useCase: SdToteReceivingBarcodeUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SdToteReceivingBarcodeUseCase(orderReceiveToteLiveData, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun test_searchBarcodeByLotNo_when_searchBarcodeInTote_is_null_expect_return_null() {
        // given
        every { orderReceiveToteLiveData.searchBarcodeInTote("1234567890123") } returns null

        // when
        val result =
            useCase.searchBarcodeByLotNo(
                barcode = "1234567890123",
                lotNo = "LOT123",
                expiryDate = "20241231".toYYYYMMDDDate(),
            )

        // then
        assertNull(result)
    }

    @Test
    fun test_searchBarcodeByLotNo_when_searchBarcodeInTote_not_null_expect_return_not_null() {
        // given
        val barcodeDetailMock = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        val expDateMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        every { expDateMock.lotNo } returns "LOT123"
        every { expDateMock.expDate } returns "20241231".toYYYYMMDDDate()
        val expList = SnapshotStateList<ISdToteExpiryDateModel>().apply { add(expDateMock) }
        every { barcodeDetailMock.expList } returns expList
        every { orderReceiveToteLiveData.searchBarcodeInTote("1234567890123") } returns barcodeDetailMock

        // when
        val result =
            useCase.searchBarcodeByLotNo(
                barcode = "1234567890123",
                lotNo = "LOT123",
                expiryDate = "20241231".toYYYYMMDDDate(),
            )

        // then
        assertNotNull(result)
    }

    @Test
    fun test_searchBarcodeByLotNo_when_searchBarcodeInTote_not_null_but_lotNo_mismatch_expect_return_null() {
        // given
        val barcodeDetailMock = mockk<SdToteDocsDetailBarcodeUIModel>(relaxed = true)
        val expDateMock = mockk<ISdToteExpiryDateModel>(relaxed = true)
        every { expDateMock.lotNo } returns "LOT123"
        every { expDateMock.expDate } returns "20241231".toYYYYMMDDDate()
        val expList = SnapshotStateList<ISdToteExpiryDateModel>().apply { add(expDateMock) }
        every { barcodeDetailMock.expList } returns expList
        every { orderReceiveToteLiveData.searchBarcodeInTote("1234567890123") } returns barcodeDetailMock

        // when
        val result =
            useCase.searchBarcodeByLotNo(
                barcode = "1234567890123",
                lotNo = "LOT124",
                expiryDate = "20241231".toYYYYMMDDDate(),
            )

        // then
        assertEquals(result?.expList?.get(1)?.qty, 0)
        assertEquals(result?.expList?.get(1)?.actualQty, 1)
        assertEquals(result?.expList?.get(1)?.lotNo, "LOT124")
    }

    @Test
    fun test_groupBarcodeByExpiryDate_when_product_type_is_NORMAL_and_qty_equals_actualQty_should_filtered_out() {
        val date = Date()
        val exp =
            SdToteDocsDetailBarcodeExpiryUIModel(
                expDate = date,
                actualQty = 10,
                qty = 10,
                lotNo = "LOT01",
            )
        val input =
            listOf(
                SdToteDocsDetailBarcodeUIModel(
                    barcode = "123456",
                    discountType = OrderReceiveToteDiscountType.NORMAL.name,
                    noOfExp = 1,
                    actualQty = 10,
                    qty = 10,
                    barFactor = 1,
                    productName = "Test",
                    unitName = "pcs",
                    skuCode = "SKU123456",
                    expList = mutableStateListOf(exp),
                ),
            )
        val result = useCase.groupBarcodeByExpiryDate(input)
        assertTrue(result.isEmpty())
    }

    @Test
    fun test_groupBarcodeByExpiryDate_when_product_type_is_NORMAL_and_qty_not_equal_actualQty_should_be_kept() {
        val date = Date()
        val exp =
            SdToteDocsDetailBarcodeExpiryUIModel(
                expDate = date,
                actualQty = 5,
                qty = 10,
                lotNo = "LOT01",
            )
        val input =
            listOf(
                SdToteDocsDetailBarcodeUIModel(
                    barcode = "123456",
                    discountType = OrderReceiveToteDiscountType.NORMAL.name,
                    noOfExp = 1,
                    actualQty = 5,
                    qty = 10,
                    barFactor = 1,
                    productName = "Test",
                    unitName = "pcs",
                    skuCode = "SKU123456",
                    expList = mutableStateListOf(exp),
                ),
            )
        val result = useCase.groupBarcodeByExpiryDate(input)
        assertEquals(1, result.size)
        assertEquals(5, result.first().actualQty)
        assertEquals(10, result.first().qty)
    }

    @Test
    fun test_groupBarcodeByExpiryDate_when_product_type_is_FRESHLIFE_and_empty_expList() {
        val input =
            listOf(
                SdToteDocsDetailBarcodeUIModel(
                    barcode = "123456",
                    discountType = OrderReceiveToteDiscountType.FRESHLIFE.name,
                    noOfExp = 1,
                    actualQty = 5,
                    qty = 10,
                    barFactor = 1,
                    productName = "Test",
                    unitName = "pcs",
                    skuCode = "SKU123456",
                    expList = mutableStateListOf(),
                ),
            )
        val result = useCase.groupBarcodeByExpiryDate(input)
        assertEquals(1, result.size)
        val expected = input.first()
        val actual = result.first()
        assertTrue(
            expected.barcode == actual.barcode &&
                expected.discountType == actual.discountType &&
                expected.noOfExp == actual.noOfExp &&
                expected.actualQty == actual.actualQty &&
                expected.barFactor == actual.barFactor &&
                expected.productName == actual.productName &&
                expected.qty == actual.qty &&
                expected.unitName == actual.unitName &&
                expected.qrCodePrint == actual.qrCodePrint &&
                expected.expList.toList() == actual.expList.toList(),
        )
    }

    @Test
    fun test_groupBarcodeByExpiryDate_when_product_type_is_FRESHLIFE_group_by_expDate_and_have_LOTDUMMY() {
        val exp1 =
            SdToteDocsDetailBarcodeExpiryUIModel(
                expDate = "20250101".toYYYYMMDDDate(),
                actualQty = 2,
                qty = 4,
                lotNo = "LOTDUMMY",
            )
        val exp2 =
            SdToteDocsDetailBarcodeExpiryUIModel(
                expDate = "20250101".toYYYYMMDDDate(),
                actualQty = 3,
                qty = 3,
                lotNo = "LOT01",
            )
        val exp3 =
            SdToteDocsDetailBarcodeExpiryUIModel(
                expDate = "20250102".toYYYYMMDDDate(),
                actualQty = 4,
                qty = 5,
                lotNo = "LOTDUMMY",
            )
        val input =
            listOf(
                SdToteDocsDetailBarcodeUIModel(
                    barcode = "123456",
                    discountType = OrderReceiveToteDiscountType.FRESHLIFE.name,
                    noOfExp = 1,
                    actualQty = 10,
                    qty = 12,
                    barFactor = 1,
                    productName = "Test",
                    unitName = "pcs",
                    skuCode = "SKU123456",
                    expList = mutableStateListOf(exp1, exp2, exp3),
                ),
            )
        val result = useCase.groupBarcodeByExpiryDate(input)
        assertEquals(2, result.size)
        assertEquals(5, result[0].actualQty) // 2+3
        assertEquals(7, result[0].qty) // 4+3
        assertEquals(2, result[0].qrCodePrint) // only from LOTDUMMY
        assertEquals(4, result[1].actualQty)
        assertEquals(5, result[1].qty)
        assertEquals(4, result[1].qrCodePrint)
    }

    @Test
    fun test_groupBarcodeByExpiryDate_when_product_type_is_FRESHLIFE_filtered_when_qty_equal_actualQty_after_grouping() {
        val exp =
            SdToteDocsDetailBarcodeExpiryUIModel(
                expDate = "20250101".toYYYYMMDDDate(),
                actualQty = 10,
                qty = 10,
                lotNo = "LOTDUMMY",
            )
        val input =
            listOf(
                SdToteDocsDetailBarcodeUIModel(
                    barcode = "123456",
                    discountType = OrderReceiveToteDiscountType.FRESHLIFE.name,
                    noOfExp = 1,
                    actualQty = 10,
                    qty = 10,
                    barFactor = 1,
                    productName = "Test",
                    unitName = "pcs",
                    skuCode = "SKU123456",
                    expList = mutableStateListOf(exp),
                ),
            )
        val result = useCase.groupBarcodeByExpiryDate(input)
        assertTrue(result.isEmpty())
    }

    @Test
    fun test_incrementFreshLifeExpiryDate_expect_should_call_orderReceiveToteLiveData_incrementFreshLifeExpiryDate() {
        // given
        val mockDate = Date()

        // when
        useCase.incrementFreshLifeExpiryDate(mockDate, false)

        // then
        verify { orderReceiveToteLiveData.incrementFreshLifeExpiryDate(mockDate, false) }
    }

    @Test
    fun test_decreaseFreshLifeExpiryDate_expect_should_call_orderReceiveToteLiveData_decreaseFreshLifeExpiryDate() {
        // given
        val mockDate = Date()

        // when
        useCase.decreaseFreshLifeExpiryDate(mockDate, false)

        // then
        verify { orderReceiveToteLiveData.decreaseFreshLifeExpiryDate(mockDate, false) }
    }

    @Test
    fun test_searchBarcode() {
        val barcode = "barcode"
        val mockBarcodeModel = mockk<SdToteDocsDetailBarcodeUIModel>()
        every { orderReceiveToteLiveData.searchBarcodeInTote(barcode) } returns mockBarcodeModel
        useCase.searchBarcode(barcode)
        verify { orderReceiveToteLiveData.searchBarcodeInTote(barcode) }
    }

    @Test
    fun test_createSubmitBarcode() {
        val now = Date()
        val isNewBarcode = false
        val receivingBarcode =
            SdToteDocsDetailBarcodeUIModel(
                barcode = "barcode",
                discountType = "discountType",
                noOfExp = 7,
                actualQty = 3,
                barFactor = 1,
                productName = "productName",
                qty = 5,
                unitName = "piece",
                skuCode = "SKU123456",
                expList = mutableStateListOf(),
            )
        val actualQty = 3
        val expList =
            mutableListOf<ISdToteExpiryDateModel>(
                SdToteDocsDetailBarcodeExpiryUIModel(
                    expDate = Date(now.time + 86400000L),
                    actualQty = 2,
                    lotNo = "L01",
                    qty = 3,
                ),
                SdToteDocsDetailBarcodeExpiryUIModel(
                    expDate = Date(now.time + (3 * 86400000L)),
                    actualQty = 1,
                    lotNo = "L02",
                    qty = 2,
                ),
            )
        val sdNo = "sdNo"
        val toteCode = "toteCode"
        val receivedDate = now
        every { orderReceiveToteLiveData.getSdToteDocNo() } returns sdNo
        every { orderReceiveToteLiveData.getSdToteCode() } returns toteCode
        every { orderReceiveToteLiveData.getReceivedDate() } returns receivedDate

        val response =
            useCase.createSubmitBarcode(
                isNewBarcode = isNewBarcode,
                receivingBarcode = receivingBarcode,
                actualQty = actualQty,
                expList = expList,
            )

        assertEquals(response.sdNo, sdNo)
        assertEquals(response.toteCode, toteCode)
        assertEquals(response.receivedDate, receivedDate)
        assertEquals(response.barcode, receivingBarcode.barcode)
        assertEquals(response.discountType, receivingBarcode.discountType)
        assertEquals(response.noOfExp, receivingBarcode.noOfExp)
        assertEquals(response.actualQty, actualQty)
        assertEquals(response.isNewBarcode, isNewBarcode)
        assertEquals(response.expList.first().expDate, Date(now.time + 86400000L))
    }

    @Test
    fun test_setToteSubmitUIModel() {
        val receivingBarcode = mockk<SdToteSubmitUIModel>()
        useCase.setToteSubmitUIModel(receivingBarcode)
        verify { orderReceiveToteLiveData.setToteSubmitUIModel(receivingBarcode) }
    }

    @Test
    fun test_addNewReceivingBarcode() {
        val newBarcode = mockk<SdToteDocsDetailBarcodeUIModel>()
        useCase.addNewReceivingBarcode(newBarcode)
        verify { orderReceiveToteLiveData.addNewReceivingBarcode(newBarcode) }
    }

    @Test
    fun test_resetSdToteSubmitUIModel() {
        useCase.resetSdToteSubmitUIModel()
        verify { orderReceiveToteLiveData.resetSdToteSubmitUIModel() }
    }

    @Test
    fun test_resetSdToteDocsDetailUIModel() {
        useCase.resetSdToteDocsDetailUIModel()
        verify { orderReceiveToteLiveData.resetSdToteDocsDetailUIModel() }
    }

    @Test
    fun test_updateOrAddSubmitExpiryDate() {
        val expiryDate = Date()
        val qrPrint = false
        useCase.updateOrAddSubmitExpiryDate(expiryDate, qrPrint)
        verify {
            orderReceiveToteLiveData.updateOrAddSubmitExpiryDate(
                expiryDate,
                qrPrint,
            )
        }
    }

    @Test
    fun test_moveExpiryDateLotToFirst() {
        val expiryDate = Date()
        useCase.moveExpiryDateLotToFirst(expiryDate)
        verify { orderReceiveToteLiveData.moveExpiryDateLotToFirst(expiryDate) }
    }

    @Test
    fun test_getSdToteDocNo() {
        val docNo = "docNo"
        every { orderReceiveToteLiveData.getSdToteDocNo() } returns docNo
        assertEquals(docNo, useCase.getSdToteDocNo())
        verify { orderReceiveToteLiveData.getSdToteDocNo() }
    }

    @Test
    fun test_getSdToteCode() {
        val toteCode = "toteCode"
        every { orderReceiveToteLiveData.getSdToteCode() } returns toteCode
        assertEquals(toteCode, useCase.getSdToteCode())
        verify { orderReceiveToteLiveData.getSdToteCode() }
    }

    @Test
    fun test_removeSubmitExpiryDate() {
        val expiryDate = Date()
        val isLotDummy = false
        useCase.removeSubmitExpiryDate(expiryDate, isLotDummy)
        verify { orderReceiveToteLiveData.removeSubmitExpiryDate(expiryDate, isLotDummy) }
    }

    @Test
    fun test_updateSubmitActualQty() {
        val actualQty = 5
        useCase.updateSubmitActualQty(actualQty)
        verify { orderReceiveToteLiveData.updateSubmitActualQty(actualQty) }
    }

    @Test
    fun test_updateIsAddLotState() {
        val isAddLot = false
        useCase.updateIsAddLotState(isAddLot)
        verify { orderReceiveToteLiveData.updateIsAddLotState(isAddLot) }
    }

    @Test
    fun test_getExpiryDateActualQty() {
        val expiryDate = Date()
        useCase.getExpiryDateActualQty(expiryDate)
        verify { orderReceiveToteLiveData.getExpiryDateActualQty(expiryDate) }
    }

    @Test
    fun test_getIncompleteItems() {
        val expectResult = listOf<SdToteDocsDetailBarcodeUIModel>()
        val result = useCase.getIncompleteItems()
        assertEquals(result, expectResult)
        verify { orderReceiveToteLiveData.getIncompleteItems() }
    }
}
