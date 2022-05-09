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
/**
 * Class: mxHierarchicalLayoutStage
 * 
 * The specific layout interface for hierarchical layouts. It adds a
 * <code>run</code> method with a parameter for the hierarchical layout model
 * that is shared between the layout stages.
 * 
 * Constructor: mxHierarchicalLayoutStage
 *
 * Constructs a new hierarchical layout stage.
 */
function mxHierarchicalLayoutStage() { };

/**
 * Function: execute
 * 
 * Takes the graph detail and configuration information within the facade
 * and creates the resulting laid out graph within that facade for further
 * use.
 */
mxHierarchicalLayoutStage.prototype.execute = function(parent) { };
