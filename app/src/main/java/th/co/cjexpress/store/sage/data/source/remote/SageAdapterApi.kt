package th.co.cjexpress.store.sage.data.source.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import th.co.cjexpress.store.sage.constants.HEADER_AUTHORIZATION
import th.co.cjexpress.store.sage.constants.HEADER_BRANCH
import th.co.cjexpress.store.sage.constants.HEADER_X_REQUEST_ID
import th.co.cjexpress.store.sage.constants.HEADER_X_TIMEOUT
import th.co.cjexpress.store.sage.constants.HEADER_X_TIMESTAMP
import th.co.cjexpress.store.sage.constants.MasterMapping
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_BARCODE_DISCOUNT_DOCUMENT
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_BARCODE_DISCOUNT_DOCUMENT_NO
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_BARCODE_DISCOUNT_DOCUMENT_SUBMIT
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_BARCODE_DISCOUNT_SEARCH_TYPE
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_BARCODE_DISCOUNT_VALIDATE
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_CAMPAIGN_DISCOUNT_CONFIG
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_BARCODE_FIND
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_PURCHASE_INVOICE_CALCULATE
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
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_TOTE_DOCUMENT
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_TOTE_DOCUMENT_APPROVE
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_TOTE_DOCUMENT_DETAIL
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_TOTE_DOCUMENT_LOWEST_FACTOR
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_TOTE_DOCUMENT_SUBMIT
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_TOTE_DOCUMENT_SUBMIT_ADJUST
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SHIPMENT_TOTE_DOCUMENT_TOTE
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_SKU_CHECK
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.model.masterMapping.BdSearchTypeDTOResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.DocumentSDResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.OrderReceiveSummaryResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcodeRequest
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcodeResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentOpenRequest
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentItemRequest
import th.co.cjexpress.store.sage.data.model.orderReceive.SkuCheckRequest
import th.co.cjexpress.store.sage.data.model.orderReceive.SkuCheckResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.OpenToteRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.OpenToteResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteApproveRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteApproveResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteDiscountsConfigRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteDiscountsConfigResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteListResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteLowestFactorRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteLowestFactorResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitExpRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SearchSdToteResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseBarcodeFindDTORequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseBarcodeFindDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCalculateDTORequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCalculateDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateRequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDocumentListDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftItemRequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftRequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierListDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceValidateBillDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceValidateBillRequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDocumentListDTOResponse
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.request.QrDocumentSubmitDTORequest
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.response.QrDocumentSubmitDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentNoDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentSubmitDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentValidateDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentValidateRequest
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.UpsertBdProductRequest
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.UpsertBdRequest
import th.co.cjexpress.store.sage.data.network.RestfulResponse
import th.co.cjexpress.store.sage.data.network.getRestfulClient
import th.co.cjexpress.store.sage.data.network.getSafeRequestTimeout
import th.co.cjexpress.store.sage.di.AppDependencyProvider
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringPosAdminDateFormat
import th.co.cjexpress.store.sage.utils.UUIDUtils.getCurrentUUID
import java.util.Date

class SageAdapterApi(
    private val appConfig: AppConfig,
) {
    private fun getHeaders(
        token: String,
        branchCode: String,
        timeout: Int?,
        timestamp: Long? = null,
    ): HashMap<String, String> {
        val map = HashMap<String, String>()
        map[HEADER_AUTHORIZATION] = "Bearer $token"
        map[HEADER_BRANCH] = branchCode
        map[HEADER_X_TIMEOUT] = timeout.toString()
        getCurrentUUID()?.let { uuid ->
            map[HEADER_X_REQUEST_ID] = uuid
        }
        timestamp?.let {
            map[HEADER_X_TIMESTAMP] = it.toString()
        }
        return map
    }

    suspend fun getSdToteList(
        token: String,
        branchCode: String,
    ): RestfulResponse<SdToteListResponse>? {
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SHIPMENT_TOTE_DOCUMENT)?.timeout)

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<SdToteListResponse>(
                    serviceName = SERVICE_SHIPMENT_TOTE_DOCUMENT,
                    header = getHeaders(token, branchCode, timeout),
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun openTote(
        token: String,
        branchCode: String,
        toteCode: String,
    ): RestfulResponse<OpenToteResponse>? {
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SHIPMENT_TOTE_DOCUMENT_TOTE)?.timeout)
        val body =
            OpenToteRequest(
                toteCode = toteCode,
            )
        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<OpenToteResponse>(
                    serviceName = SERVICE_SHIPMENT_TOTE_DOCUMENT_TOTE,
                    header = getHeaders(token, branchCode, timeout),
                    body = body,
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }

    suspend fun searchToteBySdNo(
        token: String,
        branchCode: String,
        sdNo: String,
    ): RestfulResponse<SearchSdToteResponse>? {
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SHIPMENT_TOTE_DOCUMENT_DETAIL)?.timeout)

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<SearchSdToteResponse>(
                    serviceName = SERVICE_SHIPMENT_TOTE_DOCUMENT_DETAIL,
                    header = getHeaders(token, branchCode, timeout),
                    pathParams = hashMapOf("sdNo" to sdNo),
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun sdToteSubmit(
        token: String,
        branchCode: String,
        sdNo: String,
        toteCode: String,
        barcode: String,
        skuCode: String,
        discountType: String,
        actualQty: Int,
        expList: List<ISdToteExpiryDateModel>? = null,
    ): RestfulResponse<SdToteSubmitResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SHIPMENT_TOTE_DOCUMENT_SUBMIT)?.timeout)

        val requestBody =
            SdToteSubmitRequest(
                sdNo = sdNo,
                toteCode = toteCode,
                barcode = barcode,
                skuCode = skuCode,
                discountType = discountType,
                actualQty = actualQty,
                data =
                    expList?.map {
                        SdToteSubmitExpRequest(
                            qty = it.qty,
                            actualQty = it.actualQty,
                            lotNo = it.lotNo,
                            expDate = it.expDate,
                        )
                    } ?: emptyList(),
            )

        val bodyJson = jsonUtils.toJson(requestBody)

        return withContext(Dispatchers.IO) {
            getRestfulClient<SdToteSubmitResponse>(
                serviceName = SERVICE_SHIPMENT_TOTE_DOCUMENT_SUBMIT,
                header = getHeaders(token, branchCode, timeout),
                isJsonBody = true,
                body = bodyJson,
                timeout = timeout.toLong(),
            )?.requestPost()
        }
    }

    suspend fun sdToteLowestFactor(
        token: String,
        branchCode: String,
        sdNo: String,
        barcode: String,
    ): RestfulResponse<SdToteLowestFactorResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_SHIPMENT_TOTE_DOCUMENT_LOWEST_FACTOR,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<SdToteLowestFactorResponse>(
                    serviceName = SERVICE_SHIPMENT_TOTE_DOCUMENT_LOWEST_FACTOR,
                    header = getHeaders(token, branchCode, timeout),
                    pathParams = hashMapOf("sdNo" to sdNo),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            SdToteLowestFactorRequest(barcode),
                        ),
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }

    suspend fun sdToteApprove(
        token: String,
        branchCode: String,
        sdNo: String,
    ): RestfulResponse<SdToteApproveResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SHIPMENT_TOTE_DOCUMENT_APPROVE)?.timeout)

        return withContext(Dispatchers.IO) {
            getRestfulClient<SdToteApproveResponse>(
                serviceName = SERVICE_SHIPMENT_TOTE_DOCUMENT_APPROVE,
                header = getHeaders(token, branchCode, timeout),
                isJsonBody = true,
                body = jsonUtils.toJson(SdToteApproveRequest(sdNo = sdNo)),
                timeout = timeout.toLong(),
            )?.requestPost()
        }
    }

    suspend fun sdToteDiscountsConfig(
        token: String,
        branchCode: String,
        barcode: String,
    ): RestfulResponse<SdToteDiscountsConfigResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_CAMPAIGN_DISCOUNT_CONFIG)?.timeout)

        return withContext(Dispatchers.IO) {
            getRestfulClient<SdToteDiscountsConfigResponse>(
                serviceName = SERVICE_CAMPAIGN_DISCOUNT_CONFIG,
                header = getHeaders(token, branchCode, timeout),
                isJsonBody = true,
                body =
                    jsonUtils.toJson(
                        SdToteDiscountsConfigRequest(
                            barcodes = arrayListOf(barcode),
                        ),
                    ),
                timeout = timeout.toLong(),
            )?.requestPost()
        }
    }

    suspend fun bdDocument(
        token: String,
        branchCode: String,
    ): RestfulResponse<BdDocumentDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_BARCODE_DISCOUNT_DOCUMENT)?.timeout)

        return withContext(Dispatchers.IO) {
            getRestfulClient<BdDocumentDTOResponse>(
                serviceName = SERVICE_BARCODE_DISCOUNT_DOCUMENT,
                header = getHeaders(token, branchCode, timeout),
                isJsonBody = true,
                timeout = timeout.toLong(),
            )?.requestGet()
        }
    }

    suspend fun bdDocumentNoDetail(
        token: String,
        branchCode: String,
        docNo: String,
    ): RestfulResponse<BdDocumentNoDetailDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_BARCODE_DISCOUNT_DOCUMENT_NO)?.timeout)

        return withContext(Dispatchers.IO) {
            getRestfulClient<BdDocumentNoDetailDTOResponse>(
                serviceName = SERVICE_BARCODE_DISCOUNT_DOCUMENT_NO,
                header = getHeaders(token, branchCode, timeout),
                pathParams = hashMapOf("docNo" to docNo),
                isJsonBody = true,
                timeout = timeout.toLong(),
            )?.requestGet()
        }
    }

    suspend fun bdDocumentSubmit(
        token: String,
        branchCode: String,
        documentNumber: String,
        discountType: String,
        deviceName: String,
        items: List<BdDetailProductUIModel>,
    ): RestfulResponse<BdDocumentSubmitDTOResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(
                appConfig.getServiceConfig(SERVICE_BARCODE_DISCOUNT_DOCUMENT_SUBMIT)?.timeout,
            )

        return withContext(Dispatchers.IO) {
            getRestfulClient<BdDocumentSubmitDTOResponse>(
                serviceName = SERVICE_BARCODE_DISCOUNT_DOCUMENT_SUBMIT,
                header = getHeaders(token, branchCode, timeout),
                isJsonBody = true,
                body =
                    jsonUtils.toJson(
                        UpsertBdRequest(
                            documentNumber = documentNumber,
                            discountType = discountType,
                            deviceName = deviceName,
                            items =
                                items.map { product ->
                                    UpsertBdProductRequest(
                                        barcode = product.barcode,
                                        expiryDate = product.expiredDate!!.toStringPosAdminDateFormat(),
                                        requestQty = product.numberOfDiscounted!!,
                                    )
                                },
                        ),
                    ),
                timeout = timeout.toLong(),
            )?.requestPost()
        }
    }

    suspend fun getBdSearchType(
        token: String,
        branchCode: String,
        mappingType: MasterMapping,
    ): RestfulResponse<BdSearchTypeDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_BARCODE_DISCOUNT_SEARCH_TYPE)?.timeout)

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<BdSearchTypeDTOResponse>(
                    serviceName = SERVICE_BARCODE_DISCOUNT_SEARCH_TYPE,
                    header = getHeaders(token, branchCode, timeout),
                    pathParams = hashMapOf("type" to mappingType.name),
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun bdDocumentValidate(
        token: String,
        branchCode: String,
        discountType: String,
        barcode: String,
        expiryDate: Date? = null,
    ): RestfulResponse<BdDocumentValidateDTOResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_BARCODE_DISCOUNT_VALIDATE)?.timeout)

        return withContext(Dispatchers.IO) {
            getRestfulClient<BdDocumentValidateDTOResponse>(
                serviceName = SERVICE_BARCODE_DISCOUNT_VALIDATE,
                header = getHeaders(token, branchCode, timeout),
                isJsonBody = true,
                body =
                    jsonUtils.toJson(
                        BdDocumentValidateRequest(
                            discountType = discountType,
                            barcode = barcode,
                            expiryDate = expiryDate?.toStringPosAdminDateFormat(),
                        ),
                    ),
                timeout = timeout.toLong(),
            )?.requestPost()
        }
    }

    suspend fun skuCheck(
        token: String,
        branchCode: String,
        barcode: String,
    ): RestfulResponse<SkuCheckResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout = getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SKU_CHECK)?.timeout)

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<SkuCheckResponse>(
                    serviceName = SERVICE_SKU_CHECK,
                    header = getHeaders(token, branchCode, timeout),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            SkuCheckRequest(
                                barcode = barcode,
                            ),
                        ),
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }

    suspend fun getSdDocList(
        token: String,
        branchCode: String,
    ): RestfulResponse<DocumentSDResponse>? {
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SHIPMENT_DOCUMENT)?.timeout)

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<DocumentSDResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT,
                    header = getHeaders(token, branchCode, timeout),
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun getShipmentDocumentBySDNo(
        token: String,
        branchCode: String,
        sdNo: String,
    ): RestfulResponse<ShipmentDocumentResponse>? {
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SHIPMENT_DOCUMENT)?.timeout)

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<ShipmentDocumentResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT,
                    header = getHeaders(token, branchCode, timeout),
                    queryString = "/$sdNo",
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun shipmentDocumentOpen(
        token: String,
        branchCode: String,
        ldNo: String,
    ): RestfulResponse<ShipmentDocumentResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SHIPMENT_DOCUMENT_OPEN)?.timeout)

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<ShipmentDocumentResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT_OPEN,
                    header = getHeaders(token, branchCode, timeout),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            ShipmentDocumentOpenRequest(
                                shipmentNo = ldNo,
                            ),
                        ),
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }

    suspend fun getShipmentDocumentSummaryBySd(
        token: String,
        branchCode: String,
        sdNo: String,
    ): RestfulResponse<OrderReceiveSummaryResponse>? {
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SHIPMENT_DOCUMENT_SUMMARY)?.timeout)
        val pathParams: HashMap<String, String> = hashMapOf()
        pathParams["sdNo"] = sdNo

        return withContext(Dispatchers.IO) {
            getRestfulClient<OrderReceiveSummaryResponse>(
                serviceName = SERVICE_SHIPMENT_DOCUMENT_SUMMARY,
                header = getHeaders(token, branchCode, timeout),
                pathParams = pathParams,
                timeout = timeout.toLong(),
            )?.requestGet()
        }
    }

    suspend fun shipmentDocumentByBarcode(
        token: String,
        branchCode: String,
        sdNo: String,
        data: List<ShipmentItemRequest>,
    ): RestfulResponse<ShipmentDocumentByBarcodeResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(appConfig.getServiceConfig(SERVICE_SHIPMENT_DOCUMENT_BY_BARCODE)?.timeout)

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<ShipmentDocumentByBarcodeResponse>(
                    serviceName = SERVICE_SHIPMENT_DOCUMENT_BY_BARCODE,
                    header = getHeaders(token, branchCode, timeout),
                    pathParams = hashMapOf("sdNo" to sdNo),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            ShipmentDocumentByBarcodeRequest(data),
                        ),
                    timeout = timeout.toLong(),
                )
            req?.requestPatch()
        }
    }

    suspend fun getFrequencySupplier(
        token: String,
        branchCode: String,
        perPage: String,
    ): RestfulResponse<PurchaseInvoiceSupplierListDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_GET_FREQUENCY_SUPPLIER,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseInvoiceSupplierListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_FREQUENCY_SUPPLIER,
                    header = getHeaders(token, branchCode, timeout),
                    queryString = "?perPage=$perPage",
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun filterSupplier(
        token: String,
        branchCode: String,
        searchKey: String,
    ): RestfulResponse<PurchaseInvoiceSupplierListDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_FILTER_SUPPLIER,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseInvoiceSupplierListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_FILTER_SUPPLIER,
                    header = getHeaders(token, branchCode, timeout),
                    pathParams = hashMapOf("searchKey" to searchKey),
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun getSupplierByCode(
        token: String,
        branchCode: String,
        searchCode: String,
    ): RestfulResponse<PurchaseInvoiceSupplierDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_GET_SUPPLIER_BY_CODE,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseInvoiceSupplierDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_SUPPLIER_BY_CODE,
                    header = getHeaders(token, branchCode, timeout),
                    pathParams = hashMapOf("searchCode" to searchCode),
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun getPurchaseInvoiceDocument(
        token: String,
        branchCode: String,
        supplierCode: String,
    ): RestfulResponse<PurchaseInvoiceDocumentListDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_GET_PURCHASE_INVOICE,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseInvoiceDocumentListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_PURCHASE_INVOICE,
                    header = getHeaders(token, branchCode, timeout),
                    queryString = "?supplierCodes=$supplierCode",
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun getPurchaseOrderDocument(
        token: String,
        branchCode: String,
        supplierCode: String,
    ): RestfulResponse<PurchaseOrderDocumentListDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DOCUMENT,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseOrderDocumentListDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DOCUMENT,
                    header = getHeaders(token, branchCode, timeout),
                    pathParams = hashMapOf("supplierCode" to supplierCode),
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun purchaseInvoiceCalculate(
        token: String,
        branchCode: String,
        data: PurchaseInvoiceCalculateDTORequest,
    ): RestfulResponse<PurchaseInvoiceCalculateDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_CALCULATE,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseInvoiceCalculateDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_CALCULATE,
                    header = getHeaders(token, branchCode, timeout),
                    body = data,
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }

    suspend fun saveDraftPurchaseInvoice(
        token: String,
        branchCode: String,
        piNo: String,
        docNo: String,
        flagPO: Int,
        supplierCode: String,
        billNo: String,
        timestamp: Long,
        items: List<PurchaseInvoiceDraftItemRequest>,
    ): RestfulResponse<PurchaseInvoiceDraftDTOResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_DRAFT,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseInvoiceDraftDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_DRAFT,
                    header = getHeaders(token, branchCode, timeout, timestamp),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            PurchaseInvoiceDraftRequest(
                                piNo = piNo,
                                docNo = docNo,
                                flagPO = flagPO,
                                supplierCode = supplierCode,
                                billNo = billNo,
                                items = items,
                            ),
                        ),
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }

    suspend fun getPurchaseOrderDetailByDocNo(
        token: String,
        branchCode: String,
        docNo: String,
        supplierCode: String,
    ): RestfulResponse<PurchaseOrderDetailDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DETAIL,
                    )?.timeout,
            )
        val pathParams =
            hashMapOf(
                "supplierCode" to supplierCode,
                "docNo" to docNo,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseOrderDetailDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_PURCHASE_ORDER_DETAIL,
                    header = getHeaders(token, branchCode, timeout),
                    pathParams = pathParams,
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun validatePurchaseInvoiceBill(
        token: String,
        branchCode: String,
        billNo: String,
        piNo: String,
        supplierCode: String,
    ): RestfulResponse<PurchaseInvoiceValidateBillDTOResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_VALIDATE_BILL,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseInvoiceValidateBillDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_VALIDATE_BILL,
                    header = getHeaders(token, branchCode, timeout),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            PurchaseInvoiceValidateBillRequest(
                                billNo = billNo,
                                piNo = piNo,
                                supplierCode = supplierCode,
                            ),
                        ),
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }

    suspend fun createPurchaseInvoice(
        token: String,
        branchCode: String,
        poNo: String,
        billNo: String,
        supplierCode: String,
    ): RestfulResponse<PurchaseInvoiceCreateDTOResponse>? {
        val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_CREATE,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseInvoiceCreateDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_CREATE,
                    header = getHeaders(token, branchCode, timeout),
                    isJsonBody = true,
                    body =
                        jsonUtils.toJson(
                            PurchaseInvoiceCreateRequest(
                                poNo = poNo,
                                billNo = billNo,
                                supplierCode = supplierCode,
                            ),
                        ),
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }

    suspend fun getPurchaseInvoiceDetail(
        token: String,
        branchCode: String,
        piNo: String,
    ): RestfulResponse<PurchaseInvoiceDetailDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_GET_DETAIL,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseInvoiceDetailDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_GET_DETAIL,
                    header = getHeaders(token, branchCode, timeout),
                    pathParams = hashMapOf("piNo" to piNo),
                    timeout = timeout.toLong(),
                )
            req?.requestGet()
        }
    }

    suspend fun purchaseBarcodeFind(
        token: String,
        branchCode: String,
        data: PurchaseBarcodeFindDTORequest,
    ): RestfulResponse<PurchaseBarcodeFindDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_PURCHASE_INVOICE_BARCODE_FIND,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<PurchaseBarcodeFindDTOResponse>(
                    serviceName = SERVICE_PURCHASE_INVOICE_BARCODE_FIND,
                    header = getHeaders(token, branchCode, timeout),
                    body = data,
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }

    suspend fun qrDocumentSubmit(
        token: String,
        branchCode: String,
        data: QrDocumentSubmitDTORequest,
    ): RestfulResponse<QrDocumentSubmitDTOResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_SHIPMENT_QR_DOCUMENT_SUBMIT,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<QrDocumentSubmitDTOResponse>(
                    serviceName = SERVICE_SHIPMENT_QR_DOCUMENT_SUBMIT,
                    header = getHeaders(token, branchCode, timeout),
                    body = data,
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }

    suspend fun shipmentToteDocumentSubmitAdjust(
        token: String,
        branchCode: String,
        data: SdToteSubmitAdjustRequest,
    ): RestfulResponse<SdToteSubmitAdjustResponse>? {
        val timeout =
            getSafeRequestTimeout(
                appConfig
                    .getServiceConfig(
                        SERVICE_SHIPMENT_TOTE_DOCUMENT_SUBMIT_ADJUST,
                    )?.timeout,
            )

        return withContext(Dispatchers.IO) {
            val req =
                getRestfulClient<SdToteSubmitAdjustResponse>(
                    serviceName = SERVICE_SHIPMENT_TOTE_DOCUMENT_SUBMIT_ADJUST,
                    header = getHeaders(token, branchCode, timeout),
                    body = data,
                    timeout = timeout.toLong(),
                )
            req?.requestPost()
        }
    }
}
