package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceDetailLiveData
import th.co.cjexpress.store.sage.domain.mapper.DTOModelMapper
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class PurchaseInvoiceCalculateUseCase(
    private val sageApiRepository: SageApiRepository,
    private val purchaseInvoiceDetailLiveData: PurchaseInvoiceDetailLiveData,
    private val dtoModelMapper: DTOModelMapper,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun validateLiveData(): BusinessException? {
        val data = purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.value
        return if (data == null || data.entries.isEmpty()) {
            BusinessException(
                errorCode = ErrorCodes.INVALID_PARAMETER,
                type = ErrorType.WARNING,
                title = resourceUtils.getString(R.string.text_unable_to_continue),
                message = resourceUtils.getString(R.string.text_error_data_not_found),
            )
        } else {
            null
        }
    }

    fun execute(): Flow<Result<PurchaseInvoiceCalculateModel>> {
        validateLiveData()?.let {
            return flowOf(Result.Error(cause = it))
        }

        return sageApiRepository
            .purchaseInvoiceCalculate(
                dtoModelMapper.purchaseInvoiceCalculateDTOMapper.toDTO(
                    purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.value!!,
                ),
            ).map { result ->
                when (result) {
                    is Result.Success -> {
                        val resp =
                            dtoModelMapper.purchaseInvoiceCalculateDTOMapper.toDomain(
                                dtoModel = result.data,
                            )
                        Result.Success(resp)
                    }

                    is Result.Error -> {
                        handleError(result)
                    }
                }
            }
    }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            ErrorCodes.INVALID_PARAMETER -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.BAD_REQUEST,
                            type = ErrorType.ERROR,
                            title = result.cause.title,
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
                            title = resourceUtils.getString(R.string.text_data_not_found),
                            message =
                                resourceUtils.getString(
                                    id = R.string.text_document_no_purchase_invoice_pi,
                                    purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.value?.piNo,
                                ),
                        ),
                )
            }

            else -> super.handleError(result)
        }
}
