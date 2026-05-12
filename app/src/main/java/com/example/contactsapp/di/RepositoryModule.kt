package com.example.contactsapp.di

import com.example.contactsapp.data.repositoryImpl.ContactsRepositoryImpl
import com.example.contactsapp.domain.repository.ContactsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindContactsRepository(
        impl: ContactsRepositoryImpl
    ): ContactsRepository
}