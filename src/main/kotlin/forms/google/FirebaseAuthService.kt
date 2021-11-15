package forms.google

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import jakarta.inject.Singleton

@Singleton
class FirebaseAuthService {

    init {
        FirebaseApp.initializeApp()
    }

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun verifyIdToken(idToken: String): FirebaseToken {
        return firebaseAuth.verifyIdToken(idToken)
    }
}