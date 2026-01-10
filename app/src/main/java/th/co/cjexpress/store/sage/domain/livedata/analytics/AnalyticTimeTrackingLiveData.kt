package th.co.cjexpress.store.sage.domain.livedata.analytics

import androidx.lifecycle.MutableLiveData
import th.co.cjexpress.store.sage.analytics.ProcessStatusType
import th.co.cjexpress.store.sage.analytics.model.AnalyticTimeTrackingDataModel
import th.co.cjexpress.store.sage.analytics.model.AnalyticTimeTrackingModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticTimeTrackingLiveData
    @Inject
    constructor() {
        private val _analyticTimeTrackingLiveDataModel =
            MutableLiveData(AnalyticTimeTrackingModel())

        fun addTrackProcess(
            processName: String,
            status: ProcessStatusType,
        ) {
            val current = _analyticTimeTrackingLiveDataModel.value ?: AnalyticTimeTrackingModel()
            if (current.trackedProcesses.none { it.processName == processName }) {
                val newProcess =
                    AnalyticTimeTrackingDataModel(
                        processName = processName,
                        startTime = System.currentTimeMillis(),
                        status = status,
                    )
                _analyticTimeTrackingLiveDataModel.value =
                    current.copy(trackedProcesses = current.trackedProcesses + newProcess)
            }
            updateProcessStatus(processName = processName, status = status)
        }

        fun updateProcessStatus(
            processName: String,
            status: ProcessStatusType,
        ) {
            val current = _analyticTimeTrackingLiveDataModel.value ?: AnalyticTimeTrackingModel()
            val updatedList =
                current.trackedProcesses.map { track ->
                    if (track.processName == processName) {
                        track.copy(status = status)
                    } else {
                        track
                    }
                }
            _analyticTimeTrackingLiveDataModel.value = current.copy(trackedProcesses = updatedList)
        }

        fun removeTracking(processName: String) {
            val current = _analyticTimeTrackingLiveDataModel.value ?: AnalyticTimeTrackingModel()
            val updatedList = current.trackedProcesses.filterNot { it.processName == processName }
            _analyticTimeTrackingLiveDataModel.value = current.copy(trackedProcesses = updatedList)
        }

        fun getStartTime(processName: String): Long {
            val current = _analyticTimeTrackingLiveDataModel.value ?: return System.currentTimeMillis()
            return current.trackedProcesses.find { track -> track.processName == processName }?.startTime
                ?: System.currentTimeMillis()
        }

        fun getStatus(processName: String): ProcessStatusType {
            val current = _analyticTimeTrackingLiveDataModel.value ?: return ProcessStatusType.DONE
            return current.trackedProcesses.find { track -> track.processName == processName }?.status
                ?: ProcessStatusType.OTHER
        }

        fun isDone(processName: String): Boolean {
            val status = getStatus(processName)
            return status == ProcessStatusType.DONE
        }
    }
