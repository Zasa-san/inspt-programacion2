package inspt_programacion2_kfc.backend.models.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    ROLE_USER,
    ROLE_VENDEDOR,
    ROLE_ADMIN;

    public String authority() {
        return name();
    }

    public String getRoleName() {
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
