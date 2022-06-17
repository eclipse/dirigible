/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.ACTIVE = "ACTIVE";
exports.SOLVED = "SOLVED";
exports.IGNORED = "IGNORED";

exports.save = function(location, type, line, column, cause, expected, category, module, source, program) {
    org.eclipse.dirigible.api.v3.problems.ProblemsFacade.save(location, type, line, column, cause, expected,
        category, module, source, program);
};

exports.findProblem = function(id) {
    return org.eclipse.dirigible.api.v3.problems.ProblemsFacade.findProblem(id);
};

exports.fetchAllProblems = function() {
    return org.eclipse.dirigible.api.v3.problems.ProblemsFacade.fetchAllProblems();
};

exports.fetchProblemsBatch = function(condition, limit) {
    return org.eclipse.dirigible.api.v3.problems.ProblemsFacade.fetchProblemsBatch(condition, limit);
};

exports.deleteProblem = function(id) {
    org.eclipse.dirigible.api.v3.problems.ProblemsFacade.deleteProblem(id);
};

exports.deleteAllByStatus = function(status) {
    org.eclipse.dirigible.api.v3.problems.ProblemsFacade.deleteAllByStatus(status);
};

exports.clearAllProblems = function() {
    org.eclipse.dirigible.api.v3.problems.ProblemsFacade.clearAllProblems();
};

exports.updateStatus = function(id, status) {
    org.eclipse.dirigible.api.v3.problems.ProblemsFacade.updateStatus(id, status);
};

exports.updateStatusMultiple = function(ids, status) {
    org.eclipse.dirigible.api.v3.problems.ProblemsFacade.updateStatusMultiple(ids, status);
};