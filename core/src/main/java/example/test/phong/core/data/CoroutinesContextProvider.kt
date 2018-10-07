package example.test.phong.core.data

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import javax.inject.Inject
import kotlin.coroutines.experimental.CoroutineContext

class CoroutinesContextProvider @Inject constructor() {
    val main: CoroutineContext = UI
    val io: CoroutineContext = CommonPool
}