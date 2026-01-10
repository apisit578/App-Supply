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
import timber.log.Timber
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

@Suppress("UNCHECKED_CAST")
class LoadBranchConfigUseCase(
    private val payloadRepository: PayloadRepository,
    private val configRepository: ConfigRepository,
    private val logUtils: LogUtils,
) {
    suspend fun execute(branchCode: String): Result<Boolean> { // Result<PayloadConfig?>
        val appConfig = configRepository.getAppConfig()
        return try {
            val branchConfig = payloadRepository.loadConfigByBranch(branchCode)

            // save config to sharePref
            payloadRepository.saveBranchConfig(branchConfig)

            // merge and update in appConfig
            val mergedConfig = mergeConfig(appConfig.payloadConfig, branchConfig)
            configRepository.updatePayloadAppConfig(mergedConfig)

            logUtils.logInfo(
                action = LogAction.LOAD_BRANCHES_CONFIG,
                message = "load branch config success",
            )
            Timber.tag("DEV").d(
                "LOAD BRANCH CONFIG SUCCESS : $mergedConfig",
            )
            Result.Success(true)
        } catch (e: Exception) {
            logUtils.logError(
                action = LogAction.LOAD_BRANCHES_CONFIG,
                message = "load branch config error, ${e.message}",
            )
            if (e.message == ErrorCodes.NOT_FOUND) {
                Result.Success(true)
            } else {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.LOAD_CONFIG_FAILED_API,
                            type = ErrorType.ERROR,
                            message = e.message,
                        ),
                )
            }
        }
    }

    fun mergeConfig(
        payloadConfig: PayloadConfig?,
        branchConfig: PayloadConfig,
    ): PayloadConfig = payloadConfig?.mergeWithReflect(branchConfig) ?: branchConfig

    fun PayloadConfig.mergeWithReflect(branchConfig: PayloadConfig): PayloadConfig? =
        mergeDataClassValues(PayloadConfig::class, this, branchConfig)

    fun mergeField(
        globalValue: Any?,
        branchValue: Any?,
    ): Any? =
        when {
            // กรณี value เป็น HashMap
            globalValue is Map<*, *> &&
                branchValue is Map<*, *> -> {
                val globalField = globalValue as Map<Any, Any>
                val branchField = branchValue as Map<Any, Any>
                HashMap(globalField).apply {
                    branchField.forEach { (key, value) ->
                        put(key, value)
                    }
                }
            }

            // กรณี value เป็น Data class หรือ Object
            globalValue != null &&
                branchValue != null &&
                globalValue::class.isData &&
                branchValue::class == globalValue::class -> {
                mergeDataClassValues(globalValue::class as KClass<Any>, globalValue, branchValue)
            }

            // กรณี value เป็น type อื่นๆ
            else -> {
                if (globalValue != null) branchValue ?: globalValue else branchValue
            }
        }

    fun <T : Any> mergeDataClassValues(
        objClass: KClass<T>,
        globalObj: T,
        branchObj: T,
    ): T? {
        objClass.primaryConstructor?.let { primaryConstructor ->
            val args =
                primaryConstructor.parameters.associateWith { param ->
                    val prop = objClass.memberProperties.first { it.name == param.name }
                    val globalValue = prop.getter.call(globalObj)
                    val branchValue = prop.getter.call(branchObj)
                    mergeField(globalValue, branchValue)
                }
            return primaryConstructor.callBy(args)
        } ?: run {
            return null
        }
    }
}
