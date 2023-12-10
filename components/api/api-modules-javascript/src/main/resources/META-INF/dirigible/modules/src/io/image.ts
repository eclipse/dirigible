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
/**
 * API Image
 */
import * as streams from "@dirigible/io/streams";

const ImageFacade = Java.type("org.eclipse.dirigible.components.api.io.ImageFacade");

export class Image{
	resize(original, type, width, height): streams.InputStream {
		const native = ImageFacade.resize(original, type, width, height);
		return new streams.InputStream(native);
	};
}
