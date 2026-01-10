package th.co.cjexpress.store.sage.utils

import th.co.cjexpress.store.sage.constants.MainMenuType
import java.util.EnumMap
import java.util.UUID

object UUIDUtils {
    private var currentMenu: MainMenuType = MainMenuType.NONE
    private val menuUUID: EnumMap<MainMenuType, String> = EnumMap(MainMenuType::class.java)

    fun generateUUID(menu: MainMenuType) {
        menuUUID[menu] = UUID.randomUUID().toString()
    }

    fun getCurrentUUID(): String? = menuUUID[currentMenu]

    fun setCurrentMenuType(screen: String) {
        currentMenu =
            when {
                screen.contains("OrderReceiveTote") -> MainMenuType.ORDER_RECEIVE_TOTE
                screen.contains("PurchaseInvoice") -> MainMenuType.PURCHASE_INVOICE
                screen.contains("OrderReceive") -> MainMenuType.ORDER_RECEIVE
                screen.contains("RequestDiscount") -> MainMenuType.REQUEST_DISCOUNT_RTC
                screen.contains("ReprintQr") -> MainMenuType.REPRINT_QR
                else -> MainMenuType.NONE
            }
    }
}
