package com.medicofamiliadigital.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.medicofamiliadigital.data.repository.AuthRepositoryImpl
import com.medicofamiliadigital.data.repository.ChatRepositoryImpl
import com.medicofamiliadigital.data.repository.MedicalDocumentRepositoryImpl
import com.medicofamiliadigital.data.repository.ProfileRepositoryImpl
import com.medicofamiliadigital.data.repository.ReminderRepositoryImpl
import com.medicofamiliadigital.domain.repository.AuthRepository
import com.medicofamiliadigital.domain.repository.ChatRepository
import com.medicofamiliadigital.domain.repository.MedicalDocumentRepository
import com.medicofamiliadigital.domain.repository.ProfileRepository
import com.medicofamiliadigital.domain.repository.ReminderRepository
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
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth
    ): AuthRepository = AuthRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideProfileRepository(
        firestore: FirebaseFirestore
    ): ProfileRepository = ProfileRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideMedicalDocumentRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): MedicalDocumentRepository = MedicalDocumentRepositoryImpl(firestore, storage)

    @Provides
    @Singleton
    fun provideReminderRepository(
        firestore: FirebaseFirestore
    ): ReminderRepository = ReminderRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideChatRepository(
        firestore: FirebaseFirestore
    ): ChatRepository = ChatRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideDocumentAnalysisRepository(
        firestore: FirebaseFirestore
    ): DocumentAnalysisRepository = DocumentAnalysisRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideDietRepository(
        firestore: FirebaseFirestore
    ): DietRepository = DietRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideHealthMetricRepository(
        firestore: FirebaseFirestore
    ): HealthMetricRepository = HealthMetricRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideShareableContentRepository(
        firestore: FirebaseFirestore
    ): ShareableContentRepository = ShareableContentRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideHealthDeviceRepository(
        firestore: FirebaseFirestore
    ): HealthDeviceRepository = HealthDeviceRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideEducationalContentRepository(
        firestore: FirebaseFirestore
    ): EducationalContentRepository = EducationalContentRepositoryImpl(firestore)
}

