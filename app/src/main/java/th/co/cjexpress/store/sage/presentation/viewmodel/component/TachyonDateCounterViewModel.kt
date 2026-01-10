package th.co.cjexpress.store.sage.presentation.viewmodel.component

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TachyonDateCounterViewModel
    @Inject
    constructor() : ViewModel() {
        private var selectingDate = Date()

        fun setDefaultDate(defaultDate: Date) {
            selectingDate = defaultDate
        }

        fun nextDay(): Date {
            val calendar = Calendar.getInstance()
            calendar.time = selectingDate
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            selectingDate = calendar.time
            return selectingDate
        }

        fun previousDay(minDate: Date?): Date {
            val calendar = Calendar.getInstance()
            calendar.time = selectingDate
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            selectingDate =
                if (minDate != null && calendar.time.before(minDate)) {
                    minDate
                } else {
                    calendar.time
                }
            return selectingDate
        }
    }
