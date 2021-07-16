package com.novelijk.numberlog.di

import android.app.Application
import androidx.room.Room
import com.novelijk.numberlog.data.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application
    ) = Room.databaseBuilder(app, Database::class.java, "database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideDAO(
        db: Database
    ) = db.dao()
}