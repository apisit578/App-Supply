package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentListModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetPurchaseOrderDocumentUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private var supplier: String? = null

    fun execute(supplierCode: String): Flow<Result<PurchaseOrderDocumentListModel>> {
        supplier = supplierCode
        return sageApiRepository.getPurchaseOrderDocument(supplierCode).map { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> handleError(result)
            }
        }
    }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            ErrorCodes.NOT_FOUND -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_no_pending_purchase_order),
                        ),
                )
            }

            ErrorCodes.SUPPLIER_INACTIVE -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.SUPPLIER_INACTIVE,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_error_supplier_disabled),
                                    supplier.orEmpty(),
                                ),
                        ),
                )
            }

            ErrorCodes.SUPPLIER_NOT_FOUND -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.SUPPLIER_NOT_FOUND,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_error_supplier_not_found),
                                    supplier.orEmpty(),
                                ),
                        ),
                )
            }

            else -> super.handleError(result)
        }
}
