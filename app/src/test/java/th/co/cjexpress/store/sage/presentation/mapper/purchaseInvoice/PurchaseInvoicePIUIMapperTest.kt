package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateEntryModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceEntryModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentItemModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceItemPIUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDocumentPOUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat
import java.util.Date

class PurchaseInvoicePIUIMapperTest : BaseUnitTest() {
    private lateinit var purchaseInvoicePIUIMapper: PurchaseInvoicePIUIMapper

    override fun setUp() {
        purchaseInvoicePIUIMapper = PurchaseInvoicePIUIMapper()
    }

    @Test
    fun `mapDocumentListToItemsPIUIModel mapping test`() =
        runTest {
            // Arrange
            val result: List<PurchaseInvoiceItemPIUIModel>
            val piNo = "PI2506M04C"
            val poNo = "PO123456789"
            val createdDate = Date()
            val expectedDateResult = createdDate.toStringDDMMYYYYFormat()
            val mockPurchaseInvoiceDocumentList =
                PurchaseInvoiceDocumentListModel(
                    code = "SUCCESS",
                    ref = "mocked-ref-001",
                    message = "Data fetched successfully",
                    timestamp = "2025-06-17T17:29:40.16627+07:00",
                    data =
                        listOf(
                            PurchaseInvoiceDocumentItemModel(
                                piNo = piNo,
                                poNo = poNo,
                                billNo = "INV123456",
                                createdDate = createdDate,
                                docDate = Date(),
                                dueDate = Date(),
                                shipmentDate = Date(),
                            ),
                        ),
                )

            // Act
            val mappingResult =
                purchaseInvoicePIUIMapper.mapDocumentListToItemsPIUIModel(
                    purchaseInvoiceDocumentListModel = mockPurchaseInvoiceDocumentList,
                )
            result = mappingResult

            // Assert
            assertEquals(1, result.size)
            assertEquals(piNo, result.first().piNo)
            assertEquals(poNo, result.first().poNo)
            assertEquals(expectedDateResult, result.first().createdDate)
        }

    @Test
    fun `mapOrderDocumentItemsToSupplierDocumentsPOUI mapping test`() =
        runTest {
            // Arrange
            val result: List<PurchaseInvoiceSupplierDocumentPOUIModel>
            val mockPurchaseOrderDocumentItemsModel =
                listOf(
                    PurchaseOrderDocumentItemModel(
                        docNo = "PO123456789",
                        shipmentDate = "1/01/1997",
                        grandTotalAmount = 12500.75,
                    ),
                )

            // Act
            val mappingResult =
                purchaseInvoicePIUIMapper.mapOrderDocumentItemsToSupplierDocumentsPOUI(
                    purchaseOrderDocumentItemsModel = mockPurchaseOrderDocumentItemsModel,
                )
            result = mappingResult

            // Assert
            assertEquals(1, result.size)
            assertEquals("PO123456789", result.first().docNo)
            assertEquals("1/01/1997", result.first().shipmentDate)
            assertEquals(12500.75F, result.first().grandTotalAmount.toFloat())
        }

    @Test
    fun `mapPurchaseInvoiceDetailToUIModel should correctly map detail model to UI model`() =
        runTest {
            // Arrange
            val entryModel =
                PurchaseInvoiceEntryModel(
                    isCalVat = true,
                    barcode = "1234567890123",
                    productName = "ปลากระพงนึ่งมะนาว",
                    unitName = "ชิ้น",
                    unitFactor = 1,
                    qty = 10,
                    salePrice = 99.99,
                    actualQty = 8,
                )

            val detailModel =
                PurchaseInvoiceDetailModel(
                    poNo = "PO123456789",
                    docDate = Date(),
                    supplierCode = "SUP001",
                    supplierName = "บริษัท โอ๋ จำกัด",
                    dueDate = Date(),
                    shipmentDate = Date(),
                    totalAmount = 9999.0,
                    vat = 700.0,
                    vatRate = 7.0,
                    roundAmount = 0,
                    createdDate = Date(),
                    piStatus = "DRAFT",
                    piNo = "PI987654321",
                    billNo = "BILL0001",
                    entries = listOf(entryModel),
                    code = "SUCCESS",
                    ref = "REF001",
                    message = "OK",
                    timestamp = "2025-07-21T09:00:00+07:00",
                )

            // Act
            val result = purchaseInvoicePIUIMapper.mapPurchaseInvoiceDetailToUIModel(detailModel)

            // Assert
            assertEquals("SUP001", result.supplierCode)
            assertEquals("บริษัท โอ๋ จำกัด", result.supplierName)
            assertEquals("PO123456789", result.poNo)
            assertEquals("PI987654321", result.piNo)
            assertEquals("BILL0001", result.billNo)

            assertEquals(1, result.entries.size)
            val entry = result.entries.first()
            assertEquals("1234567890123", entry.barcode)
            assertEquals("ปลากระพงนึ่งมะนาว", entry.productName)
            assertEquals("ชิ้น", entry.unitName)
            assertEquals(1, entry.unitFactor)
            assertEquals(10, entry.qty)
            assertEquals(99.99, entry.salePrice, 0.01)
            assertEquals(8, entry.actualQty)
        }

    @Test
    fun `mapPurchaseInvoiceCreateModelToUIModel should map full model correctly`() =
        runTest {
            // Arrange
            val now = Date()

            val entry =
                PurchaseInvoiceCreateEntryModel(
                    actualQty = 5,
                    barcode = "1234567890000",
                    isCalVat = true,
                    productName = "สินค้าโอทอบระดับตำบล",
                    qty = 6,
                    salePrice = 120.0,
                    setPrice = 110.0,
                    sumPrice = 720.0,
                    unitFactor = 1,
                    unitName = "ชิ้น",
                    hhTimestamp = 0L,
                )

            val model =
                PurchaseInvoiceCreateModel(
                    code = "SUCCESS",
                    ref = "REF999",
                    message = "SUCCESS",
                    billNo = "BILL123",
                    createdDate = now,
                    docDate = now,
                    dueDate = now,
                    entries = listOf(entry),
                    piNo = "PI999",
                    piStatus = "DRAFT",
                    poNo = "PO999",
                    roundAmount = 0.0,
                    shipmentDate = now,
                    supplierCode = "SUP999",
                    supplierName = "บริษัทจำลอง ถ้าไม่ลองจะรู้ได้ไง จำกัด",
                    totalAmount = 720.0,
                    vat = 50.4,
                    vatRate = 7.0,
                    timestamp = "2025-07-21T17:00:00+07:00",
                )

            // Act
            val result = purchaseInvoicePIUIMapper.mapPurchaseInvoiceCreateModelToUIModel(model)

            // Assert
            assertEquals("SUP999", result.supplierCode)
            assertEquals("บริษัทจำลอง ถ้าไม่ลองจะรู้ได้ไง จำกัด", result.supplierName)
            assertEquals("PO999", result.poNo)
            assertEquals("PI999", result.piNo)
            assertEquals("BILL123", result.billNo)

            assertEquals(1, result.entries.size)
            val mappedEntry = result.entries.first()
            assertEquals("1234567890000", mappedEntry.barcode)
            assertEquals("สินค้าโอทอบระดับตำบล", mappedEntry.productName)
            assertEquals("ชิ้น", mappedEntry.unitName)
            assertEquals(1, mappedEntry.unitFactor)
            assertEquals(6, mappedEntry.qty)
            assertEquals(120.0, mappedEntry.salePrice, 0.01)
            assertEquals(5, mappedEntry.actualQty)
        }

    @Test
    fun `mapPurchaseInvoiceDetailToUIModel should sort entries by priority groups and barcode`() =
        runTest {
            // Arrange
            val entries =
                listOf(
                    PurchaseInvoiceEntryModel(
                        isCalVat = true,
                        barcode = "B001",
                        productName = "Product B",
                        unitName = "PCS",
                        unitFactor = 1,
                        qty = 10,
                        salePrice = 100.0,
                        actualQty = 5, // insufficient (priority 1)
                    ),
                    PurchaseInvoiceEntryModel(
                        isCalVat = true,
                        barcode = "A001",
                        productName = "Product A",
                        unitName = "PCS",
                        unitFactor = 1,
                        qty = 10,
                        salePrice = 100.0,
                        actualQty = 0, // not received (priority 0)
                    ),
                    PurchaseInvoiceEntryModel(
                        isCalVat = true,
                        barcode = "D001",
                        productName = "Product D",
                        unitName = "PCS",
                        unitFactor = 1,
                        qty = 10,
                        salePrice = 100.0,
                        actualQty = 10, // complete (priority 3)
                    ),
                    PurchaseInvoiceEntryModel(
                        isCalVat = true,
                        barcode = "C001",
                        productName = "Product C",
                        unitName = "PCS",
                        unitFactor = 1,
                        qty = 10,
                        salePrice = 100.0,
                        actualQty = 15, // over (priority 2)
                    ),
                    PurchaseInvoiceEntryModel(
                        isCalVat = true,
                        barcode = "A002",
                        productName = "Product A2",
                        unitName = "PCS",
                        unitFactor = 1,
                        qty = 5,
                        salePrice = 50.0,
                        actualQty = 0, // not received (priority 0)
                    ),
                )

            val detailModel =
                PurchaseInvoiceDetailModel(
                    poNo = "PO123",
                    docDate = Date(),
                    supplierCode = "SUP001",
                    supplierName = "Test Supplier",
                    dueDate = Date(),
                    shipmentDate = Date(),
                    totalAmount = 1000.0,
                    vat = 70.0,
                    vatRate = 7.0,
                    roundAmount = 0,
                    createdDate = Date(),
                    piStatus = "DRAFT",
                    piNo = "PI123",
                    billNo = "BILL123",
                    entries = entries,
                    code = "SUCCESS",
                    ref = "REF001",
                    message = "OK",
                    timestamp = "2025-07-21T09:00:00+07:00",
                )

            // Act
            val result = purchaseInvoicePIUIMapper.mapPurchaseInvoiceDetailToUIModel(detailModel)

            // Assert
            assertEquals(5, result.entries.size)

            // Check order: actualQty = 0 (priority 0) sorted by barcode
            assertEquals("A001", result.entries[0].barcode)
            assertEquals(0, result.entries[0].actualQty)
            assertEquals("A002", result.entries[1].barcode)
            assertEquals(0, result.entries[1].actualQty)

            // Check order: actualQty < qty (priority 1)
            assertEquals("B001", result.entries[2].barcode)
            assertEquals(5, result.entries[2].actualQty)

            // Check order: actualQty > qty (priority 2)
            assertEquals("C001", result.entries[3].barcode)
            assertEquals(15, result.entries[3].actualQty)

            // Check order: actualQty == qty (priority 3)
            assertEquals("D001", result.entries[4].barcode)
            assertEquals(10, result.entries[4].actualQty)
        }

    @Test
    fun `mapPurchaseInvoiceCreateModelToUIModel should sort entries by priority groups and barcode`() =
        runTest {
            // Arrange
            val entries =
                listOf(
                    PurchaseInvoiceCreateEntryModel(
                        actualQty = 3,
                        barcode = "Z001",
                        isCalVat = true,
                        productName = "Product Z",
                        qty = 10,
                        salePrice = 100.0,
                        setPrice = 100.0,
                        sumPrice = 300.0,
                        unitFactor = 1,
                        unitName = "PCS",
                        hhTimestamp = 0L,
                    ), // insufficient (priority 1)
                    PurchaseInvoiceCreateEntryModel(
                        actualQty = 0,
                        barcode = "A001",
                        isCalVat = true,
                        productName = "Product A",
                        qty = 10,
                        salePrice = 100.0,
                        setPrice = 100.0,
                        sumPrice = 0.0,
                        unitFactor = 1,
                        unitName = "PCS",
                        hhTimestamp = 0L,
                    ), // not received (priority 0)
                    PurchaseInvoiceCreateEntryModel(
                        actualQty = 15,
                        barcode = "B001",
                        isCalVat = true,
                        productName = "Product B",
                        qty = 10,
                        salePrice = 100.0,
                        setPrice = 100.0,
                        sumPrice = 1500.0,
                        unitFactor = 1,
                        unitName = "PCS",
                        hhTimestamp = 0L,
                    ), // over (priority 2)
                    PurchaseInvoiceCreateEntryModel(
                        actualQty = 10,
                        barcode = "C001",
                        isCalVat = true,
                        productName = "Product C",
                        qty = 10,
                        salePrice = 100.0,
                        setPrice = 100.0,
                        sumPrice = 1000.0,
                        unitFactor = 1,
                        unitName = "PCS",
                        hhTimestamp = 0L,
                    ), // complete (priority 3)
                )

            val model =
                PurchaseInvoiceCreateModel(
                    code = "SUCCESS",
                    ref = "REF001",
                    message = "SUCCESS",
                    billNo = "BILL123",
                    createdDate = Date(),
                    docDate = Date(),
                    dueDate = Date(),
                    entries = entries,
                    piNo = "PI123",
                    piStatus = "DRAFT",
                    poNo = "PO123",
                    roundAmount = 0.0,
                    shipmentDate = Date(),
                    supplierCode = "SUP001",
                    supplierName = "Test Supplier",
                    totalAmount = 2800.0,
                    vat = 196.0,
                    vatRate = 7.0,
                    timestamp = "2025-07-21T17:00:00+07:00",
                )

            // Act
            val result = purchaseInvoicePIUIMapper.mapPurchaseInvoiceCreateModelToUIModel(model)

            // Assert
            assertEquals(4, result.entries.size)

            // Check order: actualQty = 0 (priority 0)
            assertEquals("A001", result.entries[0].barcode)
            assertEquals(0, result.entries[0].actualQty)

            // Check order: actualQty < qty (priority 1)
            assertEquals("Z001", result.entries[1].barcode)
            assertEquals(3, result.entries[1].actualQty)

            // Check order: actualQty > qty (priority 2)
            assertEquals("B001", result.entries[2].barcode)
            assertEquals(15, result.entries[2].actualQty)

            // Check order: actualQty == qty (priority 3)
            assertEquals("C001", result.entries[3].barcode)
            assertEquals(10, result.entries[3].actualQty)
        }
}
