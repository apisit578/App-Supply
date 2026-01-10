package th.co.cjexpress.store.sage.data.config

import android.content.Context
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.domain.model.PosJSONConfig
import th.co.cjexpress.store.sage.utils.JsonUtils

class ConfigLoader(private val context: Context, private val jsonUtils: JsonUtils) {
    private var mPosJSONConfig: PosJSONConfig? = null

    val jsonConfig: PosJSONConfig
        get() {
            if (mPosJSONConfig == null) {
                mPosJSONConfig = loadJSONConfig()
            }

            return mPosJSONConfig!!
        }

    private fun loadJSONConfig(): PosJSONConfig? {
        try {
            val rawResource = context.resources.openRawResource(R.raw.config)
            val configInByte = ByteArray(rawResource.available())
            rawResource.read(configInByte)

            val configJson = String(configInByte)
            return jsonUtils.fromJson(configJson, PosJSONConfig::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
