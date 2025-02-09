package com.app.cloudide.service

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.model.*
import jakarta.ws.rs.NotFoundException
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class DockerService(
  private val dockerClient: DockerClient
) {

  fun createAndStartContainer(imageName: String, containerName: String, hostPort: Int, exposedPort: Int): String {
    // Check if container with the same name already exists
    val existingContainer = dockerClient.listContainersCmd().withShowAll(true).exec()
      .find { it.names.any { name -> name == "/$containerName" } }

    if (existingContainer != null) {
      throw IllegalStateException("Container '$containerName' already exists with ID: ${existingContainer.id}")
    }

    // Check if image exists locally, if not, pull it
    val imageExists = dockerClient.listImagesCmd().exec().any { it.repoTags?.contains(imageName) == true }
    if (!imageExists) {
      pullImage(imageName)
    }

    // Define exposed port and port bindings
    val exposed = ExposedPort.tcp(exposedPort)
    val portBinding = PortBinding(Ports.Binding.bindPort(hostPort), exposed)

    // Create container
    val container: CreateContainerResponse = dockerClient.createContainerCmd(imageName)
      .withName(containerName)
      .withExposedPorts(exposed)
      .withHostConfig(
        HostConfig()
          .withPortBindings(portBinding)
          .withNetworkMode("bridge")
      )
      .withCmd("npm", "run", "start") // Ensure command is executed
      .exec()

    // Start the container
    dockerClient.startContainerCmd(container.id).exec()
    return "Container started successfully with ID: ${container.id}"
  }

  // Pull Image if not found locally
  private fun pullImage(imageName: String) {
    try {
      dockerClient.pullImageCmd(imageName).start().awaitCompletion()
    } catch (e: Exception) {
      throw IllegalStateException("Failed to pull image '$imageName': ${e.message}")
    }
  }

  // List running containers
  fun listContainers(): List<String> {
    return dockerClient.listContainersCmd().exec().map { container ->
      "Container ID: ${container.id}, Image: ${container.image}, Status: ${container.state}"
    }
  }

  // Stop a container
  fun stopContainer(containerId: String) {
    try {
      dockerClient.stopContainerCmd(containerId).exec()
    } catch (e: NotFoundException) {
      throw IllegalArgumentException("Container not found: $containerId")
    }
  }

  //Start a container

  fun startContainer(containerId: String) {
    try {
      // Inspect container to check its current state
      val containerInfo = dockerClient.inspectContainerCmd(containerId).exec()

      if (containerInfo.state.running == true) {
        throw IllegalStateException("Container $containerId is already running.")
      }

      // Start the container only if it's not running
      dockerClient.startContainerCmd(containerId).exec()
    } catch (e: NotFoundException) {
      throw IllegalArgumentException("Container not found: $containerId")
    } catch (e: Exception) {
      throw IllegalStateException("Failed to start container: ${e.message}")
    }
  }

  // Remove a container
  fun removeContainer(containerId: String) {
    try {
      dockerClient.removeContainerCmd(containerId).withForce(true).exec()
    } catch (e: NotFoundException) {
      throw IllegalArgumentException("Container not found: $containerId")
    }
  }

  fun getBaseFolderContents(containerId: String, basePath: String = "/app"): List<Map<String, Any>> {
    try {
      val execCreateCmd = dockerClient.execCreateCmd(containerId)
        .withAttachStdout(true)
        .withAttachStderr(true)
        .withCmd("sh", "-c", "ls -1p $basePath") // Using `-1p` to list items one per line, appending `/` for directories
        .exec()

      val outputStream = ByteArrayOutputStream()

      dockerClient.execStartCmd(execCreateCmd.id)
        .exec(object : ResultCallback.Adapter<Frame>() {
          override fun onNext(frame: Frame) {
            outputStream.write(frame.payload)
          }
        }).awaitCompletion()

      return outputStream.toString().trim().split("\n")
        .filter { it.isNotBlank() } // Remove empty lines
        .map { name ->
          val isDirectory = name.endsWith("/")
          mapOf(
            "name" to name.removeSuffix("/"),
            "isDirectory" to isDirectory,
            "path" to "$basePath/${name.removeSuffix("/")}"
          )
        }
    } catch (e: NotFoundException) {
      throw IllegalArgumentException("Container not found: $containerId")
    } catch (e: Exception) {
      throw IllegalStateException("Failed to fetch base folder contents: ${e.message}")
    }
  }

}