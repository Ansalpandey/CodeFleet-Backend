package com.app.cloudide.controller

import com.app.cloudide.dto.FileRequest
import com.app.cloudide.dto.FileResponse
import com.app.cloudide.dto.RenameRequest
import com.app.cloudide.service.FileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class FileController(private val fileService: FileService) {

  @GetMapping("/list")
  fun listFiles(
    @RequestParam containerId: String,
    @RequestParam(defaultValue = "/workspace") basePath: String
  ): List<Map<String, Any>> {
    return fileService.listDirectory(containerId, basePath)
  }

  @GetMapping("/read")
  fun readFile(
    @RequestParam containerId: String,
    @RequestParam path: String
  ): ResponseEntity<FileResponse> {
    return ResponseEntity.ok(fileService.readFile(containerId, path))
  }


  @PostMapping("/update")
  fun updateFile(@RequestBody request: FileRequest): ResponseEntity<Map<String, String>> {
    return ResponseEntity.ok(mapOf("message" to fileService.updateFile(request.path, request.content ?: "")))
  }

  @PostMapping("/createFile")
  fun createFile(@RequestBody request: FileRequest): ResponseEntity<Map<String, String>> {
    return ResponseEntity.ok(mapOf("message" to fileService.createFile(request.path)))
  }

  @PostMapping("/createFolder")
  fun createFolder(@RequestBody request: FileRequest): ResponseEntity<Map<String, String>> {
    return ResponseEntity.ok(mapOf("message" to fileService.createFolder(request.path)))
  }

  @PostMapping("/rename")
  fun renameFileOrFolder(@RequestBody request: RenameRequest): ResponseEntity<Map<String, String>> {
    return ResponseEntity.ok(mapOf("message" to fileService.renameFileOrFolder(request.oldPath, request.newPath)))
  }

  @DeleteMapping("/delete")
  fun deleteFileOrFolder(@RequestParam path: String): ResponseEntity<Map<String, String>> {
    return ResponseEntity.ok(mapOf("message" to fileService.deleteFileOrFolder(path)))
  }
}
