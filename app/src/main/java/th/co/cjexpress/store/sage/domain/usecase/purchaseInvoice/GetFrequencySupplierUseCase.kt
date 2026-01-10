package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierListModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetFrequencySupplierUseCase(
    private val sageApiRepository: SageApiRepository,
    private val menuPurchaseInvoiceUseCase: MenuPurchaseInvoiceUseCase,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(): Flow<Result<PurchaseInvoiceSupplierListModel>> =
        sageApiRepository
            .getFrequencySupplier(
                menuPurchaseInvoiceUseCase.supplierPerPage().toString(),
            ).map { result ->
                when (result) {
                    is Result.Success -> result
                    is Result.Error -> super.handleError(result)
                }
            }
}
