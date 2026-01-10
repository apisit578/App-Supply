package th.co.cjexpress.store.sage.analytics.usecase

interface TrackAppOpenedEventUseCase {
    fun execute(versionName: String)
}
