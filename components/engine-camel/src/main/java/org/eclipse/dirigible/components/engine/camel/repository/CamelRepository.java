package org.eclipse.dirigible.components.engine.camel.repository;

import org.eclipse.dirigible.components.engine.camel.domain.Camel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("camelRepository")
public interface CamelRepository extends JpaRepository<Camel, Long> {
}
