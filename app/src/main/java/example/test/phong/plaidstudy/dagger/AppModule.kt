package example.test.phong.plaidstudy.dagger

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideAppContext(app: Application): Context {
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideActiveNetworkInfo(connectivityManager: ConnectivityManager): NetworkInfo {
        return connectivityManager?.activeNetworkInfo
    }
}
