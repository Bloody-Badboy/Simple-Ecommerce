package dev.arpan.ecommerce

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ECommerceApp : Application() {

    companion object {
        lateinit var instance: ECommerceApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            plantDebugTree()
        }
    }

    private fun plantDebugTree() {
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return (
                    "ECommerce" +
                        super.createStackElementTag(element) +
                        ":" +
                        element.lineNumber
                    )
            }
        })
    }
}
