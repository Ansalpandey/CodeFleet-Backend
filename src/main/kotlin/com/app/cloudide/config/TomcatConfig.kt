package com.app.cloudide.config

import org.apache.coyote.http11.Http11NioProtocol
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TomcatConfig {
  @Bean
  fun webServerFactoryCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    return WebServerFactoryCustomizer { factory ->
      factory.addConnectorCustomizers(TomcatConnectorCustomizer { connector ->
        (connector.protocolHandler as Http11NioProtocol).apply {
          relaxedQueryChars = "<>[\\]^`{|}"
          relaxedPathChars = "<>[\\]^`{|}"
        }
      })
    }
  }
}