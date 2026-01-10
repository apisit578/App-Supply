package th.co.cjexpress.store.sage.data.mapper.orderReceive

import com.google.gson.Gson
import io.mockk.unmockkAll
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.isA
import org.junit.After
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.mapper.summary.OrderReceiveSummaryMapper
import th.co.cjexpress.store.sage.data.model.orderReceive.OrderReceiveSummaryResponse
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class OrderReceiveSummaryMapperTest : BaseUnitTest() {
    private lateinit var orderReceiveSummaryMapper: OrderReceiveSummaryMapper

    @Before
    override fun setUp() {
        orderReceiveSummaryMapper = OrderReceiveSummaryMapper()
    }

    @After
    override fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test OrderReceiveSummaryMapper`() {
        // Arrange
        val gson = Gson()
        val jsonString = getJsonFromResource(fileName = "test/order_receive/mock_order_receive_response.json")
        val mockOrderReceiveSummaryResponse: OrderReceiveSummaryResponse =
            gson.fromJson(jsonString.trimIndent(), OrderReceiveSummaryResponse::class.java)

        // Act
        val result =
            orderReceiveSummaryMapper.map(orderReceiveSummaryResponse = mockOrderReceiveSummaryResponse)

        // Assert
        MatcherAssert.assertThat(result, isA(OrderReceiveSummaryModel::class.java))
    }

    private fun getJsonFromResource(fileName: String): String {
        val inputStream =
            javaClass.classLoader?.getResourceAsStream(fileName)
                ?: throw IllegalArgumentException("File $fileName not found in resources.")
        return inputStream.bufferedReader().use { it.readText() }
    }
}
