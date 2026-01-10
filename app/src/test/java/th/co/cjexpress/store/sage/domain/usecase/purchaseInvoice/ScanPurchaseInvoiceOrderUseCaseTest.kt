package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class ScanPurchaseInvoiceOrderUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var scanPurchaseInvoiceOrderUseCase: ScanPurchaseInvoiceOrderUseCase

    override fun setUp() {
        super.setUp()
        scanPurchaseInvoiceOrderUseCase =
            ScanPurchaseInvoiceOrderUseCase(
                purchaseInvoiceDetailLiveDataUseCase = purchaseInvoiceDetailLiveDataUseCase,
                resourceUtils = resourceUtils,
            )
    }

    @Test
    fun `test getPurchaseInvoiceOrder returns matching item - expect Success`() =
        runTest {
            // given
            val barcode = "123456789"
            val mockItem =
                PurchaseInvoiceEntryUIModel(
                    barcode = barcode,
                    actualQty = 0,
                    qty = 10,
                    productName = "Product A",
                    unitName = "pcs",
                    unitFactor = 1,
                    salePrice = 0.0,
                )

            every { purchaseInvoiceDetailLiveDataUseCase.getPurchaseInvoiceOrder(barcode) } returns mockItem

            // when
            val result = scanPurchaseInvoiceOrderUseCase.execute(barcode).first()

            // then
            assertTrue(result is Result.Success)
            assertEquals(1, (result as Result.Success).data.actualQty)
        }

    @Test
    fun `test getPurchaseInvoiceOrder returns null - expect Error with BusinessException`() =
        runTest {
            // given
            val barcode = "999999999"
            every { purchaseInvoiceDetailLiveDataUseCase.getPurchaseInvoiceOrder(barcode) } returns null

            every { resourceUtils.getString(R.string.text_purchase_order_barcode_mismatch) } returns "Barcode ไม่ตรง"
            every { resourceUtils.getString(R.string.text_purchase_order_barcode) } returns "ไม่พบบาร์โค้ด %s"

            // when
            val result = scanPurchaseInvoiceOrderUseCase.execute(barcode).first()

            // then
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).cause
            assertEquals(ErrorType.WARNING, error?.type)
            assertEquals("Barcode ไม่ตรง", error?.title)
            assertEquals("ไม่พบบาร์โค้ด $barcode", error?.message)
        }

    @Test
    fun test_execute_error_when_barcode_not_found_expect_result_error() =
        runTest {
            // given
            val barcode = "123456789"
            every { purchaseInvoiceDetailLiveDataUseCase.getPurchaseInvoiceOrder(barcode) } returns null
            every { resourceUtils.getString(R.string.text_purchase_order_barcode_mismatch) } returns "Barcode mismatch"
            every { resourceUtils.getString(R.string.text_purchase_order_barcode) } returns "Barcode: %s"

            // when
            val flow = scanPurchaseInvoiceOrderUseCase.execute(barcode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_execute_error_when_barcode_blank_expect_result_error() =
        runTest {
            // given
            val barcode = ""
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_please_scan_barcode) } returns "Please scan barcode"

            // when
            val flow = scanPurchaseInvoiceOrderUseCase.execute(barcode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }
}
