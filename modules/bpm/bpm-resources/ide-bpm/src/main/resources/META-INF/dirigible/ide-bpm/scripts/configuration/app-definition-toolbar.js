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
/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

var APP_DEFINITION_TOOLBAR_CONFIG = {
    "items" : [
        {
            "type" : "button",
            "title" : "APP_DEFINITION_TOOLBAR.ACTION.SAVE",
            "cssClass" : "editor-icon editor-icon-save",
            "action" : "APP_DEFINITION_TOOLBAR.ACTIONS.saveModel"
        }
    ],
    
    "secondaryItems" : [
		{
		    "type" : "button",
		    "title" : "Close",
		    "cssClass" : "glyphicon glyphicon-remove",
		    "action" : "APP_DEFINITION_TOOLBAR.ACTIONS.closeEditor"
		}
    ]
};