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
'use strict';

var DECISION_TABLE_TOOLBAR_CONFIG = {
    "items" : [
        {
            "type" : "button",
            "title" : "TOOLBAR.ACTION.SAVE",
            "cssClass" : "editor-icon editor-icon-save",
            "action" : "DECISION_TABLE_TOOLBAR.ACTIONS.saveModel",
            "disableOnReadonly": true
        }
    ],
    
    "secondaryItems" : [
		{
		    "type" : "button",
		    "title" : "TOOLBAR.ACTION.CLOSE",
		    "cssClass" : "glyphicon glyphicon-remove",
		    "action" : "DECISION_TABLE_TOOLBAR.ACTIONS.closeEditor"
		}
    ]
};