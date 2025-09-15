package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Так как у тебя только одна роль (enum Role)
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Авторизация идёт по email
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Пока без блокировок
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Пока без блокировок
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Пока без блокировок
    }

    @Override
    public boolean isEnabled() {
        return true; // Можно добавить флаг enabled в User
    }

    public User getUser() {
        return user;
    }
}