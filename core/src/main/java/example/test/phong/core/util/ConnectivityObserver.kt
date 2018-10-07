package example.test.phong.core.util

import android.net.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class ConnectivityObserver(private val connectivityManager: ConnectivityManager, private val activeNetworkInfo: NetworkInfo?, private val callback: Callback) : LifecycleObserver {
    private var connected: Boolean = false
    private var monitoringConnectivity: Boolean = false

    private val connectivityCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            connected = true
            callback.onConnectivityStateChange(connected)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun checkConnectivity() {
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected
        if (!connected) {
            monitoringConnectivity = true
            connectivityManager.registerNetworkCallback(NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(), connectivityCallback)
        }
        callback.onConnectivityStateChange(connected)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun unregisterCheckNetwork() {
        if (monitoringConnectivity) {
            connectivityManager.unregisterNetworkCallback(connectivityCallback)
            monitoringConnectivity = false
        }
    }

    interface Callback {
        fun onConnectivityStateChange(connected: Boolean)
    }
}

