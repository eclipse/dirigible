/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.csvim.utils;

import org.eclipse.dirigible.components.api.platform.ProblemsFacade;
import org.eclipse.dirigible.components.data.csvim.synchronizer.CsvProcessor;
import org.eclipse.dirigible.components.data.csvim.synchronizer.CsvimProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvimUtils {

    public static final String PROGRAM_DEFAULT = "Eclipse Dirigible";

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(CsvProcessor.class);

    /**
     * Use to log errors from artifact processing.
     *
     * @param errorMessage the error message
     * @param errorType    the error type
     * @param location     the location
     * @param artifactType the artifact type
     */
    public static void logProcessorErrors(String errorMessage, String errorType, String location, String
            artifactType, String module) {
        try {
            ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, module, CsvimProcessor.class.getName(), PROGRAM_DEFAULT);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("There is an issue with logging of the Errors.");
            }
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
        }
    }
}
