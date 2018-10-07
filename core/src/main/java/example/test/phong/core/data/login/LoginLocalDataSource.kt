package example.test.phong.core.data.login

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class LoginLocalDataSource @Inject constructor(private val prefs: SharedPreferences) {
    companion object {
        const val DESIGNER_NEWS_PREF = "DESIGNER_NEWS_PREF"
        private const val KEY_USER_ID = "KEY_USER_ID"
        private const val KEY_USER_NAME = "KEY_USER_NAME"
        private const val KEY_USER_AVATAR = "KEY_USER_AVATAR"
    }

    fun logout() {
        prefs.edit {
            putLong(KEY_USER_ID, 0L)
            putString(KEY_USER_NAME, null)
            putString(KEY_USER_AVATAR, null)
        }
    }

}
