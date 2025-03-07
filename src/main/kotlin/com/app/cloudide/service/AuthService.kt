package com.app.cloudide.service

import com.app.cloudide.model.LoginRequest
import com.app.cloudide.model.LoginResponse
import com.app.cloudide.model.User
import com.app.cloudide.repository.UserRepository
import com.app.cloudide.utils.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
  private val userRepository: UserRepository,
  private val bcryptPasswordEncoder: PasswordEncoder,
  private val jwtUtil: JwtUtil
) {
  fun registerUser(user: User) {
    val existingUser = userRepository.findByUsername(user.username)
    if (existingUser != null) {
      throw IllegalArgumentException("User already exists")
    }
    if (user.username.isBlank() || user.password.isBlank()) {
      throw IllegalArgumentException("Password and username cannot be empty")
    }
    if (user.password.length < 6) {
      throw IllegalArgumentException("Password must be at least 6 characters long")
    }
    if (user.username.length < 4) {
      throw IllegalArgumentException("Username must be at least 4 characters long")
    }
    if (user.username.length > 20) {
      throw IllegalArgumentException("Username must be at most 20 characters long")
    }
    if (user.password.length > 20) {
      throw IllegalArgumentException("Password must be at most 20 characters long")
    }
    if (!user.username.matches(Regex("^[a-zA-Z0-9]*$"))) {
      throw IllegalArgumentException("Username must contain only letters and numbers")
    }
    if (!user.password.matches(Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*$"))) {
      throw IllegalArgumentException("Password must contain at least one capital letter, one number, and one special character.")
    }
    user.password = bcryptPasswordEncoder.encode(user.password)

    userRepository.save(user)

  }

  fun loginUser(loginRequest: LoginRequest): LoginResponse {
    if (loginRequest.username.isNullOrEmpty() && loginRequest.email.isNullOrEmpty()) {
      throw IllegalArgumentException("Username or Email cannot be empty")
    }

    val dbUser = userRepository.findByUsername(loginRequest.username ?: "")
      ?: userRepository.findByEmail(loginRequest.email ?: "")

    if (dbUser != null && bcryptPasswordEncoder.matches(loginRequest.password, dbUser.password)) {
      val token = jwtUtil.generateToken(dbUser)
      return LoginResponse(token, dbUser)
    }

    throw IllegalArgumentException("Invalid username/email or password")
  }
}