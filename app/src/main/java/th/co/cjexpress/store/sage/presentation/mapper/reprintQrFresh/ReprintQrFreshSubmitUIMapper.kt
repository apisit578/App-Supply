package th.co.cjexpress.store.sage.presentation.mapper.reprintQrFresh

import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitItemModel
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel

class ReprintQrFreshSubmitUIMapper {
    fun toQrDocumentSubmitItems(items: List<ReprintQrFreshItemUIModel>) =
        items
            .map {
                QrDocumentSubmitItemModel(
                    barcode = it.barcode,
                    expireDate = it.expireDate,
                    qty = it.qrPrintCount,
                )
            }.toList()
}
