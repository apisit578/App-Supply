package th.co.cjexpress.store.sage.data.mapper.orderReceive

import th.co.cjexpress.store.sage.data.model.orderReceive.DocumentSDResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.DocumentSDResponseData
import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDModel

class DocumentSDMapper {
    fun map(apiResponse: DocumentSDResponse): DocumentSDModel =
        DocumentSDModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResponse.data?.map { it.toDocumentSDDataModel() } ?: emptyList(),
        )

    private fun DocumentSDResponseData.toDocumentSDDataModel() =
        DocumentSDDataModel(
            createdDate = createdDate,
            sdNo = sdNo,
            ldNo = ldNo,
        )
}
