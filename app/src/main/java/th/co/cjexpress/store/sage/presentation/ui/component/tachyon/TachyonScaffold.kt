package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun TachyonScaffold(
    topAppBarContainer: (@Composable () -> Unit)?,
    bottomAppBarContainer: (@Composable () -> Unit)?,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = topAppBarContainer ?: {},
        bottomBar =
            if (bottomAppBarContainer != null) {
                {
                    TachyonBottomAppBar {
                        bottomAppBarContainer()
                    }
                }
            } else {
                {}
            },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            content()
        }
    }
}
