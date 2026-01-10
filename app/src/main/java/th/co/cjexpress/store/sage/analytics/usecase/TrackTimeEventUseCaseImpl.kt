package th.co.cjexpress.store.sage.analytics.usecase

import jakarta.inject.Inject
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent
import th.co.cjexpress.store.sage.analytics.AnalyticsTracker
import th.co.cjexpress.store.sage.analytics.ProcessStatusType
import th.co.cjexpress.store.sage.analytics.ProcessTimeTrackingType
import th.co.cjexpress.store.sage.analytics.ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS
import th.co.cjexpress.store.sage.domain.livedata.analytics.AnalyticTimeTrackingLiveData
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.MenuOrderReceiveUseCase

class TrackTimeEventUseCaseImpl
    @Inject
    constructor(
        private val menuOrderReceiveUseCase: MenuOrderReceiveUseCase,
        private val analyticTimeTrackingLiveData: AnalyticTimeTrackingLiveData,
        private val analyticsTracker: AnalyticsTracker,
    ) : TrackTimeEventUseCase {
        override fun track(
            type: ProcessTimeTrackingType,
            status: ProcessStatusType,
        ) {
            when (type) {
                ORDER_RECEIVE_PROCESS -> {
                    if (menuOrderReceiveUseCase.enableAnalyticsTimeTracking()) {
                        analyticTimeTrackingLiveData.addTrackProcess(
                            processName = type.name,
                            status = status,
                        )
                    }
                }
            }
        }

        override fun stop(type: ProcessTimeTrackingType) {
            val isDone = analyticTimeTrackingLiveData.isDone(processName = type.name)
            val startTime = analyticTimeTrackingLiveData.getStartTime(processName = type.name)
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            if (isDone) {
                when (type) {
                    ORDER_RECEIVE_PROCESS -> {
                        analyticsTracker.logEvent(
                            event =
                                AnalyticsEvent.OrderReceiveCompleted(
                                    duration = duration,
                                ),
                        )
                    }
                }
                analyticTimeTrackingLiveData.removeTracking(processName = type.name)
            }
        }
    }
