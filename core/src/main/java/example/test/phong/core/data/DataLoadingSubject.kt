package example.test.phong.core.data

interface DataLoadingSubject {
    fun isDataLoading(): Boolean
    fun registerCallback(callbacks: DataLoadingCallbacks)
    fun unregisterCallback(callbacks: DataLoadingCallbacks)

    interface DataLoadingCallbacks {
        fun dataStartedLoading()
        fun dataFinishedLoading()
    }
}