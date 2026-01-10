package th.co.cjexpress.store.sage.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.config.ConfigLoader
import th.co.cjexpress.store.sage.data.manager.OAuth2Manager
import th.co.cjexpress.store.sage.data.mapper.SageAdapterApiModelMapper
import th.co.cjexpress.store.sage.data.mapper.branchMaster.BranchIpMapper
import th.co.cjexpress.store.sage.data.repository.BranchIpRepositoryImpl
import th.co.cjexpress.store.sage.data.repository.ConfigRepositoryImpl
import th.co.cjexpress.store.sage.data.repository.LoginRepositoryImpl
import th.co.cjexpress.store.sage.data.repository.MenuRepositoryImpl
import th.co.cjexpress.store.sage.data.repository.PayloadRepositoryImpl
import th.co.cjexpress.store.sage.data.repository.SageApiRepositoryImpl
import th.co.cjexpress.store.sage.data.source.local.SharedPreferencesManager
import th.co.cjexpress.store.sage.data.source.remote.BranchIpApi
import th.co.cjexpress.store.sage.data.source.remote.PayloadApi
import th.co.cjexpress.store.sage.data.source.remote.SageAdapterApi
import th.co.cjexpress.store.sage.domain.repository.BranchIpRepository
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.domain.repository.LoginRepository
import th.co.cjexpress.store.sage.domain.repository.MenuRepository
import th.co.cjexpress.store.sage.domain.repository.PayloadRepository
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.utils.JsonUtils
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providePayloadConfigRepository(
        payloadApi: PayloadApi,
        sharedPrefManager: SharedPreferencesManager,
        configLoader: ConfigLoader,
    ): PayloadRepository = PayloadRepositoryImpl(payloadApi, sharedPrefManager, configLoader)

    @Provides
    @Singleton
    fun provideConfigRepository(appConfig: AppConfig): ConfigRepository = ConfigRepositoryImpl(appConfig)

    @Provides
    @Singleton
    fun provideBranchIpRepository(
        branchIpApi: BranchIpApi,
        mapper: BranchIpMapper,
    ): BranchIpRepository = BranchIpRepositoryImpl(branchIpApi, mapper)

    @Provides
    @Singleton
    fun provideLoginRepository(
        oAuth2Manager: OAuth2Manager,
        jsonUtils: JsonUtils,
    ): LoginRepository = LoginRepositoryImpl(oAuth2Manager, jsonUtils)

    @Provides
    @Singleton
    fun provideMenuRepository(appConfig: AppConfig): MenuRepository = MenuRepositoryImpl(appConfig)

    @Provides
    @Singleton
    fun provideSageApiRepository(
        sageAdapterApi: SageAdapterApi,
        modelMapper: SageAdapterApiModelMapper,
        appConfig: AppConfig,
    ): SageApiRepository = SageApiRepositoryImpl(sageAdapterApi, modelMapper, appConfig)
}
