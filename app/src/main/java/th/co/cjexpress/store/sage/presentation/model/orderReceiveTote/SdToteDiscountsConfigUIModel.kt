package th.co.cjexpress.store.sage.presentation.model.orderReceiveTote

data class SdToteDiscountsConfigUIModel(
    override val barcode: String,
    override val skuCode: String,
    override val discountType: String,
    override val noOfExp: Int,
    val skuName: String,
    val unitName: String,
    var expList: MutableList<ISdToteExpiryDateModel> = mutableListOf()
) : ISdToteReceivingBarcodeModel
