//package com.example.visara.data.remote.api
//
//import com.example.visara.BuildConfig
//import com.example.visara.data.remote.dto.VideoDto
//import com.example.visara.data.remote.response.ApiResponse
//import com.example.visara.di.AuthorizedOkHttpClient
//import com.example.visara.di.UnauthenticatedOkhttpClient
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import okhttp3.HttpUrl
//import okhttp3.HttpUrl.Companion.toHttpUrl
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import javax.inject.Inject
//
//class VideoApi @Inject constructor(
//    @AuthorizedOkHttpClient private val authorizedOkHttpClient: OkHttpClient,
//    @UnauthenticatedOkhttpClient private val unauthorizedOkHttpClient: OkHttpClient,
//    private val gson: Gson,
//) {
//    fun getRandomVideos(numOfVideos: Int = 10) : ApiResponse<List<VideoDto>>? {
//        val url: HttpUrl = BuildConfig.BASE_URL.toHttpUrl().newBuilder()
//            .addPathSegments("videos/random")
//            .addQueryParameter("count", numOfVideos.toString())
//            .build()
//
//        val request: Request = Request.Builder()
//            .url(url)
//            .get()
//            .build()
//
//        unauthorizedOkHttpClient.newCall(request).execute().use { response->
//            val responseBody = response.body?.string()
//            if (!response.isSuccessful) {
//                println("API Error: ${response.code} - ${response.message} - $responseBody")
//                return null
//            }
//            if (responseBody.isNullOrEmpty()) return null
//            return gson.fromJson(responseBody, object : TypeToken<ApiResponse<List<VideoDto>>>() {}.type)
//        }
//    }
//}
