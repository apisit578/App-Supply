package th.co.cjexpress.store.sage.domain.model.auth

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthBranchDataModel(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("isDC") val isDC: Boolean,
) : Serializable
