package com.app.cloudide

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CloudIdeApplication

fun main(args: Array<String>) {
  runApplication<CloudIdeApplication>(*args)
}
