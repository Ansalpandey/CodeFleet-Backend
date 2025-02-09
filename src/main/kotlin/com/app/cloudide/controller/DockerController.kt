package com.app.cloudide.controller

import com.app.cloudide.service.DockerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/docker")
class DockerController(private val dockerService: DockerService) {

  @PostMapping("/create")
  fun createContainer(): ResponseEntity<String> {
    return try {
      ResponseEntity.ok(dockerService.createAndStartContainer("nextjs-template:latest", "NextJS-App", 3000, 3000))
    } catch (e: IllegalStateException) {
      ResponseEntity.badRequest().body(e.message)
    } catch (e: Exception) {
      ResponseEntity.internalServerError().body("Unexpected error: ${e.message}")
    }
  }

  @GetMapping("/list")
  fun listContainers(): ResponseEntity<List<String>> {
    return try {
      ResponseEntity.ok(dockerService.listContainers())
    } catch (e: Exception) {
      ResponseEntity.internalServerError().body(listOf("Failed to list containers: ${e.message}"))
    }
  }

  @PostMapping("/stop/{containerId}")
  fun stopContainer(@PathVariable containerId: String): ResponseEntity<String> {
    return try {
      dockerService.stopContainer(containerId)
      ResponseEntity.ok("Container stopped successfully: $containerId")
    } catch (e: IllegalArgumentException) {
      ResponseEntity.badRequest().body(e.message)
    } catch (e: Exception) {
      ResponseEntity.internalServerError().body("Failed to stop container: ${e.message}")
    }
  }

  @PostMapping("/start/{containerId}")
  fun startContainer(@PathVariable containerId: String): ResponseEntity<String> {
    return try {
      dockerService.startContainer(containerId)
      ResponseEntity.ok("Container started successfully: $containerId")
    } catch (e: IllegalStateException) {
      ResponseEntity.badRequest().body(e.message)
    } catch (e: IllegalArgumentException) {
      ResponseEntity.notFound().build()
    } catch (e: Exception) {
      ResponseEntity.internalServerError().body("Failed to start container: ${e.message}")
    }
  }

  @DeleteMapping("/remove/{containerId}")
  fun removeContainer(@PathVariable containerId: String): ResponseEntity<String> {
    return try {
      dockerService.removeContainer(containerId)
      ResponseEntity.ok("Container removed successfully: $containerId")
    } catch (e: IllegalArgumentException) {
      ResponseEntity.badRequest().body(e.message)
    } catch (e: Exception) {
      ResponseEntity.internalServerError().body("Failed to remove container: ${e.message}")
    }
  }
}