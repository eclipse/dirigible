/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.supplemental.fileupload.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadHandler;

public final class FileUploadHandlerStore {

	private static final String ATTR_NAME = FileUploadHandlerStore.class
			.getName() + ".instance"; //$NON-NLS-1$

	private static final Object LOCK = new Object();
	private final Map<String, FileUploadHandler> handlers;
	private final Object lock;

	private FileUploadHandlerStore() {
		handlers = new HashMap<String, FileUploadHandler>();
		lock = new Object();
	}

	@SuppressWarnings("deprecation")
	public static FileUploadHandlerStore getInstance() {
		FileUploadHandlerStore result;
		synchronized (LOCK) {
			result = (FileUploadHandlerStore) RWT.getApplicationStore()
					.getAttribute(ATTR_NAME);
			if (result == null) {
				result = new FileUploadHandlerStore();
				RWT.getApplicationStore().setAttribute(ATTR_NAME, result);
			}
		}
		return result;
	}

	public void registerHandler(String token,
			FileUploadHandler fileUploadHandler) {
		synchronized (lock) {
			handlers.put(token, fileUploadHandler);
		}
	}

	public void deregisterHandler(String token) {
		synchronized (lock) {
			handlers.remove(token);
		}
	}

	public FileUploadHandler getHandler(String token) {
		FileUploadHandler result;
		synchronized (lock) {
			result = handlers.get(token);
		}
		return result;
	}

	public static String createToken() {
		int random1 = (int) (Math.random() * 0xfffffff);
		int random2 = (int) (Math.random() * 0xfffffff);
		return Integer.toHexString(random1) + Integer.toHexString(random2);
	}
}
