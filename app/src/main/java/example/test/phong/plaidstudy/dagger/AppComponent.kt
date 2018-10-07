package example.test.phong.plaidstudy.dagger

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import example.test.phong.core.injection.LocalRepositoryModule
import example.test.phong.core.injection.RemoteRepositoryModule
import example.test.phong.plaidstudy.App
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, LocalRepositoryModule::class, ActivityModule:: class,
    AppModule::class, RemoteRepositoryModule::class])
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): AppComponent
    }
}