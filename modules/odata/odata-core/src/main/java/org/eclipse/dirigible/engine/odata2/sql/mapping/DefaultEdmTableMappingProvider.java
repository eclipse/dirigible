/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.mapping;

import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.eclipse.dirigible.engine.odata2.api.ODataException;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingFactory;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;

public class DefaultEdmTableMappingProvider implements EdmTableBindingProvider {
    private final Map<String, EdmTableBinding> bindings;
    private final String MAPPING_NOT_FOUND_MSG = "Invalid table mapping configuration for entity %s";

  
    public DefaultEdmTableMappingProvider(String... resources) throws ODataException {
        bindings = new HashMap<String, EdmTableBinding>();

        fillBindings(resources);
    }

    protected void fillBindings(String... resources)  throws ODataException {
        EdmTableBindingFactory tableBindingFactory = new EdmTableBindingFactory();

        for (String resource : resources) {
            EdmTableBinding binding = loadEdmTableBinding(tableBindingFactory, DefaultEdmTableMappingProvider.class.getClassLoader(), resource);
            String fqn = binding.getEdmFullyQualifedName();
            bindings.put(fqn, binding);
        }
    }
    
    protected Map<String, EdmTableBinding> getBindings(){
        return bindings;
    }

    @Override
    public EdmTableBinding getEdmTableBinding(EdmStructuralType entityType) {
        return this.getTableBinding(fqn(entityType));

    }

    private EdmTableBinding loadEdmTableBinding(EdmTableBindingFactory tableBindingFactory, ClassLoader mappingsLoader, String resource) {
        try {
            return tableBindingFactory.createTableBinding(mappingsLoader, resource);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(MAPPING_NOT_FOUND_MSG, resource));
        }
    }
    
    public EdmTableBinding loadEdmTableBinding(EdmTableBindingFactory tableBindingFactory, String resource) {
        try {
        	return tableBindingFactory.createTableBinding(new ByteArrayInputStream(resource.getBytes()));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(MAPPING_NOT_FOUND_MSG, resource));
        }
    }

    private EdmTableBinding getTableBinding(String fqn) {
        EdmTableBinding binding = bindings.get(fqn);
        if (binding == null) {
            throw new IllegalArgumentException("Unable to find binding for FQN " + fqn);
        } else {
            return binding;
        }

    }

}
