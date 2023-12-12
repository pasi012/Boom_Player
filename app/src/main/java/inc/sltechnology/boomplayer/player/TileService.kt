package inc.sltechnology.boomplayer.player

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import inc.sltechnology.boomplayer.Constants
import inc.sltechnology.boomplayer.ui.MainActivity

@RequiresApi(Build.VERSION_CODES.N)
class PlayerTileService : TileService() {

    override fun onClick() {
        super.onClick()
        with(Intent(this, MainActivity::class.java)) {
            putExtra(Constants.LAUNCHED_BY_TILE, Constants.LAUNCHED_BY_TILE)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivityAndCollapse(this)
        }
    }
}
