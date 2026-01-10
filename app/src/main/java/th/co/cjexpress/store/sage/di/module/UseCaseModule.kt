package th.co.cjexpress.store.sage.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import th.co.cjexpress.store.sage.domain.livedata.orderReceive.OrderReceiveLiveData
import th.co.cjexpress.store.sage.domain.livedata.orderReceiveTote.OrderReceiveToteLiveData
import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceDetailLiveData
import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceLiveData
import th.co.cjexpress.store.sage.domain.livedata.reprintQrFresh.ReprintQrFreshLiveData
import th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc.RequestDiscountLiveData
import th.co.cjexpress.store.sage.domain.mapper.DTOModelMapper
import th.co.cjexpress.store.sage.domain.repository.BranchIpRepository
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.domain.repository.LoginRepository
import th.co.cjexpress.store.sage.domain.repository.MenuRepository
import th.co.cjexpress.store.sage.domain.repository.PayloadRepository
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.BarcodeCodecUseCase
import th.co.cjexpress.store.sage.domain.usecase.GetAppConfigUseCase
import th.co.cjexpress.store.sage.domain.usecase.appconfig.CheckFeatureEnableUseCase
import th.co.cjexpress.store.sage.domain.usecase.appconfig.FeatureConfigsUseCase
import th.co.cjexpress.store.sage.domain.usecase.appconfig.GetMenuItemsUseCase
import th.co.cjexpress.store.sage.domain.usecase.branch_master.CheckBranchIpUseCase
import th.co.cjexpress.store.sage.domain.usecase.login.LoginUseCase
import th.co.cjexpress.store.sage.domain.usecase.masterMapping.GetBdSearchTypeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckItemQtyMoreRefQtyUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckLimitBatchUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckProductInDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckTotePrefixUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.GetSdDocListUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.GetShipmentDocumentBySdNoUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.MenuOrderReceiveUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.OrderReceiveCheckSkuUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.OrderReceiveSummaryUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentByBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentOpenUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.GetSdToteListUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.OpenToteUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteApproveUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteDiscountsConfigUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteLowestFactorUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteReceivingBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitAdjustUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitAndGetLatestBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SearchToteByDocNoUseCase
import th.co.cjexpress.store.sage.domain.usecase.payload.LoadBranchConfigUseCase
import th.co.cjexpress.store.sage.domain.usecase.payload.LoadGlobalConfigUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.CheckPurchaseOrderExistsUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.CreatePurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.FilterSupplierUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetFrequencySupplierUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetPurchaseInvoiceDetailUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetPurchaseInvoiceDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetPurchaseOrderDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetSupplierByCodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.MenuPurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseBarcodeFindUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceCalculateUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.SaveDraftPurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.ScanPurchaseInvoiceOrderUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.ValidatePurchaseInvoiceBillUseCase
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.CheckSubmitItemDetailUseCase
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.ReprintQrFreshLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.ShipmentQrDocumentSubmitUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.BdDocumentSubmitUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.BdDocumentValidateUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.GetBdDocumentNoDetailUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.GetBdDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.RequestDiscountLiveDataUseCase
import th.co.cjexpress.store.sage.utils.LogUtils
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideLoadGlobalConfigUseCase(
        payloadRepository: PayloadRepository,
        configRepository: ConfigRepository,
        logUtils: LogUtils,
    ): LoadGlobalConfigUseCase = LoadGlobalConfigUseCase(payloadRepository, configRepository, logUtils)

    @Provides
    @Singleton
    fun provideLoadConfigByBranchUseCase(
        payloadRepository: PayloadRepository,
        configRepository: ConfigRepository,
        logUtils: LogUtils,
    ): LoadBranchConfigUseCase = LoadBranchConfigUseCase(payloadRepository, configRepository, logUtils)

    @Provides
    @Singleton
    fun provideCheckBranchIpUseCase(
        branchIpRepository: BranchIpRepository,
        configRepository: ConfigRepository,
    ): CheckBranchIpUseCase = CheckBranchIpUseCase(branchIpRepository, configRepository)

    @Provides
    @Singleton
    fun provideCheckFeatureEnableUseCase(configRepository: ConfigRepository): CheckFeatureEnableUseCase =
        CheckFeatureEnableUseCase(configRepository)

    @Provides
    @Singleton
    fun provideGetBranchInfoUseCase(configRepository: ConfigRepository): GetAppConfigUseCase = GetAppConfigUseCase(configRepository)

    @Provides
    @Singleton
    fun provideLoginUseCase(
        loginRepository: LoginRepository,
        configRepository: ConfigRepository,
        resourceUtils: ResourceUtils,
    ): LoginUseCase = LoginUseCase(loginRepository, configRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideGetBdDocumentUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): GetBdDocumentUseCase = GetBdDocumentUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideGetBdSearchTypeUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): GetBdSearchTypeUseCase = GetBdSearchTypeUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideGetBdDocumentNoDetailUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): GetBdDocumentNoDetailUseCase = GetBdDocumentNoDetailUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideGetMenuItemsUseCase(
        menuRepository: MenuRepository,
        logUtils: LogUtils,
    ): GetMenuItemsUseCase = GetMenuItemsUseCase(menuRepository, logUtils)

    @Provides
    @Singleton
    fun provideBdDocumentValidateUseCase(
        requestDiscountLiveData: RequestDiscountLiveData,
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): BdDocumentValidateUseCase =
        BdDocumentValidateUseCase(
            requestDiscountLiveData,
            sageApiRepository,
            resourceUtils,
        )

    @Provides
    @Singleton
    fun provideBdDocumentSubmitUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): BdDocumentSubmitUseCase = BdDocumentSubmitUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideGetSdToteListUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): GetSdToteListUseCase = GetSdToteListUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideOpenToteUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): OpenToteUseCase = OpenToteUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideSearchToteByDocNoUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): SearchToteByDocNoUseCase = SearchToteByDocNoUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideSdToteReceivingBarcodeUseCase(
        orderReceiveToteLiveData: OrderReceiveToteLiveData,
        resourceUtils: ResourceUtils,
    ): SdToteReceivingBarcodeUseCase = SdToteReceivingBarcodeUseCase(orderReceiveToteLiveData, resourceUtils)

    @Provides
    @Singleton
    fun provideSdToteSubmitUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): SdToteSubmitUseCase = SdToteSubmitUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideSdToteSubmitAndGetLatestBarcodeUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): SdToteSubmitAndGetLatestBarcodeUseCase = SdToteSubmitAndGetLatestBarcodeUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideSdToteLowestFactorUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): SdToteLowestFactorUseCase = SdToteLowestFactorUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideSdToteApproveUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): SdToteApproveUseCase = SdToteApproveUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideSdToteDiscountTypeUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): SdToteDiscountsConfigUseCase = SdToteDiscountsConfigUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideBarcodeCodecUseCase(): BarcodeCodecUseCase = BarcodeCodecUseCase()

    @Provides
    @Singleton
    fun provideCheckSkuByBarcodeUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): OrderReceiveCheckSkuUseCase =
        OrderReceiveCheckSkuUseCase(
            sageApiRepository,
            resourceUtils,
        )

    @Provides
    @Singleton
    fun provideShipmentDocumentUseCase(orderReceiveLiveData: OrderReceiveLiveData): ShipmentDocumentUseCase =
        ShipmentDocumentUseCase(orderReceiveLiveData)

    @Provides
    @Singleton
    fun provideGetSdDocListUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): GetSdDocListUseCase = GetSdDocListUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideGetShipmentDocumentBySDNoUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): GetShipmentDocumentBySdNoUseCase = GetShipmentDocumentBySdNoUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideShipmentDocumentOpenUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): ShipmentDocumentOpenUseCase = ShipmentDocumentOpenUseCase(sageApiRepository, resourceUtils)

    @Provides
    @Singleton
    fun provideOrderReceiveSummaryUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): OrderReceiveSummaryUseCase =
        OrderReceiveSummaryUseCase(
            sageApiRepository = sageApiRepository,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideShipmentDocumentByBarcodeUseCase(
        sageApiRepository: SageApiRepository,
        orderReceiveLiveData: OrderReceiveLiveData,
        resourceUtils: ResourceUtils,
    ): ShipmentDocumentByBarcodeUseCase = ShipmentDocumentByBarcodeUseCase(sageApiRepository, orderReceiveLiveData, resourceUtils)

    @Provides
    @Singleton
    fun provideMenuOrderReceiveUseCase(configRepository: ConfigRepository): MenuOrderReceiveUseCase =
        MenuOrderReceiveUseCase(configRepository)

    @Provides
    @Singleton
    fun provideFeatureConfigsUseCase(configRepository: ConfigRepository): FeatureConfigsUseCase = FeatureConfigsUseCase(configRepository)

    @Provides
    @Singleton
    fun provideGetFrequencySupplierUseCase(
        sageApiRepository: SageApiRepository,
        menuPurchaseInvoiceUseCase: MenuPurchaseInvoiceUseCase,
        resourceUtils: ResourceUtils,
    ): GetFrequencySupplierUseCase =
        GetFrequencySupplierUseCase(
            sageApiRepository = sageApiRepository,
            menuPurchaseInvoiceUseCase = menuPurchaseInvoiceUseCase,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideFilterSupplierUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): FilterSupplierUseCase =
        FilterSupplierUseCase(
            sageApiRepository = sageApiRepository,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideGetSupplierByCodeUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): GetSupplierByCodeUseCase =
        GetSupplierByCodeUseCase(
            sageApiRepository = sageApiRepository,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideGetPurchaseInvoiceUseCase(configRepository: ConfigRepository): MenuPurchaseInvoiceUseCase =
        MenuPurchaseInvoiceUseCase(
            configRepository = configRepository,
        )

    @Provides
    @Singleton
    fun providePurchaseInvoiceUseCase(purchaseInvoiceLiveData: PurchaseInvoiceLiveData): PurchaseInvoiceUseCase =
        PurchaseInvoiceUseCase(purchaseInvoiceLiveData)

    @Provides
    @Singleton
    fun provideGetPurchaseInvoiceDocumentUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): GetPurchaseInvoiceDocumentUseCase =
        GetPurchaseInvoiceDocumentUseCase(
            sageApiRepository = sageApiRepository,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideGetPurchaseOrderDocumentUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): GetPurchaseOrderDocumentUseCase =
        GetPurchaseOrderDocumentUseCase(
            sageApiRepository = sageApiRepository,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun providePurchaseInvoiceDetailLiveDataUseCase(
        purchaseInvoiceDetailLiveData: PurchaseInvoiceDetailLiveData,
    ): PurchaseInvoiceDetailLiveDataUseCase =
        PurchaseInvoiceDetailLiveDataUseCase(
            purchaseInvoiceDetailLiveData = purchaseInvoiceDetailLiveData,
        )

    @Provides
    @Singleton
    fun provideScanPurchaseInvoiceOrderUseCase(
        purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase,
        resourceUtils: ResourceUtils,
    ): ScanPurchaseInvoiceOrderUseCase =
        ScanPurchaseInvoiceOrderUseCase(
            purchaseInvoiceDetailLiveDataUseCase = purchaseInvoiceDetailLiveDataUseCase,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideSaveDraftPurchaseInvoiceUseCase(
        sageApiRepository: SageApiRepository,
        purchaseInvoiceUseCase: PurchaseInvoiceUseCase,
        purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase,
        resourceUtils: ResourceUtils,
    ): SaveDraftPurchaseInvoiceUseCase =
        SaveDraftPurchaseInvoiceUseCase(
            sageApiRepository = sageApiRepository,
            purchaseInvoiceUseCase = purchaseInvoiceUseCase,
            purchaseInvoiceDetailLiveDataUseCase = purchaseInvoiceDetailLiveDataUseCase,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun providePurchaseInvoiceCalculateUseCase(
        sageApiRepository: SageApiRepository,
        purchaseInvoiceDetailLiveData: PurchaseInvoiceDetailLiveData,
        dtoModelMapper: DTOModelMapper,
        resourceUtils: ResourceUtils,
    ): PurchaseInvoiceCalculateUseCase =
        PurchaseInvoiceCalculateUseCase(
            sageApiRepository = sageApiRepository,
            purchaseInvoiceDetailLiveData = purchaseInvoiceDetailLiveData,
            resourceUtils = resourceUtils,
            dtoModelMapper = dtoModelMapper,
        )

    @Provides
    @Singleton
    fun provideCheckPurchaseOrderExistsUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): CheckPurchaseOrderExistsUseCase =
        CheckPurchaseOrderExistsUseCase(
            sageApiRepository = sageApiRepository,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideCreatePurchaseInvoiceUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): CreatePurchaseInvoiceUseCase =
        CreatePurchaseInvoiceUseCase(
            sageApiRepository = sageApiRepository,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideGetPurchaseInvoiceDetailUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
    ): GetPurchaseInvoiceDetailUseCase =
        GetPurchaseInvoiceDetailUseCase(
            sageApiRepository = sageApiRepository,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideValidatePurchaseInvoiceBillUseCase(
        sageApiRepository: SageApiRepository,
        menuPurchaseInvoiceUseCase: MenuPurchaseInvoiceUseCase,
        resourceUtils: ResourceUtils,
    ): ValidatePurchaseInvoiceBillUseCase =
        ValidatePurchaseInvoiceBillUseCase(
            sageApiRepository = sageApiRepository,
            menuPurchaseInvoiceUseCase = menuPurchaseInvoiceUseCase,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun requestDiscountLiveDataUseCase(requestDiscountLiveData: RequestDiscountLiveData): RequestDiscountLiveDataUseCase =
        RequestDiscountLiveDataUseCase(
            requestDiscountLiveData = requestDiscountLiveData,
        )

    @Provides
    @Singleton
    fun purchaseBarcodeFindUseCase(
        sageApiRepository: SageApiRepository,
        purchaseInvoiceLiveData: PurchaseInvoiceLiveData,
        dtoModelMapper: DTOModelMapper,
        resourceUtils: ResourceUtils,
    ): PurchaseBarcodeFindUseCase =
        PurchaseBarcodeFindUseCase(
            sageApiRepository = sageApiRepository,
            purchaseInvoiceLiveData = purchaseInvoiceLiveData,
            dtoModelMapper = dtoModelMapper,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideReprintQrFreshLiveDataUseCase(reprintQrFreshLiveData: ReprintQrFreshLiveData): ReprintQrFreshLiveDataUseCase =
        ReprintQrFreshLiveDataUseCase(
            reprintQrFreshLiveData = reprintQrFreshLiveData,
        )

    @Provides
    @Singleton
    fun provideCheckSubmitItemDetailUseCase(resourceUtils: ResourceUtils): CheckSubmitItemDetailUseCase =
        CheckSubmitItemDetailUseCase(
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideShipmentQrDocumentSubmitUseCase(
        sageApiRepository: SageApiRepository,
        dtoModelMapper: DTOModelMapper,
        resourceUtils: ResourceUtils,
    ) = ShipmentQrDocumentSubmitUseCase(
        sageApiRepository = sageApiRepository,
        dtoModelMapper = dtoModelMapper,
        resourceUtils = resourceUtils,
    )

    @Provides
    @Singleton
    fun sdToteSubmitAdjustUseCase(
        sageApiRepository: SageApiRepository,
        resourceUtils: ResourceUtils,
        dtoModelMapper: DTOModelMapper,
    ): SdToteSubmitAdjustUseCase =
        SdToteSubmitAdjustUseCase(
            sageApiRepository = sageApiRepository,
            dtoModelMapper = dtoModelMapper,
            resourceUtils = resourceUtils,
        )

    @Provides
    @Singleton
    fun provideCheckLimitBatchUseCase(
        menuOrderReceiveUseCase: MenuOrderReceiveUseCase,
        shipmentDocumentUseCase: ShipmentDocumentUseCase,
        resourceUtils: ResourceUtils,
    ) = CheckLimitBatchUseCase(
        menuOrderReceiveUseCase = menuOrderReceiveUseCase,
        shipmentDocumentUseCase = shipmentDocumentUseCase,
        resourceUtils = resourceUtils,
    )

    @Provides
    @Singleton
    fun provideCheckProductInDocumentUseCase(
        shipmentDocumentUseCase: ShipmentDocumentUseCase,
        resourceUtils: ResourceUtils,
    ) = CheckProductInDocumentUseCase(
        shipmentDocumentUseCase = shipmentDocumentUseCase,
        resourceUtils = resourceUtils,
    )

    @Provides
    @Singleton
    fun provideCheckItemQtyMoreRefQty(
        menuOrderReceiveUseCase: MenuOrderReceiveUseCase,
        shipmentDocumentUseCase: ShipmentDocumentUseCase,
        resourceUtils: ResourceUtils,
    ) = CheckItemQtyMoreRefQtyUseCase(
        menuOrderReceiveUseCase = menuOrderReceiveUseCase,
        shipmentDocumentUseCase = shipmentDocumentUseCase,
        resourceUtils = resourceUtils,
    )

    @Provides
    @Singleton
    fun provideCheckTotePrefixUseCase(
        menuOrderReceiveUseCase: MenuOrderReceiveUseCase,
        featureConfigsUseCase: FeatureConfigsUseCase,
        shipmentDocumentUseCase: ShipmentDocumentUseCase,
        resourceUtils: ResourceUtils,
    ) = CheckTotePrefixUseCase(
        menuOrderReceiveUseCase = menuOrderReceiveUseCase,
        shipmentDocumentUseCase = shipmentDocumentUseCase,
        featureConfigsUseCase = featureConfigsUseCase,
        resourceUtils = resourceUtils,
    )
}
