package dev.marcos.vod.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import dev.marcos.vod.R
import dev.marcos.vod.lib.VODPlayer
import dev.marcos.vod.repository.Repository
import dev.marcos.vod.utils.Constants.ZERO
import dev.marcos.vod.utils.Mapper.toMediaItemsList

class PlayerFragment : Fragment() {

    // Repository
    private val listOfMedias = Repository.medias

    // Default Settings
    private var checkedSubtitleItem = ZERO
    private var checkedAudioItem = ZERO
    private var preferredTextLanguage = "pt_BR"
    private var preferredAudioLanguage = "pt"

    // States
    private var playWhenReady = true
    private var currentWindow = ZERO
    private var playbackPosition: Long = 0L

    // Player
    private lateinit var player: VODPlayer
    private lateinit var playerView: PlayerView

    // View
    private lateinit var titleView: AppCompatTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_player, container, false)

        playerView = view.findViewById(R.id.player_view)
        titleView = view.findViewById(R.id.textview_title)

        view.findViewById<AppCompatImageView>(R.id.player_playlist).setOnClickListener {
            playlistDialog()
        }

        view.findViewById<AppCompatTextView>(R.id.player_subtitles).setOnClickListener {
            subtitleDialog()
        }

        view.findViewById<AppCompatTextView>(R.id.player_audios).setOnClickListener {
            audiosDialog()
        }

        view.findViewById<AppCompatImageView>(R.id.player_config).setOnClickListener {
            Toast.makeText(requireContext(), "Still in development ( ^-^)", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlayer()
        setupPlaylist()
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        playWhenReady = player.getPlayWhenReady()
        playbackPosition = player.getCurrentPosition()
        currentWindow = player.getCurrentWindowIndex()
        player.release()
    }

    private fun setupPlayer() {
        player = VODPlayer.Builder(playerView.context)
            .setPlayerView(playerView)
            .setPreferredTextLanguage(language = preferredTextLanguage)
            .setPreferredAudioLanguage(language = preferredAudioLanguage)
            .build()

        playerView.player = player.exoPlayer

        player.addListener(object : Player.EventListener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                updateTitleHeader()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val stateString: String = when (playbackState) {
                    ExoPlayer.STATE_IDLE -> "STATE_IDLE"
                    ExoPlayer.STATE_BUFFERING -> "STATE_BUFFERING "
                    ExoPlayer.STATE_READY -> "STATE_READY"
                    ExoPlayer.STATE_ENDED -> "STATE_ENDED"
                    else -> "UNKNOWN_STATE"
                }
                Log.d(getString(R.string.app_name), "Changed state to $stateString")
            }
        })
    }

    private fun setupPlaylist() = player.addMediaItems(listOfMedias.toMediaItemsList())

    private fun initializePlayer() {
        updateTitleHeader()
        player.setPlayWhenReady(playWhenReady)
        player.seekTo(currentWindow, playbackPosition)
        player.prepare()
        player.play()
    }

    private fun updateTitleHeader() {
        titleView.text = player.getCurrentMediaMetadataTitle()
    }

    private fun playlistDialog() {
        val items = player.getPlaylistMediaMetadataTitles().toTypedArray()
        showDialog(
            initialCheckedItem = currentWindow,
            title = getString(R.string.player_playlist),
            items = items,
            action = { index -> player.seekToPlaylistAtIndex(index) },
            result = { result -> currentWindow = result }
        )
    }

    private fun subtitleDialog() {
        val subtitles = player.getCurrentSubtitles()
        val items = subtitles?.map { it.second }?.toTypedArray()
        val languages = subtitles?.map { it.first }
        checkedSubtitleItem = languages?.indexOfFirst { string -> string == preferredTextLanguage } ?: ZERO
        showDialog(
            initialCheckedItem = checkedSubtitleItem,
            title = getString(R.string.player_subtitle),
            items = items,
            action = { index -> player.setSubtitle(language = languages?.get(index)) },
            result = { result -> checkedSubtitleItem = result }
        )
    }

    private fun audiosDialog() {
        val audios = player.getCurrentAudioTracks()
        val items = audios?.map { it.second }?.toTypedArray()
        val languages = audios?.map { it.first }
        checkedAudioItem = languages?.indexOfFirst { string -> string == preferredAudioLanguage } ?: ZERO
        showDialog(
            initialCheckedItem = checkedAudioItem,
            title = getString(R.string.player_audios),
            items = items,
            action = { index -> player.setAudioTrack(language = languages?.get(index)) },
            result = { result -> checkedAudioItem = result }
        )
    }

    private fun showDialog(
        initialCheckedItem: Int,
        title: String,
        items: Array<String>?,
        action: (index: Int) -> Unit,
        result: (index: Int) -> Unit
    ) {
        var checkedItem = if (initialCheckedItem > ZERO) initialCheckedItem else ZERO
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setSingleChoiceItems(items, checkedItem) { _, which ->
                checkedItem = which
                result(checkedItem)
            }
            .setPositiveButton(R.string.player_dialog_confirm) { _, _ -> action(checkedItem) }
            .setNegativeButton(R.string.player_dialog_cancel, null)
            .create()
            .show()
    }

}