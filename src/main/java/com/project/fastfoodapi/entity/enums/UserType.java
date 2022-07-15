package com.project.fastfoodapi.entity.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum UserType {
    CLIENT(List.of("ROLE_CLIENT")),
    ADMIN(List.of("ROLE_ADMIN")),
    SUPER_ADMIN(List.of("ROLE_SUPER_ADMIN")),
    OPERATOR(List.of("ROLE_OPERATOR")),
    COURIER(List.of("ROLE_COURIER"));
    final Set<GrantedAuthority> authorities;

    UserType(Collection<String> authorities){
        if(authorities.isEmpty()){
            throw new Error("Authorities can't be empty");
        }
        this.authorities =authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }
}
