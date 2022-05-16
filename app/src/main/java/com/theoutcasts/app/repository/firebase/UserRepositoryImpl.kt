package com.theoutcasts.app.repository.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.theoutcasts.app.domain.model.User
import com.theoutcasts.app.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl : UserRepository {
    private var firebaseAuthService: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun getAuthenticatedUser(): User? {
        val firebaseUser: FirebaseUser = firebaseAuthService.currentUser ?: return null
        return User(firebaseUser.uid, firebaseUser.email)
    }

    override suspend fun signUp(email: String, password: String): Pair<User?, Exception?> {
        var retval: Pair<User?, Exception?> = Pair(null, null)

        try {
            val requestResult = firebaseAuthService.createUserWithEmailAndPassword(email, password).await()
            if (requestResult.user != null) {
                val user = User(requestResult.user!!.uid, requestResult.user!!.email)
                retval = Pair(user, null)
            }
        } catch (e: Exception) {
            retval = Pair(null, e)
        }

        return retval
    }

    override suspend fun signIn(email: String, password: String): User? {
        return try {
            val requestResult = firebaseAuthService.signInWithEmailAndPassword(email, password).await()
            if (requestResult.user == null) {
                null
            } else {
                User(requestResult.user!!.uid, requestResult.user!!.email)
            }
        } catch (e: FirebaseAuthException) {
            null
        }
    }

    override suspend fun signOut() {
        firebaseAuthService.signOut()
    }
}
