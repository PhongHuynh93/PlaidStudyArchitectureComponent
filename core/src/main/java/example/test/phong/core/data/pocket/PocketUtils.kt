package example.test.phong.core.data.pocket

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils

class PocketUtils {
    companion object {
        private val PACKAGE = "com.ideashower.readitlater.pro"
        private val MIME_TYPE = "text/plain"
        private val EXTRA_SOURCE_PACKAGE = "source"
        private val EXTRA_TWEET_STATUS_ID = "tweetStatusId"

        fun addToPocket(context: Context, url: String?, tweetStatusId: String? = null) {
            context.startActivity(Intent(Intent.ACTION_SEND).apply {
                setPackage(PACKAGE)
                type = MIME_TYPE
                putExtra(Intent.EXTRA_TEXT, url)
                if (!TextUtils.isEmpty(tweetStatusId)) {
                    putExtra(EXTRA_TWEET_STATUS_ID, tweetStatusId)
                }
                putExtra(EXTRA_SOURCE_PACKAGE, context.packageName)
            })
        }

        fun isPocketInstalled(context: Context): Boolean {
            val pm = context.packageManager
            val info = try {
                pm.getPackageInfo(PACKAGE, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
            return info != null
        }
    }
}