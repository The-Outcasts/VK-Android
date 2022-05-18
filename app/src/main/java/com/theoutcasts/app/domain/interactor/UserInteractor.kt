package com.theoutcasts.app.domain.interactor

import com.theoutcasts.app.domain.repository.UserRepository
import com.theoutcasts.app.domain.model.User

class UserAlreadyExistsException : Exception("User with provided credentials already exists")
class InvalidLoginOrPasswordException: Exception("Invalid login or password")
class WeakPasswordException: Exception("Weak password")
class InvalidCredentialsException: Exception("Provided credentials badly formatted")
class UserIsNotAuthenticatedException: Exception("User is not authenticated")

class UserInteractor(private val userRepository: UserRepository) {

    suspend fun getAuthenticatedUser(): Result<User> =
        userRepository.getAuthenticatedUser()

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> =
        userRepository.signInWithEmailAndPassword(email, password)

    suspend fun signUpWithEmailAndPassword(email: String, password: String): Result<User> =
        userRepository.signUpWithEmailAndPassword(email, password)

    suspend fun signOut() =
        userRepository.signOut()
}