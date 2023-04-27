package org.eclipse.dirigible.components.data.csvim.service;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.csvim.domain.Csvim;
import org.eclipse.dirigible.components.data.csvim.repository.CsvFileRepository;
import org.eclipse.dirigible.components.data.structures.domain.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CsvFileService implements ArtefactService<CsvFile> {

    @Autowired
    private CsvFileRepository csvFileRepository;

    @Override
    public List<CsvFile> getAll() {
        return csvFileRepository.findAll();
    }

    @Override
    public Page<CsvFile> getPages(Pageable pageable) {
        return csvFileRepository.findAll(pageable);
    }

    @Override
    public CsvFile findById(Long id) {
        Optional<CsvFile> csvFile = csvFileRepository.findById(id);
        if (csvFile.isPresent()) {
            return csvFile.get();
        } else {
            throw new IllegalArgumentException("CsvFile with id does not exist: " + id);
        }
    }

    @Override
    public CsvFile findByName(String name) {
        CsvFile filter = new CsvFile();
        filter.setName(name);
        Example<CsvFile> example = Example.of(filter);
        Optional<CsvFile> csvFile = csvFileRepository.findOne(example);
        if (csvFile.isPresent()) {
            return csvFile.get();
        } else {
            throw new IllegalArgumentException("CsvFile with name does not exist: " + name);
        }
    }

    @Override
    public List<CsvFile> findByLocation(String location) {
        CsvFile filter = new CsvFile();
        filter.setName(location);
        Example<CsvFile> example = Example.of(filter);
        return csvFileRepository.findAll(example);
    }

    @Override
    public CsvFile findByKey(String key) {
        CsvFile filter = new CsvFile();
        filter.setKey(key);
        Example<CsvFile> example = Example.of(filter);
        Optional<CsvFile> csvFile = csvFileRepository.findOne(example);
        return csvFile.orElse(null);
    }

    @Override
    public CsvFile save(CsvFile csvFile) {
        return csvFileRepository.saveAndFlush(csvFile);
    }

    @Override
    public void delete(CsvFile csvFile) {
        csvFileRepository.delete(csvFile);
    }
}
