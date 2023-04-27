package org.eclipse.dirigible.components.data.csvim.repository;

import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("csvFileRepository")
public interface CsvFileRepository extends JpaRepository<CsvFile, Long> {
}
