package forms.authentication

import forms.google.FirebaseAuthService
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.authentication.DefaultAuthentication
import io.micronaut.security.filters.AuthenticationFetcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdTokenAuthenticationFetcher @Inject constructor(
        private val firebaseAuthService: FirebaseAuthService
): AuthenticationFetcher {
    val log = LoggerFactory.getLogger(IdTokenAuthenticationFetcher::class.java)
    override fun fetchAuthentication(request: HttpRequest<*>?): Publisher<Authentication> {
        return flow {
            val authorization = request?.headers?.authorization
            if (authorization?.isPresent == true) {
                val idToken = authorization.get().substringAfter(" ")
                val firebaseToken = firebaseAuthService.verifyIdToken(idToken)
                emit(DefaultAuthentication(firebaseToken.name, mapOf("email" to firebaseToken.email, "uid" to firebaseToken.uid)))
            }
        }
        .catch { log.warn("Invalid authorization", it) }
        .asPublisher()
    }
}