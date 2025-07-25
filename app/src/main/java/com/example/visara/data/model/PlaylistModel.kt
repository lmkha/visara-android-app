package com.example.visara.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistModel(
    val id: String = "",
    val name: String = "",
    val userId: Long = 0L,
    val description: String = "",
    val thumbnail: String = "",
    val videoIds: List<String> = emptyList(),
)
