package com.example.visara.di

import android.content.Context
import com.example.visara.data.local.datasource.AuthLocalDataSource
import com.example.visara.data.local.datasource.UserLocalDataSource
import com.example.visara.data.remote.datasource.AuthRemoteDataSource
import com.example.visara.data.remote.datasource.CommentRemoteDataSource
import com.example.visara.data.remote.datasource.UserRemoteDataSource
import com.example.visara.data.remote.datasource.VideoRemoteDataSource
import com.example.visara.data.repository.AppSettingsRepository
import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.CommentRepository
import com.example.visara.data.repository.InboxRepository
import com.example.visara.data.repository.SearchRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.data.repository.VideoDetailRepository
import com.example.visara.data.repository.VideoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        authRemoteDataSource: AuthRemoteDataSource,
        authLocalDataSource: AuthLocalDataSource,
        userRepository: UserRepository,
    ) : AuthRepository {
        return AuthRepository(authRemoteDataSource, authLocalDataSource, userRepository)
    }


    @Provides
    @Singleton
    fun provideUserRepository(
        userRemoteDataSource: UserRemoteDataSource,
        userLocalDataSource: UserLocalDataSource,
    ) : UserRepository {
        return UserRepository(userRemoteDataSource, userLocalDataSource)
    }

    @Provides
    @Singleton
    fun provideAppSettingsRepository(@ApplicationContext appContext: Context) : AppSettingsRepository {
        return AppSettingsRepository(appContext)
    }

    @Provides
    @Singleton
    fun provideVideoRepository(
        videoRemoteDataSource: VideoRemoteDataSource,
        @ApplicationContext appContext: Context,
    ) : VideoRepository {
        return VideoRepository(videoRemoteDataSource, appContext)
    }

    @Provides
    @Singleton
    fun provideCommentRepository(
        commentRemoteDataSource: CommentRemoteDataSource,
        authRepository: AuthRepository,
    ) : CommentRepository {
        return CommentRepository(commentRemoteDataSource, authRepository)
    }

    @Provides
    @Singleton
    fun provideVideoDetailRepository(
        @ApplicationContext appContext: Context,
        videoRepository: VideoRepository,
    ) : VideoDetailRepository {
        return VideoDetailRepository(appContext, videoRepository)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        videoRepository: VideoRepository,
        userRepository: UserRepository,
    ) : SearchRepository {
        return SearchRepository(videoRepository, userRepository)
    }

    @Provides
    @Singleton
    fun provideInboxRepository(): InboxRepository {
        return InboxRepository()
    }
}
