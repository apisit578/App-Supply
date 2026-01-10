package th.co.cjexpress.store.sage.presentation.mapper.orderReceive

import androidx.compose.runtime.toMutableStateList
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDataModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel

class ShipmentDocumentUIMapper {
    fun toUIModel(data: ShipmentDataModel): ShipmentDocumentUIModel =
        ShipmentDocumentUIModel(
            sdNo = data.sdNo,
            ldNo = data.ldNo,
            createdDate = data.createdDate,
            count = data.count,
            sumOfActualQty = data.sumOfActualQty,
            items =
                data.items
                    .map { item ->
                        ShipmentDataItemUIModel(
                            actualQty = item.actualQty,
                            barcode = item.barcode,
                            isTote = item.isTote,
                            toteCode = item.toteCode,
                            productName = item.productName,
                            qty = item.qty,
                            unitName = item.unitName,
                            unitFactor = item.unitFactor,
                        )
                    }.toMutableStateList(),
        )
}
