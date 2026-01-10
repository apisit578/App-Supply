package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceValidateBillModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class ValidatePurchaseInvoiceBillUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    @RelaxedMockK
    lateinit var menuPurchaseInvoiceUseCase: MenuPurchaseInvoiceUseCase

    private lateinit var useCase: ValidatePurchaseInvoiceBillUseCase

    override fun setUp() {
        super.setUp()
        useCase =
            ValidatePurchaseInvoiceBillUseCase(
                repository,
                menuPurchaseInvoiceUseCase,
                resourceUtils,
            )
    }

    @Test
    fun test_validatePurchaseInvoiceBill_success_expect_result_success() =
        runTest {
            // given
            val billNo = "A123-45/BC"
            val supplierCode = "S001"
            val piNo = "pi123"
            val dataMock = mockk<PurchaseInvoiceValidateBillModel>(relaxed = true)

            coEvery {
                menuPurchaseInvoiceUseCase.getBillNoValidationRegEx()
            } returns "^[A-Z0-9/\\-.]+$"

            coEvery {
                repository.validatePurchaseInvoiceBill(billNo, piNo, supplierCode)
            } returns flowOf(Result.Success(data = dataMock))

            // when
            val flow = useCase.execute(billNo, piNo, supplierCode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(dataMock), result)
            }
        }

    @Test
    fun test_validatePurchaseInvoiceBill_error_expect_result_error() =
        runTest {
            // given
            val billNo = "Bill001"
            val supplierCode = "S001"
            val piNo = "pi123"

            coEvery {
                repository.validatePurchaseInvoiceBill(billNo, piNo, supplierCode)
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )

            // when
            val flow = useCase.execute(billNo, piNo, supplierCode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun validatePurchaseInvoiceBill_whenBillNoIsBlank_expectResultErrorWithBusinessException() =
        runTest {
            val billNo = ""
            val supplierCode = "S001"
            val piNo = "pi123"

            coEvery { resourceUtils.getString(any()) } returns "error"
            coEvery { menuPurchaseInvoiceUseCase.getBillNoValidationRegEx() } returns "^[A-Z0-9/\\-.]+$"

            val flow = useCase.execute(billNo, piNo, supplierCode)

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertTrue(error is BusinessException)
            }
        }

    @Test
    fun validatePurchaseInvoiceBill_whenSupplierCodeIsBlank_expectResultErrorWithBusinessException() =
        runTest {
            val billNo = "A123-45/BC"
            val supplierCode = ""
            val piNo = "pi123"

            coEvery { resourceUtils.getString(any()) } returns "error"
            coEvery { menuPurchaseInvoiceUseCase.getBillNoValidationRegEx() } returns "^[A-Z0-9/\\-.]+$"

            val flow = useCase.execute(billNo, piNo, supplierCode)

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertTrue(error is BusinessException)
            }
        }

    @Test
    fun validatePurchaseInvoiceBill_whenBillNoFormatIsInvalid_expectResultErrorWithBusinessException() =
        runTest {
            val billNo = "INVALID!@#"
            val supplierCode = "S001"
            val piNo = "pi123"

            coEvery { menuPurchaseInvoiceUseCase.getBillNoValidationRegEx() } returns "^[A-Z0-9/\\-.]+$"
            coEvery { resourceUtils.getString(any()) } returns "error"

            val flow = useCase.execute(billNo, piNo, supplierCode)

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertTrue(error is BusinessException)
            }
        }

    @Test
    fun validateRequireBillNo_whenBothFieldsAreNotBlank_expectNull() {
        val billNo = "A123-45/BC"
        val supplierCode = "S001"

        coEvery { resourceUtils.getString(any()) } returns "error"

        val result = useCase.validateRequireBillNo(billNo, supplierCode)
        assertEquals(null, result)
    }

    @Test
    fun validateInvoiceFormat_whenFormatIsValid_expectNull() {
        val billNo = "A123-45/BC"
        coEvery { menuPurchaseInvoiceUseCase.getBillNoValidationRegEx() } returns "^[A-Z0-9/\\-.]+$"
        coEvery { resourceUtils.getString(any()) } returns "error"

        val result = useCase.validateInvoiceFormat(billNo)
        assertEquals(null, result)
    }
}
