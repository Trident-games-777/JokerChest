package oker.vhest.gtuop

import android.app.Application
import com.onesignal.OneSignal
import oker.vhest.gtuop.utils.Constants
import timber.log.Timber

class JokerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        OneSignal.initWithContext(this)
        OneSignal.setAppId(Constants.ONE_SIGNAL)
    }
}