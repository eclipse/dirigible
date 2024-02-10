package org.eclipse.dirigible.components.tenants.domain;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A tenant owns/maintains a sub-section of the web application. Can be thought of as a website
 * within the application. Users can register for multiple tenant without them knowing that each
 * separate tenant is part of one and the same application. So the uniqueness of user accounts is
 * determined by the combination of the user's unique ID (= email) combined with the tenant ID.
 */
@Entity
@Table(name = "DIRIGIBLE_TENANTS")
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TENANT_ID", nullable = false)
    private long id;

    @Column(name = "TENANT_SLUG", unique = true, nullable = false)
    private String slug;

    @Column(name = "TENANT_NAME", nullable = false)
    private String name;

    public Tenant() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
