package th.co.cjexpress.store.sage.presentation.model.orderReceive

import th.co.cjexpress.store.sage.presentation.state.orderReceive.summary.SummaryDisplayScreenState

data class OrderReceiveSummaryUIModel(
    val sdNo: String = "",
    val items: List<OrderReceiveSummaryItemUIModel> = emptyList(),
    val displayScreenState: SummaryDisplayScreenState = SummaryDisplayScreenState.SummaryEmptyScreen,
)

data class OrderReceiveSummaryItemUIModel(
    val barcode: String = "",
    val productName: String = "",
    val unitCount: String = "",
    val unitName: String = "",
    val chipText: String = "",
    val isMissing: Boolean = false,
)

// Complete dialog model
data class OrderReceiveSummaryCompleteUIModel(
    val sdNo: String = "",
    val isComplete: Boolean = false,
)
