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
const DataService  = require('http/v4/rs-data').DataService;
var MyDAO = function(){};
MyDAO.prototype.list = function(){
    return [{id:1, text:'b'}];
};
MyDAO.prototype.count = function(){
    return this.list().length;
};
var svc = new DataService(new MyDAO());
svc.service();