package com.sarpex.producthunt

import android.app.Application
import com.sarpex.producthunt.data.AppContainer
import com.sarpex.producthunt.data.AppContainerImpl

class ProductHuntApplication : Application() {

    // AppContainer instance used by the rest of classes to obtain dependencies.
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
