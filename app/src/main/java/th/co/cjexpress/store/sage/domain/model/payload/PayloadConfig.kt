package th.co.cjexpress.store.sage.domain.model.payload

import java.io.Serializable
import java.util.Date

data class PayloadConfig(
    var branchCode: String,
    var featureEnable: HashMap<String, Boolean>?,
    var features: PayloadFeatures?,
    var oAuth: HashMap<String, OAuthRowConfig>?,
    var services: HashMap<String, ServiceHeaderConfig>?,
    var loginGroups: ArrayList<LoginGroupHeaderConfig>?,
    var totePrefix: ArrayList<String>?,
    var createdAt: Date,
    var updatedAt: Date,
    var updatedBy: String,
) : Serializable
