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
package org.eclipse.dirigible.engine.odata2.sql.api;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataProcessor;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQueryBuilder;

public interface SQLProcessor extends ODataProcessor {

    /**
     * This callback method can be used to customize the property value that is
     * being written. The default implementation returns the provided value.
     * Note that this method is dependent on the sequence of the property in the
     * entity. If this property depends on other properties, consider using
     * {@link #onCustomizeEntityInstance(EdmEntityType, Object)}
     * 
     * 
     * @param entityType
     * @param property
     *            EDM property of the model that is currently being filled with
     *            data.
     * @param entityInstance
     *            The EDM instance which will is being populated with the DB
     *            data
     * @param value
     *            The DB value of the property. <code>null</code> if the
     *            property is not mapped to DB.
     * @return the customized property value. If you do not customize then just
     *         return the <code>value</code>
     * @throws ODataException
     */

    DataSource getDataSource();

    Object onCustomizePropertyValue(EdmStructuralType entityType, EdmProperty property, Object entityInstance, Object value)
            throws EdmException;


    SQLQueryBuilder getSQLQueryBuilder();

    Map<String, Object> onCustomizeExpandedNavigatonProperty(EdmStructuralType entityType, EdmStructuralType expandType,
            Map<String, Object> expandInstance) throws EdmException;

}