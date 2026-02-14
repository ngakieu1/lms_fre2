package com.topcit.aims.aims.application;

import com.topcit.aims.aims.domain.repository.UserRepository;
import com.topcit.aims.aims.infrastructure.persistence.entity.Permission;
import com.topcit.aims.aims.infrastructure.persistence.entity.Role;
import com.topcit.aims.aims.infrastructure.persistence.entity.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserJpaEntity user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
        Role role = user.getRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
        if (role.getName() != null && role.getName().startsWith("TEACHER_")){
            authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
        }
        for (Permission p : role.getPermissions()){
            authorities.add(new SimpleGrantedAuthority(p.getCodePermission()));
        }
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}

