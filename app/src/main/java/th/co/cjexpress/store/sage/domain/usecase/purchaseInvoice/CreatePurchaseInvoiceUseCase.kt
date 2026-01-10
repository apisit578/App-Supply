package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CreatePurchaseInvoiceUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private var errorInfo: PurchaseInvoiceErrorInfo? = null

    fun execute(
        poNo: String,
        billNo: String,
        supplierCode: String,
    ): Flow<Result<PurchaseInvoiceCreateModel>> {
        errorInfo = PurchaseInvoiceErrorInfo(poNo, supplierCode)

        return sageApiRepository.createPurchaseInvoice(poNo, billNo, supplierCode).map { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> handleError(result)
            }
        }
    }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> {
        val (poNo, supplierCode) = errorInfo ?: PurchaseInvoiceErrorInfo("", "")

        return when (result.cause.errorCode) {
            ErrorCodes.ALREADY_REFERENCED,
            ErrorCodes.ALREADY_EXISTS,
            -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = result.cause.errorCode,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_error_po_already_received),
                                    poNo,
                                ),
                        ),
                )
            }

            ErrorCodes.DUPLICATE_DATA -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.DUPLICATE_DATA,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_error_duplicate_bill_no),
                            message = resourceUtils.getString(R.string.text_error_check_information),
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
                                    supplierCode,
                                ),
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
                            message = resourceUtils.getString(R.string.text_error_data_not_found),
                        ),
                )
            }

            else -> super.handleError(result)
        }
    }

    private data class PurchaseInvoiceErrorInfo(
        val poNo: String,
        val supplierCode: String,
    )
}
