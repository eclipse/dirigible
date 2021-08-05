/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.log.problems.facade;

import org.eclipse.dirigible.api.v3.log.problems.api.IDirigibleProblemsCoreService;
import org.eclipse.dirigible.api.v3.log.problems.exceptions.DirigibleProblemsException;
import org.eclipse.dirigible.api.v3.log.problems.model.DirigibleProblemsModel;
import org.eclipse.dirigible.api.v3.log.problems.service.DirigibleProblemsCoreService;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirigibleProblemsFacade implements IScriptingFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirigibleProblemsFacade.class);

    public static final void persistProblem(String location, String type, String line, String column,
                                        String category, String module, String source, String program) throws DirigibleProblemsException {

        IDirigibleProblemsCoreService dirigibleProblemsCoreService = new DirigibleProblemsCoreService();
        DirigibleProblemsModel problemsModel = new DirigibleProblemsModel(location, type, line, column, category, module, source, program);
        dirigibleProblemsCoreService.createOrUpdateProblem(problemsModel);
        LOGGER.error(problemsModel.toJson());
    }
}
