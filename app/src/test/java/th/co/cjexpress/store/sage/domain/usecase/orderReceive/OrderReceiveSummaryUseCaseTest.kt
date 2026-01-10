package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryItemModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class OrderReceiveSummaryUseCaseTest {
    @RelaxedMockK
    lateinit var sageApiRepository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var orderReceiveSummaryUseCase: OrderReceiveSummaryUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        orderReceiveSummaryUseCase = OrderReceiveSummaryUseCase(sageApiRepository, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `execute getSummaryBySd when success`() =
        runTest {
            // Arrange
            val sdNumber = "SD2505M04C-000038"
            val expectedData =
                OrderReceiveSummaryModel(
                    code = "SUCCESS",
                    data =
                        OrderReceiveSummaryDataModel(
                            createdDate = "",
                            items =
                                listOf(
                                    OrderReceiveSummaryItemModel(
                                        actualQty = 0,
                                        barcode = "SD2505M04C-000038",
                                        diffQty = 0,
                                        isTote = false,
                                        productName = "",
                                        qty = 0,
                                        toteCode = "",
                                        unitName = "",
                                        unitFactor = 0,
                                    ),
                                ) as MutableList<OrderReceiveSummaryItemModel>,
                            sdNo = sdNumber,
                        ),
                    message = "Success",
                    ref = "",
                )
            val expectedResult = flowOf(Result.Success(expectedData))
            coEvery { sageApiRepository.getShipmentDocumentSummaryBySd(sdNo = sdNumber) } returns expectedResult

            // Act
            val flow = orderReceiveSummaryUseCase.execute(sdNo = sdNumber)

            // Assert
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = expectedData), result)
            }
        }

    @Test
    fun `execute getSummaryBySd when error`() =
        runTest {
            // Arrange
            val sdNumber = "SD2505M04C-000038"

            coEvery { sageApiRepository.getShipmentDocumentSummaryBySd(sdNo = sdNumber) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )
            // Act
            val flow = orderReceiveSummaryUseCase.execute(sdNo = sdNumber)

            // Assert
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun `execute getSummaryBySd when error NOT_FOUND`() =
        runTest {
            // Arrange
            val sdNumber = "SD2505M04C-000038"

            coEvery { sageApiRepository.getShipmentDocumentSummaryBySd(sdNo = sdNumber) } answers {
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.NOT_FOUND,
                                type = ErrorType.WARNING,
                                message = "NOT_FOUND",
                            ),
                    ),
                )
            }

            // Act
            val flow = orderReceiveSummaryUseCase.execute(sdNo = sdNumber)

            // Assert
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                assertEquals(ErrorCodes.NOT_FOUND, (result as Result.Error).cause?.errorCode)
                assertEquals(ErrorType.WARNING, result.cause?.type)
                assertEquals("NOT_FOUND", result.cause?.message)
            }
        }
}
