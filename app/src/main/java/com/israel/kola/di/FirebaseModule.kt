package com.israel.kola.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule{
    @Provides
    fun provideFirebaseAuth(@ApplicationContext appContext: Context): FirebaseAuth{
        Firebase.initialize(appContext)
        return FirebaseAuth.getInstance()
    }
}