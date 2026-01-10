package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorCodes.BAD_REQUEST
import th.co.cjexpress.store.sage.constants.ErrorCodes.DOC_ALREADY_REFERENCED
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CheckPurchaseOrderExistsUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private var errorInfo: PurchaseOrderErrorInfo? = null
    private val unexpectedError =
        setOf(
            BAD_REQUEST,
            DOC_ALREADY_REFERENCED,
        )

    fun execute(
        poNo: String,
        supplierCode: String,
    ): Flow<Result<Boolean>> {
        errorInfo = PurchaseOrderErrorInfo(poNo, supplierCode)

        return sageApiRepository
            .getPurchaseOrderDetailByDocNo(
                docNo = poNo,
                supplierCode = supplierCode,
            ).map { result ->
                when (result) {
                    is Result.Success -> {
                        Result.Success(true)
                    }

                    is Result.Error -> {
                        val errorResult =
                            Result.Error<Boolean>(
                                cause = result.cause,
                            )
                        handleError(errorResult)
                    }
                }
            }
    }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> {
        val (docNo, supplierCode) = errorInfo ?: PurchaseOrderErrorInfo("", "")

        return when (result.cause.errorCode) {
            in unexpectedError -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = BAD_REQUEST,
                            type = ErrorType.ERROR,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = result.cause.message,
                            ref = result.cause.ref,
                        ),
                    canRetry = true,
                )
            }
            ErrorCodes.NOT_FOUND -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_error_po_not_found),
                                    docNo,
                                ),
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
                                    supplierCode,
                                ),
                        ),
                )
            }

            ErrorCodes.ALREADY_REFERENCED -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.ALREADY_REFERENCED,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_error_po_already_received),
                                    docNo,
                                ),
                        ),
                )
            }
            else -> super.handleError(result)
        }
    }

    private data class PurchaseOrderErrorInfo(
        val docNo: String,
        val supplierCode: String,
    )
}
