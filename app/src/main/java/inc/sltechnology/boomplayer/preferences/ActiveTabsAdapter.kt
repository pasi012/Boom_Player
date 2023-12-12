package inc.sltechnology.boomplayer.preferences

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import inc.sltechnology.boomplayer.Constants
import inc.sltechnology.boomplayer.Preferences
import inc.sltechnology.boomplayer.R
import inc.sltechnology.boomplayer.databinding.ActiveTabItemBinding
import inc.sltechnology.boomplayer.extensions.updateIconTint
import inc.sltechnology.boomplayer.utils.Theming


class ActiveTabsAdapter: RecyclerView.Adapter<ActiveTabsAdapter.CheckableItemsHolder>() {

    var availableItems = Preferences.getPrefsInstance().activeTabsDef.toMutableList()
    private val mActiveItems = Preferences.getPrefsInstance().activeTabs.toMutableList()

    fun getUpdatedItems() = availableItems.apply {
        Preferences.getPrefsInstance().activeTabsDef = this
    }.minus(availableItems.minus(mActiveItems.toSet()).toSet()) /*make sure to respect tabs order*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckableItemsHolder {
        val binding = ActiveTabItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckableItemsHolder(binding)
    }

    override fun getItemCount() = availableItems.size

    override fun onBindViewHolder(holder: CheckableItemsHolder, position: Int) {
        holder.bindItems()
    }

    inner class CheckableItemsHolder(private val binding: ActiveTabItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindItems() {

            with(binding) {

                val context = root.context
                val disabledColor = Theming.resolveWidgetsColorNormal(context)
                tabText.text = context.getString(getTabText(availableItems[absoluteAdapterPosition]))

                tabImage.setImageResource(Theming.getTabIcon(availableItems[absoluteAdapterPosition]))

                root.isEnabled = availableItems[absoluteAdapterPosition] != Constants.SETTINGS_TAB
                root.isClickable = root.isEnabled

                if (root.isEnabled) {
                    manageTabStatus(
                        context,
                        selected = mActiveItems.contains(availableItems[absoluteAdapterPosition]),
                        tabDragHandle,
                        tabText,
                        tabImage
                    )
                } else {
                    tabDragHandle.updateIconTint(disabledColor)
                    tabText.setTextColor(disabledColor)
                    tabImage.updateIconTint(disabledColor)
                }

                root.setOnClickListener {

                    manageTabStatus(context, selected = !tabImage.isSelected,
                        tabDragHandle,
                        tabText,
                        tabImage
                    )

                    val toggledItem = availableItems[absoluteAdapterPosition]
                    if (!tabImage.isSelected) {
                        mActiveItems.remove(toggledItem)
                    } else {
                        mActiveItems.add(toggledItem)
                    }
                    if (mActiveItems.size < 2) {
                        mActiveItems.add(toggledItem)
                        manageTabStatus(context, selected = true, tabDragHandle, tabText, tabImage)
                    }
                }
            }
        }
    }

    private fun manageTabStatus(
        context: Context,
        selected: Boolean,
        dragHandle: ImageView,
        textView: TextView,
        icon: ImageView
    ) {
        val disabledColor = Theming.resolveWidgetsColorNormal(context)
        icon.isSelected = selected
        val iconColor = if (selected) Theming.resolveThemeColor(icon.resources) else disabledColor
        val textColor = if (selected) {
            Theming.resolveColorAttr(context, android.R.attr.textColorPrimary)
        } else {
            disabledColor
        }
        dragHandle.updateIconTint(textColor)
        textView.setTextColor(textColor)
        icon.updateIconTint(iconColor)
    }

    private fun getTabText(tab: String) = when (tab) {
        Constants.ARTISTS_TAB -> R.string.artists
        Constants.ALBUM_TAB -> R.string.albums
        Constants.SONGS_TAB -> R.string.songs
        Constants.FOLDERS_TAB -> R.string.folders
        else -> R.string.settings
    }
}
