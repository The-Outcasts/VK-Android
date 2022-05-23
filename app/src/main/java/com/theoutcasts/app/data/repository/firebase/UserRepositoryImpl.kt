package com.theoutcasts.app.data.repository.firebase

import com.google.firebase.auth.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.theoutcasts.app.domain.interactor.*
import com.theoutcasts.app.domain.model.User
import com.theoutcasts.app.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl : UserRepository {
    private var firebaseAuthService: FirebaseAuth = FirebaseAuth.getInstance()
    private val nodeReference = Firebase.database(DATABASE_CONNECTION_STRING).reference.child(
        NODE_NAME)

    override suspend fun getAuthenticatedUser(): Result<User> {
        return try {
            val firebaseUser: FirebaseUser? = firebaseAuthService.currentUser
            val usernameResponse = nodeReference.child(firebaseUser!!.uid).child("username").get().await()
            val user = User(firebaseUser.uid, firebaseUser.email, usernameResponse.value.toString())

            Result.success(user)
        } catch (e: FirebaseAuthException) {
            Result.failure(UserIsNotAuthenticatedException())
        }
    }

    override suspend fun signUpWithEmailPasswordAndUsername(email: String, password: String, username: String): Result<User> {
        return try {
            val authResponse = firebaseAuthService.createUserWithEmailAndPassword(email, password).await()
            nodeReference.child(authResponse!!.user!!.uid).child("username").setValue(username).await()
            val user = User(authResponse.user!!.uid, authResponse.user!!.email, username)

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
            val usernameResponse = nodeReference.child(authResponse!!.user!!.uid).child("username").get().await()
            val user = User(authResponse.user!!.uid, authResponse.user!!.email, usernameResponse.value.toString())

            Result.success(user)
        } catch (e: FirebaseAuthException) {
            Result.failure(InvalidLoginOrPasswordException())
        }
    }

    override suspend fun signOut() {
        firebaseAuthService.signOut()
    }

    companion object {
        const val NODE_NAME = "users"
        const val DATABASE_CONNECTION_STRING = "https://vk-android-efc7c-default-rtdb.europe-west1.firebasedatabase.app" // TODO: move to config
    }
}
