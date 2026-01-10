package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes.OrderReceiveErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CheckItemQtyMoreRefQtyUseCase(
    private val menuOrderReceiveUseCase: MenuOrderReceiveUseCase,
    private val shipmentDocumentUseCase: ShipmentDocumentUseCase,
    private val resourceUtils: ResourceUtils,
) {
    enum class Mode {
        SCAN,
        ADJUST
    }

    fun execute(barcode: String, receiveAdjust: Int = 0, mode: Mode = Mode.SCAN,): Result<ShipmentDataItemUIModel?> =
        shipmentDocumentUseCase.searchShipmentData(barcode)?.let { summary ->
            val allowOverReceivingQty = menuOrderReceiveUseCase.allowOverReceivingQty()
            val isOverReceive = when (mode) {
                Mode.SCAN -> isOverReceiving(summary, summary.actualQty)
                Mode.ADJUST -> isOverReceiving(summary, (summary.actualQty + receiveAdjust), mode = Mode.ADJUST)
            }
            if (isOverReceive) {
                Result.Error(
                    data = if (allowOverReceivingQty) summary else null,
                    cause =
                        BusinessException(
                            errorCode = OrderReceiveErrorCodes.CHECK_ALLOW_ITEM.name,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_title_error_order_receive_overstock),
                            message = "${barcode}\n${summary.productName}",
                        ),
                )
            } else {
                Result.Success(summary)
            }
        } ?: run {
            Result.Error(
                cause =
                    BusinessException(
                        errorCode = "",
                        type = ErrorType.WARNING,
                        title = resourceUtils.getString(R.string.text_title_no_barcode_in_shipment_order),
                        message =
                            String.format(
                                resourceUtils.getString(R.string.text_detail_no_barcode_in_shipment_order),
                                shipmentDocumentUseCase.getShipmentLDNo(),
                            ),
                    ),
            )
        }

    private fun isOverReceiving(
        summaryData: ShipmentDataItemUIModel,
        totalActualQty: Int,
        mode: Mode = Mode.SCAN,
    ): Boolean {
        return !isToteNeedReceive(summaryData) && when (mode) {
            Mode.SCAN -> totalActualQty >= summaryData.qty
            Mode.ADJUST -> totalActualQty > summaryData.qty
        }
    }

    private fun isToteNeedReceive(summaryData: ShipmentDataItemUIModel) = summaryData.isTote && summaryData.qty > 0
}
