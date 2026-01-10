package th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.mapper.DTOModelMapper
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class ShipmentQrDocumentSubmitUseCase(
    private val sageApiRepository: SageApiRepository,
    private val dtoModelMapper: DTOModelMapper,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private val unexpectedError =
        setOf(
            ErrorCodes.INVALID_BODY,
            ErrorCodes.INVALID_EXPIREDATE,
            ErrorCodes.STORM_SERVICE_ERROR,
        )

    fun execute(documentData: QrDocumentSubmitModel): Flow<Result<QrDocumentSubmitMappingModel>> =
        sageApiRepository
            .qrDocumentSubmit(
                dtoModelMapper.qrDocumentSubmitDTOMapper.toDTO(domainModel = documentData),
            ).map { result ->
                when (result) {
                    is Result.Success -> result
                    is Result.Error -> handleError(result)
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

            ErrorCodes.NOT_FOUND -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_not_found_barcode_data),
                            message = "",
                        ),
                )
            }

            else -> super.handleError(result)
        }
}
