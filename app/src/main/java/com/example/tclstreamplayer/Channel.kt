package com.example.tclstreamplayer

data class Channel(
    val name: String,
    val streamUrl: String,
    val logoUrl: String? = null,
    val group: String? = null
)
