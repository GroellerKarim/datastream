package eu.groeller.datastream

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DatastreamServerApplication

fun main(args: Array<String>) {
	runApplication<DatastreamServerApplication>(*args)
}
