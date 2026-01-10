package th.co.cjexpress.store.sage.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.component.TachyonDateCounterViewModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce
import timber.log.Timber
import java.util.Date

@Composable
fun TachyonDateCounter(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    viewModel: TachyonDateCounterViewModel = hiltViewModel(),
    defaultDate: Date = Date(),
    minDate: Date? = null,
    onValueChange: (Date) -> Unit,
) {
    LaunchedEffect(Unit) {
        Timber.tag("STATE").i("TachyonDateCounter defaultDate = $defaultDate")
        viewModel.setDefaultDate(defaultDate)
    }

    Row(
        modifier =
            Modifier
                .height(48.dp)
                .focusable()
                .padding(top = 4.dp, bottom = 4.dp)
                .background(
                    color = TachyonDictionary.content._inversed,
                    shape = RoundedCornerShape(4.dp),
                ).border(
                    width = 1.dp,
                    color = TachyonDictionary.border._default,
                    shape = RoundedCornerShape(4.dp),
                ).then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.focusable(false),
            enabled = enabled,
            onClick =
                safeClickWithDebounce {
                    onValueChange(viewModel.previousDay(minDate))
                },
        ) {
            Box(
                modifier =
                    Modifier
                        .size(24.dp, 24.dp)
                        .background(
                            color =
                                if (enabled) {
                                    TachyonDictionary.surface.action.delete._default
                                } else {
                                    TachyonDictionary.surface.action.delete._disabled
                                },
                            shape = CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons_minus),
                    contentDescription = "ลดวันที่",
                    tint = if (enabled) TachyonDictionary.content._inversed else TachyonDictionary.content._disabled,
                )
            }
        }

        TachyonText(
            text = defaultDate.toStringDDMMYYYYFormat(),
            style =
                TachyonDictionary.body._1.copy(
                    if (enabled) {
                        TachyonDictionary.content._default
                    } else {
                        TachyonDictionary.content._disabled
                    },
                ),
        )

        IconButton(
            modifier = Modifier.focusable(false),
            enabled = enabled,
            onClick =
                safeClickWithDebounce {
                    onValueChange(viewModel.nextDay())
                },
        ) {
            Box(
                modifier =
                    Modifier
                        .size(24.dp, 24.dp)
                        .background(
                            color =
                                if (enabled) {
                                    TachyonDictionary.surface.action.primary._default
                                } else {
                                    TachyonDictionary.surface.action.primary._disabled
                                },
                            shape = CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons_plus),
                    contentDescription = "เพิ่มวันที่",
                    tint = if (enabled) TachyonDictionary.content._inversed else TachyonDictionary.content._disabled,
                )
            }
        }
    }
}
