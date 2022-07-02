package com.project.fastfoodapi.repository;

import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.HumanStatus;
import com.project.fastfoodapi.entity.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HumanRepository extends JpaRepository<Human, Long> {
    Optional<Human> findByStatusIsNotAndId(HumanStatus status, Long id);

    List<Human> findByUserTypeEquals(UserType userType);

    List<Human> findByUserTypeEqualsAndStatusIsNot(UserType userType, HumanStatus status);

    Optional<Human> findByNumber(String number);
}