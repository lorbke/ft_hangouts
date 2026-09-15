package com.lorbke.ft_hangouts

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.lorbke.ft_hangouts.data.Prefs

// The Application object is created once per process and lives for as long
// as the app does - the natural place to track "is any of our screens
// currently visible" across ALL activities, not just one. Declared as the
// app's entry point via android:name in AndroidManifest.xml.
class FtHangoutsApp : Application() {

    // How many of our activities are currently started (on screen). Moving
    // between our OWN activities briefly bumps this to 2 then back to 1 (the
    // new screen starts before the old one stops), so it only really hits 0
    // when the whole app leaves the foreground - home button, recents,
    // switching to another app.
    private var startedActivityCount = 0

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            // This interface has 7 methods; we only care about 2, so the
            // rest are left empty - an anonymous object still has to
            // implement everything the interface declares.
            override fun onActivityStarted(activity: Activity) {
                startedActivityCount++
            }

            override fun onActivityStopped(activity: Activity) {
                startedActivityCount--
                if (startedActivityCount == 0) {
                    // The whole app just left the foreground - remember when.
                    Prefs.setBackgroundTimestamp(this@FtHangoutsApp, System.currentTimeMillis())
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
