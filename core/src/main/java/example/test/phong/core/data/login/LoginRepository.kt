package example.test.phong.core.data.login

import example.test.phong.core.data.api.model.User
import javax.inject.Inject

class LoginRepository @Inject constructor(val localDataSource: LoginLocalDataSource, val remoteDataSource: LoginRemoteDataSource){
    fun logout() {
        user = null
        localDataSource.logout()
        remoteDataSource.logout()
    }

    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null
}