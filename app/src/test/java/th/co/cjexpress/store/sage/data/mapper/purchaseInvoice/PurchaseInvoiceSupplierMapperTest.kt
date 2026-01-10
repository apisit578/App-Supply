package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.isA
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierListDTOResponse
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class PurchaseInvoiceSupplierMapperTest : BaseUnitTest() {
    lateinit var purchaseInvoiceSupplierMapper: PurchaseInvoiceSupplierMapper

    override fun setUp() {
        super.setUp()
        purchaseInvoiceSupplierMapper = PurchaseInvoiceSupplierMapper()
    }

    @Test
    fun test_mapper_purchase_invoice_list_supplier_response() {
        // given
        val respMock =
            PurchaseInvoiceSupplierListDTOResponse(
                ref = "123",
                code = "200",
                message = "Success",
                timestamp = "20-01-2658",
                data =
                    listOf(
                        PurchaseInvoiceSupplierDetailDTOResponse(
                            supplierCode = "SUP123",
                            supplierName = "Supplier Name",
                            supplierIsRefPO = true,
                        ),
                    ),
            )
        // when
        val result = purchaseInvoiceSupplierMapper.mapList(respMock)
        // then
        assertThat(result, isA(PurchaseInvoiceSupplierListModel::class.java))
    }

    @Test
    fun test_mapper_purchase_invoice_supplier_response() {
        // given
        val respMock =
            PurchaseInvoiceSupplierDTOResponse(
                ref = "123",
                code = "200",
                message = "Success",
                timestamp = "20-01-2658",
                data =
                    PurchaseInvoiceSupplierDetailDTOResponse(
                        supplierCode = "SUP123",
                        supplierName = "Supplier Name",
                        supplierIsRefPO = true,
                    ),
            )
        // when
        val result = purchaseInvoiceSupplierMapper.map(respMock)
        // then
        assertThat(result, isA(PurchaseInvoiceSupplierModel::class.java))
    }
}
