package th.co.cjexpress.store.sage.di.module

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import th.co.cjexpress.store.sage.analytics.AnalyticsTracker
import th.co.cjexpress.store.sage.analytics.AnalyticsTrackerImpl
import th.co.cjexpress.store.sage.analytics.mapper.FirebaseEventMapper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context,
    ): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    @Provides
    @Singleton
    fun provideFirebaseEventMapper(): FirebaseEventMapper = FirebaseEventMapper()

    @Provides
    @Singleton
    fun provideAnalyticsTracker(
        firebaseAnalytics: FirebaseAnalytics,
        firebaseEventMapper: FirebaseEventMapper,
    ): AnalyticsTracker =
        AnalyticsTrackerImpl(
            firebaseAnalytics,
            firebaseEventMapper,
        )
}
