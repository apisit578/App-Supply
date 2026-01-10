package th.co.cjexpress.store.sage.data.mapper.reprintQrFresh

import th.co.cjexpress.store.sage.data.model.reprintQrFresh.response.QrDocumentSubmitDTOResponse
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitMappingModel

class QrDocumentSubmitMapper {
    fun map(data: QrDocumentSubmitDTOResponse): QrDocumentSubmitMappingModel =
        QrDocumentSubmitMappingModel(
            documentNumber = data.data.documentNumber,
        )
}
