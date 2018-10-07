package example.test.phong.core.data.login

import javax.inject.Inject

class LoginRemoteDataSource @Inject constructor(private val tokenLocalDataSource: AuthTokenLocalDataSource){
    fun logout() {
        tokenLocalDataSource.authToken = null
    }

}
