package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.BarcodeDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.SkuBarcodeDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.SkuCheckModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CheckSkuByBarcodeUseCaseTest {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var shipmentDocumentUseCase: ShipmentDocumentUseCase

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var useCase: CheckSkuByBarcodeUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = CheckSkuByBarcodeUseCase(repository, shipmentDocumentUseCase, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `skuCheck success with matching barcode emit Success UIModel and addShipmentBatchItem called`() =
        runTest {
            // given
            val barcode = "ABC123"
            val fake =
                SkuCheckModel(
                    code = "Success",
                    message = "Success",
                    data =
                        SkuBarcodeDataModel(
                            skuName = "Product 600ml",
                            barcodes =
                                listOf(
                                    BarcodeDataModel(
                                        barcode = "ABC123",
                                        barcodeFactor = 1,
                                        unitCode = "Unit",
                                        unitName = "Unit",
                                    ),
                                ),
                        ),
                )
            coEvery { repository.skuCheck(barcode) } returns flowOf(Result.Success(fake))

            // when
            val flow = useCase.execute(barcode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                val ui = (result as Result.Success).data
                assertEquals(barcode, ui.barcode)
                assertEquals(1, ui.actualQty)
                assertEquals(false, ui.isTote)
                assertEquals("Product 600ml", ui.productName)
                assertEquals(0, ui.qty)
                assertEquals("", ui.toteCode)
                assertEquals(1, ui.unitFactor)
                assertEquals("Unit", ui.unitName)

                verify {
                    shipmentDocumentUseCase.addShipmentBatchItem(
                        match<ShipmentDataItemUIModel> {
                            it.barcode == barcode &&
                                it.productName == "Product 600ml" &&
                                it.unitFactor == 1 &&
                                    it.unitName == "Unit" &&
                                !it.isTote && it.actualQty == 1
                        },
                    )
                }
            }
        }

    @Test
    fun `repo error NOT_FOUND mapped WARNING + texts`() =
        runTest {
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.NOT_FOUND,
                    type = ErrorType.ERROR,
                    title = "Title Error",
                    message = "Message Error",
                )
            coEvery { repository.skuCheck("B") } returns flowOf(Result.Error(cause = cause))

            every { resourceUtils.getString(R.string.text_title_barcode_mismatch) } returns "Mismatch"
            every { resourceUtils.getString(R.string.text_please_check_and_scan_again) } returns "Please"

            useCase.execute("B").collectLatest { res ->
                assertTrue(res is Result.Error)
                val err = (res as Result.Error).cause
                assertEquals(ErrorCodes.NOT_FOUND, err.errorCode)
                assertEquals(ErrorType.WARNING, err.type)
                assertEquals("Mismatch", err.title)
                assertEquals("Please", err.message)
            }
        }

    @Test
    fun `skuCheck success with NON-matching barcode - default factor=1 and unitName empty and addShipmentBatchItem called`() =
        runTest {
            // given
            val scan = "ABC999"
            val fake =
                SkuCheckModel(
                    code = "Success",
                    message = "Success",
                    data =
                        SkuBarcodeDataModel(
                            skuName = "Product 600ml",
                            barcodes =
                                listOf(
                                    BarcodeDataModel(
                                        barcode = "ABC123",
                                        barcodeFactor = 3,
                                        unitCode = "Unit",
                                        unitName = "Unit",
                                    ),
                                ),
                        ),
                )
            coEvery { repository.skuCheck(scan) } returns flowOf(Result.Success(fake))

            // when
            val flow = useCase.execute(scan)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                val ui = (result as Result.Success).data
                assertEquals(scan, ui.barcode)
                assertEquals(1, ui.actualQty)
                assertEquals(false, ui.isTote)
                assertEquals("Product 600ml", ui.productName)
                assertEquals(0, ui.qty)
                assertEquals("", ui.toteCode)
                assertEquals(1, ui.unitFactor)
                assertEquals("", ui.unitName)

                verify {
                    shipmentDocumentUseCase.addShipmentBatchItem(
                        match<ShipmentDataItemUIModel> {
                            it.barcode == scan &&
                                    it.productName == "Product 600ml" &&
                                    it.unitFactor == 1 &&
                                    it.unitName.isEmpty() &&
                                    !it.isTote && it.actualQty == 1
                        },
                    )
                }
            }
        }

    @Test
    fun `repo error OTHER code - expect passthrough Error from BaseUseCase`() =
        runTest {
            val cause =
                BusinessException(
                    errorCode = "ERROR!!",
                    type = ErrorType.ERROR,
                    title = "Title Error",
                    message = "Message Error",
                )
            coEvery { repository.skuCheck("AAAAA") } returns flowOf(Result.Error(cause = cause))

            useCase.execute("AAAAA").collectLatest { res ->
                assertTrue(res is Result.Error)
                assertEquals("ERROR!!", (res as Result.Error).cause.errorCode)
                assertEquals("Title Error", res.cause.title)
                assertEquals("Message Error", res.cause.message)
            }
        }
}
