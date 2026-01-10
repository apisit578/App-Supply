package th.co.cjexpress.store.sage.utils

import android.os.Build
import androidx.annotation.RequiresApi
import th.co.cjexpress.store.sage.constants.BUS_DATE_FORMAT
import th.co.cjexpress.store.sage.constants.DATE_FORMAT_DDMMYYYY
import th.co.cjexpress.store.sage.constants.DATE_FORMAT_DD_MM_YYYY
import th.co.cjexpress.store.sage.constants.POSADMIN_DATE_FORMAT
import th.co.cjexpress.store.sage.constants.YYMMDD_DATE_FORMAT
import th.co.cjexpress.store.sage.constants.YYYYMMDD_DATE_FORMAT
import th.co.cjexpress.store.sage.constants.YYYYMMDD_HHMMSSZ_DATE_FORMAT
import th.co.cjexpress.store.sage.constants.YYYY_MM_DD_DATE_FORMAT
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private val ddMMyyyyFormat = SimpleDateFormat(DATE_FORMAT_DDMMYYYY)
    private val dd_MM_yyyyFormat = SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY)
    private val busDateFormat = SimpleDateFormat(BUS_DATE_FORMAT)
    private val posAdminDateFormat = SimpleDateFormat(POSADMIN_DATE_FORMAT)
    private val yyyyMMddHHmmSSzFormat = SimpleDateFormat(YYYYMMDD_HHMMSSZ_DATE_FORMAT)
    private val yyMMddFormat = SimpleDateFormat(YYMMDD_DATE_FORMAT)
    private val yyyyMMddFormat = SimpleDateFormat(YYYYMMDD_DATE_FORMAT)
    private val yyyy_MM_ddFormat = SimpleDateFormat(YYYY_MM_DD_DATE_FORMAT)

    fun Date.toStringDDMMYYYYFormat(): String = ddMMyyyyFormat.format(this)

    fun Date.toStringDD_MM_YYYYFormat(): String = dd_MM_yyyyFormat.format(this)

    fun Date.toStringBusDateFormat(): String = busDateFormat.format(this)

    fun Date.toStringPosAdminDateFormat(): String = posAdminDateFormat.format(this)

    fun String.fromDDMMYYYYFormatToDate(): Date = ddMMyyyyFormat.parse(this)

    fun String.fromBusDateFormatToDate(): Date = busDateFormat.parse(this)

    fun String.toDDMMYYYYHHMMSSZDate(): Date {
        val parsedDate: Date = ddMMyyyyFormat.parse(this)
        val posAdminDate = yyyyMMddHHmmSSzFormat.format(parsedDate)
        return yyyyMMddHHmmSSzFormat.parse(posAdminDate)
    }

    fun getUpdatedInputDateRow(
        currentDate: Date,
        isAdd: Boolean,
    ): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_MONTH, if (isAdd) 1 else -1)
        return calendar.time
    }

    fun adjustDateByDays(
        currentDate: Date,
        daysToAdd: Int,
    ): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)
        return calendar.time
    }

    fun Date.toStringYYYYMMDDFormat(): String = yyyy_MM_ddFormat.format(this)

    fun String.toYYMMDDDate(): Date = yyMMddFormat.apply { isLenient = false }.parse(this)

    fun String.toYYYYMMDDDate(): Date = yyyyMMddFormat.apply { isLenient = false }.parse(this)

    fun getTimestampUnix(isoDateTime: String): Long {
        try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",Locale("th", "TH"))
            val date = isoFormat.parse(isoDateTime)
            return (date?.time ?: System.currentTimeMillis()) / 1000
        }catch (e: Exception) {
            return System.currentTimeMillis() / 1000
        }
    }

    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }

    fun isDateBeforeCurrentDate(
        date: String,
        month: String,
        year: String,
    ): Boolean {
        val inputCalendar =
            Calendar.getInstance().apply {
                set(Calendar.YEAR, year.toInt())
                set(Calendar.MONTH, month.toInt() - 1)
                set(Calendar.DAY_OF_MONTH, date.toInt())
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

        val currentCalendar =
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

        return inputCalendar.before(currentCalendar)
    }
}
