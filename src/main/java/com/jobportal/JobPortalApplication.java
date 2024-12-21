package com.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class JobPortalApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(JobPortalApplication.class, args);
        
        // Get the ConsoleApplication bean and start the console interface
        ConsoleApplication consoleApp = context.getBean(ConsoleApplication.class);
        try {
            consoleApp.run(args);
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            context.close();
            System.exit(1);
        }
    }
}
