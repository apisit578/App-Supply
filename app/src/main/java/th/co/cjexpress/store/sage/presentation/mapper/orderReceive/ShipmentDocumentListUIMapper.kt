package th.co.cjexpress.store.sage.presentation.mapper.orderReceive

import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentListItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentListUIModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat

class ShipmentDocumentListUIMapper {
    fun toUIModel(result: DocumentSDModel) =
        ShipmentDocumentListUIModel(
            documentListSize = result.data.size.toString(),
            shipmentDocumentList =
                result.data.map {
                    ShipmentDocumentListItemUIModel(
                        date = it.createdDate.toStringDDMMYYYYFormat(),
                        sdNo = it.sdNo,
                        ldNo = it.ldNo,
                    )
                },
        )
}
