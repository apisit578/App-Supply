package th.co.cjexpress.store.sage.domain.model.masterMapping

data class BdSearchTypeMappingModel(
    val code: String,
    val ref: String,
    val message: String,
    val timestamp: String,
    val types: List<BdSearchTypeItemModel>,
)

data class BdSearchTypeItemModel(
    val code: String,
    val name: String,
    val name2: String,
)
