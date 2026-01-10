package th.co.cjexpress.store.sage.di

import android.content.Context
import dagger.hilt.android.EntryPointAccessors
import th.co.cjexpress.store.sage.di.dependencie.AppEntryPoint

object AppDependencyProvider {
    private var appEntryPoint: AppEntryPoint? = null

    fun initialize(context: Context): AppDependencyProvider {
        appEntryPoint =
            EntryPointAccessors.fromApplication(
                context,
                AppEntryPoint::class.java,
            )
        return this
    }

    fun getProviderBuilder(): AppEntryPoint {
        return appEntryPoint!!
    }
}
