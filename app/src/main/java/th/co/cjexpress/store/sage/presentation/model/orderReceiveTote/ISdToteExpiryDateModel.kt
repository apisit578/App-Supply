package th.co.cjexpress.store.sage.presentation.model.orderReceiveTote

import java.util.Date

abstract class ISdToteExpiryDateModel(
    open val expDate: Date?,
    open var actualQty: Int,
    open var qty: Int,
    open var lotNo: String,
) : Cloneable {
    public override fun clone(): Any = super.clone()
}
