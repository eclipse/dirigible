/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.sql.binding;

import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.engine.odata2.api.IODataCoreService;
import org.eclipse.dirigible.engine.odata2.api.ODataException;
import org.eclipse.dirigible.engine.odata2.definition.ODataMappingDefinition;
import org.eclipse.dirigible.engine.odata2.service.ODataCoreService;

public class DefaultEdmTableMappingProvider implements EdmTableBindingProvider {
    private final Map<String, EdmTableBinding> bindings;
    private final String MAPPING_NOT_FOUND_MSG = "Invalid table mapping configuration for entity %s";

    public DefaultEdmTableMappingProvider(String... resources) {
        this(DefaultEdmTableMappingProvider.class.getClassLoader(), resources);
    }

    public DefaultEdmTableMappingProvider(ClassLoader loader, String... resources) {
        bindings = new HashMap<String, EdmTableBinding>();
        EdmTableBindingFactory tableBindingFactory = new EdmTableBindingFactory();

        for (String resource : resources) {
            EdmTableBinding binding = loadEdmTableBinding(tableBindingFactory, loader, resource);
            String fqn = binding.getEdmFullyQualifedName();
            bindings.put(fqn, binding);
        }
    }
    
    public DefaultEdmTableMappingProvider() throws ODataException {
    	IODataCoreService odataCoreService = StaticInjector.getInjector().getInstance(ODataCoreService.class);
        bindings = new HashMap<String, EdmTableBinding>();
        EdmTableBindingFactory tableBindingFactory = new EdmTableBindingFactory();

        List<ODataMappingDefinition> mappings = odataCoreService.getMappings();
        for (ODataMappingDefinition mapping : mappings) {
        	 EdmTableBinding binding = loadEdmTableBinding(tableBindingFactory, new String(mapping.getContent()));
             String fqn = binding.getEdmFullyQualifedName();
             bindings.put(fqn, binding);
        }
        
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
    
    private EdmTableBinding loadEdmTableBinding(EdmTableBindingFactory tableBindingFactory, String resource) {
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
