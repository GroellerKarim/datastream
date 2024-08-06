package eu.groeller.datastreamserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.DayOfWeek;

@SpringBootApplication
public class DatastreamServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatastreamServerApplication.class, args);
    }

}
