package inc.sltechnology.boomplayer.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import inc.sltechnology.boomplayer.Constants
import inc.sltechnology.boomplayer.Preferences
import inc.sltechnology.boomplayer.R
import inc.sltechnology.boomplayer.extensions.setIconTint
import inc.sltechnology.boomplayer.player.MediaPlayerHolder
import inc.sltechnology.boomplayer.ui.MainActivity


object Theming {

    @JvmStatic
    fun applyChanges(activity: Activity, currentViewPagerItem: Int) {
        with(activity) {
            finishAfterTransition()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(Constants.RESTORE_FRAGMENT, currentViewPagerItem)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    @JvmStatic
    fun getDefaultNightMode(context: Context) = when (Preferences.getPrefsInstance().theme) {
        context.getString(R.string.theme_pref_light) -> AppCompatDelegate.MODE_NIGHT_NO
        context.getString(R.string.theme_pref_dark) -> AppCompatDelegate.MODE_NIGHT_YES
        else -> if (Versioning.isQ()) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        } else {
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
    }

    @JvmStatic
    fun isThemeNight(resources: Resources): Boolean {
        val uiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    @JvmStatic
    fun isThemeBlack(resources: Resources) =
        isThemeNight(resources) && Preferences.getPrefsInstance().isBlackTheme

    fun getSortIconForSongs(sort: Int): Int {
        return when (sort) {
            Constants.ASCENDING_SORTING -> R.drawable.ic_sort_alphabetical_descending
            Constants.DESCENDING_SORTING -> R.drawable.ic_sort_alphabetical_ascending
            Constants.TRACK_SORTING -> R.drawable.ic_sort_numeric_descending
            else -> R.drawable.ic_sort_numeric_ascending
        }
    }

    fun getSortIconForSongsDisplayName(sort: Int): Int {
        return when (sort) {
            Constants.ASCENDING_SORTING -> R.drawable.ic_sort_alphabetical_descending
            else -> R.drawable.ic_sort_alphabetical_ascending
        }
    }

    @JvmStatic
    fun isDeviceLand(resources: Resources) =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    @JvmStatic
    fun getAccentName(resources: Resources, position: Int): String {
        val accentNames = resources.getStringArray(R.array.accent_names)
        return accentNames[position]
    }

    @JvmStatic
    private val styles = arrayOf(
        R.style.BaseTheme_Red,
        R.style.BaseTheme_Pink,
        R.style.BaseTheme_Purple,
        R.style.BaseTheme_DeepPurple,
        R.style.BaseTheme_Indigo,
        R.style.BaseTheme_Blue,
        R.style.BaseTheme_LightBlue,
        R.style.BaseTheme_Cyan,
        R.style.BaseTheme_Teal,
        R.style.BaseTheme_Green,
        R.style.BaseTheme_LightGreen,
        R.style.BaseTheme_Lime,
        R.style.BaseTheme_Yellow,
        R.style.BaseTheme_Amber,
        R.style.BaseTheme_Orange,
        R.style.BaseTheme_DeepOrange,
        R.style.BaseTheme_Brown,
        R.style.BaseTheme_Grey,
        R.style.BaseTheme_BlueGrey
    )

    @JvmStatic
    private val stylesBlack = arrayOf(
        R.style.BaseTheme_Black_Red,
        R.style.BaseTheme_Black_Pink,
        R.style.BaseTheme_Black_Purple,
        R.style.BaseTheme_Black_DeepPurple,
        R.style.BaseTheme_Black_Indigo,
        R.style.BaseTheme_Black_Blue,
        R.style.BaseTheme_Black_LightBlue,
        R.style.BaseTheme_Black_Cyan,
        R.style.BaseTheme_Black_Teal,
        R.style.BaseTheme_Black_Green,
        R.style.BaseTheme_Black_LightGreen,
        R.style.BaseTheme_Black_Lime,
        R.style.BaseTheme_Black_Yellow,
        R.style.BaseTheme_Black_Amber,
        R.style.BaseTheme_Black_Orange,
        R.style.BaseTheme_Black_DeepOrange,
        R.style.BaseTheme_Black_Brown,
        R.style.BaseTheme_Black_Grey,
        R.style.BaseTheme_Black_BlueGrey
    )

    @JvmStatic
    fun resolveTheme(context: Context): Int {
        val position = Preferences.getPrefsInstance().accent
        if (isThemeBlack(context.resources)) return stylesBlack[position]
        return styles[position]
    }

    @ColorInt
    @JvmStatic
    fun resolveThemeColor(resources: Resources): Int {
        val position = Preferences.getPrefsInstance().accent
        val colors = resources.getIntArray(R.array.colors)
        return colors[position]
    }

    @ColorInt
    @JvmStatic
    fun resolveWidgetsColorNormal(context: Context) = resolveColorAttr(context,
        android.R.attr.colorButtonNormal)

    @ColorInt
    @JvmStatic
    fun resolveColorAttr(context: Context, @AttrRes colorAttr: Int): Int {
        val resolvedAttr: TypedValue = resolveThemeAttr(context, colorAttr)
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        val colorRes =
            if (resolvedAttr.resourceId != 0) {
                resolvedAttr.resourceId
            } else {
                resolvedAttr.data
            }
        return ContextCompat.getColor(context, colorRes)
    }

    @JvmStatic
    private fun resolveThemeAttr(context: Context, @AttrRes attrRes: Int) =
        TypedValue().apply { context.theme.resolveAttribute(attrRes, this, true) }

    @JvmStatic
    fun getAlbumCoverAlpha(context: Context): Int {
        return when {
            isThemeBlack(context.resources) -> 25
            isThemeNight(context.resources) -> 15
            else -> 20
        }
    }

    @JvmStatic
    fun getTabIcon(tab: String) = when (tab) {
        Constants.ARTISTS_TAB -> R.drawable.ic_artist
        Constants.ALBUM_TAB -> R.drawable.ic_library_music
        Constants.SONGS_TAB -> R.drawable.ic_music_note
        Constants.FOLDERS_TAB -> R.drawable.ic_folder_music
        else -> R.drawable.ic_settings
    }

    @JvmStatic
    fun getTabAccessibilityText(tab: String) = when (tab) {
        Constants.ARTISTS_TAB -> R.string.artists
        Constants.ALBUM_TAB -> R.string.albums
        Constants.SONGS_TAB -> R.string.songs
        Constants.FOLDERS_TAB -> R.string.folders
        else -> R.string.settings
    }

    @JvmStatic
    fun getPreciseVolumeIcon(volume: Int) = when (volume) {
        in 1..33 -> R.drawable.ic_volume_mute
        in 34..67 -> R.drawable.ic_volume_down
        in 68..100 -> R.drawable.ic_volume_up
        else -> R.drawable.ic_volume_off
    }

    @JvmStatic
    fun getNotificationActionTitle(action: String) = when (action) {
        Constants.REPEAT_ACTION -> R.string.notification_actions_repeat
        Constants.REWIND_ACTION -> R.string.notification_actions_fast_seeking
        Constants.FAVORITE_ACTION -> R.string.notification_actions_favorite
        else -> R.string.notification_actions_favorite_position
    }

    @JvmStatic
    fun getNotificationActionIcon(action: String, isNotification: Boolean): Int {
        val mediaPlayerHolder = MediaPlayerHolder.getInstance()
        return when (action) {
            Constants.PLAY_PAUSE_ACTION -> if (mediaPlayerHolder.isPlaying) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }
            Constants.REPEAT_ACTION -> if (isNotification) {
                getRepeatIcon(isNotification = true)
            } else {
                R.drawable.ic_repeat
            }
            Constants.PREV_ACTION -> R.drawable.ic_skip_previous
            Constants.NEXT_ACTION -> R.drawable.ic_skip_next
            Constants.CLOSE_ACTION -> R.drawable.ic_close
            Constants.FAST_FORWARD_ACTION -> R.drawable.ic_fast_forward
            Constants.REWIND_ACTION -> R.drawable.ic_fast_rewind
            Constants.FAVORITE_ACTION -> if (isNotification) {
                getFavoriteIcon(isNotification = true)
            } else {
                R.drawable.ic_favorite
            }
            else -> R.drawable.ic_save_time
        }
    }

    @JvmStatic
    fun getRepeatIcon(isNotification: Boolean): Int {
        val mediaPlayerHolder = MediaPlayerHolder.getInstance()
        return when {
            mediaPlayerHolder.isRepeat1X -> R.drawable.ic_repeat_one
            mediaPlayerHolder.isLooping -> R.drawable.ic_repeat
            else -> if (isNotification) {
                R.drawable.ic_repeat_one_disabled_alt
            } else {
                R.drawable.ic_repeat_one_disabled
            }
        }
    }

    @JvmStatic
    fun getFavoriteIcon(isNotification: Boolean): Int {
        val mediaPlayerHolder = MediaPlayerHolder.getInstance()
        val favorites = Preferences.getPrefsInstance().favorites
        val isFavorite = favorites != null && favorites.contains(
            mediaPlayerHolder.currentSong?.copy(startFrom = 0, launchedBy = mediaPlayerHolder.launchedBy))
        return if (isFavorite) {
            R.drawable.ic_favorite
        } else {
            if (isNotification) {
                R.drawable.ic_favorite_empty_alt
            } else {
                R.drawable.ic_favorite_empty
            }
        }
    }

    @JvmStatic
    fun tintSleepTimerMenuItem(tb: MaterialToolbar, isEnabled: Boolean) {
        tb.menu.findItem(R.id.sleeptimer).setIconTint(if (isEnabled) {
            resolveThemeColor(tb.resources)
        } else {
            ContextCompat.getColor(tb.context, R.color.widgets_color)
        })
    }

    @JvmStatic
    fun getOrientation(): Int {
        if (Preferences.getPrefsInstance().lockRotation) {
            return ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }
        return ActivityInfo.SCREEN_ORIENTATION_FULL_USER
    }
}
