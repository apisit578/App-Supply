package th.co.cjexpress.store.sage.presentation.mapper.barcodeCodec

import junit.framework.TestCase
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.BarcodeCodecResultModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class BarcodeCodecResultMapperTest : BaseUnitTest() {
    private lateinit var barcodeCodecResultMapper: BarcodeCodecResultMapper

    override fun setUp() {
        barcodeCodecResultMapper = BarcodeCodecResultMapper()
    }

    @Test
    fun `toBarcodeCodecUIModel should map all fields correctly`() {
        // given
        val model =
            BarcodeCodecResultModel(
                code = "1234567890123",
                data = "SOME_DATA",
                prefix = "PREFIX_01",
            )

        // when
        val result = barcodeCodecResultMapper.toBarcodeCodecUIModel(model)

        // then
        TestCase.assertEquals("1234567890123", result.barcode)
        TestCase.assertEquals("SOME_DATA", result.data)
        TestCase.assertEquals("PREFIX_01", result.prefix)
    }
}
