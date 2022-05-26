package com.project.fastfoodapi.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum UserType {
    CLIENT(List.of("CLIENT")),
    ADMIN(List.of("ADMIN")),
    OPERATOR(List.of("OPERATOR")),
    COURIER(List.of("COURIER"));
    final Set<GrantedAuthority> authorities;

    UserType(Collection<String> authorities){
        if(authorities.isEmpty()){
            throw new Error("Authorities can't be empty");
        }
        this.authorities =authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }
}
