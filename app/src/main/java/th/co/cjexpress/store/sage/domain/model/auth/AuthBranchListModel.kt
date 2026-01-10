package th.co.cjexpress.store.sage.domain.model.auth

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AuthBranchListModel(
    @SerializedName("branches") var branches: List<AuthBranchDataModel>,
) : Serializable
