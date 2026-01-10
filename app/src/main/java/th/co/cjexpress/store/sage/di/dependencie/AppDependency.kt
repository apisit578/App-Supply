package th.co.cjexpress.store.sage.di.dependencie

import android.content.Context
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.manager.OAuth2Manager
import th.co.cjexpress.store.sage.utils.JsonUtils
import th.co.cjexpress.store.sage.utils.LogUtils

interface AppDependency {
    fun getContext(): Context

    fun getJsonUtils(): JsonUtils

    fun getAppConfig(): AppConfig

    fun getOAuth2Manager(): OAuth2Manager

    fun getLogManager(): LogUtils
}
