package th.co.cjexpress.store.sage.data.mapper.orderReceive

import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.isA
import org.junit.After
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.orderReceive.DocumentSDResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.DocumentSDResponseData
import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import java.sql.Date

class DocumentSDMapperTest : BaseUnitTest() {
    lateinit var documentSDMapper: DocumentSDMapper

    @Before
    override fun setUp() {
        documentSDMapper = DocumentSDMapper()
    }

    @After
    override fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_document_sd_mapper() {
        // given
        val respMock = mockk<DocumentSDResponse>(relaxed = true)
        // when
        val result = documentSDMapper.map(respMock)
        // then
        MatcherAssert.assertThat(result, isA(DocumentSDModel::class.java))
    }

    @Test
    fun test_document_sd_mapper_with_empty_data_list() {
        // given
        val response =
            DocumentSDResponse(
                ref = "123",
                code = "200",
                message = "No Content",
                timestamp = "",
                data = emptyList(),
            )
        // when
        val result = documentSDMapper.map(response)
        // then
        MatcherAssert.assertThat(result, isA(DocumentSDModel::class.java))
        assertEquals(response.code, result.code)
        assertEquals(response.message, result.message)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun test_document_sd_mapper_with_null_data() {
        // given
        val response =
            DocumentSDResponse(
                ref = "123",
                code = "200",
                message = "Data is null",
                timestamp = "",
                data = null,
            )
        // when
        val result = documentSDMapper.map(response)
        // then
        MatcherAssert.assertThat(result, isA(DocumentSDModel::class.java))
        assertEquals(response.code, result.code)
        assertEquals(response.message, result.message)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun test_document_sd_mapper_with_data() {
        // given
        val responseData =
            DocumentSDResponseData(
                createdDate = Date(20250601),
                sdNo = "SD001",
                ldNo = "LD001",
            )
        val response =
            DocumentSDResponse(
                ref = "123",
                code = "200",
                message = "Success",
                timestamp = "",
                data = listOf(responseData),
            )
        // when
        val result = documentSDMapper.map(response)
        // then
        MatcherAssert.assertThat(result, isA(DocumentSDModel::class.java))
        assertEquals(response.code, result.code)
        assertEquals(response.message, result.message)
        assertEquals(1, result.data.size)
        assertEquals(responseData.sdNo, result.data[0].sdNo)
        assertEquals(responseData.ldNo, result.data[0].ldNo)
        assertEquals(responseData.createdDate, result.data[0].createdDate)
    }
}
