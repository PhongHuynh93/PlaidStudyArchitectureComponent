package example.test.phong.core.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit

class DesignerNewsV1SourceRemover {
    companion object {
        private const val SOURCE_DESIGNER_NEWS_RECENT = "SOURCE_DESIGNER_NEWS_RECENT"
        fun checkAndRemoveDesignerNewsRecentSource(key: String, prefs: SharedPreferences): Boolean {
            var removed = false
            if (key == SOURCE_DESIGNER_NEWS_RECENT) {
                prefs.edit {
                    remove(key)
                    removed = true
                }
            }
            return removed
        }

    }
}