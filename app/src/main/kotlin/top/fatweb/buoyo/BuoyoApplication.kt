package top.fatweb.buoyo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import top.fatweb.buoyo.repository.userdata.UserDataRepository
import top.fatweb.buoyo.util.LogTree
import javax.inject.Inject

@HiltAndroidApp
class BuoyoApplication : Application() {
    @Inject
    lateinit var userDataRepository: UserDataRepository

    override fun onCreate() {
        super.onCreate()

        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else LogTree(this))
    }
}
