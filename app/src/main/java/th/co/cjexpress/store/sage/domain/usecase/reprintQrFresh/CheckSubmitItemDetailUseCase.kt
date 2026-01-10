package th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel
import th.co.cjexpress.store.sage.utils.DateUtils.getCurrentYear
import th.co.cjexpress.store.sage.utils.DateUtils.isDateBeforeCurrentDate
import th.co.cjexpress.store.sage.utils.ResourceUtils
import timber.log.Timber
import kotlin.math.abs

class CheckSubmitItemDetailUseCase(
    val resourceUtils: ResourceUtils,
) {
    fun execute(
        item: ReprintQrFreshItemUIModel?,
        actualQty: Int,
        dateInput: String,
    ): Flow<Result<ReprintQrFreshItemUIModel>> {
        val parts = dateInput.split("/")
        val date = parts[0]
        val month = parts.getOrNull(1) ?: ""
        val year = parts.getOrNull(2) ?: ""
        Timber.tag("DEV").d("dateInput: $dateInput , qty: $actualQty")
        when {
            abs(getCurrentYear() - year.toInt()) > 100 -> {
                return flowOf(
                    handleResultError(
                        title = resourceUtils.getString(R.string.text_unable_to_continue),
                        message = resourceUtils.getString(R.string.text_expiration_date_is_incorrect),
                    ),
                )
            }

            isDateBeforeCurrentDate(date, month, year) -> {
                return flowOf(
                    handleResultError(
                        message = resourceUtils.getString(R.string.text_expire_date_lower),
                    ),
                )
            }

            actualQty == 0 -> {
                return flowOf(
                    handleResultError(
                        message = resourceUtils.getString(R.string.text_qr_code_qty_mismatch),
                    ),
                )
            }

            else -> {
                return flowOf(
                    Result.Success(
                        ReprintQrFreshItemUIModel(
                            expireDate = "$date/$month/$year",
                            qrPrintCount = actualQty,
                            barcode = item?.barcode.orEmpty(),
                            skuName = item?.skuName.orEmpty(),
                        ),
                    ),
                )
            }
        }
    }

    fun handleResultError(
        title: String? = null,
        message: String,
    ): Result<ReprintQrFreshItemUIModel> =
        Result.Error(
            cause =
                BusinessException(
                    errorCode = "",
                    type = ErrorType.WARNING,
                    title = title ?: resourceUtils.getString(R.string.text_unable_to_continue),
                    message = message,
                ),
        )
}
