package forms.authentication

import com.nimbusds.jwt.JWTClaimsSet
import forms.google.FirebaseAuthService
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.filters.AuthenticationFetcher
import io.micronaut.security.token.jwt.validator.AuthenticationJWTClaimsSetAdapter
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class IdTokenAuthenticationFetcher @Inject constructor(
        private val firebaseAuthService: FirebaseAuthService
): AuthenticationFetcher {
    val log = LoggerFactory.getLogger(IdTokenAuthenticationFetcher::class.java)
    override fun fetchAuthentication(request: HttpRequest<*>?): Publisher<Authentication> {
        return flow {
            val authorization = request?.headers?.authorization
            if (authorization?.isPresent == true) {
                log.info("Verifying user");
                val idToken = authorization.get().substringAfter(" ")
                val firebaseToken = firebaseAuthService.verifyIdToken(idToken)
                log.info("Verified user ${firebaseToken.email}");
                val jwtClaimset = JWTClaimsSet.Builder()
                        .subject(firebaseToken.email)
                        .jwtID(firebaseToken.uid)
                        .build()
                emit(AuthenticationJWTClaimsSetAdapter(jwtClaimset))
            }
        }
        .catch { log.warn("Invalid authorization", it) }
        .asPublisher()
    }
}