/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.odata.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.eclipse.dirigible.components.odata.domain.ODataContainer;
import org.eclipse.dirigible.components.odata.domain.ODataSchema;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class ODataMetadataService.
 */
@Service
public class ODataMetadataService implements InitializingBean {

	/** The instance. */
	private static ODataMetadataService INSTANCE;

	/**
	 * After properties set.
	 *
	 * @throws Exception the exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		INSTANCE = this;
	}

	/**
	 * Gets the.
	 *
	 * @return the o data metadata service
	 */
	public static ODataMetadataService get() {
		return INSTANCE;
	}

	/**
	 * Gets the o data container service.
	 *
	 * @return the o data container service
	 */
	public ODataContainerService getODataContainerService() {
		return ODataContainerService.get();
	}

	/**
	 * Gets the o data schema service.
	 *
	 * @return the o data schema service
	 */
	public ODataSchemaService getODataSchemaService() {
		return ODataSchemaService.get();
	}

	/**
	 * Gets the metadata.
	 *
	 * @return the metadata
	 * @throws ODataException the o data exception
	 */
	public InputStream getMetadata() throws ODataException {
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		builder.append(
				"<edmx:Edmx xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\"  xmlns:sap=\"http://www.sap.com/Protocols/SAPData\" Version=\"1.0\">\n");
		builder.append(
				"    <edmx:DataServices m:DataServiceVersion=\"1.0\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\">\n");

		List<ODataSchema> schemas = getODataSchemaService().getAll();
		for (ODataSchema schema : schemas) {
			builder.append(new String(schema.getContent()));
			builder.append("\n");
		}

		builder	.append("<Schema Namespace=\"")
				.append("Default")
				.append("\"\n")
				.append("    xmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n");
		builder.append("    <EntityContainer Name=\"").append("Default").append("EntityContainer\" m:IsDefaultEntityContainer=\"true\">\n");
		List<ODataContainer> containers = getODataContainerService().getAll();
		for (ODataContainer container : containers) {
			builder.append(new String(container.getContent()));
			builder.append("\n");
		}
		builder.append("    </EntityContainer>\n");
		builder.append("</Schema>\n");

		builder.append("    </edmx:DataServices>\n");
		builder.append("</edmx:Edmx>\n");

		return new ByteArrayInputStream(builder.toString().getBytes());
	}

}
