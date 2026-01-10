package th.co.cjexpress.store.sage.presentation.viewmodel.component

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.STRING_FORMAT_DD_MM_YYYY
import th.co.cjexpress.store.sage.presentation.model.component.TachyonDateInputFieldResultModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.DateUtils.getCurrentYear
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TachyonDateInputFieldViewModel
    @Inject
    constructor() : BaseViewModel() {
        val dayFocusRequester = FocusRequester()
        val monthFocusRequester = FocusRequester()
        val yearFocusRequester = FocusRequester()

        private val _dayInputState = MutableStateFlow(TextFieldValue(""))
        val dayInputState: StateFlow<TextFieldValue> = _dayInputState

        private val _monthInputState = MutableStateFlow(TextFieldValue(""))
        val monthInputState: StateFlow<TextFieldValue> = _monthInputState

        private val _yearInputState = MutableStateFlow(TextFieldValue(getCurrentYear().toString()))
        val yearInputState: StateFlow<TextFieldValue> = _yearInputState

        private var onDateChangedCallback: ((TachyonDateInputFieldResultModel) -> Unit)? = null

        private val dateFormat =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                isLenient = false
            }

        fun setDateCallback(callback: (TachyonDateInputFieldResultModel) -> Unit) {
            onDateChangedCallback = callback
        }

        fun handleDayInput(newValue: TextFieldValue) {
            val filteredText = newValue.text.filter { it.isDigit() }.take(2)
            val newTextFieldValue =
                TextFieldValue(
                    text = filteredText,
                    selection = TextRange(filteredText.length),
                )
            updateDayState(newTextFieldValue)

            if (filteredText.length == 2) {
                focusMonthWithSelection()
            }
        }

        fun handleDayNext() {
            val currentValue = _dayInputState.value.text
            if (currentValue.isNotEmpty() && currentValue.length == 1) {
                val paddedText = currentValue.padStart(2, '0')
                updateDayState(
                    TextFieldValue(
                        text = paddedText,
                        selection = TextRange(paddedText.length),
                    ),
                )
            }
            if (currentValue.isNotEmpty()) {
                focusMonthWithSelection()
            }
        }

        fun handleMonthInput(newValue: TextFieldValue) {
            val filteredText = newValue.text.filter { it.isDigit() }.take(2)
            val newTextFieldValue =
                TextFieldValue(
                    text = filteredText,
                    selection = TextRange(filteredText.length),
                )
            updateMonthState(newTextFieldValue)

            if (filteredText.length == 2) {
                focusYearWithSelection()
            }
        }

        fun handleMonthNext() {
            val currentValue = _monthInputState.value.text
            if (currentValue.isNotEmpty() && currentValue.length == 1) {
                val paddedText = currentValue.padStart(2, '0')
                updateMonthState(
                    TextFieldValue(
                        text = paddedText,
                        selection = TextRange(paddedText.length),
                    ),
                )
            }
            if (currentValue.isNotEmpty()) {
                focusYearWithSelection()
            }
        }

        fun handleYearInput(newValue: TextFieldValue) {
            val filteredText = newValue.text.filter { it.isDigit() }.take(4)
            val newTextFieldValue =
                TextFieldValue(
                    text = filteredText,
                    selection = TextRange(filteredText.length),
                )
            updateYearState(newTextFieldValue)
        }

        fun handleBackspaceInMonth() {
            if (_monthInputState.value.text.isEmpty()) {
                focusDayWithSelection()
            }
        }

        fun handleBackspaceInYear() {
            if (_yearInputState.value.text.isEmpty()) {
                focusMonthWithSelection()
            }
        }

        private fun updateDayState(value: TextFieldValue) {
            _dayInputState.value = value
            validateAndNotifyDateChange()
        }

        private fun updateMonthState(value: TextFieldValue) {
            _monthInputState.value = value
            validateAndNotifyDateChange()
        }

        private fun updateYearState(value: TextFieldValue) {
            _yearInputState.value = value
            validateAndNotifyDateChange()
        }

        private fun validateAndNotifyDateChange() {
            val day = _dayInputState.value.text.toIntOrNull()
            val month = _monthInputState.value.text.toIntOrNull()
            val year = _yearInputState.value.text.toIntOrNull()

            val result =
                when {
                    isIncompleteDate(day, month, year) -> {
                        TachyonDateInputFieldResultModel(
                            errorCode = ErrorCodes.DateInputFieldValidationErrorCodes.INCOMPLETE_DATE,
                            resultDate = "",
                        )
                    }

                    !isValidDay(day) -> {
                        val formattedDate =
                            String.format(STRING_FORMAT_DD_MM_YYYY, day ?: 0, month ?: 0, year ?: 0)
                        TachyonDateInputFieldResultModel(
                            errorCode = ErrorCodes.DateInputFieldValidationErrorCodes.INVALID_DAY,
                            resultDate = formattedDate,
                        )
                    }

                    !isValidMonth(month) -> {
                        val formattedDate =
                            String.format(STRING_FORMAT_DD_MM_YYYY, day ?: 0, month ?: 0, year ?: 0)
                        TachyonDateInputFieldResultModel(
                            errorCode = ErrorCodes.DateInputFieldValidationErrorCodes.INVALID_MONTH,
                            resultDate = formattedDate,
                        )
                    }

                    !isValidYear(year) -> {
                        val formattedDate =
                            String.format(STRING_FORMAT_DD_MM_YYYY, day ?: 0, month ?: 0, year ?: 0)
                        TachyonDateInputFieldResultModel(
                            errorCode = ErrorCodes.DateInputFieldValidationErrorCodes.INVALID_YEAR,
                            resultDate = formattedDate,
                        )
                    }

                    !isValidCompleteDate(day!!, month!!, year!!) -> {
                        val formattedDate = String.format(STRING_FORMAT_DD_MM_YYYY, day, month, year)
                        TachyonDateInputFieldResultModel(
                            errorCode = ErrorCodes.DateInputFieldValidationErrorCodes.DATE_NOT_EXIST,
                            resultDate = formattedDate,
                        )
                    }

                    else -> {
                        val formattedDate = String.format(STRING_FORMAT_DD_MM_YYYY, day!!, month!!, year!!)
                        TachyonDateInputFieldResultModel(
                            errorCode = null,
                            resultDate = formattedDate,
                        )
                    }
                }

            onDateChangedCallback?.invoke(result)
        }

        private fun isIncompleteDate(
            day: Int?,
            month: Int?,
            year: Int?,
        ): Boolean = day == null || month == null || year == null || day == 0 || month == 0 || year == 0

        private fun isValidDay(day: Int?): Boolean = day != null && day in 1..31

        private fun isValidMonth(month: Int?): Boolean = month != null && month in 1..12

        private fun isValidYear(year: Int?): Boolean = year != null && year > 0

        private fun isValidCompleteDate(
            day: Int,
            month: Int,
            year: Int,
        ): Boolean =
            try {
                val dateString = String.format(STRING_FORMAT_DD_MM_YYYY, day, month, year)
                val parsedDate = dateFormat.parse(dateString)

                val calendar = Calendar.getInstance().apply { time = parsedDate }
                val parsedDay = calendar.get(Calendar.DAY_OF_MONTH)
                val parsedMonth = calendar.get(Calendar.MONTH) + 1
                val parsedYear = calendar.get(Calendar.YEAR)

                day == parsedDay && month == parsedMonth && year == parsedYear
            } catch (e: Exception) {
                false
            }

        fun setInitialDate(dateString: String) {
            if (dateString.isBlank()) {
                clearAllStates()
                return
            }

            parseDateString(dateString)?.let { (day, month, year) ->
                _dayInputState.value =
                    TextFieldValue(
                        text = day,
                        selection = TextRange(day.length),
                    )
                _monthInputState.value =
                    TextFieldValue(
                        text = month,
                        selection = TextRange(month.length),
                    )
                _yearInputState.value =
                    TextFieldValue(
                        text = year,
                        selection = TextRange(year.length),
                    )
                validateAndNotifyDateChange()
            }
        }

        fun clearAllStates() {
            _dayInputState.value = TextFieldValue("")
            _monthInputState.value = TextFieldValue("")
            _yearInputState.value = TextFieldValue(Calendar.getInstance().get(Calendar.YEAR).toString())
            onDateChangedCallback?.invoke(
                TachyonDateInputFieldResultModel(
                    errorCode = ErrorCodes.DateInputFieldValidationErrorCodes.INCOMPLETE_DATE,
                    resultDate = "",
                ),
            )
        }

        private fun parseDateString(dateString: String): Triple<String, String, String>? {
            val parts = dateString.split("/")
            return if (parts.size == 3) {
                Triple(
                    parts[0].filter { it.isDigit() }.take(2),
                    parts[1].filter { it.isDigit() }.take(2),
                    parts[2].filter { it.isDigit() }.take(4),
                )
            } else {
                null
            }
        }

        private fun focusDayWithSelection() {
            val currentText = _dayInputState.value.text
            _dayInputState.value =
                _dayInputState.value.copy(
                    selection = TextRange(currentText.length),
                )
            dayFocusRequester.requestFocus()
        }

        private fun focusMonthWithSelection() {
            val currentText = _monthInputState.value.text
            _monthInputState.value =
                _monthInputState.value.copy(
                    selection = TextRange(currentText.length),
                )
            monthFocusRequester.requestFocus()
        }

        private fun focusYearWithSelection() {
            val currentText = _yearInputState.value.text
            _yearInputState.value =
                _yearInputState.value.copy(
                    selection = TextRange(currentText.length),
                )
            yearFocusRequester.requestFocus()
        }

        fun returnAsInvalidateFormat() {
            onDateChangedCallback?.invoke(
                TachyonDateInputFieldResultModel(
                    errorCode = ErrorCodes.DateInputFieldValidationErrorCodes.INVALID_DATE_FORMAT,
                    resultDate = "",
                ),
            )
        }
    }
