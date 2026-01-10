package th.co.cjexpress.store.sage.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType

object SoundPoolUtils {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>()

    // as default set maxStreams = 1
    fun init(
        context: Context,
        maxStreams: Int = 1,
    ) {
        val audioAttributes =
            AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

        soundPool =
            SoundPool
                .Builder()
                .setMaxStreams(maxStreams)
                .setAudioAttributes(audioAttributes)
                .build()

        // load sound
        load(context = context, resId = R.raw.alert2_long_beep)
        load(context = context, resId = R.raw.show_dialog)
        load(context = context, resId = R.raw.success_1)
    }

    private fun load(
        context: Context,
        resId: Int,
    ) {
        soundPool?.let {
            val soundId = it.load(context, resId, 1)
            soundMap[resId] = soundId
        }
    }

    fun play(
        type: ErrorType,
        volume: Float = 1f,
    ) {
        val id =
            when (type) {
                ErrorType.WARNING -> {
                    soundMap[R.raw.alert2_long_beep]
                }

                ErrorType.SUCCESS -> {
                    soundMap[R.raw.success_1]
                }

                ErrorType.QUESTION -> {
                    soundMap[R.raw.show_dialog]
                }

                ErrorType.ERROR,
                ErrorType.SESSION_TIMEOUT,
                -> {
                    soundMap[R.raw.alert2_long_beep]
                }

                else -> {
                    null
                }
            }

        if (id != null) {
            soundPool?.play(id, volume, volume, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }
}
