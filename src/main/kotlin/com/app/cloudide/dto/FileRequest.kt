package com.app.cloudide.dto

data class FileRequest(val path: String, val content: String? = null)
data class RenameRequest(val oldPath: String, val newPath: String)

data class FileInfo(val name: String, val isDirectory: Boolean, val path: String)

sealed class FileResponse {
  data class FileContent(val content: String) : FileResponse()
  data class DirectoryContent(val content: List<FileInfo>) : FileResponse()
  data class Error(val message: String) : FileResponse()
}