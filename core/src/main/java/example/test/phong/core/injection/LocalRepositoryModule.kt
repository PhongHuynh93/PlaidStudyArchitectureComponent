package example.test.phong.core.injection

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import example.test.phong.core.data.prefs.SourceManager.Companion.SOURCES_PREF
import javax.inject.Singleton

@Module
class LocalRepositoryModule {
    @Singleton
    @Provides
    fun provideSourcePref(context: Context): SharedPreferences {
        return context.getSharedPreferences(SOURCES_PREF, Context.MODE_PRIVATE)
    }


}