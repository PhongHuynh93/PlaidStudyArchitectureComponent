package example.test.phong.core.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit

class DribbbleV1SourceRemover {
    companion object {
        fun checkAndRemove(key: String, prefs: SharedPreferences): Boolean {
            var removed = false
            if (isDribbbleV1Source(key)) {
                prefs.edit { remove(key) }
                removed = true
            }
            return removed
        }
        private const val V1_SOURCE_KEY_PREFIX = "SOURCE_DRIBBBLE_"
        private fun isDribbbleV1Source(key: String) = key.startsWith(V1_SOURCE_KEY_PREFIX)
    }
}