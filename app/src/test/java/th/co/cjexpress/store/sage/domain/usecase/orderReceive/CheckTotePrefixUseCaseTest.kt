package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.appconfig.FeatureConfigsUseCase
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CheckTotePrefixUseCaseTest {
    @RelaxedMockK
    lateinit var menuOrderReceiveUseCase: MenuOrderReceiveUseCase

    @RelaxedMockK
    lateinit var featureConfigsUseCase: FeatureConfigsUseCase

    @RelaxedMockK
    lateinit var shipmentDocumentUseCase: ShipmentDocumentUseCase

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var useCase: CheckTotePrefixUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase =
            CheckTotePrefixUseCase(
                menuOrderReceiveUseCase,
                featureConfigsUseCase,
                shipmentDocumentUseCase,
                resourceUtils,
            )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `barcode starts with tote prefix AND allowExtra true should return Error`() {
        // given
        every { featureConfigsUseCase.getTotePrefix() } returns listOf("A", "B")
        every { menuOrderReceiveUseCase.allowExtraItems() } returns true

        // when
        val result = useCase.execute("A123")

        // then
        assertTrue(result is Result.Error)
        val err = (result as Result.Error).cause
        assertEquals(ErrorType.WARNING, err.type)
        assertEquals("", err.errorCode)
        assertEquals("A123", result.data)
    }

    @Test
    fun `barcode NOT tote prefix and allowExtra true should return Success`() {
        // given
        every { featureConfigsUseCase.getTotePrefix() } returns listOf("A", "B")
        every { shipmentDocumentUseCase.getShipmentLDNo() } returns "LD123"
        every { menuOrderReceiveUseCase.allowExtraItems() } returns true

        // when
        val result = useCase.execute("Z999")

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `barcode starts with tote prefix but allowExtra false - expect WARNING error`() {
        // given
        every { featureConfigsUseCase.getTotePrefix() } returns listOf("A", "B")
        every { menuOrderReceiveUseCase.allowExtraItems() } returns false
        every { shipmentDocumentUseCase.getShipmentLDNo() } returns "LD123"
        every { resourceUtils.getString(R.string.text_title_no_barcode_in_shipment_order) } returns "Title"
        every { resourceUtils.getString(R.string.text_detail_no_barcode_in_shipment_order) } returns "Detail %s"

        // when
        val result = useCase.execute("A001")

        // then
        assertTrue(result is Result.Error)
        val err = (result as Result.Error).cause
        assertEquals(ErrorType.WARNING, err.type)
        assertEquals("", err.errorCode)
        assertEquals("Title", err.title)
        assertEquals("Detail LD123", err.message)
    }
}
