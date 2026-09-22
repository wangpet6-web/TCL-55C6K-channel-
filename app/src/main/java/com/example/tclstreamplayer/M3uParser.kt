package com.example.tclstreamplayer

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Minimal M3U / M3U8 (EXTM3U) playlist parser.
 * Supports the common #EXTINF line format:
 *   #EXTINF:-1 tvg-logo="https://..." group-title="News",Channel Name
 *   http://stream-url
 */
object M3uParser {

    private val client = OkHttpClient()

    @Throws(IOException::class)
    fun fetch(url: String): List<Channel> {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to fetch playlist: HTTP ${response.code}")
            }
            val body = response.body?.string() ?: ""
            return parse(body)
        }
    }

    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()

        var pendingName: String? = null
        var pendingLogo: String? = null
        var pendingGroup: String? = null

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) continue

            if (line.startsWith("#EXTINF")) {
                val commaIndex = line.indexOf(',')
                pendingName = if (commaIndex != -1) line.substring(commaIndex + 1).trim() else "Unnamed Channel"
                pendingLogo = Regex("tvg-logo=\"([^\"]*)\"").find(line)?.groupValues?.get(1)
                pendingGroup = Regex("group-title=\"([^\"]*)\"").find(line)?.groupValues?.get(1)
            } else if (!line.startsWith("#")) {
                // This line is a stream URL following the preceding #EXTINF (if any)
                val name = pendingName ?: line
                channels.add(
                    Channel(
                        name = name,
                        streamUrl = line,
                        logoUrl = pendingLogo,
                        group = pendingGroup
                    )
                )
                pendingName = null
                pendingLogo = null
                pendingGroup = null
            }
        }
        return channels
    }
}
