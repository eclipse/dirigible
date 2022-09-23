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
package org.eclipse.dirigible.graalium.core.javascript;

import java.nio.file.Path;

import org.eclipse.dirigible.graalium.core.CodeRunner;
import org.graalvm.polyglot.Source;

/**
 * The Interface JavascriptCodeRunner.
 *
 * @param <TSource> the generic type
 * @param <TResult> the generic type
 */
public interface JavascriptCodeRunner<TSource, TResult> extends CodeRunner<TSource, TResult> {

	/**
	 * Prepare the source.
	 *
	 * @param codeFilePath the code file path
	 * @return the source
	 */
	Source prepareSource(Path codeFilePath);

}
