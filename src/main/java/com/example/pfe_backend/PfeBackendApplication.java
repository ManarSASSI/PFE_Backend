package com.example.pfe_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@EnableScheduling
@SpringBootApplication
public class PfeBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(PfeBackendApplication.class, args);

        String rawPassword = "Sarra@2003";
        String storedHash = "$2a$10$NfNtc/s0s/lcA59i4Jertu6eumI1YtCdnFkcn5J/d2ROf86a41k.m";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        boolean isMatch = encoder.matches(rawPassword, storedHash);

        System.out.println("Le mot de passe correspond au hash ? " + isMatch);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Email-Executor-");
        return executor;
    }


}
