package inc.sltechnology.boomplayer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import inc.sltechnology.boomplayer.utils.Theming


class App : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        Preferences.initPrefs(applicationContext)
        AppCompatDelegate.setDefaultNightMode(Theming.getDefaultNightMode(applicationContext))
    }

    override fun newImageLoader() = ImageLoader.Builder(this)
        .diskCachePolicy(CachePolicy.DISABLED)
        .crossfade(true)
        .build()
}
