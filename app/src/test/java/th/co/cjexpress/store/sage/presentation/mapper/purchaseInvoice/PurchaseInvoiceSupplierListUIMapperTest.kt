package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import junit.framework.TestCase.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierListModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class PurchaseInvoiceSupplierListUIMapperTest : BaseUnitTest() {
    private lateinit var purchaseInvoiceSupplierListUIMapper: PurchaseInvoiceSupplierListUIMapper

    override fun setUp() {
        purchaseInvoiceSupplierListUIMapper = PurchaseInvoiceSupplierListUIMapper()
    }

    @Test
    fun test_map_model_to_purchaseInvoiceSupplierListUIMapper() {
        val model =
            PurchaseInvoiceSupplierListModel(
                code = "SUCCESS",
                message = "Success",
                ref = "000000001",
                timestamp = "",
                data =
                    listOf(
                        PurchaseInvoiceSupplierDetailModel(
                            supplierCode = "SUP001",
                            supplierName = "CJ Supplier 1",
                            supplierIsRefPO = true,
                        ),
                        PurchaseInvoiceSupplierDetailModel(
                            supplierCode = "SUP002",
                            supplierName = "CJ Supplier 2",
                            supplierIsRefPO = true,
                        ),
                    ),
            )

        // ---------- When ----------
        val uiModel = purchaseInvoiceSupplierListUIMapper.toUIModel(model)

        // ---------- Then ----------
        assertEquals(2, uiModel.supplierList.size)

        assertEquals("SUP001", uiModel.supplierList[0].supplierCode)
        assertEquals("CJ Supplier 1", uiModel.supplierList[0].supplierName)

        assertEquals("SUP002", uiModel.supplierList[1].supplierCode)
        assertEquals("CJ Supplier 2", uiModel.supplierList[1].supplierName)
    }
}
