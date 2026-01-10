package th.co.cjexpress.store.sage.domain.usecase

import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.PromotionType
import th.co.cjexpress.store.sage.domain.model.BarcodeCodecResultModel
import th.co.cjexpress.store.sage.domain.model.QRFreshDecodeDataModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.DateUtils.toStringYYYYMMDDFormat
import th.co.cjexpress.store.sage.utils.DateUtils.toYYMMDDDate
import th.co.cjexpress.store.sage.utils.DateUtils.toYYYYMMDDDate

class BarcodeCodecUseCase {
    fun execute(
        prefix: String? = null,
        barcode: String,
        acceptedPrefix: List<PromotionType>? = null,
    ): Result<BarcodeCodecResultModel> {
        val codePrefix = prefix ?: barcode.take(2)
        val promotionType =
            PromotionType.fromPrefix(codePrefix)
                ?: PromotionType.fromPrefix(prefix ?: barcode.take(1))
                ?: return result(barcode, "", PromotionType.COMMON_BARCODE)

        val decodeResult =
            when (promotionType) {
                // AO,A1
                PromotionType.STANDARD_DEFAULT,
                PromotionType.STANDARD_DISCOUNT,
                -> {
                    decodePriceAndDiscount(barcode, isExtended = false, promotionType)
                }

                // B0,B1
                PromotionType.EXTENDED_DEFAULT,
                PromotionType.EXTENDED_DISCOUNT,
                -> {
                    decodePriceAndDiscount(barcode, isExtended = true, promotionType)
                }

                // A2,B2
                PromotionType.STANDARD_EXPIRE_DATE,
                PromotionType.EXTENDED_EXPIRE_DATE,
                -> {
                    decodeExpireDate(barcode, promotionType)
                }

                // A3,A4
                PromotionType.STANDARD_NEW_PRICE,
                PromotionType.STANDARD_PROMOTION_PRICE,
                -> {
                    decodeNewPriceAndProPrice(barcode, promotionType)
                }

                // B3,B4
                PromotionType.EXTENDED_NEW_PRICE,
                PromotionType.EXTENDED_PROMOTION_PRICE,
                -> {
                    decodeExtendNewPriceAndProPrice(barcode, promotionType)
                }

                PromotionType.QR_FRESH -> {
                    decodeQrFresh(barcode, promotionType)
                }

                else -> result(barcode, "", PromotionType.COMMON_BARCODE)
            }

        // Validate acceptedPrefix after successful decoding
        return if (acceptedPrefix != null &&
            decodeResult is Result.Success &&
            promotionType !in acceptedPrefix
        ) {
            Result.Error(
                cause =
                    BusinessException(
                        errorCode = "",
                        type = ErrorType.ERROR,
                        message = "result contains acceptedPrefix",
                    ),
            )
        } else {
            decodeResult
        }
    }

    private fun decodePriceAndDiscount(
        barcode: String,
        isExtended: Boolean,
        type: PromotionType,
    ): Result<BarcodeCodecResultModel> {
        if (barcode.length < 9) return result(barcode, "", type = type)

        return try {
            val code = barcode.substring(if (isExtended) 10 else 8)
            val price = barcode.substring(2, if (isExtended) 8 else 6)
            val decimal = barcode.substring(if (isExtended) 8 else 6, if (isExtended) 10 else 8)

            // check valid int
            (price + decimal).toInt()

            result(code, "$price.$decimal", type)
        } catch (e: Exception) {
            result(barcode, "", type = type)
        }
    }

    private fun decodeExpireDate(
        barcode: String,
        type: PromotionType,
    ): Result<BarcodeCodecResultModel> {
        if (barcode.length < 9) return result(barcode, "", type = type)

        return try {
            val rawDate = barcode.substring(2, 8)
            val parsedDate = rawDate.toYYMMDDDate()

            // check valid date
            parsedDate.toStringYYYYMMDDFormat()

            val code = barcode.substring(8)
            result(code, rawDate, type)
        } catch (e: Exception) {
            result(barcode, "", type = type)
        }
    }

    private fun decodeNewPriceAndProPrice(
        barcode: String,
        type: PromotionType,
    ): Result<BarcodeCodecResultModel> {
        if (barcode.length < 9) return result(barcode, "", type = type)

        return try {
            val price = barcode.substring(2, 8)
            val length = (price + "X").length
            var nSum: Int? = 0

            for (nInd in 1..<length) {
                nSum = nSum!! + price[nInd - 1]
                    .toString()
                    .toInt() * ((length + 1) - nInd)
            }

            nSum = ((length - 2) - (nSum!! % (length - 2))) % 10

            if (nSum.toString() == barcode.substring(8, 9)) {
                val number = barcode.substring(2, 6)
                val decimal = barcode.substring(6, 8)
                val bar = barcode.substring(9)

                result(bar, "$number.$decimal", type)
            } else {
                result<String>(barcode, null, type)
            }
        } catch (e: Exception) {
            result(barcode, "", type = type)
        }
    }

    private fun decodeExtendNewPriceAndProPrice(
        barcode: String,
        type: PromotionType,
    ): Result<BarcodeCodecResultModel> {
        if (barcode.length < 12) return result(barcode, "", type = type)

        return try {
            val code = barcode.substring(11)
            val priceHex = barcode.substring(2, 11).removeRange(5, 6)
            val decimalPrice = priceHex.toInt(16).toString()

            val asciiChar = decimalPrice.takeLast(2).toInt().toChar()
            if (asciiChar == barcode.last()) {
                val pricePart = decimalPrice.dropLast(2)
                val decimal = pricePart.takeLast(2)
                val number = pricePart.dropLast(2)
                result(code, "$number.$decimal", type)
            } else {
                result<String>(barcode, null, type = type)
            }
        } catch (e: Exception) {
            result(barcode, "", type = type)
        }
    }

    private fun decodeQrFresh(
        qrCode: String,
        type: PromotionType,
    ): Result<BarcodeCodecResultModel> {
        val qrData = qrCode.replace(" ", "").split("|")
        if (qrData.size != 3) {
            return result(qrCode, null, type = type)
        }

        val (lotNo, barcode, expDate) = qrData

        return runCatching {
            val qrFreshData =
                QRFreshDecodeDataModel(
                    lotNo = lotNo,
                    barcode = barcode,
                    expDate = expDate.toYYYYMMDDDate(),
                )
            result(barcode, qrFreshData, type)
        }.getOrElse { e ->
            Result.Error(
                cause =
                    BusinessException(
                        errorCode = "",
                        type = ErrorType.ERROR,
                        // TBC: ข้อความ
                        message = "Failed to parse QR code: ${e.message}",
                    ),
            )
        }
    }

    private fun <T> result(
        code: String,
        data: T? = null,
        type: PromotionType? = null,
    ): Result<BarcodeCodecResultModel> =
        Result.Success(
            BarcodeCodecResultModel(
                code = code,
                data = data,
                prefix = type?.promotionPrefix,
            ),
        )
}
