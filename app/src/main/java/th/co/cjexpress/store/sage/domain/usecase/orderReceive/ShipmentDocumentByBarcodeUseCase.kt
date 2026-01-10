package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentItemRequest
import th.co.cjexpress.store.sage.domain.livedata.orderReceive.OrderReceiveLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class ShipmentDocumentByBarcodeUseCase(
    private val repository: SageApiRepository,
    private val orderReceiveLiveData: OrderReceiveLiveData,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private val sdNo: String
        get() = orderReceiveLiveData.getSdNo()
    private val unexpectedError =
        setOf(
            ErrorCodes.INVALID_DOC_TYPE,
            ErrorCodes.INVALID_BRANCH,
            ErrorCodes.INVALID_BODY,
            ErrorCodes.INVALID_PARAMETER,
        )

    fun execute(
        data: List<ShipmentItemRequest>,
    ): Flow<Result<ShipmentDocumentByBarcodeModel>> =
        if (data.isEmpty()) {
            flowOf(
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.INVALID_PARAMETER,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_title_error_not_found_order),
                            message = resourceUtils.getString(R.string.text_description_error_not_found_order),
                        ),
                ),
            )
        } else {
            repository
                .shipmentDocumentByBarcode(sdNo, data)
                .map { result ->
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
                            title = result.cause.title,
                            message = result.cause.message,
                        ),
                )
            }

            ErrorCodes.DOC_ALREADY_APPROVED -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.DOC_ALREADY_APPROVED,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_document_status_changed, sdNo),
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
                            message = resourceUtils.getString(R.string.text_format_shipment_document_no, sdNo),
                        ),
                )
            }

            ErrorCodes.INVALID_TOTE -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.INVALID_TOTE,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_data_not_found),
                            message = resourceUtils.getString(R.string.text_title_tote_code, sdNo),
                        ),
                )
            }

            else -> super.handleError(result)
        }
}
