package org.eclipse.dirigible.components.base.http.roles;

public enum Roles {

    ADMINISTRATOR("ROLE_ADMINISTRATOR"), TENANT_ADMINISTRATOR("ROLE_TENANT_ADMINISTRATOR"), USER("ROLE_USER"), DEVELOPER(
            "ROLE_DEVELOPER"), OPERATOR("ROLE_OPERATOR");

    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

}
