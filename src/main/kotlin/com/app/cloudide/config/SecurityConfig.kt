package com.app.cloudide.config

import com.app.cloudide.filter.JwtFilter
import com.app.cloudide.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebSecurity
class SecurityConfig(
  private val jwtFilter: JwtFilter,
  private val userRepository: UserRepository
) {

  @Bean
  fun passwordEncoder(): PasswordEncoder {
    return BCryptPasswordEncoder(13)
  }

  @Bean
  @Throws(Exception::class)
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .csrf {
        it.disable()
      }
      .authorizeHttpRequests {
        it.requestMatchers(
          "/icon.png",
          "/css/**",
          "/js/**",
          "/images/**",
          "/actuator/**",
          "/",
          "/api/user/register",
          "/api/user/login",
          "/api/**",
          "/terminal/**"
        ).permitAll().anyRequest()
          .authenticated()
      }
      .exceptionHandling {
        it.authenticationEntryPoint { _, response, _ ->
          response.status = HttpServletResponse.SC_UNAUTHORIZED
        }
      }
      .formLogin(Customizer.withDefaults())
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // Disable sessions
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

    return http.build()
  }

  @Bean
  @Throws(Exception::class)
  fun authenticationManager(): AuthenticationManager {
    return AuthenticationManager {
      val username = it.name
      val user = userRepository.findByUsername(username)
        ?: throw UsernameNotFoundException("User not found: $username")
      UsernamePasswordAuthenticationToken(user, null, emptyList())
    }
  }

  @Bean
  fun corsConfigurer(): WebMvcConfigurer {
    return object : WebMvcConfigurer {
      override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
          .allowedOrigins("http://localhost:5173")
          .allowedMethods("*")
          .allowedHeaders("*")
          .allowCredentials(true)
      }
    }
  }
}