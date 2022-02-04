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
import org.eclipse.dirigible.engine.odata2.sql.clause.SQLSelectClause;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLUtils;
import org.eclipse.dirigible.engine.odata2.sql.utils.OData2ResultSetEntity;
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

	public AbstractSQLProcessor() {
		this.odata2EventHandler = new DummyOData2EventHandler();
	}
	
	public AbstractSQLProcessor(OData2EventHandler odata2EventHandler) {
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
		LOG.error("readEntityComplexProperty not implemented: {}",uriInfo.toString());
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


	// TODO add unit tests for the expand functionality
	@Override
	public ODataResponse readEntity(final GetEntityUriInfo uriInfo, final String contentType) throws ODataException {
		
		final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
		final EdmEntityType targetEntityType = targetEntitySet.getEntityType();

		SQLSelectBuilder query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext());
		OData2ResultSetEntity resultEntity = null;
		// TODO change logic to read columns from ResultSet? (key has to be read even if
		// it is now requested via $select)
		Collection<EdmProperty> properties = getSelectedProperties(uriInfo.getSelect(), targetEntityType);
		try (Connection connection = getDataSource().getConnection()){
			try (PreparedStatement statement = createSelectStatement(query, connection)){
				try (ResultSet resultSet = statement.executeQuery()){
					while (resultSet.next()) {

						if (resultEntity == null) {// TODO remove the duplication here with the readEntitySet
							Map<String, Object> data = readResultSet(query, targetEntityType, properties, resultSet);
							resultEntity = new OData2ResultSetEntity(data);
						}
						List<ArrayList<NavigationPropertySegment>> expandEntities = uriInfo.getExpand();
						if (hasExpand(expandEntities)) {
							processExpand(targetEntityType, query, resultSet, resultEntity, expandEntities);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to serve request", e);
			throw new ODataException(e);
		}
		return OData2Utils.writeEntryWithExpand(getContext(), (UriInfo) uriInfo, resultEntity, contentType);
	}

	// TODO add unit tests for the expand functionality
	@Override
	public ODataResponse readEntitySet(final GetEntitySetUriInfo uriInfo, final String contentType)
			throws ODataException {
		final InlineCount inlineCountType = uriInfo.getInlineCount();
		final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
		final EdmEntityType targetEntityType = targetEntitySet.getEntityType();

		Collection<EdmProperty> properties = getSelectedProperties(uriInfo.getSelect(), targetEntityType);
		List<OData2ResultSetEntity> targetEntitiesResult = new ArrayList<>();

		Integer count;
		String nextLink;
		try (Connection connection = getDataSource().getConnection()) {
			if (inlineCountType == InlineCount.ALLPAGES) {
				SQLSelectBuilder countEntitySet = this.getSQLQueryBuilder().buildSelectCountQuery((UriInfo) uriInfo, getContext());
				count = doCountEntitySet(countEntitySet, connection); // does not close the connection
			} else {
				count = null;
			}
			SQLSelectBuilder query;
			List<String> readIdsForExpand = new ArrayList<>();
			if (OData2Utils.hasExpand((UriInfo) uriInfo)) {
				LOG.info("Reading the ids that will be used for $expand");
				readIdsForExpand = readIdsForExpand(uriInfo);
				LOG.info("Using IDs for $expand: {}", readIdsForExpand);
			}
			query = this.getSQLQueryBuilder().buildSelectEntitySetQuery((UriInfo) uriInfo, readIdsForExpand, getContext());

			try (PreparedStatement statement = createSelectStatement(query, connection)) {
				try (ResultSet resultSet = statement.executeQuery()) {
					OData2ResultSetEntity currentResultSetEntity = null;

					// we iterate the ResultSet rows here
					// for every row, we create one target entity instance and compare it with the
					// previous one. If the key property values are different, then we move to the
					// next target instance.
					// Otherwise that row has only navigation properties, then in that iteration
					// only the navigation properties are set
					while (resultSet.next()) {// TODO remove the duplication here

						Map<String, Object> data = readResultSet(query, targetEntityType, properties, resultSet);
						OData2ResultSetEntity nextResultSetEntity = new OData2ResultSetEntity(data);

						LOG.info("Current object is " + nextResultSetEntity);
						if (OData2Utils.isEmpty(targetEntityType, nextResultSetEntity)) {
							continue;
						}
						if (!OData2Utils.isSameInstance(targetEntityType, currentResultSetEntity,
								nextResultSetEntity)) {
							// now the result set has a new entity
							currentResultSetEntity = nextResultSetEntity;
							targetEntitiesResult.add(currentResultSetEntity);
						}
						List<ArrayList<NavigationPropertySegment>> expandEntities = uriInfo.getExpand();
						if (hasExpand(expandEntities)) {
							processExpand(targetEntityType, query, resultSet, currentResultSetEntity, expandEntities);
						}
					}
					boolean needsNextLink = query.isServersidePaging() && targetEntitiesResult.size() == this
							.getSQLQueryBuilder().getEntityPagingSize(targetEntityType);
					nextLink = needsNextLink ? generateNextLink(query, targetEntityType) : null;
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to serve request", e);
			throw new ODataException(e);
		}
		return OData2Utils.writeFeedWithExpand(getContext(), (UriInfo) uriInfo, targetEntitiesResult, contentType,
				count, nextLink);
	}

	private void processExpand(EdmEntityType targetEntityType, SQLSelectBuilder query, ResultSet resultSet, OData2ResultSetEntity currentResultSetEntity,
							   List<ArrayList<NavigationPropertySegment>> expandEntities) throws SQLException, ODataException {
		for (List<NavigationPropertySegment> expandContents : expandEntities) {
			for (NavigationPropertySegment expandContent : expandContents) {
				EdmEntityType expandType = expandContent.getTargetEntitySet().getEntityType();
				Map<String, Object> expandData = readResultSet(query, expandType, EdmUtils.getProperties(expandType), resultSet);
				if (OData2Utils.isEmpty(expandType, expandData)) {
					// nothing there, we need to find the next one
					continue;
				}
				Map<String, Object> customizedExpandData = onCustomizeExpandedNavigatonProperty(targetEntityType, expandType, expandData);
				currentResultSetEntity.addExpandedEntityProperties(expandType, customizedExpandData);
			}
		}
	}

	public List<String> readIdsForExpand (final GetEntitySetUriInfo uriInfo) throws ODataException {
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
	 * @param query the query
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
	public ODataResponse readEntitySimplePropertyValue(final GetSimplePropertyUriInfo uriInfo, final String contentType)
			throws ODataException {
		final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
		final EdmEntityType targetEntityType = targetEntitySet.getEntityType();

		SQLSelectBuilder query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext());
		OData2ResultSetEntity resultEntity = null;
		Collection<EdmProperty> properties = uriInfo.getPropertyPath();
		try (Connection connection = getDataSource().getConnection()){
			try (PreparedStatement statement = createSelectStatement(query, connection)){
				try (ResultSet resultSet = statement.executeQuery()){
					while (resultSet.next()) {
						if (resultEntity == null) {// TODO remove the duplication here with the readEntitySet
							Map<String, Object> data = readResultSet(query, targetEntityType, properties, resultSet);
							resultEntity = new OData2ResultSetEntity(data);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to serve request", e);
			throw new ODataException(e);
		}
		return OData2Utils.writeEntryPropertyValue(getContext(), uriInfo.getPropertyPath().iterator().next(),
				(UriInfo) uriInfo, resultEntity, contentType);
	}

	@Override
	public ODataResponse readEntitySimpleProperty(final GetSimplePropertyUriInfo uriInfo, final String contentType)
			throws ODataException {
		final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
		final EdmEntityType targetEntityType = targetEntitySet.getEntityType();

		SQLSelectBuilder query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext());
		OData2ResultSetEntity resultEntity = null;
		Collection<EdmProperty> properties = uriInfo.getPropertyPath();
		try (Connection connection = getDataSource().getConnection()){
			try(PreparedStatement statement = createSelectStatement(query, connection)){
				try (ResultSet resultSet = statement.executeQuery()){
					while (resultSet.next()) {
						if (resultEntity == null) {// TODO remove the duplication here with the readEntitySet
							Map<String, Object> data = readResultSet(query, targetEntityType, properties, resultSet);
							resultEntity = new OData2ResultSetEntity(data);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to serve request", e);
			throw new ODataException(e);
		}
		return OData2Utils.writeEntryProperty(getContext(), uriInfo.getPropertyPath().iterator().next(),
				(UriInfo) uriInfo, resultEntity, contentType);
	}

	@Override
	public ODataResponse readEntityMedia(final GetMediaResourceUriInfo uriInfo, final String contentType)
			throws ODataException {
		throw new ODataNotImplementedException();
	}

	@Override
	public ODataResponse readEntityLinks(final GetEntitySetLinksUriInfo uriInfo, final String contentType)
			throws ODataException {
		throw new ODataNotImplementedException();
	}

	protected Map<String, Object> readResultSet(SQLSelectBuilder selectEntityQuery, final EdmStructuralType entityType,
												Collection<EdmProperty> properties, final ResultSet resultSet) throws SQLException, ODataException {
		Map<String, Object> result = new HashMap<>();
		for (EdmProperty property : properties) {
			result.put(property.getName(), readProperty(entityType, property, selectEntityQuery, resultSet));
		}
		return result;
	}

	protected Object readProperty(EdmStructuralType entityType, EdmProperty property, SQLSelectBuilder selectEntityQuery,
			ResultSet resultSet) throws SQLException, ODataException {
		Object propertyDbValue;
		if (property.isSimple()) {
			if (!selectEntityQuery.isTransientType(entityType, property)) {
				final String columnName = selectEntityQuery.getSQLTableColumnAlias(entityType, property);
				if ("Binary".equals(property.getType().getName())) {
					propertyDbValue = resultSet.getBytes(columnName);
				} else {
					propertyDbValue = resultSet.getObject(columnName);
					propertyDbValue = onCustomizePropertyValue(entityType, property, entityType, propertyDbValue);
				}
				return propertyDbValue;
			} else {
				return null;
			}
		} else {
			EdmStructuralType complexProperty = (EdmStructuralType) property.getType();
			Map<String, Object> complexPropertyData = new HashMap<>();
			for (String pn : complexProperty.getPropertyNames()) {
				EdmProperty prop = (EdmProperty) complexProperty.getProperty(pn);
				final String columnName = selectEntityQuery.getSQLTableColumnAlias(complexProperty, prop);
				propertyDbValue = resultSet.getObject(columnName);
				propertyDbValue = onCustomizePropertyValue(entityType, property, entityType, propertyDbValue);
				complexPropertyData.put(pn, propertyDbValue);
			}
			return complexPropertyData;
		}
	}

	@Override
	public ODataResponse createEntity(final PostUriInfo uriInfo, final InputStream content,
			final String requestContentType, final String contentType) throws ODataException {
		
		if (this.odata2EventHandler.forbidCreateEntity(uriInfo, requestContentType, contentType)) {
			throw new ODataException(String.format("Create operation on entity: %s is forbidden.",
					OData2Utils.fqn(uriInfo.getTargetType())));
		}
		
		if (this.odata2EventHandler.usingOnCreateEntity(uriInfo, requestContentType, contentType)) {
			return this.odata2EventHandler.onCreateEntity(uriInfo, content,
					requestContentType, contentType);
		}

		final EdmEntitySet entitySet = uriInfo.getTargetEntitySet();
		final EdmEntityType entityType = entitySet.getEntityType();
		ODataEntry entry = parseEntry(entitySet, content, requestContentType, false);
		this.odata2EventHandler.beforeCreateEntity(uriInfo,
				requestContentType, contentType, entry);

		SQLInsertBuilder insertBuilder = this.getSQLQueryBuilder().buildInsertEntityQuery((UriInfo) uriInfo, entry, getContext());

		try (Connection connection = getDataSource().getConnection()){
			try (PreparedStatement statement = createInsertStatement(insertBuilder, connection)){
				statement.executeUpdate();
			}
		} catch (Exception e) {
			LOG.error("Unable to serve request", e);
			throw new ODataException(e);
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
						String msg = "Cannot create entity without key(s)";
						LOG.error(msg);
						throw new ODataException(msg);
					}
				}
				
				((UriInfoImpl) uriInfo).setKeyPredicates(keyPredicates);
				
				// Re-read the inserted entity to get the auto-generated properties
				SQLSelectBuilder query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext());
				OData2ResultSetEntity resultEntity = null;
				Collection<EdmProperty> properties = getProperties(entityType);
				try (Connection connection = getDataSource().getConnection()){
					try (PreparedStatement statement = createSelectStatement(query, connection)){
						try (ResultSet resultSet = statement.executeQuery()){
							while (resultSet.next()) {
								if (resultEntity == null) {
									Map<String, Object> data = readResultSet(query, entityType, properties, resultSet);
									resultEntity = new OData2ResultSetEntity(data);
								}
							}
						}
					}
					ODataResponse response = OData2Utils.writeEntryWithExpand(getContext(), (UriInfo) uriInfo, resultEntity, contentType);
					this.odata2EventHandler.afterCreateEntity(uriInfo, requestContentType, contentType, entry);
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
		this.odata2EventHandler.afterCreateEntity(uriInfo, requestContentType, contentType, entry);
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
		if (this.odata2EventHandler.usingOnDeleteEntity(uriInfo, contentType)) {
			return this.odata2EventHandler.onDeleteEntity(uriInfo, contentType);
		}
		this.odata2EventHandler.beforeDeleteEntity(uriInfo, contentType);

		SQLDeleteBuilder deleteBuilder = this.getSQLQueryBuilder().buildDeleteEntityQuery((UriInfo) uriInfo, mapKeys(uriInfo.getKeyPredicates()), getContext());

		try (Connection connection = getDataSource().getConnection()) {
			try (PreparedStatement statement = createDeleteStatement(deleteBuilder, connection)) {
				statement.executeUpdate();
			}
		} catch (Exception e) {
			LOG.error("Unable to serve request", e);
			throw new ODataException(e);
		}

		ODataResponse response = ODataResponse.newBuilder().build();
		this.odata2EventHandler.afterDeleteEntity(uriInfo, contentType);
		return response;
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

		if (this.odata2EventHandler.usingOnUpdateEntity(uriInfo, requestContentType, merge, contentType)) {
			return this.odata2EventHandler.onUpdateEntity(uriInfo, content, requestContentType, merge, contentType);
		}
		
		final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
		final EdmEntityType targetEntityType = targetEntitySet.getEntityType();

		SQLSelectBuilder query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo, getContext());
		OData2ResultSetEntity resultEntity = null;
		Collection<EdmProperty> properties = targetEntityType.getKeyProperties();
		try (Connection connection = getDataSource().getConnection()) {
			try (PreparedStatement statement = createSelectStatement(query, connection)) {
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						if (resultEntity == null) {
							Map<String, Object> data = readResultSet(query, targetEntityType, properties, resultSet);
							resultEntity = new OData2ResultSetEntity(data);
						}
					}
					if (resultEntity == null) {
						throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to serve request", e);
			throw new ODataException(e);
		}

		ODataEntry entry = parseEntry(targetEntitySet, content, requestContentType, false);
		this.odata2EventHandler.beforeUpdateEntity(uriInfo, requestContentType, merge, contentType, entry);

		SQLUpdateBuilder updateBuilder = this.getSQLQueryBuilder().buildUpdateEntityQuery((UriInfo) uriInfo, entry,
				mapKeys(uriInfo.getKeyPredicates()), getContext());

		try(Connection connection = getDataSource().getConnection()){
			try (PreparedStatement statement = createUpdateStatement(updateBuilder, connection)){
				statement.executeUpdate();
			}
		} catch (Exception e) {
			LOG.error("Unable to serve request", e);
			throw new ODataException(e);
		}
		ODataResponse response = ODataResponse.newBuilder().status(HttpStatusCodes.NO_CONTENT).build();
		this.odata2EventHandler.afterUpdateEntity(uriInfo, requestContentType, merge, contentType, entry);
		return response;
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
}
