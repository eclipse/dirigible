package org.eclipse.dirigible.integration.tests.ui.tests.multitenancy;

import java.util.UUID;

class DirigibleTestTenant {
    private final String name;
    private final String id;
    private final String subdomain;
    private final String username;
    private final String password;

    DirigibleTestTenant(String name, String subdomain) {
        this.name = name;
        this.id = UUID.randomUUID()
                      .toString();
        this.subdomain = subdomain;
        this.username = UUID.randomUUID()
                            .toString();
        this.password = UUID.randomUUID()
                            .toString();
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getName() {
        return name;
    }

    String getId() {
        return id;
    }

    String getSubdomain() {
        return subdomain;
    }

    @Override
    public String toString() {
        return "DirigibleTestTenant{" + "name='" + name + '\'' + ", id='" + id + '\'' + ", subdomain='" + subdomain + '\'' + ", username='"
                + username + '\'' + ", password='" + password + '\'' + '}';
    }
}
