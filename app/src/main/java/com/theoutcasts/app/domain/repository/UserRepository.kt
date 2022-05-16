package com.theoutcasts.app.domain.repository

import com.theoutcasts.app.domain.model.User

interface UserRepository {
    suspend fun getAuthenticatedUser(): User?
    suspend fun signUp(email: String, password: String): Pair<User?, Exception?>
    suspend fun signIn(email: String, password: String): User?
    suspend fun signOut()
}
