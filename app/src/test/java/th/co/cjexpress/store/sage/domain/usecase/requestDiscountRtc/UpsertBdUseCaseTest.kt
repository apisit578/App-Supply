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
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class BdDocumentSubmitUseCaseTest {
    @RelaxedMockK
    lateinit var repo: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var useCase: BdDocumentSubmitUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = BdDocumentSubmitUseCase(repo, resourceUtils)

        every { resourceUtils.getString(R.string.text_save_failed) } returns "Save-Failed"
        every { resourceUtils.getString(R.string.text_can_not_save_document) } returns "Cannot-Save"
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `execute success returns Result_Success`() =
        runTest {
            val mapping = mockk<BdDocumentSubmitMappingModel>()
            coEvery { repo.bdDocumentSubmit("1234", "NORMAL", emptyList()) } returns
                flowOf(Result.Success(mapping))

            useCase.execute("1234", "NORMAL", emptyList()).collectLatest { out ->
                assertTrue(out is Result.Success)
                assertEquals(mapping, (out as Result.Success).data)
            }
        }

    @Test
    fun `INVALID_STATUS maps to NOT_FOUND error with localized strings`() =
        runTest {
            val rawCause =
                BusinessException(
                    errorCode = ErrorCodes.INVALID_STATUS,
                    type = ErrorType.ERROR,
                    title = "Ignored",
                    message = "Ignored",
                )
            coEvery { repo.bdDocumentSubmit("1234", "NORMAL", emptyList()) } returns
                flowOf(Result.Error(cause = rawCause))

            useCase.execute("1234", "NORMAL", emptyList()).collectLatest { out ->
                val c = (out as Result.Error).cause
                assertEquals(ErrorCodes.NOT_FOUND, c.errorCode)
                assertEquals(ErrorType.ERROR, c.type)
                assertEquals("Save-Failed", c.title)
                assertEquals("Cannot-Save", c.message)
            }
        }

    @Test
    fun `BARCODE_CANT_SUGGESTION maps to NOT_FOUND warning with localized strings`() =
        runTest {
            val rawCause =
                BusinessException(
                    errorCode = ErrorCodes.BARCODE_CANT_SUGGESTION,
                    type = ErrorType.ERROR,
                    title = "should be ignored",
                    message = "should be ignored",
                )
            coEvery { repo.bdDocumentSubmit("1234", "NORMAL", emptyList()) } returns
                flowOf(Result.Error(cause = rawCause))

            useCase.execute("1234", "NORMAL", emptyList()).collectLatest { out ->
                val c = (out as Result.Error).cause
                assertEquals(ErrorCodes.NOT_FOUND, c.errorCode)
                assertEquals(ErrorType.ERROR, c.type)
                assertEquals("Save-Failed", c.title)
                assertEquals("Cannot-Save", c.message)
            }
        }

    @Test
    fun `unknown code with message falls to default handler`() =
        runTest {
            every { resourceUtils.getString(any()) } returns "GENERIC"
            val cause = BusinessException("00000", ErrorType.ERROR, message = "err!")
            val out = useCase.handleError(Result.Error<BdDocumentSubmitMappingModel>(cause = cause))

            val c = out.cause
            assertEquals("00000", c.errorCode)
            assertEquals("err!", c.message)
        }

    @Test
    fun `unknown code with NULL message falls to default handler`() =
        runTest {
            every { resourceUtils.getString(any()) } returns "GENERIC"
            val cause = BusinessException("00000", ErrorType.WARNING, message = null)
            val out = useCase.handleError(Result.Error<BdDocumentSubmitMappingModel>(cause = cause))

            val c = out.cause
            assertEquals("00000", c.errorCode)
            assertNull(c.message)
        }

    @Test
    fun `execute returns Error and preserves data when repo emits generic Error`() =
        runTest {
            val mapping = mockk<BdDocumentSubmitMappingModel>()
            val err = BusinessException("99999", ErrorType.ERROR, message = "generic")
            coEvery { repo.bdDocumentSubmit("1234", "NORMAL", emptyList()) } returns
                flowOf(Result.Error(mapping, err))

            useCase.execute("1234", "NORMAL", emptyList()).collectLatest { out ->
                assertTrue(out is Result.Error)
                assertEquals(mapping, (out as Result.Error).data)
                assertEquals(err, out.cause)
            }
        }
}
