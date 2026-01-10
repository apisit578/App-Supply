package th.co.cjexpress.store.sage.domain.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import th.co.cjexpress.store.sage.domain.livedata.reprintQrFresh.ReprintQrFreshLiveData
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel
import th.co.cjexpress.store.sage.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class ReprintQrFreshLiveDataTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var reprintQrFreshLiveData: ReprintQrFreshLiveData

    @Before
    fun setUp() {
        reprintQrFreshLiveData = ReprintQrFreshLiveData()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_addQrFreshProduct_adds_new_product() {
        val item = ReprintQrFreshItemUIModel("123", "skuName", "2025-12-31", 1)
        reprintQrFreshLiveData.addQrFreshProduct(item)

        val result = reprintQrFreshLiveData.reprintQrFreshLiveUIModel.value!!.items
        assertEquals(result.first(), item)
    }

    @Test
    fun test_addQrFreshProduct_updates_existing_product() {
        val item1 = ReprintQrFreshItemUIModel("123", "skuName", "2025-12-31", 1)
        reprintQrFreshLiveData.addQrFreshProduct(item1)

        val item2 = ReprintQrFreshItemUIModel("123", "skuName", "2025-12-31", 2)
        reprintQrFreshLiveData.addQrFreshProduct(item2)

        val result = reprintQrFreshLiveData.reprintQrFreshLiveUIModel.value!!.items
        assertEquals(result.first().qrPrintCount, 3)
    }

    @Test
    fun test_updateQrPrintCountByBarcode_updates_count() {
        val item = ReprintQrFreshItemUIModel("123", "skuName", "2025-12-31", 1)
        reprintQrFreshLiveData.addQrFreshProduct(item)

        reprintQrFreshLiveData.updateQrPrintCountByBarcode("123", "2025-12-31", 5)

        val result = reprintQrFreshLiveData.reprintQrFreshLiveUIModel.value!!.items
        assertEquals(result.first().qrPrintCount, 5)
    }

    @Test
    fun test_removeProductItem_removes_correct_item() {
        val item = ReprintQrFreshItemUIModel("123", "skuName", "2025-12-31", 1)
        reprintQrFreshLiveData.addQrFreshProduct(item)

        reprintQrFreshLiveData.removeProductItem("123")
        val result = reprintQrFreshLiveData.reprintQrFreshLiveUIModel.value!!.items
        assertEquals(result.size, 0)
    }

    @Test
    fun test_setQrFreshItem_sets_current_item() {
        val item = ReprintQrFreshItemUIModel("123", "skuName", "2025-12-31", 1)
        reprintQrFreshLiveData.setQrFreshItem(item)

        assertEquals(reprintQrFreshLiveData.reprintQrFreshItemUIModel.value!!, item)
    }

    @Test
    fun test_resetQrFreshItem_sets_current_item_to_null() {
        val item = ReprintQrFreshItemUIModel("123", "skuName", "2025-12-31", 1)
        reprintQrFreshLiveData.setQrFreshItem(item)

        reprintQrFreshLiveData.resetQrFreshItem()

        assertNull(reprintQrFreshLiveData.reprintQrFreshItemUIModel.value)
    }

    @Test
    fun test_updateQrFreshItemPrintCount_changes_print_count() {
        val item = ReprintQrFreshItemUIModel("123", "skuName", "2025-12-31", 1)
        reprintQrFreshLiveData.setQrFreshItem(item)

        reprintQrFreshLiveData.updateQrFreshItemPrintCount(10)

        assertEquals(reprintQrFreshLiveData.reprintQrFreshItemUIModel.value!!.qrPrintCount, 10)
    }

    @Test
    fun test_resetQrFreshItemList_clears_list() {
        val item = ReprintQrFreshItemUIModel("123", "skuName", "2025-12-31", 1)
        reprintQrFreshLiveData.addQrFreshProduct(item)

        reprintQrFreshLiveData.resetQrFreshItemList()
        val result = reprintQrFreshLiveData.reprintQrFreshLiveUIModel.value!!.items
        assertEquals(result.size, 0)
    }
}