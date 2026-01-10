package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorCodes.BARCODE_NOT_ALLOW_BUY
import th.co.cjexpress.store.sage.constants.ErrorCodes.BARCODE_NOT_FOUND
import th.co.cjexpress.store.sage.constants.ErrorCodes.INVALID_BODY
import th.co.cjexpress.store.sage.constants.ErrorCodes.INVALID_PARAMETER
import th.co.cjexpress.store.sage.constants.ErrorCodes.INVALID_SUPPLIER_BARCODE
import th.co.cjexpress.store.sage.constants.ErrorCodes.PRODUCT_SKU_INACTIVE
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceLiveData
import th.co.cjexpress.store.sage.domain.mapper.DTOModelMapper
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseBarcodeFindListModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class PurchaseBarcodeFindUseCase(
    private val sageApiRepository: SageApiRepository,
    private val purchaseInvoiceLiveData: PurchaseInvoiceLiveData,
    private val dtoModelMapper: DTOModelMapper,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private val unexpectedError =
        setOf(
            INVALID_PARAMETER,
            INVALID_BODY,
        )
    private var barcode: String = ""

    fun execute(barcode: String): Flow<Result<PurchaseBarcodeFindListModel>> =
        if (barcode.isBlank()) {
            flowOf(
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_please_scan_barcode),
                        ),
                ),
            )
        } else {
            this.barcode = barcode
            val dtoModel =
                dtoModelMapper.purchaseBarcodeFindDTOMapper.toDTO(
                    barcode = barcode,
                    supplierCode =
                        purchaseInvoiceLiveData.purchaseInvoiceSupplierLiveData.value
                            ?.supplier
                            ?.supplierCode
                            ?: "",
                )

            sageApiRepository.purchaseBarcodeFind(dtoModel).map { result ->
                when (result) {
                    is Result.Success -> {
                        result
                    }
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
                            errorCode = ErrorCodes.INTERNAL_SERVER_ERROR,
                            type = ErrorType.ERROR,
                            title = result.cause.title,
                            message = result.cause.message,
                        ),
                )
            }

            BARCODE_NOT_FOUND -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = BARCODE_NOT_FOUND,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_title_barcode_mismatch),
                            message = resourceUtils.getString(R.string.text_error_check_information),
                        ),
                )
            }

            INVALID_SUPPLIER_BARCODE -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = INVALID_SUPPLIER_BARCODE,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message =
                                resourceUtils.getString(
                                    R.string.text_error_supplier_mismatch,
                                    barcode,
                                ),
                        ),
                )
            }

            PRODUCT_SKU_INACTIVE -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = PRODUCT_SKU_INACTIVE,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_error_sku_inactive_title),
                            message = resourceUtils.getString(R.string.text_error_sku_inactive_desc),
                        ),
                )
            }

            BARCODE_NOT_ALLOW_BUY -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = BARCODE_NOT_ALLOW_BUY,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_error_sku_inactive_title),
                            message = resourceUtils.getString(R.string.text_please_check_and_scan_again),
                        ),
                )
            }

            else -> super.handleError(result)
        }
}
