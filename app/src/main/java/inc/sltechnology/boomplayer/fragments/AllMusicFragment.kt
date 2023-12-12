package inc.sltechnology.boomplayer.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import inc.sltechnology.boomplayer.Constants
import inc.sltechnology.boomplayer.Preferences
import inc.sltechnology.boomplayer.MusicViewModel
import inc.sltechnology.boomplayer.R
import inc.sltechnology.boomplayer.databinding.FragmentAllMusicBinding
import inc.sltechnology.boomplayer.databinding.MusicItemBinding
import inc.sltechnology.boomplayer.extensions.setTitleColor
import inc.sltechnology.boomplayer.extensions.toFormattedDate
import inc.sltechnology.boomplayer.extensions.toFormattedDuration
import inc.sltechnology.boomplayer.extensions.toName
import inc.sltechnology.boomplayer.models.Music
import inc.sltechnology.boomplayer.player.MediaPlayerHolder
import inc.sltechnology.boomplayer.ui.MediaControlInterface
import inc.sltechnology.boomplayer.ui.UIControlInterface
import inc.sltechnology.boomplayer.utils.Lists
import inc.sltechnology.boomplayer.utils.Popups
import inc.sltechnology.boomplayer.utils.Theming
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import me.zhanghai.android.fastscroll.PopupTextProvider


/**
 * A simple [Fragment] subclass.
 * Use the [AllMusicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllMusicFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _allMusicFragmentBinding: FragmentAllMusicBinding? = null
    private lateinit var mUIControlInterface: UIControlInterface
    private lateinit var mMediaControlInterface: MediaControlInterface

    // view model
    private lateinit var mMusicViewModel: MusicViewModel

    // sorting
    private lateinit var mSortMenuItem: MenuItem
    private var mSorting = Preferences.getPrefsInstance().allMusicSorting

    private var mAllMusic: List<Music>? = null

    private val sIsFastScrollerPopup get() = (mSorting == Constants.ASCENDING_SORTING || mSorting == Constants.DESCENDING_SORTING) && Preferences.getPrefsInstance().songsVisualization != Constants.FN

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mUIControlInterface = activity as UIControlInterface
            mMediaControlInterface = activity as MediaControlInterface
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _allMusicFragmentBinding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _allMusicFragmentBinding = FragmentAllMusicBinding.inflate(inflater, container, false)
        return _allMusicFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMusicViewModel =
            ViewModelProvider(requireActivity())[MusicViewModel::class.java].apply {
                deviceMusic.observe(viewLifecycleOwner) { returnedMusic ->
                    if (!returnedMusic.isNullOrEmpty()) {
                        mAllMusic = Lists.getSortedMusicListForAllMusic(
                            mSorting,
                            mMusicViewModel.deviceMusicFiltered
                        )
                        finishSetup()
                    }
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setMusicDataSource(musicList: List<Music>?) {
        musicList?.run {
            mAllMusic = this
            _allMusicFragmentBinding?.allMusicRv?.adapter?.notifyDataSetChanged()
        }
    }

    private fun finishSetup() {

        _allMusicFragmentBinding?.run {

            allMusicRv.adapter = AllMusicAdapter()

            FastScrollerBuilder(allMusicRv).useMd2Style().build()

            shuffleFab.text = mAllMusic?.size.toString()
            val fabColor = ColorUtils.blendARGB(
                Theming.resolveColorAttr(requireContext(), R.attr.toolbar_bg),
                Theming.resolveThemeColor(resources),
                0.10f
            )
            shuffleFab.backgroundTintList = ColorStateList.valueOf(fabColor)
            shuffleFab.setOnClickListener {
                mMediaControlInterface.onSongsShuffled(
                    mAllMusic,
                    Constants.ARTIST_VIEW
                )
            }

            searchToolbar.let { stb ->

                stb.inflateMenu(R.menu.menu_music_search)
                stb.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_sort)
                stb.setNavigationOnClickListener {
                    mUIControlInterface.onCloseActivity()
                }

                with(stb.menu) {

                    mSortMenuItem = Lists.getSelectedSortingForMusic(mSorting, this).apply {
                        setTitleColor(Theming.resolveThemeColor(resources))
                    }

                    with (findItem(R.id.action_search).actionView as SearchView) {
                        setOnQueryTextListener(this@AllMusicFragment)
                        setOnQueryTextFocusChangeListener { _, hasFocus ->
                            stb.menu.setGroupVisible(R.id.sorting, !hasFocus)
                            stb.menu.findItem(R.id.sleeptimer).isVisible = !hasFocus
                        }
                    }

                    setMenuOnItemClickListener(stb.menu)
                }
            }
        }

        tintSleepTimerIcon(enabled = MediaPlayerHolder.getInstance().isSleepTimer)
    }

    fun tintSleepTimerIcon(enabled: Boolean) {
        _allMusicFragmentBinding?.searchToolbar?.run {
            Theming.tintSleepTimerMenuItem(this, enabled)
        }
    }

    private fun setMenuOnItemClickListener(menu: Menu) {

        _allMusicFragmentBinding?.searchToolbar?.setOnMenuItemClickListener {

            if (it.itemId == R.id.default_sorting || it.itemId == R.id.ascending_sorting
                || it.itemId == R.id.descending_sorting || it.itemId == R.id.date_added_sorting
                || it.itemId == R.id.date_added_sorting_inv || it.itemId == R.id.artist_sorting
                || it.itemId == R.id.artist_sorting_inv || it.itemId == R.id.album_sorting
                || it.itemId == R.id.album_sorting_inv) {

                mSorting = it.order
                mAllMusic = Lists.getSortedMusicListForAllMusic(mSorting, mAllMusic)

                setMusicDataSource(mAllMusic)

                mSortMenuItem.setTitleColor(
                    Theming.resolveColorAttr(requireContext(), android.R.attr.textColorPrimary)
                )

                mSortMenuItem = Lists.getSelectedSortingForMusic(mSorting, menu).apply {
                    setTitleColor(Theming.resolveThemeColor(resources))
                }

                Preferences.getPrefsInstance().allMusicSorting = mSorting

            } else if (it.itemId != R.id.action_search) {
                mUIControlInterface.onOpenSleepTimerDialog()
            }

            return@setOnMenuItemClickListener true
        }
    }

    fun onSongVisualizationChanged() = if (_allMusicFragmentBinding != null) {
        mAllMusic = Lists.getSortedMusicListForAllMusic(mSorting, mAllMusic)
        setMusicDataSource(mAllMusic)
        true
    } else {
        false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        setMusicDataSource(
            Lists.processQueryForMusic(newText,
                Lists.getSortedMusicListForAllMusic(mSorting, mMusicViewModel.deviceMusicFiltered)
            ) ?: mAllMusic)
        return false
    }

    override fun onQueryTextSubmit(query: String?) = false

    private inner class AllMusicAdapter : RecyclerView.Adapter<AllMusicAdapter.SongsHolder>(), PopupTextProvider {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsHolder {
            val binding = MusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SongsHolder(binding)
        }

        override fun getPopupText(position: Int): String {
            if (sIsFastScrollerPopup) {
                mAllMusic?.get(position)?.title?.run {
                    if (isNotEmpty()) return first().toString()
                }
            }
            return ""
        }

        override fun getItemCount(): Int {
            return mAllMusic?.size!!
        }

        override fun onBindViewHolder(holder: SongsHolder, position: Int) {
            holder.bindItems(mAllMusic?.get(holder.absoluteAdapterPosition))
        }

        inner class SongsHolder(private val binding: MusicItemBinding): RecyclerView.ViewHolder(binding.root) {

            fun bindItems(itemSong: Music?) {

                with(binding) {

                    val formattedDuration = itemSong?.duration?.toFormattedDuration(
                        isAlbum = false,
                        isSeekBar = false
                    )

                    duration.text = getString(R.string.duration_date_added, formattedDuration,
                        itemSong?.dateAdded?.toFormattedDate())
                    title.text = itemSong.toName()
                    subtitle.text =
                        getString(R.string.artist_and_album, itemSong?.artist, itemSong?.album)

                    root.setOnClickListener {
                        with(MediaPlayerHolder.getInstance()) {
                            if (isCurrentSongFM) currentSongFM = null
                        }
                        mMediaControlInterface.onSongSelected(
                            itemSong,
                            mAllMusic,
                            Constants.ARTIST_VIEW
                        )
                    }

                    root.setOnLongClickListener {
                        val vh = _allMusicFragmentBinding?.allMusicRv?.findViewHolderForAdapterPosition(absoluteAdapterPosition)
                        Popups.showPopupForSongs(
                            requireActivity(),
                            vh?.itemView,
                            itemSong,
                            Constants.ARTIST_VIEW
                        )
                        return@setOnLongClickListener true
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AllMusicFragment.
         */
        @JvmStatic
        fun newInstance() = AllMusicFragment()
    }
}
