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
package org.eclipse.dirigible.components.data.store.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import org.eclipse.dirigible.commons.utils.xml2json.Xml2Json;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.data.store.DataStore;
import org.eclipse.dirigible.components.data.store.domain.Entity;
import org.eclipse.dirigible.components.data.store.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class BpmnSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(290)
public class EntityJsonSynchronizer<A extends Artefact> extends EntitySynchronizer<Entity> {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(EntityJsonSynchronizer.class);
	
	/** The Constant FILE_EXTENSION_BPMN. */
	public static final String FILE_EXTENSION_ENTITY = ".entity";
	
	/**
	 * Instantiates a new entity json synchronizer.
	 *
	 * @param entityService the entity service
	 * @param dataStore the data store
	 */
	public EntityJsonSynchronizer(EntityService entityService, DataStore dataStore) {
		super(entityService, dataStore);
	}
	
	/**
	 * Checks if is accepted.
	 *
	 * @param file the file
	 * @param attrs the attrs
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(Path file, BasicFileAttributes attrs) {
		return file.toString().endsWith(getFileExtension());
	}
	
	/**
	 * Checks if is accepted.
	 *
	 * @param type the artefact
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(String type) {
		return Entity.ARTEFACT_TYPE.equals(type);
	}

	/**
	 * Prepare content.
	 *
	 * @param entity the entity
	 * @return the string
	 */
	public String prepareContent(Entity entity) {
		String json = new String(entity.getContent(), StandardCharsets.UTF_8);
		return Xml2Json.toXml(json);
	}

	/**
	 * Gets the file entity.
	 *
	 * @return the file entity
	 */
	@Override
	public String getFileExtension() {
		return FILE_EXTENSION_ENTITY;
	}

}
