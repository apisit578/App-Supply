package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceLiveData
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class PurchaseInvoiceUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var purchaseInvoiceLiveData: PurchaseInvoiceLiveData

    private lateinit var purchaseInvoiceUseCase: PurchaseInvoiceUseCase

    override fun setUp() {
        super.setUp()
        purchaseInvoiceUseCase = PurchaseInvoiceUseCase(purchaseInvoiceLiveData)
    }

    @Test
    fun test_setPurchaseInvoiceLiveData() =
        runTest {
            // given
            val dataMock = mockk<PurchaseInvoiceSupplierUIModel>(relaxed = true)

            // when
            purchaseInvoiceUseCase.setPurchaseInvoiceSupplier(dataMock)

            // then
            io.mockk.verify { purchaseInvoiceLiveData.setPurchaseInvoiceSupplier(dataMock) }
        }

    @Test
    fun test_resetPurchaseInvoiceSupplier() =
        runTest {
            // when
            purchaseInvoiceUseCase.resetPurchaseInvoiceSupplier()

            // then
            io.mockk.verify { purchaseInvoiceLiveData.resetPurchaseInvoiceSupplier() }
        }

    @Test
    fun test_getListInvoiceSupplierCode() =
        runTest {
            // when
            purchaseInvoiceUseCase.getInvoiceSupplierCode()

            // then
            io.mockk.verify { purchaseInvoiceLiveData.getInvoiceSupplierCode() }
        }

    @Test
    fun test_getPurchaseInvoiceSupplier() =
        runTest {
            // when
            purchaseInvoiceUseCase.getPurchaseInvoiceSupplier()

            // then
            io.mockk.verify { purchaseInvoiceLiveData.getPurchaseInvoiceSupplier() }
        }
}
