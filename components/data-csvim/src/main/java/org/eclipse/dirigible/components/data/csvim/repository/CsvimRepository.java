package org.eclipse.dirigible.components.data.csvim.repository;

import org.eclipse.dirigible.components.data.csvim.domain.Csvim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("csvimRepository")
public interface CsvimRepository extends JpaRepository<Csvim, Long> {
}
