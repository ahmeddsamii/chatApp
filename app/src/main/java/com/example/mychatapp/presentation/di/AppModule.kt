package com.example.mychatapp.presentation.di

import com.example.mychatapp.data.repo_impl.AuthRepoImpl
import com.example.mychatapp.data.repo_impl.UserRepoImpl
import com.example.mychatapp.domain.repo.IAuthRepo
import com.example.mychatapp.domain.repo.IUserRepo
import com.example.mychatapp.domain.usecase.GetAllUsersUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun providesFirebaseAuthInstance(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesAuthRepoInstance(firebaseAuth: FirebaseAuth):IAuthRepo{
        return AuthRepoImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun providesFirebaseFireStoreInstance():FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun providesUserRepoInstance(firestore: FirebaseFirestore):IUserRepo{
        return UserRepoImpl(firestore)
    }

    @Provides
    @Singleton
    fun providesGetAllUsersUseCase(userRepo: IUserRepo):GetAllUsersUseCase{
        return GetAllUsersUseCase(userRepo)
    }
}