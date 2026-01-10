package th.co.cjexpress.store.sage.domain.model.payload.features

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.constants.DELAY_BEFORE_KEYBOARD_ON_DONE
import th.co.cjexpress.store.sage.constants.DELAY_DEFAULT_TIME
import th.co.cjexpress.store.sage.constants.DELAY_KEYBOARD_ON_DONE_DEBOUNCE
import java.io.Serializable

data class DelayConfigs(
    @SerializedName("defaultDelayMs")
    val defaultDelayMs: Long = DELAY_DEFAULT_TIME,
    @SerializedName("keyboardOnDoneDebounceMs")
    val keyboardOnDoneDebounceMs: Long = DELAY_KEYBOARD_ON_DONE_DEBOUNCE,
    @SerializedName("beforeKeyboardOnDone")
    val beforeKeyboardOnDone: Long = DELAY_BEFORE_KEYBOARD_ON_DONE,
) : Serializable
