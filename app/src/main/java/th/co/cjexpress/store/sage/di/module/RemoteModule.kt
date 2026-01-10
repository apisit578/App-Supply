package th.co.cjexpress.store.sage.di.module

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.config.ConfigLoader
import th.co.cjexpress.store.sage.data.source.remote.BranchIpApi
import th.co.cjexpress.store.sage.data.source.remote.FirestoreDataSource
import th.co.cjexpress.store.sage.data.source.remote.PayloadApi
import th.co.cjexpress.store.sage.data.source.remote.SageAdapterApi
import th.co.cjexpress.store.sage.utils.JsonUtils
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Provides
    @Singleton
    fun providePayloadApi(configLoader: ConfigLoader): PayloadApi = PayloadApi(configLoader)

    @Provides
    @Singleton
    fun provideBranchIpApi(): BranchIpApi = BranchIpApi()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirestoreDataSource(
        firestore: FirebaseFirestore,
        jsonUtils: JsonUtils,
    ): FirestoreDataSource = FirestoreDataSource(firestore, jsonUtils)

    @Provides
    @Singleton
    fun provideSageAdapterApi(appConfig: AppConfig): SageAdapterApi = SageAdapterApi(appConfig)
}
