package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceItemModel
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class ScanPurchaseInvoiceOrderUseCase(
    private val purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(barcode: String): Flow<Result<IPurchaseInvoiceProductsModel>> =
        if (barcode.isBlank()) {
            flowOf(
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_scan_or_enter_barcode),
                        ),
                ),
            )
        } else {
            purchaseInvoiceDetailLiveDataUseCase.getPurchaseInvoiceOrder(barcode)?.let {
                val result =
                    PurchaseInvoiceItemModel(
                        barcode = it.barcode,
                        actualQty = it.actualQty + 1,
                    )
                flowOf(Result.Success(result))
            } ?: run {
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "",
                                type = ErrorType.WARNING,
                                title = resourceUtils.getString(R.string.text_purchase_order_barcode_mismatch),
                                message =
                                    String.format(
                                        resourceUtils.getString(R.string.text_purchase_order_barcode),
                                        barcode,
                                    ),
                            ),
                    ),
                )
            }
        }
}
