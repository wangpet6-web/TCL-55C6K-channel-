package com.example.tclstreamplayer

import android.content.Context

object Prefs {
    private const val FILE = "tcl_stream_player_prefs"
    private const val KEY_PLAYLIST_URL = "playlist_url"
    private const val KEY_LAST_CHANNEL_INDEX = "last_channel_index"

    fun getPlaylistUrl(context: Context): String? {
        val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PLAYLIST_URL, null)
    }

    fun setPlaylistUrl(context: Context, url: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PLAYLIST_URL, url)
            .apply()
    }

    fun getLastChannelIndex(context: Context): Int {
        val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_LAST_CHANNEL_INDEX, 0)
    }

    fun setLastChannelIndex(context: Context, index: Int) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_LAST_CHANNEL_INDEX, index)
            .apply()
    }
}
