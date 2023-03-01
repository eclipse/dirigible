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
exports.ACTIVE = "ACTIVE";
exports.SOLVED = "SOLVED";
exports.IGNORED = "IGNORED";

exports.save = function(location, type, line, column, cause, expected, category, module, source, program) {
   org.eclipse.dirigible.components.api.platform.ProblemsFacade.save(location, type, line, column, cause, expected,
        category, module, source, program);
};

exports.findProblem = function(id) {
    returnorg.eclipse.dirigible.components.api.platform.ProblemsFacade.findProblem(id);
};

exports.fetchAllProblems = function() {
    returnorg.eclipse.dirigible.components.api.platform.ProblemsFacade.fetchAllProblems();
};

exports.fetchProblemsBatch = function(condition, limit) {
    returnorg.eclipse.dirigible.components.api.platform.ProblemsFacade.fetchProblemsBatch(condition, limit);
};

exports.deleteProblem = function(id) {
   org.eclipse.dirigible.components.api.platform.ProblemsFacade.deleteProblem(id);
};

exports.deleteAllByStatus = function(status) {
   org.eclipse.dirigible.components.api.platform.ProblemsFacade.deleteAllByStatus(status);
};

exports.clearAllProblems = function() {
   org.eclipse.dirigible.components.api.platform.ProblemsFacade.clearAllProblems();
};

exports.updateStatus = function(id, status) {
   org.eclipse.dirigible.components.api.platform.ProblemsFacade.updateStatus(id, status);
};

exports.updateStatusMultiple = function(ids, status) {
   org.eclipse.dirigible.components.api.platform.ProblemsFacade.updateStatusMultiple(ids, status);
};