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
exports.getSources = function (parameters) {
	return [
		// Location: "gen/index.html"
		{
			location: "/template-application-ui-angular/index.html",
			action: "generate",
			rename: "gen/index.html"
		},
		// Location: "gen/ui/core/launchpad/Home"
		{
			location: "/template-application-ui-angular/ui/core/launchpad/Home/controller.js",
			action: "generate",
			rename: "gen/ui/core/launchpad/Home/controller.js"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/Home/index.html",
			action: "generate",
			rename: "gen/ui/core/launchpad/Home/index.html",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/Home/view.extension",
			action: "generate",
			rename: "gen/ui/core/launchpad/Home/view.extension",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/Home/view.js",
			action: "generate",
			rename: "gen/ui/core/launchpad/Home/view.js",
			engine: "velocity"
		},
		// Location: "gen/ui/core/launchpad"
		{
			location: "/template-application-ui-angular/ui/core/launchpad/dialog-window.extensionpoint",
			action: "generate",
			rename: "gen/ui/core/launchpad/dialog-window.extensionpoint",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/menu-help.extension",
			action: "generate",
			rename: "gen/ui/core/launchpad/menu-help.extension",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/menu-help.js",
			action: "copy",
			rename: "gen/ui/core/launchpad/menu-help.js"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/menu.extensionpoint",
			action: "generate",
			rename: "gen/ui/core/launchpad/menu.extensionpoint",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/perspective.extension",
			action: "generate",
			rename: "gen/ui/core/launchpad/perspective.extension",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/perspective.extensionpoint",
			action: "generate",
			rename: "gen/ui/core/launchpad/perspective.extensionpoint",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/perspective.js",
			action: "generate",
			rename: "gen/ui/core/launchpad/perspective.js",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/tile.extensionpoint",
			action: "generate",
			rename: "gen/ui/core/launchpad/tile.extensionpoint",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/launchpad/view.extensionpoint",
			action: "generate",
			rename: "gen/ui/core/launchpad/view.extensionpoint",
			engine: "velocity"
		},
		// Location: "gen/ui/core/modules/templates"
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/accordionPane.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/accordionPane.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/contextmenu.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/contextmenu.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/contextmenuSubmenu.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/contextmenuSubmenu.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/headerHamburgerMenu.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/headerHamburgerMenu.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/headerMenu.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/headerMenu.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/headerSubmenu.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/headerSubmenu.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/ideDialogs.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/ideDialogs.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/ideHeader.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/ideHeader.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/ideSidebar.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/ideSidebar.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/ideStatusBar.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/ideStatusBar.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/layout.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/layout.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/splittedTabs.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/splittedTabs.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/tabs.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/tabs.html"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/templates/toolbar.html",
			action: "copy",
			rename: "gen/ui/core/modules/templates/toolbar.html"
		},
		// Location: "gen/ui/core/modules"
		{
			location: "/template-application-ui-angular/ui/core/modules/core-modules.js",
			action: "generate",
			rename: "gen/ui/core/modules/core-modules.js",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/entityApi.js",
			action: "copy",
			rename: "gen/ui/core/modules/entityApi.js"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/layout.js",
			action: "generate",
			rename: "gen/ui/core/modules/layout.js",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/theming.js",
			action: "copy",
			rename: "gen/ui/core/modules/theming.js"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/view.js",
			action: "generate",
			rename: "gen/ui/core/modules/view.js",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/modules/widgets.js",
			action: "copy",
			rename: "gen/ui/core/modules/widgets.js"
		},
		// Location: "gen/ui/core/services"
		{
			location: "/template-application-ui-angular/ui/core/services/dialog-windows.js",
			action: "generate",
			rename: "gen/ui/core/services/dialog-windows.js",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/services/loader.js",
			action: "generate",
			rename: "gen/ui/core/services/loader.js",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/services/menu.js",
			action: "copy",
			rename: "gen/ui/core/services/menu.js"
		},
		{
			location: "/template-application-ui-angular/ui/core/services/perspectives.js",
			action: "generate",
			rename: "gen/ui/core/services/perspectives.js",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/services/tiles.js",
			action: "generate",
			rename: "gen/ui/core/services/tiles.js",
			engine: "velocity"
		},
		{
			location: "/template-application-ui-angular/ui/core/services/user-name.js",
			action: "copy",
			rename: "gen/ui/core/services/user-name.js"
		},
		{
			location: "/template-application-ui-angular/ui/core/services/views.js",
			action: "generate",
			rename: "gen/ui/core/services/views.js",
			engine: "velocity"
		},
		// Location: "gen/ui/core"
		{
			location: "/template-application-ui-angular/ui/core/ide-message-hub.js",
			action: "copy",
			rename: "gen/ui/core/ide-message-hub.js"
		},
		{
			location: "/template-application-ui-angular/ui/core/message-hub.js",
			action: "copy",
			rename: "gen/ui/core/message-hub.js"
		},
		{
			location: "/template-application-ui-angular/ui/core/uri-builder.js",
			action: "copy",
			rename: "gen/ui/core/uri-builder.js"
		}];
};
