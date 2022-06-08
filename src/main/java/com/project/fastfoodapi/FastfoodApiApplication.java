package com.project.fastfoodapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
public class FastfoodApiApplication {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        TimeZone tzone = TimeZone.getTimeZone("Asia/Tashkent");
        TimeZone.setDefault(tzone);
        SpringApplication.run(FastfoodApiApplication.class, args);
    }

}
