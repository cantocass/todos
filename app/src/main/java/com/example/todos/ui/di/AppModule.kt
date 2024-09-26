package com.example.todos.ui.di

import android.content.Context
import androidx.room.Room
import com.example.todos.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoomDb(@ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(context,
            AppDatabase::class.java, "task-db")
            .build()
    }

    @Provides
    fun provideTaskDao(appDatabase: AppDatabase) = appDatabase.taskDao()


}