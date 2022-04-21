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
package org.eclipse.dirigible.engine.odata2.sql.processor;

import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.batch.BatchRequestPart;
import org.apache.olingo.odata2.api.batch.BatchResponsePart;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.commons.InlineCount;
import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderBatchProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataBadRequestException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.info.*;
import org.apache.olingo.odata2.core.uri.KeyPredicateImpl;
import org.apache.olingo.odata2.core.uri.UriInfoImpl;
import org.eclipse.dirigible.engine.odata2.sql.api.*;
import org.eclipse.dirigible.engine.odata2.sql.builder.*;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLUtils;
import org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils;
import org.eclipse.dirigible.engine.odata2.sql.utils.SingleConnectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils.getProperties;
import static org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils.getSelectedProperties;
import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.hasExpand;

public abstract class AbstractSQLProcessor extends ODataSingleProcessor implements SQLProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSQLProcessor.class);

    private final OData2EventHandler odata2EventHandler;
    private DataSource dataSource;
    private final ResultSetReader resultSetReader;

    public AbstractSQLProcessor() {
        this(new DummyOData2EventHandler());
    }

    public AbstractSQLProcessor(OData2EventHandler odata2EventHandler) {
        this.resultSetReader = new ResultSetReader(this);
        this.odata2EventHandler = odata2EventHandler;
    }

    @Override
    public ODataResponse updateEntitySimplePropertyValue(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType, String contentType) throws ODataException {
        return super.updateEntitySimplePropertyValue(uriInfo, content, requestContentType, contentType);
    }

    @Override
    public ODataResponse countEntitySet(final GetEntitySetCountUriInfo uriInfo, final String contentType)
            throws ODataException {
        if (uriInfo.getTop() != null || uriInfo.getSkip() != null) {
            throw new ODataNotImplementedException();
        }
        try {
            SQLSelectBuilder sqlQuery = this.getSQLQueryBuilder().buildSelectCountQuery((UriInfo) uriInfo, getContext());

            try (Connection connection = getDataSource().getConnection()) {
                int count = doCountEntitySet(sqlQuery, connection);
                return ODataResponse.fromResponse(EntityProvider.writeText(String.valueOf(count))).build();
            }
        } catch (SQLException e) {
            throw new ODataException(e);
        }
    }

    @Override
    public ODataResponse readEntityComplexProperty(final GetComplexPropertyUriInfo uriInfo, final String contentType)
            throws ODataException {
        LOG.error("readEntityComplexProperty not implemented: {}", uriInfo.toString());
        throw new ODataException("Not Implemented");
    }

    protected int doCountEntitySet(SQLSelectBuilder sqlQuery, final Connection connection) throws ODataException, SQLException {
        // TODO cache the metadata, because it fires a DB query every time
        // TODO do we really need to select the entities?
        String sql = sqlQuery.buildSelect(createSQLContext(connection));
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            LOG.info(sql);
            setParamsOnStatement(preparedStatement, sqlQuery.getStatementParams());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // TODO do we need to assert that resultSet.next() == true and subsequently
                // resultSet.next() == false here?
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    protected SQLContext createSQLContext(final Connection connection) throws SQLException {
        return new SQLContext(connection.getMetaData(), this.getContext());
    }

    protected PreparedStatement createSelectStatement(SQLSelectBuilder selectQuery, final Connection connection)
            throws SQLException, ODataException {
        String sql = selectQuery.buildSelect(createSQLContext(connection));
        LOG.info(sql);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        setParamsOnStatement(preparedStatement, selectQuery.getStatementParams());
        return preparedStatement;
    }

    @Override
    public ODataResponse readEntity(final GetEntityUriInfo uriInfo, final String contentType) throws ODataException {

        final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
        final EdmEntityType targetEntityType = targetEntitySet.getEntityType();

        SQLSelectBuilder query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext());
        ResultSetReader.ExpandAccumulator currentAccumulator = new ResultSetReader.ExpandAccumulator(targetEntityType);

        Collection<EdmProperty> properties = getSelectedProperties(uriInfo.getSelect(), targetEntityType);
        try (Connection connection = getDataSource().getConnection()) {
            try (PreparedStatement statement = createSelectStatement(query, connection)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        ResultSetReader.ResultSetEntity currentTargetEntity = resultSetReader.getResultSetEntity(query, targetEntityType, properties, resultSet);
                        if (!currentAccumulator.isAccumulatorFor(currentTargetEntity)) {
                            currentAccumulator = new ResultSetReader.ExpandAccumulator(currentTargetEntity);
                        }
                        List<ArrayList<NavigationPropertySegment>> expandEntities = uriInfo.getExpand();
                        if (hasExpand(expandEntities)) {
                            resultSetReader.accumulateExpandedEntities(query, resultSet, currentAccumulator, expandEntities);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to serve request", e);
            throw new ODataException(e);
        }
        return ExpandCallBack.writeEntryWithExpand(getContext(), (UriInfo) uriInfo, currentAccumulator, contentType);
    }


    @Override
    public ODataResponse readEntitySet(final GetEntitySetUriInfo uriInfo, final String contentType)
            throws ODataException {
        final InlineCount inlineCountType = uriInfo.getInlineCount();
        final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
        final EdmEntityType targetEntityType = targetEntitySet.getEntityType();

        Collection<EdmProperty> properties = getSelectedProperties(uriInfo.getSelect(), targetEntityType);
        List<ResultSetReader.ExpandAccumulator> entitiesFeed = new ArrayList<>();
        Integer count;
        String nextLink;
        try (Connection connection = getDataSource().getConnection()) {
            if (inlineCountType == InlineCount.ALLPAGES) {
                SQLSelectBuilder countEntitySet = this.getSQLQueryBuilder().buildSelectCountQuery((UriInfo) uriInfo, getContext());
                count = doCountEntitySet(countEntitySet, connection); // does not close the connection
            } else {
                count = null;
            }
            List<String> readIdsForExpand = new ArrayList<>();
            if (OData2Utils.hasExpand((UriInfo) uriInfo)) {
                LOG.debug("Reading the ids that will be used for $expand");
                readIdsForExpand = readIdsForExpand(uriInfo);
                LOG.info("Using IDs for $expand: {}", readIdsForExpand);
            }

            SQLSelectBuilder query = this.getSQLQueryBuilder().buildSelectEntitySetQuery((UriInfo) uriInfo, readIdsForExpand, getContext());
            try (PreparedStatement statement = createSelectStatement(query, connection)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    ResultSetReader.ExpandAccumulator currentAccumulator = new ResultSetReader.ExpandAccumulator(targetEntityType);
                    while (resultSet.next()) {
                        ResultSetReader.ResultSetEntity currentTargetEntity = resultSetReader.getResultSetEntity(query, targetEntityType, properties, resultSet);
                        LOG.info("Current entity set object is {}", currentTargetEntity);
                        if (!currentAccumulator.isAccumulatorFor(currentTargetEntity)) {
                            currentAccumulator = new ResultSetReader.ExpandAccumulator(currentTargetEntity);
                            entitiesFeed.add(currentAccumulator);
                        }

                        List<ArrayList<NavigationPropertySegment>> expandEntities = uriInfo.getExpand();
                        if (hasExpand(expandEntities)) {
                            resultSetReader.accumulateExpandedEntities(query, resultSet, currentAccumulator, expandEntities);
                        }
                    }
                    boolean needsNextLink = query.isServersidePaging() && entitiesFeed.size() == this
                            .getSQLQueryBuilder().getEntityPagingSize(targetEntityType);
                    nextLink = needsNextLink ? generateNextLink(query, targetEntityType) : null;
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to serve request", e);
            throw new ODataException(e);
        }
        return ExpandCallBack.writeFeedWithExpand(getContext(), (UriInfo) uriInfo, entitiesFeed, contentType, count, nextLink);
    }

    public List<String> readIdsForExpand(final GetEntitySetUriInfo uriInfo) throws ODataException {
        List<String> idsOfLeadingEntities = new ArrayList<>();
        try (Connection connection = getDataSource().getConnection()) {
            SQLSelectBuilder queryForIdsInExpand = this.getSQLQueryBuilder().buildSelectEntitySetIdsForTopAndExpandQuery((UriInfo) uriInfo, getContext());
            try (PreparedStatement statement = createSelectStatement(queryForIdsInExpand, connection)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {// TODO remove the duplication here
                        // we select only the ids here (we assume that only one key property is in the
                        // ID)
                        // Override this method if this is not the case
                        idsOfLeadingEntities.add(resultSet.getString(1));
                    }
                }
            }
        } catch (Exception e) {
            throw new ODataException(e);
        }
        return idsOfLeadingEntities;
    }


    /**
     * Generates the next link for server-side paging. The next-link is based on the
     * URI of the current request, except that {@code $skip} or {@code $skiptoken}
     * will be removed.
     *
     * @param query            the query
     * @param targetEntityType the target entity type
     * @return the link
     * @throws ODataException in case of an error
     */
    protected String generateNextLink(SQLSelectBuilder query, EdmEntityType targetEntityType) throws ODataException {
        int top = query.getSelectExpression().getTop();
        int pagingSize = this.getSQLQueryBuilder().getEntityPagingSize(targetEntityType);
        return OData2Utils.generateNextLink(getContext(), top, pagingSize);
    }

    @Override
    public ODataResponse readEntitySimplePropertyValue(final GetSimplePropertyUriInfo uriInfo, final String contentType) throws ODataException {
        final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
        final EdmEntityType targetEntityType = targetEntitySet.getEntityType();
        SQLSelectBuilder query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext());
        ResultSetReader.ResultSetEntity currentTargetEntity = new ResultSetReader.ResultSetEntity(targetEntityType, Collections.emptyMap());
        Collection<EdmProperty> properties = uriInfo.getPropertyPath();
        try (Connection connection = getDataSource().getConnection()) {
            try (PreparedStatement statement = createSelectStatement(query, connection)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        currentTargetEntity = resultSetReader.getResultSetEntity(query, targetEntityType, properties, resultSet);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to serve request", e);
            throw new ODataException(e);
        }
        return writeEntryPropertyValue(uriInfo.getPropertyPath().iterator().next(), currentTargetEntity.data, contentType);
    }

    private static ODataResponse writeEntryPropertyValue(EdmProperty property, Map<String, Object> data, final String contentType) throws ODataException {
        if (data == null || data.isEmpty()) {
            return OData2Utils.noContentResponse(contentType);
        } else {
            return EntityProvider.writePropertyValue(property, data.get(property.getName()));
        }
    }

    @Override
    public ODataResponse readEntitySimpleProperty(final GetSimplePropertyUriInfo uriInfo, final String contentType) throws ODataException {
        return readEntitySimplePropertyValue(uriInfo, contentType);
    }

    @Override
    public ODataResponse readEntityMedia(final GetMediaResourceUriInfo uriInfo, final String contentType) throws ODataException {
        throw new ODataNotImplementedException();
    }

    @Override
    public ODataResponse readEntityLinks(final GetEntitySetLinksUriInfo uriInfo, final String contentType)
            throws ODataException {
        throw new ODataNotImplementedException();
    }

    @Override
    public ODataResponse createEntity(final PostUriInfo uriInfo, final InputStream content,
                                      final String requestContentType, final String contentType) throws ODataException {
        //TODO: change operations order to be correct; write the updated entry in aftertablename

        if (this.odata2EventHandler.forbidCreateEntity(uriInfo, requestContentType, contentType)) {
            throw new ODataException(String.format("Create operation on entity: %s is forbidden.",
                    OData2Utils.fqn(uriInfo.getTargetType())));
        }

        final EdmEntitySet entitySet = uriInfo.getTargetEntitySet();
        final EdmEntityType entityType = entitySet.getEntityType();
        ODataEntry entry = parseEntry(entitySet, content, requestContentType, false);
        SQLInsertBuilder insertBuilder = this.getSQLQueryBuilder().buildInsertEntityQuery((UriInfo) uriInfo, entry, getContext());

        try (Connection connection = getDataSource().getConnection()) {
            Map<Object, Object> context = new HashMap<Object, Object>();
            context.put("dummyBuilder", this.getSQLQueryBuilder().buildInsertEntityQuery((UriInfo) uriInfo, entry, getContext()));
            context.put("insertBuilder", this.getSQLQueryBuilder().buildInsertEntityQuery((UriInfo) uriInfo, entry, getContext()));
            context.put("sqlContext", createSQLContext(connection));
            this.odata2EventHandler.beforeCreateEntity(uriInfo,
                    requestContentType, contentType, entry, context);
            try (PreparedStatement statement = createInsertStatement(insertBuilder, connection)) {
                statement.executeUpdate();
            }
        } catch (Exception e) {
            LOG.error("Unable to serve request", e);
            throw new ODataException(e);
        }

        if (this.odata2EventHandler.usingOnCreateEntity(uriInfo, requestContentType, contentType)) {
            try (Connection connection = getDataSource().getConnection()) {
                Map<Object, Object> context = new HashMap<Object, Object>();
                // TODO: used only to derive target table - refactor
                context.put("dummyBuilder", this.getSQLQueryBuilder().buildInsertEntityQuery((UriInfo) uriInfo, entry, getContext()));
                context.put("insertBuilder", this.getSQLQueryBuilder().buildInsertEntityQuery((UriInfo) uriInfo, entry, getContext()));
                context.put("sqlContext", createSQLContext(connection));
                this.odata2EventHandler.onCreateEntity(uriInfo, content,
                        requestContentType, contentType, context); //TODO: handle return properly
            } catch (Exception e) {
                LOG.error("Unable to serve request", e);
                throw new ODataException(e);
            }
        }

        try {
            List<EdmProperty> keyProperties = entityType.getKeyProperties();
            List<KeyPredicate> keyPredicates = new ArrayList<>();
            if (!keyProperties.isEmpty()) {
                for (EdmProperty keyProperty : keyProperties) {
                    if (entry.getProperties().get(keyProperty.getName()) != null) {
                        keyPredicates.add(new KeyPredicateImpl(
                                entry.getProperties().get(keyProperty.getName()).toString(),
                                keyProperty));
                    } else {
                        final String msg = "Cannot create entity without key(s)";
                        LOG.error(msg);
                        throw new ODataException(msg);
                    }
                }

                ((UriInfoImpl) uriInfo).setKeyPredicates(keyPredicates);

                // Re-read the inserted entity to get the auto-generated properties
                SQLSelectBuilder query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext());
                ResultSetReader.ResultSetEntity currentTargetEntity = new ResultSetReader.ResultSetEntity(entityType, Collections.emptyMap());
                Collection<EdmProperty> properties = getProperties(entityType);
                try (Connection connection = getDataSource().getConnection()) {
                    try (PreparedStatement statement = createSelectStatement(query, connection)) {
                        try (ResultSet resultSet = statement.executeQuery()) {
                            while (resultSet.next()) {
                                currentTargetEntity = resultSetReader.getResultSetEntity(query, entityType, properties, resultSet);
                            }
                        }
                    }
                    ODataResponse response = ExpandCallBack.writeEntryWithExpand(getContext(), //
                            (UriInfo) uriInfo, //
                            new ResultSetReader.ExpandAccumulator(currentTargetEntity), //
                            contentType);
                    Map<Object, Object> context = new HashMap<Object, Object>();
                    context.put("selectBuilder", this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext()));
                    context.put("sqlContext", createSQLContext(connection));
                    this.odata2EventHandler.afterCreateEntity(uriInfo, requestContentType, contentType, entry, context);
                    return response;
                } catch (Exception e) {
                    LOG.error("Unable to serve request", e);
                    throw new ODataException(e);
                }
            }
        } catch (Throwable t) {
            LOG.error("Unable to get back the created entity", t);
        }

        ODataContext context = getContext();
        EntityProviderWriteProperties writeProperties = EntityProviderWriteProperties
                .serviceRoot(context.getPathInfo().getServiceRoot()).expandSelectTree(entry.getExpandSelectTree())
                .build();
        final ODataResponse response = EntityProvider.writeEntry(contentType, entitySet, entry.getProperties(), writeProperties);
        this.odata2EventHandler.afterCreateEntity(uriInfo, requestContentType, contentType, entry, new HashMap<Object, Object>());
        return response;
    }

    public final ODataEntry parseEntry(final EdmEntitySet entitySet, final InputStream content,
                                       final String requestContentType, final boolean merge) throws ODataBadRequestException {
        try {
            EntityProviderReadProperties entityProviderProperties = EntityProviderReadProperties.init()
                    .mergeSemantic(merge).build();
            return EntityProvider.readEntry(requestContentType, entitySet, content, entityProviderProperties);
        } catch (Exception e) {
            throw new ODataBadRequestException(ODataBadRequestException.BODY, e);
        }
    }

    @Override
    public ODataResponse deleteEntity(final DeleteUriInfo uriInfo, final String contentType) throws ODataException {
        if (this.odata2EventHandler.forbidDeleteEntity(uriInfo, contentType)) {
            throw new ODataException(String.format("Delete operation on entity: %s is forbidden.", OData2Utils.fqn(uriInfo.getTargetType())));
        }

        SQLDeleteBuilder deleteBuilder = this.getSQLQueryBuilder().buildDeleteEntityQuery((UriInfo) uriInfo, mapKeys(uriInfo.getKeyPredicates()), getContext());

        try (Connection connection = dataSource.getConnection()) {
            Map<Object, Object> context = new HashMap<Object, Object>();
            context.put("selectBuilder", this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext()));
            this.odata2EventHandler.beforeDeleteEntity(uriInfo, contentType, context);

            if (this.odata2EventHandler.usingOnDeleteEntity(uriInfo, contentType)) {
                context = new HashMap<Object, Object>();
                context.put("selectBuilder", this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext()));
                this.odata2EventHandler.onDeleteEntity(uriInfo, contentType, context); //TODO: handle return properly
            } else {
                try (PreparedStatement statement = createDeleteStatement(deleteBuilder, connection)) {
                    statement.executeUpdate();
                }
            }

            ODataResponse response = ODataResponse.newBuilder().build();
            context = new HashMap<Object, Object>();
            this.odata2EventHandler.afterDeleteEntity(uriInfo, contentType, context);
            return response;
        } catch (Exception e) {
            LOG.error("Unable to serve request", e);
            throw new ODataException(e);
        }
    }

    private static Map<String, Object> mapKeys(final List<KeyPredicate> keys) throws EdmException {
        Map<String, Object> keyMap = new HashMap<>();
        for (final KeyPredicate key : keys) {
            final EdmProperty property = key.getProperty();
            final EdmSimpleType type = (EdmSimpleType) property.getType();
            keyMap.put(property.getName(), type.valueOfString(key.getLiteral(), EdmLiteralKind.DEFAULT,
                    property.getFacets(), type.getDefaultType()));
        }
        return keyMap;
    }

    @Override
    public ODataResponse updateEntity(final PutMergePatchUriInfo uriInfo, final InputStream content,
                                      final String requestContentType, final boolean merge, final String contentType) throws ODataException {
        if (uriInfo.getFilter() != null) {
            throw new ODataException("Update operation is not allowed with $filter.");
        }
        if (this.odata2EventHandler.forbidUpdateEntity(uriInfo, requestContentType, merge, contentType)) {
            throw new ODataException(String.format("Update operation forbidden on entity: %s", OData2Utils.fqn(uriInfo.getTargetType())));
        }

        final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
        final EdmEntityType targetEntityType = targetEntitySet.getEntityType();

        ODataEntry entry = parseEntry(targetEntitySet, content, requestContentType, false);
        SQLSelectBuilder query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext());
        try (Connection connection = dataSource.getConnection()) {
            Map<Object, Object> context = new HashMap<Object, Object>();
            context.put("selectBuilder", this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext()));
            context.put("updateBuilder", this.getSQLQueryBuilder().buildUpdateEntityQuery((UriInfo) uriInfo, entry,
                    mapKeys(uriInfo.getKeyPredicates()), getContext()));
            context.put("sqlContext", createSQLContext(connection));
            this.odata2EventHandler.beforeUpdateEntity(uriInfo, requestContentType, merge, contentType, entry, context);

            if (this.odata2EventHandler.usingOnUpdateEntity(uriInfo, requestContentType, merge, contentType)) {
                context = new HashMap<Object, Object>();
                context.put("selectBuilder", this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext()));
                context.put("updateBuilder", this.getSQLQueryBuilder().buildUpdateEntityQuery((UriInfo) uriInfo, entry,
                        mapKeys(uriInfo.getKeyPredicates()), getContext()));
                context.put("sqlContext", createSQLContext(connection));
                return this.odata2EventHandler.onUpdateEntity(uriInfo, content, requestContentType, merge, contentType, context); //TODO: handle return properly
            } else {
                ResultSetReader.ResultSetEntity currentTargetEntity = new ResultSetReader.ResultSetEntity(targetEntityType, Collections.emptyMap());
                Collection<EdmProperty> properties = targetEntityType.getKeyProperties();
                try (PreparedStatement statement = createSelectStatement(query, connection)) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            currentTargetEntity = resultSetReader.getResultSetEntity(query, targetEntityType, properties, resultSet);
                        }
                        if (currentTargetEntity.data.isEmpty()) {
                            throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
                        }
                    }
                }

                SQLUpdateBuilder updateBuilder = this.getSQLQueryBuilder().buildUpdateEntityQuery((UriInfo) uriInfo, entry,
                        mapKeys(uriInfo.getKeyPredicates()), getContext());
                try (PreparedStatement statement = createUpdateStatement(updateBuilder, connection)) {
                    statement.executeUpdate();
                }

                ODataResponse response = ODataResponse.newBuilder().status(HttpStatusCodes.NO_CONTENT).build();
                context = new HashMap<Object, Object>();
                context.put("dummyBuilder", this.getSQLQueryBuilder().buildInsertEntityQuery((UriInfo) uriInfo, entry, getContext()));
                context.put("selectBuilder", this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext()));
                context.put("sqlContext", createSQLContext(connection));
                this.odata2EventHandler.afterUpdateEntity(uriInfo, requestContentType, merge, contentType, entry, context);
                return response;
            }
        } catch (Exception e) {
            LOG.error("Unable to serve request", e);
            throw new ODataException(e);
        }
    }


    protected PreparedStatement createInsertStatement(SQLInsertBuilder builder, final Connection connection) throws SQLException, ODataException {
        return createPreparedStatement(connection, builder.build(createSQLContext(connection)));
    }

    protected PreparedStatement createDeleteStatement(SQLDeleteBuilder builder, final Connection connection) throws SQLException, ODataException {
        return createPreparedStatement(connection, builder.build(createSQLContext(connection)));
    }

    protected PreparedStatement createUpdateStatement(SQLUpdateBuilder builder, final Connection connection) throws SQLException, ODataException {
        return createPreparedStatement(connection, builder.build(createSQLContext(connection)));
    }

    protected PreparedStatement createPreparedStatement(Connection connection, SQLStatement statement) throws ODataException, SQLException {
        String sql = statement.sql();
        LOG.info("SQL Statement: {}", sql);

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        setParamsOnStatement(preparedStatement, statement.getStatementParams());
        return preparedStatement;
    }

    public void setParamsOnStatement(PreparedStatement preparedStatement, List<SQLStatementParam> params) throws SQLException {
        LOG.debug("SQL Params: {}", params);
        SQLUtils.setParamsOnStatement(preparedStatement, params);
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ODataResponse executeBatch(final BatchHandler handler, final String contentType, final InputStream content) throws ODataException {
        try {
            ODataResponse batchResponse;
            PathInfo pathInfo = getContext().getPathInfo();
            EntityProviderBatchProperties batchProperties = EntityProviderBatchProperties.init().pathInfo(pathInfo).build();
            List<BatchRequestPart> batchParts = EntityProvider.parseBatchRequest(contentType, content, batchProperties);
            List<BatchResponsePart> parts = new ArrayList<>();
            for (BatchRequestPart batchPart : batchParts) {
                parts.add(handler.handleBatchPart(batchPart));
            }
            batchResponse = EntityProvider.writeBatchResponse(parts);
            return batchResponse;
        } catch (ODataException e) {
            LOG.error("Problem during batch processing", e);
            throw e;
        } catch (Exception e) {
            throw new ODataException("Problem during batch processing", e);
        }
    }

    @Override
    public BatchResponsePart executeChangeSet(final BatchHandler handler, final List<ODataRequest> requests) throws ODataException {
        DataSource originalDataSource = this.getDataSource();
        try (Connection connection = getDataSource().getConnection()) {
            SingleConnectionDataSource singleConnectionDataSource = new SingleConnectionDataSource(connection);
            this.setDataSource(singleConnectionDataSource);
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                //override the setDataSource so that all requests in a change set run in the same connection
                return doExecuteChangeSet(handler, requests);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (ODataException e) {
            LOG.error("Unable to execute chnange set", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Unable to execute chnange set", e);
            throw new ODataException("Unable to execute the change set ", e);
        } finally {
            //restore the original data source
            this.setDataSource(originalDataSource);
        }
    }

    public BatchResponsePart doExecuteChangeSet(final BatchHandler handler, final List<ODataRequest> requests) throws ODataException, SQLException {
        boolean changeSetFailed = false;
        try (Connection nonClosableConnectionUsedForChangeSet = this.getDataSource().getConnection()) { //
            try {
                if (nonClosableConnectionUsedForChangeSet.getAutoCommit()) {
                    nonClosableConnectionUsedForChangeSet.setAutoCommit(false);
                }
                List<ODataResponse> responses = new ArrayList<>();
                for (ODataRequest request : requests) {
                    ODataResponse response = handler.handleRequest(request);
                    //erroneous requests are >= 400 (BAD_REQUEST)
                    if (response.getStatus().getStatusCode() >= HttpStatusCodes.BAD_REQUEST.getStatusCode()) {
                        List<ODataResponse> errorResponses = new ArrayList<>();
                        errorResponses.add(response);
                        changeSetFailed = true;
                        return BatchResponsePart.responses(errorResponses).changeSet(false).build();
                    }
                    responses.add(response);
                }
                return BatchResponsePart.responses(responses).changeSet(true).build();
            } catch (ODataException e) {
                changeSetFailed = true;
                throw e;
            } catch (RuntimeException e) {
                changeSetFailed = true;
                throw new ODataException("Unable to process change set", e);
            } finally {
                if (!nonClosableConnectionUsedForChangeSet.getAutoCommit()) {
                    if (changeSetFailed) {
                        nonClosableConnectionUsedForChangeSet.rollback();
                    } else {
                        nonClosableConnectionUsedForChangeSet.commit();
                    }
                } else {
                    throw new IllegalStateException("Invalid implementation - the OData operations must not unset the auto commit to false on the connection!");
                }
            }
        }
    }

    private Map<Object, Object> createHandlerContext(Connection connection, AbstractQueryBuilder queryBuilder) throws SQLException {
        Map<Object, Object> context = new HashMap<>();
        context.put("tableBindingProvider", queryBuilder.getTableBinding());
        context.put("sqlContext", createSQLContext(connection));
        return context;
    }
}
