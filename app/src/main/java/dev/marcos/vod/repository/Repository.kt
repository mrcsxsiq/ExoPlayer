package dev.marcos.vod.repository

import dev.marcos.vod.repository.entity.Media
import dev.marcos.vod.repository.entity.Subtitle

object Repository {

    val medias = listOf(
        Media(
            title = "Filme 1",
            cover = "",
            source = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
            subtitle = listOf(
                Subtitle(
                    lang = "pt_BR",
                    label = "Portugues",
                    source = "https://raw.githubusercontent.com/amiraelsayed/cv/master/subtitle.srt",
                ),
                Subtitle(
                    lang = "en",
                    label = "Inglês",
                    source = "https://raw.githubusercontent.com/amiraelsayed/cv/master/subtitle.srt",
                ),
                Subtitle(
                    lang = "fr",
                    label = "Francês",
                    source = "https://raw.githubusercontent.com/amiraelsayed/cv/master/french.srt",
                )
            )
        ),
        Media(
            title = "Filme 2 - Harry Potter",
            cover = "",
            source = "https://slave1.odeonvod.com/?path=L21udC9zZXJ2aWRvcjAxL0cvRmlsbWVzL0ZpbG1lcyBEdWFsL0Fub3MgMjAwMC9IYXJyeSBQb3R0ZXIgZSBhcyBSZWzDrXF1aWFzIGRhIE1vcnRlIC0gUGFydGUgMiAoMjAxMSkvSGFycnkgUG90dGVyIGUgYXMgUmVsw61xdWlhcyBkYSBNb3J0ZSAtIFBhcnRlIDIgKDIwMTEpLm1wNA==",
            subtitle = listOf(
                Subtitle(
                    lang = "pt_BR",
                    label = "Português",
                    source = "https://sistema.odeonvod.com/legenda/6a00c0b3-7abe-41f2-8431-1a50be285062",
                ),
                Subtitle(
                    lang = "pt_BR",
                    label = "Espanhol",
                    source = "https://raw.githubusercontent.com/amiraelsayed/cv/master/subtitle.srt",
                ),
                Subtitle(
                    lang = "jp",
                    label = "Japonês",
                    source = "https://raw.githubusercontent.com/amiraelsayed/cv/master/french.srt",
                )
            )
        ),
        Media(
            title = "Filme 3",
            cover = "",
            source = "https://sistema.odeonvod.com/midia/8f9fadfc-e995-41de-926c-1ffb9637db99",
            subtitle = listOf(
                Subtitle(
                    lang = "ru",
                    label = "Russo",
                    source = "Português",
                ),
                Subtitle(
                    lang = "cn",
                    label = "Chinês",
                    source = "https://raw.githubusercontent.com/amiraelsayed/cv/master/subtitle.srt",
                )
            )
        )
    )
}