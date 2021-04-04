package dev.marcos.vod.lib

import android.content.Context
import android.view.View
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView

class VODPlayer(
    context: Context,
    private val playerView: PlayerView?,
    private var preferredAudioLanguage: String? = null,
    private var preferredTextLanguage: String? = null
) {

    private val trackSelector = DefaultTrackSelector(context)

    val exoPlayer: SimpleExoPlayer = SimpleExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .build()

    data class Builder(var _context: Context) {
        private var _playerView: PlayerView? = null
        private var _preferredAudioLanguage: String? = null
        private var _preferredTextLanguage: String? = null

        fun setPlayerView(playerView: PlayerView) = apply {
            _playerView = playerView
        }

        fun setPreferredAudioLanguage(language: String) = apply {
            _preferredAudioLanguage = language
        }

        fun setPreferredTextLanguage(language: String) = apply {
            _preferredTextLanguage = language
        }

        fun setPreferredLanguage(audioLanguage: String, textLanguage: String) = apply {
            _preferredAudioLanguage = audioLanguage
            _preferredTextLanguage = textLanguage
        }

        fun build() = VODPlayer(
            context = _context,
            playerView = _playerView,
            preferredAudioLanguage = _preferredAudioLanguage,
            preferredTextLanguage = _preferredTextLanguage
        )
    }

    fun play() {
        exoPlayer.play()
        updateAudioOrSubtitle(preferredAudioLanguage, preferredTextLanguage)
    }

    fun setPlayWhenReady(playWhenReady: Boolean) {
        exoPlayer.playWhenReady = playWhenReady
    }

    fun release() = exoPlayer.release()
    fun prepare() = exoPlayer.prepare()
    fun getPlayWhenReady() = exoPlayer.playWhenReady
    fun getCurrentPosition() = exoPlayer.currentPosition
    fun getCurrentWindowIndex() = exoPlayer.currentWindowIndex
    fun addMediaItems(list: List<MediaItem>) = exoPlayer.addMediaItems(list)
    fun addListener(listener: Player.EventListener) = exoPlayer.addListener(listener)
    fun seekToPlaylistAtIndex(windowIndex: Int) = exoPlayer.seekTo(windowIndex, 0)
    fun seekTo(windowIndex: Int, positionMs: Long) = exoPlayer.seekTo(windowIndex, positionMs)
    fun getCurrentMediaMetadataTitle() = exoPlayer.currentMediaItem?.mediaMetadata?.title ?: EMPTY_CHAR

    fun getPlaylistMediaMetadataTitles(): List<String> {
        val items = mutableListOf<String>()
        for (index in 0 until exoPlayer.mediaItemCount) {
            items.add(exoPlayer.getMediaItemAt(index).mediaMetadata.title ?: EMPTY_CHAR)
        }
        return items
    }

    fun getCurrentSubtitles(): List<Pair<String, String>>? {
        val subtitles = exoPlayer.currentMediaItem?.playbackProperties?.subtitles
        val items = mutableListOf<Pair<String, String>>()
        subtitles?.forEach {
            val label = it.label ?: EMPTY_CHAR
            val lang = it.language ?: EMPTY_CHAR
            items.add(Pair(lang, label))
        }
        return items
    }

    fun getCurrentAudioTracks(): List<Pair<String, String>>? {
        val audios = exoPlayer.currentTrackGroups
        val items = mutableListOf<Pair<String, String>>()
        for (index in 0 until audios.length) {
            with(audios[index].getFormat(0)) {
                val format = sampleMimeType
                val language = language
                if (format != null && format.contains("audio") && id != null && language != null) {
                    val label = Utils.getLanguageTagBCP47(langCode = language)?.second ?: UNDEFINED
                    val lang = language
                    items.add(Pair(lang, label))
                }
            }
        }
        return items
    }

    fun setAudioTrack(language: String?) {
        updateAudioOrSubtitle(audioLanguage = language)
    }

    fun setSubtitle(language: String?) {
        when (language) {
            null -> hideSubtitleView()
            else -> showSubtitleView()
        }
        updateAudioOrSubtitle(textLanguage = language)

//        val dataSourceFactory = DefaultDataSourceFactory(context)
//
//        val videoUri = getCurrentMediaItem()?.playbackProperties?.uri ?: return
//
//        val subtitleUri =
//            getCurrentMediaItem()?.playbackProperties?.subtitles?.getOrNull(subtitleIndex)?.uri
//                ?: return
//
//        val contentMediaSource = ExtractorMediaSource(
//            videoUri,
//            dataSourceFactory,
//            DefaultExtractorsFactory(),
//            Handler(),
//            null
//        )
//
//        val subtitleSource = SingleSampleMediaSource(
//            subtitleUri, dataSourceFactory,
//            Format.Builder()
//                .setId(null)
//                .setLanguage(Locale.getDefault().toLanguageTag())
//                .setSelectionFlags(Format.NO_VALUE)
//                .setSampleMimeType(MimeTypes.APPLICATION_SUBRIP)
//                .build(),
//            C.TIME_UNSET
//        )
//
//        val sub =
//            getCurrentMediaItem()?.playbackProperties?.subtitles?.getOrNull(subtitleIndex) ?: return
//
//        val subtitleSource = SingleSampleMediaSource.Factory(dataSourceFactory)
//            .createMediaSource(sub, exoPlayer.duration)
//
//
//        val mediaSource: MediaSource = MergingMediaSource(contentMediaSource, subtitleSource)
//
//        val contentSeekPosition = exoPlayer.currentPosition
//        val currentIndex = exoPlayer.currentWindowIndex
//
//        exoPlayer.addMediaSource(currentIndex + 1, mediaSource)
//        exoPlayer.removeMediaItem(currentIndex)
//        exoPlayer.prepare()
//        exoPlayer.seekTo(contentSeekPosition)
//        exoPlayer.playWhenReady = true

    }

    private fun updateAudioOrSubtitle(
        audioLanguage: String? = preferredAudioLanguage,
        textLanguage: String? = preferredTextLanguage
    ) {
        preferredAudioLanguage = audioLanguage
        preferredTextLanguage = textLanguage
        trackSelector.parameters = trackSelector
            .buildUponParameters()
            .setPreferredAudioLanguage(preferredAudioLanguage)
            .setPreferredTextLanguage(preferredTextLanguage)
            .build()
    }

    private fun hideSubtitleView() {
        playerView?.subtitleView?.visibility = View.GONE
    }

    private fun showSubtitleView() {
        playerView?.subtitleView?.visibility = View.VISIBLE
    }

    companion object {
        private const val EMPTY_CHAR = ""
        private const val UNDEFINED = "Undefined"
    }
}

