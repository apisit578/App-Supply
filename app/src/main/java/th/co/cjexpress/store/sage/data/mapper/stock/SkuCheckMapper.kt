package th.co.cjexpress.store.sage.data.mapper.stock

import th.co.cjexpress.store.sage.data.model.orderReceive.BarcodeDataResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.SkuBarcodeDataResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.SkuCheckResponse
import th.co.cjexpress.store.sage.domain.model.orderReceive.BarcodeDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.SkuBarcodeDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.SkuCheckModel

class SkuCheckMapper {
    fun map(apiResponse: SkuCheckResponse): SkuCheckModel =
        SkuCheckModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResponse.data?.toSkuBarcodeDataModel() ?: SkuBarcodeDataModel("", emptyList()),
        )

    private fun SkuBarcodeDataResponse.toSkuBarcodeDataModel(): SkuBarcodeDataModel =
        SkuBarcodeDataModel(
            skuName = this.skuName ?: "",
            barcodes = this.barcodes?.map { it.toBarcodeDataModel() } ?: emptyList(),
        )

    private fun BarcodeDataResponse.toBarcodeDataModel(): BarcodeDataModel =
        BarcodeDataModel(
            barcode = this.barcode ?: "",
            barcodeFactor = this.barcodeFactor ?: 1,
            unitCode = this.unitCode ?: "",
            unitName = this.unitName ?: "",
        )
}
