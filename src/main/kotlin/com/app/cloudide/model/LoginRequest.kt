package com.app.cloudide.model

data class LoginRequest(
  val username: String? = null,
  val email: String? = null,
  val password: String
)