package th.co.cjexpress.store.sage.domain.model.payload.features

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FeatureConfigs(
    val totePrefix: List<String>? = null,
    @SerializedName("delay")
    val delay: DelayConfigs? = null,
) : Serializable
