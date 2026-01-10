package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.appconfig.FeatureConfigsUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CheckTotePrefixUseCase(
    private val menuOrderReceiveUseCase: MenuOrderReceiveUseCase,
    private val featureConfigsUseCase: FeatureConfigsUseCase,
    private val shipmentDocumentUseCase: ShipmentDocumentUseCase,
    private val resourceUtils: ResourceUtils,
) {
    fun execute(barcode: String): Result<String> {
        val allowExtraItems = menuOrderReceiveUseCase.allowExtraItems()
        val totePrefix = featureConfigsUseCase.getTotePrefix()
        return if (allowExtraItems) {
            if (barcode.first().toString() in totePrefix) {
                Result.Error(
                    data = barcode,
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
            } else {
                Result.Success(barcode)
            }
        } else {
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
    }
}
