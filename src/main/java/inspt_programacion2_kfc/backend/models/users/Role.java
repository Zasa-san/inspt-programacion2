package inspt_programacion2_kfc.backend.models.users;

public enum Role {
    ROLE_SOPORTE,
    ROLE_VENDEDOR,
    ROLE_ADMIN;

    public String authority() {
        return name();
    }

    public String getRoleName() {
        String n = name();
        return n.startsWith("ROLE_") ? n.substring(5) : n;
    }

    @Override
    public String toString() {
        return authority();
    }
}
