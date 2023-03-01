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
package org.eclipse.dirigible.engine.odata2.transformers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataEntityDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataHandlerDefinition;

/**
 * The Class OData2ODataHTransformer.
 */
public class OData2ODataHTransformer {

    /**
     * Transform.
     *
     * @param model the model
     * @return the list
     * @throws SQLException the SQL exception
     */
    public List<ODataHandlerDefinition> transform(ODataDefinition model) throws SQLException {

        List<ODataHandlerDefinition> result = new ArrayList<>();

        for (ODataEntityDefinition entity : model.getEntities()) {
            String namespace = model.getNamespace();
            String name = entity.getName();

            entity.getHandlers().forEach(handler -> {
                ODataMetadataUtil.validateHandlerDefinitionMethod(handler.getMethod(), entity.getName());
                ODataMetadataUtil.validateHandlerDefinitionTypes(handler.getType(), entity.getName());

                ODataHandlerDefinition handlerDefinition = new ODataHandlerDefinition();
                handlerDefinition.setNamespace(namespace);
                handlerDefinition.setName(name + "Type");
                handlerDefinition.setMethod(handler.getMethod());
                handlerDefinition.setType(handler.getType());
                handlerDefinition.setHandler(handler.getHandler());
                result.add(handlerDefinition);
            });
        }
        return result;
    }
}
