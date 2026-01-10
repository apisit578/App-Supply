package th.co.cjexpress.store.sage.domain.usecase

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.PromotionType
import th.co.cjexpress.store.sage.domain.model.BarcodeCodecResultModel
import th.co.cjexpress.store.sage.domain.model.QRFreshDecodeDataModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.utils.DateUtils.toYYYYMMDDDate

class BarcodeCodecUseCaseTest {
    private lateinit var useCase: BarcodeCodecUseCase

    @Before
    fun setUp() {
        useCase = BarcodeCodecUseCase()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun correctPrice_00() {
        val oData = useCase.execute("00", "00999999000000100008") as Result.Success
        assertEquals(BarcodeCodecResultModel("00999999000000100008", "", ""), oData.data)
    }

    /**
     * Test for A0-A4
     * **/

    @Test
    fun correctPrice_A0() {
        val oData = useCase.execute("A0", "A0999999000000100009") as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100009", "9999.99", "A0"), oData.data)
    }

    @Test
    fun correctPrice_A1() {
        val oDatat = useCase.execute("A1", "A1999999000000100010") as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100010", "9999.99", "A1"), oDatat.data)
    }

    @Test
    fun correctDate_A2() {
        val oData = useCase.execute("A2", "A2200310000000100011") as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100011", "200310", "A2"), oData.data)
    }

    @Test
    fun correctPrice_A3() {
        val oData = useCase.execute("A3", "A39999992000000100012") as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100012", "9999.99", "A3"), oData.data)
    }

    @Test
    fun correctPrice_A4() {
        val oData = useCase.execute("A4", "A49999992000000100013") as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100013", "9999.99", "A4"), oData.data)
    }

    @Test
    fun correctPrice_a0() {
        val oData = useCase.execute("A0", "a0999999000000100009") as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100009", "9999.99", "A0"), oData.data)
    }

    @Test
    fun correctPrice_a1() {
        val oData = useCase.execute("A1", "a1999999000000100010") as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100010", "9999.99", "A1"), oData.data)
    }

    @Test
    fun correctDate_a2() {
        val oData = useCase.execute("A2", "a2200310000000100011") as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100011", "200310", "A2"), oData.data)
    }

    @Test
    fun correctPrice_a3() {
        val oData = useCase.execute("A3", "a39999992000000100012") as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100012", "9999.99", "A3"), oData.data)
    }

    @Test
    fun correctPrice_a4() {
        val oData = useCase.execute("A4", "a49999992000000100013") as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100013", "9999.99", "A4"), oData.data)
    }

    // --- Incorrect Input
    @Test
    fun incorrectPrice_A0() {
        val oData = useCase.execute("A0", "A0XXXXXX000000100009") as Result.Success
        assertEquals(BarcodeCodecResultModel("A0XXXXXX000000100009", "", "A0"), oData.data)
    }

    @Test
    fun incorrectPrice_A1() {
        val oData = useCase.execute("A1", "A1XXXXXX000000100010") as Result.Success
        assertEquals(BarcodeCodecResultModel("A1XXXXXX000000100010", "", "A1"), oData.data)
    }

    @Test
    fun incorrectDate_A2() {
        val oData = useCase.execute("A2", "A2303030000000100011") as Result.Success
        assertEquals(BarcodeCodecResultModel("A2303030000000100011", "", "A2"), oData.data)
    }

    @Test
    fun incorrectPrice_A3() {
        val oData = useCase.execute("A3", "A3XXXXXX3000000100012") as Result.Success
        assertEquals(BarcodeCodecResultModel("A3XXXXXX3000000100012", "", "A3"), oData.data)
    }

    @Test
    fun incorrectPrice_A4() {
        val oData = useCase.execute("A4", "A4XXXXXX3000000100013") as Result.Success
        assertEquals(BarcodeCodecResultModel("A4XXXXXX3000000100013", "", "A4"), oData.data)
    }

    @Test
    fun incorrectType_A8() {
        val oData = useCase.execute("A8", "A8XXXXXX3000000100013") as Result.Success
        assertEquals(BarcodeCodecResultModel("A8XXXXXX3000000100013", "", ""), oData.data)
    }

    @Test
    fun incorrectCheckSum_A3() {
        val oData = useCase.execute("A3", "A39999998000000100012") as Result.Success
        assertEquals(BarcodeCodecResultModel("A39999998000000100012", null, "A3"), oData.data)
    }

    @Test
    fun incorrectCheckSum_A4() {
        val oData = useCase.execute("A4", "A49999998000000100013") as Result.Success
        assertEquals(BarcodeCodecResultModel("A49999998000000100013", null, "A4"), oData.data)
    }

    @Test
    fun incorrectPrice_a0() {
        val oData = useCase.execute("A0", "a0XXXXXX000000100009") as Result.Success
        assertEquals(BarcodeCodecResultModel("a0XXXXXX000000100009", "", "A0"), oData.data)
    }

    @Test
    fun incorrectPrice_a1() {
        val oData = useCase.execute("A1", "a1XXXXXX000000100010") as Result.Success
        assertEquals(BarcodeCodecResultModel("a1XXXXXX000000100010", "", "A1"), oData.data)
    }

    @Test
    fun incorrectDate_a2() {
        val oData = useCase.execute("A2", "a2303030000000100011") as Result.Success
        assertEquals(BarcodeCodecResultModel("a2303030000000100011", "", "A2"), oData.data)
    }

    @Test
    fun incorrectPrice_a3() {
        val oData = useCase.execute("A3", "a3XXXXXX3000000100012") as Result.Success
        assertEquals(BarcodeCodecResultModel("a3XXXXXX3000000100012", "", "A3"), oData.data)
    }

    @Test
    fun incorrectPrice_a4() {
        val oData = useCase.execute("A4", "a4XXXXXX3000000100013") as Result.Success
        assertEquals(BarcodeCodecResultModel("a4XXXXXX3000000100013", "", "A4"), oData.data)
    }

    @Test
    fun incorrectType_a8() {
        val oData = useCase.execute("A8", "a8XXXXXX3000000100013") as Result.Success
        assertEquals(BarcodeCodecResultModel("a8XXXXXX3000000100013", "", ""), oData.data)
    }

    @Test
    fun incorrectCheckSum_a3() {
        val oData = useCase.execute("A3", "a39999998000000100012") as Result.Success
        assertEquals(BarcodeCodecResultModel("a39999998000000100012", null, "A3"), oData.data)
    }

    @Test
    fun incorrectCheckSum_a4() {
        val oData = useCase.execute("A4", "a49999998000000100013") as Result.Success
        assertEquals(BarcodeCodecResultModel("a49999998000000100013", null, "A4"), oData.data)
    }

    /**
     * Test for B0-B4
     * **/
    @Test
    fun correctPrice_B0() {
        assertEquals(
            BarcodeCodecResultModel("50084425", "000012.00", "B0"),
            (useCase.execute("B0", "B00000120050084425") as Result.Success).data,
        )
        assertEquals(
            BarcodeCodecResultModel("50084425", "000012.00", "B0"),
            (useCase.execute("B0", "b00000120050084425") as Result.Success).data,
        )
    }

    @Test
    fun correctPrice_B1() {
        assertEquals(
            BarcodeCodecResultModel("50084425", "000052.00", "B1"),
            (useCase.execute("B1", "B10000520050084425") as Result.Success).data,
        )

        assertEquals(
            BarcodeCodecResultModel("50084425", "000052.00", "B1"),
            (useCase.execute("B1", "b10000520050084425") as Result.Success).data,
        )
    }

    @Test
    fun correctDate_B2() {
        assertEquals(
            BarcodeCodecResultModel("50084425", "241204", "B2"),
            (useCase.execute("B2", "b224120450084425") as Result.Success).data,
        )
    }

    @Test
    fun correctPrice_B3() {
        assertEquals(
            BarcodeCodecResultModel("8851952350147", "289.75", "B3"),
            (useCase.execute("B3", "B3002C326938851952350147") as Result.Success).data,
        )
    }

    @Test
    fun correctPrice_B4() {
        assertEquals(
            BarcodeCodecResultModel("8851952350147", "289.75", "B4"),
            (useCase.execute("B4", "B4002C346938851952350147") as Result.Success).data,
        )
    }

    // --- Incorrect Input
    @Test
    fun incorrectPrice_00() {
        val oData = useCase.execute("00", "00XXXXXX000000100008") as Result.Success
        assertEquals(BarcodeCodecResultModel("00XXXXXX000000100008", "", ""), oData.data)
    }

    @Test
    fun incorrectPrice_B0() {
        val oData = useCase.execute("B0", "B0XXXXXX000000100009") as Result.Success
        assertEquals(BarcodeCodecResultModel("B0XXXXXX000000100009", "", "B0"), oData.data)
    }

    @Test
    fun incorrectPrice_B1() {
        val oData = useCase.execute("B1", "B1XXXXXX000000100010") as Result.Success
        assertEquals(BarcodeCodecResultModel("B1XXXXXX000000100010", "", "B1"), oData.data)
    }

    @Test
    fun incorrectDate_B2() {
        val oData = useCase.execute("B2", "B2303030000000100011") as Result.Success
        assertEquals(BarcodeCodecResultModel("B2303030000000100011", "", "B2"), oData.data)
    }

    @Test
    fun incorrectPrice_B3_lastDigitMismatch() {
        val oData = useCase.execute("B3", "B3002C326938851952350148") as Result.Success
        val expectedIncorrect = BarcodeCodecResultModel("B3002C326938851952350148", null, "B3")
        assertEquals(expectedIncorrect, oData.data)
    }

    @Test
    fun incorrectPrice_B3_wrong_price_hex() {
        val oData = useCase.execute("B3", "B3XXXXXXXXX8851952350147") as Result.Success
        val expectedIncorrect = BarcodeCodecResultModel("B3XXXXXXXXX8851952350147", "", "B3")
        assertEquals(expectedIncorrect, oData.data)
    }

    @Test
    fun incorrectPrice_B4_lastDigitMismatch() {
        val oData = useCase.execute("B4", "B4002C326938851952350148") as Result.Success
        val expectedIncorrect = BarcodeCodecResultModel("B4002C326938851952350148", null, "B4")
        assertEquals(expectedIncorrect, oData.data)
    }

    @Test
    fun incorrectPrice_B4_wrong_price_hex() {
        val oData = useCase.execute("B4", "B4XXXXXXXXX8851952350148") as Result.Success
        val expectedIncorrect = BarcodeCodecResultModel("B4XXXXXXXXX8851952350148", "", "B4")
        assertEquals(expectedIncorrect, oData.data)
    }

    @Test
    fun incorrectType_B8() {
        val oData = useCase.execute("B8", "B8XXXXXX3000000100013") as Result.Success
        assertEquals(BarcodeCodecResultModel("B8XXXXXX3000000100013", "", ""), oData.data)
    }

    // --- Barcode length is lower than 9 digit
    @Test
    fun lower9digitBarCodeUpperCase() {
        var oData = useCase.execute("00", "00999999") as Result.Success
        assertEquals(BarcodeCodecResultModel("00999999", "", ""), oData.data)
        /** for B0-B4 */
        oData = useCase.execute("B0", "B0999999") as Result.Success
        assertEquals(BarcodeCodecResultModel("B0999999", "", "B0"), oData.data)

        oData = useCase.execute("B1", "B1999999") as Result.Success
        assertEquals(BarcodeCodecResultModel("B1999999", "", "B1"), oData.data)

        oData = useCase.execute("B2", "B2200310") as Result.Success
        assertEquals(BarcodeCodecResultModel("B2200310", "", "B2"), oData.data)

        oData = useCase.execute("B3", "B3999999") as Result.Success
        assertEquals(BarcodeCodecResultModel("B3999999", "", "B3"), oData.data)

        oData = useCase.execute("B4", "B4999999") as Result.Success
        assertEquals(BarcodeCodecResultModel("B4999999", "", "B4"), oData.data)
    }

    @Test
    fun lower9digitBarCodeLowerCase() {
        var oData = useCase.execute("00", "00999999") as Result.Success
        assertEquals(BarcodeCodecResultModel("00999999", "", ""), oData.data)
        /** for B0-B4 */
        oData = useCase.execute("B0", "b0999999") as Result.Success
        assertEquals(BarcodeCodecResultModel("b0999999", "", "B0"), oData.data)

        oData = useCase.execute("B1", "b1999999") as Result.Success
        assertEquals(BarcodeCodecResultModel("b1999999", "", "B1"), oData.data)

        oData = useCase.execute("B2", "b2200310") as Result.Success
        assertEquals(BarcodeCodecResultModel("b2200310", "", "B2"), oData.data)

        oData = useCase.execute("B3", "b3999999") as Result.Success
        assertEquals(BarcodeCodecResultModel("b3999999", "", "B3"), oData.data)

        oData = useCase.execute("B4", "b4999999") as Result.Success
        assertEquals(BarcodeCodecResultModel("b4999999", "", "B4"), oData.data)
    }

    // --- Regular Case
    @Test
    fun regularBarCode() {
        val oData = useCase.execute("88", "88503885450118") as Result.Success
        assertEquals(BarcodeCodecResultModel("88503885450118", "", ""), oData.data)
    }

    // --- QR Fresh
    @Test
    fun decodeQRFresh_correct_format() {
        var oData = useCase.execute(barcode = "Lot0005|8851952350147|20241231") as Result.Success
        assertEquals(
            BarcodeCodecResultModel(
                "8851952350147",
                QRFreshDecodeDataModel("Lot0005", "8851952350147", "20241231".toYYYYMMDDDate()),
                "L",
            ),
            oData.data,
        )

        oData = useCase.execute(barcode = "L10000000|8851952350148|20250422") as Result.Success
        assertEquals(
            BarcodeCodecResultModel(
                "8851952350148",
                QRFreshDecodeDataModel("L10000000", "8851952350148", "20250422".toYYYYMMDDDate()),
                "L",
            ),
            oData.data,
        )
    }

    @Test
    fun decodeQRFresh_incorrect_format_pipe_not_found() {
        val oData = useCase.execute(barcode = "Lot0005885195235014720241231") as Result.Success
        assertEquals(
            BarcodeCodecResultModel("Lot0005885195235014720241231", null, "L"),
            oData.data,
        )
    }

    @Test
    fun decodeQRFresh_incorrect_format_incomplete_pipe() {
        val oData = useCase.execute(barcode = "Lot0005|885195235014720241231") as Result.Success
        assertEquals(
            BarcodeCodecResultModel("Lot0005|885195235014720241231", null, "L"),
            oData.data,
        )
    }

    @Test
    fun decodeQRFresh_incorrect_format_date_string_incorrect() {
        var oData = useCase.execute(barcode = "Lot0005|8851952350147|20241231ABCDE")
        assertTrue(oData is Result.Success)

        oData = useCase.execute(barcode = "Lot0005|8851952350147|202412313333")
        assertTrue(oData is Result.Error)

        oData = useCase.execute(barcode = "Lot0005|8851952350147|AA20241231")
        assertTrue(oData is Result.Error)
    }

    /**
     * Test for acceptedPrefix validation
     * **/

    @Test
    fun `acceptedPrefix null should work normally`() {
        // When acceptedPrefix is null, should work as normal
        val oData =
            useCase.execute(
                barcode = "A3004500589012345",
                acceptedPrefix = null,
            ) as Result.Success
        assertEquals(BarcodeCodecResultModel("89012345", "0045.00", "A3"), oData.data)
    }

    @Test
    fun `acceptedPrefix allowed type should return decoded result`() {
        // When decoded type is in acceptedPrefix list, should return normal result
        val oData =
            useCase.execute(
                barcode = "A3004500589012345",
                acceptedPrefix = listOf(PromotionType.STANDARD_NEW_PRICE, PromotionType.QR_FRESH),
            ) as Result.Success
        assertEquals(BarcodeCodecResultModel("89012345", "0045.00", "A3"), oData.data)
    }

    @Test
    fun `acceptedPrefix not allowed type should return error`() {
        // When decoded type is not in acceptedPrefix list, should return Result.Error
        val result =
            useCase.execute(
                barcode = "A3123456789012345",
                acceptedPrefix = listOf(PromotionType.COMMON_BARCODE, PromotionType.QR_FRESH),
            )
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertEquals("result contains acceptedPrefix", error.cause.message)
    }

    @Test
    fun `acceptedPrefix qr fresh allowed should decode normally`() {
        // QR Fresh in accepted list should decode normally
        val oData =
            useCase.execute(
                barcode = "Lot0005|8851952350147|20250422",
                acceptedPrefix = listOf(PromotionType.COMMON_BARCODE, PromotionType.QR_FRESH),
            ) as Result.Success
        assertEquals(
            BarcodeCodecResultModel(
                "8851952350147",
                QRFreshDecodeDataModel("Lot0005", "8851952350147", "20250422".toYYYYMMDDDate()),
                "L",
            ),
            oData.data,
        )
    }

    @Test
    fun `acceptedPrefix qr fresh not allowed should return error`() {
        // QR Fresh not in accepted list should return Result.Error
        val result =
            useCase.execute(
                barcode = "Lot0005|8851952350147|20250422",
                acceptedPrefix = listOf(PromotionType.STANDARD_NEW_PRICE, PromotionType.STANDARD_DISCOUNT),
            )
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertEquals("result contains acceptedPrefix", error.cause.message)
    }

    @Test
    fun `acceptedPrefix decode failed should not validate`() {
        // If decode fails, acceptedPrefix validation should not be applied
        val oData =
            useCase.execute(
                barcode = "A3123", // Too short to decode properly
                acceptedPrefix = listOf(PromotionType.COMMON_BARCODE, PromotionType.QR_FRESH),
            )
        assertEquals(true, oData is Result.Error)
    }

    @Test
    fun `acceptedPrefix multiple allowed types`() {
        // Test with multiple allowed types
        val acceptedTypes =
            listOf(
                PromotionType.STANDARD_NEW_PRICE,
                PromotionType.STANDARD_DISCOUNT,
                PromotionType.QR_FRESH,
            )

        // Test A3 (STANDARD_NEW_PRICE) - should be allowed
        val oDataA3 =
            useCase.execute(
                barcode = "A3004500589012345",
                acceptedPrefix = acceptedTypes,
            ) as Result.Success
        assertEquals(BarcodeCodecResultModel("89012345", "0045.00", "A3"), oDataA3.data)

        // Test A1 (STANDARD_DISCOUNT) - should be allowed
        val oDataA1 =
            useCase.execute(
                barcode = "A1999999000000100010",
                acceptedPrefix = acceptedTypes,
            ) as Result.Success
        assertEquals(BarcodeCodecResultModel("000000100010", "9999.99", "A1"), oDataA1.data)

        // Test A0 (STANDARD_DEFAULT) - should not be allowed and return error
        val resultA0 =
            useCase.execute(
                barcode = "A0999999000000100009",
                acceptedPrefix = acceptedTypes,
            )
        assertTrue(resultA0 is Result.Error)
        val errorA0 = resultA0 as Result.Error
        assertEquals("result contains acceptedPrefix", errorA0.cause.message)
    }

    @Test
    fun `acceptedPrefix common barcode type should work when in accepted list`() {
        // Test COMMON_BARCODE when it's in the accepted list
        val oData =
            useCase.execute(
                barcode = "1234567890123",
                acceptedPrefix = listOf(PromotionType.COMMON_BARCODE, PromotionType.QR_FRESH),
            ) as Result.Success
        assertEquals(BarcodeCodecResultModel("1234567890123", "", ""), oData.data)
    }

    @Test
    fun `acceptedPrefix empty list should return error for any promotion type`() {
        // Test with empty accepted list - should return error for any decoded type
        val result =
            useCase.execute(
                barcode = "A3123456789012345",
                acceptedPrefix = emptyList(),
            )
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertEquals("result contains acceptedPrefix", error.cause.message)
    }
}
