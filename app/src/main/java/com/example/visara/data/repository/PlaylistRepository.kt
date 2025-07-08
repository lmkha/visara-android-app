package com.example.visara.data.repository

import com.example.visara.data.mapper.PlaylistMapper
import com.example.visara.data.model.PlaylistModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.PlaylistRemoteDatasource
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val playlistRemoteDatasource: PlaylistRemoteDatasource,
    private val playlistMapper: PlaylistMapper,
) {
    suspend fun createPlaylist(name: String, description: String, videoIdsList: List<String>) : PlaylistModel? {
        val apiResult = playlistRemoteDatasource.createPlaylist(name, description, videoIdsList)
        if (apiResult !is ApiResult.Success || apiResult.data == null) return null
        return playlistMapper.toModel(apiResult.data)
    }
    suspend fun uploadThumbnailForPlaylist(playlistId: String, thumbnailFile: File) : Boolean {
        val apiResult = playlistRemoteDatasource.uploadThumbnailForPlaylist(playlistId, thumbnailFile)
        return apiResult is ApiResult.Success
    }
    suspend fun getPlaylistById(playlistId: String) : PlaylistModel? {
        val apiResult = playlistRemoteDatasource.getPlaylistById(playlistId)
        if (apiResult !is ApiResult.Success || apiResult.data == null) return null
        return playlistMapper.toModel(apiResult.data)
    }
    suspend fun addVideoToPlaylist(playlistId: String, videoId: String) : Boolean {
        val apiResult = playlistRemoteDatasource.addVideoToPlaylist(playlistId, videoId)
        return apiResult is ApiResult.Success && apiResult.data == true
    }
    suspend fun removeVideoFromPlaylist(playlistId: String, videoId: String) : Boolean {
        val apiResult = playlistRemoteDatasource.removeVideoFromPlaylist(playlistId, videoId)
        return apiResult is ApiResult.Success && apiResult.data == true
    }
    suspend fun getAllPlaylistByUserId(userId: Long) : List<PlaylistModel> {
        val apiResult = playlistRemoteDatasource.getAllPlaylistByUserId(userId)
        if (apiResult !is ApiResult.Success || apiResult.data == null) return emptyList()
        return apiResult.data.map { playlistMapper.toModel(it) }
    }
}