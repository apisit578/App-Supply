package th.co.cjexpress.store.sage.data.network.adapter

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import th.co.cjexpress.store.sage.constants.YYYYMMDD_HHMMSSZ_DATE_FORMAT
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date

class SpecificDateYYYYmmHHmmSSzTypeAdapter : JsonSerializer<Date> {
    private val dateFormat = SimpleDateFormat(YYYYMMDD_HHMMSSZ_DATE_FORMAT)

    override fun serialize(
        src: Date?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ): JsonElement {
        return JsonPrimitive(src?.let { dateFormat.format(it) })
    }
}
