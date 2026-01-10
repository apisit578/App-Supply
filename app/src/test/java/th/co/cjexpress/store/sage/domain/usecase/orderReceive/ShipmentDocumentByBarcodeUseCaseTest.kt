package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentItemRequest
import th.co.cjexpress.store.sage.domain.livedata.orderReceive.OrderReceiveLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

class ShipmentDocumentByBarcodeUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var shipmentDocumentByBarcodeUseCase: ShipmentDocumentByBarcodeUseCase

    @RelaxedMockK
    lateinit var orderReceiveLiveData: OrderReceiveLiveData

    override fun setUp() {
        super.setUp()
        shipmentDocumentByBarcodeUseCase =
            ShipmentDocumentByBarcodeUseCase(repository, orderReceiveLiveData, resourceUtils)
    }

    @Test
    fun test_shipment_document_by_barcode_success_expect_result_success() =
        runTest {
            // given
            val dataMock = mockk<ShipmentDocumentByBarcodeModel>(relaxed = true)
            val dataListRequestMock = mockk<ShipmentItemRequest>(relaxed = true)

            coEvery {
                repository.shipmentDocumentByBarcode(
                    any(),
                    any(),
                )
            } returns flowOf(Result.Success(data = dataMock))
            // when
            val flow =
                shipmentDocumentByBarcodeUseCase.execute(
                    listOf(dataListRequestMock),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = dataMock), result)
            }
        }

    @Test
    fun test_shipment_document_by_barcode_expect_result_error() =
        runTest {
            // given
            val dataListRequestMock = mockk<ShipmentItemRequest>(relaxed = true)

            coEvery {
                repository.shipmentDocumentByBarcode(
                    any(),
                    any(),
                )
            } returns
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
            // when
            val flow =
                shipmentDocumentByBarcodeUseCase.execute(
                    listOf(dataListRequestMock),
                )
            // then
            flow.collectLatest { result ->
                TestCase.assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipment_document_by_barcode_failed_when_error_code_ERR_NOT_FOUND_expect_result_error() =
        runTest {
            // given
            val dataListRequestMock = mockk<ShipmentItemRequest>(relaxed = true)

            coEvery { repository.shipmentDocumentByBarcode(any(), any()) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "ERR_NOT_FOUND",
                                type = ErrorType.ERROR,
                                title = "Barcode List",
                                message = "Please try again",
                            ),
                    ),
                )

            // when
            val flow =
                shipmentDocumentByBarcodeUseCase.execute(
                    listOf(dataListRequestMock),
                )

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipment_document_by_barcode_with_empty_data_expect_result_error() =
        runTest {
            // when
            val flow =
                shipmentDocumentByBarcodeUseCase.execute(
                    emptyList(),
                )

            // then
            flow.collectLatest { result ->
                TestCase.assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals(ErrorType.WARNING, errorResult.cause.type)
                assertEquals(
                    resourceUtils.getString(R.string.text_title_error_not_found_order),
                    errorResult.cause.title,
                )
                assertEquals(
                    resourceUtils.getString(R.string.text_description_error_not_found_order),
                    errorResult.cause.message,
                )
            }
        }

    @Test
    fun test_shipment_document_by_barcode_failed_when_error_code_in_unexpectedError_expect_result_error_BAD_REQUEST() =
        runTest {
            // given
            val dataListRequestMock = mockk<ShipmentItemRequest>(relaxed = true)
            val originalTitle = "Some title"
            val originalMessage = "Some message"

            coEvery { repository.shipmentDocumentByBarcode(any(), any()) } returns
                    flowOf(
                        Result.Error(
                            cause = BusinessException(
                                errorCode = ErrorCodes.INVALID_DOC_TYPE,
                                type = ErrorType.ERROR,
                                title = originalTitle,
                                message = originalMessage,
                            ),
                        ),
                    )

            // when
            val flow = shipmentDocumentByBarcodeUseCase.execute(listOf(dataListRequestMock))

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertEquals(ErrorCodes.BAD_REQUEST, error.errorCode)
                assertEquals(ErrorType.ERROR, error.type)
                assertEquals(originalTitle, error.title)
                assertEquals(originalMessage, error.message)
            }
        }

    @Test
    fun test_shipment_document_by_barcode_failed_when_error_code_DOC_ALREADY_APPROVED_expect_result_error() =
        runTest {
            // given
            val dataListRequestMock = mockk<ShipmentItemRequest>(relaxed = true)
            val sdNo = "SD123456-123456"
            every { orderReceiveLiveData.getSdNo() } returns sdNo
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "ไม่สามารถดำเนินการต่อได้"
            every { resourceUtils.getString(R.string.text_document_status_changed, sdNo) } returns "เนื่องจากเลขที่เอกสาร: $sdNo ถูกเปลี่ยนสถานะแล้ว"

            coEvery { repository.shipmentDocumentByBarcode(any(), any()) } returns
                    flowOf(
                        Result.Error(
                            cause = BusinessException(
                                errorCode = ErrorCodes.DOC_ALREADY_APPROVED,
                                type = ErrorType.ERROR,
                                message = null
                            )
                        )
                    )

            // when
            val flow = shipmentDocumentByBarcodeUseCase.execute(listOf(dataListRequestMock))

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertEquals(ErrorCodes.DOC_ALREADY_APPROVED, error.errorCode)
                assertEquals(ErrorType.WARNING, error.type)
                assertEquals("ไม่สามารถดำเนินการต่อได้", error.title)
                assertEquals("เนื่องจากเลขที่เอกสาร: $sdNo ถูกเปลี่ยนสถานะแล้ว", error.message)
            }
        }

    @Test
    fun test_shipment_document_by_barcode_failed_when_error_code_NOT_FOUND_expect_result_error() =
        runTest {
            // given
            val dataListRequestMock = mockk<ShipmentItemRequest>(relaxed = true)
            val sdNo = "SD123456-123456"
            every { orderReceiveLiveData.getSdNo() } returns sdNo
            every { resourceUtils.getString(R.string.text_data_not_found) } returns "ไม่พบข้อมูล"
            every { resourceUtils.getString(R.string.text_format_shipment_document_no, sdNo) } returns "เลขที่เอกสาร: $sdNo"

            coEvery { repository.shipmentDocumentByBarcode(any(), any()) } returns
                    flowOf(
                        Result.Error(
                            cause = BusinessException(
                                errorCode = ErrorCodes.NOT_FOUND,
                                type = ErrorType.ERROR,
                                message = null
                            )
                        )
                    )

            // when
            val flow = shipmentDocumentByBarcodeUseCase.execute(listOf(dataListRequestMock))

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertEquals(ErrorCodes.NOT_FOUND, error.errorCode)
                assertEquals(ErrorType.WARNING, error.type)
                assertEquals("ไม่พบข้อมูล", error.title)
                assertEquals("เลขที่เอกสาร: $sdNo", error.message)
            }
        }

    @Test
    fun test_shipment_document_by_barcode_failed_when_error_code_INVALID_TOTE_expect_result_error() =
        runTest {
            // given
            val dataListRequestMock = mockk<ShipmentItemRequest>(relaxed = true)
            val sdNo = "SD123456-123456"
            every { orderReceiveLiveData.getSdNo() } returns sdNo
            every { resourceUtils.getString(R.string.text_data_not_found) } returns "ไม่พบข้อมูล"
            every { resourceUtils.getString(R.string.text_title_tote_code, sdNo) } returns "เลข Tote: $sdNo"

            coEvery { repository.shipmentDocumentByBarcode(any(), any()) } returns
                    flowOf(
                        Result.Error(
                            cause = BusinessException(
                                errorCode = ErrorCodes.INVALID_TOTE,
                                type = ErrorType.ERROR,
                                message = null
                            )
                        )
                    )

            // when
            val flow = shipmentDocumentByBarcodeUseCase.execute(listOf(dataListRequestMock))

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertEquals(ErrorCodes.INVALID_TOTE, error.errorCode)
                assertEquals(ErrorType.WARNING, error.type)
                assertEquals("ไม่พบข้อมูล", error.title)
                assertEquals("เลข Tote: $sdNo", error.message)
            }
        }
}
