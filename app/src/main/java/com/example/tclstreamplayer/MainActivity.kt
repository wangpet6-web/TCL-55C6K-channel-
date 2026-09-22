package com.example.tclstreamplayer

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var grid: RecyclerView
    private lateinit var emptyText: View
    private var channels: List<Channel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        grid = findViewById(R.id.channelGrid)
        emptyText = findViewById(R.id.emptyText)
        grid.layoutManager = GridLayoutManager(this, 5)

        loadPlaylist()
    }

    override fun onResume() {
        super.onResume()
        // Refresh in case the playlist URL changed in Settings
        loadPlaylist()
    }

    private fun loadPlaylist() {
        val url = Prefs.getPlaylistUrl(this)
        if (url.isNullOrBlank()) {
            showEmpty()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { M3uParser.fetch(url) }
            }
            result.onSuccess { list ->
                channels = list
                if (channels.isEmpty()) {
                    showEmpty()
                } else {
                    showChannels()
                }
            }.onFailure {
                showEmpty()
            }
        }
    }

    private fun showChannels() {
        emptyText.visibility = View.GONE
        grid.visibility = View.VISIBLE
        grid.adapter = ChannelAdapter(channels) { channel, index ->
            Prefs.setLastChannelIndex(this, index)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(PlayerActivity.EXTRA_STREAM_URL, channel.streamUrl)
            intent.putExtra(PlayerActivity.EXTRA_CHANNEL_NAME, channel.name)
            startActivity(intent)
        }
    }

    private fun showEmpty() {
        grid.visibility = View.GONE
        emptyText.visibility = View.VISIBLE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // TV remotes typically send KEYCODE_MENU; also support long BACK as a fallback.
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
