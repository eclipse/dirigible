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
package org.eclipse.dirigible.api.v3.documents;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PDFFacade.
 */
public class PDFFacade {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(PDFFacade.class);

	/**
	 * Generate.
	 *
	 * @param template the template
	 * @param data the data
	 * @return the byte[]
	 */
	public static byte[] generate(String template, String data) {
		if (logger.isInfoEnabled()) {
			logger.info("Generating PDF from template: [\n{}\n] and data: [\n{}\n]", template, data);
		}

		try {
			StreamSource templateSource = new StreamSource(IOUtils.toInputStream(template, Charset.defaultCharset()));
			StreamSource dataSource = new StreamSource(IOUtils.toInputStream(data, Charset.defaultCharset()));
			
			FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
			FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, baos);
			
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(templateSource);
			
			Result result = new SAXResult(fop.getDefaultHandler());
			
			transformer.transform(dataSource, result);
			return baos.toByteArray();
		} catch (FOPException | TransformerException e) {
			logger.error(e.getMessage());
			throw new PDFException(e.getMessage(), e);
		}
	}

}
