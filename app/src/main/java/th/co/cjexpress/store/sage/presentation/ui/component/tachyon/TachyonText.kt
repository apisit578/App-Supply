package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

sealed class TextSegment {
    abstract val content: String

    data class Normal(
        override val content: String,
    ) : TextSegment()

    data class Underlined(
        override val content: String,
    ) : TextSegment()

    data class Highlight(
        override val content: String,
        val color: Color,
    ) : TextSegment()
}

@Composable
fun TachyonText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        maxLines = maxLines,
        style = style,
        onTextLayout = onTextLayout,
    )
}

@Composable
fun TachyonText(
    modifier: Modifier = Modifier,
    segmentsText: List<TextSegment>,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
) {
    val annotatedText =
        buildAnnotatedString {
            segmentsText.forEach { segment ->
                val spanStyle =
                    when (segment) {
                        is TextSegment.Normal ->
                            SpanStyle(color = color)

                        is TextSegment.Underlined ->
                            SpanStyle(textDecoration = TextDecoration.Underline, color = color)

                        is TextSegment.Highlight ->
                            SpanStyle(color = segment.color)
                    }
                withStyle(spanStyle) {
                    append(segment.content)
                }
            }
        }

    Text(
        modifier = modifier,
        text = annotatedText,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        maxLines = maxLines,
        style = style,
    )
}
