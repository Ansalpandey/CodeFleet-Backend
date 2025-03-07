package com.app.cloudide.model

data class LoginResponse(
  val token: String,
  val user: User
)