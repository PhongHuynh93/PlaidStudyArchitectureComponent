package example.test.phong.plaidstudy

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import example.test.phong.plaidstudy.dagger.DaggerAppComponent
import timber.log.Timber

class App: DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
//        AppInjector.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }
}

class AppInjector {
    companion object {
        fun init(app: App) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}