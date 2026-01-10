package th.co.cjexpress.store.sage.di.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import th.co.cjexpress.store.sage.analytics.usecase.TrackAppOpenedEventUseCase
import th.co.cjexpress.store.sage.analytics.usecase.TrackAppOpenedEventUseCaseImpl
import th.co.cjexpress.store.sage.analytics.usecase.TrackLoginEventUseCase
import th.co.cjexpress.store.sage.analytics.usecase.TrackLoginEventUseCaseImpl
import th.co.cjexpress.store.sage.analytics.usecase.TrackPressStartReceiveEventUseCase
import th.co.cjexpress.store.sage.analytics.usecase.TrackPressStartReceiveEventUseCaseImpl
import th.co.cjexpress.store.sage.analytics.usecase.TrackTimeEventUseCase
import th.co.cjexpress.store.sage.analytics.usecase.TrackTimeEventUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsEventModule {
    @Binds
    abstract fun bindTrackAppOpenedEventUseCase(trackAppOpenedEventUseCase: TrackAppOpenedEventUseCaseImpl): TrackAppOpenedEventUseCase

    @Binds
    abstract fun bindTrackTimeEventUseCase(trackTimeEventUseCase: TrackTimeEventUseCaseImpl): TrackTimeEventUseCase

    @Binds
    abstract fun bindTrackLoginEventUseCase(trackLoginEventUseCase: TrackLoginEventUseCaseImpl): TrackLoginEventUseCase

    @Binds
    abstract fun bindTrackPressStartReceiveEventUseCase(
        trackPressStartReceiveEventUseCase: TrackPressStartReceiveEventUseCaseImpl,
    ): TrackPressStartReceiveEventUseCase
}
