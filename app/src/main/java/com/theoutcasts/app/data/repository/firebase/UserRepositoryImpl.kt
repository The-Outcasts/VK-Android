package com.theoutcasts.app.data.repository.firebase

import com.google.firebase.auth.*
import com.theoutcasts.app.domain.interactor.*
import com.theoutcasts.app.domain.model.User
import com.theoutcasts.app.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl : UserRepository {
    private var firebaseAuthService: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun getAuthenticatedUser(): Result<User> {
        return try {
            val firebaseUser: FirebaseUser? = firebaseAuthService.currentUser
            val user = User(firebaseUser!!.uid, firebaseUser.email)
            Result.success(user)
        } catch (e: FirebaseAuthException) {
            Result.failure(UserIsNotAuthenticatedException())
        }
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): Result<User> {
        return try {
            val authResponse = firebaseAuthService.createUserWithEmailAndPassword(email, password).await()
            val user = User(authResponse.user!!.uid, authResponse.user!!.email)
            Result.success(user)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.failure(WeakPasswordException())
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(UserAlreadyExistsException())
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(InvalidCredentialsException())
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
        return try {
            val authResponse = firebaseAuthService.signInWithEmailAndPassword(email, password).await()
            val user = User(authResponse.user!!.uid, authResponse.user!!.email)
            Result.success(user)
        } catch (e: FirebaseAuthException) {
            Result.failure(InvalidLoginOrPasswordException())
        }
    }

    override suspend fun signOut() {
        firebaseAuthService.signOut()
    }
}
