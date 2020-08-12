/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
(function() {
    var generator = require("template-form-builder-angularjs/template/generate-form-angularjs");
    return generator.generate(__context.get('workspaceName'), __context.get('projectName'), __context.get('filePath'));
})();