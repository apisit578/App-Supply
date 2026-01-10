package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CheckLimitBatchUseCase(
    private val menuOrderReceiveUseCase: MenuOrderReceiveUseCase,
    private val shipmentDocumentUseCase: ShipmentDocumentUseCase,
    private val resourceUtils: ResourceUtils,
) {
    enum class Mode {
        SCAN,
        ADJUST
    }
    fun execute(
        receiveAdjust: Int = 0,
        barcode: String,
        mode: Mode = Mode.SCAN,
    ): Result<Unit> {
        val maxBatchQty = menuOrderReceiveUseCase.maxBatchQty()
        val summaryBatchQty = shipmentDocumentUseCase.getSummaryBatchQty()

        validateBarcode(barcode)?.let {
            return Result.Error(cause = it)
        }

        val isValid = when (mode) {
            Mode.SCAN -> (maxBatchQty == 0 || summaryBatchQty < maxBatchQty)
            Mode.ADJUST -> (maxBatchQty == 0 || (summaryBatchQty + receiveAdjust) <= maxBatchQty)
        }

        return if (isValid) {
            Result.Success(Unit)
        } else {
            Result.Error(
                cause =
                    BusinessException(
                        errorCode = "",
                        type = ErrorType.WARNING,
                        title = resourceUtils.getString(R.string.text_title_over_max_qty),
                        message =
                            resourceUtils.getString(
                                R.string.text_detail_over_max_qty,
                                maxBatchQty,
                            ),
                    ),
            )
        }
    }

    fun validateBarcode(barcode: String): BusinessException? =
        if (barcode.isBlank()) {
            BusinessException(
                errorCode = "",
                type = ErrorType.WARNING,
                title = resourceUtils.getString(R.string.text_unable_to_continue),
                message = resourceUtils.getString(R.string.text_please_scan_barcode),
            )
        } else {
            null
        }
}
