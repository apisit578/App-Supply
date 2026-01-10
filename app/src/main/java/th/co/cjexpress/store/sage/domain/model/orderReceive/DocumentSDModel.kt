package th.co.cjexpress.store.sage.domain.model.orderReceive

import java.util.Date

data class DocumentSDModel(
    val code: String,
    val message: String,
    val data: List<DocumentSDDataModel>,
)

data class DocumentSDDataModel(
    val createdDate: Date,
    val sdNo: String,
    val ldNo: String
)
