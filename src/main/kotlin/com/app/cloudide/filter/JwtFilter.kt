package com.app.cloudide.filter

import com.app.cloudide.repository.UserRepository
import com.app.cloudide.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
  private val jwtUtil: JwtUtil,
  private val userRepository: UserRepository
) : OncePerRequestFilter() {
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    val authorizationHeader = request.getHeader("Authorization")
    var token: String? = null
    var username: String? = null

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      token = authorizationHeader.substring(7)
      username = jwtUtil.extractUsername(token)
    }

    if (username != null && SecurityContextHolder.getContext().authentication == null) {
      val userDetails = userRepository.findByUsername(username)

      if (token?.let { jwtUtil.validateToken(it) } == true) {
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(userDetails, null, emptyList())

        usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
      }
    }

    filterChain.doFilter(request, response)
  }
}