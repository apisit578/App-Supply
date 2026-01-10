package th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc

import io.mockk.MockKAnnotations
import io.mockk.coEvery
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
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetBdDocumentNoDetailUseCaseTest {
    @RelaxedMockK
    lateinit var repo: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var useCase: GetBdDocumentNoDetailUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetBdDocumentNoDetailUseCase(repo, resourceUtils)

        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
        every { resourceUtils.getString(R.string.text_changed_status_document) } returns "Changed"
        every { resourceUtils.getString(R.string.text_not_found_document) } returns "Not-Found"
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `execute success returns Result_Success`() =
        runTest {
            val mapping = mockk<BdDocumentNoDetailMappingModel>()
            coEvery { repo.bdDocumentNoDetail("1234") } returns flowOf(Result.Success(mapping))

            useCase.execute("1234").collectLatest { out ->
                assertTrue(out is Result.Success)
                assertEquals(mapping, (out as Result.Success).data)
            }
        }

    @Test
    fun `INVALID_DOC maps to BAD_REQUEST error`() =
        runTest {
            val raw =
                BusinessException(
                    errorCode = ErrorCodes.INVALID_DOC,
                    type = ErrorType.ERROR,
                    title = "Raw-Title",
                    message = "Raw-Message",
                )
            coEvery { repo.bdDocumentNoDetail("1234") } returns flowOf(Result.Error(cause = raw))

            useCase.execute("1234").collectLatest { out ->
                val c = (out as Result.Error).cause
                assertEquals(ErrorCodes.BAD_REQUEST, c.errorCode)
                assertEquals(ErrorType.ERROR, c.type)
                assertEquals("Raw-Title", c.title)
                assertEquals("Raw-Message", c.message)
            }
        }

    @Test
    fun `DOC_NOT_FOUND maps to NOT_FOUND error`() =
        runTest {
            val raw =
                BusinessException(
                    errorCode = ErrorCodes.DOC_NOT_FOUND,
                    type = ErrorType.ERROR,
                    title = "Doc-Title",
                    message = "Doc-Message",
                )
            coEvery { repo.bdDocumentNoDetail("1234") } returns flowOf(Result.Error(cause = raw))

            useCase.execute("1234").collectLatest { out ->
                val c = (out as Result.Error).cause
                assertEquals(ErrorCodes.NOT_FOUND, c.errorCode)
                assertEquals(ErrorType.WARNING, c.type)
                assertEquals("Unable", c.title)
                assertEquals("Not-Found", c.message)
            }
        }

    @Test
    fun `INVALID_STATUS maps to NOT_FOUND warning with localized strings`() =
        runTest {
            val raw =
                BusinessException(
                    errorCode = ErrorCodes.INVALID_STATUS,
                    type = ErrorType.ERROR,
                    message = "ignored",
                )
            coEvery { repo.bdDocumentNoDetail("1234") } returns flowOf(Result.Error(cause = raw))

            useCase.execute("1234").collectLatest { out ->
                val c = (out as Result.Error).cause
                assertEquals(ErrorCodes.NOT_FOUND, c.errorCode)
                assertEquals(ErrorType.WARNING, c.type)
                assertEquals("Unable", c.title)
                assertEquals("Changed", c.message)
            }
        }

    @Test
    fun `unknown code with message falls to default handler`() =
        runTest {
            every { resourceUtils.getString(any()) } returns "GENERIC"
            val raw =
                BusinessException("00000", ErrorType.ERROR, title = "GENERIC", message = "boom!")
            val out = useCase.handleError(Result.Error<BdDocumentNoDetailMappingModel>(cause = raw))

            val c = out.cause
            assertEquals("00000", c.errorCode)
            assertEquals("boom!", c.message)
            assertEquals("GENERIC", c.title)
        }

    @Test
    fun `unknown code with NULL message falls to default handler`() =
        runTest {
            every { resourceUtils.getString(any()) } returns "GENERIC"
            val raw =
                BusinessException("11111", ErrorType.WARNING, title = "GENERIC", message = null)
            val out = useCase.handleError(Result.Error<BdDocumentNoDetailMappingModel>(cause = raw))

            val c = out.cause
            assertEquals("11111", c.errorCode)
            assertNull(c.message)
            assertEquals("GENERIC", c.title)
        }

    @Test
    fun `execute returns Error and preserves data when repo emits generic Error`() =
        runTest {
            val mapping = mockk<BdDocumentNoDetailMappingModel>()
            val err = BusinessException("99999", ErrorType.ERROR, message = "generic")
            coEvery { repo.bdDocumentNoDetail("1234") } returns flowOf(Result.Error(mapping, err))

            useCase.execute("1234").collectLatest { out ->
                assertTrue(out is Result.Error)
                assertEquals(mapping, (out as Result.Error).data)
                assertEquals(err, out.cause)
            }
        }
}
