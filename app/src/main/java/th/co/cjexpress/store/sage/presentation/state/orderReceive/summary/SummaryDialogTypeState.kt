package th.co.cjexpress.store.sage.presentation.state.orderReceive.summary

sealed class SummaryDialogTypeState {
    data object SummaryComplete: SummaryDialogTypeState()
    data object SummaryConfirm: SummaryDialogTypeState()
    data object SummaryRetry: SummaryDialogTypeState()
    data object Close: SummaryDialogTypeState()
}