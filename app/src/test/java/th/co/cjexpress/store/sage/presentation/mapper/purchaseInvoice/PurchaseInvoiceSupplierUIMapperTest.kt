package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class PurchaseInvoiceSupplierUIMapperTest : BaseUnitTest() {
    private lateinit var purchaseInvoiceSupplierUIMapper: PurchaseInvoiceSupplierUIMapper

    override fun setUp() {
        purchaseInvoiceSupplierUIMapper = PurchaseInvoiceSupplierUIMapper()
    }

    @Test
    fun test_map_model_to_purchaseInvoiceSupplierUIMapper() {
        // given
        val supplierCode = "SUP001"
        val supplierName = "CJ Supplier"
        val isRefPO = true

        val detailModel = mockk<PurchaseInvoiceSupplierDetailModel>()
        every { detailModel.supplierCode } returns supplierCode
        every { detailModel.supplierName } returns supplierName
        every { detailModel.supplierIsRefPO } returns isRefPO

        val model = mockk<PurchaseInvoiceSupplierModel>()
        every { model.data } returns detailModel

        // when
        val uiModel = purchaseInvoiceSupplierUIMapper.toUIModel(model)

        // then
        assertEquals(supplierCode, uiModel.supplier.supplierCode)
        assertEquals(supplierName, uiModel.supplier.supplierName)
        assertEquals(isRefPO, uiModel.supplier.isRefPo)
    }
}
