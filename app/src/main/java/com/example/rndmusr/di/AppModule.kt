package com.example.rndmusr.di

import android.content.Context
import androidx.room.Room
import com.example.rndmusr.data.UserMapper
import com.example.rndmusr.data.local.database.AppDatabase
import com.example.rndmusr.data.remote.api.UserApi
import com.example.rndmusr.data.repository.UserRepositoryImpl
import com.example.rndmusr.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApi(okHttpClient: OkHttpClient): UserApi {
        return Retrofit.Builder()
            .baseUrl("https://randomuser.me/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "user_database"
        ).build()
    }

    @Provides
    fun provideUserRepository(
        userApi: UserApi,
        database: AppDatabase,
        mapper: UserMapper
    ): UserRepository {
        return UserRepositoryImpl(
            userApi = userApi,
            userDao = database.userDao(),
            mapper = mapper
        )
    }

    @Provides
    fun provideUserMapper(): UserMapper {
        return UserMapper()
    }
}