package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentListModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetPurchaseInvoiceDocumentUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(supplierCode: String): Flow<Result<PurchaseInvoiceDocumentListModel>> =
        sageApiRepository.getPurchaseInvoiceDocument(supplierCode).map { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> super.handleError(result)
            }
        }
}
