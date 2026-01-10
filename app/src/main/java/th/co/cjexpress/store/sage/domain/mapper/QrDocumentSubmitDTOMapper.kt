package th.co.cjexpress.store.sage.domain.mapper

import th.co.cjexpress.store.sage.data.model.reprintQrFresh.request.QrDocumentSubmitDTORequest
import th.co.cjexpress.store.sage.data.model.reprintQrFresh.request.QrDocumentSubmitData
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitModel

class QrDocumentSubmitDTOMapper {
    fun toDTO(domainModel: QrDocumentSubmitModel) =
        QrDocumentSubmitDTORequest(
            lotNumber = domainModel.lotNumber,
            data =
                domainModel.data.map {
                    QrDocumentSubmitData(
                        barcode = it.barcode,
                        expireDate = it.expireDate,
                        qty = it.qty,
                    )
                },
        )
}
