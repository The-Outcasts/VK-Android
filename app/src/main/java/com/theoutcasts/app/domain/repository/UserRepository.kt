package com.theoutcasts.app.domain.repository

import com.theoutcasts.app.domain.model.User

interface UserRepository {
    fun getAuthenticatedUserId(): String?
    suspend fun getAuthenticatedUser(): Result<User>
    suspend fun signUpWithEmailPasswordAndUsername(email: String, password: String, username: String): Result<User>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun signOut()
}
