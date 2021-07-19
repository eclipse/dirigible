/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.ExpandSelectTreeNode;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OData2Utils {
    private static final Logger LOG = LoggerFactory.getLogger(OData2Utils.class);

    private OData2Utils() {
    }

    public static String fqn(EdmType type) {
        try {
            return fqn(type.getNamespace(), type.getName());
        } catch (EdmException e) {
            throw new RuntimeException("Unable to get the fully qualified name from type " + type, e);
        }
    }

    public static String fqn(String ns, String name) {
        return ns + "." + name;
    }

    /**
     * Generates the next link for server-side paging. The next-link is based on
     * the URI of the current request, except that {@code $skip} or
     * {@code $skiptoken} will be removed.
     */
    public static String generateNextLink(ODataContext context, int top, int pagingSize) throws ODataException {
        String nextLink;

        final int skipToken;
        if (top > 0) {
            // We already have limited the maximum number of results for the current request
            // (which could be either a server-side paging
            // or a client-side $top).
            skipToken = top;
        } else {
            skipToken = pagingSize;
        }

        final StringBuilder nextLinkBuilder = new StringBuilder();
        String requestUri = percentEncodeNextLink(
                context.getPathInfo().getServiceRoot().relativize(context.getPathInfo().getRequestUri()).toString());
        nextLinkBuilder.append(requestUri);
        nextLinkBuilder.append(requestUri.contains("?") ? "&" : "?");
        nextLinkBuilder.append("$skiptoken=");
        nextLinkBuilder.append(skipToken);
        nextLink = nextLinkBuilder.toString();
        return nextLink;
    }

    public static <T> ODataResponse writeEntryWithExpand(ODataContext context, UriInfo uriInfo, OData2ResultSetEntity data,
            final String contentType) throws ODataException, EntityProviderException {
        if (data == null) {
            // Important NOTE:
            // T07.2016: After input by Olingo developers "No content" code is not
            // appropriate for this situation.
            // If we use "Not found" and we return a payload, it has to be a standard
            // conform error response.
            // If we raised a ODataApplicationException, the exception would be logged and
            // potentially spam the
            // log. After discussing the matter, we decided to write the error response
            // directly and not log the
            // error as 404 is usually not related to coding bugs but just not existent
            // keys.
            ODataErrorContext errorContext = new ODataErrorContext();
            errorContext.setContentType(contentType);
            errorContext.setHttpStatus(HttpStatusCodes.NOT_FOUND);
            errorContext.setErrorCode(HttpStatusCodes.NOT_FOUND.toString());
            errorContext.setLocale(Locale.ENGLISH);
            errorContext.setMessage("No content");
            return EntityProvider.writeErrorDocument(errorContext);
        }
        final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
        final org.apache.olingo.odata2.api.edm.EdmEntityType entityType = targetEntitySet.getEntityType();
        final ExpandSelectTreeNode expandSelectTree = UriParser.createExpandSelectTree(uriInfo.getSelect(), uriInfo.getExpand());
        final List<OData2ResultSetEntity> resultSetData = data != null ? Arrays.asList(data) : new ArrayList<OData2ResultSetEntity>();

        EntityProviderWriteProperties writeProperties = EntityProviderWriteProperties.serviceRoot(context.getPathInfo().getServiceRoot())
                .expandSelectTree(expandSelectTree).callbacks(buildExpand(context, resultSetData, entityType)).build();

        final ODataResponse response = EntityProvider.writeEntry(contentType, targetEntitySet, data.getEntitiyPropertiesData(),
                writeProperties);
        return response;
    }
    
    public static <T> ODataResponse writeEntryProperty(ODataContext context, EdmProperty edmProperty, UriInfo uriInfo, OData2ResultSetEntity data,
            final String contentType) throws ODataException, EntityProviderException {
        if (data == null) {
            ODataErrorContext errorContext = new ODataErrorContext();
            errorContext.setContentType(contentType);
            errorContext.setHttpStatus(HttpStatusCodes.NOT_FOUND);
            errorContext.setErrorCode(HttpStatusCodes.NOT_FOUND.toString());
            errorContext.setLocale(Locale.ENGLISH);
            errorContext.setMessage("No content");
            return EntityProvider.writeErrorDocument(errorContext);
        }
 
        final ODataResponse response = EntityProvider.writeProperty(contentType, edmProperty, data.getEntitiyPropertiesData().entrySet().iterator().next().getValue());
        return response;
    }
    
    public static <T> ODataResponse writeEntryPropertyValue(ODataContext context, EdmProperty edmProperty, UriInfo uriInfo, OData2ResultSetEntity data,
            final String contentType) throws ODataException, EntityProviderException {
        if (data == null) {
            ODataErrorContext errorContext = new ODataErrorContext();
            errorContext.setContentType(contentType);
            errorContext.setHttpStatus(HttpStatusCodes.NOT_FOUND);
            errorContext.setErrorCode(HttpStatusCodes.NOT_FOUND.toString());
            errorContext.setLocale(Locale.ENGLISH);
            errorContext.setMessage("No content");
            return EntityProvider.writeErrorDocument(errorContext);
        }
 
        final ODataResponse response = EntityProvider.writePropertyValue(edmProperty, data.getEntitiyPropertiesData().entrySet().iterator().next().getValue());
        return response;
    }

    public static ODataResponse writeFeedWithExpand(ODataContext context, UriInfo uriInfo, List<OData2ResultSetEntity> feedEntities,
            final String contentType, Integer count, String nextLink) throws ODataException, EntityProviderException {
        final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
        // Map<String, Map<String, Object>> links = new HashMap<String,
        // Map<String,Object>>();
        // HashMap<String, Object> value = new HashMap<String,Object>();
        // value.put("Mplguid", "haha");
        // links.put("Messages", value);

        final EntityProviderWriteProperties feedProperties = EntityProviderWriteProperties
                .serviceRoot(context.getPathInfo().getServiceRoot()).inlineCountType(uriInfo.getInlineCount()).inlineCount(count)
                .expandSelectTree(UriParser.createExpandSelectTree(uriInfo.getSelect(), uriInfo.getExpand()))
                // .additionalLinks(links)
                .callbacks(buildExpand(context, feedEntities, uriInfo.getTargetEntitySet().getEntityType())).nextLink(nextLink).build();

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (OData2ResultSetEntity resultEntity : feedEntities) {
            result.add(resultEntity.getEntitiyPropertiesData());
        }
        return EntityProvider.writeFeed(contentType, targetEntitySet, result, feedProperties);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ODataCallback> buildExpand(ODataContext context, List<OData2ResultSetEntity> data, EdmEntityType entityType)
            throws ODataException, EntityProviderException {
        if (data == null || data.isEmpty()) {
            return Collections.EMPTY_MAP;
        } else {
            return getNavigationPropertiesWriteCallbacks(context, data, entityType);
        }
    }

    /**
     * returns map of navigation property name to a callback.
     * 
     * @throws ODataException
     * @throws EntityProviderException
     */
    public static Map<String, ODataCallback> getNavigationPropertiesWriteCallbacks(ODataContext context,
            final List<OData2ResultSetEntity> feedData, final EdmEntityType entityType) throws ODataException, EntityProviderException {
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

    static String percentEncodeNextLink(final String link) {
        if (link == null) {
            return null;
        }
        return link.replaceAll("\\$skiptoken=.+?(?:&|$)", "").replaceAll("\\$skip=.+?(?:&|$)", "").replaceFirst("(?:\\?|&)$", ""); // Remove potentially trailing "?" or "&" left over from remove actions
    }

    public static boolean hasExpand(UriInfo info) {
        return hasExpand(info.getExpand());
    }


    public static boolean isEmpty(EdmEntityType entityType, OData2ResultSetEntity entity) throws ODataException {
        final List<String> keyPropertyNames = entityType.getKeyPropertyNames();
        boolean allKeysNull = true;
        for (String keyPropertyName : keyPropertyNames) {
            EdmTyped keyProperty = entityType.getProperty(keyPropertyName);
            if (keyProperty instanceof EdmProperty) {
                Object pv = entity.getEntitiyPropertiesData().get(keyProperty.getName());
                if (pv != null) {
                    allKeysNull = false;
                    break;
                }
            }
        }
        return allKeysNull;
    }

    public static boolean hasExpand(List<ArrayList<NavigationPropertySegment>> expand) {
        if (expand == null || expand.isEmpty()) {
            return false;
        }
        return true;
    }


    public static boolean isSameInstance(EdmEntityType type, OData2ResultSetEntity obj1, Map<String, Object> entryData)
            throws ODataException {
        final List<String> keyPropertyNames = type.getKeyPropertyNames();
        for (String keyPropertyName : keyPropertyNames) {
            EdmTyped keyProperty = type.getProperty(keyPropertyName);
            if (keyProperty instanceof EdmProperty) {
                Object propertyValue1 = obj1.getEntitiyPropertiesData().get(keyProperty.getName());
                Object propertyValue2 = entryData.get(keyProperty.getName());
                if (!equals(propertyValue1, propertyValue2)) {
                    return false;
                }
            } else {
                if (keyProperty.getType().getKind() == EdmTypeKind.COMPLEX) {
                    throw new OData2Exception("Complex property keys are not supported so far. Please extend the implementation.",
                            HttpStatusCodes.NOT_IMPLEMENTED);
                }
            }
        }
        return true;
    }

    public static boolean isSameInstance(EdmEntityType type, OData2ResultSetEntity obj1, OData2ResultSetEntity obj2) throws ODataException {
        if ((obj1 == null && obj2 != null) || (obj1 != null && obj2 == null)) {
            return false;
        }
        final List<String> keyPropertyNames = type.getKeyPropertyNames();
        for (String keyPropertyName : keyPropertyNames) {
            EdmTyped keyProperty = type.getProperty(keyPropertyName);
            if (keyProperty instanceof EdmProperty) {
                Object propertyValue1 = obj1.getEntitiyPropertiesData().get(keyProperty.getName());
                Object propertyValue2 = obj2.getEntitiyPropertiesData().get(keyProperty.getName());

                //                Object propertyValue1 = valueAccess.getPropertyValue(obj1.getEntityInstance(), (EdmProperty) keyProperty);
                //                Object propertyValue2 = valueAccess.getPropertyValue(obj2.getEntityInstance(), (EdmProperty) keyProperty);
                if (!equals(propertyValue1, propertyValue2)) {
                    return false;
                }
            } else {
                if (keyProperty.getType().getKind() == EdmTypeKind.COMPLEX) {
                    throw new OData2Exception(
                            "Complex property keys are not supported as key properties so far. Please extend the implementation.",
                            HttpStatusCodes.NOT_IMPLEMENTED);
                }
            }
        }

        return true;
    }

    public static String getTenantNameFromContext(ODataContext context) {
        ODataContext parentContext = context.getBatchParentContext();
        if (parentContext != null) {
            while (parentContext.getBatchParentContext() != null) {
                parentContext = parentContext.getBatchParentContext();
            }
            return (String) parentContext.getParameter(OData2Constants.ODATA_CTX_PARAMETER_TENANT_NAME);
        } else {
            return (String) context.getParameter(OData2Constants.ODATA_CTX_PARAMETER_TENANT_NAME);
        }
    }

    private static boolean equals(Object propertyValue1, Object propertyValue2) {
        if (propertyValue1 == propertyValue2)
            return true;
        if (propertyValue1 == null)
            return false;
        if (propertyValue2 == null)
            return false;
        if (!propertyValue1.equals(propertyValue2))
            return false;
        return true;
    }

    public static IOException closeConsumeException(Closeable closeable, String details) {
        try {
            close(closeable);
            return null;
        } catch (IOException e) {
            LOG.warn("Close failed", e);
            return e;
        }
    }

    public static IOException closeConsumeException(Closeable closeable) {
        return closeConsumeException(closeable, null);
    }

    public static void close(Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }

    public static void closeConsumeException(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
                return;
            } catch (Exception e) {
               LOG.warn("Close failed", e);
            }
        }
    }
}
