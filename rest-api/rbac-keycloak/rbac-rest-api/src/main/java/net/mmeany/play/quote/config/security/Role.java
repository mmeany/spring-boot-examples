package net.mmeany.play.quote.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public enum Role {
    ANONYMOUS,
    MEMBER,
    SWAGGER,
    MANAGER(Role.MEMBER),
    ADMIN(Role.MANAGER, Role.SWAGGER);

    public static boolean exists(String name) {
        return Arrays.stream(values()).anyMatch(r -> r.name().equals(name));
    }

    private final List<Role> roles;

    Role() {
        roles = List.of(this);
    }

    Role(Role... parents) {
        Set<Role> a = new HashSet<>();
        a.add(this);
        Arrays.stream(parents).forEach(r -> a.addAll(r.roles));
        roles = a.stream().toList();
    }

    public List<Role> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    public List<? extends GrantedAuthority> asGrantedAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .toList();
    }
}
