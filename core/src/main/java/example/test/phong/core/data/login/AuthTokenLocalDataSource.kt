package example.test.phong.core.data.login

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class AuthTokenLocalDataSource @Inject constructor(private val prefs: SharedPreferences) {
    companion object {
        const val DESIGNER_NEWS_AUTH_PREF = "DESIGNER_NEWS_AUTH_PREF"
        private const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
    }

    private var _authToken: String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    var authToken: String? = _authToken
        set(value) {
            prefs.edit { putString(KEY_ACCESS_TOKEN, value) }
            field = value
        }
}
