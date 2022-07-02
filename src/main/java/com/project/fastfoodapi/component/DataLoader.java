package com.project.fastfoodapi.component;

import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.HumanStatus;
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
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    final PasswordEncoder passwordEncoder;
    final HumanRepository humanRepository;
    @Value("${spring.sql.init.mode:never}")
    private String initMode;

    @Override
    public void run(String... args) {
        List<Human> humans = humanRepository.findByUserTypeEqualsAndStatusIsNot(UserType.ADMIN, HumanStatus.DELETED);
        if (initMode.equalsIgnoreCase("always") || humans.isEmpty()) {
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
