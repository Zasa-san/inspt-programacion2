package inspt_programacion2_kfc.models.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    public String authority() {
        return name();
    }

    public String withoutPrefix() {
        String n = name();
        return n.startsWith("ROLE_") ? n.substring(5) : n;
    }

    public GrantedAuthority asAuthority() {
        return new SimpleGrantedAuthority(authority());
    }

    @Override
    public String toString() {
        return authority();
    }
}
