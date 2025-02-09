package com.app.cloudide

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.CrossOrigin

@SpringBootApplication
class CloudIdeApplication

fun main(args: Array<String>) {
  runApplication<CloudIdeApplication>(*args)
}
