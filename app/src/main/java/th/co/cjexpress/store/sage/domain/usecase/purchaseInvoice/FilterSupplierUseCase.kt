package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierListModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.presentation.model.BusinessException

class FilterSupplierUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(searchKey: String): Flow<Result<PurchaseInvoiceSupplierListModel>> =
        if (searchKey.isBlank()) {
            flowOf(
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_input_supplier_require),
                        ),
                ),
            )
        } else {
            sageApiRepository.filterSupplier(searchKey).map { result ->
                when (result) {
                    is Result.Success -> result
                    is Result.Error -> handleError(result)
                }
            }
        }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            ErrorCodes.NOT_FOUND ->
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = result.cause.errorCode,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_supplier_not_found),
                        ),
                )

            else -> super.handleError(result)
        }
}
