/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.wiki.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.engine.wiki.domain.Confluence;
import org.eclipse.dirigible.components.engine.wiki.domain.Markdown;
import org.eclipse.dirigible.components.engine.wiki.repository.ConfluenceRepository;
import org.eclipse.dirigible.components.engine.wiki.repository.MarkdownRepository;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class MarkdownService.
 */

@Service
@Transactional
public class MarkdownService implements ArtefactService<Markdown> {

    /** The Markdown repository. */
    @Autowired
    private MarkdownRepository markdownRepository;

    /**
     * The repository.
     */
    private IRepository repository;

    /**
     * Instantiates a new markdown service.
     * 
     * @param repository the repository
     */
    @Autowired
    public MarkdownService(IRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets the repository.
     *
     * @return the repository
     */
    protected IRepository getRepository() {
        return repository;
    }

    /**
     * Gets the resource.
     *
     * @param path the path
     * @return the resource
     */
    public IResource getResource(String path) {
        return getRepository().getResource(path);
    }

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    @Transactional(readOnly = true)
    public List<Markdown> getAll() {
        return markdownRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Markdown> getPages(Pageable pageable) {
        return markdownRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the markdown
     */
    @Override
    @Transactional(readOnly = true)
    public Markdown findById(Long id) {
        Optional<Markdown> markdown = markdownRepository.findById(id);
        if (markdown.isPresent()) {
            return markdown.get();
        } else {
            throw new IllegalArgumentException("Markdown with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the markdown
     */
    @Override
    @Transactional(readOnly = true)
    public Markdown findByName(String name) {
    	Markdown filter = new Markdown();
        filter.setName(name);
        Example<Markdown> example = Example.of(filter);
        Optional<Markdown> markdown = markdownRepository.findOne(example);
        if (markdown.isPresent()) {
            return markdown.get();
        } else {
            throw new IllegalArgumentException("Markdown with name does not exist: " + name);
        }
    }
    
    /**
     * Find by key.
     *
     * @param key the key
     * @return the markdown
     */
    @Override
    @Transactional(readOnly = true)
    public Markdown findByKey(String key) {
    	Markdown filter = new Markdown();
        filter.setKey(key);
        Example<Markdown> example = Example.of(filter);
        Optional<Markdown> markdown = markdownRepository.findOne(example);
        if (markdown.isPresent()) {
            return markdown.get();
        }
        return null;
    }

    /**
     * Save.
     *
     * @param markdown the markdown
     * @return the markdown
     */
    @Override
    public Markdown save(Markdown markdown) {
        return markdownRepository.saveAndFlush(markdown);
    }

    /**
     * Delete.
     *
     * @param markdown the markdown
     */
    @Override
    public void delete(Markdown markdown) {
    	markdownRepository.delete(markdown);
    }
}
