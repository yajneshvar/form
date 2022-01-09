package forms.google

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FirebaseAuthService constructor(@Inject val googleCredentials: GoogleCredentials) {

    init {
        val firebaseOption = FirebaseOptions.builder()
                .setProjectId("forms-304923")
                .setCredentials(googleCredentials)
                .build()
        FirebaseApp.initializeApp(firebaseOption)
    }

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun verifyIdToken(idToken: String): FirebaseToken {
        return firebaseAuth.verifyIdToken(idToken)
    }
}