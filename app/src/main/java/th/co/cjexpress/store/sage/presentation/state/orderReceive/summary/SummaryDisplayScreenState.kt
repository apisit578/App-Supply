package th.co.cjexpress.store.sage.presentation.state.orderReceive.summary

sealed class SummaryDisplayScreenState {
    data object SummaryListItemScreen : SummaryDisplayScreenState()

    data object SummaryEmptyScreen : SummaryDisplayScreenState()
}
