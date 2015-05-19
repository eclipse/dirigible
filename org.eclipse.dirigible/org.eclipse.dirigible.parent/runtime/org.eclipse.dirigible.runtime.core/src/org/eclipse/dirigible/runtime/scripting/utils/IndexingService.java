/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting.utils;

import org.eclipse.dirigible.repository.ext.indexing.IIndex;
import org.eclipse.dirigible.repository.ext.indexing.LuceneMemoryIndexer;
import org.eclipse.dirigible.runtime.scripting.IIndexingService;

public class IndexingService <T> implements IIndexingService<T> {

	public IIndex<T> getIndex(String indexName) {
		return LuceneMemoryIndexer.getIndex(indexName);
	}

}
