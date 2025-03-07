package com.app.cloudide.utils
import com.app.cloudide.model.LoginRequest
import com.app.cloudide.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {
  private val secretKeyString =
    "YourSecretKjhkldasfhjkladsfljkdasfjklsadfhjkdsahfljksdahfljkasdhfjlkhsfjklsadhlfjkasdfasdfajdsklaf;jklfdjfkl;jfklnm,cxvnm,zxncv.uiefhuhjdksm,cvn.m,zxcvujifhewoiufhoeijfioejfiewsdey"
  private val jwtExpirationMs = 86400000

  private val secretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray())
  fun generateToken(loginRequest: User): String {
    val claims: Map<String, Any> = mapOf("username" to loginRequest.username)

    return Jwts.builder().claims(claims).issuedAt(Date())
      .expiration(Date(System.currentTimeMillis() + jwtExpirationMs)).signWith(secretKey).compact()
  }

  fun validateToken(token: String): Boolean {
    return try {
      Jwts.parser().verifyWith(secretKey).build().parse(token)
      true
    } catch (e: Exception) {
      false
    }
  }

  fun extractUsername(token: String): String? {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload["username"] as String?
  }
}