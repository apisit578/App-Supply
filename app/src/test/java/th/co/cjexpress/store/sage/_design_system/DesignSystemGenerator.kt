package th.co.cjexpress.store.sage._design_system

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import org.junit.Test
import java.io.File
import java.io.FileReader

class DesignSystemGenerator {
    @Test
    fun main() {
        val globalJson =
            Gson().fromJson<JsonObject>(
                JsonReader(FileReader("./src/test/resources/_design_system/global.json")),
                JsonObject::class.java,
            )
        val globalContent = generateDesignSystemDictionary(globalJson)

        val aliasJson =
            Gson().fromJson<JsonObject>(
                JsonReader(FileReader("./src/test/resources/_design_system/Alias.json")),
                JsonObject::class.java,
            )
        val aliasContent = generateDesignSystemDictionary(aliasJson)

        val componentJson =
            Gson().fromJson<JsonObject>(
                JsonReader(FileReader("./src/test/resources/_design_system/Component.json")),
                JsonObject::class.java,
            )
        val componentContent = generateDesignSystemDictionary(componentJson)

        val importContent =
            arrayOf(
                "package th.co.cjexpress.store.sage.presentation.activity.ui.theme",
                "import androidx.compose.ui.graphics.Color",
                "import androidx.compose.ui.text.TextStyle",
                "import androidx.compose.ui.text.font.Font",
                "import androidx.compose.ui.text.font.FontFamily",
                "import androidx.compose.ui.text.font.FontWeight",
                "import androidx.compose.ui.text.style.TextDecoration",
                "import androidx.compose.ui.unit.dp",
                "import androidx.compose.ui.unit.sp",
                "import th.co.cjexpress.store.sage.R",
                "import th.co.cjexpress.store.sage.di.AppDependencyProvider",
                "object TachyonDictionary {",
                "private val displayDensity",
                "get() = AppDependencyProvider.getProviderBuilder().getContext().resources.displayMetrics.density\n\n",
            )

        File("output/design_system/TachyonDictionary.kt").apply {
            this.parentFile?.mkdirs()
            writeText(
                importContent.joinToString("\n") +
                    "//--- global\n" +
                    globalContent +
                    "\n//--- alias\n" +
                    aliasContent +
                    "\n//--- component\n" +
                    componentContent +
                    "}",
            )
        }
    }

    fun generateDesignSystemDictionary(jsonObject: JsonObject): String {
        val builder = StringBuilder()
        nestedLoop(jsonObject, "", builder)

        return builder.toString()
    }

    fun nestedLoop(
        jsonObject: JsonObject,
        parent: String,
        builder: StringBuilder,
    ) {
        val loopKey = jsonObject.asMap().keys.filter { !arrayOf("type").contains(it) }
        loopKey.forEachIndexed { index, it ->
            val obj = jsonObject.get(it)
            if (obj.isJsonObject) {
                if (it == "value") {
                    builder.delete(builder.lastIndexOf("object"), builder.length)

                    when (jsonObject.get("type").asString) {
                        "typography" -> {
                            builder.append("val _$parent = TextStyle(\n")
                            obj.asJsonObject.entrySet()
                                .filter { kVal -> !arrayOf("paragraphSpacing", "textCase").contains(kVal.key) }
                                .forEachIndexed { kIndex, kVal ->
                                    when (kVal.key) {
                                        "textCase" -> {
                                            builder.append("textDecoration = ${convertAliasToCoreFormat(kVal.value.asString)}")
                                        }
                                        else -> {
                                            builder.append("${kVal.key} = ${convertAliasToCoreFormat(kVal.value.asString)}")
                                        }
                                    }

                                    if (kIndex != obj.asJsonObject.entrySet().size - 1) {
                                        builder.append(",")
                                    }

                                    builder.append("\n")
                                }

                            builder.delete(builder.lastIndexOf(","), builder.length)
                            builder.append(")\n")
                        }
                        else -> {
                            nestedLoop(obj.asJsonObject, it, builder)
                        }
                    }
                } else {
                    builder.append("object $it {\n")

                    nestedLoop(obj.asJsonObject, it, builder)
                    if (index == loopKey.size - 1) {
                        builder.append("}\n")
                    }
                }
            } else {
                if (!arrayOf("type", "description").contains(it)) {
                    builder.delete(builder.lastIndexOf("object"), builder.length)
                    val tokenValue = jsonObject.get(it).asString

                    when (parent) {
                        "bold" -> {
                            builder.append("    val _$parent = FontWeight.Bold\n")
                        }
                        "semiBold" -> {
                            builder.append("    val _$parent = FontWeight.SemiBold\n")
                        }
                        "regular" -> {
                            builder.append("    val _$parent = FontWeight.Normal\n")
                        }
                        "none" -> {
                            builder.append("    val _$parent = TextDecoration.None\n")
                        }
                        "underLine" -> {
                            builder.append("    val _$parent = TextDecoration.Underline\n")
                        }
                        "lineThrough" -> {
                            builder.append("    val _$parent = TextDecoration.LineThrough\n")
                        }
                        else -> {
                            if (tokenValue.contains('#')) {
                                builder.append("val _$parent = ${tokenValue.replace("#", "Color(0xFF")})\n")
                            } else {
                                if (tokenValue.contains(".")) {
                                    builder.append("val _$parent = ${convertAliasToCoreFormat(tokenValue)}\n")
                                } else {
                                    when (jsonObject.get("type").asString) {
                                        "typography" -> {
                                            val fontName = tokenValue.replace(" ", "")
                                            builder.append("val _$parent = Typography(\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_bold, FontWeight.Bold),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_extralight, FontWeight.ExtraLight),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_light, FontWeight.Light),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_medium, FontWeight.Medium),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_regular, FontWeight.Normal),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_semibold, FontWeight.SemiBold),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_thin, FontWeight.Thin)\n")
                                            builder.append(")\n")
                                        }
                                        "fontFamilies" -> {
                                            val fontName = tokenValue.replace(" ", "")
                                            builder.append("val _$parent = FontFamily(\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_bold, FontWeight.Bold),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_extralight, FontWeight.ExtraLight),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_light, FontWeight.Light),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_medium, FontWeight.Medium),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_regular, FontWeight.Normal),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_semibold, FontWeight.SemiBold),\n")
                                            builder.append("Font(R.font.${fontName.lowercase()}_thin, FontWeight.Thin)\n")
                                            builder.append(")\n")
                                        }
                                        "fontSizes", "lineHeights", "letterSpacing" -> {
                                            builder.append("val _$parent = ($tokenValue / displayDensity).sp\n")
                                        }
                                        "paragraphSpacing", "spacing" -> {
                                            builder.append("val _$parent = ($tokenValue / displayDensity).dp\n")
                                        }
                                        else -> {
                                            builder.append("val _$parent = $tokenValue\n")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (parent == "") {
            builder.delete(builder.lastIndexOf("}\n"), builder.length)
        }
    }

    fun convertAliasToCoreFormat(input: String): String {
        val cleanedInput = input.removePrefix("{").removeSuffix("}")
        val lastDotIndex = cleanedInput.lastIndexOf('.')
        return if (lastDotIndex != -1 && lastDotIndex < cleanedInput.length - 1) {
            cleanedInput.substring(0, lastDotIndex + 1) +
                "_" + cleanedInput.substring(lastDotIndex + 1)
        } else {
            cleanedInput
        }
    }
}
