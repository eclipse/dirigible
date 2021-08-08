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
exports.createOrUpdateProblem = function(location, type, line, column, category, module, source, program) {
    org.eclipse.dirigible.api.v3.log.problems.facade.DirigibleProblemsFacade.createOrUpdateProblem(location, type, line, column,
        category, module, source, program);
};

exports.findProblem = function(id) {
    return org.eclipse.dirigible.api.v3.log.problems.facade.DirigibleProblemsFacade.findProblem(id);
};

exports.fetchAllProblems = function() {
    return org.eclipse.dirigible.api.v3.log.problems.facade.DirigibleProblemsFacade.fetchAllProblems();
};

exports.deleteProblem = function(id) {
    org.eclipse.dirigible.api.v3.log.problems.facade.DirigibleProblemsFacade.deleteProblem(id);
};

exports.deleteAllByStatus = function(status) {
    org.eclipse.dirigible.api.v3.log.problems.facade.DirigibleProblemsFacade.deleteAllByStatus(status);
};

exports.clearAllProblems = function() {
    org.eclipse.dirigible.api.v3.log.problems.facade.DirigibleProblemsFacade.clearAllProblems();
};

exports.updateStatus = function(id, status) {
    org.eclipse.dirigible.api.v3.log.problems.facade.DirigibleProblemsFacade.updateStatus(id, status);
};

exports.updateStatusMultiple = function(ids, status) {
    org.eclipse.dirigible.api.v3.log.problems.facade.DirigibleProblemsFacade.updateStatusMultiple(ids, status);
};