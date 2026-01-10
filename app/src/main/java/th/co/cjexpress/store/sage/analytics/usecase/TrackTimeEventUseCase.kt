package th.co.cjexpress.store.sage.analytics.usecase

import th.co.cjexpress.store.sage.analytics.ProcessStatusType
import th.co.cjexpress.store.sage.analytics.ProcessTimeTrackingType

interface TrackTimeEventUseCase {
    fun track(
        type: ProcessTimeTrackingType,
        status: ProcessStatusType,
    )

    fun stop(type: ProcessTimeTrackingType)
}
