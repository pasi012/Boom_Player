package inc.sltechnology.boomplayer.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import inc.sltechnology.boomplayer.Constants
import inc.sltechnology.boomplayer.R
import inc.sltechnology.boomplayer.ui.UIControlInterface

object Permissions {

    private val PERMISSION_READ_AUDIO get() = if (Versioning.isTiramisu()) {
        // READ_EXTERNAL_STORAGE was superseded by READ_MEDIA_AUDIO in Android 13
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    @JvmStatic
    fun hasToAskForReadStoragePermission(activity: Activity) =
        Versioning.isMarshmallow() && ContextCompat.checkSelfPermission(
            activity,
            PERMISSION_READ_AUDIO
        ) != PackageManager.PERMISSION_GRANTED

    @JvmStatic
    fun manageAskForReadStoragePermission(
        activity: Activity
    ) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, PERMISSION_READ_AUDIO)) {

            MaterialAlertDialogBuilder(activity)
                .setCancelable(false)
                .setTitle(R.string.app_name)
                .setMessage(R.string.perm_rationale)
                .setPositiveButton(R.string.ok) { _, _ ->
                    askForReadStoragePermission(activity)
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    (activity as UIControlInterface).onDenyPermission()
                }
                .show()
        } else {
            askForReadStoragePermission(
                activity
            )
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun askForReadStoragePermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(PERMISSION_READ_AUDIO),
            Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
        )
    }
}
