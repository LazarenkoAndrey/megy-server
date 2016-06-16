package ru.megy.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import ru.megy.config.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication(scanBasePackages = "ru.megy.service")
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Application application = new Application();
        application.run(args);
    }

    public void run(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(  Application.class,
                                                                            DataSourceConfig.class,
                                                                            JpaConfig.class,
                                                                            MvcConfig.class,
                                                                            WebSecurityConfig.class,
                                                                            SchedulingConfig.class)
                .web(true)
                .run(args);
    }

}
