package com.wassallni.di

import android.content.Context
import com.wassallni.data.model.DoHomeWork
import com.wassallni.firebase.authentication.PhoneAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(ActivityComponent::class)
object MyModule {

    @Provides
    @ActivityScoped
    fun providePhoneAuth(@ActivityContext context: Context): DoHomeWork {
        return DoHomeWork(context)
    }
}







