package th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.DateUtils
import th.co.cjexpress.store.sage.utils.DateUtils.isDateBeforeCurrentDate
import th.co.cjexpress.store.sage.utils.ResourceUtils

@ExperimentalCoroutinesApi
class CheckSubmitItemDetailUseCaseTest : BaseUnitTest() {
    private val resourceUtils = mockk<ResourceUtils>()
    private lateinit var useCase: CheckSubmitItemDetailUseCase

    override fun setUp() {
        super.setUp()
        useCase = CheckSubmitItemDetailUseCase(resourceUtils)
        setupResourceStrings()
    }

    private fun setupResourceStrings() {
        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
        every { resourceUtils.getString(R.string.text_please_input_expire_date) } returns "Please input expire date"
        every { resourceUtils.getString(R.string.text_expiration_date_is_incorrect) } returns "Date mismatch"
        every { resourceUtils.getString(R.string.text_expire_date_mismatch) } returns "Invalid date format"
        every { resourceUtils.getString(R.string.text_expire_date_lower) } returns "Date cannot be in the past"
        every { resourceUtils.getString(R.string.text_qr_code_qty_mismatch) } returns "Invalid quantity"
    }

    @Test
    fun `execute returns error when diff year more than 100`() =
        runTest {
            // given
            val mockItem = mockk<ReprintQrFreshItemUIModel>()

            // when
            val result =
                useCase
                    .execute(
                        item = mockItem,
                        actualQty = 1,
                        dateInput = "1/1/2500",
                    ).first()

            // then
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).cause as BusinessException
            assertEquals(ErrorType.WARNING, error.type)
            assertEquals("Unable to continue", error.title)
            assertEquals("Date mismatch", error.message)
        }

    @Test
    fun `execute returns error when quantity is zero`() =
        runTest {
            // given
            val mockItem = mockk<ReprintQrFreshItemUIModel>()
            mockkObject(DateUtils)
            every {
                isDateBeforeCurrentDate(
                    date = any(),
                    month = any(),
                    year = any(),
                )
            } returns false

            // when
            val result =
                useCase
                    .execute(
                        item = mockItem,
                        actualQty = 0,
                        dateInput = "01/01/2024",
                    ).first()

            // then
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).cause as BusinessException
            assertEquals(ErrorType.WARNING, error.type)
            assertEquals("Invalid quantity", error.message)
        }

    @Test
    fun `execute returns success with valid inputs`() =
        runTest {
            // given
            val inputItem =
                ReprintQrFreshItemUIModel(
                    barcode = "123",
                    skuName = "Test Product",
                    expireDate = "",
                    qrPrintCount = 0,
                )
            mockkObject(DateUtils)
            every {
                isDateBeforeCurrentDate(
                    date = any(),
                    month = any(),
                    year = any(),
                )
            } returns false

            // when
            val result =
                useCase
                    .execute(
                        item = inputItem,
                        actualQty = 1,
                        dateInput = "01/12/2024",
                    ).first()

            // then
            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals("01/12/2024", data.expireDate)
            assertEquals(1, data.qrPrintCount)
            assertEquals("123", data.barcode)
            assertEquals("Test Product", data.skuName)
        }

    @Test
    fun `execute returns success with valid inputs when item null`() =
        runTest {
            // given
            val inputItem = null
            mockkObject(DateUtils)
            every {
                isDateBeforeCurrentDate(
                    date = any(),
                    month = any(),
                    year = any(),
                )
            } returns false

            // when
            val result =
                useCase
                    .execute(
                        item = inputItem,
                        actualQty = 1,
                        dateInput = "01/12/2024",
                    ).first()

            // then
            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals("01/12/2024", data.expireDate)
            assertEquals(1, data.qrPrintCount)
            assertEquals("", data.barcode)
            assertEquals("", data.skuName)
        }

    @Test
    fun `execute returns error for past date`() =
        runTest {
            // given
            val mockItem = mockk<ReprintQrFreshItemUIModel>()

            // when
            val result =
                useCase
                    .execute(
                        item = mockItem,
                        actualQty = 1,
                        dateInput = "01/01/2023",
                    ).first()

            // then
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).cause as BusinessException
            assertEquals(ErrorType.WARNING, error.type)
            assertEquals("Date cannot be in the past", error.message)
        }

    @Test
    fun `handleResultError returns proper error result`() {
        // when
        val result =
            useCase.handleResultError(
                title = "Test Title",
                message = "Test Message",
            )

        // then
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).cause as BusinessException
        assertEquals(ErrorType.WARNING, error.type)
        assertEquals("Test Title", error.title)
        assertEquals("Test Message", error.message)
        assertEquals("", error.errorCode)
    }
}
