package forms.google

import com.google.auth.oauth2.GoogleCredentials
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class CredentialsFactory {

    @Bean
    @Singleton
    fun getCredentials() = GoogleCredentials.getApplicationDefault();

}