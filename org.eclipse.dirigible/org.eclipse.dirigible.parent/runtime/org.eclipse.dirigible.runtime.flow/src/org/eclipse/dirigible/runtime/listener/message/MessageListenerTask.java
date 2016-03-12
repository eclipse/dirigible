/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.listener.message;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.task.IRunnableTask;

public class MessageListenerTask implements IRunnableTask {

	private static final Logger logger = Logger.getLogger(MessageListenerTask.class);

	@Override
	public String getName() {
		return "Message Listener Task";
	}

	@Override
	public void start() {
		logger.debug("entering: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "start()"); //$NON-NLS-1$
		try {
			MessageListenerManager.getInstance().processMessages();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("exiting: " + this.getClass().getCanonicalName() + " -> " //$NON-NLS-1$ //$NON-NLS-2$
				+ "start()"); //$NON-NLS-1$
	}

}
