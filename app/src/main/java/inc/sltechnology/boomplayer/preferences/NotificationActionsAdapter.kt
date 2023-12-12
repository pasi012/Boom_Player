package inc.sltechnology.boomplayer.preferences

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import inc.sltechnology.boomplayer.Constants
import inc.sltechnology.boomplayer.Preferences
import inc.sltechnology.boomplayer.databinding.NotificationActionsItemBinding
import inc.sltechnology.boomplayer.models.NotificationAction
import inc.sltechnology.boomplayer.utils.Theming


class NotificationActionsAdapter: RecyclerView.Adapter<NotificationActionsAdapter.CheckableItemsHolder>() {

    var selectedActions = Preferences.getPrefsInstance().notificationActions

    private val mActions = listOf(
        NotificationAction(Constants.REPEAT_ACTION, Constants.CLOSE_ACTION), // default
        NotificationAction(Constants.REWIND_ACTION, Constants.FAST_FORWARD_ACTION),
        NotificationAction(Constants.FAVORITE_ACTION, Constants.CLOSE_ACTION),
        NotificationAction(Constants.FAVORITE_POSITION_ACTION, Constants.CLOSE_ACTION)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckableItemsHolder {
        val binding = NotificationActionsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckableItemsHolder(binding)
    }

    override fun getItemCount() = mActions.size

    override fun onBindViewHolder(holder: CheckableItemsHolder, position: Int) {
        holder.bindItems()
    }

    inner class CheckableItemsHolder(private val binding: NotificationActionsItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindItems() {

            with(binding) {

                val context = binding.root.context

                notifAction0.setImageResource(
                    Theming.getNotificationActionIcon(mActions[absoluteAdapterPosition].first, isNotification = false)
                )
                notifAction1.setImageResource(
                    Theming.getNotificationActionIcon(mActions[absoluteAdapterPosition].second, isNotification = false)
                )
                radio.isChecked = selectedActions == mActions[absoluteAdapterPosition]

                root.contentDescription = context.getString(Theming.getNotificationActionTitle(mActions[absoluteAdapterPosition].first))

                root.setOnClickListener {
                    notifyItemChanged(mActions.indexOf(selectedActions))
                    selectedActions = mActions[absoluteAdapterPosition]
                    notifyItemChanged(absoluteAdapterPosition)
                    Preferences.getPrefsInstance().notificationActions = selectedActions
                }

                root.setOnLongClickListener {
                    Toast.makeText(
                        context,
                        Theming.getNotificationActionTitle(mActions[absoluteAdapterPosition].first),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnLongClickListener true
                }
            }
        }
    }
}
