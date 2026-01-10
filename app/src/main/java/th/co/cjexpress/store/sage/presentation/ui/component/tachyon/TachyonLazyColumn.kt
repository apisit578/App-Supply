package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

data class TachyonLazyColumnEmptyState(
    val painter: Painter,
    val titleText: String? = null,
    val descriptionText: String? = null,
)

data class TachyonLazyColumnPullToRefreshState(
    val pullToRefreshLoadingState: Boolean,
    val pullToRefreshCallback: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> TachyonLazyColumn(
    modifier: Modifier,
    pullToRefreshState: TachyonLazyColumnPullToRefreshState? = null,
    rowHeaderText: @Composable (() -> Unit)? = null,
    emptyState: TachyonLazyColumnEmptyState? = null,
    items: List<T>,
    itemKey: (T) -> Any,
    isScrollToTopWhenUpdatedItem: Boolean? = false,
    itemContent: @Composable (T) -> Unit,
) {
    val pullState = rememberPullToRefreshState()
    val pullScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    pullToRefreshState?.let {
        LaunchedEffect(it.pullToRefreshLoadingState) {
            if (!it.pullToRefreshLoadingState) {
                pullState.animateToHidden()
            }
        }
    }

    if (isScrollToTopWhenUpdatedItem == true) {
        LaunchedEffect(items.size) {
            if (items.isNotEmpty()) {
                lazyListState.animateScrollToItem(index = 0)
            }
        }
    }

    PullToRefreshBox(
        state = pullState,
        indicator =
            if (pullToRefreshState != null && !pullToRefreshState.pullToRefreshLoadingState) {
                {
                    Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = false,
                        state = pullState,
                    )
                }
            } else {
                {}
            },
        isRefreshing = pullToRefreshState?.pullToRefreshLoadingState ?: false,
        onRefresh = {
            pullToRefreshState?.let {
                pullScope.launch {
                    it.pullToRefreshCallback()
                }
            }
        },
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowHeaderText?.invoke()
            }

            if (items.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
                    state = lazyListState,
                ) {
                    items(
                        items = items,
                        key = itemKey,
                    ) { item ->
                        itemContent(item)
                    }
                }
            } else {
                if (emptyState != null) {
                    TachyonEmptyLazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                        painter = emptyState.painter,
                        title = emptyState.titleText,
                        description = emptyState.descriptionText ?: "",
                    )
                }
            }
        }
    }
}
