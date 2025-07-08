package com.example.visara.data.mapper

import com.example.visara.data.model.PlaylistModel
import com.example.visara.data.remote.dto.PlayListDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistMapper @Inject constructor() {
    fun toModel(dto: PlayListDto) : PlaylistModel {
        return with(dto) {
            PlaylistModel(
                id = id,
                userId = userId,
                name = name,
                description = description,
                thumbnail = pictureUrl,
                videoIds = videoIdsList,
            )
        }
    }
}
