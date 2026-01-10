package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import th.co.cjexpress.store.sage.domain.livedata.orderReceive.OrderReceiveLiveData
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

class ShipmentDocumentUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var orderReceiveLiveData: OrderReceiveLiveData

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var shipmentDocumentUseCase: ShipmentDocumentUseCase

    override fun setUp() {
        super.setUp()
        shipmentDocumentUseCase =
            ShipmentDocumentUseCase(orderReceiveLiveData)
    }

    @Test
    fun test_resetShipmentBatchModel() =
        runTest {
            // when
            shipmentDocumentUseCase.resetShipmentBatchModel()

            // then
            verify { orderReceiveLiveData.resetShipmentBatchModel() }
        }

    @Test
    fun test_resetShipmentSummaryUIModel() =
        runTest {
            // when
            shipmentDocumentUseCase.resetShipmentSummaryUIModel()

            // then
            verify { orderReceiveLiveData.resetShipmentSummaryUIModel() }
        }

    @Test
    fun test_setShipmentBatchModel() =
        runTest {
            // given
            val dataMock = mockk<ShipmentDocumentUIModel>(relaxed = true)

            // when
            shipmentDocumentUseCase.setShipmentBatchModel(dataMock)

            // then
            verify { orderReceiveLiveData.setShipmentBatchModel(dataMock) }
        }

    @Test
    fun test_setShipmentSummaryUIModel() =
        runTest {
            // given
            val dataMock = mockk<ShipmentDocumentUIModel>(relaxed = true)

            // when
            shipmentDocumentUseCase.setShipmentSummaryUIModel(dataMock)

            // then
            verify { orderReceiveLiveData.setShipmentSummaryUIModel(dataMock) }
        }

    @Test
    fun test_updateShipmentBatch() =
        runTest {
            // given
            val dataMock = "111111"
            val isIncrease = true

            // when
            shipmentDocumentUseCase.updateShipmentBatch(dataMock, isIncrease)

            // then
            verify { orderReceiveLiveData.updateShipmentBatch(dataMock, isIncrease) }
        }

    @Test
    fun test_addShipmentBatchItem() =
        runTest {
            // given
            val dataMock = mockk<ShipmentDataItemUIModel>(relaxed = true)

            // when
            shipmentDocumentUseCase.addShipmentBatchItem(dataMock)

            // then
            verify { orderReceiveLiveData.addShipmentBatchItem(dataMock) }
        }

    @Test
    fun test_searchShipmentData() =
        runTest {
            // when
            shipmentDocumentUseCase.searchShipmentData("123456")

            // then
            verify { orderReceiveLiveData.searchShipmentData("123456") }
        }

    @Test
    fun test_searchShipmentBatchData() =
        runTest {
            // when
            shipmentDocumentUseCase.searchShipmentBatchData("123456")

            // then
            verify { orderReceiveLiveData.searchShipmentBatchData("123456") }
        }

    @Test
    fun test_get_shipmentSummaryLiveData() =
        runTest {
            // when
            shipmentDocumentUseCase.shipmentSummaryLiveData

            // then
            verify { orderReceiveLiveData.shipmentSummaryUIModel }
        }

    @Test
    fun test_get_shipmentBatchLiveData() =
        runTest {
            // when
            shipmentDocumentUseCase.shipmentBatchLiveData

            // then
            verify { orderReceiveLiveData.shipmentBatchUIModel }
        }

    @Test
    fun test_get_getShipmentLDNo() =
        runTest {
            // when
            shipmentDocumentUseCase.getShipmentLDNo()

            // then
            verify { orderReceiveLiveData.getShipmentLDNo() }
        }

    @Test
    fun test_removeShipmentBatchModel() =
        runTest {
            // when
            shipmentDocumentUseCase.removeShipmentBatchModel()

            // then
            verify { orderReceiveLiveData.removeShipmentBatchModel() }
        }

    @Test
    fun test_removeShipmentBatch() =
        runTest {
            // give
            val dataMock = mockk<ShipmentDataItemUIModel>(relaxed = true)

            // when
            shipmentDocumentUseCase.removeShipmentBatchItem(dataMock)

            // then
            verify { orderReceiveLiveData.removeShipmentBatchItem(dataMock) }
        }

    @Test
    fun test_updateShipmentSummary() =
        runTest {
            // given
            val dataMock = mockk<ShipmentDocumentUIModel>(relaxed = true)

            // when
            shipmentDocumentUseCase.updateShipmentSummary(dataMock)

            // then
            verify { orderReceiveLiveData.updateShipmentSummary(dataMock) }
        }

    @Test
    fun test_setShipmentDocumentByBarcode() =
        runTest {
            // given
            val barcode = "123456"
            val dataMock = mockk<ShipmentDocumentUIModel>(relaxed = true)

            // when
            shipmentDocumentUseCase.setShipmentDocumentByBarcode(barcode)

            // then
            verify { orderReceiveLiveData.setIncreaseShipmentDocumentByBarcode(barcode) }
        }

    @Test
    fun test_getSummaryBatchQty() =
        runTest {
            // when
            shipmentDocumentUseCase.getSummaryBatchQty()

            // then
            verify { orderReceiveLiveData.getSummaryBatchQty() }
        }

    @Test
    fun test_setShipmentDocumentItemDetail() =
        runTest {
            // given
            val item = mockk<ShipmentDataItemUIModel>(relaxed = true)

            // when
            shipmentDocumentUseCase.setShipmentDocumentItemDetail(item)

            // then
            verify { orderReceiveLiveData.setShipmentDocumentItemDetail(item) }
        }

    @Test
    fun test_removeShipmentDocumentItemDetail() =
        runTest {
            // when
            shipmentDocumentUseCase.removeShipmentDocumentItemDetail()

            // then
            verify { orderReceiveLiveData.removeShipmentDocumentItemDetail() }
        }

    @Test
    fun test_getShipmentDocumentItemDetail() =
        runTest {
            // given
            val expected = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { orderReceiveLiveData.getShipmentDocumentItemDetail() } returns expected

            // when
            val result = shipmentDocumentUseCase.getShipmentDocumentItemDetail()

            // then
            verify { orderReceiveLiveData.getShipmentDocumentItemDetail() }
            assertEquals(expected, result)
        }
}
