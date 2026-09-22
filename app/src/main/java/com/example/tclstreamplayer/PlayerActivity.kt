package com.example.tclstreamplayer

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STREAM_URL = "extra_stream_url"
        const val EXTRA_CHANNEL_NAME = "extra_channel_name"
    }

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val streamUrl = intent.getStringExtra(EXTRA_STREAM_URL)
        val channelName = intent.getStringExtra(EXTRA_CHANNEL_NAME) ?: ""

        findViewById<TextView>(R.id.channelLabel).text = channelName

        if (streamUrl.isNullOrBlank()) {
            Toast.makeText(this, "Invalid channel URL", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        initializePlayer(streamUrl)
    }

    private fun initializePlayer(streamUrl: String) {
        val exoPlayer = ExoPlayer.Builder(this).build()
        player = exoPlayer

        val playerView = findViewById<PlayerView>(R.id.playerView)
        playerView.player = exoPlayer

        val mediaItem = MediaItem.fromUri(streamUrl)
        exoPlayer.setMediaItem(mediaItem)

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Toast.makeText(
                    this@PlayerActivity,
                    "Playback error: ${error.errorCodeName}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
