package org.eclipse.dirigible.components.tenants.domain;

import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "DIRIGIBLE_USERS", uniqueConstraints = {@UniqueConstraint(columnNames = {"USER_TENANT_ID", "USER_EMAIL"})})
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_TENANT_ID")
    private Tenant tenant;

    @Column(name = "USER_EMAIL", nullable = false)
    private String email;

    @Column(name = "USER_PASSWORD", nullable = false)
    private String password;

    @Column(name = "USER_ROLE", nullable = false)
    private Roles role;

    public User() {}

    public User(Tenant tenant, String email, String password, Roles role) {
        this.tenant = tenant;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
}
