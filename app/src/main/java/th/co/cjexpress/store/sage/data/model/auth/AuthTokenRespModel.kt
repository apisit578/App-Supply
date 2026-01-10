package th.co.cjexpress.store.sage.data.model.auth

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthTokenRespModel(
    @SerializedName("access_token") val token: String = "",
    @SerializedName("expires_in") val expiresIn: Int = 0,
    @SerializedName("refresh_expires_in") val refreshExpiresIn: Int = 0,
    @SerializedName("refresh_token") val refreshToken: String = "",
    @SerializedName("token_type") val tokenType: String = "",
    @SerializedName("not-before-policy") val notBeforePolicy: Int = 0,
    @SerializedName("session_state") val sessionState: String = "",
    @SerializedName("scope") val scope: String = "",
    @SerializedName("error") val error: String = "",
    @SerializedName("error_description") val description: String = "",
) : Serializable
