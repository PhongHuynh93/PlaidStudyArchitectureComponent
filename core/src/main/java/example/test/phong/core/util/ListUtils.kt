package example.test.phong.core.util

inline fun <E: Any, T: Collection<E>> T?.withNotNullNorEmpty(func: T.() -> Unit): Unit {
    if (this != null && this.isNotEmpty()) {
        with (this) { func() }
    }
}

inline fun  <E: Any, T: Collection<E>> T?.whenNotNullNorEmpty(func: (T) -> Unit): Unit {
    if (this != null && this.isNotEmpty()) {
        func(this)
    }
}

inline fun <E: Any, T: Collection<E>> T?.withNullOrEmpty(func: () -> Unit): Unit {
    if (this == null || this.isEmpty()) {
        func()
    }
}