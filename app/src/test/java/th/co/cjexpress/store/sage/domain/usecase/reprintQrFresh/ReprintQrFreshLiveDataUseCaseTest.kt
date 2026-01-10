package th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.domain.livedata.reprintQrFresh.ReprintQrFreshLiveData
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel

class ReprintQrFreshLiveDataUseCaseTest {

    private lateinit var reprintQrFreshLiveData: ReprintQrFreshLiveData
    private lateinit var useCase: ReprintQrFreshLiveDataUseCase

    @Before
    fun setup() {
        reprintQrFreshLiveData = mockk(relaxed = true)
        useCase = ReprintQrFreshLiveDataUseCase(reprintQrFreshLiveData)
    }

    @Test
    fun test_getProductQrFreshItem_should_return_item_from_liveData() {
        val item = ReprintQrFreshItemUIModel(
            barcode = "123",
            skuName = "skuName",
            expireDate = "2025-08-11",
            qrPrintCount = 1
        )
        every { reprintQrFreshLiveData.getProductItem("123") } returns item

        val result = useCase.getProductQrFreshItem("123")

        assertEquals(item, result)
        verify { reprintQrFreshLiveData.getProductItem("123") }
    }

    @Test
    fun test_addQrFreshProduct_should_call_liveData() {
        val item = ReprintQrFreshItemUIModel("123", skuName = "skuName", "2025-08-11", 1)

        useCase.addQrFreshProduct(item)

        verify { reprintQrFreshLiveData.addQrFreshProduct(item) }
    }

    @Test
    fun test_updateQrPrintCount_should_call_liveData() {
        useCase.updateQrPrintCount("123", "2025-08-11", 5)

        verify { reprintQrFreshLiveData.updateQrPrintCountByBarcode("123", "2025-08-11", 5) }
    }

    @Test
    fun test_removeProductQrFreshItem_should_call_liveData() {
        useCase.removeProductQrFreshItem("123")

        verify { reprintQrFreshLiveData.removeProductItem("123") }
    }

    @Test
    fun test_setQrFreshItem_should_call_liveData() {
        val item = ReprintQrFreshItemUIModel("123", skuName = "skuName", "2025-08-11", 1)

        useCase.setQrFreshItem(item)

        verify { reprintQrFreshLiveData.setQrFreshItem(item) }
    }

    @Test
    fun test_resetQrFreshItem_should_call_liveData() {
        useCase.resetQrFreshItem()

        verify { reprintQrFreshLiveData.resetQrFreshItem() }
    }

    @Test
    fun test_updateQrFreshItemPrintCount_should_call_liveData() {
        useCase.updateQrFreshItemPrintCount(10)

        verify { reprintQrFreshLiveData.updateQrFreshItemPrintCount(10) }
    }

    @Test
    fun test_resetQrFreshItemList_should_call_liveData() {
        useCase.resetQrFreshItemList()

        verify { reprintQrFreshLiveData.resetQrFreshItemList() }
    }
}