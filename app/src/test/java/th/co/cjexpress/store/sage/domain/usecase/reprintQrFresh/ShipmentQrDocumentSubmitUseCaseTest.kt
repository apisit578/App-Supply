package th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh

import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.mapper.DTOModelMapper
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

@OptIn(ExperimentalCoroutinesApi::class)
class ShipmentQrDocumentSubmitUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var sageApiRepository: SageApiRepository

    @RelaxedMockK
    lateinit var dtoModelMapper: DTOModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var useCase: ShipmentQrDocumentSubmitUseCase

    @Before
    override fun setUp() {
        super.setUp()
        every { dtoModelMapper.qrDocumentSubmitDTOMapper.toDTO(any()) } returns mockk(relaxed = true)

        useCase =
            ShipmentQrDocumentSubmitUseCase(
                sageApiRepository = sageApiRepository,
                dtoModelMapper = dtoModelMapper,
                resourceUtils = resourceUtils,
            )
    }

    @Test
    fun `execute returns Success when repository returns Success`() =
        runTest {
            // given
            val domain = mockk<QrDocumentSubmitModel>(relaxed = true)
            val mapping = mockk<QrDocumentSubmitMappingModel>(relaxed = true)

            coEvery { sageApiRepository.qrDocumentSubmit(any()) } returns
                flowOf(Result.Success(mapping))

            // when
            val result = useCase.execute(domain).first()

            // then
            assertTrue(result is Result.Success)
            assertEquals(mapping, (result as Result.Success).data)

            verify(exactly = 1) { dtoModelMapper.qrDocumentSubmitDTOMapper.toDTO(domain) }
        }

    @Test
    fun `handleError maps unexpectedError codes to BAD_REQUEST and drops data`() =
        runTest {
            // given
            val domain = mockk<QrDocumentSubmitModel>(relaxed = true)
            val originalData = mockk<QrDocumentSubmitMappingModel>(relaxed = true)
            val title = "Title Test"
            val message = "Message"

            val codes =
                listOf(
                    ErrorCodes.INVALID_BODY,
                    ErrorCodes.INVALID_EXPIREDATE,
                    ErrorCodes.STORM_SERVICE_ERROR,
                )

            for (code in codes) {
                coEvery { sageApiRepository.qrDocumentSubmit(any()) } returns
                    flowOf(
                        Result.Error(
                            data = originalData,
                            cause =
                                BusinessException(
                                    errorCode = code,
                                    type = ErrorType.ERROR,
                                    title = title,
                                    message = message,
                                ),
                        ),
                    )

                // when
                val result = useCase.execute(domain).first()

                // then
                assertTrue(result is Result.Error)
                val cause = (result as Result.Error).cause
                assertEquals(ErrorCodes.BAD_REQUEST, cause.errorCode)
                assertEquals(ErrorType.ERROR, cause.type)
                assertEquals(title, cause.title)
                assertEquals(message, cause.message)

                assertNull(result.data)
            }
        }

    @Test
    fun `error code NOT in unexpectedError falls back to super and preserves data`() =
        runTest {
            // given
            val domain = mockk<QrDocumentSubmitModel>(relaxed = true)
            val originalData = mockk<QrDocumentSubmitMappingModel>(relaxed = true)
            val original =
                BusinessException(
                    errorCode = "X12345",
                    type = ErrorType.WARNING,
                    title = "Orig-Title",
                    message = "Orig-Message",
                )

            coEvery { sageApiRepository.qrDocumentSubmit(any()) } returns
                flowOf(Result.Error(data = originalData, cause = original))

            // when
            val result = useCase.execute(domain).first()

            // then
            assertTrue(result is Result.Error)
            val out = (result as Result.Error)
            assertEquals("X12345", out.cause.errorCode)
            assertEquals(ErrorType.WARNING, out.cause.type)
            assertEquals("Orig-Title", out.cause.title)
            assertEquals("Orig-Message", out.cause.message)
            assertEquals(originalData, out.data)
        }
}
