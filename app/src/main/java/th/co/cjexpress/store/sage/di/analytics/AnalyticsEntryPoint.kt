package th.co.cjexpress.store.sage.di.analytics

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import th.co.cjexpress.store.sage.analytics.usecase.TrackAppOpenedEventUseCase

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AnalyticsEntryPoint {
    fun logAppOpenedEventUseCase(): TrackAppOpenedEventUseCase
}
