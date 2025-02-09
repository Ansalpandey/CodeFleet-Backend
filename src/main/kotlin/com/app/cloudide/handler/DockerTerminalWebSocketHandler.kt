package com.app.cloudide.handler
import com.app.cloudide.config.TerminalFactory
import com.app.cloudide.config.TerminalSession
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

@Component
class DockerTerminalWebSocketHandler : TextWebSocketHandler() {
  private val sessions = ConcurrentHashMap<String, TerminalSession>()

  override fun afterConnectionEstablished(session: WebSocketSession) {
    val containerId = extractContainerId(session)
    if (containerId.isNullOrEmpty()) {
      session.close(CloseStatus.BAD_DATA)
      return
    }
    val terminal = TerminalFactory.createDockerTerminal(
      containerId = containerId,
      outputHandler = {
        sendMessage(session, it)
      },
    )
    sessions[session.id] = terminal
  }

  private val sessionLock = ReentrantLock()

  fun sendMessage(session: WebSocketSession, message: String) {
    sessionLock.lock()
    try {
      if (session.isOpen) {
        session.sendMessage(TextMessage(message))
      }
    } catch (e: Exception) {
      println("Failed to send message: ${e.message}")
    } finally {
      sessionLock.unlock()
    }
  }

  private fun extractContainerId(session: WebSocketSession): String? {
    return session.uri?.query?.split("=")?.find { it == "containerId" }?.let {
      session.uri?.query?.split("=")?.last()
    }
  }

  override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
    sessions[session.id]?.write(message.payload)
  }

  override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
    sessions[session.id]?.destroy()
    sessions.remove(session.id)
  }

  override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
    sessions[session.id]?.destroy()
    sessions.remove(session.id)
    session.close(CloseStatus.SERVER_ERROR)
  }
}