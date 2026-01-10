package th.co.cjexpress.store.sage.domain.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceLiveData
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierUIModel
import th.co.cjexpress.store.sage.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class PurchaseInvoiceLiveDataTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var purchaseInvoiceLiveData: PurchaseInvoiceLiveData

    @Before
    fun setUp() {
        purchaseInvoiceLiveData = PurchaseInvoiceLiveData()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_setPurchaseInvoiceSupplier_expect_value_propagated() {
        val data = mockk<PurchaseInvoiceSupplierUIModel>(relaxed = true)

        purchaseInvoiceLiveData.setPurchaseInvoiceSupplier(data)

        assertEquals(data, purchaseInvoiceLiveData.purchaseInvoiceSupplierLiveData.value)
    }

    @Test
    fun test_resetPurchaseInvoiceSupplier_expect_value_null() {
        purchaseInvoiceLiveData.setPurchaseInvoiceSupplier(
            mockk(relaxed = true),
        )

        purchaseInvoiceLiveData.resetPurchaseInvoiceSupplier()

        assertEquals(null, purchaseInvoiceLiveData.purchaseInvoiceSupplierLiveData.value)
    }

    @Test
    fun test_getListInvoiceSupplierCode_expect_not_empty() {
        // Arrange
        purchaseInvoiceLiveData.setPurchaseInvoiceSupplier(
            data =
                PurchaseInvoiceSupplierUIModel(
                    supplier =
                        PurchaseInvoiceSupplierDetailUIModel(
                            supplierCode = "1111",
                            supplierName = "",
                            isRefPo = true,
                        ),
                ),
        )

        // Act
        val data = purchaseInvoiceLiveData.getInvoiceSupplierCode()

        // Assert
        assertEquals(true, data.isNotEmpty())
    }

    @Test
    fun test_getListInvoiceSupplierCode_expect_should_empty() {
        // Act
        purchaseInvoiceLiveData.resetPurchaseInvoiceSupplier()
        val data = purchaseInvoiceLiveData.getInvoiceSupplierCode()

        // Assert
        assertEquals(true, data.isEmpty())
    }

    @Test
    fun test_getPurchaseInvoiceSupplier_expect_should_get_supplier() {
        // Arrange
        val mockData =
            PurchaseInvoiceSupplierUIModel(
                supplier =
                    PurchaseInvoiceSupplierDetailUIModel(
                        supplierCode = "1111",
                        supplierName = "",
                        isRefPo = true,
                    ),
            )
        val expectedDataResult = mockData.supplier
        purchaseInvoiceLiveData.setPurchaseInvoiceSupplier(
            data = mockData,
        )

        // Act
        val data = purchaseInvoiceLiveData.getPurchaseInvoiceSupplier()

        // Assert
        assertEquals(expectedDataResult, data)
    }

    @Test
    fun test_getPurchaseInvoiceSupplier_expect_should_get_supplier_with_empty_code() {
        // Arrange
        purchaseInvoiceLiveData.resetPurchaseInvoiceSupplier()

        // Act
        val data = purchaseInvoiceLiveData.getPurchaseInvoiceSupplier()

        // Assert
        assertEquals("", data.supplierCode)
    }

    @Test
    fun test_isRefPo_should_return_true_when_supplier_isRefPo_true() {
        // Arrange
        val supplier =
            PurchaseInvoiceSupplierDetailUIModel(
                supplierCode = "1234",
                supplierName = "CJ",
                isRefPo = true,
            )
        val data = PurchaseInvoiceSupplierUIModel(supplier = supplier)

        purchaseInvoiceLiveData.setPurchaseInvoiceSupplier(data)

        // Act
        val result = purchaseInvoiceLiveData.isRefPo()

        // Assert
        assertEquals(true, result)
    }

    @Test
    fun test_isRefPo_should_return_false_when_supplier_isRefPo_false() {
        // Arrange
        val supplier =
            PurchaseInvoiceSupplierDetailUIModel(
                supplierCode = "1234",
                supplierName = "CJ",
                isRefPo = false,
            )
        val data = PurchaseInvoiceSupplierUIModel(supplier = supplier)

        purchaseInvoiceLiveData.setPurchaseInvoiceSupplier(data)

        // Act
        val result = purchaseInvoiceLiveData.isRefPo()

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun test_isRefPo_should_return_false_when_supplier_null() {
        // Arrange
        purchaseInvoiceLiveData.resetPurchaseInvoiceSupplier()

        // Act
        val result = purchaseInvoiceLiveData.isRefPo()

        // Assert
        assertEquals(false, result)
    }
}
