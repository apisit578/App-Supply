package th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc

import androidx.lifecycle.MutableLiveData
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc.RequestDiscountLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class BdDocumentValidateUseCaseTest {
    @RelaxedMockK
    lateinit var repo: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var liveData: RequestDiscountLiveData
    private lateinit var useCase: BdDocumentValidateUseCase
    private lateinit var dummyLiveData: MutableLiveData<BdDetailProductUIModel?>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        liveData = RequestDiscountLiveData()
        dummyLiveData = MutableLiveData()
        useCase = BdDocumentValidateUseCase(liveData, repo, resourceUtils)

        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
        every { resourceUtils.getString(R.string.text_please_scan_barcode) } returns "Scan-barcode"
        every { resourceUtils.getString(R.string.text_not_found_data) } returns "Not-Found"
        every { resourceUtils.getString(R.string.text_product_not_eligible) } returns "Not-Eligible"
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `execute success returns Result_Success`() =
        runTest {
            val mapping = mockk<BdDocumentValidateMappingModel>()
            coEvery { repo.bdDocumentValidate("DISC", "123", null) } returns
                flowOf(Result.Success(mapping))

            useCase.execute("DISC", "123").collectLatest { out ->
                assertTrue(out is Result.Success)
                assertEquals(mapping, (out as Result.Success).data)
            }
        }

    @Test
    fun `empty barcode returns INVALID_PARAMETER warning without hitting repo`() =
        runTest {
            useCase.execute("", "").collectLatest { out ->
                val c = (out as Result.Error).cause
                assertEquals(ErrorCodes.INVALID_PARAMETER, c.errorCode)
                assertEquals(ErrorType.WARNING, c.type)
                assertEquals("Scan-barcode", c.message)
                assertEquals("Unable", c.title)
            }
            coVerify(exactly = 0) { repo.bdDocumentValidate(any(), any(), any()) }
        }

    @Test
    fun `error NOT_FOUND maps to NOT_FOUND warning with generic text`() =
        runTest {
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.NOT_FOUND,
                    type = ErrorType.ERROR,
                    message = "raw-msg",
                )
            coEvery { repo.bdDocumentValidate("DISC", "123", null) } returns
                flowOf(Result.Error(cause = cause))

            useCase.execute("DISC", "123").collectLatest { out ->
                val c = (out as Result.Error).cause
                assertEquals(ErrorCodes.NOT_FOUND, c.errorCode)
                assertEquals(ErrorType.WARNING, c.type)
                assertEquals("Not-Found", c.message)
                assertEquals("Unable", c.title)
            }
        }

    @Test
    fun `error NOT_ELIGIBLE maps to NOT_FOUND warning with eligible text`() =
        runTest {
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.NOT_ELIGIBLE,
                    type = ErrorType.WARNING,
                    message = "should-be-ignored",
                )
            coEvery { repo.bdDocumentValidate("DISC", "123", null) } returns
                flowOf(Result.Error(cause = cause))

            useCase.execute("DISC", "123").collectLatest { out ->
                val c = (out as Result.Error).cause
                assertEquals(ErrorCodes.NOT_FOUND, c.errorCode)
                assertEquals(ErrorType.WARNING, c.type)
                assertEquals("Not-Eligible", c.message)
                assertEquals("Unable", c.title)
            }
        }

    @Test
    fun `unexpected error maps to BAD_REQUEST error`() =
        runTest {
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.INVALID_BODY,
                    type = ErrorType.ERROR,
                    message = "backend-msg",
                )
            coEvery { repo.bdDocumentValidate("DISC", "123", null) } returns
                flowOf(Result.Error(cause = cause))

            useCase.execute("DISC", "123").collectLatest { out ->
                val c = (out as Result.Error).cause
                assertEquals(ErrorCodes.BAD_REQUEST, c.errorCode)
                assertEquals(ErrorType.ERROR, c.type)
                assertEquals("backend-msg", c.message)
                assertEquals("Unable", c.title)
            }
        }

    @Test
    fun `unknown code with message is passed through BaseUseCase`() =
        runTest {
            every { resourceUtils.getString(any()) } returns "GENERIC"
            val cause = BusinessException("00000", ErrorType.ERROR, message = "err!")
            val out =
                useCase.handleError(Result.Error<BdDocumentValidateMappingModel>(cause = cause))

            val c = out.cause
            assertEquals("00000", c.errorCode)
            assertEquals("err!", c.message)
        }

    @Test
    fun `unknown code with NULL message is passed through BaseUseCase`() =
        runTest {
            every { resourceUtils.getString(any()) } returns "GENERIC"
            val cause = BusinessException("00000", ErrorType.WARNING, message = null)
            val out =
                useCase.handleError(Result.Error<BdDocumentValidateMappingModel>(cause = cause))

            val c = out.cause
            assertEquals("00000", c.errorCode)
            assertNull(c.message)
        }

    @Test
    fun `execute returns Error and preserves data when repo emits generic Error`() =
        runTest {
            val mapping = mockk<BdDocumentValidateMappingModel>()
            val genericErr = BusinessException("99999", ErrorType.ERROR, message = "err")
            coEvery { repo.bdDocumentValidate("", "123", any()) } returns
                flowOf(Result.Error(mapping, genericErr))

            useCase.execute("", "123").collectLatest { out ->
                assertTrue(out is Result.Error)
                assertEquals(mapping, (out as Result.Error).data)
                assertEquals(genericErr.errorCode, out.cause.errorCode)
            }
        }

    @Test
    fun `editingProductFlow exposes same instance from RequestDiscountLiveData`() {
        assertTrue(useCase.editingProductFlow === liveData.editingProduct)
    }
}
