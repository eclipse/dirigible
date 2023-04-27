package org.eclipse.dirigible.components.data.csvim.repository;

import org.eclipse.dirigible.components.data.csvim.domain.Csv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("csvRepository")
public interface CsvRepository extends JpaRepository<Csv, Long> {
}
