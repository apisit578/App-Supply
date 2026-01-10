package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TachyonTopAppBar(
    titleText: String,
    titleAlignment: Alignment.Horizontal = Alignment.Start,
    showNavigationIcon: Boolean = true,
    onNavigationIconClick: () -> Unit,
    action: @Composable () -> Unit = {},
) {
    TopAppBar(
        modifier =
            Modifier
                .height((64 / LocalDensity.current.density).dp)
                .fillMaxWidth(),
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = TachyonDictionary.surface.static._default2,
            ),
        navigationIcon = {
            if (showNavigationIcon) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    IconButton(
                        modifier =
                            Modifier
                                .focusProperties { canFocus = false },
                        onClick =
                            safeClickWithDebounce {
                                onNavigationIconClick()
                            },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "กลับ",
                        )
                    }
                }
            }
        },
        title = {
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                contentAlignment =
                    when (titleAlignment) {
                        Alignment.Start -> Alignment.CenterStart
                        Alignment.CenterHorizontally -> Alignment.Center
                        Alignment.End -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    },
            ) {
                TachyonText(
                    text = titleText,
                    style = TachyonDictionary.headLine._4,
                    color = TachyonDictionary.content._default,
                )
            }
        },
        actions = {
            action()
        },
    )
}
