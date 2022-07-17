package ru.dan.tsvcreater;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.dan.tsvcreater.service.TsvCreator;

@Slf4j
@SpringBootApplication
@AllArgsConstructor
public class Application implements CommandLineRunner {

    private TsvCreator tsvCreator;
        public static void main(String[] args) {
            log.info("STARTING THE APPLICATION");
            SpringApplication springApplication = new SpringApplication(Application.class);
            springApplication.setWebApplicationType(WebApplicationType.NONE);
            springApplication.setBannerMode(Banner.Mode.OFF);
            springApplication.run(args);
            log.info("APPLICATION FINISHED");
        }

        @Override
        public void run(String... args) {
            log.info("EXECUTING : command line runner");
            tsvCreator.createTsvFile();
        }
}
