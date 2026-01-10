package th.co.cjexpress.store.sage.domain.model.payload.features

import th.co.cjexpress.store.sage.constants.BILL_NO_VALIDATION_REGEX

data class MenuPurchaseInvoice(
    val enable: Boolean? = null,
    val supplierPerPage: Int? = null,
    val billNoValidation: BillNoValidation = BillNoValidation(),
)

data class BillNoValidation(
    val billNoRegEx: String = BILL_NO_VALIDATION_REGEX,
    val placeholder: String = "กรุณากรอก A-Z, 0-9, /, -, .",
)
