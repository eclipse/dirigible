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
 * Utility URL builder
 */
let UriBuilder = function UriBuilder() {
	this.pathSegments = [];
	return this;
}
UriBuilder.prototype.path = function (_pathSegments) {
	if (!Array.isArray(_pathSegments))
		_pathSegments = [_pathSegments];
	_pathSegments = _pathSegments.filter(function (segment) {
		return segment;
	})
		.map(function (segment) {
			if (segment.length) {
				if (segment.charAt(segment.length - 1) === '/')
					segment = segment.substring(0, segment.length - 2);
				segment = encodeURIComponent(segment);
			}
			return segment;
		});
	this.pathSegments = this.pathSegments.concat(_pathSegments);
	return this;
}
UriBuilder.prototype.build = function (isBasePath = true) {
	if (isBasePath) return '/' + this.pathSegments.join('/');
	return this.pathSegments.join('/');
}

/**
 * Workspace Service API delegate
 */
let WorkspaceService = function ($http, $window, workspaceSearchServiceUrl, workspacesServiceUrl, treeCfg) {

	this.$http = $http;
	this.$window = $window;
	this.workspaceSearchServiceUrl = workspaceSearchServiceUrl;
	this.workspacesServiceUrl = workspacesServiceUrl;
	this.typeMapping = treeCfg['types'];
};

WorkspaceService.prototype.search = function (wsResourcePath, term) {
	let url = new UriBuilder().path(this.workspaceSearchServiceUrl.split('/')).path(wsResourcePath.split('/')).build();
	return this.$http.post(url, term)
		.then(function (response) {
			return response.data;
		});
}
WorkspaceService.prototype.listWorkspaceNames = function () {
	let url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).build();
	return this.$http.get(url)
		.then(function (response) {
			return response.data;
		});
}

/**
 * Workspace Tree Adapter mediating the workspace service REST api and the jst tree componet working with it
 */
let WorkspaceTreeAdapter = function (treeConfig, workspaceService, messageHub) {
	this.treeConfig = treeConfig;
	this.workspaceService = workspaceService;
	this.messageHub = messageHub;

	this._buildTreeNode = function (files) {
		let children = files.map(function (f) {
			f.label = f.name;
			return {
				"text": f.path.substring(f.path.indexOf('/', 1)),
				"type": f.type,
				"_file": f
			}
		});
		return children;
	};

	this._fnr = function (node, replacement) {
		if (node.children) {
			let done;
			node.children = node.children.map(function (c) {
				if (!done && c._file.path === replacement._file.path) {
					done = true;
					return replacement;
				}
				return c;
			});
			if (done)
				return true;
			node.children.forEach(function (c) {
				return this._fnr(c, replacement);
			}.bind(this));
		}
		return;
	};
};

WorkspaceTreeAdapter.prototype.init = function (containerEl, workspaceController, scope) {
	this.containerEl = containerEl;
	this.workspaceController = workspaceController;
	this.workspaceName = workspaceController.selectedWorkspace;
	this.searchTerm = workspaceController.searchTerm;
	this.scope = scope;

	let self = this;
	let jstree = this.containerEl.jstree(this.treeConfig);

	//subscribe event listeners
	jstree.on('select_node.jstree', function (e, data) {
		if (data.node.type === 'file') {
			this.clickNode(this.jstree.get_node(data.node));
		}
	}.bind(this))
		.on('dblclick.jstree', function (evt) {
			this.dblClickNode(this.jstree.get_node(evt.target))
		}.bind(this))
		.on('jstree.workspace.openWith', function (e, data, editor) {
			this.openWith(data, editor);
		}.bind(this))
		;

	this.jstree = $.jstree.reference(jstree);
	return this;
};
WorkspaceTreeAdapter.prototype.dblClickNode = function (node) {
	let type = node.original.type;
	let parent = node;
	for (let i = 0; i < node.parents.length - 1; i++) {
		parent = this.jstree.get_node(parent.parent);
	}
	if (parent.original.git)
		node.original._file["gitName"] = parent.original.gitName;
	if (['folder', 'project'].indexOf(type) < 0)
		this.messageHub.announceFileOpen(node.original._file);
}
WorkspaceTreeAdapter.prototype.openWith = function (node, editor) {
	this.messageHub.announceFileOpen(node, editor);
}
WorkspaceTreeAdapter.prototype.clickNode = function (node) {
	this.messageHub.announceFileSelected(node.original._file);
};
WorkspaceTreeAdapter.prototype.raw = function () {
	return this.jstree;
}
WorkspaceTreeAdapter.prototype.refresh = function (node) {
	if (this.searchTerm.length === 0) {
		return;
	}
	return this.workspaceService.search(this.workspaceName, this.searchTerm)
		.then(function (_data) {
			let data = [_data];

			data = data.map(this._buildTreeNode.bind(this));
			this.jstree.settings.core.data = data[0];

			this.jstree.refresh();
		}.bind(this));
};

angular.module('workspace.config', [])
	.constant('WS_SVC_URL', '/services/v4/ide/workspaces')
	.constant('WS_SVC_SEARCH_URL', '/services/v4/ide/workspace-search');

angular.module('workspace', ['workspace.config', 'ideUiCore', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])
	.factory('httpRequestInterceptor', function () {
		let csrfToken = null;
		return {
			request: function (config) {
				config.headers['X-Requested-With'] = 'Fetch';
				config.headers['X-CSRF-Token'] = csrfToken ? csrfToken : 'Fetch';
				return config;
			},
			response: function (response) {
				let token = response.headers()['x-csrf-token'];
				if (token) {
					csrfToken = token;
				}
				return response;
			}
		};
	})
	.config(['$httpProvider', function ($httpProvider) {
		//check if response is error. errors currently are non-json formatted and fail too early
		$httpProvider.interceptors.push('httpRequestInterceptor');
		$httpProvider.defaults.transformResponse.unshift(function (data, headersGetter, status) {
			if (status > 399) {
				data = {
					"error": data
				}
				data = JSON.stringify(data);
			}
			return data;
		});
	}])
	.factory('messageHub', [function () {
		let messageHub = new FramesMessageHub();
		let send = function (evtName, data, absolute) {
			messageHub.post({ data: data }, (absolute ? '' : 'workspace.') + evtName);
		};
		let announceFileSelected = function (fileDescriptor) {
			this.send('file.selected', fileDescriptor);
		};
		let announceFileCreated = function (fileDescriptor) {
			this.send('file.created', fileDescriptor);
		};
		let announceFileOpen = function (fileDescriptor, editor) {
			this.send('file.open', {
				file: fileDescriptor,
				editor: editor
			});
		};

		return {
			send: send,
			announceFileSelected: announceFileSelected,
			announceFileCreated: announceFileCreated,
			announceFileOpen: announceFileOpen,
			on: function (evt, cb) {
				messageHub.subscribe(cb, evt);
			}
		};
	}])
	.factory('$treeConfig.openmenuitem', ['Editors', function (Editors) {
		let OpenMenuItemFactory = function (Editors) {
			let openWithEventName = this.openWithEventName = 'jstree.workspace.openWith';
			let editorsForContentType = Editors.editorsForContentType;

			let getEditorsForContentType = function (contentType) {
				if (Object.keys(editorsForContentType).indexOf(contentType) > -1) {
					return editorsForContentType[contentType];
				} else
					return editorsForContentType[""];
			};

			let onOpenWithEditorAction = function (editor, data) {
				let tree = $.jstree.reference(data.reference);
				let node = tree.get_node(data.reference);
				let parent = node;
				for (let i = 0; i < node.parents.length - 1; i++) {
					parent = tree.get_node(parent.parent);
				}
				if (parent.original.git)
					node.original._file["gitName"] = parent.original.gitName;
				tree.element.trigger(openWithEventName, [node.original._file, editor]);
			};

			let createOpenEditorMenuItem = function (editorId, label) {
				return {
					"label": label || editorId.charAt(0).toUpperCase() + editorId.slice(1),
					"action": onOpenWithEditorAction.bind(this, editorId)
				};
			};

			let createOpenWithSubmenu = function (editors) {
				editorsSubmenu = {};
				if (editors) {
					editors.forEach(function (editor) {
						editorsSubmenu[editor.id] = createOpenEditorMenuItem(editor.id, editor.label);
					}.bind(this));
				}
				return editorsSubmenu;
			};

			/**
			 * Depending on the number of assignable editors for the file content type, this mehtod
			 * will create Open (singular eidtor) or Open with... choice dropdown for multiple editors.
			 */
			this.createOpenFileMenuItem = function (ctxmenu, node) {
				let contentType = node.original._file.contentType || "";
				let editors = getEditorsForContentType(contentType);
				if (!editors) editors = [{ id: Editors.defaultEditorId }];
				if (editors.length > 1) {
					ctxmenu.openWith = {
						"label": "Open with...",
						"submenu": createOpenWithSubmenu.call(this, editors)
					};
				} else {
					ctxmenu.open = createOpenEditorMenuItem(editors[0].id, 'Open');
				}
			}
		};

		let openMenuItemFactory = new OpenMenuItemFactory(Editors);

		return openMenuItemFactory;
	}])
	.factory('$treeConfig', ['$treeConfig.openmenuitem', function (openmenuitem) {

		return {
			'core': {
				'themes': {
					"name": "default",
					"responsive": false,
					"dots": false,
					"icons": true,
					'variant': 'small',
					'stripes': true
				},
				'check_callback': function (o, n, p, i, m) {
					if (m && m.dnd && m.pos !== 'i') { return false; }
					if (o === "move_node" || o === "copy_node") {
						if (this.get_node(n).parent === this.get_node(p).id) { return false; }
					}
					return true;
				}
			},
			'plugins': ['state', 'dnd', 'sort', 'types', 'contextmenu', 'unique'],
			'unique': {
				'newNodeName': function (node, typesConfig) {
					let typeCfg = typesConfig[node.type] || typesConfig['default'];
					let name = typeCfg.default_name || typesConfig['default'].default_name || 'file';
					let tmplName = typeCfg.template_new_name || typesConfig['default'].template_new_name || '{name}{ext}';
					let parameters = {
						'{name}': name,
						'{counter}': '',
						'{ext}': typeCfg.ext || typesConfig['default'].ext || ''
					};
					let regex = new RegExp(Object.keys(parameters).join('|'), 'g');
					let fName = tmplName.replace(regex, function (m) {
						return parameters[m] !== undefined ? parameters[m] : m;
					});
					return fName;
				},
				'duplicate': function (name, counter, node, typesConfig) {
					let typeCfg = typesConfig[node.type] || typesConfig['default'];
					name = typeCfg.default_name || typesConfig['default'].default_name || 'file';
					let tmplName = typeCfg.template_new_name || typesConfig['default'].template_new_name || '{name}{counter}{ext}';
					let parameters = {
						'{name}': name,
						'{counter}': counter,
						'{ext}': typeCfg.ext
					};
					let regex = new RegExp(Object.keys(parameters).join('|'), 'g');
					let fName = tmplName.replace(regex, function (m) {
						return parameters[m] !== undefined ? parameters[m] : m;
					});
					return fName;
				}
			},
			"types": {
				"default": {
					"icon": "fa fa-file-o",
					"default_name": "file",
					"template_new_name": "{name}{counter}"
				},
				'file': {
					"valid_children": []
				},
				'folder': {
					"default_name": "folder",
					'icon': "fa fa-folder-o"
				},
				"project": {
					"icon": "fa fa-pencil-square-o"
				}
			},
			"contextmenu": {
				"items": function (node) {
					let _ctxmenu = $.jstree.defaults.contextmenu.items();
					let ctxmenu = {};
					if (this.get_type(node) === "file") {
						/*Open/Open with...*/
						openmenuitem.createOpenFileMenuItem(ctxmenu, node);
					}

					return ctxmenu;
				}
			}
		}
	}])
	.factory('workspaceService', ['$http', '$window', 'WS_SVC_SEARCH_URL', 'WS_SVC_URL', '$treeConfig', function ($http, $window, WS_SVC_SEARCH_URL, WS_SVC_URL, $treeConfig) {
		return new WorkspaceService($http, $window, WS_SVC_SEARCH_URL, WS_SVC_URL, $treeConfig);
	}])
	.factory('workspaceTreeAdapter', ['$treeConfig', 'workspaceService', 'messageHub', function ($treeConfig, WorkspaceService, messageHub) {
		return new WorkspaceTreeAdapter($treeConfig, WorkspaceService, messageHub);
	}])
	.controller('WorkspaceController', ['workspaceService', 'workspaceTreeAdapter', 'messageHub', '$scope', function (workspaceService, workspaceTreeAdapter, messageHub, $scope) {

		this.wsTree;
		this.workspaces;
		this.selectedWorkspace;
		this.searchTerm = "";

		this.refreshWorkspaces = function () {
			workspaceService.listWorkspaceNames()
				.then(function (workspaceNames) {
					this.workspaces = workspaceNames;
					if (this.workspaceName) {
						this.selectedWorkspace = this.workspaceName;
						this.workspaceSelected()
					} else if (this.workspaces[0]) {
						this.selectedWorkspace = this.workspaces[0];
						this.workspaceSelected()
					}
				}.bind(this));
		};
		this.refreshWorkspaces();

		this.workspaceSelected = function () {
			if (this.wsTree) {
				this.wsTree.workspaceName = this.selectedWorkspace;
				this.wsTree.searchTerm = this.searchTerm;
				this.wsTree.refresh();
				return;
			}
			this.wsTree = workspaceTreeAdapter.init($('.search'), this, $scope);
			if (!workspaceService.typeMapping)
				workspaceService.typeMapping = $treeConfig[types];
			this.wsTree.refresh();
		};

		this.search = function () {
			this.wsTree.searchTerm = this.searchTerm;
			this.wsTree.refresh();
		};

	}]);