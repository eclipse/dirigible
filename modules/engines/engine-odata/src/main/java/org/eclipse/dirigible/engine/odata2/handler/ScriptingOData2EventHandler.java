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
package org.eclipse.dirigible.engine.odata2.handler;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.engine.odata2.api.ODataException;
import org.eclipse.dirigible.engine.odata2.definition.ODataHandlerDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataHandlerMethods;
import org.eclipse.dirigible.engine.odata2.definition.ODataHandlerTypes;
import org.eclipse.dirigible.engine.odata2.service.ODataCoreService;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2EventHandler;
import org.eclipse.dirigible.engine.odata2.sql.processor.DefaultSQLProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptingOData2EventHandler implements OData2EventHandler {

    private static final String DIRIGIBLE_ODATA_HANDLER_EXECUTOR_TYPE = "DIRIGIBLE_ODATA_HANDLER_EXECUTOR_TYPE";
    private static final String DIRIGIBLE_ODATA_HANDLER_EXECUTOR_ON_EVENT = "DIRIGIBLE_ODATA_HANDLER_EXECUTOR_ON_EVENT";

    private static final Logger logger = LoggerFactory.getLogger(DefaultSQLProcessor.class);

    private static final String DIRIGIBLE_ODATA_WRAPPER_MODULE_ON_EVENT = "odata/wrappers/onEvent";

    private static final String ERROR_EXECUTING_XSJSLIB_HANDLER = "Error executing .xsjslib handler: ";

    private ODataCoreService odataCoreService = new ODataCoreService();

    @Override
    public ODataResponse beforeCreateEntity(PostUriInfo uriInfo, String requestContentType,
                                            String contentType, ODataEntry entry, Map<Object, Object> context) throws ODataException {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.create.name();
            String type = ODataHandlerTypes.before.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            context.put("uriInfo", uriInfo);
            context.put("requestContentType", requestContentType);
            context.put("contentType", contentType);
            context.put("entry", entry);
            executeHandlers(handlers, context);
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        } catch (ScriptingException e) {
            throw new ODataException(ERROR_EXECUTING_XSJSLIB_HANDLER + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public ODataResponse afterCreateEntity(PostUriInfo uriInfo, String requestContentType,
                                           String contentType, ODataEntry entry, Map<Object, Object> context) throws ODataException {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.create.name();
            String type = ODataHandlerTypes.after.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            context.put("uriInfo", uriInfo);
            context.put("requestContentType", requestContentType);
            context.put("contentType", contentType);
            context.put("entry", entry);
            executeHandlers(handlers, context);
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        } catch (ScriptingException e) {
            throw new ODataException(ERROR_EXECUTING_XSJSLIB_HANDLER + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean isUsingOnCreateEntity(PostUriInfo uriInfo, String requestContentType,
                                         String contentType) {
        return isUsingEvent((UriInfo) uriInfo, ODataHandlerMethods.create.name(), ODataHandlerTypes.on.name());
    }

    @Override
    public boolean isUsingAfterCreateEntity(PostUriInfo uriInfo, String requestContentType,
                                         String contentType) {
        return isUsingEvent((UriInfo) uriInfo, ODataHandlerMethods.create.name(), ODataHandlerTypes.after.name());
    }

    @Override
    public ODataResponse onCreateEntity(PostUriInfo uriInfo, InputStream content, String requestContentType,
                                        String contentType, Map<Object, Object> context) throws ODataException {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.create.name();
            String type = ODataHandlerTypes.on.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            context.put("uriInfo", uriInfo);
            context.put("requestContentType", requestContentType);
            context.put("contentType", contentType);
            context.put("content", content);
            String responseMessage = executeHandler(handlers, context);
            ODataResponse response = EntityProvider.writeText(responseMessage);
            return response;
        } catch (EdmException | ODataException | EntityProviderException e) {
            logger.error(e.getMessage(), e);
        } catch (ScriptingException e) {
            throw new ODataException(ERROR_EXECUTING_XSJSLIB_HANDLER + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean forbidCreateEntity(PostUriInfo uriInfo, String requestContentType,
                                      String contentType) {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.create.name();
            String type = ODataHandlerTypes.forbid.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            return handlers.size() > 0;
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public ODataResponse beforeUpdateEntity(PutMergePatchUriInfo uriInfo, String requestContentType,
                                            boolean merge, String contentType, ODataEntry entry, Map<Object, Object> context) throws ODataException {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.update.name();
            String type = ODataHandlerTypes.before.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            context.put("uriInfo", uriInfo);
            context.put("requestContentType", requestContentType);
            context.put("merge", merge);
            context.put("contentType", contentType);
            context.put("entry", entry);
            executeHandlers(handlers, context);
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        } catch (ScriptingException e) {
            throw new ODataException(ERROR_EXECUTING_XSJSLIB_HANDLER + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public ODataResponse afterUpdateEntity(PutMergePatchUriInfo uriInfo, String requestContentType,
                                           boolean merge, String contentType, ODataEntry entry, Map<Object, Object> context) throws ODataException {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.update.name();
            String type = ODataHandlerTypes.after.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            context.put("uriInfo", uriInfo);
            context.put("requestContentType", requestContentType);
            context.put("merge", merge);
            context.put("contentType", contentType);
            context.put("entry", entry);
            executeHandlers(handlers, context);
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        } catch (ScriptingException e) {
            throw new ODataException(ERROR_EXECUTING_XSJSLIB_HANDLER + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean isUsingOnUpdateEntity(PutMergePatchUriInfo uriInfo, String requestContentType,
                                         boolean merge, String contentType) {
        return isUsingEvent((UriInfo) uriInfo, ODataHandlerMethods.update.name(), ODataHandlerTypes.on.name());
    }

    @Override
    public ODataResponse onUpdateEntity(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType,
                                        boolean merge, String contentType, Map<Object, Object> context) throws ODataException {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.update.name();
            String type = ODataHandlerTypes.on.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            context.put("uriInfo", uriInfo);
            context.put("requestContentType", requestContentType);
            context.put("merge", merge);
            context.put("contentType", contentType);
            context.put("content", content);
            String responseMessage = executeHandler(handlers, context);
            ODataResponse response = EntityProvider.writeText(responseMessage);
            return response;
        } catch (EdmException | ODataException | EntityProviderException e) {
            logger.error(e.getMessage(), e);
        } catch (ScriptingException e) {
            throw new ODataException(ERROR_EXECUTING_XSJSLIB_HANDLER + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean forbidUpdateEntity(PutMergePatchUriInfo uriInfo, String requestContentType,
                                      boolean merge, String contentType) {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.update.name();
            String type = ODataHandlerTypes.forbid.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            return handlers.size() > 0;
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public ODataResponse beforeDeleteEntity(DeleteUriInfo uriInfo, String contentType, Map<Object, Object> context) throws ODataException {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.delete.name();
            String type = ODataHandlerTypes.before.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            context.put("uriInfo", uriInfo);
            context.put("contentType", contentType);
            executeHandlers(handlers, context);
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        } catch (ScriptingException e) {
            throw new ODataException(ERROR_EXECUTING_XSJSLIB_HANDLER + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public ODataResponse afterDeleteEntity(DeleteUriInfo uriInfo, String contentType, Map<Object, Object> context) throws ODataException {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.delete.name();
            String type = ODataHandlerTypes.after.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            context.put("uriInfo", uriInfo);
            context.put("contentType", contentType);
            executeHandlers(handlers, context);
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        } catch (ScriptingException e) {
            throw new ODataException(ERROR_EXECUTING_XSJSLIB_HANDLER + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean isUsingOnDeleteEntity(DeleteUriInfo uriInfo, String contentType) {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.delete.name();
            String type = ODataHandlerTypes.on.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            return handlers.size() > 0;
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public ODataResponse onDeleteEntity(DeleteUriInfo uriInfo, String contentType, Map<Object, Object> context) throws ODataException {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.delete.name();
            String type = ODataHandlerTypes.on.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            context.put("uriInfo", uriInfo);
            context.put("contentType", contentType);
            String responseMessage = executeHandler(handlers, context);
            ODataResponse response = EntityProvider.writeText(responseMessage);
            return response;
        } catch (EdmException | ODataException | EntityProviderException e) {
            logger.error(e.getMessage(), e);
        } catch (ScriptingException e) {
            throw new ODataException(ERROR_EXECUTING_XSJSLIB_HANDLER + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean forbidDeleteEntity(DeleteUriInfo uriInfo, String contentType) {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            String method = ODataHandlerMethods.delete.name();
            String type = ODataHandlerTypes.forbid.name();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            return handlers.size() > 0;
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public String getName() {
        return DEFAULT_ODATA_EVENT_HANDLER_NAME;
    }

    private void executeHandlers(List<ODataHandlerDefinition> handlers, Map<Object, Object> context) {
        handlers.forEach(handler -> {
            setHandlerParametersInContext(context, handler);
            executeHandlerByExecutor(context);
        });
    }

    private String executeHandler(List<ODataHandlerDefinition> handlers, Map<Object, Object> context) {
        if (handlers.size() > 0) {
            ODataHandlerDefinition handler = handlers.get(0);
            setHandlerParametersInContext(context, handler);
            Object response = executeHandlerByExecutor(context);
            return response != null ? response.toString() : "Empty response.";
        }
        ;
        return "No response.";
    }

    private Object executeHandlerByExecutor(Map<Object, Object> context) throws ScriptingException {
        String odataHandlerExecutorType = Configuration.get(DIRIGIBLE_ODATA_HANDLER_EXECUTOR_TYPE);
        String odataHandlerExecutorOnEvent = Configuration.get(DIRIGIBLE_ODATA_HANDLER_EXECUTOR_ON_EVENT);
        if (odataHandlerExecutorType != null && odataHandlerExecutorOnEvent != null) {
            Object response = ScriptEngineExecutorsManager.executeServiceModule(
                    odataHandlerExecutorType, odataHandlerExecutorOnEvent, context);
            return response;
        }
        Object response = ScriptEngineExecutorsManager.executeServiceModule(
                IJavascriptEngineExecutor.JAVASCRIPT_TYPE_DEFAULT, DIRIGIBLE_ODATA_WRAPPER_MODULE_ON_EVENT, context);
        return response;
    }

    private void setHandlerParametersInContext(Map<Object, Object> context, ODataHandlerDefinition handler) {
        context.put("location", handler.getLocation());
        context.put("namespace", handler.getNamespace());
        context.put("name", handler.getName());
        context.put("method", handler.getMethod());
        context.put("type", handler.getType());
        context.put("handler", handler.getHandler());
    }

    private boolean isUsingEvent(UriInfo uriInfo, String method, String type) {
        try {
            String namespace = uriInfo.getTargetType().getNamespace();
            String name = uriInfo.getTargetType().getName();
            List<ODataHandlerDefinition> handlers = odataCoreService.getHandlers(namespace, name, method, type);
            return handlers.size() > 0;
        } catch (EdmException | ODataException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

}
