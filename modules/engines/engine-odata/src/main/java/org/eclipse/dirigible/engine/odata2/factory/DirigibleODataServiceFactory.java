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
package org.eclipse.dirigible.engine.odata2.factory;

import static org.eclipse.dirigible.engine.odata2.sql.processor.DefaultSQLProcessor.DEFAULT_DATA_SOURCE_CONTEXT_KEY;

import javax.sql.DataSource;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.core.edm.provider.EdmxProvider;
import org.eclipse.dirigible.api.v3.db.DatabaseFacade;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.engine.odata2.api.IODataCoreService;
import org.eclipse.dirigible.engine.odata2.service.ODataCoreService;
import org.eclipse.dirigible.engine.odata2.sql.binding.DefaultEdmTableMappingProvider;
import org.eclipse.dirigible.engine.odata2.sql.processor.DefaultSQLProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirigibleODataServiceFactory extends ODataServiceFactory {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSQLProcessor.class);
	
	private static IODataCoreService odataCoreService = StaticInjector.getInjector().getInstance(ODataCoreService.class);

	@Override
	public ODataService createService(ODataContext ctx) throws ODataException {
		try {
			EdmProvider edmProvider = new EdmxProvider();
			((EdmxProvider) edmProvider).parse(odataCoreService.getMetadata(), false);

			setDefaultDataSource(ctx);
			
			DefaultEdmTableMappingProvider tableMappingProvider = new DefaultEdmTableMappingProvider();

			DefaultSQLProcessor singleProcessor = new DefaultSQLProcessor(tableMappingProvider);

			return createODataSingleProcessorService(edmProvider, singleProcessor);
		} catch (org.eclipse.dirigible.engine.odata2.api.ODataException e) {
			LOG.error(e.getMessage(), e);
			throw new ODataException(e);
		}
	}

	@Override
	public <T extends ODataCallback> T getCallback(Class<T> callbackInterface) {
		if (callbackInterface.isAssignableFrom(ODataErrorCallback.class)) {
			return (T) new ODataDefaulErrorCallback();
		}
		return super.getCallback(callbackInterface);
	}

	private class ODataDefaulErrorCallback implements ODataErrorCallback {
		@Override
		public ODataResponse handleError(ODataErrorContext context) throws ODataApplicationException {
			LOG.error(context.getMessage());
			return EntityProvider.writeErrorDocument(context);
		}
	}
	
	private void setDefaultDataSource(ODataContext ctx) throws ODataException {
        DataSource dataSource;
        dataSource = DatabaseFacade.getDefaultDataSource();
        ctx.setParameter(DEFAULT_DATA_SOURCE_CONTEXT_KEY, dataSource);
    }
}
