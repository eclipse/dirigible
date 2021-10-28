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
exports.generateQRCode = function(text){
    return org.eclipse.dirigible.api.v3.utils.QRCodeFacade.generateQRCode(text);
};

exports.generateQRCode = function(text, workspaceName, projectName, fileName){
    const EXTENSION      = ".png";
    var workspaceManager = require("platform/v4/workspace");
    var qrCodeByteArray  = org.eclipse.dirigible.api.v3.utils.QRCodeFacade.generateQRCode(text);

    workspaceManager.getWorkspace(workspaceName).getProject(projectName).createFile(fileName + EXTENSION).setContent(qrCodeByteArray);
};