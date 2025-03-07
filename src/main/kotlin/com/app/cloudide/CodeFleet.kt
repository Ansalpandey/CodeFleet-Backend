package com.app.cloudide

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.validation.annotation.Validated

@SpringBootApplication
@Validated
class CloudIdeApplication

fun main(args: Array<String>) {
  runApplication<CloudIdeApplication>(*args)
}
