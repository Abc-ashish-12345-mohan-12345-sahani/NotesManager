package android.notesmanager.ui.theme

import android.app.Application
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs.Builder().setContext(this).build()
    }
}