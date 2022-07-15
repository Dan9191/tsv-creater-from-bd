package ru.dan.tsvcreater;


import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.dan.tsvcreater.service.TsvCreator;


@SpringBootApplication
@AllArgsConstructor
public class Application implements CommandLineRunner {

    private TsvCreator tsvCreator;

        private static Logger LOG = LoggerFactory
                .getLogger(Application.class);

        public static void main(String[] args) {
            LOG.info("STARTING THE APPLICATION");
            SpringApplication springApplication = new SpringApplication(Application.class);
            springApplication.setWebApplicationType(WebApplicationType.NONE);
            springApplication.setBannerMode(Banner.Mode.OFF);
            springApplication.run(args);
            LOG.info("APPLICATION FINISHED");
        }

        @Override
        public void run(String... args) {
            LOG.info("EXECUTING : command line runner");
            tsvCreator.createTsvFile();
        }
}
