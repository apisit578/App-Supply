package th.co.cjexpress.store.sage.domain.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc.RequestDiscountLiveData
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentNoDetailUIModel
import th.co.cjexpress.store.sage.utils.MainDispatcherRule
import th.co.cjexpress.store.sage.utils.getOrAwaitValue
import java.util.Date

@ExperimentalCoroutinesApi
class RequestDiscountLiveDataTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var requestDiscountLiveData: RequestDiscountLiveData

    @Before
    fun setUp() {
        requestDiscountLiveData = RequestDiscountLiveData()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_resetDocsData() {
        requestDiscountLiveData.resetDocsData()
        assertNotNull(requestDiscountLiveData.docsData.getOrAwaitValue())
        assertNull(requestDiscountLiveData.editingProduct.getOrAwaitValue())
        assertFalse(requestDiscountLiveData.isNewItem.getOrAwaitValue())
    }

    @Test
    fun test_getBdProductByBarcode_when_barcode_is_matched_expect_not_null() {
        // given
        val modelMock = mockk<BdDocumentNoDetailUIModel>(relaxed = true)
        val productMock = mockk<BdDetailProductUIModel>(relaxed = true)
        every { productMock.barcode } returns "8888888888"
        val snapshotList: SnapshotStateList<BdDetailProductUIModel> =
            mutableStateListOf(productMock)
        every { modelMock.products } returns snapshotList

        requestDiscountLiveData.setDocsData(modelMock)
        // when then
        assertNotNull(requestDiscountLiveData.getBdProductByBarcode("8888888888"))
    }

    @Test
    fun test_getBdProductByBarcode_when_barcode_mismatched_expect_null() {
        // given
        val modelMock = mockk<BdDocumentNoDetailUIModel>(relaxed = true)
        val productMock = mockk<BdDetailProductUIModel>(relaxed = true)
        every { productMock.barcode } returns "8888888888"
        val snapshotList: SnapshotStateList<BdDetailProductUIModel> =
            mutableStateListOf(productMock)
        every { modelMock.products } returns snapshotList
        requestDiscountLiveData.setDocsData(modelMock)
        // when then
        assertNull(requestDiscountLiveData.getBdProductByBarcode("8888888899"))
    }

    @Test
    fun test_getBdProductByBarcodeAndExpiryDate_when_barcode_and_expireDate_is_matched() {
        // given
        val expDate = Date()
        val modelMock = mockk<BdDocumentNoDetailUIModel>(relaxed = true)
        val productMock = mockk<BdDetailProductUIModel>(relaxed = true)
        every { productMock.barcode } returns "8888888888"
        every { productMock.expiredDate } returns expDate
        val snapshotList: SnapshotStateList<BdDetailProductUIModel> =
            mutableStateListOf(productMock)
        every { modelMock.products } returns snapshotList
        requestDiscountLiveData.setDocsData(modelMock)
        // when then
        assertNotNull(requestDiscountLiveData.getBdProductByBarcodeAndExpiryDate("8888888888", expDate))
    }

    @Test
    fun test_getEditingProduct() {
        val modelMock = mockk<BdDetailProductUIModel>()
        requestDiscountLiveData.setEditingProduct(modelMock)
        assertEquals(modelMock, requestDiscountLiveData.getEditingProduct())
    }

    @Test
    fun test_setDocsData() =
        runTest {
            val modelMock = mockk<BdDocumentNoDetailUIModel>()
            requestDiscountLiveData.setDocsData(modelMock)
            assertEquals(modelMock, requestDiscountLiveData.docsData.getOrAwaitValue())
        }

    @Test
    fun test_setDiscountType() =
        runTest {
            // given
            val bdDetailUIModel = mockk<BdDocumentNoDetailUIModel>()
            val slot = slot<RequestDiscountType>()
            every {
                bdDetailUIModel.copy(
                    any(),
                    any(),
                    capture(slot),
                    any(),
                )
            } returns bdDetailUIModel
            requestDiscountLiveData.setDocsData(bdDetailUIModel)
            // when
            requestDiscountLiveData.setDiscountType(RequestDiscountType.DEFECTIVE)
        }

    @Test
    fun test_setEditingProduct() {
        val modelMock = mockk<BdDetailProductUIModel>()
        requestDiscountLiveData.setEditingProduct(modelMock)
        assertEquals(modelMock, requestDiscountLiveData.editingProduct.getOrAwaitValue())
    }

    @Test
    fun test_setIsNewProduct() {
        requestDiscountLiveData.setIsNewProduct(true)
        assertEquals(true, requestDiscountLiveData.isNewItem.getOrAwaitValue())
    }

    @Test
    fun `test_removeProductFromBdDetail when barcode mismatched`() {
        // given
        val modelMock = mockk<BdDocumentNoDetailUIModel>(relaxed = true)
        val productMock = mockk<BdDetailProductUIModel>(relaxed = true)
        every { productMock.barcode } returns "8888888888"
        val snapshotList: SnapshotStateList<BdDetailProductUIModel> =
            mutableStateListOf(productMock)
        every { modelMock.products } returns snapshotList
        requestDiscountLiveData.setDocsData(modelMock)
        requestDiscountLiveData.setEditingProduct(mockk())
        // when
        requestDiscountLiveData.setEditingProduct(productMock)
        // then
        assertNotNull(requestDiscountLiveData.editingProduct.getOrAwaitValue())
    }

    @Test
    fun test_updateDocsProducts_with_newItem() {
        // given
        val expDate = Date()
        val modelMock = mockk<BdDocumentNoDetailUIModel>(relaxed = true)
        val productMock = mockk<BdDetailProductUIModel>(relaxed = true)
        every { productMock.barcode } returns "8888888888"
        every { productMock.expiredDate } returns expDate
        val snapshotList: SnapshotStateList<BdDetailProductUIModel> =
            mutableStateListOf(productMock)
        every { modelMock.products } returns snapshotList
        requestDiscountLiveData.setDocsData(modelMock)
        // when
        val addingProduct = mockk<BdDetailProductUIModel>(relaxed = true)
        every { addingProduct.barcode } returns "8888888888"
        every { addingProduct.expiredDate } returns expDate
        every { addingProduct.numberOfDiscounted } returns 1000
        val result = requestDiscountLiveData.updateDocsProducts(true, addingProduct)
        // then
        assertTrue(result)
    }

    @Test
    fun test_updateDocsProducts_with_not_newItem() {
        // given
        val expDate = Date()
        val modelMock = mockk<BdDocumentNoDetailUIModel>(relaxed = true)
        val productMock = mockk<BdDetailProductUIModel>(relaxed = true)
        every { productMock.barcode } returns "8888888888"
        every { productMock.expiredDate } returns expDate
        val snapshotList: SnapshotStateList<BdDetailProductUIModel> =
            mutableStateListOf(productMock)
        every { modelMock.products } returns snapshotList
        requestDiscountLiveData.setDocsData(modelMock)
        // when
        val addingProduct = mockk<BdDetailProductUIModel>(relaxed = true)
        every { addingProduct.barcode } returns "8888888888"
        every { addingProduct.expiredDate } returns expDate
        val result = requestDiscountLiveData.updateDocsProducts(false, addingProduct)
        // then
        assertTrue(result)
    }

    @Test
    fun test_updateDocsProducts_when_barcode_mismatched() {
        // given
        val expDate = Date()
        val modelMock = mockk<BdDocumentNoDetailUIModel>(relaxed = true)
        val productMock = mockk<BdDetailProductUIModel>(relaxed = true)
        every { productMock.barcode } returns "8888888888"
        every { productMock.expiredDate } returns expDate
        val snapshotList: SnapshotStateList<BdDetailProductUIModel> =
            mutableStateListOf(productMock)
        every { modelMock.products } returns snapshotList
        requestDiscountLiveData.setDocsData(modelMock)
        // when
        val addingProduct = mockk<BdDetailProductUIModel>(relaxed = true)
        every { addingProduct.barcode } returns "8888888899"
        every { addingProduct.expiredDate } returns expDate
        val result = requestDiscountLiveData.updateDocsProducts(false, addingProduct)
        // then
        assertTrue(result)
    }
}
