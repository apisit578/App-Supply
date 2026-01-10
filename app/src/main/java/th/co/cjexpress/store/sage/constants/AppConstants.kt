package th.co.cjexpress.store.sage.constants

const val DELAY_DEFAULT_TIME = 100L
const val DELAY_KEYBOARD_ON_DONE_DEBOUNCE = 200L
const val DELAY_BEFORE_KEYBOARD_ON_DONE = 200L
const val DEFAULT_REQUEST_TIMEOUT_MS = 180000 // 3 minutes
const val MIN_REQUEST_TIMEOUT_MS = 1000

// format
const val BUS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
const val POSADMIN_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val YYYYMMDD_HHMMSSZ_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val DATE_FORMAT_DDMMYYYY = "dd/MM/yyyy"
const val DATE_FORMAT_DD_MM_YYYY = "dd_MM_yyyy"
const val YYMMDD_DATE_FORMAT = "yyMMdd"
const val YYYYMMDD_DATE_FORMAT = "yyyyMMdd"
const val YYYY_MM_DD_DATE_FORMAT = "yyyy-MM-dd"
const val STRING_FORMAT_DD_MM_YYYY = "%02d/%02d/%04d"

// Regex
const val BILL_NO_VALIDATION_REGEX = "^[A-Z0-9/\\\\-.]+\$"

// Is Ref PO
const val REF_PO = 0
const val NOT_REF_PO = 1

object SharePrefKeys {
    const val PAYLOAD_GLOBAL_CONFIG_KEY = "PAYLOAD_GLOBAL_CONFIG_KEY"
    const val PAYLOAD_BRANCH_CONFIG_KEY = "PAYLOAD_BRANCH_CONFIG_KEY"
}

object Logging {
    const val TAG_PAYLOAD_CONFIG = "PayloadConfig"
    const val TAG_AUTH = "Auth"
    const val TAG_BRANCH_IP = "BranchIP"
    const val TAG_LOGIN = "Login"
    const val TAG_GET_BDLIST = "GET_DBLIST"
    const val TAG_GET_BDETAIL = "GET_DBDETAIL"
    const val TAG_GET_CAMPAIGN_DISCOUNT_TYPE = "GET_CAMPAIGN_DISCOUNT_TYPE"
}

enum class RestfulAuthType {
    NONE,
    BASIC,
    OAUTH,
}

enum class ErrorType {
    WARNING,
    ERROR,
    SUCCESS,
    QUESTION,
    SESSION_TIMEOUT,
}

enum class MasterMapping {
    CAMPAIGN_DISCOUNT_TYPE,
}

enum class MainMenuType {
    REQUEST_DISCOUNT_RTC,
    ORDER_RECEIVE_TOTE,
    ORDER_RECEIVE,
    PURCHASE_INVOICE,
    REPRINT_QR,
    NONE,
}

enum class LogLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR,
}

enum class RequestDiscountType(
    val text: String,
) {
    FRESHLIFE("สินค้าของสด"),
    NORMAL("สินค้าทั่วไป"),
    DEFECTIVE("สินค้าเสื่อม/ชำรุด"),
    MONTH_DISCOUNT("สินค้า 5-7 เดือน"),
}

enum class OrderReceiveToteDiscountType {
    FRESHLIFE,
    NORMAL,
}

enum class OrderReceiveToteLotType {
    LOTDUMMY,
    MANUAL_ADJUST,
}

enum class ScanBarcodeButtonType {
    CHECK,
    START_OVER,
    BACK,
}

// headers
const val HEADER_AUTHORIZATION = "Authorization"
const val HEADER_BRANCH = "branch"
const val HEADER_X_TIMEOUT = "X-Timeout"
const val HEADER_X_TIMESTAMP = "x-timestamp"
const val HEADER_X_REQUEST_ID = "X-Request-ID"
