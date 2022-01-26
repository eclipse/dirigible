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

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataHttpException;
import org.apache.olingo.odata2.api.exception.ODataRuntimeApplicationException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.ExpandSelectTreeNode;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.CommonExpression;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderExpression;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2Exception;

import java.util.*;

public class OData2Utils {

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
     * 
     * @param context the context
     * @param top the top param
     * @param pagingSize the page size
     * @return the link
     * @throws ODataException in case of an error
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
            final String contentType) throws ODataException {
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
        final List<OData2ResultSetEntity> resultSetData = Arrays.asList(data);

        EntityProviderWriteProperties writeProperties = EntityProviderWriteProperties.serviceRoot(context.getPathInfo().getServiceRoot())
                .expandSelectTree(expandSelectTree).callbacks(buildExpand(context, resultSetData, entityType)).build();

        final ODataResponse response = EntityProvider.writeEntry(contentType, targetEntitySet, data.getEntitiyPropertiesData(),
                writeProperties);
        return response;
    }
    
    public static <T> ODataResponse writeEntryProperty(ODataContext context, EdmProperty edmProperty, UriInfo uriInfo, OData2ResultSetEntity data,
            final String contentType) throws ODataException {
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
            final String contentType) throws ODataException {
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
            final String contentType, Integer count, String nextLink) throws ODataException {
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

        List<Map<String, Object>> result = new ArrayList<>();
        for (OData2ResultSetEntity resultEntity : feedEntities) {
            result.add(resultEntity.getEntitiyPropertiesData());
        }
        return EntityProvider.writeFeed(contentType, targetEntitySet, result, feedProperties);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ODataCallback> buildExpand(ODataContext context, List<OData2ResultSetEntity> data, EdmEntityType entityType)
            throws ODataException {
        if (data == null || data.isEmpty()) {
            return Collections.EMPTY_MAP;
        } else {
            return getNavigationPropertiesWriteCallbacks(context, data, entityType);
        }
    }

    /**
     * Returns map of navigation property name to a callback.
     * 
     * @param context the context
     * @param feedData the feed data
     * @param entityType the entity type
     * @return map of navigation property name to a callback
     * @throws ODataException in case of an error
     * @throws EntityProviderException in case of an error
     */
    public static Map<String, ODataCallback> getNavigationPropertiesWriteCallbacks(ODataContext context,
            final List<OData2ResultSetEntity> feedData, final EdmEntityType entityType) throws ODataException {
        final List<String> navigationPropertyNames = entityType.getNavigationPropertyNames();
        if (navigationPropertyNames.isEmpty()) {
            return null;
        } else {
            final ExpandSupportCallback callback = new ExpandSupportCallback(context, feedData);
            Map<String, ODataCallback> callbacks = new HashMap<>();
            for (final String name : navigationPropertyNames) {
                callbacks.put(name, callback);
            }
            return callbacks;
        }
    }

    public static String getInlineEntryKeyValue(Map<String, Object> values, EdmTyped inlineEntry, EdmProperty inlinEntityKey) throws EdmException {
        if (inlineEntry.getType() instanceof EdmEntityType) {
            Object inlineEntryData = values.get(inlineEntry.getName());
            if (inlineEntryData instanceof ODataEntry) {
                Map inlineEntryDataProperties = ((ODataEntry) inlineEntryData).getProperties();
                for (Object inlineEntityKeyName : inlineEntryDataProperties.keySet()) {
                    Object inlineKeyValue = inlineEntryDataProperties.get(inlinEntityKey.getName());
                    if (inlineKeyValue instanceof String) {
                        return (String) inlineKeyValue;
                    } else {
                        throw new OData2Exception("Invalid inline entity: the key " + inlinEntityKey.getName()//
                                + " of entity " + inlineEntry.getName() + " must be of type String!", HttpStatusCodes.BAD_REQUEST);
                    }
                }
            }
            throw new OData2Exception("Invalid inline entity: missing key " + inlinEntityKey.getName() + //
                    " of entity " + inlineEntry.getName(), HttpStatusCodes.BAD_REQUEST);
        }
        throw new OData2Exception("Invalid inline entity: required is EntityType for " + inlineEntry.getName(), HttpStatusCodes.BAD_REQUEST);
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
        return isEmpty(entityType, entity.getEntitiyPropertiesData());
    }

    public static boolean isEmpty(EdmEntityType entityType, Map<String, Object> entityData) throws ODataException {
        final List<String> keyPropertyNames = entityType.getKeyPropertyNames();
        boolean allKeysNull = true;
        for (String keyPropertyName : keyPropertyNames) {
            EdmTyped keyProperty = entityType.getProperty(keyPropertyName);
            if (keyProperty instanceof EdmProperty) {
                Object pv = entityData.get(keyProperty.getName());
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


    public static boolean isOrderByEntityInExpand(OrderExpression orderExpression, UriInfo uriInfo) throws EdmException {
        MemberExpression memberExpression = (MemberExpression)orderExpression.getExpression();
        CommonExpression pathExpression = memberExpression.getPath();
        EdmType  entityType = pathExpression.getEdmType();
        if (!hasExpand(uriInfo.getExpand())) {
            return false;
        }
        for (List<NavigationPropertySegment> expand: uriInfo.getExpand()) {
            for (NavigationPropertySegment segment: expand) {
                EdmEntitySet epxandEntity = segment.getTargetEntitySet();
                if (epxandEntity.getEntityType().getName().equals(entityType.getName())) {
                    return true;
                }
             }
        }
        return false;
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

    public static HttpStatusCodes getStatusCodeForException(Exception e) {
        HttpStatusCodes codeOnFailedRequest = HttpStatusCodes.INTERNAL_SERVER_ERROR;
        if (e instanceof OData2Exception) {
            codeOnFailedRequest = ((OData2Exception) e).getHttpStatus();
        } else if (e instanceof ODataApplicationException) {
            ODataApplicationException odae = (ODataApplicationException) e;
            codeOnFailedRequest = odae.getHttpStatus();
            if (codeOnFailedRequest == null) {
                codeOnFailedRequest = determineStatusCode(odae);
            }
        } else if (e instanceof ODataException) {
            codeOnFailedRequest = determineStatusCode((ODataException) e);
        } else if (e instanceof ODataRuntimeApplicationException) {
            codeOnFailedRequest = ((ODataRuntimeApplicationException) e).getHttpStatus();
            if (codeOnFailedRequest == null) {
                codeOnFailedRequest = HttpStatusCodes.INTERNAL_SERVER_ERROR;
            }
        }
        return codeOnFailedRequest;
    }

    private static HttpStatusCodes determineStatusCode(ODataException ode) {
        HttpStatusCodes codeOnFailedRequest = HttpStatusCodes.INTERNAL_SERVER_ERROR;
        if (ode.isCausedByHttpException()) {
            codeOnFailedRequest = ((ODataHttpException) ode).getHttpStatus();
        } else if (ode.isCausedByMessageException()) {
            codeOnFailedRequest = HttpStatusCodes.BAD_REQUEST;
        } else if (ode.isCausedByApplicationException()) {
            codeOnFailedRequest = HttpStatusCodes.INTERNAL_SERVER_ERROR;
        }
        return codeOnFailedRequest;
    }
}
