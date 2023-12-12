package inc.sltechnology.boomplayer.equalizer

import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import inc.sltechnology.boomplayer.BaseActivity
import inc.sltechnology.boomplayer.databinding.ActivityEqualizerBinding
import inc.sltechnology.boomplayer.extensions.applyEdgeToEdge
import inc.sltechnology.boomplayer.utils.Theming


class EqualizerActivity : BaseActivity() {

    private lateinit var mEqualizerBinding: ActivityEqualizerBinding

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishAndRemoveTask()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(Theming.resolveTheme(this))

        mEqualizerBinding = ActivityEqualizerBinding.inflate(layoutInflater)
        setContentView(mEqualizerBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            mEqualizerBinding.root.applyEdgeToEdge()
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}
