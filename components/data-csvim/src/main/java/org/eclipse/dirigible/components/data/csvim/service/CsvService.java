package org.eclipse.dirigible.components.data.csvim.service;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.data.csvim.domain.Csv;
import org.eclipse.dirigible.components.data.csvim.repository.CsvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CsvService implements ArtefactService<Csv> {

    /**
     * The csv repository.
     */
    @Autowired
    private CsvRepository csvRepository;


    @Override
    public List<Csv> getAll() {
        return csvRepository.findAll();
    }

    @Override
    public Page<Csv> getPages(Pageable pageable) {
        return csvRepository.findAll(pageable);
    }

    @Override
    public Csv findById(Long id) {
        Optional<Csv> csv = csvRepository.findById(id);
        if (csv.isPresent()) {
            return csv.get();
        } else {
            throw new IllegalArgumentException("Csv with id does not exist: " + id);
        }
    }

    @Override
    public Csv findByName(String name) {
        Csv filter = new Csv();
        filter.setName(name);
        Example<Csv> example = Example.of(filter);
        Optional<Csv> csv = csvRepository.findOne(example);
        if (csv.isPresent()) {
            return csv.get();
        } else {
            throw new IllegalArgumentException("Csv with name does not exist: " + name);
        }
    }

    @Override
    public List<Csv> findByLocation(String location) {
        Csv filter = new Csv();
        filter.setName(location);
        Example<Csv> example = Example.of(filter);
        return csvRepository.findAll(example);
    }

    @Override
    public Csv findByKey(String key) {
        Csv filter = new Csv();
        filter.setKey(key);
        Example<Csv> example = Example.of(filter);
        Optional<Csv> csv = csvRepository.findOne(example);
        return csv.orElse(null);
    }

    @Override
    public Csv save(Csv csv) {
        return csvRepository.saveAndFlush(csv);
    }

    @Override
    public void delete(Csv csv) {
        csvRepository.delete(csv);
    }
}
