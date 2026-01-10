package th.co.cjexpress.store.sage.domain.usecase.payload

import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.domain.repository.PayloadRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.LogUtils

class LoadGlobalConfigUseCase(
    private val payloadRepository: PayloadRepository,
    private val configRepository: ConfigRepository,
    private val logUtils: LogUtils,
) {
    suspend fun execute(): Result<PayloadConfig?> =
        try {
            val config = payloadRepository.loadGlobalConfig()

            // save to sharePref
            payloadRepository.saveGlobalConfig(config)

            // update in appConfig
            configRepository.updatePayloadAppConfig(config)

            logUtils.logInfo(
                action = LogAction.LOAD_GLOBAL_CONFIG,
                message = "load global config success",
            )

            Result.Success(config)
        } catch (e: Exception) {
            // TODO: should reject to launch app?

            logUtils.logError(
                action = LogAction.LOAD_GLOBAL_CONFIG,
                message = "load global config error, ${e.message}",
            )

            Result.Error(
                cause =
                    BusinessException(
                        errorCode = ErrorCodes.LOAD_CONFIG_FAILED_API,
                        type = ErrorType.ERROR,
                        message = e.message,
                    ),
            )

//            val cacheConfig = payloadRepository.loadGlobalCacheConfig()
//            if (cacheConfig != null) {
//                //update in appConfig
//                configRepository.updatePayloadAppConfig(cacheConfig)
//
//                logUtils.logInfo(
//                    action = LogAction.LOAD_GLOBAL_CONFIG,
//                    message = "load global cache config success"
//                )
//
//                Result.Success(cacheConfig)
//            } else {
//                logUtils.logWarning(
//                    action = LogAction.LOAD_GLOBAL_CONFIG,
//                    message = "load global cache config failure"
//                )
//
//                Result.Error(
//                    cause = BusinessException(
//                        errorCode = ErrorCodes.LOAD_CONFIG_FAILED_CACHE,
//                        "Load global config error, ${e.message}"
//                    )
//                )
//            }
        }
}
