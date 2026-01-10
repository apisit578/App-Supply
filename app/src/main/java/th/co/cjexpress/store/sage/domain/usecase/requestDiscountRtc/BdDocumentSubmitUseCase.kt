package th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class BdDocumentSubmitUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private val cannotSaveError =
        setOf(
            ErrorCodes.BARCODE_CANT_SUGGESTION,
            ErrorCodes.BAD_REQUEST,
            ErrorCodes.INVALID_STATUS,
            ErrorCodes.NOT_FOUND,
        )

    fun execute(
        documentNumber: String,
        discountType: String,
        items: List<BdDetailProductUIModel>,
    ): Flow<Result<BdDocumentSubmitMappingModel>> =
        sageApiRepository
            .bdDocumentSubmit(
                documentNumber = documentNumber,
                discountType = discountType,
                items = items,
            ).map { result ->
                when (result) {
                    is Result.Success -> result
                    is Result.Error -> handleError(result)
                }
            }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            in cannotSaveError -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.ERROR,
                            title = resourceUtils.getString(R.string.text_save_failed),
                            message = resourceUtils.getString(R.string.text_can_not_save_document),
                        ),
                    canRetry = false,
                )
            }

            else -> super.handleError(result)
        }
}
