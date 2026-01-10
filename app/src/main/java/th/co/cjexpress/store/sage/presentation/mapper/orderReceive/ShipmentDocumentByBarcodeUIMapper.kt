package th.co.cjexpress.store.sage.presentation.mapper.orderReceive

import androidx.compose.runtime.toMutableStateList
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel

class ShipmentDocumentByBarcodeUIMapper {
    fun toUIModel(data: ShipmentDocumentByBarcodeModel): ShipmentDocumentUIModel =
        ShipmentDocumentUIModel(
            sdNo = data.data.sdNo,
            ldNo = "",
            createdDate = "",
            count = data.data.count,
            sumOfActualQty = data.data.sumOfActualQty,
            items =
                data.data.items
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
