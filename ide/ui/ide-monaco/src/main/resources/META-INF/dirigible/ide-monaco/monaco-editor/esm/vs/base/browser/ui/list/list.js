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
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
export var ListAriaRootRole;
(function (ListAriaRootRole) {
    /** default tree structure role */
    ListAriaRootRole["TREE"] = "tree";
    /** role='tree' can interfere with screenreaders reading nested elements inside the tree row. Use FORM in that case. */
    ListAriaRootRole["FORM"] = "form";
})(ListAriaRootRole || (ListAriaRootRole = {}));
var ListError = /** @class */ (function (_super) {
    __extends(ListError, _super);
    function ListError(user, message) {
        return _super.call(this, "ListError [" + user + "] " + message) || this;
    }
    return ListError;
}(Error));
export { ListError };
