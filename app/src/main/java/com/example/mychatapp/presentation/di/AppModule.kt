package com.example.mychatapp.presentation.di

import com.example.mychatapp.data.repo_impl.AuthRepoImpl
import com.example.mychatapp.data.repo_impl.MessageRepoImpl
import com.example.mychatapp.data.repo_impl.UserRepoImpl
import com.example.mychatapp.domain.repo.IAuthRepo
import com.example.mychatapp.domain.repo.IMessageRepo
import com.example.mychatapp.domain.repo.IUserRepo
import com.example.mychatapp.domain.usecase.GetAllUsersUseCase
import com.example.mychatapp.domain.usecase.GetLoggedUserData
import com.example.mychatapp.domain.usecase.GetMessages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

import javax.inject.Singleton


@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

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

    @Provides
    @Singleton
    fun providesGetLoggedUserData(userRepo: IUserRepo):GetLoggedUserData{
        return GetLoggedUserData(userRepo)
    }

    @IoDispatcher
    @Provides
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun providesMessageRepo(firestore: FirebaseFirestore): IMessageRepo{
        return MessageRepoImpl(firestore)
    }

    @Provides
    @Singleton
    fun providesGetMessageUseCase(messageRepo:IMessageRepo):GetMessages{
        return GetMessages(messageRepo)
    }

}