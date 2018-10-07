package example.test.phong.core.designernews

import android.content.Context
import example.test.phong.core.data.login.LoginRepository
import example.test.phong.core.util.ShortcutHelper
import javax.inject.Inject

class DesignerNewsPrefs @Inject constructor(val loginRepository: LoginRepository) {
    fun isLoggedIn(): Boolean {
        return loginRepository.isLoggedIn
    }

    fun logout(context: Context) {
        loginRepository.logout()
        ShortcutHelper.disablePostShortcut(context)
    }
}


