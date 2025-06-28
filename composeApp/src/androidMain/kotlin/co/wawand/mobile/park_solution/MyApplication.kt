package co.wawand.mobile.park_solution

import android.app.Application
import co.wawand.mobile.park_solution.di.initializeKoin
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.koin.android.ext.koin.androidContext


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin(
            config = { androidContext(this@MyApplication) }
        )
        Firebase.initialize(context = this)
    }
}