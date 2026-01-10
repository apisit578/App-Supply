package th.co.cjexpress.store.sage.di.module

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import th.co.cjexpress.store.sage.constants.BUS_DATE_FORMAT
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.config.ConfigLoader
import th.co.cjexpress.store.sage.data.manager.OAuth2Manager
import th.co.cjexpress.store.sage.data.source.local.SharedPreferencesManager
import th.co.cjexpress.store.sage.data.source.remote.FirestoreDataSource
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.CustomBooleanTypeAdapter
import th.co.cjexpress.store.sage.utils.DefaultDispatchers
import th.co.cjexpress.store.sage.utils.DispatcherProvider
import th.co.cjexpress.store.sage.utils.JsonUtils
import th.co.cjexpress.store.sage.utils.LogUtils
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(
        @ApplicationContext context: Context,
    ): Context = context

    @Provides
    @Singleton
    fun provideConfigLoader(
        @ApplicationContext context: Context,
        jsonUtils: JsonUtils,
    ): ConfigLoader = ConfigLoader(context, jsonUtils)

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .setDateFormat(BUS_DATE_FORMAT)
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .registerTypeAdapter(Boolean::class.java, CustomBooleanTypeAdapter())
            .registerTypeAdapter(Boolean::class.javaObjectType, CustomBooleanTypeAdapter())
            .registerTypeAdapter(Boolean::class.javaPrimitiveType, CustomBooleanTypeAdapter())
            .disableHtmlEscaping()
            .create()

    @Provides
    @Singleton
    fun provideJsonUtils(gson: Gson): JsonUtils = JsonUtils(gson)

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(
        @ApplicationContext context: Context,
        jsonUtils: JsonUtils,
    ): SharedPreferencesManager = SharedPreferencesManager(context, jsonUtils)

    @Provides
    @Singleton
    fun provideAppConfig(): AppConfig = AppConfig()

    @Provides
    @Singleton
    fun provideOAuth2Manager(appConfig: AppConfig): OAuth2Manager = OAuth2Manager(appConfig)

    @Provides
    @Singleton
    fun provideLogUtils(
        dataSource: FirestoreDataSource,
        appConfig: AppConfig,
    ): LogUtils = LogUtils(dataSource, appConfig)

    @Provides
    @Singleton
    fun provideResourceUtils(
        @ApplicationContext context: Context,
    ): ResourceUtils = ResourceUtils(context)

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatchers()
}
