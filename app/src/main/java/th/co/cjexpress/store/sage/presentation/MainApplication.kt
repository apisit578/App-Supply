package th.co.cjexpress.store.sage.presentation

import android.app.Application
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import th.co.cjexpress.store.sage.BuildConfig
import th.co.cjexpress.store.sage.di.AppDependencyProvider
import th.co.cjexpress.store.sage.utils.SoundPoolUtils
import th.co.cjexpress.store.sage.di.analytics.AnalyticsEntryPoint
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDependencyProvider.initialize(this)
        logAppOpenedEvent()
        Timber.plant(Timber.DebugTree())

        // Init SoundPoolUtils
        SoundPoolUtils.init(context = this)
    }

    override fun onTerminate() {
        super.onTerminate()
        SoundPoolUtils.release()
    }

    private fun logAppOpenedEvent() {
        val entryPoint =
            EntryPointAccessors.fromApplication(
                this,
                AnalyticsEntryPoint::class.java,
            )
        entryPoint.logAppOpenedEventUseCase().execute(versionName = BuildConfig.VERSION_NAME)
    }
}
