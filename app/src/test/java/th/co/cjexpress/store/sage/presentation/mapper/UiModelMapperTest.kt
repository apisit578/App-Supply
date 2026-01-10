package th.co.cjexpress.store.sage.presentation.mapper

import junit.framework.TestCase.assertNotNull
import org.junit.Test
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class UiModelMapperTest : BaseUnitTest() {
    private lateinit var mapper: UiModelMapper

    override fun setUp() {
        mapper = UiModelMapper()
    }

    @Test
    fun `all mappers should be initialized`() {
        assertNotNull(mapper.sdToteExpMapper)
        assertNotNull(mapper.sdToteDocDetailUIMapper)
        assertNotNull(mapper.sdToteDiscountsConfigUIMapper)
        assertNotNull(mapper.barcodeCodecMapper)
        assertNotNull(mapper.shipmentDocumentUIMapper)
        assertNotNull(mapper.shipmentDocumentListUIMapper)
        assertNotNull(mapper.orderReceiveSummaryUIMapper)
        assertNotNull(mapper.shipmentDocumentByBarcodeUIMapper)
        assertNotNull(mapper.purchaseInvoiceSupplierListUIMapper)
        assertNotNull(mapper.purchaseInvoiceSupplierUIMapper)
        assertNotNull(mapper.purchaseInvoicePIUIMapper)
        assertNotNull(mapper.purchaseInvoicePOUIMapper)
    }
}
