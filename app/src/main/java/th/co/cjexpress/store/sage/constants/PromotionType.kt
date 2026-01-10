package th.co.cjexpress.store.sage.constants

enum class PromotionType(
    val promotionPrefix: String,
) {
    COMMON_BARCODE(""),

    STANDARD_DEFAULT("A0"),
    STANDARD_DISCOUNT("A1"),
    STANDARD_EXPIRE_DATE("A2"),
    STANDARD_NEW_PRICE("A3"),
    STANDARD_PROMOTION_PRICE("A4"),

    EXTENDED_DEFAULT("B0"),
    EXTENDED_DISCOUNT("B1"),
    EXTENDED_EXPIRE_DATE("B2"),
    EXTENDED_NEW_PRICE("B3"),
    EXTENDED_PROMOTION_PRICE("B4"),
    QR_FRESH("L"),
    ;

    override fun toString(): String = promotionPrefix

    companion object {
        fun fromPrefix(prefix: String): PromotionType? = entries.find { it.promotionPrefix == prefix }
    }
}
