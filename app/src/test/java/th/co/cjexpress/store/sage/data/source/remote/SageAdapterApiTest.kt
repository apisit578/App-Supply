package th.co.cjexpress.store.sage.data.source.remote

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_CREATE
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_DRAFT
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_FILTER_SUPPLIER
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_GET_DETAIL
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_GET_FREQUENCY_SUPPLIER
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_GET_PURCHASE_INVOICE
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DETAIL
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DOCUMENT
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_GET_SUPPLIER_BY_CODE
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_VALIDATE_BILL
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_DOCUMENT
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_DOCUMENT_BY_BARCODE
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_DOCUMENT_OPEN
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_DOCUMENT_SUMMARY
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_QR_DOCUMENT_SUBMIT
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SKU_CHECK
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.model.orderReceive.DocumentSDResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.OrderReceiveSummaryResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcodeRequest
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcodeResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentOpenRequest
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.SkuCheckRequest
import th.co.cjexpress.store.sage.data.model.orderReceive.SkuCheckResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDocumentListDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftRequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierListDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceValidateBillDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDocumentListDTOResponse
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.request.QrDocumentSubmitDTORequest
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.request.QrDocumentSubmitData
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.response.QrDocumentSubmitDTOResponse
import th.co.cjexpress.store.sage.data.network.RestfulClient
import th.co.cjexpress.store.sage.data.network.RestfulResponse
import th.co.cjexpress.store.sage.data.network.getRestfulClient
import th.co.cjexpress.store.sage.di.AppDependencyProvider
import th.co.cjexpress.store.sage.di.dependencie.AppEntryPoint
import th.co.cjexpress.store.sage.utils.JsonUtils

class SageAdapterApiTest {
    @RelaxedMockK
    lateinit var appConfig: AppConfig

    @RelaxedMockK
    lateinit var jsonUtils: JsonUtils

    private lateinit var sageAdapterApi: SageAdapterApi

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic("th.co.cjexpress.store.sage.data.network.RestfulUtilsKt")
        mockkObject(AppDependencyProvider)
        every { AppDependencyProvider.getProviderBuilder().getJsonUtils() } returns jsonUtils
        every { jsonUtils.toJson(any()) } returns ""

        sageAdapterApi = SageAdapterApi(appConfig)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_skuCheck() =
        runTest {
            // given
            val restfulClientMock = mockk<RestfulClient<SkuCheckResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<SkuCheckResponse>>(relaxed = true)
            every { restfulClientMock.requestPost() } returns restfulResponseMock
            every {
                getRestfulClient<SkuCheckResponse>(
                    serviceName = SERVICE_SKU_CHECK,
                    header = any(),
                    timeout = any(),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            SkuCheckRequest(
                                barcode = "test_barcode",
                            ),
                        ),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.skuCheck("test_token", "", "test_barcode")
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_skuCheck_when_service_config_null() =
        runTest {
            // given
            val restfulClientMock = mockk<RestfulClient<SkuCheckResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<SkuCheckResponse>>(relaxed = true)
            every { appConfig.getServiceConfig(any()) } returns null
            every { restfulClientMock.requestPost() } returns restfulResponseMock
            every {
                getRestfulClient<SkuCheckResponse>(
                    serviceName = SERVICE_SKU_CHECK,
                    header = any(),
                    timeout = any(),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            SkuCheckRequest(
                                barcode = "test_barcode",
                            ),
                        ),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.skuCheck("test_token", "", "test_barcode")
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getSdDocList() =
        runTest {
            // given
            val restfulClientMock = mockk<RestfulClient<DocumentSDResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<DocumentSDResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every {
                getRestfulClient<DocumentSDResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT,
                    header = any(),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.getSdDocList("test_token", "")
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getSdDocList_when_service_config_null() =
        runTest {
            // given
            val restfulClientMock = mockk<RestfulClient<DocumentSDResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<DocumentSDResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every { appConfig.getServiceConfig(any()) } returns null
            every {
                getRestfulClient<DocumentSDResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT,
                    header = any(),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.getSdDocList("test_token", "")
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getShipmentDocumentBySDNo() =
        runTest {
            // given
            val sdNo = "SD123456"
            val restfulClientMock = mockk<RestfulClient<ShipmentDocumentResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<ShipmentDocumentResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every {
                getRestfulClient<ShipmentDocumentResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT,
                    header = any(),
                    timeout = any(),
                    queryString = "/$sdNo",
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.getShipmentDocumentBySDNo("test_token", "", sdNo)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getShipmentDocumentBySDNo_when_service_config_null() =
        runTest {
            // given
            val sdNo = "SD123456"
            val restfulClientMock = mockk<RestfulClient<ShipmentDocumentResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<ShipmentDocumentResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every { appConfig.getServiceConfig(any()) } returns null
            every {
                getRestfulClient<ShipmentDocumentResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT,
                    header = any(),
                    timeout = any(),
                    queryString = "/$sdNo",
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.getShipmentDocumentBySDNo("test_token", "", sdNo)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_shipmentDocumentOpen() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            val restfulClientMock = mockk<RestfulClient<ShipmentDocumentResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<ShipmentDocumentResponse>>(relaxed = true)
            every { restfulClientMock.requestPost() } returns restfulResponseMock
            every {
                getRestfulClient<ShipmentDocumentResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT_OPEN,
                    header = any(),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            ShipmentDocumentOpenRequest(
                                shipmentNo = ldNo,
                            ),
                        ),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.shipmentDocumentOpen("test_token", "", ldNo)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_shipmentDocumentOpen_when_service_config_null() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            val restfulClientMock = mockk<RestfulClient<ShipmentDocumentResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<ShipmentDocumentResponse>>(relaxed = true)
            every { restfulClientMock.requestPost() } returns restfulResponseMock
            every { appConfig.getServiceConfig(any()) } returns null
            every {
                getRestfulClient<ShipmentDocumentResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT_OPEN,
                    header = any(),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            ShipmentDocumentOpenRequest(
                                shipmentNo = ldNo,
                            ),
                        ),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.shipmentDocumentOpen("test_token", "", ldNo)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getShipmentDocumentSummaryBySd() =
        runTest {
            // given
            val sdNo = "SD25052790000001"
            val restfulClientMock = mockk<RestfulClient<OrderReceiveSummaryResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<OrderReceiveSummaryResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every {
                getRestfulClient<OrderReceiveSummaryResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT_SUMMARY,
                    header = any(),
                    pathParams = hashMapOf("sdNo" to sdNo),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.getShipmentDocumentSummaryBySd("test_token", "", sdNo)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getShipmentDocumentSummaryBySd_when_service_config_null() =
        runTest {
            // given
            val sdNo = "SD25052790000001"
            val restfulClientMock = mockk<RestfulClient<OrderReceiveSummaryResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<OrderReceiveSummaryResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every { appConfig.getServiceConfig(any()) } returns null
            every {
                getRestfulClient<OrderReceiveSummaryResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT_SUMMARY,
                    header = any(),
                    pathParams = hashMapOf("sdNo" to sdNo),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.getShipmentDocumentSummaryBySd("test_token", "", sdNo)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_shipmentDocumentByBarcode() =
        runTest {
            // given
            val sdNo = "SD25052790000001"
            val restfulClientMock = mockk<RestfulClient<ShipmentDocumentByBarcodeResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<ShipmentDocumentByBarcodeResponse>>(relaxed = true)
            every { restfulClientMock.requestPatch() } returns restfulResponseMock
            every {
                getRestfulClient<ShipmentDocumentByBarcodeResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT_BY_BARCODE,
                    header = any(),
                    pathParams = hashMapOf("sdNo" to sdNo),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            ShipmentDocumentByBarcodeRequest(listOf()),
                        ),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.shipmentDocumentByBarcode("test_token", "", sdNo, listOf())
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_shipmentDocumentByBarcode_when_service_config_null() =
        runTest {
            // given
            val sdNo = "SD25052790000001"
            val restfulClientMock = mockk<RestfulClient<ShipmentDocumentByBarcodeResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<ShipmentDocumentByBarcodeResponse>>(relaxed = true)
            every { restfulClientMock.requestPatch() } returns restfulResponseMock
            every { appConfig.getServiceConfig(any()) } returns null
            every {
                getRestfulClient<ShipmentDocumentByBarcodeResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT_BY_BARCODE,
                    header = any(),
                    pathParams = hashMapOf("sdNo" to sdNo),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            ShipmentDocumentByBarcodeRequest(listOf()),
                        ),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.shipmentDocumentByBarcode("test_token", "", sdNo, listOf())
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getFrequencySupplier() =
        runTest {
            // given
            val perPage = "20"
            val restfulClientMock = mockk<RestfulClient<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every {
                getRestfulClient<PurchaseInvoiceSupplierListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_FREQUENCY_SUPPLIER,
                    header = any(),
                    queryString = "?perPage=$perPage",
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.getFrequencySupplier("test_token", "", perPage)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getFrequencySupplier_when_service_config_null() =
        runTest {
            // given
            val perPage = "20"
            val restfulClientMock = mockk<RestfulClient<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every { appConfig.getServiceConfig(any()) } returns null
            every {
                getRestfulClient<PurchaseInvoiceSupplierListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_FREQUENCY_SUPPLIER,
                    header = any(),
                    queryString = "?perPage=$perPage",
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.getFrequencySupplier("test_token", "", perPage)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_filterSupplier() =
        runTest {
            // given
            val searchKey = "searchKey"
            val restfulClientMock = mockk<RestfulClient<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every {
                getRestfulClient<PurchaseInvoiceSupplierListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_FILTER_SUPPLIER,
                    header = any(),
                    pathParams = hashMapOf("searchKey" to searchKey),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.filterSupplier("test_token", "", searchKey)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_filterSupplier_when_service_config_null() =
        runTest {
            // given
            val searchKey = "searchKey"
            val restfulClientMock = mockk<RestfulClient<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every { appConfig.getServiceConfig(any()) } returns null
            every {
                getRestfulClient<PurchaseInvoiceSupplierListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_FILTER_SUPPLIER,
                    header = any(),
                    pathParams = hashMapOf("searchKey" to searchKey),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.filterSupplier("test_token", "", searchKey)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getSupplierByCode() =
        runTest {
            // given
            val searchCode = "searchCode"
            val restfulClientMock = mockk<RestfulClient<PurchaseInvoiceSupplierDTOResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<PurchaseInvoiceSupplierDTOResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every {
                getRestfulClient<PurchaseInvoiceSupplierDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_SUPPLIER_BY_CODE,
                    header = any(),
                    pathParams = hashMapOf("searchCode" to searchCode),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.getSupplierByCode("test_token", "", searchCode)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getSupplierByCode_when_service_config_null() =
        runTest {
            // given
            val searchCode = "searchCode"
            val restfulClientMock = mockk<RestfulClient<PurchaseInvoiceSupplierDTOResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<PurchaseInvoiceSupplierDTOResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every { appConfig.getServiceConfig(any()) } returns null
            every {
                getRestfulClient<PurchaseInvoiceSupplierDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_SUPPLIER_BY_CODE,
                    header = any(),
                    pathParams = hashMapOf("searchCode" to searchCode),
                    timeout = any(),
                )
            } returns restfulClientMock
            // when
            val result = sageAdapterApi.getSupplierByCode("test_token", "", searchCode)
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getPurchaseInvoiceDocument() =
        runTest {
            // given
            val token = "test_token"
            val branchCode = "B001"
            val supplierCodes = "S001"
            val timeout = 30
            val restfulClientMock =
                mockk<RestfulClient<PurchaseInvoiceDocumentListDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseInvoiceDocumentListDTOResponse>>(relaxed = true)

            every { appConfig.getServiceConfig(SERVICE_PURCHASE_INVOICE_GET_PURCHASE_INVOICE)?.timeout } returns timeout
            every {
                getRestfulClient<PurchaseInvoiceDocumentListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_PURCHASE_INVOICE,
                    header = any(),
                    timeout = any(),
                    queryString = any(),
                )
            } returns restfulClientMock
            every { restfulClientMock.requestGet() } returns restfulResponseMock

            // when
            val result = sageAdapterApi.getPurchaseInvoiceDocument(token, branchCode, supplierCodes)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_getPurchaseInvoiceDocument_when_service_config_null() =
        runTest {
            // given
            val token = "test_token"
            val branchCode = "B001"
            val supplierCodes = "S001"
            val restfulClientMock =
                mockk<RestfulClient<PurchaseInvoiceDocumentListDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseInvoiceDocumentListDTOResponse>>(relaxed = true)

            every { appConfig.getServiceConfig(SERVICE_PURCHASE_INVOICE_GET_PURCHASE_INVOICE) } returns null
            every {
                getRestfulClient<PurchaseInvoiceDocumentListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_PURCHASE_INVOICE,
                    header = any(),
                    timeout = any(),
                    queryString = any(),
                )
            } returns restfulClientMock
            every { restfulClientMock.requestGet() } returns restfulResponseMock

            // when
            val result = sageAdapterApi.getPurchaseInvoiceDocument(token, branchCode, supplierCodes)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_getPurchaseOrderDocument() =
        runTest {
            // given
            val token = "test_token"
            val branchCode = "B001"
            val supplierCode = "SUP123"
            val timeout = 30
            val restfulClientMock =
                mockk<RestfulClient<PurchaseOrderDocumentListDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseOrderDocumentListDTOResponse>>(relaxed = true)

            every { appConfig.getServiceConfig(SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DOCUMENT)?.timeout } returns timeout
            every {
                getRestfulClient<PurchaseOrderDocumentListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DOCUMENT,
                    header = any(),
                    pathParams = hashMapOf("supplierCode" to supplierCode),
                    timeout = any(),
                )
            } returns restfulClientMock
            every { restfulClientMock.requestGet() } returns restfulResponseMock

            // when
            val result = sageAdapterApi.getPurchaseOrderDocument(token, branchCode, supplierCode)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_getPurchaseOrderDocument_when_service_config_null() =
        runTest {
            // given
            val token = "test_token"
            val branchCode = "B001"
            val supplierCode = "SUP123"
            val restfulClientMock =
                mockk<RestfulClient<PurchaseOrderDocumentListDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseOrderDocumentListDTOResponse>>(relaxed = true)

            every { appConfig.getServiceConfig(SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DOCUMENT) } returns null
            every {
                getRestfulClient<PurchaseOrderDocumentListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DOCUMENT,
                    header = any(),
                    pathParams = hashMapOf("supplierCode" to supplierCode),
                    timeout = any(),
                )
            } returns restfulClientMock
            every { restfulClientMock.requestGet() } returns restfulResponseMock

            // when
            val result = sageAdapterApi.getPurchaseOrderDocument(token, branchCode, supplierCode)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_saveDraftPurchaseInvoice() =
        runTest {
            // given
            val restfulClientMock =
                mockk<RestfulClient<PurchaseInvoiceDraftDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseInvoiceDraftDTOResponse>>(relaxed = true)
            every { restfulClientMock.requestPost() } returns restfulResponseMock
            every {
                getRestfulClient<PurchaseInvoiceDraftDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_DRAFT,
                    header = any(),
                    timeout = any(),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            PurchaseInvoiceDraftRequest(
                                piNo = "PI123",
                                docNo = "DOC123",
                                flagPO = 1,
                                supplierCode = "SUP001",
                                billNo = "BILL123",
                                items = emptyList(),
                            ),
                        ),
                )
            } returns restfulClientMock
            // when
            val result =
                sageAdapterApi.saveDraftPurchaseInvoice(
                    token = "test_token",
                    branchCode = "",
                    piNo = "PI123",
                    docNo = "DOC123",
                    flagPO = 1,
                    billNo = "BILL123",
                    supplierCode = "SUP001",
                    items = emptyList(),
                    timestamp = 0L,
                )
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_saveDraftPurchaseInvoice_when_service_config_null() =
        runTest {
            // given
            val restfulClientMock =
                mockk<RestfulClient<PurchaseInvoiceDraftDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseInvoiceDraftDTOResponse>>(relaxed = true)
            every { appConfig.getServiceConfig(any()) } returns null
            every { restfulClientMock.requestPost() } returns restfulResponseMock
            every {
                getRestfulClient<PurchaseInvoiceDraftDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_DRAFT,
                    header = any(),
                    timeout = any(),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            PurchaseInvoiceDraftRequest(
                                piNo = "PI123",
                                docNo = "DOC123",
                                flagPO = 1,
                                supplierCode = "SUP001",
                                billNo = "BILL123",
                                items = emptyList(),
                            ),
                        ),
                )
            } returns restfulClientMock
            // when
            val result =
                sageAdapterApi.saveDraftPurchaseInvoice(
                    token = "test_token",
                    branchCode = "",
                    piNo = "PI123",
                    docNo = "DOC123",
                    flagPO = 1,
                    billNo = "BILL123",
                    supplierCode = "SUP001",
                    items = emptyList(),
                    timestamp = 0L
                )
            // then
            assertEquals(result, restfulResponseMock)
        }

    @Test
    fun test_getPurchaseOrderDetailByDocNo() =
        runTest {
            // given
            val token = "test_token"
            val branchCode = "B001"
            val supplierCode = "SUP123"
            val docNo = "PO456"

            val restfulClientMock = mockk<RestfulClient<PurchaseOrderDetailDTOResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<PurchaseOrderDetailDTOResponse>>(relaxed = true)

            val appConfigMock = mockk<AppConfig>(relaxed = true)
            val jsonUtilsMock = mockk<JsonUtils>(relaxed = true)
            val appEntryPointMock = mockk<AppEntryPoint>()

            every { appEntryPointMock.getAppConfig() } returns appConfigMock
            every { appEntryPointMock.getJsonUtils() } returns jsonUtilsMock
            mockkObject(AppDependencyProvider)
            every { AppDependencyProvider.getProviderBuilder() } returns appEntryPointMock
            every {
                getRestfulClient<PurchaseOrderDetailDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DETAIL,
                    header = any(),
                    pathParams = hashMapOf("supplierCode" to supplierCode, "docNo" to docNo),
                    timeout = any(),
                )
            } returns restfulClientMock

            coEvery { restfulClientMock.requestGet() } returns restfulResponseMock

            val api = SageAdapterApi(appConfigMock)

            // when
            val result = api.getPurchaseOrderDetailByDocNo(token, branchCode, docNo, supplierCode)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_validatePurchaseInvoiceBill() =
        runTest {
            // given
            val restfulClientMock =
                mockk<RestfulClient<PurchaseInvoiceValidateBillDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseInvoiceValidateBillDTOResponse>>(relaxed = true)

            val token = "token"
            val branchCode = "branch"
            val billNo = "bill123"
            val supplierCode = "sup123"
            val piNo = "PI012345"

            every {
                getRestfulClient<PurchaseInvoiceValidateBillDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_VALIDATE_BILL,
                    header = any(),
                    timeout = any(),
                    isJsonBody = true,
                    body = any(),
                )
            } returns restfulClientMock

            every { restfulClientMock.requestPost() } returns restfulResponseMock

            // when
            val result =
                sageAdapterApi.validatePurchaseInvoiceBill(token, branchCode, billNo, piNo, supplierCode)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_validatePurchaseInvoiceBill_serviceConfigNull() =
        runTest {
            // given
            val restfulClientMock =
                mockk<RestfulClient<PurchaseInvoiceValidateBillDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseInvoiceValidateBillDTOResponse>>(relaxed = true)

            val token = "token"
            val branchCode = "branch"
            val billNo = "bill123"
            val supplierCode = "sup123"
            val piNo = "PI012345"

            every { appConfig.getServiceConfig(SERVICE_PURCHASE_INVOICE_VALIDATE_BILL) } returns null

            every {
                getRestfulClient<PurchaseInvoiceValidateBillDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_VALIDATE_BILL,
                    header = any(),
                    timeout = any(),
                    isJsonBody = true,
                    body = any(),
                )
            } returns restfulClientMock

            every { restfulClientMock.requestPost() } returns restfulResponseMock

            // when
            val result =
                sageAdapterApi.validatePurchaseInvoiceBill(token, branchCode, billNo, piNo, supplierCode)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_createPurchaseInvoice() =
        runTest {
            // given
            val restfulClientMock =
                mockk<RestfulClient<PurchaseInvoiceCreateDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseInvoiceCreateDTOResponse>>(relaxed = true)

            val token = "token"
            val branchCode = "branch"
            val poNo = "po123"
            val billNo = "bill123"
            val supplierCode = "sup123"

            every {
                getRestfulClient<PurchaseInvoiceCreateDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_CREATE,
                    header = any(),
                    timeout = any(),
                    isJsonBody = true,
                    body = any(),
                )
            } returns restfulClientMock

            every { restfulClientMock.requestPost() } returns restfulResponseMock

            // when
            val result =
                sageAdapterApi.createPurchaseInvoice(token, branchCode, poNo, billNo, supplierCode)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_createPurchaseInvoice_serviceConfigNull() =
        runTest {
            // given
            val restfulClientMock =
                mockk<RestfulClient<PurchaseInvoiceCreateDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseInvoiceCreateDTOResponse>>(relaxed = true)

            val token = "token"
            val branchCode = "branch"
            val poNo = "po123"
            val billNo = "bill123"
            val supplierCode = "sup123"

            every { appConfig.getServiceConfig(SERVICE_PURCHASE_INVOICE_CREATE) } returns null

            every {
                getRestfulClient<PurchaseInvoiceCreateDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_CREATE,
                    header = any(),
                    timeout = any(),
                    isJsonBody = true,
                    body = any(),
                )
            } returns restfulClientMock

            every { restfulClientMock.requestPost() } returns restfulResponseMock

            // when
            val result =
                sageAdapterApi.createPurchaseInvoice(token, branchCode, poNo, billNo, supplierCode)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_getPurchaseInvoiceDetail() =
        runTest {
            // given
            val restfulClientMock =
                mockk<RestfulClient<PurchaseInvoiceDetailDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseInvoiceDetailDTOResponse>>(relaxed = true)

            val token = "token"
            val branchCode = "branch"
            val piNo = "pi123"

            every {
                getRestfulClient<PurchaseInvoiceDetailDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_DETAIL,
                    header = any(),
                    pathParams = hashMapOf("piNo" to piNo),
                    timeout = any(),
                )
            } returns restfulClientMock

            every { restfulClientMock.requestGet() } returns restfulResponseMock

            // when
            val result = sageAdapterApi.getPurchaseInvoiceDetail(token, branchCode, piNo)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_getPurchaseInvoiceDetail_serviceConfigNull() =
        runTest {
            // given
            val restfulClientMock =
                mockk<RestfulClient<PurchaseInvoiceDetailDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<PurchaseInvoiceDetailDTOResponse>>(relaxed = true)

            val token = "token"
            val branchCode = "branch"
            val piNo = "pi123"

            every { appConfig.getServiceConfig(SERVICE_PURCHASE_INVOICE_GET_DETAIL) } returns null

            every {
                getRestfulClient<PurchaseInvoiceDetailDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_DETAIL,
                    header = any(),
                    pathParams = hashMapOf("piNo" to piNo),
                    timeout = any(),
                )
            } returns restfulClientMock

            every { restfulClientMock.requestGet() } returns restfulResponseMock

            // when
            val result = sageAdapterApi.getPurchaseInvoiceDetail(token, branchCode, piNo)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_qrDocumentSubmit() =
        runTest {
            // given
            val restfulClientMock =
                mockk<RestfulClient<QrDocumentSubmitDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<QrDocumentSubmitDTOResponse>>(relaxed = true)

            val token = "token"
            val branchCode = "branch"
            val data =
                QrDocumentSubmitDTORequest(
                    lotNumber = "pi123",
                    data =
                        listOf(
                            QrDocumentSubmitData(
                                barcode = "11223344",
                                expireDate = "08082025",
                                qty = 1,
                            ),
                        ),
                )

            every {
                getRestfulClient<QrDocumentSubmitDTOResponse>(
                    serviceName = SERVICE_SHIPMENT_QR_DOCUMENT_SUBMIT,
                    header = any(),
                    body = data,
                    timeout = any(),
                )
            } returns restfulClientMock

            every { restfulClientMock.requestPost() } returns restfulResponseMock

            // when
            val result = sageAdapterApi.qrDocumentSubmit(token, branchCode, data)

            // then
            assertEquals(restfulResponseMock, result)
        }

    @Test
    fun test_qrDocumentSubmit_serviceConfigNull() =
        runTest {
            // given
            val restfulClientMock =
                mockk<RestfulClient<QrDocumentSubmitDTOResponse>>(relaxed = true)
            val restfulResponseMock =
                mockk<RestfulResponse<QrDocumentSubmitDTOResponse>>(relaxed = true)

            val token = "token"
            val branchCode = "branch"
            val data =
                QrDocumentSubmitDTORequest(
                    lotNumber = "pi123",
                    data =
                        listOf(
                            QrDocumentSubmitData(
                                barcode = "11223344",
                                expireDate = "08082025",
                                qty = 1,
                            ),
                        ),
                )

            every { appConfig.getServiceConfig(SERVICE_SHIPMENT_QR_DOCUMENT_SUBMIT) } returns null

            every {
                getRestfulClient<QrDocumentSubmitDTOResponse>(
                    serviceName = SERVICE_SHIPMENT_QR_DOCUMENT_SUBMIT,
                    header = any(),
                    body = data,
                    timeout = any(),
                )
            } returns restfulClientMock

            every { restfulClientMock.requestPost() } returns restfulResponseMock

            // when
            val result = sageAdapterApi.qrDocumentSubmit(token, branchCode, data)

            // then
            assertEquals(restfulResponseMock, result)
        }
}
