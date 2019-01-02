/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
/**
 * Utility URL builder
 */
var UriBuilder = function UriBuilder(){
	this.pathSegments = [];
	return this;
}
UriBuilder.prototype.path = function(_pathSegments){
	if(!Array.isArray(_pathSegments))
		_pathSegments = [_pathSegments];
	_pathSegments = _pathSegments.filter(function(segment){
			return segment;
		})
		.map(function(segment){
			if(segment.length){
				if(segment.charAt(segment.length-1) ==='/')
					segment = segment.substring(0, segment.length-2);
				segment = encodeURIComponent(segment);
			} 
			return segment;
		});
	this.pathSegments = this.pathSegments.concat(_pathSegments);
	return this;
}
UriBuilder.prototype.build = function(){
	var uriPath = '/'+this.pathSegments.join('/');
	return uriPath;
}

/**
 * Workspace Service API delegate
 */
var WorkspaceService = function($http, $window, workspaceSearchServiceUrl, workspacesServiceUrl, treeCfg){

	this.$http = $http;
	this.$window = $window;
	this.workspaceSearchServiceUrl = workspaceSearchServiceUrl;
	this.workspacesServiceUrl = workspacesServiceUrl;
	this.typeMapping = treeCfg['types'];
};


WorkspaceService.prototype.search = function(wsResourcePath, term){
	var url = new UriBuilder().path(this.workspaceSearchServiceUrl.split('/')).path(wsResourcePath.split('/')).build();
	return this.$http.post(url, term)
			.then(function(response){
				return response.data;
			});
}
WorkspaceService.prototype.listWorkspaceNames = function(){
	var url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).build();
	return this.$http.get(url)
			.then(function(response){
				return response.data;
			});
}

/**
 * Workspace Tree Adapter mediating the workspace service REST api and the jst tree componet working with it
 */
var WorkspaceTreeAdapter = function(treeConfig, workspaceService, messageHub){
	this.treeConfig = treeConfig;
	this.workspaceService = workspaceService;
	this.messageHub = messageHub;

	this._buildTreeNode = function(files){
		var children = files.map(function(f) {
			f.label = f.name;
			return {
				"text": f.path.substring(f.path.indexOf('/', 1)),
				"children": children,
				"type": f.type,
				"_file": f
			}
		});
		return children;
	};
	
	this._fnr = function (node, replacement){
		if(node.children){
			var done;
			node.children = node.children.map(function(c){
				if(!done && c._file.path === replacement._file.path){
					done = true;
					return replacement;
				}
				return c;
			});
			if(done)
				return true;
			node.children.forEach(function(c){
				return this._fnr(c, replacement);
			}.bind(this));
		}
		return;
	};
};

WorkspaceTreeAdapter.prototype.init = function(containerEl, workspaceController, scope){
	this.containerEl = containerEl;
	this.workspaceController = workspaceController;
	this.workspaceName = workspaceController.selectedWorkspace;
	this.searchTerm = workspaceController.searchTerm;
	this.scope = scope;
	
	var self = this;
	var jstree = this.containerEl.jstree(this.treeConfig);
	
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
WorkspaceTreeAdapter.prototype.dblClickNode = function(node){
	var type = node.original.type;
	if(['folder','project'].indexOf(type)<0)
		this.messageHub.announceFileOpen(node.original._file);
}
WorkspaceTreeAdapter.prototype.openWith = function(node, editor){
	this.messageHub.announceFileOpen(node, editor);
}
WorkspaceTreeAdapter.prototype.clickNode = function(node){
	var type = node.original.type;
	this.messageHub.announceFileSelected(node.original._file);
};
WorkspaceTreeAdapter.prototype.raw = function(){
	return this.jstree;
}
WorkspaceTreeAdapter.prototype.refresh = function(node){
	if (this.searchTerm.length === 0) {
		return;
	}
	return this.workspaceService.search(this.workspaceName, this.searchTerm)
			.then(function(_data){
				var data = [_data];
				
				data = data.map(this._buildTreeNode.bind(this));
				this.jstree.settings.core.data = data[0];
				
				this.jstree.refresh();
			}.bind(this));
};

angular.module('workspace.config', [])
	.constant('WS_SVC_URL','../../../../services/v3/ide/workspaces')
	.constant('WS_SVC_SEARCH_URL','../../../../services/v3/ide/workspace-search');
	
angular.module('workspace', ['workspace.config', 'ideUiCore', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])
.config(['$httpProvider', function($httpProvider) {
	//check if response is error. errors currently are non-json formatted and fail too early
	$httpProvider.defaults.transformResponse.unshift(function(data, headersGetter, status){
		if(status>399){
			data = {
				"error": data
			}
			data = JSON.stringify(data);
		}
		return data;
	});
}])
.factory('messageHub', [function(){
	var messageHub = new FramesMessageHub();	
	var send = function(evtName, data, absolute){
		messageHub.post({data: data}, (absolute ? '' : 'workspace.') + evtName);
	};
	var announceFileSelected = function(fileDescriptor){
		this.send('file.selected', fileDescriptor);
	};
	var announceFileCreated = function(fileDescriptor){
		this.send('file.created', fileDescriptor);
	};
	var announceFileOpen = function(fileDescriptor, editor){
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
		on: function(evt, cb){
			messageHub.subscribe(cb, evt);
		}
	};
}])
.factory('$treeConfig.openmenuitem', ['Editors', function(Editors){
	var OpenMenuItemFactory = function(Editors){
		var openWithEventName = this.openWithEventName = 'jstree.workspace.openWith';
		var editorsForContentType = Editors.editorsForContentType;
		
		var getEditorsForContentType = function(contentType){
			if(Object.keys(editorsForContentType).indexOf(contentType) > -1){
				return editorsForContentType[contentType];
			} else 
				return editorsForContentType[""];
		};
		
		var onOpenWithEditorAction = function (editor, data) {
			var tree = $.jstree.reference(data.reference);
			var node = tree.get_node(data.reference);
			tree.element.trigger(openWithEventName, [node.original._file, editor]);
		};
		
		var createOpenEditorMenuItem = function(editorId, label){
			return {
				"label": label || editorId.charAt(0).toUpperCase() + editorId.slice(1),
				"action": onOpenWithEditorAction.bind(this, editorId)
			};
		};
		
		var createOpenWithSubmenu = function(contentType){
			editorsSubmenu = {};
			var editors = getEditorsForContentType(contentType);
			if(editors){
				editors.forEach(function(editorId){
					editorsSubmenu[editorId] = createOpenEditorMenuItem(editorId);
				}.bind(this));
			}
			return editorsSubmenu;
		};
		
		/**
		 * Depending on the number of assignable editors for the file content type, this mehtod
		 * will create Open (singular eidtor) or Open with... choice dropdown for multiple editors.
		 */
		this.createOpenFileMenuItem = function(ctxmenu, node){
			var contentType = node.original._file.contentType;
			var editors = getEditorsForContentType(contentType || "");
			if(editors.length > 1){
				ctxmenu.openWith =  {
					"label": "Open with...",
					"submenu": createOpenWithSubmenu.call(this, contentType)
				};			
			} else {
				ctxmenu.open = createOpenEditorMenuItem(editors[0], 'Open');
			}
		}		
	};
	
	var openMenuItemFactory = new OpenMenuItemFactory(Editors);
	
	return openMenuItemFactory;
}])
.factory('$treeConfig', ['$treeConfig.openmenuitem', function(openmenuitem){
	
	return {
		'core' : {
			'themes': {
				"name": "default",
				"responsive": false,
				"dots": false,
				"icons": true,
				'variant' : 'small',
				'stripes' : true
			},
			'check_callback' : function(o, n, p, i, m) {
				if(m && m.dnd && m.pos !== 'i') { return false; }
				if(o === "move_node" || o === "copy_node") {
					if(this.get_node(n).parent === this.get_node(p).id) { return false; }
				}
				return true;
			}
		},
		'plugins': ['state','dnd','sort','types','contextmenu','unique'],
		'unique': {
			'newNodeName': function(node, typesConfig){
				var typeCfg = typesConfig[node.type] || typesConfig['default'];
				var name = typeCfg.default_name || typesConfig['default'].default_name || 'file';
				var tmplName = typeCfg.template_new_name || typesConfig['default'].template_new_name || '{name}{ext}';
				var parameters = {
					'{name}': name,
					'{counter}': '',
					'{ext}': typeCfg.ext || typesConfig['default'].ext || ''
				};
				var regex = new RegExp(Object.keys(parameters).join('|'), 'g');
				var fName = tmplName.replace(regex, function(m) {
					return parameters[m]!==undefined?parameters[m]:m;
				});
				return fName;
			},
			'duplicate' : function (name, counter, node, typesConfig) {
				var typeCfg = typesConfig[node.type] || typesConfig['default'];
				var name = typeCfg.default_name || typesConfig['default'].default_name || 'file';
				var tmplName = typeCfg.template_new_name || typesConfig['default'].template_new_name || '{name}{counter}{ext}';
				var parameters = {
					'{name}': name,
					'{counter}': counter,
					'{ext}': typeCfg.ext
				};
				var regex = new RegExp(Object.keys(parameters).join('|'), 'g');
				var fName = tmplName.replace(regex, function(m) {
					return parameters[m]!==undefined?parameters[m]:m;
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
			"items" : function(node) {
				var _ctxmenu = $.jstree.defaults.contextmenu.items();
				var ctxmenu = {};
				if(this.get_type(node) === "file") {
					/*Open/Open with...*/
					openmenuitem.createOpenFileMenuItem(ctxmenu, node);
				}

				return ctxmenu;
			}
		}
	}
}])
.factory('workspaceService', ['$http', '$window', 'WS_SVC_SEARCH_URL', 'WS_SVC_URL', '$treeConfig', function($http, $window, WS_SVC_SEARCH_URL, WS_SVC_URL, $treeConfig){
	return new WorkspaceService($http, $window, WS_SVC_SEARCH_URL, WS_SVC_URL, $treeConfig);
}])
.factory('workspaceTreeAdapter', ['$treeConfig', 'workspaceService','messageHub', function($treeConfig, WorkspaceService, messageHub){
	return new WorkspaceTreeAdapter($treeConfig, WorkspaceService, messageHub);
}])
.controller('WorkspaceController', ['workspaceService', 'workspaceTreeAdapter', 'messageHub', '$scope', function (workspaceService, workspaceTreeAdapter, messageHub, $scope) {

	this.wsTree;
	this.workspaces;
	this.selectedWorkspace;
	this.searchTerm = "";

	this.refreshWorkspaces = function() {
		workspaceService.listWorkspaceNames()
			.then(function(workspaceNames) {
				this.workspaces = workspaceNames;
				if(this.workspaceName) {
					this.selectedWorkspace = this.workspaceName;
					this.workspaceSelected()
				} else if(this.workspaces[0]) {
					this.selectedWorkspace = this.workspaces[0];
					this.workspaceSelected()					
				} 
			}.bind(this));
	};
	this.refreshWorkspaces();
	
	this.workspaceSelected = function(){
		if (this.wsTree) {
			this.wsTree.workspaceName = this.selectedWorkspace;
			this.wsTree.searchTerm = this.searchTerm;
			this.wsTree.refresh();
			return;
		}
		this.wsTree = workspaceTreeAdapter.init($('.search'), this, $scope);
		if(!workspaceService.typeMapping)
			workspaceService.typeMapping = $treeConfig[types];
		this.wsTree.refresh();
	};
	
	this.search = function(){
		this.wsTree.searchTerm = this.searchTerm;
		this.wsTree.refresh();
	};
	
	messageHub.on('workbench.theme.changed', function(msg){
		var themeUrl = msg.data;
		
		$('a[href="../../../../services/v3/core/theme/jstree.css"]').remove();
		$('<link id="theme-stylesheet" href="../../../../services/v3/core/theme/jstree.css" rel="stylesheet" />').appendTo('head');
		
		$('a[href="../../../../services/v3/core/theme/ide.css"]').remove();
		$('<link href="../../../../services/v3/core/theme/ide.css" rel="stylesheet" />').appendTo('head');
		
		$('#theme-stylesheet').remove();
		$('<link id="theme-stylesheet" href="'+themeUrl +'" rel="stylesheet" />').appendTo('head');
	}.bind(this));

}]);
