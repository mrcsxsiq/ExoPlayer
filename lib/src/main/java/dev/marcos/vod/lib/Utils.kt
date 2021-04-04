package dev.marcos.vod.lib

import java.util.Locale

object Utils {
    fun getLanguageTagBCP47(langCode: String? = null, country: String? = null): Pair<String, String>? {
        for (locale in Locale.getAvailableLocales()) {
            val code = locale.language + "_" + locale.country
            val displayName = locale.displayName
            if (langCode == code || langCode == locale.language || country == displayName)
                return Pair(code, locale.displayName)
        }
        return null
    }
}