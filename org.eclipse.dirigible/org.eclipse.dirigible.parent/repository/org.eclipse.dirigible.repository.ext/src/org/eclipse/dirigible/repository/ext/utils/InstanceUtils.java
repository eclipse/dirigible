/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.utils;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Utilities related to the current server instance
 */
public class InstanceUtils {

	private static final Logger logger = Logger.getLogger(InstanceUtils.class);

	static String instanceName = null;

	/**
	 * Unique instance name taken as environment parameter or as the host name
	 *
	 * @return instance name
	 */
	public static String getInstanceName() {
		if (instanceName == null) {
			instanceName = System.getProperty(ICommonConstants.PARAM_INSTANCE_NAME);
			if (instanceName == null) {
				try {
					instanceName = java.net.InetAddress.getLocalHost().getHostName();
				} catch (UnknownHostException e) {
					// logger.error(e.getMessage(), e);

					if (instanceName == null) {
						try {
							instanceName = IOUtils.toString(Runtime.getRuntime().exec("hostname").getInputStream(), "UTF-8");
						} catch (IOException e1) {
							// e1.printStackTrace();
						}
					}

				}
			}

			if (instanceName == null) {
				instanceName = "LOCAL";
			}
		}

		return instanceName.trim();
	}

}
