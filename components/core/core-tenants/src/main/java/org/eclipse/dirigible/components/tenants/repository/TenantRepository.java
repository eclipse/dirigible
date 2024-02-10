package org.eclipse.dirigible.components.tenants.repository;

import java.util.Optional;

import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findBySlug(String slug);
}
