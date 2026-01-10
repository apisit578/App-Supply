package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.presentation.model.BusinessException

class GetShipmentDocumentBySdNoUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private var sdNo: String = ""

    fun execute(sdNo: String): Flow<Result<ShipmentDocumentModel>> {
        this.sdNo = sdNo
        return sageApiRepository.getShipmentDocumentBySDNo(sdNo).map { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> handleError(result)
            }
        }
    }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            ErrorCodes.NOT_FOUND, ErrorCodes.INVALID_STATUS ->
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = result.cause.errorCode,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_data_not_found),
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_format_shipment_document_no),
                                    sdNo,
                                ),
                        ),
                )

            else -> super.handleError(result)
        }
}
