package th.co.cjexpress.store.sage.data.model.branchMaster

import com.google.gson.annotations.SerializedName
import java.util.Date

data class BranchIpResponse(
    @SerializedName("branchCode")
    val branchCode: String?,
    @SerializedName("branchNameTh")
    val branchNameTh: String?,
    @SerializedName("timestamp")
    val timestamp: Date?,
    @SerializedName("error_code")
    val errorCode: String?,
    @SerializedName("error_message")
    val errorMessage: String?,
    @SerializedName("error_details")
    val errorDetails: Any?,
)
