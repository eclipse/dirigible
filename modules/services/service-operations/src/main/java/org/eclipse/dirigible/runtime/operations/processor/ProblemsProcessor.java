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
package org.eclipse.dirigible.runtime.operations.processor;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.problems.model.ProblemsModel;
import org.eclipse.dirigible.core.problems.service.ProblemsCoreService;
import java.util.List;

public class ProblemsProcessor {

    private ProblemsCoreService problemsCoreService = new ProblemsCoreService();

    public String list() throws ProblemsException {

        List<ProblemsModel> jobs = problemsCoreService.getAllProblems();

        return GsonHelper.GSON.toJson(jobs);
    }
}
