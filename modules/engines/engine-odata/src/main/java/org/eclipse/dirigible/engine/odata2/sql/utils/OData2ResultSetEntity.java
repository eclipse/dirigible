/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holder object for reading result sets
 */
public class OData2ResultSetEntity {
    private final Map<String, Object> entitiyPropertiesData;
    private Map<String, List<Object>> expandData;

    public OData2ResultSetEntity(Map<String, Object> entitiyPropertiesData) {
        this.entitiyPropertiesData = entitiyPropertiesData;
        this.expandData = new HashMap<String, List<Object>>();
    }

    public Map<String, Object> getEntitiyPropertiesData() {
        return entitiyPropertiesData;
    }

    public Map<String, List<Object>> getExpandData() {
        return expandData;
    }

    public void setExpandData(Map<String, List<Object>> expandData) {
        this.expandData = expandData;
    }

    @Override
    public String toString() {
        return "OData2ResultSetEntity [" + entitiyPropertiesData + "]"; 
    }

}
