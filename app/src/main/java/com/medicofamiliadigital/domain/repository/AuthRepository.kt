package com.medicofamiliadigital.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): FirebaseUser?
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>
    suspend fun signOut()
    fun isUserLoggedIn(): Flow<Boolean>
}

