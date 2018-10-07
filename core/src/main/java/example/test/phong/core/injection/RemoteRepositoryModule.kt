package example.test.phong.core.injection

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import example.test.phong.core.BuildConfig
import example.test.phong.core.data.api.DenvelopingConverter
import example.test.phong.core.data.search.DribbbleSearchConverter
import example.test.phong.core.data.search.DribbbleSearchService
import example.test.phong.core.producthunt.data.api.AuthInterceptor
import example.test.phong.core.producthunt.data.api.ProductHuntService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RemoteRepositoryModule {
    @Singleton
    @Provides
    fun provideDribbbleSearchService(loggingInterceptor: HttpLoggingInterceptor): DribbbleSearchService {
        val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        return Retrofit.Builder()
                .baseUrl(DribbbleSearchService.ENDPOINT)
                .addConverterFactory(DribbbleSearchConverter.Factory())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(client)
                .build()
                .create(DribbbleSearchService::class.java)
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor() =
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

    @Singleton
    @Provides
    fun provideGson() = Gson()

    @Singleton
    @Provides
    fun provideProductHuntService(loggingInterceptor: HttpLoggingInterceptor, gson: Gson, denvelopingConverter: DenvelopingConverter): ProductHuntService {
        val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(AuthInterceptor(BuildConfig.PRODUCT_HUNT_DEVELOPER_TOKEN))
                .build()
        return Retrofit.Builder()
                .baseUrl(ProductHuntService.ENDPOINT)
                .addConverterFactory(denvelopingConverter)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
                .create(ProductHuntService::class.java)
    }
}