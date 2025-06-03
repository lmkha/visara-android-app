package com.example.visara.data.model

data class PlaylistModel(
    val id: String = "",
    val name: String = "",
    val userId: Long = 0L,
    val description: String = "",
    val thumbnail: String = "",
    val videoIds: List<String> = emptyList(),
)
