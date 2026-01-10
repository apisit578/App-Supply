package th.co.cjexpress.store.sage.domain.model.payload

import th.co.cjexpress.store.sage.domain.model.payload.features.FeatureConfigs
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuOrderReceive
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuOrderReceiveTote
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuPurchaseInvoice
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuReprintQR
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuRequestDiscountRTC
import java.io.Serializable

data class PayloadFeatures(
    val menuRequestDiscountRTC: MenuRequestDiscountRTC? = null,
    val menuOrderReceiveTote: MenuOrderReceiveTote? = null,
    val menuOrderReceive: MenuOrderReceive? = null,
    val menuPurchaseInvoice: MenuPurchaseInvoice? = null,
    val menuReprintQR: MenuReprintQR? = null,
    val configs: FeatureConfigs? = null,
) : Serializable
