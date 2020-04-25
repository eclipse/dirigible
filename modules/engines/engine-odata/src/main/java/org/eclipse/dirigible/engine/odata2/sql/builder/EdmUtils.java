/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.sql.builder;

import static java.lang.String.format;
import static org.apache.olingo.odata2.api.commons.HttpStatusCodes.INTERNAL_SERVER_ERROR;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeException;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.uri.SelectItem;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EdmUtils {

    private EdmUtils() {

    }

    private static final Logger LOG = LoggerFactory.getLogger(EdmUtils.class);

    /**
     * Extract selected properties from query option. This method assumes ONLY
     * Properties of Entity in the items but NOT NavigationPaths or '*' terms.
     * Both cases are considered NOT IMPLEMENTED and will yield an exception
     * 
     * @param selectedItems the selected items
     * @param entityType the entity type
     * @return the selected properties
     * @throws EdmException in case of an edm error
     * @throws ODataNotImplementedException in case of missing feature
     */
    public static Collection<EdmProperty> getSelectedProperties(List<SelectItem> selectedItems, EdmStructuralType entityType)
            throws EdmException, ODataNotImplementedException {
        Collection<String> propertyNames = getSelectedPropertyNames(selectedItems, entityType);
        Collection<EdmProperty> result = new ArrayList<EdmProperty>();

        for (String propertyName : propertyNames) {
            EdmTyped property = entityType.getProperty(propertyName);
            result.add((EdmProperty) property);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Collection<EdmProperty> getProperties(EdmStructuralType entityType) throws EdmException, ODataNotImplementedException {
        Collection<String> propertyNames = getSelectedPropertyNames(Collections.EMPTY_LIST, entityType);
        Collection<EdmProperty> result = new ArrayList<EdmProperty>();

        for (String propertyName : propertyNames) {
            EdmTyped property = entityType.getProperty(propertyName);
            result.add((EdmProperty) property);
        }
        return result;
    }

    /**
     * IMPORTANT: This method does not make much sense in general case.
     * 
     * SelectItem may not only contain plain Properties but NavigationPath or
     * even an '*' flag as well. Both latter cases are considered as
     * "Note implemented" (yet)
     * 
     * throws EdmException. Determines which object properties have to be
     * queried and populated in the entity.
     * <ul>
     * <li>If no select has been specified, all of the entity type's properties
     * are returned</li>
     * <li>If a select has been specified, the selected properties plus all
     * not-selected key properties are returned</li>
     * </ul>
     * 
     * @param selectedItems the selected items
     * @param type the edm type
     * @return the list of the selected property names
     * @throws EdmException in case of an edm error
     * @throws ODataNotImplementedException in case of a missing feature
     */
    public static Collection<String> getSelectedPropertyNames(List<SelectItem> selectedItems, EdmStructuralType type)
            throws EdmException, ODataNotImplementedException {
        final Set<String> selectedPropertyNames = getSelectedPropertyNames(selectedItems);
        final List<String> allPropertyNames = type.getPropertyNames();

        Collection<String> namesOfEdmPropertiesToBePopulated;
        if (selectedPropertyNames.isEmpty()) {
            // no select specified in uri -> select all
            namesOfEdmPropertiesToBePopulated = allPropertyNames;
        } else {
            if (!allPropertyNames.containsAll(selectedPropertyNames)) {
                Set<String> nonExistingProperties = new HashSet<String>(selectedPropertyNames);
                nonExistingProperties.removeAll(allPropertyNames);
                throw new OData2Exception(format("Some of the selected properties don't exist: %s", nonExistingProperties.toString()),
                        HttpStatusCodes.BAD_REQUEST);
            }
            // Ensure key properties are always read even if not selected (those are required to build the self link, NullPointerException will occur otherwise)
            final List<String> keyPropertyNames = type instanceof EdmEntityType ? ((EdmEntityType) type).getKeyPropertyNames()
                    : Collections.<String> emptyList();
            selectedPropertyNames.addAll(keyPropertyNames);
            namesOfEdmPropertiesToBePopulated = selectedPropertyNames;
        }
        return namesOfEdmPropertiesToBePopulated;
    }

    private static Set<String> getSelectedPropertyNames(List<SelectItem> selectedPropertyNames)
            throws EdmException, ODataNotImplementedException {
        Set<String> result = new HashSet<String>();
        for (SelectItem selectItem : selectedPropertyNames) {
            if (selectItem.getNavigationPropertySegments() != null && !(selectItem.getNavigationPropertySegments().isEmpty()) //
                    || selectItem.getProperty() == null //
                    || selectItem.isStar() //
            ) {
                LOG.error("SelectItems with NavigationPath or 'Star' values are not implemented yet!");
                throw new ODataNotImplementedException();
            }
            result.add(selectItem.getProperty().getName());
        }
        return result;
    }

    /**
     * This method evaluates the expression based on the type instance. Used for
     * adding escape characters where necessary.
     * 
     * @param value the datetime instance
     * @param edmSimpleType edm type
     * @return the evaluated expression
     */
    public static Object evaluateDateTimeExpressions(Object value, final EdmSimpleType edmSimpleType) {
        if (edmSimpleType == EdmSimpleTypeKind.DateTime.getEdmSimpleTypeInstance()
                || edmSimpleType == EdmSimpleTypeKind.DateTimeOffset.getEdmSimpleTypeInstance()) {
            try {
                Calendar datetime = (Calendar) edmSimpleType.valueOfString(String.valueOf(value), EdmLiteralKind.DEFAULT, null,
                        edmSimpleType.getDefaultType());
                return datetime;
            } catch (EdmSimpleTypeException e) {
                throw new OData2Exception(e.getMessage(), INTERNAL_SERVER_ERROR, e);
            }
        } else if (edmSimpleType == EdmSimpleTypeKind.Time.getEdmSimpleTypeInstance()) {
            try {
                Calendar time = (Calendar) edmSimpleType.valueOfString(String.valueOf(value), EdmLiteralKind.DEFAULT, null,
                        edmSimpleType.getDefaultType());
                return time;
            } catch (EdmSimpleTypeException e) {
                throw new OData2Exception(e.getMessage(), INTERNAL_SERVER_ERROR, e);
            }
        }
        //nothing to be done
        return value;
    }
}
