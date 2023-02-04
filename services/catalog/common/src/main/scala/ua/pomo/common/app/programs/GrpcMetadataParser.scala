package ua.pomo.common.app.programs

import cats.{Monad, MonadThrow}
import io.grpc.Metadata
import org.jose4j.jwe.{ContentEncryptionAlgorithmIdentifiers, JsonWebEncryption, KeyManagementAlgorithmIdentifiers}
import org.jose4j.keys.AesKey
import cats.syntax.functor.toFunctorOps
import io.circe.parser
import ua.pomo.common.domain.auth.{AuthConfig, CallContext, User, UserEmail, UserRole}
import org.jose4j.jwa.AlgorithmConstraints
import cats.syntax.flatMap.toFlatMapOps

class GrpcMetadataParser[F[_]: MonadThrow](authConfig: AuthConfig) {
  private val authHeaderKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)

  def extractMetadata(c: CallContext): F[Metadata] = for {
    key <- getDerivedKey(authConfig.jweSecret)
    nextAuthHeader <- MonadThrow[F].catchNonFatal {
      c.user.map { user =>
        val userData = s"""{"email":"${user.email.value}"}"""
        // Create a new Json Web Encryption object
        val senderJwe = new JsonWebEncryption()

        // The plaintext of the JWE is the message that we want to encrypt.
        senderJwe.setPlaintext(userData)

        // Set the "alg" header, which indicates the key management mode for this JWE.
        // In this example we are using the direct key management mode, which means
        // the given key will be used directly as the content encryption key.
        senderJwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.DIRECT)

        // Set the "enc" header, which indicates the content encryption algorithm to be used.
        // This example is using AES_128_CBC_HMAC_SHA_256 which is a composition of AES CBC
        // and HMAC SHA2 that provides authenticated encryption.
        senderJwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_GCM)

        // Set the key on the JWE. In this case, using direct mode, the key will used directly as
        // the content encryption key. AES_128_CBC_HMAC_SHA_256, which is being used to encrypt the
        // content requires a 256 bit key.
        senderJwe.setKey(key)

        // Produce the JWE compact serialization, which is where the actual encryption is done.
        // The JWE compact serialization consists of five base64url encoded parts
        // combined with a dot ('.') character in the general format of
        // <header>.<encrypted key>.<initialization vector>.<ciphertext>.<authentication tag>
        // Direct encryption doesn't use an encrypted key so that field will be an empty string
        // in this case.
        senderJwe.getCompactSerialization
      }
    }
  } yield {
    val md = new Metadata()
    nextAuthHeader.foreach {
      md.put(authHeaderKey, _)
    }
    md
  }

  def extractCallContext(m: Metadata): F[CallContext] = for {
    user <- getUser(m)
  } yield CallContext(user)

  private def getDerivedKey(key: String): F[AesKey] = {
    HKDF
      .hkdf[F](key, "", "NextAuth.js Generated Encryption Key", 32)
      .map(new AesKey(_))
  }

  private def getUser(m: Metadata): F[Option[User]] = {
    val authTokenOpt = Option(m.get(authHeaderKey))
    authTokenOpt.fold[F[Option[User]]](Monad[F].pure(None)) { authToken =>
      for {
        derivedKey <- getDerivedKey(authConfig.jweSecret)
        decodedTokenPayloadJson <- MonadThrow[F].catchNonFatal {
          val receiverJwe = new JsonWebEncryption()
          val algConstraints = new AlgorithmConstraints(
            AlgorithmConstraints.ConstraintType.PERMIT,
            KeyManagementAlgorithmIdentifiers.DIRECT
          )
          receiverJwe.setAlgorithmConstraints(algConstraints)
          val encConstraints =
            new AlgorithmConstraints(
              AlgorithmConstraints.ConstraintType.PERMIT,
              ContentEncryptionAlgorithmIdentifiers.AES_256_GCM
            )
          receiverJwe.setContentEncryptionAlgorithmConstraints(encConstraints)

          receiverJwe.setCompactSerialization(authToken)

          // Symmetric encryption, like we are doing here, requires that both parties have the same key.
          // The key will have had to have been securely exchanged out-of-band somehow.
          receiverJwe.setKey(derivedKey)

          // Get the message that was encrypted in the JWE. This step performs the actual decryption steps.
          receiverJwe.getPlaintextString
        }
        json <- MonadThrow[F].fromEither(parser.parse(decodedTokenPayloadJson))
        userEmail <- MonadThrow[F].fromEither(json.hcursor.downField("email").as[String].map(UserEmail.apply))
      } yield Some(User(userEmail, getRole(userEmail)))
    }
  }

  private def getRole(userEmail: UserEmail): UserRole = {
    if (authConfig.admins.contains(userEmail)) {
      UserRole.Admin
    } else {
      UserRole.User
    }
  }

}
