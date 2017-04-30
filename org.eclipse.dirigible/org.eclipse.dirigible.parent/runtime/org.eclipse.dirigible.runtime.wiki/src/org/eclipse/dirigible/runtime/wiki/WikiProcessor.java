/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.wiki;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.ext.fs.FileSystemUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class WikiProcessor {

	private static final Logger logger = Logger.getLogger(WikiProcessor.class);

	public static final String DEFAULT_WIKI_EXTENSION = ".wiki"; //$NON-NLS-1$
	public static final String CONFLUENCE_EXTENSION = ".confluence"; //$NON-NLS-1$

	public static final String MARKDOWN_EXTENSION = ".markdown"; //$NON-NLS-1$
	public static final String MARKDOWN_EXTENSION2 = ".mdown"; //$NON-NLS-1$
	public static final String MARKDOWN_EXTENSION3 = ".mkdn"; //$NON-NLS-1$
	public static final String MARKDOWN_EXTENSION4 = ".md"; //$NON-NLS-1$
	public static final String MARKDOWN_EXTENSION5 = ".mkd"; //$NON-NLS-1$
	public static final String MARKDOWN_EXTENSION6 = ".mdwn"; //$NON-NLS-1$

	// public static final String MEDIAWIKI_EXTENSION = ".mediawiki"; //$NON-NLS-1$

	public static final String TEXTILE_EXTENSION = ".textile"; //$NON-NLS-1$

	public static final String TRACWIKI_EXTENSION = ".tracwiki"; //$NON-NLS-1$

	public static final String TWIKI_EXTENSION = ".twiki"; //$NON-NLS-1$

	public static final String BATCH_EXTENSION = ".wikis"; //$NON-NLS-1$

	private static final String ERROR_READING_BATCH_WITH_WIKI_PAGES = Messages.getString("WikiRegistryServlet.ERROR_READING_BATCH_WITH_WIKI_PAGES"); //$NON-NLS-1$

	private static final int WIKI_CACHE_LIMIT = 10000; // ~ 100MB for cache of the wikis

	// // caches
	// private static final Map<String, Date> resourceToModification = Collections.synchronizedMap(new HashMap<String,
	// Date>());
	// private static final Map<String, String> resourceToWiki = Collections.synchronizedMap(new HashMap<String,
	// String>());

	public static byte[] processContent(byte[] rawContent, IEntity entity) throws IOException {
		String name = entity.getName();
		if (name.endsWith(CONFLUENCE_EXTENSION) || name.endsWith(DEFAULT_WIKI_EXTENSION)) {
			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_CONFLUENCE);
		} else if (name.endsWith(MARKDOWN_EXTENSION) || name.endsWith(MARKDOWN_EXTENSION2) || name.endsWith(MARKDOWN_EXTENSION3)
				|| name.endsWith(MARKDOWN_EXTENSION4) || name.endsWith(MARKDOWN_EXTENSION5) || name.endsWith(MARKDOWN_EXTENSION6)) {
			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_MARKDOWN);
			// } else if (name.endsWith(MEDIAWIKI_EXTENSION)) {
			// return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_MEDIAWIKI);
		} else if (name.endsWith(TEXTILE_EXTENSION)) {
			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_TEXTILE);
		} else if (name.endsWith(TRACWIKI_EXTENSION)) {
			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_TRACWIKI);
		} else if (name.endsWith(TWIKI_EXTENSION)) {
			return wikiToHtml(rawContent, entity, WikiUtils.WIKI_FORMAT_TWIKI);
		} else if (name.endsWith(BATCH_EXTENSION)) {
			return batchToHtml(rawContent, entity);
		}
		return rawContent;
	}

	private static byte[] wikiToHtml(byte[] rawContent, IEntity entity, String format) throws IOException {

		byte[] result = null;

		// Date lastModification = resourceToModification.get(entity.getPath());
		// String existingWiki = resourceToWiki.get(entity.getPath());
		// if (lastModification == null
		// || existingWiki == null
		// || !lastModification.after(entity.getInformation().getModifiedAt())) {

		WikiUtils wikiUtils = new WikiUtils();
		String htmlContent = wikiUtils.toHtml(new String(rawContent, "UTF8"), format);
		result = htmlContent.getBytes("UTF8");

		// resourceToModification.put(entity.getPath(), entity.getInformation().getModifiedAt());
		// resourceToWiki.put(entity.getPath(), htmlContent);
		//
		// if (resourceToModification.size() > WIKI_CACHE_LIMIT) {
		// // to many cached wiki pages, clean and collect the most used again
		// resourceToModification.clear();
		// resourceToWiki.clear();
		// logger.info("Wiki cache reaches its limit of 10000 pages. Clean-up done."); //$NON-NLS-1$
		// }
		// } else {
		// result = existingWiki.getBytes();
		// }
		return result;
	}

	private static byte[] batchToHtml(byte[] rawContent, IEntity entity) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader((new ByteArrayInputStream(rawContent))));
		String line = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			while ((line = reader.readLine()) != null) {
				IResource wikiResource = entity.getRepository().getResource(entity.getParent().getPath() + IRepository.SEPARATOR + line);
				if (wikiResource.exists()) {
					outputStream.write(wikiToHtml(wikiResource.getContent(), wikiResource,
							WikiUtils.getLanguageByExtension(FileSystemUtils.getExtension(line))));
				} else {
					logger.error(String.format("Error while render batch of wiki pages. Resource %s does not exist", wikiResource.getPath())); //$NON-NLS-1$
				}
			}
		} catch (IOException e) {
			throw new IOException(ERROR_READING_BATCH_WITH_WIKI_PAGES, e);
		}
		return outputStream.toByteArray();
	}
}
