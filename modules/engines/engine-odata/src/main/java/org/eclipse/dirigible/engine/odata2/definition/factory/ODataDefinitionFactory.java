/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.definition.factory;

import java.sql.Timestamp;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;

/**
 * A factory for creating ODataDefinition objects.
 */
public class ODataDefinitionFactory {
	
	/**
	 * Parses the O data.
	 *
	 * @param contentPath the content path
	 * @param data the data
	 * @return the o data definition
	 */
	public static ODataDefinition parseOData(String contentPath, String data) {
		ODataDefinition odataDefinition = GsonHelper.GSON.fromJson(data, ODataDefinition.class);
		odataDefinition.setLocation(contentPath);
		odataDefinition.setHash(DigestUtils.md5Hex(data));
		odataDefinition.setCreatedBy(UserFacade.getName());
		odataDefinition.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
		
		odataDefinition.getAssociations().forEach(association -> {
			if (association.getFrom().getProperty() != null) {
				association.getFrom().getProperties().add(association.getFrom().getProperty());
			}
			if (association.getTo().getProperty() != null) {
				association.getTo().getProperties().add(association.getTo().getProperty());
			}
		});
		
		return odataDefinition;
	}

}
