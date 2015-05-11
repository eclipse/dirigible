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

package org.eclipse.dirigible.ide.template.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class VelocityGenerator {
	
	private static final String COULD_NOT_EVALUATE_TEMPLATE = Messages.getString("VelocityGenerator.COULD_NOT_EVALUATE_TEMPLATE"); //$NON-NLS-1$
	private VelocityEngine engine;

	public VelocityGenerator() {
		engine = new VelocityEngine();
		try {
			engine.init();
		} catch (Throwable e) {
//			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	public void generate(Reader in, Writer out, Map<String, Object> parameters,
			String tag) throws VelocityGeneratorException {
		try {
			final VelocityContext context = new VelocityContext();
			prepareContextData(parameters, context);
			engine.evaluate(context, out, tag, in);
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new VelocityGeneratorException(COULD_NOT_EVALUATE_TEMPLATE, ex);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	public void generate(InputStream in, OutputStream out,
			Map<String, Object> parameters, String tag)
			throws VelocityGeneratorException {
		try {

			final VelocityContext context = new VelocityContext();
			prepareContextData(parameters, context);
			final Reader reader = new InputStreamReader(in);
			final Writer writer = new OutputStreamWriter(out);
			engine.evaluate(context, writer, tag, reader);
			writer.flush();
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new VelocityGeneratorException(COULD_NOT_EVALUATE_TEMPLATE,
					ex);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new VelocityGeneratorException(COULD_NOT_EVALUATE_TEMPLATE, e);
			}
		}
	}

	private void prepareContextData(Map<String, Object> parameters,
			VelocityContext context) {
		Iterator<Map.Entry<String, Object>> iterator = parameters.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator
					.next();
			context.put(entry.getKey(), entry.getValue());
		}
	}

}
