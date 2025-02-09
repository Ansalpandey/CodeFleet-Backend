package com.app.cloudide.config

import com.app.cloudide.handler.DockerTerminalWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(private val dockerTerminalHandler: DockerTerminalWebSocketHandler) : WebSocketConfigurer {
  override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
    registry.addHandler(dockerTerminalHandler, "/ws/docker-terminal")
      .setAllowedOriginPatterns("*")
  }
}