package forms.google

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Message
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.apache.commons.codec.binary.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.inject.Singleton
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Singleton
class GoogleGmailService {

    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val jacksonFactory = GsonFactory.getDefaultInstance()
    val credentials = GoogleCredentials.fromStream(readCredentials()).createScoped(GmailScopes.GMAIL_SEND).createDelegated("yajneshvar@tst-ims.com")
    val GMAIL = Gmail.Builder(httpTransport, jacksonFactory, HttpCredentialsAdapter(credentials)).build()
    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    @Throws(MessagingException::class)
    fun createEmail(to: String?,
                    from: String?,
                    subject: String?,
                    bodyText: String?): MimeMessage? {
        val props = Properties()
        val session: Session = Session.getDefaultInstance(props, null)
        val email = MimeMessage(session)
        email.setFrom(InternetAddress(from))
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                InternetAddress(to))
        email.setSubject(subject)
        email.setText(bodyText)
        return email
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    @Throws(MessagingException::class, IOException::class)
    fun createMessageWithEmail(emailContent: MimeMessage): Message? {
        val buffer = ByteArrayOutputStream()
        emailContent.writeTo(buffer)
        val bytes = buffer.toByteArray()
        val encodedEmail: String = Base64.encodeBase64URLSafeString(bytes)
        val message = Message()
        message.setRaw(encodedEmail)
        return message
    }

    fun sendEmail(user: String, email: MimeMessage) {
        val message = createMessageWithEmail(email)
        val returnedMessage = GMAIL.users().messages().send(user, message).execute()
        println(returnedMessage.id)
        println(returnedMessage.toPrettyString())
    }

    companion object {
        fun readCredentials(): InputStream = this::class.java.getResourceAsStream("/GoogleCredential.json")
    }

}

