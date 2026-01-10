package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceDetailLiveData
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

class PurchaseInvoiceDetailLiveDataUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var purchaseInvoiceDetailLiveData: PurchaseInvoiceDetailLiveData

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase

    override fun setUp() {
        super.setUp()
        purchaseInvoiceDetailLiveDataUseCase =
            PurchaseInvoiceDetailLiveDataUseCase(purchaseInvoiceDetailLiveData)
    }

    @Test
    fun test_get_purchaseInvoiceDetail_then_return_purchaseInvoiceDetailUIModel() =
        runTest {
            // when
            purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail

            // then
            verify { purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel }
        }

    @Test
    fun test_get_purchaseInvoiceItem_then_return_purchaseInvoiceEntryUIModel() =
        runTest {
            // when
            purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceItem

            // then
            verify { purchaseInvoiceDetailLiveData.purchaseInvoiceItemUIModel }
        }

    @Test
    fun test_setPurchaseInvoiceDetail() =
        runTest {
            // given
            val dataMock = mockk<PurchaseInvoiceDetailUIModel>(relaxed = true)

            // when
            purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceDetail(dataMock)

            // then
            verify { purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(dataMock) }
        }

    @Test
    fun test_resetPurchaseInvoiceDetail() =
        runTest {
            // when
            purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceDetail()

            // then
            verify { purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail() }
        }

    @Test
    fun test_setPurchaseInvoiceItem() =
        runTest {
            // given
            val dataMock = mockk<PurchaseInvoiceEntryUIModel>(relaxed = true)

            // when
            purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceItem(dataMock)

            // then
            verify { purchaseInvoiceDetailLiveData.setPurchaseInvoiceItem(dataMock) }
        }

    @Test
    fun test_resetPurchaseInvoiceItem() =
        runTest {
            // when
            purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceItem()

            // then
            verify { purchaseInvoiceDetailLiveData.resetPurchaseInvoiceItem() }
        }

    @Test
    fun test_getPurchaseInvoiceOrder() =
        runTest {
            // when
            purchaseInvoiceDetailLiveDataUseCase.getPurchaseInvoiceOrder("1234567890")

            // then
            verify { purchaseInvoiceDetailLiveData.getPurchaseInvoiceOrder("1234567890") }
        }

    @Test
    fun test_updatePurchaseInvoiceProduct() =
        runTest {
            // given
            val dataMock = mockk<IPurchaseInvoiceProductsModel>(relaxed = true)

            // when
            purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceProduct(listOf(dataMock))

            // then
            verify { purchaseInvoiceDetailLiveData.updatePurchaseInvoiceProduct(listOf(dataMock)) }
        }

    @Test
    fun test_updatePurchaseInvoiceItemActualQty() =
        runTest {
            // given
            val actualQty = 10

            // when
            purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceItemActualQty(actualQty)

            // then
            verify { purchaseInvoiceDetailLiveData.updatePurchaseInvoiceItemActualQty(actualQty) }
        }

    @Test
    fun test_updatePurchaseInvoiceDetailBillNo() =
        runTest {
            // given
            val billNo = "1234567890"

            // when
            purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceDetailBillNo(billNo)

            // then
            verify { purchaseInvoiceDetailLiveData.updatePurchaseInvoiceDetailBillNo(billNo) }
        }

    @Test
    fun test_updatePurchaseInvoiceItem() =
        runTest {
            // given
            val item = mockk<IPurchaseInvoiceProductsModel>(relaxed = true)

            // when
            purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceItem(item)

            // then
            verify { purchaseInvoiceDetailLiveData.updatePurchaseInvoiceItem(item) }
        }

    @Test
    fun test_addPurchaseInvoiceProduct() =
        runTest {
            // given
            val entry = mockk<PurchaseInvoiceEntryUIModel>(relaxed = true)

            // when
            purchaseInvoiceDetailLiveDataUseCase.addPurchaseInvoiceProduct(entry)

            // then
            verify { purchaseInvoiceDetailLiveData.addPurchaseInvoiceProduct(entry) }
        }

    @Test
    fun test_removePurchaseInvoiceProductItem() =
        runTest {
            // given
            val barcode = "ABC123"

            // when
            purchaseInvoiceDetailLiveDataUseCase.removePurchaseInvoiceProductItem(barcode)

            // then
            verify { purchaseInvoiceDetailLiveData.removePurchaseInvoiceProductItem(barcode) }
        }

    @Test
    fun test_setUpdatedTimestamp() =
        runTest {
            // given
            val ts = 1699999999999L

            // when
            purchaseInvoiceDetailLiveDataUseCase.setUpdatedTimestamp(ts)

            // then
            verify { purchaseInvoiceDetailLiveData.setUpdatedTimestamp(ts) }
        }

    @Test
    fun test_getPurchaseInvoiceDetailBillNo() =
        runTest {
            // given
            val expected = "BILL-001"
            io.mockk.every { purchaseInvoiceDetailLiveData.getPurchaseInvoiceDetailBillNo() } returns expected

            // when
            val actual = purchaseInvoiceDetailLiveDataUseCase.getPurchaseInvoiceDetailBillNo()

            // then
            assertEquals(expected, actual)
            verify { purchaseInvoiceDetailLiveData.getPurchaseInvoiceDetailBillNo() }
        }

    @Test
    fun test_getPIDetail() =
        runTest {
            // given
            val detail = mockk<PurchaseInvoiceDetailUIModel>(relaxed = true)
            io.mockk.every { purchaseInvoiceDetailLiveData.getPIDetail() } returns detail

            // when
            val actual = purchaseInvoiceDetailLiveDataUseCase.getPIDetail()

            // then
            assertEquals(detail, actual)
            verify { purchaseInvoiceDetailLiveData.getPIDetail() }
        }
}
