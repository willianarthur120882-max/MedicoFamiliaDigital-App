package com.medicofamiliadigital.domain.repository

import com.medicofamiliadigital.data.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfilesForUser(userId: String): Flow<List<Profile>>
    suspend fun createProfile(profile: Profile): Result<String>
    suspend fun updateProfile(profile: Profile): Result<Unit>
    suspend fun deleteProfile(profileId: String): Result<Unit>
    suspend fun getProfile(profileId: String): Result<Profile>
}

