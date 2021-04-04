package dev.marcos.vod.utils

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.util.MimeTypes
import dev.marcos.vod.utils.Constants.EMPTY_MEDIA_SOURCE
import dev.marcos.vod.utils.Extensions.addOnTop
import dev.marcos.vod.repository.entity.Media
import dev.marcos.vod.repository.entity.Subtitle
import dev.marcos.vod.utils.Constants.NO_SUBTITLE

object Mapper {
    fun List<Media>.toMediaItemsList() = this.map { media ->
        MediaItem.Builder()
            .setUri(media.source)
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(media.title).build()
            )
            .setSubtitles(
                media.subtitle?.addOnTop(
                    Subtitle(
                        lang = null,
                        label = NO_SUBTITLE,
                        source = EMPTY_MEDIA_SOURCE
                    )
                )?.map { subtitle ->
                    MediaItem.Subtitle(
                        Uri.parse(subtitle.source),
                        MimeTypes.APPLICATION_SUBRIP,
                        subtitle.lang,
                        0,
                        0,
                        subtitle.label
                    )
                }
            ).build()
    }
}