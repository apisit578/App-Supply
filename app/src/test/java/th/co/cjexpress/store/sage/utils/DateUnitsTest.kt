package th.co.cjexpress.store.sage.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import th.co.cjexpress.store.sage.constants.BUS_DATE_FORMAT
import th.co.cjexpress.store.sage.constants.POSADMIN_DATE_FORMAT
import th.co.cjexpress.store.sage.constants.YYYY_MM_DD_DATE_FORMAT
import th.co.cjexpress.store.sage.utils.DateUtils.fromBusDateFormatToDate
import th.co.cjexpress.store.sage.utils.DateUtils.toDDMMYYYYHHMMSSZDate
import th.co.cjexpress.store.sage.utils.DateUtils.toStringBusDateFormat
import th.co.cjexpress.store.sage.utils.DateUtils.toStringPosAdminDateFormat
import th.co.cjexpress.store.sage.utils.DateUtils.toStringYYYYMMDDFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateUtilsTest {
    private fun createDate(
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
    ): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, second) // Month is 0-based
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun createExpectedDate(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int,
        millisecond: Int,
        timeZoneId: String,
    ): Date {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId))
        calendar.set(year, month - 1, day, hour, minute, second)
        calendar.set(Calendar.MILLISECOND, millisecond)
        return calendar.time
    }

    @Test
    fun toStringBusDateFormat_formatsDateCorrectly() {
        val date = createDate(2023, 10, 26)
        val expected = SimpleDateFormat(BUS_DATE_FORMAT, Locale.getDefault()).format(date)
        assertEquals(expected, date.toStringBusDateFormat())
    }

    @Test
    fun toStringPosAdminDateFormat_formatsDateCorrectly() {
        val date = createDate(2023, 10, 26, 14, 30, 15)
        val expected = SimpleDateFormat(POSADMIN_DATE_FORMAT, Locale.getDefault()).format(date)
        assertEquals(expected, date.toStringPosAdminDateFormat())
    }

    @Test
    fun getUpdatedInputDateRow_addsDayWhenIsAddTrue() {
        val currentDate = createDate(2023, 10, 26)
        val expectedDate = createDate(2023, 10, 27)
        assertEquals(expectedDate, DateUtils.getUpdatedInputDateRow(currentDate, true))
    }

    @Test
    fun getUpdatedInputDateRow_subtractsDayWhenIsAddFalse() {
        val currentDate = createDate(2023, 10, 26)
        val expectedDate = createDate(2023, 10, 25)
        assertEquals(expectedDate, DateUtils.getUpdatedInputDateRow(currentDate, false))
    }

    @Test
    fun adjustDateByDays_addsCorrectNumberOfDays() {
        val currentDate = createDate(2023, 10, 26)
        val expectedDate = createDate(2023, 10, 29) // 26 + 3 days
        assertEquals(expectedDate, DateUtils.adjustDateByDays(currentDate, 3))
    }

    @Test
    fun adjustDateByDays_subtractsCorrectNumberOfDays() {
        val currentDate = createDate(2023, 10, 26)
        val expectedDate = createDate(2023, 10, 23) // 26 - 3 days
        assertEquals(expectedDate, DateUtils.adjustDateByDays(currentDate, -3))
    }

    @Test
    fun adjustDateByDays_handlesZeroDays() {
        val currentDate = createDate(2023, 10, 26)
        assertEquals(currentDate, DateUtils.adjustDateByDays(currentDate, 0))
    }

    @Test
    fun toStringYYYYMMDDFormat_formatsDateCorrectly() {
        val date = createDate(2023, 10, 26)
        val expected = "2023-10-26"
        assertEquals(expected, date.toStringYYYYMMDDFormat())
    }

    @Test
    fun toStringYYYYMMDDFormat_uses_yyyy_MM_ddFormat() {
        val date = createDate(2024, 1, 15)
        val expected = SimpleDateFormat(YYYY_MM_DD_DATE_FORMAT, Locale.getDefault()).format(date)
        assertEquals(expected, date.toStringYYYYMMDDFormat())
    }

    @Test
    fun fromBusDateFormatToDate_parsesStringWithOffsetCorrectly() {
        val dateStringWithOffset = "2023-10-26T14:30:45.123+0700"
        val expected =
            createExpectedDate(
                year = 2023,
                month = 10,
                day = 26,
                hour = 14,
                minute = 30,
                second = 45,
                millisecond = 123,
                timeZoneId = "GMT+07:00",
            )
        val actual = dateStringWithOffset.fromBusDateFormatToDate()
        assertNotNull("Parsed date should not be null for valid input", actual)
        assertEquals(expected.time, actual.time)
    }

    @Test(expected = ParseException::class)
    fun fromBusDateFormatToDate_throwsParseExceptionForInvalidFormat() {
        val invalidDateString = "26-10-2023"
        invalidDateString.fromBusDateFormatToDate()
    }

    @Test
    fun toDDMMYYYYHHMMSSZDate_convertsDateToUtcMidnight() {
        val inputDateString = "26/10/2023"
        val expected =
            createDate(
                year = 2023,
                month = 10,
                day = 26,
                hour = 0,
                minute = 0,
                second = 0,
            )
        val actual = inputDateString.toDDMMYYYYHHMMSSZDate()
        assertNotNull("Converted date should not be null for valid input", actual)
        assertEquals(expected.time, actual.time)
    }

    @Test
    fun toDDMMYYYYHHMMSSZDate_handlesDifferentDateCorrectly() {
        val inputDateString = "01/01/2024"
        val expected =
            createDate(
                year = 2024,
                month = 1,
                day = 1,
                hour = 0,
                minute = 0,
                second = 0,
            )
        val actual = inputDateString.toDDMMYYYYHHMMSSZDate()
        assertNotNull("Converted date should not be null", actual)
        assertEquals(expected.time, actual.time)
    }

    @Test
    fun toDDMMYYYYHHMMSSZDate_handlesLeapYearDate() {
        val inputDateString = "29/02/2024"
        val expected =
            createDate(
                year = 2024,
                month = 2,
                day = 29,
                hour = 0,
                minute = 0,
                second = 0,
            )
        val actual = inputDateString.toDDMMYYYYHHMMSSZDate()
        assertNotNull("Converted date should not be null", actual)
        assertEquals(expected.time, actual.time)
    }

    @Test(expected = ParseException::class)
    fun toDDMMYYYYHHMMSSZDate_throwsParseExceptionForInvalidInputFormat() {
        val invalidInputString = "20231026" // Incorrect format for "ddMMyyyy"
        invalidInputString.toDDMMYYYYHHMMSSZDate() // Should throw due to ddMMyyyyFormat
    }

    @Test(expected = ParseException::class)
    fun toDDMMYYYYHHMMSSZDate_throwsParseExceptionForNonExistentDate() {
        val nonExistentDateString = "32102023" // Invalid day for "ddMMyyyy"
        nonExistentDateString.toDDMMYYYYHHMMSSZDate()
    }
}
