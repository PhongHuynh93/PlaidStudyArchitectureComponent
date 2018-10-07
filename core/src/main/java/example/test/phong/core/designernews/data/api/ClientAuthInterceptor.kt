package example.test.phong.core.designernews.data.api

import example.test.phong.core.data.login.AuthTokenLocalDataSource
import okhttp3.Interceptor
import okhttp3.Response

class ClientAuthInterceptor(private val authTokenDataSource: AuthTokenLocalDataSource, private val clientId: String) :
Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        if (authTokenDataSource.authToken.isNullOrEmpty()) {
            val url = chain.request().url().newBuilder().addQueryParameter("client_id", clientId).build()
            requestBuilder.url(url)
        } else {
            requestBuilder.addHeader("Authorization", "Bearer " + authTokenDataSource.authToken)
        }
        return chain.proceed(requestBuilder.build())
    }
}