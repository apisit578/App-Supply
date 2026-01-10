package th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc.RequestDiscountLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils
import java.util.Date

class BdDocumentValidateUseCase(
    requestDiscountLiveData: RequestDiscountLiveData,
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    val editingProductFlow = requestDiscountLiveData.editingProduct
    private val unexpectedError =
        setOf(
            ErrorCodes.INVALID_BODY,
            ErrorCodes.BAD_REQUEST,
        )

    fun execute(
        discountType: String,
        barcode: String,
        expiryDate: Date? = null,
    ): Flow<Result<BdDocumentValidateMappingModel>> =
        if (barcode.isEmpty()) {
            flowOf(
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.INVALID_PARAMETER,
                            type = ErrorType.WARNING,
                            title =
                                resourceUtils.getString(
                                    R.string.text_unable_to_continue,
                                ),
                            message = resourceUtils.getString(R.string.text_please_scan_barcode),
                        ),
                ),
            )
        } else {
            sageApiRepository
                .bdDocumentValidate(
                    discountType,
                    barcode,
                    expiryDate,
                ).map { result ->
                    when (result) {
                        is Result.Success -> result
                        is Result.Error -> handleError(result)
                    }
                }
        }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            in unexpectedError -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.BAD_REQUEST,
                            type = ErrorType.ERROR,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = result.cause.message,
                        ),
                )
            }

            ErrorCodes.NOT_FOUND -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_not_found_data),
                        ),
                )
            }

            ErrorCodes.NOT_ELIGIBLE -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_product_not_eligible),
                        ),
                )
            }
            else -> super.handleError(result)
        }
}
