package com.theoutcasts.app.domain.repository

import com.theoutcasts.app.domain.model.User

interface UserRepository {
    suspend fun getAuthenticatedUser(): Result<User>
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun signOut()
}
