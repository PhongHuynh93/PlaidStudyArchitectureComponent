package example.test.phong.core.util

import android.content.Intent

private const val PACKAGE_NAME = "example.test.phong.plaidstudy"

fun intentTo(activity: AddressableActivity): Intent {
    return Intent(Intent.ACTION_VIEW).setClassName(PACKAGE_NAME, activity.className)
}

object Activities {

    object Search : AddressableActivity {
        override val className: String = "$PACKAGE_NAME.ui.search.SearchActivity"
    }

    object Dribbble {
        object Shot : AddressableActivity {
            override val className = "$PACKAGE_NAME.ui.shot.ShotActivity"
            const val EXTRA_SHOT_ID = "xShotId"
            const val RESULT_EXTRA_SHOT_ID = "ResultXShotId"
        }
    }

    object DesignerNews {
        object Login : AddressableActivity {
            override val className: String = "$PACKAGE_NAME.ui.search"
        }
    }

    object About: AddressableActivity {
        override val className: String = "$PACKAGE_NAME.ui.search"
    }
}

interface AddressableActivity {
    val className: String
}
