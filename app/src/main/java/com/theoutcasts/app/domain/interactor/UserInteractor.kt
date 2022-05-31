package com.theoutcasts.app.domain.interactor

import com.theoutcasts.app.domain.repository.UserRepository
import com.theoutcasts.app.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserAlreadyExistsException : Exception("User with provided credentials already exists")
class InvalidLoginOrPasswordException: Exception("Invalid login or password")
class WeakPasswordException: Exception("Weak password")
class InvalidCredentialsException: Exception("Provided credentials badly formatted")
class UserIsNotAuthenticatedException: Exception("User is not authenticated")

class UserInteractor(private val userRepository: UserRepository) {

    fun getAuthenticatedUserId(): String? =
        userRepository.getAuthenticatedUserId()

    suspend fun getAuthenticatedUser(): Result<User> =
        userRepository.getAuthenticatedUser()

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> =
        userRepository.signInWithEmailAndPassword(email, password)

    suspend fun signUpWithEmailPasswordAndUsername(email: String, password: String,
                                                   username: String): Result<User> =
        userRepository.signUpWithEmailPasswordAndUsername(email, password, username)

    suspend fun signOut() =
        userRepository.signOut()
}
