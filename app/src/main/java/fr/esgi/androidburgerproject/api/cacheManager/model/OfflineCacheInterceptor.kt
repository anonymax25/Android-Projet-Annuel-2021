package fr.esgi.androidburgerproject.api.cacheManager.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import fr.esgi.androidburgerproject.api.cacheManager.api.data.Cacheable
import fr.esgi.androidburgerproject.api.cacheManager.api.data.HeaderContants
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import timber.log.Timber
import java.util.concurrent.TimeUnit

open class OfflineCacheInterceptor(var context: Context?) : Interceptor {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val invocation: Invocation? = request.tag(Invocation::class.java)

        if (invocation != null) {
            val annotation: Cacheable? =
                invocation.method().getAnnotation(Cacheable::class.java)

            /* check if this request has the [Cacheable] annotation */
            if (annotation != null &&
                annotation.annotationClass.simpleName.equals("Cacheable") &&

                !this.isNetworkConnected()
            ) {
                Timber.d("CACHE ANNOTATION: called.::%s", annotation.annotationClass.simpleName)

                // prevent caching when network is on. For that we use the "networkInterceptor"
                Timber.d("cache interceptor: called.")
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()

                request = request.newBuilder()
                    .removeHeader(HeaderContants.HEADER_PRAGMA)
                    .removeHeader(HeaderContants.HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            } else {
                Timber.d("cache interceptor: not called.")
            }
        }
        return chain.proceed(request)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkConnected(): Boolean {
        val connectivityManager: ConnectivityManager =
            this.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

}