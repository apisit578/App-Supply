package th.co.cjexpress.store.sage.domain.usecase.masterMapping

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.MasterMapping
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.masterMapping.BdSearchTypeItemModel
import th.co.cjexpress.store.sage.domain.model.masterMapping.BdSearchTypeMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetBdSearchTypeUseCaseTest {
    private lateinit var useCase: GetBdSearchTypeUseCase
    private lateinit var sageApiRepository: SageApiRepository
    private lateinit var resourceUtils: ResourceUtils

    @Before
    fun setUp() {
        sageApiRepository = mockk()
        resourceUtils = mockk()
        useCase =
            GetBdSearchTypeUseCase(
                sageApiRepository = sageApiRepository,
                resourceUtils = resourceUtils,
            )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `execute should return Success result when repository returns success`() =
        runTest {
            // Arrange
            val expectedData =
                BdSearchTypeMappingModel(
                    code = "Success",
                    message = "Success",
                    ref = "123456",
                    timestamp = "1111",
                    types =
                        listOf(
                            BdSearchTypeItemModel("DISC01", "Discount1", "ส่วนลด1"),
                            BdSearchTypeItemModel("DISC02", "Discount2", "ส่วนลด2"),
                        ),
                )
            coEvery {
                sageApiRepository.bdDocumentSearchType(MasterMapping.CAMPAIGN_DISCOUNT_TYPE)
            } returns flowOf(Result.Success(expectedData))

            // Act
            val actualFlow = useCase.execute(MasterMapping.CAMPAIGN_DISCOUNT_TYPE)

            val actual = actualFlow.first()
            assertTrue(actual is Result.Success)
            assertEquals(expectedData, (actual as Result.Success).data)
        }

    @Test
    fun `execute should return Error result when repository returns error`() =
        runTest {
            val expectedData =
                BdSearchTypeMappingModel(
                    code = "Success",
                    message = "Success",
                    ref = "123456",
                    timestamp = "1111",
                    types =
                        listOf(
                            BdSearchTypeItemModel("DISC01", "Discount1", "ส่วนลด1"),
                            BdSearchTypeItemModel("DISC02", "Discount2", "ส่วนลด2"),
                        ),
                )
            coEvery {
                sageApiRepository.bdDocumentSearchType(MasterMapping.CAMPAIGN_DISCOUNT_TYPE)
            } returns flowOf(Result.Success(expectedData))

            // Act & Assert
            useCase.execute(MasterMapping.CAMPAIGN_DISCOUNT_TYPE).test {
                val emitted = awaitItem()
                assertTrue(emitted is Result.Success)
                assertEquals(expectedData, (emitted as Result.Success).data)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
