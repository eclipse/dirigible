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
sap.ui.define([
	'sap/ui/core/mvc/Controller',
	'sap/m/MessageToast',
	"sap/ui/model/json/JSONModel"
], function (Controller, MessageToast, JSONModel) {
	"use strict";

	var PageController = Controller.extend("sap.m.sample.GenericTileLineMode.controller.Page", {
		onInit: function () {
			var oModel = new JSONModel(sap.ui.require.toUrl("sap/m/sample/GenericTileLineMode/tiles.json"));
			this.getView().setModel(oModel);
		},

		changeEnforceSmall: function (oEvent) {
			var oSwitch = oEvent.getSource();
			this.getView().getModel().setProperty("/sizeBehavior", oSwitch.getState() ? "Small" : "Responsive");
		},

		press: function (evt) {
			var oTile = evt.getSource(),
				sTileName = oTile.getHeader() || oTile.getTooltip();

			if (evt.getParameter("action") === "Remove") {
				MessageToast.show("Remove action of GenericTile \"" + sTileName + "\" has been pressed.");
			} else {
				MessageToast.show("The GenericTile \"" + sTileName + "\" has been pressed.");
			}
		},

		pressSlideTile: function (evt) {
			var oTile = evt.getSource();

			if (evt.getParameter("action") === "Remove") {
				MessageToast.show("Remove action of SlideTile \"" + oTile.getTooltip() + "\" has been pressed.");
			} else {
				MessageToast.show("The SlideTile \"" + oTile.getTooltip() + "\" has been pressed.");
			}
		}
	});

	return PageController;
});
