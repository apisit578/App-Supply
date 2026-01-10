package th.co.cjexpress.store.sage.data.repository

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.MasterMapping
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.mapper.SageAdapterApiModelMapper
import th.co.cjexpress.store.sage.data.model.masterMapping.BdSearchTypeDTOResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.DocumentSDResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.OrderReceiveSummaryResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcodeResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentItemRequest
import th.co.cjexpress.store.sage.data.model.orderReceive.SkuCheckResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.OpenToteResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteApproveResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteDiscountsConfigResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteListResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteLowestFactorResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SearchSdToteResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseBarcodeFindDTORequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseBarcodeFindDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCalculateDTORequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCalculateDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCalculateItemDTORequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDocumentListDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierListDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceValidateBillDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDocumentListDTOResponse
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.request.QrDocumentSubmitDTORequest
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.response.QrDocumentSubmitDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentNoDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentSubmitDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentValidateDTOResponse
import th.co.cjexpress.store.sage.data.network.RestfulResponse
import th.co.cjexpress.store.sage.data.source.remote.SageAdapterApi
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.masterMapping.BdSearchTypeMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.SkuCheckModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteApproveMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteListMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteLowestFactorMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitAdjustModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseBarcodeFindListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceValidateBillModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentListModel
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateMappingModel
import java.io.IOException
import java.util.Date

class SageAdapterRepositoryImplTest {
    @RelaxedMockK
    lateinit var sageAdapterApi: SageAdapterApi

    @RelaxedMockK
    lateinit var sageModelMapper: SageAdapterApiModelMapper

    @RelaxedMockK
    lateinit var appConfig: AppConfig

    private lateinit var repository: SageApiRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { appConfig.androidId } returns "t12345678"
        every { appConfig.authToken?.token } returns "test_token"
        every { appConfig.getBranchInfo()?.branchCode } returns "M04C"

        repository =
            SageApiRepositoryImpl(
                sageAdapterApi,
                sageModelMapper,
                appConfig,
            )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_skuCheck_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<SkuCheckResponse>>(relaxed = true)
            val respModel = mockk<SkuCheckResponse>()
            val model = mockk<SkuCheckModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SkuCheckResponse::class.java) } returns respModel
            every { sageModelMapper.skuCheckMapper.map(respModel) } returns model

            coEvery {
                sageAdapterApi.skuCheck(
                    token = any(),
                    branchCode = any(),
                    barcode = any(),
                )
            } returns restfulResponse
            // when
            val flow =
                repository.skuCheck(
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_skuCheck_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<SkuCheckResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SkuCheckResponse::class.java) } throws Exception()

            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null

            coEvery {
                sageAdapterApi.skuCheck(
                    token = any(),
                    branchCode = any(),
                    barcode = "barcode",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.skuCheck(
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_skuCheck_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<SkuCheckResponse>>(relaxed = true)
            val respModel = mockk<SkuCheckResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(SkuCheckResponse::class.java) } returns respModel

            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo() } returns null

            coEvery {
                sageAdapterApi.skuCheck(
                    token = any(),
                    branchCode = any(),
                    barcode = "barcode",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.skuCheck(
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_skuCheck_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.skuCheck(
                    token = "token",
                    branchCode = "branchCode",
                    barcode = "barcode",
                )
            } returns null
            // when
            val flow =
                repository.skuCheck(
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_skuCheck_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.skuCheck(
                    token = "token",
                    branchCode = "branchCode",
                    barcode = "barcode",
                )
            } throws IOException()
            // when
            val flow =
                repository.skuCheck(
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSdDocList_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<DocumentSDResponse>>(relaxed = true)
            val respModel = mockk<DocumentSDResponse>()
            val model = mockk<DocumentSDModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(DocumentSDResponse::class.java) } returns respModel
            every { sageModelMapper.documentSDMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.getSdDocList(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } returns restfulResponse
            // when
            val flow = repository.getSdDocList()
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_getSdDocList_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<DocumentSDResponse>>(relaxed = true)
            val respModel = mockk<DocumentSDResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(DocumentSDResponse::class.java) } returns respModel
            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null
            coEvery {
                sageAdapterApi.getSdDocList(
                    token = any(),
                    branchCode = any(),
                )
            } returns restfulResponse
            // when
            val flow = repository.getSdDocList()
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSdDocList_failure_exception_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<DocumentSDResponse>>(relaxed = true)
            every { restfulResponse.t } returns Exception("error")
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(DocumentSDResponse::class.java) } returns null
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo() } returns null
            coEvery {
                sageAdapterApi.getSdDocList(
                    token = any(),
                    branchCode = any(),
                )
            } returns restfulResponse
            // when
            val flow = repository.getSdDocList()

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSdDocList_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.getSdDocList(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } returns null
            // when
            val flow = repository.getSdDocList()
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSdDocList_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.getSdDocList(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } throws IOException("error")
            // when
            val flow = repository.getSdDocList()
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_success_expect_result_success() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            val restfulResponse = mockk<RestfulResponse<ShipmentDocumentResponse>>(relaxed = true)
            val respModel = mockk<ShipmentDocumentResponse>()
            val model = mockk<ShipmentDocumentModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(ShipmentDocumentResponse::class.java) } returns respModel
            every { sageModelMapper.shipmentDocumentMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.getShipmentDocumentBySDNo(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = sdNo,
                )
            } returns restfulResponse
            // when
            val flow = repository.getShipmentDocumentBySDNo(sdNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_failure_expect_result_error() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            val restfulResponse = mockk<RestfulResponse<ShipmentDocumentResponse>>(relaxed = true)
            val respModel = mockk<ShipmentDocumentResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(ShipmentDocumentResponse::class.java) } returns respModel
            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null
            coEvery {
                sageAdapterApi.getShipmentDocumentBySDNo(
                    token = any(),
                    branchCode = any(),
                    sdNo = sdNo,
                )
            } returns restfulResponse
            // when
            val flow = repository.getShipmentDocumentBySDNo(sdNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_failure_exception_expect_result_error() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            val restfulResponse = mockk<RestfulResponse<ShipmentDocumentResponse>>(relaxed = true)
            every { restfulResponse.t } returns Exception("error")
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(ShipmentDocumentResponse::class.java) } returns null
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo() } returns null
            coEvery {
                sageAdapterApi.getShipmentDocumentBySDNo(
                    token = any(),
                    branchCode = any(),
                    sdNo = sdNo,
                )
            } returns restfulResponse
            // when
            val flow = repository.getShipmentDocumentBySDNo(sdNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            coEvery {
                sageAdapterApi.getShipmentDocumentBySDNo(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = sdNo,
                )
            } returns null
            // when
            val flow = repository.getShipmentDocumentBySDNo(sdNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_when_iOException_expect_result_error() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            coEvery {
                sageAdapterApi.getShipmentDocumentBySDNo(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = sdNo,
                )
            } throws IOException("error")
            // when
            val flow = repository.getShipmentDocumentBySDNo(sdNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentDocumentOpen_success_expect_result_success() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            val restfulResponse = mockk<RestfulResponse<ShipmentDocumentResponse>>(relaxed = true)
            val respModel = mockk<ShipmentDocumentResponse>()
            val model = mockk<ShipmentDocumentModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(ShipmentDocumentResponse::class.java) } returns respModel
            every { sageModelMapper.shipmentDocumentMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.shipmentDocumentOpen(
                    token = "test_token",
                    branchCode = "M04C",
                    ldNo = ldNo,
                )
            } returns restfulResponse
            // when
            val flow = repository.shipmentDocumentOpen(ldNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_shipmentDocumentOpen_failure_expect_result_error() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            val restfulResponse = mockk<RestfulResponse<ShipmentDocumentResponse>>(relaxed = true)
            val respModel = mockk<ShipmentDocumentResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(ShipmentDocumentResponse::class.java) } returns respModel
            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null
            coEvery {
                sageAdapterApi.shipmentDocumentOpen(
                    token = any(),
                    branchCode = any(),
                    ldNo = ldNo,
                )
            } returns restfulResponse
            // when
            val flow = repository.shipmentDocumentOpen(ldNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentDocumentOpen_failure_exception_expect_result_error() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            val restfulResponse = mockk<RestfulResponse<ShipmentDocumentResponse>>(relaxed = true)
            every { restfulResponse.t } returns Exception("error")
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(ShipmentDocumentResponse::class.java) } returns null
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo() } returns null
            coEvery {
                sageAdapterApi.shipmentDocumentOpen(
                    token = any(),
                    branchCode = any(),
                    ldNo = ldNo,
                )
            } returns restfulResponse
            // when
            val flow = repository.shipmentDocumentOpen(ldNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentDocumentOpen_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            coEvery {
                sageAdapterApi.shipmentDocumentOpen(
                    token = "test_token",
                    branchCode = "M04C",
                    ldNo = ldNo,
                )
            } returns null
            // when
            val flow = repository.shipmentDocumentOpen(ldNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentDocumentOpen_when_iOException_expect_result_error() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            coEvery {
                sageAdapterApi.shipmentDocumentOpen(
                    token = "test_token",
                    branchCode = "M04C",
                    ldNo = ldNo,
                )
            } throws IOException("error")
            // when
            val flow = repository.shipmentDocumentOpen(ldNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentDocumentByBarcode_success_expect_result_success() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000039"
            val barcode = "8851717021114"
            val actualQty = 10
            val restfulResponse =
                mockk<RestfulResponse<ShipmentDocumentByBarcodeResponse>>(relaxed = true)
            val respModel = mockk<ShipmentDocumentByBarcodeResponse>()
            val model = mockk<ShipmentDocumentByBarcodeModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(ShipmentDocumentByBarcodeResponse::class.java) } returns respModel
            every { sageModelMapper.shipmentDocumentByBarcodeMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.shipmentDocumentByBarcode(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = sdNo,
                    data = listOf(ShipmentItemRequest(barcode = barcode, actualQty = actualQty)),
                )
            } returns restfulResponse
            // when
            val flow =
                repository.shipmentDocumentByBarcode(
                    sdNo,
                    listOf(ShipmentItemRequest(barcode = barcode, actualQty = actualQty)),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_shipmentDocumentByBarcode_success_expect_result_error() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000039"
            val barcode = "8851717021114"
            val actualQty = 10
            val restfulResponse =
                mockk<RestfulResponse<ShipmentDocumentByBarcodeResponse>>(relaxed = true)
            val respModel = mockk<ShipmentDocumentByBarcodeResponse>()
            val model = mockk<ShipmentDocumentByBarcodeModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(ShipmentDocumentByBarcodeResponse::class.java) } returns respModel
            every { sageModelMapper.shipmentDocumentByBarcodeMapper.map(respModel) } returns model
            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null
            coEvery {
                sageAdapterApi.shipmentDocumentByBarcode(
                    token = any(),
                    branchCode = any(),
                    sdNo = sdNo,
                    data = listOf(ShipmentItemRequest(barcode = barcode, actualQty = actualQty)),
                )
            } returns null
            // when
            val flow =
                repository.shipmentDocumentByBarcode(
                    sdNo,
                    listOf(ShipmentItemRequest(barcode = barcode, actualQty = actualQty)),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentDocumentByBarcode_unsuccessful_response_expect_result_error() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000039"
            val barcode = "8851717021114"
            val actualQty = 10

            val restfulResponse = mockk<RestfulResponse<ShipmentDocumentByBarcodeResponse>>()
            every { restfulResponse.isSuccessful } returns false
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo() } returns null

            coEvery {
                sageAdapterApi.shipmentDocumentByBarcode(
                    token = any(),
                    branchCode = any(),
                    sdNo = sdNo,
                    data = listOf(ShipmentItemRequest(barcode = barcode, actualQty = actualQty)),
                )
            } returns restfulResponse

            // when
            val flow =
                repository.shipmentDocumentByBarcode(
                    sdNo,
                    listOf(ShipmentItemRequest(barcode = barcode, actualQty = actualQty)),
                )

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getShipmentDocumentSummaryBySd_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<OrderReceiveSummaryResponse>>(relaxed = true)
            val respModel = mockk<OrderReceiveSummaryResponse>()
            val model = mockk<OrderReceiveSummaryModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(OrderReceiveSummaryResponse::class.java) } returns respModel
            every { sageModelMapper.summaryMapper.map(orderReceiveSummaryResponse = respModel) } returns model

            coEvery {
                sageAdapterApi.getShipmentDocumentSummaryBySd(
                    token = any(),
                    branchCode = any(),
                    sdNo = any(),
                )
            } returns restfulResponse
            // when
            val flow = repository.getShipmentDocumentSummaryBySd(sdNo = "SD123")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_getShipmentDocumentSummaryBySd_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<OrderReceiveSummaryResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false

            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null

            coEvery {
                sageAdapterApi.getShipmentDocumentSummaryBySd(
                    token = any(),
                    branchCode = any(),
                    sdNo = any(),
                )
            } returns restfulResponse
            // when
            val flow = repository.getShipmentDocumentSummaryBySd(sdNo = "SD123")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getShipmentDocumentSummaryBySd_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo() } returns null
            coEvery {
                sageAdapterApi.getShipmentDocumentSummaryBySd(
                    token = any(),
                    branchCode = any(),
                    sdNo = any(),
                )
            } returns null
            // when
            val flow = repository.getShipmentDocumentSummaryBySd(sdNo = "SD123")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getShipmentDocumentSummaryBySd_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.getShipmentDocumentSummaryBySd(
                    token = any(),
                    branchCode = any(),
                    sdNo = any(),
                )
            } throws IOException()
            // when
            val flow = repository.getShipmentDocumentSummaryBySd(sdNo = "SD123")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getFrequencySupplier_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceSupplierListDTOResponse>()
            val model = mockk<PurchaseInvoiceSupplierListModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceSupplierListDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseInvoiceSupplierMapper.mapList(respModel) } returns model

            coEvery {
                sageAdapterApi.getFrequencySupplier(
                    token = any(),
                    branchCode = any(),
                    perPage = any(),
                )
            } returns restfulResponse
            // when
            val flow = repository.getFrequencySupplier(perPage = "10")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_getFrequencySupplier_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false

            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null

            coEvery {
                sageAdapterApi.getFrequencySupplier(
                    token = any(),
                    branchCode = any(),
                    perPage = any(),
                )
            } returns restfulResponse
            // when
            val flow = repository.getFrequencySupplier(perPage = "10")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getFrequencySupplier_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo() } returns null
            coEvery {
                sageAdapterApi.getFrequencySupplier(
                    token = any(),
                    branchCode = any(),
                    perPage = any(),
                )
            } returns null
            // when
            val flow = repository.getFrequencySupplier(perPage = "10")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getFrequencySupplier_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.getFrequencySupplier(
                    token = any(),
                    branchCode = any(),
                    perPage = any(),
                )
            } throws IOException()
            // when
            val flow = repository.getFrequencySupplier(perPage = "10")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_filterSupplier_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceSupplierListDTOResponse>()
            val model = mockk<PurchaseInvoiceSupplierListModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceSupplierListDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseInvoiceSupplierMapper.mapList(respModel) } returns model

            coEvery {
                sageAdapterApi.filterSupplier(
                    token = any(),
                    branchCode = any(),
                    searchKey = any(),
                )
            } returns restfulResponse
            // when
            val flow = repository.filterSupplier(searchKey = "testKey")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_filterSupplier_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceSupplierListDTOResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false

            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null

            coEvery {
                sageAdapterApi.filterSupplier(
                    token = any(),
                    branchCode = any(),
                    searchKey = any(),
                )
            } returns restfulResponse
            // when
            val flow = repository.filterSupplier(searchKey = "testKey")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_filterSupplier_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo() } returns null
            coEvery {
                sageAdapterApi.filterSupplier(
                    token = any(),
                    branchCode = any(),
                    searchKey = any(),
                )
            } returns null
            // when
            val flow = repository.filterSupplier(searchKey = "testKey")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_filterSupplier_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.filterSupplier(
                    token = any(),
                    branchCode = any(),
                    searchKey = any(),
                )
            } throws IOException()
            // when
            val flow = repository.filterSupplier(searchKey = "testKey")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSupplierByCode_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceSupplierDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceSupplierDTOResponse>()
            val model = mockk<PurchaseInvoiceSupplierModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceSupplierDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseInvoiceSupplierMapper.map(respModel) } returns model

            coEvery {
                sageAdapterApi.getSupplierByCode(
                    token = any(),
                    branchCode = any(),
                    searchCode = any(),
                )
            } returns restfulResponse
            // when
            val flow = repository.getSupplierByCode(searchCode = "SUP123")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_getSupplierByCode_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceSupplierDTOResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false

            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null

            coEvery {
                sageAdapterApi.getSupplierByCode(
                    token = any(),
                    branchCode = any(),
                    searchCode = any(),
                )
            } returns restfulResponse
            // when
            val flow = repository.getSupplierByCode(searchCode = "SUP123")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSupplierByCode_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo() } returns null
            coEvery {
                sageAdapterApi.getSupplierByCode(
                    token = any(),
                    branchCode = any(),
                    searchCode = any(),
                )
            } returns null
            // when
            val flow = repository.getSupplierByCode(searchCode = "SUP123")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSupplierByCode_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.getSupplierByCode(
                    token = any(),
                    branchCode = any(),
                    searchCode = any(),
                )
            } throws IOException()
            // when
            val flow = repository.getSupplierByCode(searchCode = "SUP123")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getPurchaseInvoiceDocument_success_expect_result_success() =
        runTest {
            // given
            val supplierCode = "S001"
            val branchCode = "B001"
            val token = "token"
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceDocumentListDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceDocumentListDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceDocumentListModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceDocumentListDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseInvoiceDocumentMapper.map(respModel) } returns mappedModel

            every { appConfig.authToken?.token } returns token
            every { appConfig.getBranchInfo()?.branchCode } returns branchCode

            coEvery {
                sageAdapterApi.getPurchaseInvoiceDocument(
                    token = token,
                    branchCode = branchCode,
                    supplierCode = supplierCode,
                )
            } returns restfulResponse

            // when
            val flow = repository.getPurchaseInvoiceDocument(supplierCode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_getPurchaseInvoiceDocument_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            val supplierCode = "S001"
            val branchCode = "B001"
            val token = "token"
            every { appConfig.authToken?.token } returns token
            every { appConfig.getBranchInfo()?.branchCode } returns branchCode

            coEvery {
                sageAdapterApi.getPurchaseInvoiceDocument(token, branchCode, supplierCode)
            } returns null

            // when
            val flow = repository.getPurchaseInvoiceDocument(supplierCode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getPurchaseInvoiceDocument_failure_expect_result_error() =
        runTest {
            val supplierCode = "S001"
            val branchCode = "B001"
            val token = "token"
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceDocumentListDTOResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false

            every { appConfig.authToken?.token } returns token
            every { appConfig.getBranchInfo()?.branchCode } returns branchCode

            coEvery {
                sageAdapterApi.getPurchaseInvoiceDocument(token, branchCode, supplierCode)
            } returns restfulResponse

            val flow = repository.getPurchaseInvoiceDocument(supplierCode)

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getPurchaseInvoiceDocument_when_ioException_expect_result_error() =
        runTest {
            // given
            val supplierCode = "S001"
            val branchCode = "B001"
            val token = "token"
            every { appConfig.authToken?.token } returns token
            every { appConfig.getBranchInfo()?.branchCode } returns branchCode
            coEvery {
                sageAdapterApi.getPurchaseInvoiceDocument(token, branchCode, supplierCode)
            } throws IOException()

            // when
            val flow = repository.getPurchaseInvoiceDocument(supplierCode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getPurchaseInvoiceDocument_when_token_is_null_expect_empty_token_used() =
        runTest {
            // given
            val supplierCode = "S001"
            val branchCode = "B001"
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceDocumentListDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceDocumentListDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceDocumentListModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceDocumentListDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseInvoiceDocumentMapper.map(respModel) } returns mappedModel

            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns branchCode

            coEvery {
                sageAdapterApi.getPurchaseInvoiceDocument(
                    token = "",
                    branchCode = branchCode,
                    supplierCode = supplierCode,
                )
            } returns restfulResponse

            // when
            val flow = repository.getPurchaseInvoiceDocument(supplierCode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_getPurchaseInvoiceDocument_when_branchInfo_is_null_expect_empty_branchCode_used() =
        runTest {
            // given
            val supplierCode = "S001"
            val token = "token"
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceDocumentListDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceDocumentListDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceDocumentListModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceDocumentListDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseInvoiceDocumentMapper.map(respModel) } returns mappedModel

            every { appConfig.authToken?.token } returns token
            every { appConfig.getBranchInfo() } returns null

            coEvery {
                sageAdapterApi.getPurchaseInvoiceDocument(
                    token = "token",
                    branchCode = "",
                    supplierCode = supplierCode,
                )
            } returns restfulResponse

            // when
            val flow = repository.getPurchaseInvoiceDocument(supplierCode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_getPurchaseOrderDocument_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseOrderDocumentListDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseOrderDocumentListDTOResponse>()
            val mappedModel = mockk<PurchaseOrderDocumentListModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseOrderDocumentListDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseOrderDocumentMapper.map(respModel) } returns mappedModel

            every { appConfig.authToken?.token } returns "mockToken"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.getPurchaseOrderDocument(
                    token = any(),
                    branchCode = any(),
                    supplierCode = any(),
                )
            } returns restfulResponse

            // when
            val flow = repository.getPurchaseOrderDocument("SUP999")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_getPurchaseOrderDocument_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.getPurchaseOrderDocument(any(), any(), any())
            } returns null

            // when
            val flow = repository.getPurchaseOrderDocument("SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getPurchaseOrderDocument_when_ioException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.getPurchaseOrderDocument(any(), any(), any())
            } throws IOException()

            // when
            val flow = repository.getPurchaseOrderDocument("SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getPurchaseOrderDocument_when_token_is_null_expect_empty_token_used() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseOrderDocumentListDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseOrderDocumentListDTOResponse>()
            val mappedModel = mockk<PurchaseOrderDocumentListModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseOrderDocumentListDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseOrderDocumentMapper.map(respModel) } returns mappedModel

            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.getPurchaseOrderDocument("", "B001", "SUP001")
            } returns restfulResponse

            // when
            val flow = repository.getPurchaseOrderDocument("SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_getPurchaseOrderDocument_when_branchInfo_is_null_expect_empty_branchCode_used() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseOrderDocumentListDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseOrderDocumentListDTOResponse>()
            val mappedModel = mockk<PurchaseOrderDocumentListModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseOrderDocumentListDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseOrderDocumentMapper.map(respModel) } returns mappedModel

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo() } returns null

            coEvery {
                sageAdapterApi.getPurchaseOrderDocument("token", "", "SUP001")
            } returns restfulResponse

            // when
            val flow = repository.getPurchaseOrderDocument("SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_purchaseInvoiceCalculate_success_expect_result_success() =
        runTest {
            // given
            val dataBarcode =
                PurchaseInvoiceCalculateDTORequest(
                    items =
                        listOf(
                            PurchaseInvoiceCalculateItemDTORequest(
                                barcode = "8851123237048",
                                actualQty = 10,
                                qty = 5,
                            ),
                        ),
                    piNo = "PI2506M04C-000031",
                    poNo = "TESTB112233445",
                    supplierCode = "0000100917",
                )

            val branchCode = "B001"
            val token = "token"
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceCalculateDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceCalculateDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceCalculateModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceCalculateDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseInvoiceCalculateMapper.map(respModel) } returns mappedModel

            every { appConfig.authToken?.token } returns token
            every { appConfig.getBranchInfo()?.branchCode } returns branchCode

            coEvery {
                sageAdapterApi.purchaseInvoiceCalculate(
                    token = token,
                    branchCode = branchCode,
                    data = dataBarcode,
                )
            } returns restfulResponse

            // when
            val flow = repository.purchaseInvoiceCalculate(dataBarcode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_purchaseInvoiceCalculate_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            val dataBarcode =
                PurchaseInvoiceCalculateDTORequest(
                    items =
                        listOf(
                            PurchaseInvoiceCalculateItemDTORequest(
                                barcode = "8851123237048",
                                actualQty = 10,
                                qty = 5,
                            ),
                        ),
                    piNo = "PI2506M04C-000031",
                    poNo = "TESTB112233445",
                    supplierCode = "0000100917",
                )
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.purchaseInvoiceCalculate(any(), any(), any())
            } returns null

            // when
            val flow = repository.purchaseInvoiceCalculate(dataBarcode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_purchaseInvoiceCalculate_when_branchInfo_is_null_expect_empty_branchCode_used() =
        runTest {
            // given
            val dataBarcode =
                PurchaseInvoiceCalculateDTORequest(
                    items =
                        listOf(
                            PurchaseInvoiceCalculateItemDTORequest(
                                barcode = "8851123237048",
                                actualQty = 10,
                                qty = 5,
                            ),
                        ),
                    piNo = "PI2506M04C-000031",
                    poNo = "TESTB112233445",
                    supplierCode = "0000100917",
                )
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceCalculateDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceCalculateDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceCalculateModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceCalculateDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseInvoiceCalculateMapper.map(respModel) } returns mappedModel

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo() } returns null

            coEvery {
                sageAdapterApi.purchaseInvoiceCalculate("token", "", dataBarcode)
            } returns restfulResponse

            // when
            val flow = repository.purchaseInvoiceCalculate(dataBarcode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_purchaseInvoiceCalculate_when_token_is_null_expect_empty_token_used() =
        runTest {
            // given
            val dataBarcode =
                PurchaseInvoiceCalculateDTORequest(
                    items =
                        listOf(
                            PurchaseInvoiceCalculateItemDTORequest(
                                barcode = "8851123237048",
                                actualQty = 10,
                                qty = 5,
                            ),
                        ),
                    piNo = "PI2506M04C-000031",
                    poNo = "TESTB112233445",
                    supplierCode = "0000100917",
                )
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceCalculateDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceCalculateDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceCalculateModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceCalculateDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseInvoiceCalculateMapper.map(respModel) } returns mappedModel

            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.purchaseInvoiceCalculate("", "B001", dataBarcode)
            } returns restfulResponse

            // when
            val flow = repository.purchaseInvoiceCalculate(dataBarcode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_purchaseInvoiceCalculate_when_ioException_expect_result_error() =
        runTest {
            // given
            val dataBarcode =
                PurchaseInvoiceCalculateDTORequest(
                    items =
                        listOf(
                            PurchaseInvoiceCalculateItemDTORequest(
                                barcode = "8851123237048",
                                actualQty = 10,
                                qty = 5,
                            ),
                        ),
                    piNo = "PI2506M04C-000031",
                    poNo = "TESTB112233445",
                    supplierCode = "0000100917",
                )
            coEvery {
                sageAdapterApi.purchaseInvoiceCalculate(any(), any(), any())
            } throws IOException()

            // when
            val flow = repository.purchaseInvoiceCalculate(dataBarcode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceDraftDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceDraftDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceDraftModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceDraftDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseInvoiceDraftMapper.map(respModel) } returns mappedModel

            every { appConfig.authToken?.token } returns "mockToken"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.saveDraftPurchaseInvoice(
                    token = any(),
                    branchCode = any(),
                    piNo = any(),
                    docNo = any(),
                    flagPO = any(),
                    supplierCode = any(),
                    billNo = any(),
                    items = any(),
                    timestamp = any(),
                )
            } returns restfulResponse
            // when
            val flow =
                repository.saveDraftPurchaseInvoice(
                    piNo = "PI123",
                    docNo = "DOC123",
                    flagPO = 1,
                    billNo = "BILL123",
                    supplierCode = "SUP001",
                    items = emptyList(),
                    timestamp = 0L,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceDraftDTOResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceDraftDTOResponse::class.java) } throws Exception()

            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null

            coEvery {
                sageAdapterApi.saveDraftPurchaseInvoice(
                    token = any(),
                    branchCode = any(),
                    piNo = any(),
                    docNo = any(),
                    flagPO = any(),
                    supplierCode = any(),
                    billNo = any(),
                    items = any(),
                    timestamp = any(),
                )
            } returns restfulResponse
            // when
            val flow =
                repository.saveDraftPurchaseInvoice(
                    piNo = "PI123",
                    docNo = "DOC123",
                    flagPO = 1,
                    billNo = "BILL123",
                    supplierCode = "SUP001",
                    items = emptyList(),
                    timestamp = 0L,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceDraftDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseInvoiceDraftDTOResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(PurchaseInvoiceDraftDTOResponse::class.java) } returns respModel

            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo() } returns null

            coEvery {
                sageAdapterApi.saveDraftPurchaseInvoice(
                    token = any(),
                    branchCode = any(),
                    piNo = any(),
                    docNo = any(),
                    flagPO = any(),
                    supplierCode = any(),
                    billNo = any(),
                    items = any(),
                    timestamp = any(),
                )
            } returns restfulResponse
            // when
            val flow =
                repository.saveDraftPurchaseInvoice(
                    piNo = "PI123",
                    docNo = "DOC123",
                    flagPO = 1,
                    billNo = "BILL123",
                    supplierCode = "SUP001",
                    items = emptyList(),
                    timestamp = 0L,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.saveDraftPurchaseInvoice(
                    token = any(),
                    branchCode = any(),
                    piNo = any(),
                    docNo = any(),
                    flagPO = any(),
                    supplierCode = any(),
                    billNo = any(),
                    items = any(),
                    timestamp = any(),
                )
            } returns null
            // when
            val flow =
                repository.saveDraftPurchaseInvoice(
                    piNo = "PI123",
                    docNo = "DOC123",
                    flagPO = 1,
                    billNo = "BILL123",
                    supplierCode = "SUP001",
                    items = emptyList(),
                    timestamp = 0L,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.saveDraftPurchaseInvoice(
                    token = any(),
                    branchCode = any(),
                    piNo = any(),
                    docNo = any(),
                    flagPO = any(),
                    supplierCode = any(),
                    billNo = any(),
                    items = any(),
                    timestamp = any(),
                )
            } throws IOException()
            // when
            val flow =
                repository.saveDraftPurchaseInvoice(
                    piNo = "PI123",
                    docNo = "DOC123",
                    flagPO = 1,
                    billNo = "BILL123",
                    supplierCode = "SUP001",
                    items = emptyList(),
                    timestamp = 0L,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun `validatePurchaseInvoiceBill success should emit Result Success`() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceValidateBillDTOResponse>>(relaxed = true)
            val dtoModel = mockk<PurchaseInvoiceValidateBillDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceValidateBillModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceValidateBillDTOResponse::class.java) } returns dtoModel
            every { sageModelMapper.purchaseInvoiceValidateBillMapper.map(dtoModel) } returns mappedModel
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.validatePurchaseInvoiceBill("token", "B001", "BILL123", "PI012345", "SUP001")
            } returns restfulResponse

            // when
            val flow = repository.validatePurchaseInvoiceBill("BILL123", "PI012345", "SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun `validatePurchaseInvoiceBill returns null response should emit Result Error`() =
        runTest {
            // given
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.validatePurchaseInvoiceBill("token", "B001", "BILL123", "PI012345", "SUP001")
            } returns null

            // when
            val flow = repository.validatePurchaseInvoiceBill("BILL123", "PI012345", "SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun `validatePurchaseInvoiceBill throws IOException should emit Result Error`() =
        runTest {
            // given
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.validatePurchaseInvoiceBill("token", "B001", "BILL123", "PI012345", "SUP001")
            } throws IOException()

            // when
            val flow = repository.validatePurchaseInvoiceBill("BILL123", "PI012345", "SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun `validatePurchaseInvoiceBill with null token should use empty string`() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceValidateBillDTOResponse>>(relaxed = true)
            val dtoModel = mockk<PurchaseInvoiceValidateBillDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceValidateBillModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceValidateBillDTOResponse::class.java) } returns dtoModel
            every { sageModelMapper.purchaseInvoiceValidateBillMapper.map(dtoModel) } returns mappedModel
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.validatePurchaseInvoiceBill("", "B001", "BILL123", "PI012345", "SUP001")
            } returns restfulResponse

            // when
            val flow = repository.validatePurchaseInvoiceBill("BILL123", "PI012345", "SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun `validatePurchaseInvoiceBill with null branchInfo should use empty string`() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceValidateBillDTOResponse>>(relaxed = true)
            val dtoModel = mockk<PurchaseInvoiceValidateBillDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceValidateBillModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceValidateBillDTOResponse::class.java) } returns dtoModel
            every { sageModelMapper.purchaseInvoiceValidateBillMapper.map(dtoModel) } returns mappedModel
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo() } returns null

            coEvery {
                sageAdapterApi.validatePurchaseInvoiceBill("token", "", "BILL123", "PI012345", "SUP001")
            } returns restfulResponse

            // when
            val flow = repository.validatePurchaseInvoiceBill("BILL123", "PI012345", "SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun `createPurchaseInvoice success should emit Result Success`() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceCreateDTOResponse>>(relaxed = true)
            val dtoModel = mockk<PurchaseInvoiceCreateDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceCreateModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceCreateDTOResponse::class.java) } returns dtoModel
            every { sageModelMapper.purchaseInvoiceCreateMapper.map(dtoModel) } returns mappedModel
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.createPurchaseInvoice("token", "B001", "PO123", "BILL123", "SUP001")
            } returns restfulResponse

            // when
            val flow = repository.createPurchaseInvoice("PO123", "BILL123", "SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun `createPurchaseInvoice returns null response should emit Result Error`() =
        runTest {
            // given
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.createPurchaseInvoice("token", "B001", "PO123", "BILL123", "SUP001")
            } returns null

            // when
            val flow = repository.createPurchaseInvoice("PO123", "BILL123", "SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun `createPurchaseInvoice throws IOException should emit Result Error`() =
        runTest {
            // given
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.createPurchaseInvoice("token", "B001", "PO123", "BILL123", "SUP001")
            } throws IOException()

            // when
            val flow = repository.createPurchaseInvoice("PO123", "BILL123", "SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun `createPurchaseInvoice with null token should use empty string`() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceCreateDTOResponse>>(relaxed = true)
            val dtoModel = mockk<PurchaseInvoiceCreateDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceCreateModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceCreateDTOResponse::class.java) } returns dtoModel
            every { sageModelMapper.purchaseInvoiceCreateMapper.map(dtoModel) } returns mappedModel
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.createPurchaseInvoice("", "B001", "PO123", "BILL123", "SUP001")
            } returns restfulResponse

            // when
            val flow = repository.createPurchaseInvoice("PO123", "BILL123", "SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun `createPurchaseInvoice with null branchCode should use empty string`() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceCreateDTOResponse>>(relaxed = true)
            val dtoModel = mockk<PurchaseInvoiceCreateDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceCreateModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceCreateDTOResponse::class.java) } returns dtoModel
            every { sageModelMapper.purchaseInvoiceCreateMapper.map(dtoModel) } returns mappedModel
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo() } returns null

            coEvery {
                sageAdapterApi.createPurchaseInvoice("token", "", "PO123", "BILL123", "SUP001")
            } returns restfulResponse

            // when
            val flow = repository.createPurchaseInvoice("PO123", "BILL123", "SUP001")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun `getPurchaseInvoiceDetail success should emit Result Success`() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceDetailDTOResponse>>(relaxed = true)
            val dtoModel = mockk<PurchaseInvoiceDetailDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceDetailModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceDetailDTOResponse::class.java) } returns dtoModel
            every { sageModelMapper.purchaseInvoiceDetailMapper.map(dtoModel) } returns mappedModel
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.getPurchaseInvoiceDetail("token", "B001", "PI123")
            } returns restfulResponse

            // when
            val flow = repository.getPurchaseInvoiceDetail("PI123")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun `getPurchaseInvoiceDetail returns null response should emit Result Error`() =
        runTest {
            // given
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.getPurchaseInvoiceDetail("token", "B001", "PI123")
            } returns null

            // when
            val flow = repository.getPurchaseInvoiceDetail("PI123")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun `getPurchaseInvoiceDetail throws IOException should emit Result Error`() =
        runTest {
            // given
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.getPurchaseInvoiceDetail("token", "B001", "PI123")
            } throws IOException()

            // when
            val flow = repository.getPurchaseInvoiceDetail("PI123")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun `getPurchaseInvoiceDetail with null token should use empty string`() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceDetailDTOResponse>>(relaxed = true)
            val dtoModel = mockk<PurchaseInvoiceDetailDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceDetailModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceDetailDTOResponse::class.java) } returns dtoModel
            every { sageModelMapper.purchaseInvoiceDetailMapper.map(dtoModel) } returns mappedModel
            every { appConfig.authToken } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.getPurchaseInvoiceDetail("", "B001", "PI123")
            } returns restfulResponse

            // when
            val flow = repository.getPurchaseInvoiceDetail("PI123")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun `getPurchaseInvoiceDetail with null branchInfo should use empty string`() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<PurchaseInvoiceDetailDTOResponse>>(relaxed = true)
            val dtoModel = mockk<PurchaseInvoiceDetailDTOResponse>()
            val mappedModel = mockk<PurchaseInvoiceDetailModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseInvoiceDetailDTOResponse::class.java) } returns dtoModel
            every { sageModelMapper.purchaseInvoiceDetailMapper.map(dtoModel) } returns mappedModel
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo() } returns null

            coEvery {
                sageAdapterApi.getPurchaseInvoiceDetail("token", "", "PI123")
            } returns restfulResponse

            // when
            val flow = repository.getPurchaseInvoiceDetail("PI123")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_purchaseBarcodeFind_success_expect_result_success() =
        runTest {
            // given
            val requestData = mockk<PurchaseBarcodeFindDTORequest>()
            val restfulResponse =
                mockk<RestfulResponse<PurchaseBarcodeFindDTOResponse>>(relaxed = true)
            val respModel = mockk<PurchaseBarcodeFindDTOResponse>()
            val mappedModel = mockk<PurchaseBarcodeFindListModel>()

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(PurchaseBarcodeFindDTOResponse::class.java) } returns respModel
            every { sageModelMapper.purchaseBarcodeFindMapper.map(respModel) } returns mappedModel

            coEvery {
                sageAdapterApi.purchaseBarcodeFind(
                    token = "token",
                    branchCode = "B001",
                    data = requestData,
                )
            } returns restfulResponse

            // when
            val flow = repository.purchaseBarcodeFind(requestData)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_purchaseBarcodeFind_parseError_expect_result_error() =
        runTest {
            // given
            val requestData = mockk<PurchaseBarcodeFindDTORequest>()
            val restfulResponse =
                mockk<RestfulResponse<PurchaseBarcodeFindDTOResponse>>(relaxed = true)

            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null
            every { restfulResponse.isSuccessful } returns true
            every {
                restfulResponse.bodyToObject(PurchaseBarcodeFindDTOResponse::class.java)
            } throws Exception()

            coEvery {
                sageAdapterApi.purchaseBarcodeFind(any(), any(), requestData)
            } returns restfulResponse

            // when
            val flow = repository.purchaseBarcodeFind(requestData)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_purchaseBarcodeFind_failure_expect_result_error() =
        runTest {
            // given
            val requestData = mockk<PurchaseBarcodeFindDTORequest>()
            val restfulResponse =
                mockk<RestfulResponse<PurchaseBarcodeFindDTOResponse>>(relaxed = true)

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"
            every { restfulResponse.isSuccessful } returns false

            coEvery {
                sageAdapterApi.purchaseBarcodeFind(any(), any(), requestData)
            } returns restfulResponse

            // when
            val flow = repository.purchaseBarcodeFind(requestData)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_purchaseBarcodeFind_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            val requestData = mockk<PurchaseBarcodeFindDTORequest>()
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.purchaseBarcodeFind("token", "B001", requestData)
            } returns null

            // when
            val flow = repository.purchaseBarcodeFind(requestData)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_purchaseBarcodeFind_when_iOException_expect_result_error() =
        runTest {
            // given
            val requestData = mockk<PurchaseBarcodeFindDTORequest>()
            every { appConfig.authToken?.token } returns ""
            every { appConfig.getBranchInfo()?.branchCode } returns ""

            coEvery {
                sageAdapterApi.purchaseBarcodeFind(any(), any(), requestData)
            } throws IOException()

            // when
            val flow = repository.purchaseBarcodeFind(requestData)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getBdList_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<BdDocumentDTOResponse>>(relaxed = true)
            val respModel = mockk<BdDocumentDTOResponse>()
            val model = mockk<BdDocumentMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(BdDocumentDTOResponse::class.java) } returns respModel
            every { sageModelMapper.bdDocumentMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.bdDocument(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } returns restfulResponse
            // when
            val result = repository.bdDocument()
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_getBdList_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<BdDocumentDTOResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(BdDocumentDTOResponse::class.java) } throws Exception()
            coEvery {
                sageAdapterApi.bdDocument(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } returns restfulResponse
            // when
            val result = repository.bdDocument()
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getBdList_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<BdDocumentDTOResponse>>(relaxed = true)
            val respModel = mockk<BdDocumentDTOResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(BdDocumentDTOResponse::class.java) } returns respModel
            coEvery {
                sageAdapterApi.bdDocument(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } returns restfulResponse
            // when
            val result = repository.bdDocument()
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getBdList_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.bdDocument(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } returns null
            // when
            val result = repository.bdDocument()
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getBdList_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.bdDocument(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } throws IOException()
            // when
            val result = repository.bdDocument()
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getBdDetail_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<BdDocumentNoDetailDTOResponse>>(relaxed = true)
            val respModel = mockk<BdDocumentNoDetailDTOResponse>()
            val model = mockk<BdDocumentNoDetailMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(BdDocumentNoDetailDTOResponse::class.java) } returns respModel
            every { sageModelMapper.bdDocumentNoDetailMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.bdDocumentNoDetail(
                    token = "test_token",
                    branchCode = "M04C",
                    docNo = "1234",
                )
            } returns restfulResponse
            // when
            val result = repository.bdDocumentNoDetail("1234")
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_getBdDetail_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<BdDocumentNoDetailDTOResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(BdDocumentNoDetailDTOResponse::class.java) } throws Exception()
            coEvery {
                sageAdapterApi.bdDocumentNoDetail(
                    token = "test_token",
                    branchCode = "M04C",
                    docNo = "1234",
                )
            } returns restfulResponse
            // when
            val result = repository.bdDocumentNoDetail("1234")
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getBdDetail_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<BdDocumentNoDetailDTOResponse>>(relaxed = true)
            val respModel = mockk<BdDocumentNoDetailDTOResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(BdDocumentNoDetailDTOResponse::class.java) } returns respModel
            coEvery {
                sageAdapterApi.bdDocumentNoDetail(
                    token = "test_token",
                    branchCode = "M04C",
                    docNo = "1234",
                )
            } returns restfulResponse
            // when
            val result = repository.bdDocumentNoDetail("1234")
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getBdDetail_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.bdDocumentNoDetail(
                    token = "test_token",
                    branchCode = "M04C",
                    docNo = "1234",
                )
            } returns null
            // when
            val result = repository.bdDocumentNoDetail("1234")
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getBdDetail_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.bdDocumentNoDetail(
                    token = "test_token",
                    branchCode = "M04C",
                    docNo = "1234",
                )
            } throws IOException()
            // when
            val result = repository.bdDocumentNoDetail("1234")
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getMasterMapping_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<BdSearchTypeDTOResponse>>(relaxed = true)
            val respModel = mockk<BdSearchTypeDTOResponse>()
            val model = mockk<BdSearchTypeMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(BdSearchTypeDTOResponse::class.java) } returns respModel
            every { sageModelMapper.bdSearchTypeMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.getBdSearchType(
                    token = "test_token",
                    branchCode = "M04C",
                    mappingType = MasterMapping.CAMPAIGN_DISCOUNT_TYPE,
                )
            } returns restfulResponse
            // when
            val result = repository.bdDocumentSearchType(MasterMapping.CAMPAIGN_DISCOUNT_TYPE)
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_getMasterMapping_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<BdSearchTypeDTOResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(BdSearchTypeDTOResponse::class.java) } throws Exception()
            coEvery {
                sageAdapterApi.getBdSearchType(
                    token = "test_token",
                    branchCode = "M04C",
                    mappingType = MasterMapping.CAMPAIGN_DISCOUNT_TYPE,
                )
            } returns restfulResponse
            // when
            val result = repository.bdDocumentSearchType(MasterMapping.CAMPAIGN_DISCOUNT_TYPE)
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getMasterMapping_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<BdSearchTypeDTOResponse>>(relaxed = true)
            val respModel = mockk<BdSearchTypeDTOResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(BdSearchTypeDTOResponse::class.java) } returns respModel
            coEvery {
                sageAdapterApi.getBdSearchType(
                    token = "test_token",
                    branchCode = "M04C",
                    mappingType = MasterMapping.CAMPAIGN_DISCOUNT_TYPE,
                )
            } returns restfulResponse
            // when
            val result = repository.bdDocumentSearchType(MasterMapping.CAMPAIGN_DISCOUNT_TYPE)
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getMasterMapping_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.getBdSearchType(
                    token = "test_token",
                    branchCode = "M04C",
                    mappingType = MasterMapping.CAMPAIGN_DISCOUNT_TYPE,
                )
            } returns null
            // when
            val result = repository.bdDocumentSearchType(MasterMapping.CAMPAIGN_DISCOUNT_TYPE)
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getMasterMapping_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.getBdSearchType(
                    token = "test_token",
                    branchCode = "M04C",
                    mappingType = MasterMapping.CAMPAIGN_DISCOUNT_TYPE,
                )
            } throws IOException()
            // when
            val result = repository.bdDocumentSearchType(MasterMapping.CAMPAIGN_DISCOUNT_TYPE)
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_bdDocumentValidate_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<BdDocumentValidateDTOResponse>>(relaxed = true)
            val respModel = mockk<BdDocumentValidateDTOResponse>()
            val model = mockk<BdDocumentValidateMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(BdDocumentValidateDTOResponse::class.java) } returns respModel
            every { sageModelMapper.bdValidateBarcodeMapper.map(respModel) } returns model
            val expiryDate = Date()
            coEvery {
                sageAdapterApi.bdDocumentValidate(
                    token = "test_token",
                    branchCode = "M04C",
                    discountType = "discountType",
                    barcode = "1234455",
                    expiryDate = expiryDate,
                )
            } returns restfulResponse
            // when
            val flow =
                repository.bdDocumentValidate(
                    discountType = "discountType",
                    barcode = "1234455",
                    expiryDate = expiryDate,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_bdDocumentValidate_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<BdDocumentValidateDTOResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(BdDocumentValidateDTOResponse::class.java) } throws Exception()
            val expiryDate = Date()
            coEvery {
                sageAdapterApi.bdDocumentValidate(
                    token = "test_token",
                    branchCode = "M04C",
                    discountType = "discountType",
                    barcode = "1234455",
                    expiryDate = expiryDate,
                )
            } returns restfulResponse
            // when
            val flow =
                repository.bdDocumentValidate(
                    discountType = "discountType",
                    barcode = "1234455",
                    expiryDate = expiryDate,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_bdDocumentValidate_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<BdDocumentValidateDTOResponse>>(relaxed = true)
            val respModel = mockk<BdDocumentValidateDTOResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(BdDocumentValidateDTOResponse::class.java) } returns respModel
            val expiryDate = Date()
            coEvery {
                sageAdapterApi.bdDocumentValidate(
                    token = "test_token",
                    branchCode = "M04C",
                    discountType = "discountType",
                    barcode = "1234455",
                    expiryDate = expiryDate,
                )
            } returns restfulResponse
            // when
            val flow =
                repository.bdDocumentValidate(
                    discountType = "discountType",
                    barcode = "1234455",
                    expiryDate = expiryDate,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_bdDocumentValidate_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            val expiryDate = Date()
            coEvery {
                sageAdapterApi.bdDocumentValidate(
                    token = "test_token",
                    branchCode = "M04C",
                    discountType = "discountType",
                    barcode = "1234455",
                    expiryDate = expiryDate,
                )
            } returns null
            // when
            val flow =
                repository.bdDocumentValidate(
                    discountType = "discountType",
                    barcode = "1234455",
                    expiryDate = expiryDate,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_bdDocumentValidate_when_iOException_expect_result_error() =
        runTest {
            // given
            val expiryDate = Date()
            coEvery {
                sageAdapterApi.bdDocumentValidate(
                    token = "test_token",
                    branchCode = "M04C",
                    discountType = "discountType",
                    barcode = "1234455",
                    expiryDate = expiryDate,
                )
            } throws IOException()
            // when
            val flow =
                repository.bdDocumentValidate(
                    discountType = "discountType",
                    barcode = "1234455",
                    expiryDate = expiryDate,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_bdDocumentSubmit_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<BdDocumentSubmitDTOResponse>>(relaxed = true)
            val respModel = mockk<BdDocumentSubmitDTOResponse>()
            val model = mockk<BdDocumentSubmitMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(BdDocumentSubmitDTOResponse::class.java) } returns respModel
            every { sageModelMapper.bdDocumentSubmitMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.bdDocumentSubmit(
                    token = "test_token",
                    branchCode = "M04C",
                    discountType = "discountType",
                    documentNumber = "documentNumber",
                    deviceName = "t12345678",
                    items = emptyList(),
                )
            } returns restfulResponse
            // when
            val flow =
                repository.bdDocumentSubmit(
                    discountType = "discountType",
                    documentNumber = "documentNumber",
                    items = emptyList(),
                )
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_bdDocumentSubmit_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<BdDocumentSubmitDTOResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(BdDocumentSubmitDTOResponse::class.java) } throws Exception()
            coEvery {
                sageAdapterApi.bdDocumentSubmit(
                    token = "test_token",
                    branchCode = "M04C",
                    discountType = "discountType",
                    documentNumber = "documentNumber",
                    deviceName = "t12345678",
                    items = emptyList(),
                )
            } returns restfulResponse
            // when
            repository.bdDocumentSubmit(
                discountType = "discountType",
                documentNumber = "documentNumber",
                items = emptyList(),
            )
            val flow =
                repository.bdDocumentSubmit(
                    discountType = "discountType",
                    documentNumber = "documentNumber",
                    items = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_bdDocumentSubmit_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<BdDocumentSubmitDTOResponse>>(relaxed = true)
            val respModel = mockk<BdDocumentSubmitDTOResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(BdDocumentSubmitDTOResponse::class.java) } returns respModel
            coEvery {
                sageAdapterApi.bdDocumentSubmit(
                    token = "test_token",
                    branchCode = "M04C",
                    discountType = "discountType",
                    documentNumber = "documentNumber",
                    deviceName = "t12345678",
                    items = emptyList(),
                )
            } returns restfulResponse
            // when
            val flow =
                repository.bdDocumentSubmit(
                    discountType = "discountType",
                    documentNumber = "documentNumber",
                    items = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_bdDocumentSubmit_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.bdDocumentSubmit(
                    token = "test_token",
                    branchCode = "M04C",
                    discountType = "discountType",
                    documentNumber = "documentNumber",
                    deviceName = "t12345678",
                    items = emptyList(),
                )
            } returns null
            // when
            repository.bdDocumentSubmit(
                discountType = "discountType",
                documentNumber = "documentNumber",
                items = emptyList(),
            )
            val flow =
                repository.bdDocumentSubmit(
                    discountType = "discountType",
                    documentNumber = "documentNumber",
                    items = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_bdDocumentSubmit_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.bdDocumentSubmit(
                    token = "test_token",
                    branchCode = "M04C",
                    discountType = "discountType",
                    documentNumber = "documentNumber",
                    deviceName = "t12345678",
                    items = emptyList(),
                )
            } throws IOException()
            // when
            val flow =
                repository.bdDocumentSubmit(
                    discountType = "discountType",
                    documentNumber = "documentNumber",
                    items = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSdToteList_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteListResponse>>(relaxed = true)
            val respModel = mockk<SdToteListResponse>()
            val model = mockk<SdToteListMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteListResponse::class.java) } returns respModel
            every { sageModelMapper.sdToteListMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.getSdToteList(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } returns restfulResponse
            // when
            val flow = repository.getSdToteList()
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_getSdToteList_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteListResponse>>(relaxed = true)
            val respModel = mockk<SdToteListResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(SdToteListResponse::class.java) } returns respModel
            coEvery {
                sageAdapterApi.getSdToteList(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } returns restfulResponse
            // when
            val flow = repository.getSdToteList()
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSdToteList_failure_exception_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteListResponse>>(relaxed = true)
            every { restfulResponse.t } returns Exception("error")
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(SdToteListResponse::class.java) } returns null
            coEvery {
                sageAdapterApi.getSdToteList(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } returns restfulResponse
            // when
            val flow = repository.getSdToteList()

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSdToteList_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.getSdToteList(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } returns null
            // when
            val flow = repository.getSdToteList()
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getSdToteList_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.getSdToteList(
                    token = "test_token",
                    branchCode = "M04C",
                )
            } throws IOException("error")
            // when
            val flow = repository.getSdToteList()
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_openTote_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<OpenToteResponse>>(relaxed = true)
            val respModel = mockk<OpenToteResponse>()
            val model = mockk<SdToteDetailMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(OpenToteResponse::class.java) } returns respModel
            every { sageModelMapper.openToteMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.openTote(
                    token = "test_token",
                    branchCode = "M04C",
                    toteCode = "TL001234",
                )
            } returns restfulResponse
            // when
            val flow = repository.openTote("TL001234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_openTote_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<OpenToteResponse>>(relaxed = true)
            val respModel = mockk<OpenToteResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(OpenToteResponse::class.java) } returns respModel
            coEvery {
                sageAdapterApi.openTote(
                    token = "test_token",
                    branchCode = "M04C",
                    toteCode = "TL001234",
                )
            } returns restfulResponse
            // when
            val flow = repository.openTote("TL001234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_openTote_failure_exception_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<OpenToteResponse>>(relaxed = true)
            every { restfulResponse.t } returns Exception("error")
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(OpenToteResponse::class.java) } returns null
            coEvery {
                sageAdapterApi.openTote(
                    token = "test_token",
                    branchCode = "M04C",
                    toteCode = "TL001234",
                )
            } returns restfulResponse
            // when
            val flow = repository.openTote("TL001234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_openTote_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.openTote(
                    token = "test_token",
                    branchCode = "M04C",
                    toteCode = "TL001234",
                )
            } returns null
            // when
            val flow = repository.openTote("TL001234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_openTote_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.openTote(
                    token = "test_token",
                    branchCode = "M04C",
                    toteCode = "TL001234",
                )
            } throws IOException("error")
            // when
            val flow = repository.openTote("TL001234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_searchToteBySdNo_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SearchSdToteResponse>>(relaxed = true)
            val respModel = mockk<SearchSdToteResponse>()
            val model = mockk<SdToteDetailMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SearchSdToteResponse::class.java) } returns respModel
            every { sageModelMapper.searchToteMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.searchToteBySdNo(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "SD001234",
                )
            } returns restfulResponse
            // when
            val flow = repository.searchToteBySdNo("SD001234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_searchToteBySdNo_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SearchSdToteResponse>>(relaxed = true)
            val respModel = mockk<SearchSdToteResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(SearchSdToteResponse::class.java) } returns respModel
            coEvery {
                sageAdapterApi.searchToteBySdNo(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "SD001234",
                )
            } returns restfulResponse
            // when
            val flow = repository.searchToteBySdNo("SD001234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_searchToteBySdNo_failure_exception_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SearchSdToteResponse>>(relaxed = true)
            every { restfulResponse.t } returns Exception("error")
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(SearchSdToteResponse::class.java) } returns null
            coEvery {
                sageAdapterApi.searchToteBySdNo(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "SD001234",
                )
            } returns restfulResponse
            // when
            val flow = repository.searchToteBySdNo("SD001234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_searchToteBySdNo_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.searchToteBySdNo(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "SD001234",
                )
            } returns null
            // when
            val flow = repository.searchToteBySdNo("SD001234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_searchToteBySdNo_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.searchToteBySdNo(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "SD001234",
                )
            } throws IOException("error")
            // when
            val flow = repository.searchToteBySdNo("SD001234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteSubmit_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteSubmitResponse>>(relaxed = true)
            val respModel = mockk<SdToteSubmitResponse>()
            val model = mockk<SdToteSubmitMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteSubmitResponse::class.java) } returns respModel
            every { sageModelMapper.sdToteSubmitMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.sdToteSubmit(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "SKU123456",
                    expList = emptyList(),
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteSubmit(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "SKU123456",
                    expList = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_sdToteSubmit_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteSubmitResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteSubmitResponse::class.java) } throws Exception()
            coEvery {
                sageAdapterApi.sdToteSubmit(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "SKU123456",
                    expList = emptyList(),
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteSubmit(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "SKU123456",
                    expList = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteSubmit_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteSubmitResponse>>(relaxed = true)
            val respModel = mockk<SdToteSubmitResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(SdToteSubmitResponse::class.java) } returns respModel
            coEvery {
                sageAdapterApi.sdToteSubmit(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "SKU123456",
                    expList = emptyList(),
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteSubmit(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "SKU123456",
                    expList = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteSubmit_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.sdToteSubmit(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "SKU123456",
                    expList = emptyList(),
                )
            } returns null
            // when
            val flow =
                repository.sdToteSubmit(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "SKU123456",
                    expList = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteSubmit_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.sdToteSubmit(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "SKU123456",
                    expList = emptyList(),
                )
            } throws IOException()
            // when
            val flow =
                repository.sdToteSubmit(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "SKU123456",
                    expList = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteLowestFactor_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteLowestFactorResponse>>(relaxed = true)
            val respModel = mockk<SdToteLowestFactorResponse>()
            val model = mockk<SdToteLowestFactorMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteLowestFactorResponse::class.java) } returns respModel
            every { sageModelMapper.sdToteLowestFactorMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.sdToteLowestFactor(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                    barcode = "barcode",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteLowestFactor(
                    sdNo = "sdNo",
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_sdToteLowestFactor_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteLowestFactorResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteLowestFactorResponse::class.java) } throws Exception()
            coEvery {
                sageAdapterApi.sdToteLowestFactor(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                    barcode = "barcode",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteLowestFactor(
                    sdNo = "sdNo",
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteLowestFactor_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteLowestFactorResponse>>(relaxed = true)
            val respModel = mockk<SdToteLowestFactorResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(SdToteLowestFactorResponse::class.java) } returns respModel
            coEvery {
                sageAdapterApi.sdToteLowestFactor(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                    barcode = "barcode",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteLowestFactor(
                    sdNo = "sdNo",
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteLowestFactor_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.sdToteLowestFactor(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                    barcode = "barcode",
                )
            } returns null
            // when
            val flow =
                repository.sdToteLowestFactor(
                    sdNo = "sdNo",
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteLowestFactor_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.sdToteLowestFactor(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                    barcode = "barcode",
                )
            } throws IOException()
            // when
            val flow =
                repository.sdToteLowestFactor(
                    sdNo = "sdNo",
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteApprove_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteApproveResponse>>(relaxed = true)
            val respModel = mockk<SdToteApproveResponse>()
            val model = mockk<SdToteApproveMappingModel>()
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteApproveResponse::class.java) } returns respModel
            every { sageModelMapper.sdToteApproveMapper.map(respModel) } returns model
            coEvery {
                sageAdapterApi.sdToteApprove(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteApprove(
                    sdNo = "sdNo",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_sdToteApprove_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteApproveResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteApproveResponse::class.java) } throws Exception()
            coEvery {
                sageAdapterApi.sdToteApprove(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteApprove(
                    sdNo = "sdNo",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteApprove_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse = mockk<RestfulResponse<SdToteApproveResponse>>(relaxed = true)
            val respModel = mockk<SdToteApproveResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(SdToteApproveResponse::class.java) } returns respModel
            coEvery {
                sageAdapterApi.sdToteApprove(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteApprove(
                    sdNo = "sdNo",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteApprove_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.sdToteApprove(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                )
            } returns null
            // when
            val flow =
                repository.sdToteApprove(
                    sdNo = "sdNo",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteApprove_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.sdToteApprove(
                    token = "test_token",
                    branchCode = "M04C",
                    sdNo = "sdNo",
                )
            } throws IOException()
            // when
            val flow =
                repository.sdToteApprove(
                    sdNo = "sdNo",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteDiscountsConfig_success_expect_result_success() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<SdToteDiscountsConfigResponse>>(relaxed = true)
            val respModel = mockk<SdToteDiscountsConfigResponse>(relaxed = true)
            val model = mockk<SdToteDiscountsConfigMappingModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteDiscountsConfigResponse::class.java) } returns respModel

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "branchCode"

            every { sageModelMapper.sdToteDiscountsConfigMapping.map(respModel) } returns model

            coEvery {
                sageAdapterApi.sdToteDiscountsConfig(
                    token = "token",
                    branchCode = "branchCode",
                    barcode = "barcode",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteDiscountsConfig(
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_sdToteDiscountsConfig_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<SdToteDiscountsConfigResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteDiscountsConfigResponse::class.java) } throws Exception()

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "branchCode"

            coEvery {
                sageAdapterApi.sdToteDiscountsConfig(
                    token = "token",
                    branchCode = "branchCode",
                    barcode = "barcode",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteDiscountsConfig(
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteDiscountsConfig_failure_expect_result_error() =
        runTest {
            // given
            val restfulResponse =
                mockk<RestfulResponse<SdToteDiscountsConfigResponse>>(relaxed = true)
            val respModel = mockk<SdToteDiscountsConfigResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(SdToteDiscountsConfigResponse::class.java) } returns respModel

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "branchCode"

            coEvery {
                sageAdapterApi.sdToteDiscountsConfig(
                    token = "token",
                    branchCode = "branchCode",
                    barcode = "barcode",
                )
            } returns restfulResponse
            // when
            val flow =
                repository.sdToteDiscountsConfig(
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteDiscountsConfig_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.sdToteDiscountsConfig(
                    token = "token",
                    branchCode = "branchCode",
                    barcode = "barcode",
                )
            } returns null
            // when
            val flow =
                repository.sdToteDiscountsConfig(
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteDiscountsConfig_when_iOException_expect_result_error() =
        runTest {
            // given
            coEvery {
                sageAdapterApi.sdToteDiscountsConfig(
                    token = "token",
                    branchCode = "branchCode",
                    barcode = "barcode",
                )
            } throws IOException()
            // when
            val flow =
                repository.sdToteDiscountsConfig(
                    barcode = "barcode",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_qrDocumentSubmit_success_expect_result_success() =
        runTest {
            // given
            val requestData = mockk<QrDocumentSubmitDTORequest>()
            val restfulResponse =
                mockk<RestfulResponse<QrDocumentSubmitDTOResponse>>(relaxed = true)
            val respModel = mockk<QrDocumentSubmitDTOResponse>()
            val mappedModel = mockk<QrDocumentSubmitMappingModel>()

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(QrDocumentSubmitDTOResponse::class.java) } returns respModel
            every { sageModelMapper.qrDocumentSubmitMapper.map(respModel) } returns mappedModel

            coEvery {
                sageAdapterApi.qrDocumentSubmit(
                    token = "token",
                    branchCode = "B001",
                    data = requestData,
                )
            } returns restfulResponse

            // when
            val flow = repository.qrDocumentSubmit(requestData)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(mappedModel, (result as Result.Success).data)
            }
        }

    @Test
    fun test_qrDocumentSubmit_parseError_expect_result_error() =
        runTest {
            // given
            val requestData = mockk<QrDocumentSubmitDTORequest>()
            val restfulResponse =
                mockk<RestfulResponse<QrDocumentSubmitDTOResponse>>(relaxed = true)

            every { appConfig.authToken?.token } returns null
            every { appConfig.getBranchInfo()?.branchCode } returns null
            every { restfulResponse.isSuccessful } returns true
            every {
                restfulResponse.bodyToObject(QrDocumentSubmitDTOResponse::class.java)
            } throws Exception()

            coEvery {
                sageAdapterApi.qrDocumentSubmit(any(), any(), requestData)
            } returns restfulResponse

            // when
            val flow = repository.qrDocumentSubmit(requestData)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_qrDocumentSubmit_failure_expect_result_error() =
        runTest {
            // given
            val requestData = mockk<QrDocumentSubmitDTORequest>()
            val restfulResponse =
                mockk<RestfulResponse<QrDocumentSubmitDTOResponse>>(relaxed = true)

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"
            every { restfulResponse.isSuccessful } returns false

            coEvery {
                sageAdapterApi.qrDocumentSubmit(any(), any(), requestData)
            } returns restfulResponse

            // when
            val flow = repository.qrDocumentSubmit(requestData)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_qrDocumentSubmit_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            val requestData = mockk<QrDocumentSubmitDTORequest>()
            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "B001"

            coEvery {
                sageAdapterApi.qrDocumentSubmit("token", "B001", requestData)
            } returns null

            // when
            val flow = repository.qrDocumentSubmit(requestData)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_qrDocumentSubmit_when_iOException_expect_result_error() =
        runTest {
            // given
            val requestData = mockk<QrDocumentSubmitDTORequest>()
            every { appConfig.authToken?.token } returns ""
            every { appConfig.getBranchInfo()?.branchCode } returns ""

            coEvery {
                sageAdapterApi.qrDocumentSubmit(any(), any(), requestData)
            } throws IOException()

            // when
            val flow = repository.qrDocumentSubmit(requestData)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentToteDocumentSubmitAdjust_success_expect_result_success() =
        runTest {
            // given
            val request = mockk<SdToteSubmitAdjustRequest>()
            val restfulResponse =
                mockk<RestfulResponse<SdToteSubmitAdjustResponse>>(relaxed = true)
            val respModel = mockk<SdToteSubmitAdjustResponse>(relaxed = true)
            val model = mockk<SdToteSubmitAdjustModel>()

            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteSubmitAdjustResponse::class.java) } returns respModel

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "branchCode"

            every { sageModelMapper.sdToteSubmitAdjustMapper.map(respModel) } returns model

            coEvery {
                sageAdapterApi.shipmentToteDocumentSubmitAdjust(
                    token = "token",
                    branchCode = "branchCode",
                    data = request,
                )
            } returns restfulResponse
            // when
            val flow =
                repository.shipmentToteDocumentSubmitAdjust(
                    data = request,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = model), result)
            }
        }

    @Test
    fun test_shipmentToteDocumentSubmitAdjust_success_but_parse_error_expect_result_error() =
        runTest {
            // given
            // given
            val request = mockk<SdToteSubmitAdjustRequest>()
            val restfulResponse =
                mockk<RestfulResponse<SdToteSubmitAdjustResponse>>(relaxed = true)
            every { restfulResponse.isSuccessful } returns true
            every { restfulResponse.bodyToObject(SdToteSubmitAdjustResponse::class.java) } throws Exception()

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "branchCode"

            coEvery {
                sageAdapterApi.shipmentToteDocumentSubmitAdjust(
                    token = "token",
                    branchCode = "branchCode",
                    data = request,
                )
            } returns restfulResponse
            // when
            val flow =
                repository.shipmentToteDocumentSubmitAdjust(
                    data = request,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentToteDocumentSubmitAdjust_failure_expect_result_error() =
        runTest {
            // given
            val request = mockk<SdToteSubmitAdjustRequest>()
            val restfulResponse =
                mockk<RestfulResponse<SdToteSubmitAdjustResponse>>(relaxed = true)
            val respModel = mockk<SdToteSubmitAdjustResponse>(relaxed = true)
            every { restfulResponse.isSuccessful } returns false
            every { restfulResponse.bodyToObject(SdToteSubmitAdjustResponse::class.java) } returns respModel

            every { appConfig.authToken?.token } returns "token"
            every { appConfig.getBranchInfo()?.branchCode } returns "branchCode"

            coEvery {
                sageAdapterApi.shipmentToteDocumentSubmitAdjust(
                    token = "token",
                    branchCode = "branchCode",
                    data = request,
                )
            } returns restfulResponse
            // when
            val flow =
                repository.shipmentToteDocumentSubmitAdjust(
                    data = request,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentToteDocumentSubmitAdjust_when_resp_is_null_expect_result_error() =
        runTest {
            // given
            val request = mockk<SdToteSubmitAdjustRequest>()
            coEvery {
                sageAdapterApi.shipmentToteDocumentSubmitAdjust(
                    token = "token",
                    branchCode = "branchCode",
                    data = request,
                )
            } returns null
            // when
            val flow =
                repository.shipmentToteDocumentSubmitAdjust(
                    data = request,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentToteDocumentSubmitAdjust_when_iOException_expect_result_error() =
        runTest {
            // given
            val request = mockk<SdToteSubmitAdjustRequest>()
            coEvery {
                sageAdapterApi.shipmentToteDocumentSubmitAdjust(
                    token = "token",
                    branchCode = "branchCode",
                    data = request,
                )
            } throws IOException()
            // when
            val flow =
                repository.shipmentToteDocumentSubmitAdjust(
                    data = request,
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }
}
