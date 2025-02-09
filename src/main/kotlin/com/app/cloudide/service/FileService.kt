package com.app.cloudide.service

import com.app.cloudide.dto.FileInfo
import com.app.cloudide.dto.FileResponse
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
    return dockerService.getBaseFolderContents(containerId, basePath)
  }

  fun readFile(path: String): FileResponse {
    return try {
      val decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8.name())
      val file = File(decodedPath)

      if (!file.exists()) return FileResponse.Error("File or directory does not exist")

      if (file.isDirectory) {
        val files = file.listFiles()?.map {
          FileInfo(it.name, it.isDirectory, it.absolutePath)
        } ?: emptyList()
        FileResponse.DirectoryContent(files)
      } else {
        val content = Files.readString(Paths.get(decodedPath))
        FileResponse.FileContent(content)
      }
    } catch (e: SecurityException) {
      FileResponse.Error("Access denied")
    } catch (e: Exception) {
      FileResponse.Error("Unable to read file or directory: ${e.message}")
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
