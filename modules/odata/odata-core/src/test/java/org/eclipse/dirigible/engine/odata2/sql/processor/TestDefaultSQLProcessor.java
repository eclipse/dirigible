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
package org.eclipse.dirigible.engine.odata2.sql.processor;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.eclipse.dirigible.engine.odata2.sql.binding.EdmTableBindingProvider;
import org.eclipse.dirigible.engine.odata2.sql.processor.DefaultSQLProcessor;

public class TestDefaultSQLProcessor extends DefaultSQLProcessor {

    public TestDefaultSQLProcessor(EdmTableBindingProvider tableMappingProvider) {
        super(tableMappingProvider);

    }

    @Override
    public Object onCustomizePropertyValue(EdmStructuralType entityType, EdmProperty property, Object entityInstance, Object value)
            throws EdmException {

        if ("ConvertValue".equals(property.getName()) && (value instanceof Number)) {
            return String.valueOf(value);
        }

        return value;
    }

}
