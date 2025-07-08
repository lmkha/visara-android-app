package com.example.visara.data.remote.dto

import com.example.visara.data.model.PlaylistModel
import kotlinx.serialization.Serializable

@Serializable
data class PlayListDto(
    val id: String = "",
    val userId: Long = 0L,
    val name: String = "",
    val description: String = "",
    val pictureUrl: String = "",
    val videoIdsList: List<String> = emptyList(),
)
