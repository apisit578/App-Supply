package th.co.cjexpress.store.sage.analytics.model

import th.co.cjexpress.store.sage.analytics.ProcessStatusType

data class AnalyticTimeTrackingModel(
    val trackedProcesses: List<AnalyticTimeTrackingDataModel> = emptyList(),
)

data class AnalyticTimeTrackingDataModel(
    val processName: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val status: ProcessStatusType = ProcessStatusType.OTHER,
)
