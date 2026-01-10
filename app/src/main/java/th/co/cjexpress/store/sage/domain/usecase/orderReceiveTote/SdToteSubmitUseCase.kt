package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SdToteSubmitUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(
        sdNo: String,
        toteCode: String,
        barcode: String,
        skuCode: String,
        discountType: String,
        actualQty: Int,
        expList: List<ISdToteExpiryDateModel>?,
    ): Flow<Result<SdToteSubmitMappingModel>> =
        sageApiRepository
            .sdToteSubmit(
                sdNo = sdNo,
                toteCode = toteCode,
                barcode = barcode,
                skuCode = skuCode,
                discountType = discountType,
                actualQty = actualQty,
                expList = expList,
            ).map { result ->
                when (result) {
                    is Result.Success -> result
                    is Result.Error -> super.handleError(result)
                }
            }
}
