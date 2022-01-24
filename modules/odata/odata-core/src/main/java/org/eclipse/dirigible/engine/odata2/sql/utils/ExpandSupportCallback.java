/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.utils;

import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.callback.OnWriteEntryContent;
import org.apache.olingo.odata2.api.ep.callback.OnWriteFeedContent;
import org.apache.olingo.odata2.api.ep.callback.WriteCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteEntryCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteEntryCallbackResult;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackResult;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;

public class ExpandSupportCallback implements OnWriteEntryContent, OnWriteFeedContent {

    private final ODataContext context;
    private final List<OData2ResultSetEntity> feedData;

    public ExpandSupportCallback(ODataContext context, List<OData2ResultSetEntity> feedData) {
        this.context = context;
        this.feedData = feedData;
    }

    @SuppressWarnings("unchecked")
    private List<Object> getRelatedData(final WriteCallbackContext writeCallbackContext) throws ODataException {

        EdmEntityType sourceType = writeCallbackContext.getSourceEntitySet().getEntityType();
        String fqn = fqn(writeCallbackContext.getNavigationProperty().getType());
        Map<String, Object> entryData = writeCallbackContext.getEntryData();
        for (OData2ResultSetEntity feedEntity : feedData) {
            if (OData2Utils.isSameInstance(sourceType, feedEntity, entryData)) {
                if (feedEntity.getExpandData().containsKey(fqn)) {
                    List<Object> list = feedEntity.getExpandData().get(fqn);
                    List<Object> rawData = new ArrayList<Object>();
                    for (Object data : list) {
                        if (data instanceof OData2ResultSetEntity) {
                            OData2ResultSetEntity d = (OData2ResultSetEntity) data;
                            rawData.add(d.getEntitiyPropertiesData());
                        } else {
                            rawData.add(data);
                        }
                    }
                    return rawData;
                } else {
                    return Collections.EMPTY_LIST;
                }
            }
        }
        return Collections.EMPTY_LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WriteFeedCallbackResult retrieveFeedResult(WriteFeedCallbackContext writeFeedContext) throws ODataApplicationException {
        try {
            final EdmEntityType entityType = writeFeedContext.getSourceEntitySet()
                    .getRelatedEntitySet(writeFeedContext.getNavigationProperty()).getEntityType();
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            List<Object> relatedData = null;
            try {
                relatedData = getRelatedData(writeFeedContext);
                for (final Object entryData : (List<?>) relatedData) {
                    if (entryData instanceof Map) {
                        values.add((Map<String, Object>) entryData);
                    } else {
                        throw new IllegalArgumentException("Serialization of " + entryData.getClass().getName() + " not supported yet!");
                    }
                }
            } catch (final ODataNotFoundException e) {
                values.clear();
            }
            WriteFeedCallbackResult result = new WriteFeedCallbackResult();
            result.setFeedData(values);
            EntityProviderWriteProperties inlineProperties = EntityProviderWriteProperties
                    .serviceRoot(context.getPathInfo().getServiceRoot()).callbacks(getCallbacks(feedData, entityType))
                    .expandSelectTree(writeFeedContext.getCurrentExpandSelectTreeNode()).selfLink(writeFeedContext.getSelfLink()).build();
            result.setInlineProperties(inlineProperties);
            return result;
        } catch (final ODataException e) {
            throw new OData2Exception(HttpStatusCodes.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public WriteEntryCallbackResult retrieveEntryResult(WriteEntryCallbackContext writeEntryContext) throws ODataApplicationException {
        try {
            final EdmEntityType entityType = writeEntryContext.getSourceEntitySet()
                    .getRelatedEntitySet(writeEntryContext.getNavigationProperty()).getEntityType();
            WriteEntryCallbackResult result = new WriteEntryCallbackResult();
            List<Object> relatedData;
            try {
                relatedData = getRelatedData(writeEntryContext);
            } catch (final ODataNotFoundException e) {
                relatedData = null;
            }

            if (relatedData == null) {
                result.setEntryData(Collections.<String, Object> emptyMap());
            } else {
                if (writeEntryContext.getNavigationProperty().getMultiplicity() == EdmMultiplicity.ZERO_TO_ONE
                        || writeEntryContext.getNavigationProperty().getMultiplicity() == EdmMultiplicity.ONE) {
                    //handles many to one case (http://localhost:8080/api/v1/Entity?$expand=Child)
                    List<Object> rd = (List<Object>) relatedData;
                    if (rd.size() == 1) {
                        Object relatedDataElement = rd.get(0);
                        if (relatedDataElement instanceof Map) {
                            result.setEntryData((Map<String, Object>) relatedDataElement);
                        } else {
                            throw new IllegalArgumentException("Functionality not supported yet!");
                        }
                    }
                } else {
                    //one to many
                    if (relatedData instanceof Map) {
                        result.setEntryData((Map<String, Object>) relatedData);
                    } else {
                        throw new IllegalArgumentException("Functionality not supported yet!");
                    }
                }
                EntityProviderWriteProperties inlineProperties = EntityProviderWriteProperties
                        .serviceRoot(context.getPathInfo().getServiceRoot()).callbacks(getCallbacks(feedData, entityType))
                        .expandSelectTree(writeEntryContext.getCurrentExpandSelectTreeNode()).build();
                result.setInlineProperties(inlineProperties);
            }
            return result;
        } catch (final ODataException e) {
            throw new OData2Exception(HttpStatusCodes.INTERNAL_SERVER_ERROR, e);
        }
    }

    private Map<String, ODataCallback> getCallbacks(final List<OData2ResultSetEntity> feedData, final EdmEntityType entityType)
            throws EdmException {
        final List<String> navigationPropertyNames = entityType.getNavigationPropertyNames();
        if (navigationPropertyNames.isEmpty()) {
            return null;
        } else {
            final ExpandSupportCallback callback = new ExpandSupportCallback(context, feedData);
            Map<String, ODataCallback> callbacks = new HashMap<String, ODataCallback>();
            for (final String name : navigationPropertyNames) {
                callbacks.put(name, callback);
            }
            return callbacks;
        }
    }
}
