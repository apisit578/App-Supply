package th.co.cjexpress.store.sage.utils

import java.text.DecimalFormat

object CurrencyUtils {
    fun formatCurrency(number: Double): String =
        try {
            val formatter = DecimalFormat("#,##0.00")
            formatter.format(number)
        } catch (e: NumberFormatException) {
            "0.00"
        }
}
