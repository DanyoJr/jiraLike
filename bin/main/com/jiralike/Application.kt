package com.jiralike

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JiraLikeApplication

fun main(args: Array<String>) {
    runApplication<JiraLikeApplication>(*args)
}
