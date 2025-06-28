package tn.esprithub.server.common.enums;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    CHIEF("ROLE_CHIEF"),
    TEACHER("ROLE_TEACHER"),
    STUDENT("ROLE_STUDENT");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
