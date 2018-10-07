package example.test.phong.core.data

abstract class BaseDataManager<T>: DataLoadingSubject {
    abstract fun onDataLoaded(data: T)
    abstract fun cancelLoading()
}