package com.app.cloudide.controller

import com.app.cloudide.model.LoginRequest
import com.app.cloudide.model.User
import com.app.cloudide.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class AuthController(
  private val authService: AuthService,
) {
  @PostMapping("/register")
  fun registerUser(@RequestBody user: User): ResponseEntity<Map<String, String>> {
    return try {
      authService.registerUser(user)
      val response = mapOf("message" to "User successfully registered")
      ResponseEntity(response, HttpStatus.CREATED)
    } catch (e: IllegalArgumentException) {
      val errorResponse = mapOf("error" to e.message!!)
      ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    } catch (e: Exception) {
      val errorResponse = mapOf("error" to "Internal Server Error")
      ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }

  @PostMapping("/login")
  fun loginUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
    return try {
      val response = authService.loginUser(loginRequest)
      ResponseEntity(response, HttpStatus.OK)
    } catch (e: IllegalArgumentException) {
      val errorResponse = mapOf("error" to e.message!!)
      ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    } catch (e: Exception) {
      val errorResponse = mapOf("error" to "Internal Server Error")
      ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }
}