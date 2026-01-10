package th.co.cjexpress.store.sage.utils

import android.content.Context
import android.graphics.drawable.Drawable

class ResourceUtils(
    private val context: Context,
) {
    fun getString(id: Int): String {
        return context.getString(id)
    }

    fun getString(
        id: Int,
        vararg formatArgs: Any?,
    ): String {
        return context.getString(id, *formatArgs)
    }

    fun getDrawable(id: Int): Drawable? {
        return context.getDrawable(id)
    }
}