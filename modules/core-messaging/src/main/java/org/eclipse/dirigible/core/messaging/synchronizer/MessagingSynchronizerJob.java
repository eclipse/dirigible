/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.messaging.synchronizer;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

public class MessagingSynchronizerJob extends AbstractSynchronizerJob {
	
	private MessagingSynchronizer messagingSynchronizer = StaticInjector.getInjector().getInstance(MessagingSynchronizer.class);
	
	@Override
	public ISynchronizer getSynchronizer() {
		return messagingSynchronizer;
	}

}
