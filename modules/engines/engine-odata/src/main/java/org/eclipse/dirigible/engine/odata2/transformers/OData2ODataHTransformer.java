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
package org.eclipse.dirigible.engine.odata2.transformers;

import org.eclipse.dirigible.engine.odata2.definition.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class OData2ODataHTransformer {

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
