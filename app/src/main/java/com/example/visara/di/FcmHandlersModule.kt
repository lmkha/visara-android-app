package com.example.visara.di

import com.example.visara.service.fcm.RemoteNotificationType
import com.example.visara.service.fcm.handlers.FcmCommentLikeHandler
import com.example.visara.service.fcm.handlers.FcmCommentOnVideoHandler
import com.example.visara.service.fcm.handlers.FcmNewChatMessageHandler
import com.example.visara.service.fcm.handlers.FcmNewFollowerHandler
import com.example.visara.service.fcm.handlers.FcmNewVideoProcessedHandler
import com.example.visara.service.fcm.handlers.FcmUnknownTypeHandler
import com.example.visara.service.fcm.handlers.FcmVideoLikeHandler
import com.example.visara.service.fcm.handlers.HandleFcmMessageStrategy
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
abstract class FcmHandlersModule {
    @Binds
    @IntoMap
    @RemoteNotificationTypeKey(RemoteNotificationType.VIDEO_UPLOAD_PROCESSED)
    abstract fun bindFcmNewVideoProcessedHandler(handler: FcmNewVideoProcessedHandler) : HandleFcmMessageStrategy


    @Binds
    @IntoMap
    @RemoteNotificationTypeKey(RemoteNotificationType.NEW_FOLLOWER)
    abstract fun bindFcmNewFollowerHandler(handler: FcmNewFollowerHandler) : HandleFcmMessageStrategy

    @Binds
    @IntoMap
    @RemoteNotificationTypeKey(RemoteNotificationType.VIDEO_LIKED)
    abstract fun bindFcmVideoLikeHandler(handler: FcmVideoLikeHandler) : HandleFcmMessageStrategy

    @Binds
    @IntoMap
    @RemoteNotificationTypeKey(RemoteNotificationType.COMMENT_LIKED)
    abstract fun bindFcmCommentLikeHandler(handler: FcmCommentLikeHandler) : HandleFcmMessageStrategy

    @Binds
    @IntoMap
    @RemoteNotificationTypeKey(RemoteNotificationType.COMMENT_ON_VIDEO)
    abstract fun bindFcmCommentOnVideoHandler(handler: FcmCommentOnVideoHandler) : HandleFcmMessageStrategy
    @Binds
    @IntoMap
    @RemoteNotificationTypeKey(RemoteNotificationType.NEW_MESSAGE)
    abstract fun bindNewChatMessageHandler(handler: FcmNewChatMessageHandler) : HandleFcmMessageStrategy


    @Binds
    @IntoMap
    @RemoteNotificationTypeKey(RemoteNotificationType.UNKNOWN)
    abstract fun bindFcmUnknownTypeHandler(handler: FcmUnknownTypeHandler) : HandleFcmMessageStrategy
}

@MapKey
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteNotificationTypeKey(val value: RemoteNotificationType)
