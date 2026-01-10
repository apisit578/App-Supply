package th.co.cjexpress.store.sage.presentation.viewmodel.component

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import java.util.Calendar

class TachyonDateCounterViewModelTest : BaseUnitTest() {
    private lateinit var viewModel: TachyonDateCounterViewModel

    @Before
    fun setup() {
        viewModel = TachyonDateCounterViewModel()
    }

    @Test
    fun `setDefaultDate should update selectingDate`() {
        val calendar = Calendar.getInstance()
        calendar.set(2023, Calendar.JANUARY, 1)
        val defaultDate = calendar.time

        viewModel.setDefaultDate(defaultDate)
        val result = viewModel.nextDay()

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        assertEquals(calendar.time, result)
    }

    @Test
    fun `nextDay should return date increased by 1 day`() {
        val calendar = Calendar.getInstance()
        calendar.set(2023, Calendar.MARCH, 15)
        val startDate = calendar.time

        viewModel.setDefaultDate(startDate)
        val result = viewModel.nextDay()

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        assertEquals(calendar.time, result)
    }

    @Test
    fun `previousDay should return date decreased by 1 day`() {
        val calendar =
            Calendar.getInstance().apply {
                set(2023, Calendar.MARCH, 15)
            }
        val startDate = calendar.time
        viewModel.setDefaultDate(startDate)

        val result = viewModel.previousDay(minDate = null)

        calendar.add(Calendar.DAY_OF_MONTH, -1)
        assertEquals(calendar.time, result)
    }

    @Test
    fun `calling nextDay then previousDay should return to original date`() {
        val calendar =
            Calendar.getInstance().apply {
                set(2023, Calendar.JULY, 5)
            }
        val startDate = calendar.time
        viewModel.setDefaultDate(startDate)
        viewModel.nextDay()

        val result = viewModel.previousDay(minDate = null)

        assertEquals(startDate, result)
    }
}
