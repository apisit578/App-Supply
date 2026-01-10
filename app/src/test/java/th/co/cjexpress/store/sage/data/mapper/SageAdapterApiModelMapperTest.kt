package th.co.cjexpress.store.sage.data.mapper

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceCreateMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceDetailMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceDocumentMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceDraftMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseInvoiceValidateBillMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseOrderDetailMapper
import th.co.cjexpress.store.sage.data.mapper.purchaseInvoice.PurchaseOrderDocumentMapper
import th.co.cjexpress.store.sage.data.model.orderReceive.BarcodeDataResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.DocumentSDResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.DocumentSDResponseData
import th.co.cjexpress.store.sage.data.model.orderReceive.OrderReceiveSummaryResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.OrderReceiveSummaryResponseData
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentResponseData
import th.co.cjexpress.store.sage.data.model.orderReceive.SkuBarcodeDataResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.SkuCheckResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDocumentListDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceValidateBillDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDocumentListDTOResponse
import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.SkuCheckModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceValidateBillModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentListModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class SageAdapterApiModelMapperTest : BaseUnitTest() {
    private lateinit var sageAdapterApiModelMapper: SageAdapterApiModelMapper

    override fun setUp() {
        sageAdapterApiModelMapper = SageAdapterApiModelMapper()
    }

    @Test
    fun test_shipmentDocumentMapper() {
        // given
        val respMock = mockk<ShipmentDocumentResponse>(relaxed = true)
        val respDataMock = mockk<ShipmentResponseData>(relaxed = true)
        every { respMock.data } returns respDataMock
        // when
        val result = sageAdapterApiModelMapper.shipmentDocumentMapper.map(respMock)
        // then
        assertThat(result, isA(ShipmentDocumentModel::class.java))
    }

    @Test
    fun test_skuCheckMapper() {
        // given
        val respMock = mockk<SkuCheckResponse>(relaxed = true)
        val respDataMock = mockk<SkuBarcodeDataResponse>(relaxed = true)
        val respBarcodeDataMock = mockk<BarcodeDataResponse>(relaxed = true)
        every { respMock.data } returns respDataMock
        every { respDataMock.barcodes } returns listOf(respBarcodeDataMock)
        // when
        val result = sageAdapterApiModelMapper.skuCheckMapper.map(respMock)
        // then
        assertThat(result, isA(SkuCheckModel::class.java))
    }

    @Test
    fun test_documentSDMapper() {
        // given
        val respMock = mockk<DocumentSDResponse>(relaxed = true)
        val respDataMock = mockk<List<DocumentSDResponseData>>(relaxed = true)
        every { respMock.data } returns respDataMock
        // when
        val result = sageAdapterApiModelMapper.documentSDMapper.map(respMock)
        // then
        assertThat(result, isA(DocumentSDModel::class.java))
    }

    @Test
    fun test_OrderReceiveSummaryMapper() {
        // given
        val respMock = mockk<OrderReceiveSummaryResponse>(relaxed = true)
        val respDataMock = mockk<OrderReceiveSummaryResponseData>(relaxed = true)
        every { respMock.data } returns respDataMock
        // when
        val result = sageAdapterApiModelMapper.summaryMapper.map(respMock)
        // then
        assertThat(result, isA(OrderReceiveSummaryModel::class.java))
    }

    @Test
    fun test_purchaseInvoiceSupplierMapper() {
        // given
        val respMock = mockk<PurchaseInvoiceSupplierDTOResponse>(relaxed = true)

        // when
        val result = sageAdapterApiModelMapper.purchaseInvoiceSupplierMapper.map(respMock)
        // then
        assertThat(result, isA(PurchaseInvoiceSupplierModel::class.java))
    }

    @Test
    fun test_purchaseInvoiceDocumentMapper() {
        // given
        val respMock = mockk<PurchaseInvoiceDocumentListDTOResponse>(relaxed = true)
        every { respMock.data } returns listOf(mockk(relaxed = true))
        // when
        val result = PurchaseInvoiceDocumentMapper().map(respMock)
        // then
        assertThat(result, isA(PurchaseInvoiceDocumentListModel::class.java))
    }

    @Test
    fun test_purchaseOrderDocumentMapper() {
        // given
        val respMock = mockk<PurchaseOrderDocumentListDTOResponse>(relaxed = true)
        every { respMock.data } returns listOf(mockk(relaxed = true))
        // when
        val result = PurchaseOrderDocumentMapper().map(respMock)
        // then
        assertThat(result, isA(PurchaseOrderDocumentListModel::class.java))
    }

    @Test
    fun test_purchaseInvoiceDraftMapper() {
        // given
        val respMock = mockk<PurchaseInvoiceDraftDTOResponse>(relaxed = true)
        every { respMock.data } returns mockk(relaxed = true)
        // when
        val result = PurchaseInvoiceDraftMapper().map(respMock)
        // then
        assertThat(result, isA(PurchaseInvoiceDraftModel::class.java))
    }

    @Test
    fun test_purchaseInvoiceCreateMapper() {
        // given
        val respMock = mockk<PurchaseInvoiceCreateDTOResponse>(relaxed = true)
        every { respMock.data } returns mockk(relaxed = true)
        // when
        val result = PurchaseInvoiceCreateMapper().map(respMock)
        // then
        assertThat(result, isA(PurchaseInvoiceCreateModel::class.java))
    }

    @Test
    fun test_purchaseInvoiceDetailMapper() {
        // given
        val respMock = mockk<PurchaseInvoiceDetailDTOResponse>(relaxed = true)
        every { respMock.data } returns mockk(relaxed = true)
        // when
        val result = PurchaseInvoiceDetailMapper().map(respMock)
        // then
        assertThat(result, isA(PurchaseInvoiceDetailModel::class.java))
    }

    @Test
    fun test_purchaseInvoiceValidateBillMapper() {
        // given
        val respMock = mockk<PurchaseInvoiceValidateBillDTOResponse>(relaxed = true)
        every { respMock.data } returns mockk(relaxed = true)
        // when
        val result = PurchaseInvoiceValidateBillMapper().map(respMock)
        // then
        assertThat(result, isA(PurchaseInvoiceValidateBillModel::class.java))
    }

    @Test
    fun test_purchaseOrderDetailMapper() {
        // given
        val respMock = mockk<PurchaseOrderDetailDTOResponse>(relaxed = true)
        every { respMock.data } returns mockk(relaxed = true)
        // when
        val result = PurchaseOrderDetailMapper().map(respMock)
        // then
        assertThat(result, isA(PurchaseOrderDetailModel::class.java))
    }
}
