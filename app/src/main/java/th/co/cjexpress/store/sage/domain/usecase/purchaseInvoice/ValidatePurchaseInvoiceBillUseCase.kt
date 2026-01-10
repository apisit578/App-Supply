package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceValidateBillModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class ValidatePurchaseInvoiceBillUseCase(
    private val sageApiRepository: SageApiRepository,
    private val menuPurchaseInvoiceUseCase: MenuPurchaseInvoiceUseCase,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(
        billNo: String,
        piNo: String,
        supplierCode: String,
    ): Flow<Result<PurchaseInvoiceValidateBillModel>> {
        validateRequireBillNo(billNo, supplierCode)?.let {
            return flowOf(Result.Error(cause = it))
        }

        validateInvoiceFormat(billNo)?.let {
            return flowOf(Result.Error(cause = it))
        }

        return sageApiRepository
            .validatePurchaseInvoiceBill(billNo, piNo, supplierCode)
            .map { result ->
                when (result) {
                    is Result.Success -> result
                    is Result.Error -> handleError(result)
                }
            }
    }

    fun validateRequireBillNo(
        billNo: String,
        supplierCode: String,
    ): BusinessException? {
        val isValid = billNo.isNotBlank() && supplierCode.isNotBlank()
        return if (!isValid) {
            BusinessException(
                errorCode = ErrorCodes.INVALID_PARAMETER,
                type = ErrorType.WARNING,
                title = resourceUtils.getString(R.string.text_unable_to_continue),
                message =
                    resourceUtils.getString(
                        R.string.text_please_scan_barcode,
                    ),
            )
        } else {
            null
        }
    }

    fun validateInvoiceFormat(billNo: String): BusinessException? {
        val regEx = menuPurchaseInvoiceUseCase.getBillNoValidationRegEx().toRegex()
        val isValid = regEx.matches(billNo.trim())
        return if (!isValid) {
            BusinessException(
                errorCode = ErrorCodes.INVALID_PARAMETER,
                type = ErrorType.WARNING,
                title = resourceUtils.getString(R.string.text_incorrect_bill_no),
                message =
                    resourceUtils.getString(
                        R.string.text_please_verify_data,
                    ),
            )
        } else {
            null
        }
    }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            ErrorCodes.INVALID_PARAMETER ->
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = result.cause.errorCode,
                            type = ErrorType.WARNING,
                            title =
                                result.cause.title
                                    ?: resourceUtils.getString(R.string.text_unable_to_continue),
                            message =
                                result.cause.message
                                    ?: resourceUtils.getString(R.string.text_please_verify_data),
                        ),
                )

            else -> super.handleError(result)
        }
}
