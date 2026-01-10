package th.co.cjexpress.store.sage.presentation.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.di.AppDependencyProvider

object TachyonDictionary {
    private val displayDensity
        get() =
            AppDependencyProvider
                .getProviderBuilder()
                .getContext()
                .resources.displayMetrics.density

    // --- global
    object global {
        object colors {
            object brand {
                val _10 = Color(0xFFF5FFF7)
                val _20 = Color(0xFFD5F0E0)
                val _30 = Color(0xFF8EC5A5)
                val _40 = Color(0xFF5FA77D)
                val _50 = Color(0xFF409261)
                val _60 = Color(0xFF006D2E)
                val _70 = Color(0xFF004A1F)
                val _80 = Color(0xFF003C19)
                val _90 = Color(0xFF002C12)
                val _95 = Color(0xFF00210E)
            }

            object gray {
                val _0 = Color(0xFFFFFFFF)
                val _5 = Color(0xFFFAFAFA)
                val _10 = Color(0xFFF6F7FB)
                val _20 = Color(0xFFE9EBF0)
                val _30 = Color(0xFFD3D7E1)
                val _40 = Color(0xFFBEC4D1)
                val _50 = Color(0xFFA8B0C2)
                val _60 = Color(0xFF778093)
                val _70 = Color(0xFF5C6372)
                val _80 = Color(0xFF424752)
                val _90 = Color(0xFF272A31)
                val _100 = Color(0xFF0C0E11)
            }

            object danger {
                val _10 = Color(0xFFFFF2F2)
                val _20 = Color(0xFFF9D1D1)
                val _30 = Color(0xFFF6BABA)
                val _40 = Color(0xFFEA8484)
                val _50 = Color(0xFFE74747)
                val _60 = Color(0xFFE11919)
                val _70 = Color(0xFFB41414)
                val _80 = Color(0xFF870F0F)
                val _90 = Color(0xFF5A0A0A)
                val _95 = Color(0xFF440808)
            }

            object warning {
                val _10 = Color(0xFFFFF8DD)
                val _20 = Color(0xFFFFEEB1)
                val _30 = Color(0xFFFFDD86)
                val _40 = Color(0xFFFFCB59)
                val _50 = Color(0xFFFFB828)
                val _60 = Color(0xFFF9A60C)
                val _70 = Color(0xFFEC9709)
                val _80 = Color(0xFFDF8806)
                val _90 = Color(0xFFD37A03)
                val _95 = Color(0xFFB26000)
            }

            object success {
                val _10 = Color(0xFFECF6ED)
                val _20 = Color(0xFFC8E6C9)
                val _30 = Color(0xFFB3DBB5)
                val _40 = Color(0xFF8DC891)
                val _50 = Color(0xFF67B66C)
                val _60 = Color(0xFF41A447)
                val _70 = Color(0xFF348339)
                val _80 = Color(0xFF27622B)
                val _90 = Color(0xFF1A421C)
                val _95 = Color(0xFF143115)
            }

            object process {
                val _10 = Color(0xFFEDF0FE)
                val _20 = Color(0xFFDBE1FF)
                val _30 = Color(0xFFB5C1FA)
                val _40 = Color(0xFF91A3F7)
                val _50 = Color(0xFF6C84F5)
                val _60 = Color(0xFF4765F2)
                val _70 = Color(0xFF3951C2)
                val _80 = Color(0xFF2B3D91)
                val _90 = Color(0xFF1C2861)
                val _95 = Color(0xFF151E49)
            }
        }

        object font {
            object families {
                val _display =
                    FontFamily(
                        Font(R.font.ibmplexsansthai_bold, FontWeight.Bold),
                        Font(R.font.ibmplexsansthai_extralight, FontWeight.ExtraLight),
                        Font(R.font.ibmplexsansthai_light, FontWeight.Light),
                        Font(R.font.ibmplexsansthai_medium, FontWeight.Medium),
                        Font(R.font.ibmplexsansthai_regular, FontWeight.Normal),
                        Font(R.font.ibmplexsansthai_semibold, FontWeight.SemiBold),
                        Font(R.font.ibmplexsansthai_thin, FontWeight.Thin),
                    )
                val _Content =
                    FontFamily(
                        Font(R.font.ibmplexsansthai_bold, FontWeight.Bold),
                        Font(R.font.ibmplexsansthai_extralight, FontWeight.ExtraLight),
                        Font(R.font.ibmplexsansthai_light, FontWeight.Light),
                        Font(R.font.ibmplexsansthai_medium, FontWeight.Medium),
                        Font(R.font.ibmplexsansthai_regular, FontWeight.Normal),
                        Font(R.font.ibmplexsansthai_semibold, FontWeight.SemiBold),
                        Font(R.font.ibmplexsansthai_thin, FontWeight.Thin),
                    )
            }

            object weight {
                val _bold = FontWeight.Bold
                val _semiBold = FontWeight.SemiBold
                val _regular = FontWeight.Normal
                val _medium = FontWeight.Medium
            }

            object lineHeight {
                val _3xs = (20 / displayDensity).sp
                val _2xs = (24 / displayDensity).sp
                val _xs = (26 / displayDensity).sp
                val _sm = (30 / displayDensity).sp
                val _md = (40 / displayDensity).sp
                val _lg = (48 / displayDensity).sp
                val _xl = (52 / displayDensity).sp
                val _2xl = (66 / displayDensity).sp
                val _3xl = (132 / displayDensity).sp
            }

            object fontSize {
                val _3xs = (20 / displayDensity).sp
                val _2xs = (24 / displayDensity).sp
                val _xs = (28 / displayDensity).sp
                val _2sm = (32 / displayDensity).sp
                val _sm = (36 / displayDensity).sp
                val _md = (40 / displayDensity).sp
                val _lg = (44 / displayDensity).sp
                val _xl = (48 / displayDensity).sp
                val _2xl = (52 / displayDensity).sp
                val _3xl = (56 / displayDensity).sp
            }

            object textCase {
                val _none = TextDecoration.None
            }

            object letterSpacing {
                val _md = (0 / displayDensity).sp
            }

            object paragraphSpacing {
                val _md = (0 / displayDensity).dp
            }

            object decoration {
                val _none = TextDecoration.None
                val _underLine = TextDecoration.Underline
                val _lineThrough = TextDecoration.LineThrough
            }
        }

        object spacing {
            val _2xs = (4 / displayDensity).dp
            val _xs = (8 / displayDensity).dp
            val _sm = (12 / displayDensity).dp
            val _md = (16 / displayDensity).dp
            val _lg = (20 / displayDensity).dp
            val _xl = (24 / displayDensity).dp
            val _2xl = (28 / displayDensity).dp
            val _3xl = (32 / displayDensity).dp
            val _4xl = (36 / displayDensity).dp
        }

        object zindex {
            val _100 = 100
            val _200 = 200
            val _300 = 300
            val _400 = 400
            val _500 = 500
            val _600 = 600
            val _700 = 700
        }

        object BorderRadius {
            val _2xs = 2
            val _xs = 4
            val _sm = 8
            val _md = 16
            val _lg = 24
            val _xl = 32
            val _2xl = 48
        }

        object borderWidth {
            val _none = TextDecoration.None
            val _xs = 1
            val _sm = 2
            val _md = 4
            val _lg = 6
            val _xl = 8
        }
    }

    // --- alias
    object surface {
        object static {
            object ui {
                val _default = global.colors.gray._0
                val _hover = global.colors.gray._10
                val _active = global.colors.gray._20
                val _disabled = global.colors.gray._10
                val _primary = global.colors.brand._10
                val _delete = global.colors.danger._10
            }

            val _default1 = global.colors.gray._0
            val _default2 = global.colors.gray._10
            val _default3 = global.colors.gray._20

            object process {
                val _default = global.colors.process._10
                val _hover = global.colors.process._20
                val _active = global.colors.process._30
                val _disabled = global.colors.process._10
            }

            object success {
                val _default = global.colors.brand._20
                val _hover = global.colors.brand._30
                val _active = global.colors.brand._40
                val _disabled = global.colors.brand._10
            }

            object warning {
                val _default = global.colors.warning._10
                val _hover = global.colors.warning._20
                val _active = global.colors.warning._30
                val _disabled = global.colors.warning._10
            }

            object danger {
                val _default = global.colors.danger._10
                val _hover = global.colors.danger._20
                val _active = global.colors.danger._30
                val _disabled = global.colors.danger._10
            }

            val _default4 = global.colors.process._95
            val _default5 = global.colors.gray._5
        }

        object action {
            object primary {
                val _default = global.colors.brand._60
                val _hover = global.colors.brand._50
                val _active = global.colors.brand._70
                val _disabled = global.colors.gray._10
            }

            object delete {
                val _default = global.colors.danger._50
                val _hover = global.colors.danger._40
                val _active = global.colors.danger._70
                val _disabled = global.colors.gray._10
            }

            val _secondary = global.colors.gray._0
        }
    }

    object content {
        val _default = global.colors.gray._100
        val _inversed = global.colors.gray._0
        val _disabled = global.colors.gray._40
        val _description = global.colors.gray._70
        val _placeholder = global.colors.gray._40
        val _onaction = global.colors.gray._90
        val _onactioninversed = global.colors.gray._0

        object primary {
            val _default = global.colors.brand._60
            val _subdued = global.colors.brand._40
        }

        object process {
            val _default = global.colors.process._60
            val _subdued = global.colors.process._40
        }

        object success {
            val _default = global.colors.success._60
            val _subdued = global.colors.success._40
        }

        object warning {
            val _default = global.colors.warning._60
            val _subdued = global.colors.warning._40
        }

        object danger {
            val _default = global.colors.danger._60
            val _subdued = global.colors.danger._40
        }

        val _subdued = global.colors.gray._50
    }

    object border {
        val _default = global.colors.gray._30
        val _disabled = global.colors.gray._40
        val _subdued = global.colors.gray._5
        val _accented = global.colors.gray._100
        val _inversed = global.colors.gray._0

        object primary {
            val _default = global.colors.brand._60
            val _subdued = global.colors.brand._50
            val _accented = global.colors.brand._70
        }

        object process {
            val _default = global.colors.process._50
            val _subdued = global.colors.process._30
            val _accented = global.colors.process._70
        }

        object success {
            val _default = global.colors.success._50
            val _subdued = global.colors.success._40
            val _accented = global.colors.success._70
        }

        object warning {
            val _default = global.colors.warning._50
            val _subdued = global.colors.warning._40
            val _accented = global.colors.warning._70
        }

        object danger {
            val _default = global.colors.danger._50
            val _subdued = global.colors.danger._40
            val _accented = global.colors.danger._70
        }

        val _focus = global.colors.brand._20
    }

    object font {
        object fontFamily {
            val _display1 = global.font.families._display
            val _display2 = global.font.families._display
            val _headLine1 = global.font.families._Content
            val _headLine2 = global.font.families._Content
            val _headLine3 = global.font.families._Content
            val _headLine4 = global.font.families._Content
            val _headLine5 = global.font.families._Content
            val _body1 = global.font.families._Content
            val _body2 = global.font.families._Content
            val _caption = global.font.families._Content
            val _textLink = global.font.families._Content
        }

        object letterSpacing {
            val _display1 = global.font.letterSpacing._md
            val _display2 = global.font.letterSpacing._md
            val _headLine1 = global.font.letterSpacing._md
            val _headLine2 = global.font.letterSpacing._md
            val _headLine3 = global.font.letterSpacing._md
            val _headLine4 = global.font.letterSpacing._md
            val _headLine5 = global.font.letterSpacing._md
            val _body1 = global.font.letterSpacing._md
            val _body2 = global.font.letterSpacing._md
            val _caption = global.font.letterSpacing._md
            val _textLink = global.font.letterSpacing._md
        }

        object lineHeight {
            val _display1 = global.font.lineHeight._3xl
            val _display2 = global.font.lineHeight._2xl
            val _headLine1 = global.font.lineHeight._xl
            val _headLine2 = global.font.lineHeight._lg
            val _headLine3 = global.font.lineHeight._lg
            val _headLine4 = global.font.lineHeight._md
            val _headLine5 = global.font.lineHeight._md
            val _body1 = global.font.lineHeight._md
            val _body2 = global.font.lineHeight._sm
            val _caption = global.font.lineHeight._sm
            val _textLink = global.font.lineHeight._md
        }

        object fontWeight {
            val _display1 = global.font.weight._semiBold
            val _display2 = global.font.weight._semiBold
            val _headLine1 = global.font.weight._bold
            val _headLine2 = global.font.weight._semiBold
            val _headLine3 = global.font.weight._semiBold
            val _headLine4 = global.font.weight._semiBold
            val _headLine5 = global.font.weight._semiBold
            val _body1 = global.font.weight._semiBold
            val _body2 = global.font.weight._medium
            val _caption = global.font.weight._regular
            val _textLink = global.font.weight._semiBold
        }

        object fontSize {
            val _display1 = global.font.fontSize._2xl
            val _display2 = global.font.fontSize._xl
            val _headLine1 = global.font.fontSize._lg
            val _headLine2 = global.font.fontSize._md
            val _headLine3 = global.font.fontSize._sm
            val _headLine4 = global.font.fontSize._2sm
            val _headLine5 = global.font.fontSize._xs
            val _body1 = global.font.fontSize._xs
            val _body2 = global.font.fontSize._2xs
            val _caption = global.font.fontSize._3xs
            val _textLink = global.font.fontSize._xs
        }

        object textCase {
            val _display1 = global.font.textCase._none
            val _display2 = global.font.textCase._none
            val _headLine1 = global.font.textCase._none
            val _headLine2 = global.font.textCase._none
            val _headLine3 = global.font.textCase._none
            val _headLine4 = global.font.textCase._none
            val _headLine5 = global.font.textCase._none
            val _body1 = global.font.textCase._none
            val _body2 = global.font.textCase._none
            val _caption = global.font.textCase._none
            val _textLink = global.font.textCase._none
        }

        object textDecoration {
            val _display1 = global.font.decoration._none
            val _display2 = global.font.decoration._none
            val _headLine1 = global.font.decoration._none
            val _headLine2 = global.font.decoration._none
            val _headLine3 = global.font.decoration._none
            val _headLine4 = global.font.decoration._none
            val _headLine5 = global.font.decoration._none
            val _body1 = global.font.decoration._none
            val _body2 = global.font.decoration._none
            val _caption = global.font.decoration._none
            val _textLink = global.font.decoration._underLine
        }

        object paragraphSpacing {
            val _display1 = global.font.paragraphSpacing._md
            val _display2 = global.font.paragraphSpacing._md
            val _headLine1 = global.font.paragraphSpacing._md
            val _headLine2 = global.font.paragraphSpacing._md
            val _headLine3 = global.font.paragraphSpacing._md
            val _headLine4 = global.font.paragraphSpacing._md
            val _headLine5 = global.font.paragraphSpacing._md
            val _body1 = global.font.paragraphSpacing._md
            val _body2 = global.font.paragraphSpacing._md
            val _caption = global.font.paragraphSpacing._md
            val _textLink = global.font.paragraphSpacing._md
        }
    }

    object space {
        object padding {
            val _2xs = global.spacing._2xs
            val _xs = global.spacing._xs
            val _sm = global.spacing._sm
            val _md = global.spacing._md
            val _lg = global.spacing._lg
            val _xl = global.spacing._xl
            val _2xl = global.spacing._2xl
            val _3xl = global.spacing._3xl
        }

        object gap {
            val _2xs = global.spacing._2xs
            val _xs = global.spacing._xs
            val _sm = global.spacing._sm
            val _md = global.spacing._md
            val _lg = global.spacing._lg
            val _xl = global.spacing._xl
            val _2xl = global.spacing._2xl
            val _3xl = global.spacing._3xl
        }
    }

    object alias {
        object zindex {
            val _dropdown = global.zindex._100
            val _sticky = global.zindex._200
            val _float = global.zindex._300
            val _modal = global.zindex._400
            val _popover = global.zindex._500
            val _toast = global.zindex._600
            val _overlay = global.zindex._700
        }
    }

    // --- component
    object display {
        val _1 =
            TextStyle(
                fontFamily = font.fontFamily._display1,
                fontWeight = font.fontWeight._display1,
                fontSize = font.fontSize._display1,
                lineHeight = font.lineHeight._display1,
                letterSpacing = font.letterSpacing._display1,
                textDecoration = font.textDecoration._display1,
            )
        val _2 =
            TextStyle(
                fontFamily = font.fontFamily._display2,
                fontWeight = font.fontWeight._display2,
                fontSize = font.fontSize._display2,
                lineHeight = font.lineHeight._display2,
                letterSpacing = font.letterSpacing._display2,
                textDecoration = font.textDecoration._display2,
            )
    }

    object headLine {
        val _1 =
            TextStyle(
                fontFamily = font.fontFamily._headLine1,
                fontWeight = font.fontWeight._headLine1,
                fontSize = font.fontSize._headLine1,
                lineHeight = font.lineHeight._headLine1,
                letterSpacing = font.letterSpacing._headLine1,
                textDecoration = font.textDecoration._headLine1,
            )
        val _2 =
            TextStyle(
                fontFamily = font.fontFamily._headLine2,
                fontWeight = font.fontWeight._headLine2,
                fontSize = font.fontSize._headLine2,
                lineHeight = font.lineHeight._headLine2,
                letterSpacing = font.letterSpacing._headLine2,
                textDecoration = font.textDecoration._headLine2,
            )
        val _3 =
            TextStyle(
                fontFamily = font.fontFamily._headLine3,
                fontWeight = font.fontWeight._headLine3,
                fontSize = font.fontSize._headLine3,
                lineHeight = font.lineHeight._headLine3,
                letterSpacing = font.letterSpacing._headLine3,
                textDecoration = font.textDecoration._headLine3,
            )
        val _4 =
            TextStyle(
                fontFamily = font.fontFamily._headLine4,
                fontWeight = font.fontWeight._headLine4,
                fontSize = font.fontSize._headLine4,
                lineHeight = font.lineHeight._headLine4,
                letterSpacing = font.letterSpacing._headLine4,
                textDecoration = font.textDecoration._headLine4,
            )
        val _5 =
            TextStyle(
                fontFamily = font.fontFamily._headLine5,
                fontWeight = font.fontWeight._headLine5,
                fontSize = font.fontSize._headLine5,
                lineHeight = font.lineHeight._headLine5,
                letterSpacing = font.letterSpacing._headLine5,
                textDecoration = font.textDecoration._headLine5,
            )
    }

    object body {
        val _1 =
            TextStyle(
                fontFamily = font.fontFamily._body1,
                fontWeight = font.fontWeight._body1,
                fontSize = font.fontSize._body1,
                lineHeight = font.lineHeight._body1,
                letterSpacing = font.letterSpacing._body1,
                textDecoration = font.textDecoration._body1,
            )
        val _2 =
            TextStyle(
                fontFamily = font.fontFamily._body2,
                fontWeight = font.fontWeight._body2,
                fontSize = font.fontSize._body2,
                lineHeight = font.lineHeight._body2,
                letterSpacing = font.letterSpacing._body2,
                textDecoration = font.textDecoration._body2,
            )
    }

    object caption {
        val _1 =
            TextStyle(
                fontFamily = font.fontFamily._caption,
                fontWeight = font.fontWeight._caption,
                fontSize = font.fontSize._caption,
                lineHeight = font.lineHeight._caption,
                letterSpacing = font.letterSpacing._caption,
                textDecoration = font.textDecoration._caption,
            )
    }

    object textLink {
        val _default =
            TextStyle(
                fontFamily = font.fontFamily._textLink,
                fontWeight = font.fontWeight._textLink,
                fontSize = font.fontSize._textLink,
                lineHeight = font.lineHeight._textLink,
                letterSpacing = font.letterSpacing._textLink,
                textDecoration = font.textDecoration._textLink,
            )
    }
}
