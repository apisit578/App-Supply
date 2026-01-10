package th.co.cjexpress.store.sage.utils

import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.LogLevel
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.model.HandheldLogModel
import th.co.cjexpress.store.sage.data.source.remote.FirestoreDataSource
import timber.log.Timber
import java.util.concurrent.Executors

class LogUtils(
    private val dataSource: FirestoreDataSource,
    private val appConfig: AppConfig,
) {
    private val executor = Executors.newSingleThreadExecutor()

    companion object {
        private const val TAG = "EVENT_LOG"
        private const val ACTION = "action"
        private const val SCREEN = "screen"
        private const val MESSAGE = "message"
        private const val URL = "url"
    }

    fun logInfo(
        action: LogAction,
        username: String = "",
        branchCode: String = appConfig.getBranchInfo()?.branchCode ?: "N/A",
        url: String? = null,
        message: String? = null,
    ) = executor.execute {
        val logModel =
            HandheldLogModel(
                level = LogLevel.INFO,
                action = action,
                username = username,
                branchCode = branchCode,
                appScreen = ScreenHolder.getCurrentScreen(),
            )

        url?.let {
            logModel.url = it
        }

        message?.let {
            logModel.message = it
        }
        Timber.tag(TAG).i(
            mapOf(
                ACTION to logModel.action,
                SCREEN to logModel.appScreen,
                MESSAGE to logModel.message,
                URL to logModel.url,
            ).toString(),
        )
        dataSource.insertHandheldLog(logModel)
    }

    fun logWarning(
        action: LogAction,
        username: String = "",
        branchCode: String = appConfig.getBranchInfo()?.branchCode ?: "N/A",
        url: String? = null,
        message: String? = null,
    ) = executor.execute {
        val logModel =
            HandheldLogModel(
                level = LogLevel.WARNING,
                action = action,
                username = username,
                branchCode = branchCode,
                appScreen = ScreenHolder.getCurrentScreen(),
            )

        url?.let {
            logModel.url = it
        }

        message?.let {
            logModel.message = it
        }
        Timber.tag(TAG).w(
            mapOf(
                ACTION to logModel.action,
                SCREEN to logModel.appScreen,
                MESSAGE to logModel.message,
                URL to logModel.url,
            ).toString(),
        )
        dataSource.insertHandheldLog(logModel)
    }

    fun logError(
        action: LogAction,
        username: String = "",
        branchCode: String = appConfig.getBranchInfo()?.branchCode ?: "N/A",
        url: String? = null,
        message: String? = null,
    ) = executor.execute {
        val logModel =
            HandheldLogModel(
                level = LogLevel.ERROR,
                action = action,
                username = username,
                branchCode = branchCode,
                appScreen = ScreenHolder.getCurrentScreen(),
            )

        url?.let {
            logModel.url = it
        }

        message?.let {
            logModel.message = it
        }
        Timber.tag(TAG).e(
            mapOf(
                ACTION to logModel.action,
                SCREEN to logModel.appScreen,
                MESSAGE to logModel.message,
                URL to logModel.url,
            ).toString(),
        )
        dataSource.insertHandheldLog(logModel)
    }

    fun logDebug(
        action: LogAction,
        username: String = "",
        branchCode: String = appConfig.getBranchInfo()?.branchCode ?: "N/A",
        url: String? = null,
        message: String? = null,
    ) = executor.execute {
        val logModel =
            HandheldLogModel(
                level = LogLevel.DEBUG,
                action = action,
                username = username,
                branchCode = branchCode,
                appScreen = ScreenHolder.getCurrentScreen(),
            )

        url?.let {
            logModel.url = it
        }

        message?.let {
            logModel.message = it
        }
        Timber.tag(TAG).d(
            mapOf(
                ACTION to logModel.action,
                SCREEN to logModel.appScreen,
                MESSAGE to logModel.message,
                URL to logModel.url,
            ).toString(),
        )
        dataSource.insertHandheldLog(logModel)
    }
}
