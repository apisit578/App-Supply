package th.co.cjexpress.store.sage.domain.model.orderReceiveTote

data class SdToteListMappingModel(
    val code: String,
    val message: String,
    val data: List<SdToteDataModel>,
)

data class SdToteDataModel(
    val documentDate: String,
    val documentNo: String,
    var toteCode: String,
)
