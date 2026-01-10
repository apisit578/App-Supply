package th.co.cjexpress.store.sage.presentation.mapper.orderRecieve

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryItemModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel
import th.co.cjexpress.store.sage.presentation.mapper.orderReceive.OrderReceiveSummaryUIMapper
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryCompleteUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class OrderReceiveSummaryUIMapperTest : BaseUnitTest() {
    private lateinit var orderReceiveSummaryUIMapper: OrderReceiveSummaryUIMapper

    override fun setUp() {
        orderReceiveSummaryUIMapper = OrderReceiveSummaryUIMapper()
    }

    @Test
    fun `mapToOrderReceiveSummaryUIModel mapping test`() =
        runTest {
            // Arrange
            val mockOrderReceiveSummaryModel =
                OrderReceiveSummaryModel(
                    code = "SUCCESS",
                    data =
                        OrderReceiveSummaryDataModel(
                            createdDate = "1-1-1",
                            listOf(
                                OrderReceiveSummaryItemModel(
                                    actualQty = 0,
                                    qty = 5,
                                    isTote = true,
                                    barcode = "1234",
                                    diffQty = 0,
                                    productName = "",
                                    toteCode = "1234",
                                    unitName = "test",
                                    unitFactor = 0,
                                ),
                            ) as MutableList<OrderReceiveSummaryItemModel>,
                            sdNo = "1234",
                        ),
                    message = "Success",
                    ref = "",
                )
            val mock2OrderReceiveSummaryModel =
                OrderReceiveSummaryModel(
                    code = "SUCCESS",
                    data =
                        OrderReceiveSummaryDataModel(
                            items =
                                listOf(
                                    OrderReceiveSummaryItemModel(
                                        actualQty = 0,
                                        qty = 5,
                                        isTote = true,
                                        barcode = "1234",
                                        diffQty = 0,
                                        productName = "",
                                        toteCode = "1234",
                                        unitName = "test",
                                        unitFactor = 0,
                                    ),
                                ) as MutableList<OrderReceiveSummaryItemModel>,
                            createdDate = "1-1-1",
                            sdNo = "1234",
                        ),
                    message = "Success",
                    ref = "",
                )
            val mock3OrderReceiveSummaryModel =
                OrderReceiveSummaryModel(
                    code = "SUCCESS",
                    data =
                        OrderReceiveSummaryDataModel(
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
                            createdDate = "1-1-1",
                            sdNo = "1234",
                        ),
                    message = "Success",
                    ref = "",
                )
            var result = OrderReceiveSummaryUIModel()
            var result2 = OrderReceiveSummaryUIModel()
            var result3 = OrderReceiveSummaryUIModel()

            // Act
            val mappingResult =
                orderReceiveSummaryUIMapper.mapToOrderReceiveSummaryUIModel(
                    orderReceiveSummaryModel = mockOrderReceiveSummaryModel,
                )
            result = mappingResult

            val mappingResult2 =
                orderReceiveSummaryUIMapper.mapToOrderReceiveSummaryUIModel(
                    orderReceiveSummaryModel = mock2OrderReceiveSummaryModel,
                )
            result2 = mappingResult2

            val mappingResult3 =
                orderReceiveSummaryUIMapper.mapToOrderReceiveSummaryUIModel(
                    orderReceiveSummaryModel = mock3OrderReceiveSummaryModel,
                )
            result3 = mappingResult3

            // Assert
            // result
            Assert.assertEquals(mappingResult, result)

            // result2
            Assert.assertEquals(true, result2.items.first().isMissing)
            Assert.assertEquals(
                OrderReceiveSummaryUIMapper.Companion.CONST_PRODUCT_NAME_TOTE,
                result2.items.first().productName,
            )
            mock2OrderReceiveSummaryModel.data.items.first().also { item ->
                Assert.assertEquals(
                    "${OrderReceiveSummaryUIMapper.Companion.CONST_STATUS_MISSING} ${item.qty - item.actualQty}",
                    result2.items.first().chipText,
                )
                Assert.assertEquals(
                    OrderReceiveSummaryUIMapper.Companion.CONST_DEFAULT_PRODUCT_UNIT_NAME,
                    result2.items.first().unitName,
                )
            }

            // result3
            Assert.assertEquals(false, result3.items.first().isMissing)
            mock3OrderReceiveSummaryModel.data.items.first().also { item ->
                Assert.assertEquals(
                    "${OrderReceiveSummaryUIMapper.Companion.CONST_STATUS_OVER} ${item.actualQty - item.qty}",
                    result3.items.first().chipText,
                )
                Assert.assertEquals(item.productName, result3.items.first().productName)
                Assert.assertEquals(
                    item.unitName,
                    result3.items.first().unitName,
                )
            }
        }

    @Test
    fun `mapToOrderReceiveSummaryCompleteUIModel mapping test`() =
        runTest {
            // Arrange
            val sdNo = "test"
            var result = OrderReceiveSummaryCompleteUIModel()

            // Act
            val mappingResult =
                orderReceiveSummaryUIMapper.mapToOrderReceiveSummaryCompleteUIModel(
                    sdNo = sdNo,
                    isComplete = true,
                )
            result = mappingResult

            // Assert
            Assert.assertEquals(sdNo, result.sdNo)
            Assert.assertEquals(true, result.isComplete)
        }
}
