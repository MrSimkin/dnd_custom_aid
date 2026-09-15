package io.github.mrsimkin.dndcustomaid.android

import android.app.Application

class DndCustomAidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeHostedDevelopmentAuthentication(this)
    }
}
