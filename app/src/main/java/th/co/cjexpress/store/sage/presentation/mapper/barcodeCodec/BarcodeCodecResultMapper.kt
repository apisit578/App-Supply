package th.co.cjexpress.store.sage.presentation.mapper.barcodeCodec

import th.co.cjexpress.store.sage.domain.model.BarcodeCodecResultModel
import th.co.cjexpress.store.sage.presentation.model.component.TachyonBarcodeInputFieldUIModel

class BarcodeCodecResultMapper {
    fun toBarcodeCodecUIModel(model: BarcodeCodecResultModel): TachyonBarcodeInputFieldUIModel =
        TachyonBarcodeInputFieldUIModel(
            barcode = model.code,
            data = model.data,
            prefix = model.prefix,
        )
}
