package th.co.cjexpress.store.sage.presentation.model.orderReceiveTote

interface ISdToteReceivingBarcodeModel {
    val barcode: String
    val skuCode: String
    val discountType: String
    val noOfExp: Int
}
