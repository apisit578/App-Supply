package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import th.co.cjexpress.store.sage.utils.DateUtils.fromDDMMYYYYFormatToDate
import java.util.Calendar
import java.util.Date

@Composable
fun AndroidAppDatePickerDialog(
    expiryDate: String?,
    onDismiss: () -> Unit,
    onDateSelected: (Date) -> Unit,
) {
    val calendar = Calendar.getInstance()
    val arrayDate = expiryDate?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
    val defYear: Int
    val defMonth: Int
    val defDay: Int

    if (!expiryDate.isNullOrEmpty()) {
        arrayDate!!.let {
            defYear = it[2].toInt()
            defMonth = it[1].toInt() - 1
            defDay = it[0].toInt()
        }
    } else {
        defYear = calendar.get(Calendar.YEAR)
        defMonth = calendar.get(Calendar.MONTH)
        defDay = calendar.get(Calendar.DAY_OF_MONTH)
    }

    DatePickerDialog(
        LocalContext.current,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(selectedDate.fromDDMMYYYYFormatToDate())
        },
        defYear,
        defMonth,
        defDay,
    ).apply {
        datePicker.minDate = calendar.timeInMillis

        setOnDismissListener {
            onDismiss()
        }

        show()
    }
}
