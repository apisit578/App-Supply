package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import th.co.cjexpress.store.sage.constants.BILL_NO_VALIDATION_REGEX
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository

class MenuPurchaseInvoiceUseCase(
    private val configRepository: ConfigRepository,
) {
    fun supplierPerPage() =
        configRepository
            .getAppConfig()
            .payloadConfig
            ?.features
            ?.menuPurchaseInvoice
            ?.supplierPerPage
            ?: 20

    fun getBillNoValidationRegEx() =
        configRepository
            .getAppConfig()
            .payloadConfig
            ?.features
            ?.menuPurchaseInvoice
            ?.billNoValidation
            ?.billNoRegEx
            ?: BILL_NO_VALIDATION_REGEX

    fun getBillNoValidationPlaceholder() =
        configRepository
            .getAppConfig()
            .payloadConfig
            ?.features
            ?.menuPurchaseInvoice
            ?.billNoValidation
            ?.placeholder
            ?: "กรุณากรอก A-Z, 0-9, /, -, ."
}
