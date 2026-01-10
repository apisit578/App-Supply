package th.co.cjexpress.store.sage.presentation.mapper.orderReceiveTote

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailData
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItem
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItemExpiryDateLot
import java.text.SimpleDateFormat
import java.util.Locale

class SdToteDocDetailsUIMapperTest {
    private lateinit var mapper: SdToteDocDetailsUIMapper
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    @Before
    fun setup() {
        mapper = SdToteDocDetailsUIMapper()
    }

    @Test
    fun `toUIModel with SdToteDetailData should group and sort items correctly`() {
        val items =
            mutableListOf(
                SdToteDetailItem(
                    actualQty = 0,
                    barcode = "003",
                    barFactor = 1,
                    discountType = "NONE",
                    noOfExp = 0,
                    productName = "Not Received",
                    qty = 5,
                    unitName = "PCS",
                    skuCode = "SKU003",
                    expList = null,
                ),
                SdToteDetailItem(
                    actualQty = 2,
                    barcode = "002",
                    barFactor = 1,
                    discountType = "DISC",
                    noOfExp = 0,
                    productName = "Insufficient",
                    qty = 5,
                    unitName = "PCS",
                    skuCode = "SKU002",
                    expList = null,
                ),
                SdToteDetailItem(
                    actualQty = 5,
                    barcode = "001",
                    barFactor = 1,
                    discountType = "DISC",
                    noOfExp = 0,
                    productName = "Match",
                    qty = 5,
                    unitName = "PCS",
                    skuCode = "SKU001",
                    expList = null,
                ),
                SdToteDetailItem(
                    actualQty = 10,
                    barcode = "004",
                    barFactor = 1,
                    discountType = "OVER",
                    noOfExp = 0,
                    productName = "Over",
                    qty = 5,
                    unitName = "PCS",
                    skuCode = "SKU004",
                    expList = null,
                ),
            )

        val data =
            SdToteDetailData(
                items = items,
                sdNo = "SD001",
                toteCode = "TOTE999",
                createdDate = sdf.parse("2025-07-01")!!,
                receivedDate = sdf.parse("2025-07-02")!!,
            )

        val result = mapper.toUIModel(data)

        assertEquals("SD001", result.sdNo)
        assertEquals("TOTE999", result.toteCode)
        assertEquals(data.receivedDate, result.receivedDate)
        assertEquals(4, result.items.size)

        val expectedOrder = listOf("003", "002", "004", "001")
        assertEquals(expectedOrder, result.items.map { it.barcode })
    }

    @Test
    fun `toUIModel with SdToteDetailItem should map all fields and sort expList`() {
        val exp1 =
            SdToteDetailItemExpiryDateLot(
                expDate = sdf.parse("2025-12-31"),
                actualQty = 1,
                qty = 2,
                lotNo = "L001",
            )
        val exp2 =
            SdToteDetailItemExpiryDateLot(
                expDate = sdf.parse("2025-11-30"),
                actualQty = 2,
                qty = 3,
                lotNo = "L002",
            )

        val item =
            SdToteDetailItem(
                actualQty = 5,
                barcode = "123456",
                barFactor = 2,
                discountType = "DISC",
                noOfExp = 2,
                productName = "Test Product",
                qty = 10,
                unitName = "PCS",
                skuCode = "SKU123456",
                expList = listOf(exp1, exp2),
            )

        val result = mapper.toUIModel(item)

        assertEquals("123456", result.barcode)
        assertEquals(2, result.expList.size)
        assertEquals(sdf.parse("2025-11-30"), result.expList[0].expDate)
        assertEquals("L002", result.expList[0].lotNo)
    }

    @Test
    fun `toUIModel with SdToteDetailItemExpiryDateLot should map fields correctly`() {
        val exp =
            SdToteDetailItemExpiryDateLot(
                expDate = sdf.parse("2025-10-01"),
                actualQty = 2,
                qty = 5,
                lotNo = "LOT123",
            )

        val result = mapper.toUIModel(exp)

        assertEquals(sdf.parse("2025-10-01"), result.expDate)
        assertEquals(2, result.actualQty)
        assertEquals(5, result.qty)
        assertEquals("LOT123", result.lotNo)
    }

    @Test
    fun `toUIModel with SdToteDetailItem and null expList should map empty expList`() {
        val item =
            SdToteDetailItem(
                actualQty = 3,
                barcode = "789",
                barFactor = 1,
                discountType = "NONE",
                noOfExp = 0,
                productName = "No Exp",
                qty = 10,
                unitName = "PCS",
                skuCode = "SKU789",
                expList = null,
            )

        val result = mapper.toUIModel(item)

        assertNotNull(result.expList)
        assertTrue(result.expList.isEmpty())
    }

    @Test
    fun `toUIModel should handle item with negative actualQty`() {
        val item =
            SdToteDetailItem(
                actualQty = -1,
                barcode = "NEG",
                barFactor = 1,
                discountType = "NONE",
                noOfExp = 0,
                productName = "Negative",
                qty = 5,
                unitName = "PCS",
                skuCode = "SKUNEG",
                expList = null,
            )
        val data =
            SdToteDetailData(
                items = mutableListOf(item),
                sdNo = "SDX",
                toteCode = "TOTE_X",
                createdDate = sdf.parse("2025-10-01")!!,
                receivedDate = sdf.parse("2025-10-01")!!,
            )

        val result = mapper.toUIModel(data)

        assertTrue(result.items.isEmpty())
    }

    @Test
    fun `toUIModel should handle item with actualQty 0 and qty 0`() {
        val item =
            SdToteDetailItem(
                actualQty = 0,
                barcode = "ZERO",
                barFactor = 1,
                discountType = "NONE",
                noOfExp = 0,
                productName = "Zero Case",
                qty = 0,
                unitName = "PCS",
                skuCode = "SKUZERO",
                expList = null,
            )
        val data =
            SdToteDetailData(
                items = mutableListOf(item),
                sdNo = "SDX",
                toteCode = "TOTE_X",
                createdDate = sdf.parse("2025-10-01")!!,
                receivedDate = sdf.parse("2025-10-01")!!,
            )

        val result = mapper.toUIModel(data)

        assertEquals(1, result.items.size)
        assertEquals("ZERO", result.items.first().barcode)
    }

    @Test
    fun `toUIModel should handle empty expList`() {
        val item =
            SdToteDetailItem(
                actualQty = 2,
                barcode = "EMPTY_EXP",
                barFactor = 1,
                discountType = "X",
                noOfExp = 0,
                productName = "Empty ExpList",
                qty = 5,
                unitName = "PCS",
                skuCode = "SKUEMPTY_EXP",
                expList = emptyList(),
            )

        val result = mapper.toUIModel(item)
        assertNotNull(result.expList)
        assertTrue(result.expList.isEmpty())
    }

    @Test
    fun `toUIModel should correctly group and sort SdToteDetailItems`() {
        val data =
            SdToteDetailData(
                sdNo = "SD001",
                toteCode = "TOTE001",
                createdDate = sdf.parse("2025-10-01")!!,
                receivedDate = sdf.parse("2025-10-01")!!,
                items =
                    mutableListOf(
                        SdToteDetailItem(
                            actualQty = 0,
                            barcode = "003",
                            barFactor = 1,
                            discountType = "X",
                            noOfExp = 0,
                            productName = "Product3",
                            qty = 5,
                            unitName = "PCS",
                            skuCode = "SKU003",
                            expList = null,
                        ),
                        SdToteDetailItem(
                            actualQty = 2,
                            barcode = "002",
                            barFactor = 1,
                            discountType = "X",
                            noOfExp = 0,
                            productName = "Product2",
                            qty = 5,
                            unitName = "PCS",
                            skuCode = "SKU002",
                            expList = null,
                        ),
                        SdToteDetailItem(
                            actualQty = 6,
                            barcode = "004",
                            barFactor = 1,
                            discountType = "X",
                            noOfExp = 0,
                            productName = "Product4",
                            qty = 5,
                            unitName = "PCS",
                            skuCode = "SKU004",
                            expList = null,
                        ),
                        SdToteDetailItem(
                            actualQty = 5,
                            barcode = "001",
                            barFactor = 1,
                            discountType = "X",
                            noOfExp = 0,
                            productName = "Product1",
                            qty = 5,
                            unitName = "PCS",
                            skuCode = "SKU001",
                            expList = null,
                        ),
                    ),
            )

        val result = SdToteDocDetailsUIMapper().toUIModel(data)

        val expectedBarcodeOrder = listOf("003", "002", "004", "001")
        assertEquals(expectedBarcodeOrder, result.items.map { it.barcode })
        assertEquals("SD001", result.sdNo)
        assertEquals("TOTE001", result.toteCode)
        assertEquals(data.receivedDate, result.receivedDate)
    }

    @Test
    fun `toUIModel with null expList should fallback to empty state list`() {
        val item =
            SdToteDetailItem(
                actualQty = 5,
                barcode = "NOEXP",
                barFactor = 1,
                discountType = "NONE",
                noOfExp = 0,
                productName = "No Exp Product",
                qty = 5,
                unitName = "PCS",
                skuCode = "SKUNOEXP",
                expList = null,
            )

        val result = mapper.toUIModel(item)

        assertNotNull(result.expList)
        assertTrue(result.expList.isEmpty())
    }

    @Test
    fun `toUIModel with empty expList should return empty mapped list`() {
        val item =
            SdToteDetailItem(
                actualQty = 5,
                barcode = "EMPTYEXP",
                barFactor = 1,
                discountType = "NONE",
                noOfExp = 0,
                productName = "Empty Exp Product",
                qty = 5,
                unitName = "PCS",
                skuCode = "SKUEMPTYEXP",
                expList = emptyList(),
            )

        val result = mapper.toUIModel(item)

        assertNotNull(result.expList)
        assertEquals(0, result.expList.size)
    }

    @Test
    fun `toUIModel with non-empty expList should map to expiry UI list`() {
        val exp1 =
            SdToteDetailItemExpiryDateLot(
                expDate = sdf.parse("2025-12-31"),
                actualQty = 2,
                qty = 5,
                lotNo = "L999",
            )

        val item =
            SdToteDetailItem(
                actualQty = 5,
                barcode = "FULL",
                barFactor = 1,
                discountType = "DISC",
                noOfExp = 1,
                productName = "Full Product",
                qty = 5,
                unitName = "PCS",
                skuCode = "SKUFULL",
                expList = listOf(exp1),
            )

        val result = mapper.toUIModel(item)

        assertEquals(1, result.expList.size)
        assertEquals("L999", result.expList.first().lotNo)
    }
}
