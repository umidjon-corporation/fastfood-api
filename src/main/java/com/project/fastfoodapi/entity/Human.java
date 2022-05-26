package com.project.fastfoodapi.entity;

import com.project.fastfoodapi.entity.enums.ClientStatus;
import com.project.fastfoodapi.entity.enums.Language;
import com.project.fastfoodapi.entity.enums.Region;
import com.project.fastfoodapi.entity.enums.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Human implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String number;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ClientStatus status=ClientStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language lang;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Attachment photo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.userType.getAuthorities();
    }

    @Override
    public String getUsername() {
        return this.number;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status==ClientStatus.ACTIVE;
    }
}
