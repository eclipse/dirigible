/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.entities.northwind;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.annotation.edm.EdmEntityType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFacets;
import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty.Multiplicity;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

@EdmEntityType(name = "PersonDetail")
@EdmEntitySet(name = "PersonDetails", container = "DemoService")
public class PersonDetail {

	@EdmKey
	@EdmProperty(name = "PersonID", facets = @EdmFacets(nullable = false))
	private Integer personId;

	@EdmProperty(type = EdmType.BYTE, facets = @EdmFacets(nullable = false))
	private Integer age;

	@EdmProperty(facets = @EdmFacets(nullable = false))
	private Boolean gender;

	@EdmProperty
	private String phone;

	@EdmProperty
	private Address address;

	@EdmNavigationProperty( //
			toMultiplicity = Multiplicity.ZERO_OR_ONE, //
			toType = Person.class, //
			toRole = "Person_PersonDetail", //
			association = INorthwindODataAssociations.Person_PersonDetail_PersonDetail_Person //
	)
	private Person person;
}
