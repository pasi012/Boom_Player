package inc.sltechnology.boomplayer

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.ConfigurationCompat
import inc.sltechnology.boomplayer.preferences.ContextUtils
import inc.sltechnology.boomplayer.utils.Theming
import java.util.*


abstract class BaseActivity: AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        if (Theming.isThemeBlack(resources)) {
            window?.statusBarColor = Color.BLACK
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        newBase?.let { ctx ->
            // Be sure that prefs are initialized
            Preferences.initPrefs(newBase).locale?.run {
                val locale = Locale.forLanguageTag(this)
                val localeUpdatedContext = ContextUtils.updateLocale(ctx, locale)
                super.attachBaseContext(localeUpdatedContext)
                return
            }
            val sysLocales = ConfigurationCompat.getLocales(Resources.getSystem().configuration)
            sysLocales[0]?.let { defaultLocale ->
                super.attachBaseContext(ContextUtils.updateLocale(ctx, defaultLocale))
                return
            }
            super.attachBaseContext(ContextUtils.updateLocale(ctx, Locale.getDefault()))
        }
    }
}
