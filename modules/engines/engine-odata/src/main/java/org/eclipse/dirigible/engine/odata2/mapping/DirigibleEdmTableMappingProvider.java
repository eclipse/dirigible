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
package org.eclipse.dirigible.engine.odata2.mapping;

import java.util.List;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.engine.odata2.api.IODataCoreService;
import org.eclipse.dirigible.engine.odata2.api.ODataException;
import org.eclipse.dirigible.engine.odata2.definition.ODataMappingDefinition;
import org.eclipse.dirigible.engine.odata2.service.ODataCoreService;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBinding;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingFactory;
import org.eclipse.dirigible.engine.odata2.sql.mapping.DefaultEdmTableMappingProvider;

public class DirigibleEdmTableMappingProvider extends DefaultEdmTableMappingProvider {
    
    public DirigibleEdmTableMappingProvider() throws ODataException {
    }
    
    @Override
    protected void fillBindings(String... resources) throws ODataException {
        EdmTableBindingFactory tableBindingFactory = new EdmTableBindingFactory();

        
        IODataCoreService odataCoreService = StaticInjector.getInjector().getInstance(ODataCoreService.class);
        List<ODataMappingDefinition> mappings = odataCoreService.getMappings();
        for (ODataMappingDefinition mapping : mappings) {
             EdmTableBinding binding = loadEdmTableBinding(tableBindingFactory, new String(mapping.getContent()));
             String fqn = binding.getEdmFullyQualifedName();
             getBindings().put(fqn, binding);
        }
    }
    
   
}
