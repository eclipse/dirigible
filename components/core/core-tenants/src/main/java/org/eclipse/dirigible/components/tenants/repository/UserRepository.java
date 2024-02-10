package org.eclipse.dirigible.components.tenants.repository;

import java.util.Optional;

import org.eclipse.dirigible.components.tenants.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT user FROM User AS user INNER JOIN FETCH user.tenant" + " WHERE user.email = :email" + " AND user.tenant.slug = :slug")
    Optional<User> findUser(String email, String slug);

    @Query("SELECT user FROM User AS user" + " WHERE user.tenant IS NULL" + " AND user.email = :email" + " AND user.role = 0")
    Optional<User> findGeneralAdmin(String email);
}
