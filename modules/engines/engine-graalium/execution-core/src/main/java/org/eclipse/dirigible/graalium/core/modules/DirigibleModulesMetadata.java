/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.graalium.core.modules;

import java.util.List;

/**
 * The Class DirigibleModulesMetadata.
 */
public class DirigibleModulesMetadata {

  /** The Constant PURE_ESM_MODULES. */
  private static final List<String> PURE_ESM_MODULES =
      List.of("@dirigible/http", "@dirigible/io", "@dirigible/bpm", "@dirigible/cms", "@dirigible/core", "@dirigible/db", "@dirigible/etcd",
          "@dirigible/extensions", "@dirigible/git", "@dirigible/indexing", "@dirigible/job", "@dirigible/kafka", "@dirigible/log",
          "@dirigible/mail", "@dirigible/messaging", "@dirigible/mongodb", "@dirigible/net", "@dirigible/pdf", "@dirigible/platform",
          "@dirigible/qldb", "@dirigible/rabbitmq", "@dirigible/redis", "@dirigible/user", "@dirigible/template", "@dirigible/utils");

  /**
   * Checks if is pure esm module.
   *
   * @param module the module
   * @return true, if is pure esm module
   */
  static boolean isPureEsmModule(String module) {
    return PURE_ESM_MODULES.stream()
                           .anyMatch(module::startsWith);
  }
}
