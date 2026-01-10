package th.co.cjexpress.store.sage.data.repository

import kotlinx.coroutines.flow.Flow
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
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDocumentListDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftItemRequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierListDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceValidateBillDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDocumentListDTOResponse
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.request.QrDocumentSubmitDTORequest
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.response.QrDocumentSubmitDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentNoDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentSubmitDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentValidateDTOResponse
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
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentListModel
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.utils.getApiResponse
import java.util.Date

class SageApiRepositoryImpl(
    private val sageAdapterApi: SageAdapterApi,
    private val modelMapper: SageAdapterApiModelMapper,
    private val appConfig: AppConfig,
) : SageApiRepository {
    override fun getSdToteList(): Flow<Result<SdToteListMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getSdToteList(
                    token = token,
                    branchCode = branchCode,
                )
            },
            mapper = { modelMapper.sdToteListMapper.map(it) },
            modelClass = SdToteListResponse::class.java,
        )
    }

    override fun openTote(toteCode: String): Flow<Result<SdToteDetailMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.openTote(
                    token = token,
                    branchCode = branchCode,
                    toteCode = toteCode,
                )
            },
            mapper = { modelMapper.openToteMapper.map(it) },
            modelClass = OpenToteResponse::class.java,
        )
    }

    override fun searchToteBySdNo(sdNo: String): Flow<Result<SdToteDetailMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.searchToteBySdNo(
                    token = token,
                    branchCode = branchCode,
                    sdNo = sdNo,
                )
            },
            mapper = { modelMapper.searchToteMapper.map(it) },
            modelClass = SearchSdToteResponse::class.java,
        )
    }

    override fun sdToteSubmit(
        sdNo: String,
        toteCode: String,
        barcode: String,
        skuCode: String,
        discountType: String,
        actualQty: Int,
        expList: List<ISdToteExpiryDateModel>?,
    ): Flow<Result<SdToteSubmitMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.sdToteSubmit(
                    token = token,
                    branchCode = branchCode,
                    sdNo = sdNo,
                    toteCode = toteCode,
                    barcode = barcode,
                    skuCode = skuCode,
                    discountType = discountType,
                    actualQty = actualQty,
                    expList = expList,
                )
            },
            mapper = { modelMapper.sdToteSubmitMapper.map(it) },
            modelClass = SdToteSubmitResponse::class.java,
        )
    }

    override fun sdToteLowestFactor(
        sdNo: String,
        barcode: String,
    ): Flow<Result<SdToteLowestFactorMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.sdToteLowestFactor(
                    token = token,
                    branchCode = branchCode,
                    sdNo = sdNo,
                    barcode = barcode,
                )
            },
            mapper = { modelMapper.sdToteLowestFactorMapper.map(it) },
            modelClass = SdToteLowestFactorResponse::class.java,
        )
    }

    override fun sdToteApprove(sdNo: String): Flow<Result<SdToteApproveMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.sdToteApprove(
                    token = token,
                    branchCode = branchCode,
                    sdNo = sdNo,
                )
            },
            mapper = { modelMapper.sdToteApproveMapper.map(it) },
            modelClass = SdToteApproveResponse::class.java,
        )
    }

    override fun sdToteDiscountsConfig(barcode: String): Flow<Result<SdToteDiscountsConfigMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.sdToteDiscountsConfig(
                    token = token,
                    branchCode = branchCode,
                    barcode = barcode,
                )
            },
            mapper = { modelMapper.sdToteDiscountsConfigMapping.map(it) },
            modelClass = SdToteDiscountsConfigResponse::class.java,
        )
    }

    override fun bdDocument(): Flow<Result<BdDocumentMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.bdDocument(
                    token = token,
                    branchCode = branchCode,
                )
            },
            mapper = { modelMapper.bdDocumentMapper.map(it) },
            modelClass = BdDocumentDTOResponse::class.java,
        )
    }

    override fun bdDocumentNoDetail(docNo: String): Flow<Result<BdDocumentNoDetailMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.bdDocumentNoDetail(
                    token = token,
                    branchCode = branchCode,
                    docNo = docNo,
                )
            },
            mapper = { modelMapper.bdDocumentNoDetailMapper.map(it) },
            modelClass = BdDocumentNoDetailDTOResponse::class.java,
        )
    }

    override fun bdDocumentSubmit(
        documentNumber: String,
        discountType: String,
        items: List<BdDetailProductUIModel>,
    ): Flow<Result<BdDocumentSubmitMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.bdDocumentSubmit(
                    token = token,
                    branchCode = branchCode,
                    documentNumber = documentNumber,
                    discountType = discountType,
                    deviceName = appConfig.androidId,
                    items = items,
                )
            },
            mapper = { modelMapper.bdDocumentSubmitMapper.map(it) },
            modelClass = BdDocumentSubmitDTOResponse::class.java,
        )
    }

    override fun bdDocumentSearchType(type: MasterMapping): Flow<Result<BdSearchTypeMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getBdSearchType(
                    token = token,
                    branchCode = branchCode,
                    mappingType = type,
                )
            },
            mapper = { modelMapper.bdSearchTypeMapper.map(it) },
            modelClass = BdSearchTypeDTOResponse::class.java,
        )
    }

    override fun bdDocumentValidate(
        discountType: String,
        barcode: String,
        expiryDate: Date?,
    ): Flow<Result<BdDocumentValidateMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.bdDocumentValidate(
                    token = token,
                    branchCode = branchCode,
                    discountType = discountType,
                    barcode = barcode,
                    expiryDate = expiryDate,
                )
            },
            mapper = { modelMapper.bdValidateBarcodeMapper.map(it) },
            modelClass = BdDocumentValidateDTOResponse::class.java,
        )
    }

    override fun skuCheck(barcode: String): Flow<Result<SkuCheckModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.skuCheck(
                    token = token,
                    branchCode = branchCode,
                    barcode = barcode,
                )
            },
            mapper = { modelMapper.skuCheckMapper.map(it) },
            modelClass = SkuCheckResponse::class.java,
        )
    }

    override fun getSdDocList(): Flow<Result<DocumentSDModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getSdDocList(
                    token = token,
                    branchCode = branchCode,
                )
            },
            mapper = { modelMapper.documentSDMapper.map(it) },
            modelClass = DocumentSDResponse::class.java,
        )
    }

    override fun getShipmentDocumentBySDNo(sdNo: String): Flow<Result<ShipmentDocumentModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getShipmentDocumentBySDNo(
                    token = token,
                    branchCode = branchCode,
                    sdNo,
                )
            },
            mapper = { modelMapper.shipmentDocumentMapper.map(it) },
            modelClass = ShipmentDocumentResponse::class.java,
        )
    }

    override fun shipmentDocumentOpen(ldNo: String): Flow<Result<ShipmentDocumentModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.shipmentDocumentOpen(
                    token = token,
                    branchCode = branchCode,
                    ldNo,
                )
            },
            mapper = { modelMapper.shipmentDocumentMapper.map(it) },
            modelClass = ShipmentDocumentResponse::class.java,
        )
    }

    override fun getShipmentDocumentSummaryBySd(sdNo: String): Flow<Result<OrderReceiveSummaryModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getShipmentDocumentSummaryBySd(
                    token = token,
                    branchCode = branchCode,
                    sdNo = sdNo,
                )
            },
            mapper = { modelMapper.summaryMapper.map(orderReceiveSummaryResponse = it) },
            modelClass = OrderReceiveSummaryResponse::class.java,
        )
    }

    override fun shipmentDocumentByBarcode(
        sdNo: String,
        data: List<ShipmentItemRequest>,
    ): Flow<Result<ShipmentDocumentByBarcodeModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.shipmentDocumentByBarcode(
                    token = token,
                    branchCode = branchCode,
                    sdNo = sdNo,
                    data = data,
                )
            },
            mapper = { modelMapper.shipmentDocumentByBarcodeMapper.map(it) },
            modelClass = ShipmentDocumentByBarcodeResponse::class.java,
        )
    }

    override fun getFrequencySupplier(perPage: String): Flow<Result<PurchaseInvoiceSupplierListModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getFrequencySupplier(
                    token = token,
                    branchCode = branchCode,
                    perPage = perPage,
                )
            },
            mapper = { modelMapper.purchaseInvoiceSupplierMapper.mapList(it) },
            modelClass = PurchaseInvoiceSupplierListDTOResponse::class.java,
        )
    }

    override fun filterSupplier(searchKey: String): Flow<Result<PurchaseInvoiceSupplierListModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.filterSupplier(
                    token = token,
                    branchCode = branchCode,
                    searchKey = searchKey,
                )
            },
            mapper = { modelMapper.purchaseInvoiceSupplierMapper.mapList(it) },
            modelClass = PurchaseInvoiceSupplierListDTOResponse::class.java,
        )
    }

    override fun getSupplierByCode(searchCode: String): Flow<Result<PurchaseInvoiceSupplierModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getSupplierByCode(
                    token = token,
                    branchCode = branchCode,
                    searchCode = searchCode,
                )
            },
            mapper = { modelMapper.purchaseInvoiceSupplierMapper.map(it) },
            modelClass = PurchaseInvoiceSupplierDTOResponse::class.java,
        )
    }

    override fun getPurchaseInvoiceDocument(supplierCode: String): Flow<Result<PurchaseInvoiceDocumentListModel>> {
        val token = appConfig.authToken?.token.orEmpty()
        val branchCode = appConfig.getBranchInfo()?.branchCode.orEmpty()

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getPurchaseInvoiceDocument(
                    token = token,
                    branchCode = branchCode,
                    supplierCode = supplierCode,
                )
            },
            mapper = { modelMapper.purchaseInvoiceDocumentMapper.map(it) },
            modelClass = PurchaseInvoiceDocumentListDTOResponse::class.java,
        )
    }

    override fun getPurchaseOrderDocument(supplierCode: String): Flow<Result<PurchaseOrderDocumentListModel>> {
        val token = appConfig.authToken?.token.orEmpty()
        val branchCode = appConfig.getBranchInfo()?.branchCode.orEmpty()

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getPurchaseOrderDocument(
                    token = token,
                    branchCode = branchCode,
                    supplierCode = supplierCode,
                )
            },
            mapper = { modelMapper.purchaseOrderDocumentMapper.map(it) },
            modelClass = PurchaseOrderDocumentListDTOResponse::class.java,
        )
    }

    override fun purchaseInvoiceCalculate(data: PurchaseInvoiceCalculateDTORequest): Flow<Result<PurchaseInvoiceCalculateModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.purchaseInvoiceCalculate(
                    token = token,
                    branchCode = branchCode,
                    data,
                )
            },
            mapper = { modelMapper.purchaseInvoiceCalculateMapper.map(it) },
            modelClass = PurchaseInvoiceCalculateDTOResponse::class.java,
        )
    }

    override fun saveDraftPurchaseInvoice(
        piNo: String,
        docNo: String,
        flagPO: Int,
        supplierCode: String,
        billNo: String,
        timestamp: Long,
        items: List<PurchaseInvoiceDraftItemRequest>,
    ): Flow<Result<PurchaseInvoiceDraftModel>> {
        val token = appConfig.authToken?.token.orEmpty()
        val branchCode = appConfig.getBranchInfo()?.branchCode.orEmpty()

        return getApiResponse(
            apiCall = {
                sageAdapterApi.saveDraftPurchaseInvoice(
                    token = token,
                    branchCode = branchCode,
                    piNo = piNo,
                    docNo = docNo,
                    flagPO = flagPO,
                    supplierCode = supplierCode,
                    billNo = billNo,
                    items = items,
                    timestamp = timestamp,
                )
            },
            mapper = { modelMapper.purchaseInvoiceDraftMapper.map(it) },
            modelClass = PurchaseInvoiceDraftDTOResponse::class.java,
        )
    }

    override fun getPurchaseOrderDetailByDocNo(
        docNo: String,
        supplierCode: String,
    ): Flow<Result<PurchaseOrderDetailModel>> {
        val token = appConfig.authToken?.token.orEmpty()
        val branchCode = appConfig.getBranchInfo()?.branchCode.orEmpty()

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getPurchaseOrderDetailByDocNo(
                    token = token,
                    branchCode = branchCode,
                    docNo = docNo,
                    supplierCode = supplierCode,
                )
            },
            mapper = { modelMapper.purchaseOrderDetailMapper.map(it) },
            modelClass = PurchaseOrderDetailDTOResponse::class.java,
        )
    }

    override fun validatePurchaseInvoiceBill(
        billNo: String,
        piNo: String,
        supplierCode: String,
    ): Flow<Result<PurchaseInvoiceValidateBillModel>> {
        val token = appConfig.authToken?.token.orEmpty()
        val branchCode = appConfig.getBranchInfo()?.branchCode.orEmpty()

        return getApiResponse(
            apiCall = {
                sageAdapterApi.validatePurchaseInvoiceBill(
                    token = token,
                    branchCode = branchCode,
                    billNo = billNo,
                    piNo = piNo,
                    supplierCode = supplierCode,
                )
            },
            mapper = { modelMapper.purchaseInvoiceValidateBillMapper.map(it) },
            modelClass = PurchaseInvoiceValidateBillDTOResponse::class.java,
        )
    }

    override fun createPurchaseInvoice(
        poNo: String,
        billNo: String,
        supplierCode: String,
    ): Flow<Result<PurchaseInvoiceCreateModel>> {
        val token = appConfig.authToken?.token.orEmpty()
        val branchCode = appConfig.getBranchInfo()?.branchCode.orEmpty()

        return getApiResponse(
            apiCall = {
                sageAdapterApi.createPurchaseInvoice(
                    token = token,
                    branchCode = branchCode,
                    poNo = poNo,
                    billNo = billNo,
                    supplierCode = supplierCode,
                )
            },
            mapper = { modelMapper.purchaseInvoiceCreateMapper.map(it) },
            modelClass = PurchaseInvoiceCreateDTOResponse::class.java,
        )
    }

    override fun getPurchaseInvoiceDetail(piNo: String): Flow<Result<PurchaseInvoiceDetailModel>> {
        val token = appConfig.authToken?.token.orEmpty()
        val branchCode = appConfig.getBranchInfo()?.branchCode.orEmpty()

        return getApiResponse(
            apiCall = {
                sageAdapterApi.getPurchaseInvoiceDetail(
                    token = token,
                    branchCode = branchCode,
                    piNo = piNo,
                )
            },
            mapper = { modelMapper.purchaseInvoiceDetailMapper.map(it) },
            modelClass = PurchaseInvoiceDetailDTOResponse::class.java,
        )
    }

    override fun purchaseBarcodeFind(data: PurchaseBarcodeFindDTORequest): Flow<Result<PurchaseBarcodeFindListModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.purchaseBarcodeFind(
                    token = token,
                    branchCode = branchCode,
                    data,
                )
            },
            mapper = { modelMapper.purchaseBarcodeFindMapper.map(it) },
            modelClass = PurchaseBarcodeFindDTOResponse::class.java,
        )
    }

    override fun qrDocumentSubmit(data: QrDocumentSubmitDTORequest): Flow<Result<QrDocumentSubmitMappingModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.qrDocumentSubmit(
                    token = token,
                    branchCode = branchCode,
                    data = data,
                )
            },
            mapper = { modelMapper.qrDocumentSubmitMapper.map(it) },
            modelClass = QrDocumentSubmitDTOResponse::class.java,
        )
    }

    override fun shipmentToteDocumentSubmitAdjust(data: SdToteSubmitAdjustRequest): Flow<Result<SdToteSubmitAdjustModel>> {
        val token = appConfig.authToken?.token ?: ""
        val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""

        return getApiResponse(
            apiCall = {
                sageAdapterApi.shipmentToteDocumentSubmitAdjust(
                    token = token,
                    branchCode = branchCode,
                    data = data,
                )
            },
            mapper = { modelMapper.sdToteSubmitAdjustMapper.map(it) },
            modelClass = SdToteSubmitAdjustResponse::class.java,
        )
    }
}
