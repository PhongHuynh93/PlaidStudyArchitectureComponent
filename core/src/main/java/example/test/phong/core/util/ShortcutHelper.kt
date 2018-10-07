package example.test.phong.core.util

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ShortcutManager
import android.os.Build

class ShortcutHelper {
    companion object {
        private val SEARCH_SHORTCUT_ID = "search"
        private val POST_SHORTCUT_ID = "post_dn_story"
        private val DYNAMIC_SHORTCUT_IDS = listOf(POST_SHORTCUT_ID)

        @TargetApi(Build.VERSION_CODES.N_MR1)
        fun disablePostShortcut(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return
            val shortCutManager = context.getSystemService(ShortcutManager::class.java)
            shortCutManager.disableShortcuts(DYNAMIC_SHORTCUT_IDS)
        }
    }
}