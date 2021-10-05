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
package org.eclipse.dirigible.engine.odata2.sql.processor;

import org.apache.olingo.odata2.api.commons.InlineCount;
import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataBadRequestException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.info.*;
import org.apache.olingo.odata2.core.uri.KeyPredicateImpl;
import org.apache.olingo.odata2.core.uri.UriInfoImpl;
import org.eclipse.dirigible.engine.odata2.sql.api.OData2EventHandler;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLProcessor;
import org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLContext;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQuery;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLQueryBuilder;
import org.eclipse.dirigible.engine.odata2.sql.builder.expression.SQLExpressionSelect;
import org.eclipse.dirigible.engine.odata2.sql.utils.OData2ResultSetEntity;
import org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public AbstractSQLProcessor() {
		this.odata2EventHandler = new DummyOData2EventHandler();
	}
	
	public AbstractSQLProcessor(OData2EventHandler odata2EventHandler) {
		this.odata2EventHandler = odata2EventHandler;
	}
	
	

	@Override
	public ODataResponse countEntitySet(final GetEntitySetCountUriInfo uriInfo, final String contentType)
			throws ODataException {
		if (uriInfo.getTop() != null || uriInfo.getSkip() != null) {
			throw new ODataNotImplementedException();
		}
		try {
			SQLQuery sqlQuery = this.getSQLQueryBuilder().buildSelectCountQuery((UriInfo) uriInfo);
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
		throw new ODataException("Not Implemented");
	}

	protected int doCountEntitySet(SQLQuery sqlQuery, final Connection connection) throws ODataException, SQLException {
		// TODO cache the metadata, because it fires a DB query every time
		// TODO do we really need to select the entities?
		String sql = sqlQuery.buildSelect(createSQLContext(connection));
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			LOG.info(sql);
			sqlQuery.setParamsOnStatement(preparedStatement);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				// TODO do we need to assert that resultSet.next() == true and subsequently
				// resultSet.next() == false here?
				resultSet.next();
				int count = resultSet.getInt(1);
				return count;
			}
		}
	}

	protected SQLContext createSQLContext(final Connection connection) throws SQLException {
		return new SQLContext(connection.getMetaData(), this.getContext());
	}

	protected PreparedStatement createSelectStatement(SQLQuery query, final Connection connection)
			throws SQLException, ODataException {
		String sql = query.buildSelect(createSQLContext(connection));
		LOG.info(sql);
		PreparedStatement statement;
		if (query.getSelectExpression().getSkip() != SQLExpressionSelect.NOT_SET) {
			statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			/*
			 * Restrict fetch size to prevent OutOfMemoryErrors for huge page sizes. If no
			 * fetch size is specified, jConnect will fetch all records up to the requested
			 * page (in case of the last page, this would be the entire result set).
			 * 
			 * Internal Incident:
			 * https://support.wdf.sap.corp/sap/support/message/1780258655
			 */

			statement.setFetchSize(SQLQueryBuilder.DEFAULT_SERVER_PAGING_SIZE);

		} else {
			statement = connection.prepareStatement(sql);
		}

		query.setParamsOnStatement(statement);
		return statement;
	}

	protected PreparedStatement createInsertStatement(SQLQuery query, final Connection connection)
			throws SQLException, ODataException {
		String sql = query.buildInsert(createSQLContext(connection));
		LOG.info(sql);
		PreparedStatement statement;

		statement = connection.prepareStatement(sql);

		query.setValuesOnStatement(statement);
		return statement;
	}

	// TODO add unit tests for the expand functionality
	@Override
	public ODataResponse readEntity(final GetEntityUriInfo uriInfo, final String contentType) throws ODataException {
		
		final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
		final EdmEntityType targetEntityType = targetEntitySet.getEntityType();

		SQLQuery query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo);
		OData2ResultSetEntity resultEntity = null;
		// TODO change logic to read columns from ResultSet? (key has to be read even if
		// it is now requested via $select)
		Collection<EdmProperty> properties = getSelectedProperties(uriInfo.getSelect(), targetEntityType);
		try (Connection connection = getDataSource().getConnection()){
			try (PreparedStatement statement = createSelectStatement(query, connection)){
				try (ResultSet resultSet = statement.executeQuery()){
					query.setOffset(resultSet);
					while (query.next(resultSet)) {

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
		
		ODataResponse response = OData2Utils.writeEntryWithExpand(getContext(), (UriInfo) uriInfo, resultEntity, contentType);
		return response;
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
				SQLQuery countEntitySet = this.getSQLQueryBuilder().buildSelectCountQuery((UriInfo) uriInfo);
				count = doCountEntitySet(countEntitySet, connection); // does not close the connection
			} else {
				count = null;
			}
			SQLQuery query;
			List<String> readIdsForExpand = new ArrayList<>();
			if (OData2Utils.hasExpand((UriInfo) uriInfo)) {
				LOG.info("Reading the ids that will be used for $expand");
				readIdsForExpand = readIdsForExpand(uriInfo);
				LOG.info("Using IDs for $expand: {}", readIdsForExpand);
			}
			query = this.getSQLQueryBuilder().buildSelectEntitySetQuery((UriInfo) uriInfo, readIdsForExpand);

			try (PreparedStatement statement = createSelectStatement(query, connection)) {
				try (ResultSet resultSet = statement.executeQuery()) {
					query.setOffset(resultSet);
					OData2ResultSetEntity currentResultSetEntity = null;

					// we iterate the ResultSet rows here
					// for every row, we create one target entity instance and compare it with the
					// previous one. If the key property values are different, then we move to the
					// next target instance.
					// Otherwise that row has only navigation properties, then in that iteration
					// only the navigation properties are set
					while (query.next(resultSet)) {// TODO remove the duplication here

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

	private void processExpand(EdmEntityType targetEntityType, SQLQuery query, ResultSet resultSet, OData2ResultSetEntity currentResultSetEntity,
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
			SQLQuery queryForIdsInExpand = this.getSQLQueryBuilder().buildSelectEntitySetIdsForTopAndExpandQuery((UriInfo) uriInfo);
			try (PreparedStatement statement = createSelectStatement(queryForIdsInExpand, connection)) {
				try (ResultSet resultSet = statement.executeQuery()) {
					queryForIdsInExpand.setOffset(resultSet);
					while (queryForIdsInExpand.next(resultSet)) {// TODO remove the duplication here
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
	protected String generateNextLink(SQLQuery query, EdmEntityType targetEntityType) throws ODataException {
		int top = query.getSelectExpression().getTop();
		int pagingSize = this.getSQLQueryBuilder().getEntityPagingSize(targetEntityType);
		return OData2Utils.generateNextLink(getContext(), top, pagingSize);
	}

	@Override
	public ODataResponse readEntitySimplePropertyValue(final GetSimplePropertyUriInfo uriInfo, final String contentType)
			throws ODataException {
		final EdmEntitySet targetEntitySet = uriInfo.getTargetEntitySet();
		final EdmEntityType targetEntityType = targetEntitySet.getEntityType();

		SQLQuery query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo);
		OData2ResultSetEntity resultEntity = null;
		Collection<EdmProperty> properties = uriInfo.getPropertyPath();
		try (Connection connection = getDataSource().getConnection()){
			try (PreparedStatement statement = createSelectStatement(query, connection)){
				try (ResultSet resultSet = statement.executeQuery()){
					query.setOffset(resultSet);
					while (query.next(resultSet)) {
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

		SQLQuery query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo);
		OData2ResultSetEntity resultEntity = null;
		Collection<EdmProperty> properties = uriInfo.getPropertyPath();
		try (Connection connection = getDataSource().getConnection()){
			try(PreparedStatement statement = createSelectStatement(query, connection)){
				try (ResultSet resultSet = statement.executeQuery()){
					query.setOffset(resultSet);
					while (query.next(resultSet)) {
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

	protected Map<String, Object> readResultSet(SQLQuery selectEntityQuery, final EdmStructuralType entityType,
			Collection<EdmProperty> properties, final ResultSet resultSet) throws SQLException, ODataException {
		Map<String, Object> result = new HashMap<>();
		for (EdmProperty property : properties) {
			result.put(property.getName(), readProperty(entityType, property, selectEntityQuery, resultSet));
		}
		return result;
	}

	protected Object readProperty(EdmStructuralType entityType, EdmProperty property, SQLQuery selectEntityQuery,
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
		SQLQuery query = this.getSQLQueryBuilder().buildInsertEntityQuery((UriInfo) uriInfo, entry);

		try (Connection connection = getDataSource().getConnection()){
			try (PreparedStatement statement = createInsertStatement(query, connection)){
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
				query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo);
				OData2ResultSetEntity resultEntity = null;
				Collection<EdmProperty> properties = getProperties(entityType);
				try (Connection connection = getDataSource().getConnection()){
					try (PreparedStatement statement = createSelectStatement(query, connection)){
						try (ResultSet resultSet = statement.executeQuery()){
							query.setOffset(resultSet);
							while (query.next(resultSet)) {
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
		ODataEntry entryValues;
		try {
			EntityProviderReadProperties entityProviderProperties = EntityProviderReadProperties.init()
					.mergeSemantic(merge).build();
			entryValues = EntityProvider.readEntry(requestContentType, entitySet, content, entityProviderProperties);
		} catch (Exception e) {
			throw new ODataBadRequestException(ODataBadRequestException.BODY, e);
		}
		return entryValues;
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

		SQLQuery query = this.getSQLQueryBuilder().buildDeleteEntityQuery((UriInfo) uriInfo, mapKeys(uriInfo.getKeyPredicates()));

		try (Connection connection = getDataSource().getConnection()) {
			try (PreparedStatement statement = createDeleteStatement(query, connection)) {
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

	protected PreparedStatement createDeleteStatement(SQLQuery query, final Connection connection) throws SQLException, ODataException {
		String sql = query.buildDelete(createSQLContext(connection));
		LOG.info(sql);
		PreparedStatement statement = connection.prepareStatement(sql);
		query.setKeysOnStatement(statement);
		return statement;
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

		SQLQuery query = this.getSQLQueryBuilder().buildSelectEntityQuery((UriInfo) uriInfo);
		OData2ResultSetEntity resultEntity = null;
		Collection<EdmProperty> properties = targetEntityType.getKeyProperties();
		try (Connection connection = getDataSource().getConnection()) {
			try (PreparedStatement statement = createSelectStatement(query, connection)) {
				try (ResultSet resultSet = statement.executeQuery()) {
					query.setOffset(resultSet);
					while (query.next(resultSet)) {
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

		query = this.getSQLQueryBuilder().buildUpdateEntityQuery((UriInfo) uriInfo, entry,
				mapKeys(uriInfo.getKeyPredicates()));

		try(Connection connection = getDataSource().getConnection()){
			try (PreparedStatement statement = createUpdateStatement(query, connection)){
				statement.executeUpdate();
			}
		} catch (Exception e) {
			LOG.error("Unable to serve request", e);
			throw new ODataException(e);
		}

		ODataContext context = getContext();
		EntityProviderWriteProperties writeProperties = EntityProviderWriteProperties
				.serviceRoot(context.getPathInfo().getServiceRoot()).expandSelectTree(entry.getExpandSelectTree())
				.build();
		final ODataResponse response = EntityProvider.writeEntry(contentType, targetEntitySet, entry.getProperties(),
				writeProperties);
		
		this.odata2EventHandler.afterUpdateEntity(uriInfo, requestContentType, merge, contentType, entry);
		return response;
	}

	protected PreparedStatement createUpdateStatement(SQLQuery query, final Connection connection) throws SQLException, ODataException {
		String sql = query.buildUpdate(createSQLContext(connection));
		LOG.info(sql);
		PreparedStatement statement;
		statement = connection.prepareStatement(sql);
		query.setValuesAndKeysOnStatement(statement);
		return statement;
	}

}
