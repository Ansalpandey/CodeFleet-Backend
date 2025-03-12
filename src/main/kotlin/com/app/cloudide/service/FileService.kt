package com.app.cloudide.service

import com.app.cloudide.dto.FileResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

@Service
class FileService(
  private val dockerService: DockerService
) {

  fun listDirectory(containerId: String, basePath: String = "/app"): List<Map<String, Any>> {

    val logger = LoggerFactory.getLogger("DirectoryTraversalLogger")
    val result = mutableListOf<Map<String, Any>>()
    val queue = mutableListOf(basePath)

    logger.info("Starting directory traversal for containerId: $containerId")

    while (queue.isNotEmpty()) {
      val currentPath = queue.removeAt(0)
      logger.info("Processing directory: $currentPath")

      // Skip the node_modules directory
      if (currentPath.endsWith("node_modules")) {
        logger.info("Skipping node_modules directory: $currentPath")
        continue
      }

      val contents = dockerService.getBaseFolderContents(containerId, currentPath)
      logger.info("Found ${contents.size} items in directory: $currentPath")

      for (item in contents) {
        val path = item["path"] as String
        val isDirectory = item["isDirectory"] as Boolean

        // Skip any item inside the node_modules directory
        if (path.contains("node_modules")) {
          logger.debug("Skipping item inside node_modules: $path")
          continue
        }

        result.add(item)
        logger.debug("Added item to result: $path")

        if (isDirectory) {
          queue.add(path) // Add subdirectory to the queue for processing
          logger.debug("Added subdirectory to queue: $path")
        }
      }
    }

    logger.info("Directory traversal completed. Total items processed: ${result.size}")
    return result
  }

  fun readFile(containerId: String, path: String): FileResponse {
    return try {
      val decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8.name())

      // Run the cat command inside the Docker container to read the file
      val command = listOf("docker", "exec", containerId, "cat", decodedPath)
      val process = ProcessBuilder(command).start()
      val content = process.inputStream.bufferedReader().readText()
      val error = process.errorStream.bufferedReader().readText()
      val exitCode = process.waitFor()

      if (exitCode == 0) {
        FileResponse.FileContent(content)
      } else {
        FileResponse.Error("Failed to read file: $error")
      }
    } catch (e: Exception) {
      FileResponse.Error("Unable to read file: ${e.message}")
    }
  }


  fun updateFile(path: String, content: String): String {
    Files.writeString(Paths.get(path), content)
    return "File saved successfully"
  }

  fun createFile(path: String): String {
    val file = File(path)
    if (file.exists()) throw Exception("File already exists")
    file.createNewFile()
    return "File created successfully"
  }

  fun createFolder(path: String): String {
    val folder = File(path)
    if (folder.exists()) throw Exception("Folder already exists")
    folder.mkdirs()
    return "Folder created successfully"
  }

  fun renameFileOrFolder(oldPath: String, newPath: String): String {
    val oldFile = File(oldPath)
    if (!oldFile.exists()) throw Exception("File or folder does not exist")

    val newFile = File(newPath)
    if (newFile.exists()) throw Exception("A file or folder with the new name already exists")

    oldFile.renameTo(newFile)
    return "Renamed successfully"
  }

  fun deleteFileOrFolder(path: String): String {
    val file = File(path)
    if (!file.exists()) throw Exception("File or folder does not exist")

    if (file.isDirectory) file.deleteRecursively() else file.delete()
    return "Deleted successfully"
  }
}
