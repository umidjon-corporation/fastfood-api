package com.project.fastfoodapi.component;

import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.Language;
import com.project.fastfoodapi.entity.enums.Region;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.repository.HumanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    @Value("${spring.sql.init.mode:never}")
    private String initMode;
    final PasswordEncoder passwordEncoder;
    final HumanRepository humanRepository;
    @Override
    public void run(String... args) {
        if(initMode.equalsIgnoreCase("always")){
            humanRepository.save(Human.builder()
                            .userType(UserType.ADMIN)
                            .birthdate(LocalDate.parse("1991-01-23"))
                            .lang(Language.UZBEK)
                            .name("Tojiboyev Umidjon")
                            .number("+998990472436")
                            .region(Region.TASHKENT)
                            .password(passwordEncoder.encode("1234"))
                    .build());
        }
    }
}
