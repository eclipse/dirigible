/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.log;

import java.util.Optional;

/**
 * Turns an error a script hands to the log into a {@link Throwable} carrying the script's stack
 * trace, so it is logged like a Java exception.
 *
 * <p>
 * Looked up with {@link java.util.ServiceLoader}, because {@link LogFacade} is called statically,
 * from scripts and from the Java SDK alike. The JavaScript engine provides the implementation;
 * without it no script can hand the log an error in the first place.
 */
public interface ScriptErrorExtractor {

    /**
     * Extracts the error.
     *
     * @param scriptError the error object the script passed
     * @return the error as a throwable, or empty when the object is not an error of this engine
     */
    Optional<Throwable> extract(Object scriptError);
}
