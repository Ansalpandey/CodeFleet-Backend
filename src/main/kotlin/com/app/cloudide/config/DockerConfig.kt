package com.app.cloudide.config

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.okhttp.OkDockerHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DockerConfig {
  @Bean
  fun dockerClient(): DockerClient {
    val config = DefaultDockerClientConfig.createDefaultConfigBuilder()
      .withDockerHost("tcp://localhost:2375")
      .build()

    val httpClient = OkDockerHttpClient.Builder()
      .dockerHost(config.dockerHost)
      .build()

    return DockerClientBuilder.getInstance(config)
      .withDockerHttpClient(httpClient)
      .build()
  }
}