package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDetailModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetPurchaseInvoiceDetailUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private var piNo: String = ""

    fun execute(piNo: String): Flow<Result<PurchaseInvoiceDetailModel>> {
        this.piNo = piNo
        return sageApiRepository.getPurchaseInvoiceDetail(piNo).map { result ->
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
                            title = resourceUtils.getString(R.string.text_data_not_found),
                            message =
                                resourceUtils.getString(
                                    id = R.string.text_document_no_purchase_invoice_pi,
                                    piNo,
                                ),
                        ),
                )
            }

            ErrorCodes.NOT_DRAFT -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_DRAFT,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message =
                                resourceUtils.getString(
                                    R.string.text_error_pi_status_is_not_draft,
                                    piNo,
                                ),
                        ),
                )
            }

            else -> super.handleError(result)
        }
}
