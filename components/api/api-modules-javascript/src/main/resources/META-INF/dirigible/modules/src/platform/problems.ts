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

const ProblemsFacade = Java.type("org.eclipse.dirigible.components.api.platform.ProblemsFacade");

export const ACTIVE = "ACTIVE";
export const SOLVED = "SOLVED";
export const IGNORED = "IGNORED";

export function save(location, type, line, column, cause, expected, category, module, source, program) {
   ProblemsFacade.save(location, type, line, column, cause, expected,
      category, module, source, program);
};

export function findProblem(id) {
   return ProblemsFacade.findProblem(id);
};

export function fetchAllProblems() {
   return ProblemsFacade.fetchAllProblems();
};

export function fetchProblemsBatch(condition, limit) {
   return ProblemsFacade.fetchProblemsBatch(condition, limit);
};

export function deleteProblem(id) {
   ProblemsFacade.deleteProblem(id);
};

export function deleteAllByStatus(status) {
   ProblemsFacade.deleteAllByStatus(status);
};

export function clearAllProblems() {
   ProblemsFacade.clearAllProblems();
};

export function updateStatus(id, status) {
   ProblemsFacade.updateStatus(id, status);
};

export function updateStatusMultiple(ids, status) {
   ProblemsFacade.updateStatusMultiple(ids, status);
};