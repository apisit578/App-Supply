package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.NOT_REF_PO
import th.co.cjexpress.store.sage.constants.REF_PO
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftItemRequest
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SaveDraftPurchaseInvoiceUseCase(
    private val sageApiRepository: SageApiRepository,
    private val purchaseInvoiceUseCase: PurchaseInvoiceUseCase,
    private val purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private var errorInfo: SaveDraftPurchaseInvoiceErrorInfo = SaveDraftPurchaseInvoiceErrorInfo()

    fun execute(product: IPurchaseInvoiceProductsModel): Flow<Result<PurchaseInvoiceDraftModel>> =
        purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value?.let { purchaseOrder ->
            errorInfo =
                SaveDraftPurchaseInvoiceErrorInfo(
                    piNo = purchaseOrder.piNo,
                    supplierCode = purchaseOrder.supplierCode,
                )
            purchaseOrder.entries.find { it.barcode == product.barcode }?.let { line ->
                if (purchaseInvoiceUseCase.isRefPo() && product.actualQty > line.qty) {
                    flowOf(handleErrorOverQty())
                } else {
                    if (purchaseInvoiceUseCase.isRefPo()) {
                        saveDraft(
                            purchaseOrder,
                            listOf(
                                PurchaseInvoiceDraftItemRequest(
                                    product.barcode,
                                    product.actualQty,
                                ),
                            ),
                        )
                    } else {
                        val items =
                            purchaseOrder.entries
                                .filter { it.actualQty > 0 }
                                .map { item ->
                                    PurchaseInvoiceDraftItemRequest(
                                        barcode = item.barcode,
                                        actualQty = item.actualQty,
                                    )
                                }
                        saveDraft(purchaseOrder, items)
                    }
                }
            } ?: run {
                flowOf(handleErrorConnectService())
            }
        } ?: run {
            flowOf(handleErrorConnectService())
        }

    fun executeList(products: List<IPurchaseInvoiceProductsModel>): Flow<Result<PurchaseInvoiceDraftModel>> =
        purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value?.let { purchaseOrder ->
            errorInfo =
                SaveDraftPurchaseInvoiceErrorInfo(
                    piNo = purchaseOrder.piNo,
                    supplierCode = purchaseOrder.supplierCode,
                )
            val overQty =
                products.any { product ->
                    purchaseOrder.entries.find { it.barcode == product.barcode }?.let { line ->
                        product.actualQty > line.qty
                    } == true
                }
            if (purchaseInvoiceUseCase.isRefPo() && overQty) {
                flowOf(handleErrorOverQty())
            } else {
                if (purchaseInvoiceUseCase.isRefPo()) {
                    saveDraft(
                        purchaseOrder,
                        products.map {
                            PurchaseInvoiceDraftItemRequest(
                                it.barcode,
                                it.actualQty,
                            )
                        },
                    )
                } else {
                    val items =
                        purchaseOrder.entries
                            .filter { it.actualQty > 0 }
                            .map { item ->
                                PurchaseInvoiceDraftItemRequest(
                                    barcode = item.barcode,
                                    actualQty = item.actualQty,
                                )
                            }
                    saveDraft(purchaseOrder, items)
                }
            }
        } ?: run {
            flowOf(handleErrorConnectService())
        }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> {
        val (piNo, supplierCode) = errorInfo

        return when (result.cause.errorCode) {
            ErrorCodes.INVALID_STATUS -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = result.cause.errorCode,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_error_pi_status_is_not_draft),
                                    piNo,
                                ),
                        ),
                )
            }

            ErrorCodes.DUPLICATE_DATA -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = result.cause.errorCode,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_error_duplicate_bill_no),
                            message = resourceUtils.getString(R.string.text_please_verify_data),
                        ),
                )
            }

            ErrorCodes.SUPPLIER_INACTIVE -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = result.cause.errorCode,
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
                            errorCode = result.cause.errorCode,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_error_data_not_found),
                        ),
                )
            }

            ErrorCodes.DOC_NOT_FOUND -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = result.cause.errorCode,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_data_not_found),
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_document_saved_number),
                                    piNo,
                                ),
                        ),
                )
            }

            else -> super.handleError(result)
        }
    }

    private data class SaveDraftPurchaseInvoiceErrorInfo(
        val piNo: String = "",
        val supplierCode: String = "",
    )

    private fun saveDraft(
        purchaseOrder: PurchaseInvoiceDetailUIModel,
        items: List<PurchaseInvoiceDraftItemRequest>,
    ): Flow<Result<PurchaseInvoiceDraftModel>> =
        sageApiRepository
            .saveDraftPurchaseInvoice(
                piNo = purchaseOrder.piNo,
                docNo = purchaseOrder.poNo,
                flagPO = if (purchaseInvoiceUseCase.isRefPo()) REF_PO else NOT_REF_PO,
                supplierCode = purchaseOrder.supplierCode,
                billNo = purchaseOrder.billNo,
                timestamp = purchaseInvoiceDetailLiveDataUseCase.updatedTimestamp.value ?: 0L,
                items = items,
            ).map { result ->
                when (result) {
                    is Result.Success -> result
                    is Result.Error -> handleError(result)
                }
            }

    private fun handleErrorOverQty(): Result.Error<PurchaseInvoiceDraftModel> =
        Result.Error(
            cause =
                BusinessException(
                    errorCode = "",
                    type = ErrorType.WARNING,
                    title = resourceUtils.getString(R.string.text_unable_to_continue),
                    message = resourceUtils.getString(R.string.text_alert_over_actual_qty),
                ),
        )

    private fun handleErrorConnectService(): Result.Error<PurchaseInvoiceDraftModel> =
        Result.Error(
            cause =
                BusinessException(
                    errorCode = "",
                    type = ErrorType.ERROR,
                    title = resourceUtils.getString(R.string.text_unable_to_continue),
                    message = resourceUtils.getString(R.string.text_service_failed),
                ),
        )
}
