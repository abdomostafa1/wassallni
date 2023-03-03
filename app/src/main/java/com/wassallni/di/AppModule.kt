package com.wassallni.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.wassallni.data.model.LoggedInUser
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
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideLoggedInUser(sharedPreferences:SharedPreferences):LoggedInUser{
        val name=sharedPreferences.getString("name","")
        val token=sharedPreferences.getString("token","")
        return LoggedInUser(token!!, name!!)
    }
}