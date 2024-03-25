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

export class Problems{
   public static save(location: string, type: string, line: string, column: string, 
      cause: string, expected: string, category: string, module: string, source: string, program: string): void {
      ProblemsFacade.save(location, type, line, column, cause, expected,
         category, module, source, program);
   };

   public static findProblem(id: number): string {
      return ProblemsFacade.findProblem(id);
   };

   public static fetchAllProblems(): string {
      return ProblemsFacade.fetchAllProblems();
   };

   public static fetchProblemsBatch(condition: string, limit: number): string {
      return ProblemsFacade.fetchProblemsBatch(condition, limit);
   };

   public static deleteProblem(id: number): void {
      ProblemsFacade.deleteProblem(id);
   };

   public static deleteAllByStatus(status: string): void {
      ProblemsFacade.deleteAllByStatus(status);
   };

   public static clearAllProblems(): void {
      ProblemsFacade.clearAllProblems();
   };

   public static updateStatus(id: number, status: string): void {
      ProblemsFacade.updateStatus(id, status);
   };

   public static updateStatusMultiple(ids: any, status: string): void {
      ProblemsFacade.updateStatusMultiple(ids, status);
   };
}