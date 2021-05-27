/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * Utility URL builder
 */
var UriBuilder = function UriBuilder(){
	this.pathSegments = [];
	return this;
};
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
};
UriBuilder.prototype.build = function(){
	return this.pathSegments.join('/');
};

/**
 * Workspace Service API delegate
 */
var WorkspaceService = function($http, $window, workspaceManagerServiceUrl, workspacesServiceUrl, treeCfg){

	this.$http = $http;
	this.$window = $window;
	this.workspaceManagerServiceUrl = workspaceManagerServiceUrl;
	this.workspacesServiceUrl = workspacesServiceUrl;
	this.typeMapping = treeCfg['types'];
	
	this.newFileName = function(name, type, siblingFilenames){
		type = type || 'default';
		//check for custom new file name template in the global configuration
		if(type && this.typeMapping[type] && this.typeMapping[type].template_new_name){
			var nameIncrementRegex = this.typeMapping[type].name_increment_regex;
			siblingFilenames = siblingFilenames || [];
			var suffix = nextIncrementSegment(siblingFilenames, name, nameIncrementRegex);
			suffix = suffix < 0 ? " " : suffix;
			var parameters = {
				"{name}": name || 'file',
				"{ext}": this.typeMapping[type].ext,
				"{increment}" : "-"+suffix
			};
			var tmpl = this.typeMapping[type].template_new_name;
			var regex = new RegExp(Object.keys(parameters).join('|'), 'g');
			var fName = tmpl.replace(regex, function(m) {
				return parameters[m]!==undefined?parameters[m]:m;
			});
			name = fName.trim();
		} 
		return name;
	};
			
	var startsWith = function (stringToTest, prefixToTest){
		var startsWithRegEx = new RegExp('^'+prefixToTest);
		var matches = stringToTest.match(startsWithRegEx);
		return matches != null && matches.length > 0;
	};
	
	var strictInt = function(value) {
	  if (/^(\-|\+)?([0-9]+|Infinity)$/.test(value))
		return Number(value);
	  return NaN;
	};
	
	var toInt = function(value){
		if(value ===undefined)
			return;
		var _result = value.trim();
		_result = strictInt(_result);
		if(isNaN(_result))
			_result = undefined;
		return _result;
	};
	
	//processes an array of sibling string filenames to calculate the next incrmeent suffix segment
	var nextIncrementSegment = function(filenames, filenameToMatch, nameIncrementRegex){
		var maxIncrement = filenames.map(function(siblingFilename){
			//find out incremented file name matches (such as {file-name} {i}.{extension} or {file-name}-{i}.{extension})
			var incr = -2;
			//in case we have a regex configured to find out the increment direclty, use it
			if(nameIncrementRegex){
				var regex = new Regex(nameIncrementRegex);
				var result = siblingFilename.match(regex);
				if(result!==null){
					incr = toInt(result[0]);
				}
			} else {
				//try heuristics
				var regex = /(.*?)(\.[^.]*$|$)/;
				var siblingTextSegments = siblingFilename.match(regex);//matches filename and extension segments of a filename
				var siblingTextFileName = siblingTextSegments[1];
				var siblingTextExtension = siblingTextSegments[2];
				var nodeTextSegments = filenameToMatch.match(regex);
				var nodeTextFileName = nodeTextSegments[1];
				var nodeTextExtension = nodeTextSegments[2];
				if(siblingTextExtension === nodeTextExtension){
					if(siblingTextFileName === nodeTextFileName)
						return -1;
					if(startsWith(siblingTextFileName, nodeTextFileName)){
						//try to figure out the increment segment from the name part. Starting backwards, exepcts that the increment is the last numeric segment in the name
						var _inc = "";
						for(var i=siblingTextFileName.length-1; i>-1; i-- ){
							var code = siblingTextFileName.charCodeAt(i);
							if(code<48 || code>57)//decimal numbers only
								break;
							_inc = siblingTextFileName[i] +_inc;
						}
						if(_inc){
							incr = toInt(_inc);
						}	
					}
				}
			}		
			return incr;
		}).sort(function(a, b){
			return a - b;
		}).pop();
		return ++maxIncrement;
	};
};

WorkspaceService.prototype.createFolder = function(type){
	var inst = $.jstree.reference(data.reference),
		obj = inst.get_node(data.reference);
	var node_tmpl = {
		type: 'folder',
		text: this.newFileName('folder', 'folder')
	};
	inst.create_node(obj, node_tmpl, "last", function (new_node) {
		setTimeout(function () { inst.edit(new_node); },0);
	});
};

WorkspaceService.prototype.createFile = function (name, path, node) {
	var isDirectory = node.type === 'folder';
	var url = new UriBuilder().path((this.workspacesServiceUrl + path).split('/')).path(name).build();
	if (isDirectory)
		url += "/";
	if (!node.data)
		node.data = '';
	return this.$http.post(url, JSON.stringify(node.data), { headers: { 'Dirigible-Editor': 'Workspace' } })
		.then(function (response) {
			var filePath = response.headers('location');
			filePath = filePath.substring(filePath.indexOf("/services"))
			return this.$http.get(filePath, { headers: { 'describe': 'application/json' } })
				.then(function (response) { return response.data; });
		}.bind(this))
		.catch(function (response) {
			var msg;
			if (response.data && response.data.error)
				msg = response.data.error;
			else
				msg = response.data || response.statusText || 'Unspecified server error. HTTP Code [' + response.status + ']';
			throw msg;
		});
};
WorkspaceService.prototype.uploadFile = function(name, path, node){
	var isDirectory = node.type === 'folder';
	var url = new UriBuilder().path((this.workspacesServiceUrl+path).split('/')).path(name).build();
	if(isDirectory)
		url+="/";
	if (!node.data)
		node.data = '';
	var req = {
		method: 'POST',
		url: url,
		headers: {
		   'Content-Type': 'application/octet-stream',
		   'Content-Transfer-Encoding': 'base64'
		},
		data: JSON.stringify(btoa(node.data))
	};
	return this.$http(req)
			.then(function(response){
				var filePath = response.headers('location');
				filePath = filePath.substring(filePath.indexOf("/services"));
				return this.$http.get(filePath, {headers: { 'describe': 'application/json'}})
					.then(function(response){ return response.data;});
			}.bind(this))
			.catch(function(response) {
				var msg;
				if(response.data && response.data.error)
					msg = response.data.error;
				else 
					msg = response.data || response.statusText || 'Unspecified server error. HTTP Code ['+response.status+']';
				throw msg;
			});	
};
WorkspaceService.prototype.remove = function(filepath){
	var url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(filepath.split('/')).build();
	return this.$http['delete'](url,{ headers: { 'Dirigible-Editor': 'Workspace' } });
};
WorkspaceService.prototype.rename = function(oldName, newName, path){
	var pathSegments = path.split('/');
	if(pathSegments.length > 2){
		var workspaceName = path.split('/')[1];
		var url = new UriBuilder().path(this.workspaceManagerServiceUrl.split('/')).path(workspaceName).path('rename').build();
		var parent = pathSegments.slice(2, -1);
		var sourcepath = new UriBuilder().path(parent).path(oldName).build();
		var targetpath = new UriBuilder().path(parent).path(newName).build();
		return this.$http.post(url, {
					source: sourcepath,
					target: targetpath
				})
				.then(function(response){
					return response.data;
				});
	}
};
WorkspaceService.prototype.move = function(filename, sourcepath, targetpath, workspaceName){
	var url = new UriBuilder().path(this.workspaceManagerServiceUrl.split('/')).path(workspaceName).path('move').build();
	//NOTE: The third argument is a temporary fix for the REST API issue that sending header  content-type: 'application/json' fails the move operation
	return this.$http.post(url, { 
		source: sourcepath + '/' + filename,
		target: targetpath + '/' + filename,
	});
};
WorkspaceService.prototype.copy = function(filename, sourcepath, targetpath, workspaceName){
	var url = new UriBuilder().path(this.workspaceManagerServiceUrl.split('/')).path(workspaceName).path('copy').build();
	//NOTE: The third argument is a temporary fix for the REST API issue that sending header  content-type: 'application/json' fails the move operation
	return this.$http.post(url, { 
		//source: sourcepath + '/' + filename,
		source: sourcepath,
		target: targetpath + '/',
	});
};
WorkspaceService.prototype.load = function(wsResourcePath){
	var url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(wsResourcePath.split('/')).build();
	return this.$http.get(url, {headers: { 'describe': 'application/json'}})
			.then(function(response){
				return response.data;
			});
};
WorkspaceService.prototype.listWorkspaceNames = function(){
	var url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).build();
	return this.$http.get(url)
			.then(function(response){
				return response.data;
			});
};
WorkspaceService.prototype.createWorkspace = function(workspace){
	var url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(workspace).build();
	return this.$http.post(url, {})
			.then(function(response){
				return response.data;
			});
};
WorkspaceService.prototype.createProject = function(workspace, project, wsTree){
	var url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(workspace).path(project).build();
	return this.$http.post(url, {})
			.then(function(response){
				wsTree.refresh();
				return response.data;
			});
};
WorkspaceService.prototype.linkProject = function(workspace, project, path, wsTree){
	var url = new UriBuilder().path(this.workspaceManagerServiceUrl.split('/')).path(workspace).path('linkProject').build();
	return this.$http.post(url, { 
		source: project,
		target: path
	})
	.then(function(response){
		wsTree.refresh();
		return response.data;
	});
};

/**
 * Workspace Tree Adapter mediating the workspace service REST api and the jst tree componet working with it
 */
var WorkspaceTreeAdapter = function(treeConfig, workspaceService, publishService, exportService, messageHub){
	this.treeConfig = treeConfig;
	this.workspaceService = workspaceService;
	this.publishService = publishService;
	this.exportService = exportService;
	this.messageHub = messageHub;

	this._buildTreeNode = function(f){
		var children = [];
		if(f.type=='folder' || f.type=='project'){
			children = f.folders.map(this._buildTreeNode.bind(this));
			var _files = f.files.map(this._buildTreeNode.bind(this));
			children = children.concat(_files);
		}
		var icon = getIcon(f);
		
		f.label = f.name;
		return {
			"text": f.name,
			"children": children,
			"type": f.type,
			"git": f.git,
			"icon": icon,
			"_file": f
		};
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
	this.scope = scope;
	this.copy_node = null;
	
	var self = this;
	var jstree = this.containerEl.jstree(this.treeConfig);
	
	//subscribe event listeners
	jstree.on('select_node.jstree', function (e, data) {
		if (data.node.type === 'file') {
			this.clickNode(this.jstree.get_node(data.node));
		}
	}.bind(this))
	.on('dblclick.jstree', function (evt) {
		this.dblClickNode(this.jstree.get_node(evt.target));
	}.bind(this))
	.on('open_node.jstree', function(evt, data) {
		if (data.node.type !== 'project') {
			data.instance.set_icon(data.node, 'fa fa-folder-open-o');
		}
//		else {
//			data.instance.set_icon(data.node, 'fa fa-folder-open');
//		}
	})
	.on('close_node.jstree', function(evt, data) {
		if (data.node.type !== 'project') {
			data.instance.set_icon(data.node, 'fa fa-folder-o');
		}
//		else {
//			data.instance.set_icon(data.node, 'fa fa-folder');
//		}
			
	})
	.on('delete_node.jstree', function (e, data) {
		this.deleteNode(data.node);
	}.bind(this))
	.on('create_node.jstree', function (e, data) {
		data.node.name = data.node.text;
		data.node.icon = getIcon(data.node);
	})
	.on('rename_node.jstree', function (e, data) {
		if(data.old !== data.text || !data.node.original._file){
			this.renameNode(data.node, data.old, data.text);
		}
	}.bind(this))
	.on('move_node.jstree', function (e, data) {
		var node = data.node;
		var oldParentNode = data.instance.get_node(data.old_parent);
		this.moveNode(oldParentNode, node);
	}.bind(this))
	.on('copy_node.jstree', function (e, data) {
		var node = data.node;
		var oldParentNode = data.instance.get_node(data.old_parent);
		this.copyNode(oldParentNode, node);
	}.bind(this))
	.on('jstree.workspace.publish', function (e, data) {		
		this.publish(data);
	}.bind(this))
	.on('jstree.workspace.unpublish', function (e, data) {		
		this.unpublish(data);
	}.bind(this))
	.on('jstree.workspace.export', function (e, data) {		
		this.exportProject(data);
	}.bind(this))
	.on('jstree.workspace.generate', function (e, data) {		
		this.generateFile(data, scope);
	}.bind(this))
	.on('jstree.workspace.upload', function (e, data) {		
		this.uploadFileInPlace(data, scope);
	}.bind(this))
	.on('jstree.workspace.openWith', function (e, data, editor) {		
		this.openWith(data, editor);
	}.bind(this))
	.on('jstree.workspace.copy', function (e, data) {
		this.copy(data);
	}.bind(this))
	.on('jstree.workspace.paste', function (e, data) {
		this.paste(data);
	}.bind(this))
//	.on('jstree.workspace.file.properties', function (e, data) {
//	 	var url = data.path + '/' + data.name;
// 		this.openNodeProperties(url);
// 	}.bind(this))
	;
	
	this.jstree = $.jstree.reference(jstree);	
	return this;
};
WorkspaceTreeAdapter.prototype.createNode = function(parentNode, type, defaultName){
	if(type===undefined)
		type = 'file';
	var siblingIds = parentNode.children || [];
	var filenames = siblingIds.map(function (id) {
		if(this.jstree.get_node(id).original.type !== type)
			return;
		return this.jstree.get_node(id).text;
	}.bind(this))
	.filter(function(siblingFName){
		return siblingFName!==undefined;
	});

	if(!defaultName){
		defaultName = type === 'folder'?'folder':'file';
	}

	var node_tmpl = {
		type: type,
		text: this.workspaceService.newFileName(defaultName, type, filenames)
	};
	
	var ctxPath = parentNode.original._file.path;
	
	var self = this;
	this.jstree.create_node(parentNode, node_tmpl, "last", 
		function (new_node) {
			var name = node_tmpl.text;
			self.jstree.edit(new_node); 
		});
};
WorkspaceTreeAdapter.prototype.deleteNode = function(node){
	if(node.original && node.original._file){
		var path = node.original._file.path;
		var self = this;
		return this.workspaceService.remove.apply(this.workspaceService, [path])
				.then(function(){
					self.messageHub.announceFileDeleted(node.original._file);
				})
				.finally(function () {
					self.refresh();
				});	
	}
};
WorkspaceTreeAdapter.prototype.renameNode = function(node, oldName, newName){
	var fpath;
	if(!node.original._file){
		var parentNode = this.jstree.get_node(node.parent);
		var fpath = parentNode.original._file.path;
		this.workspaceService.createFile.apply(this.workspaceService, [newName, fpath, node])
			.then(function(f){
				node.original._file = f;
				node.original._file.label = node.original._file.name;
				this.messageHub.announceFileCreated(f);
			}.bind(this))
			.catch(function(node, err){
				//this.jstree.delete_node(node);
				this.refresh();
				throw err;
			}.bind(this, node))
			.finally(function() {
				this.refresh();
			}.bind(this));
	} else {
		this.workspaceService.rename.apply(this.workspaceService, [oldName, newName, node.original._file.path])
			.then(function(data){
				this.messageHub.announceFileRenamed(node.original._file, oldName, newName);
				//this.jstree.reference(node).select_node(node);
			}.bind(this))
			.finally(function() {
				this.refresh();
			}.bind(this));
	}
};
WorkspaceTreeAdapter.prototype.moveNode = function(sourceParentNode, node){
	//strip the "/{workspace}" segment from paths and the file segment from source path (for consistency) 
	var sourceParentNode = sourceParentNode;
	var sourcepath = sourceParentNode.original._file.path.substring(this.workspaceName.length+1);
	var tagetParentNode = this.jstree.get_node(node.parent);
	var targetpath = tagetParentNode.original._file.path.substring(this.workspaceName.length+1);
	var self = this;
	return this.workspaceService.move(node.text, sourcepath, targetpath, this.workspaceName)
			.then(function(sourceParentNode, tagetParentNode){
				self.refresh(sourceParentNode, true);
				self.refresh(tagetParentNode, true).then(function(){
					self.messageHub.announceFileMoved(targetpath+'/'+node.text, sourcepath, targetpath);
				});
			}.bind(this, sourceParentNode, tagetParentNode))
			.finally(function() {
				this.refresh();
			}.bind(this));
};
WorkspaceTreeAdapter.prototype.copyNode = function(sourceParentNode, node){
	//strip the "/{workspace}" segment from paths and the file segment from source path (for consistency) 
	var sourceParentNode = sourceParentNode;
	var sourcepath = sourceParentNode.original._file.path.substring(this.workspaceName.length+1);
	//var tagetParentNode = this.jstree.get_node(node.parent);
	var targetpath = node.original._file.path.substring(this.workspaceName.length+1);
	var self = this;
	return this.workspaceService.copy(node.text, sourcepath, targetpath, this.workspaceName)
			.then(function(sourceParentNode, tagetParentNode){
				self.refresh(sourceParentNode, true);
				self.refresh(node, true).then(function(){
					self.messageHub.announceFileCopied(targetpath+'/'+node.text, sourcepath, targetpath);
				});
			}.bind(this, sourceParentNode, node))
			.finally(function() {
				this.refresh();
			}.bind(this));
};
WorkspaceTreeAdapter.prototype.dblClickNode = function(node){
	var type = node.original.type;
	if(['folder','project'].indexOf(type)<0)
		this.messageHub.announceFileOpen(node.original._file);
};
WorkspaceTreeAdapter.prototype.openWith = function(node, editor){
	this.messageHub.announceFileOpen(node, editor);
};
WorkspaceTreeAdapter.prototype.clickNode = function(node){
	//var type = node.original.type;
	this.messageHub.announceFileSelected(node.original._file);
};
WorkspaceTreeAdapter.prototype.raw = function(){
	return this.jstree;
};
WorkspaceTreeAdapter.prototype.copy = function(node){
	this.copy_node = node;
};
WorkspaceTreeAdapter.prototype.paste = function(node){
	if (this.copy_node && this.copy_node !== null) {
		this.copyNode(this.copy_node, node);
	}
	this.copy_node = null;
};
WorkspaceTreeAdapter.prototype.refresh = function(node, keepState){
	//TODO: This is reliable but a bit intrusive. Find out a more subtle way to update on demand
	var resourcepath;
	if(node){
		resourcepath = node.original._file.path;
	} else {
		resourcepath = this.workspaceName;
	}
	return this.workspaceService.load(resourcepath)
			.then(function(_data){
				var data = [];
				if(_data.type === 'workspace'){
					data = _data.projects;
				} else if(_data.type === 'folder' || _data.type === 'project'){
					data = [_data];
				}
				
				data = data.map(this._buildTreeNode.bind(this));
				
				if(!this.jstree.settings.core.data || _data.type === 'workspace')
					this.jstree.settings.core.data = data;
				else{
					//find and replace the loaded node
					var self  = this;
					this.jstree.settings.core.data = this.jstree.settings.core.data.map(function(node){
						data.forEach(function(_node, replacement){
							if(self._fnr(_node, replacement))
								return;
						}.bind(self, node));
						return node;
					});
				}
				if(!keepState)
					this.jstree.refresh();
			}.bind(this));
};
WorkspaceTreeAdapter.prototype.openNodeProperties = function(resource){
	this.messageHub.announceFilePropertiesOpen(resource);
};
WorkspaceTreeAdapter.prototype.publish = function(resource){
	return this.publishService.publish(resource.path)
	.then(function(){
		return this.messageHub.announcePublish(resource);
	}.bind(this));
};
WorkspaceTreeAdapter.prototype.unpublish = function(resource){
	return this.publishService.unpublish(resource.path)
	.then(function(){
		return this.messageHub.announceUnpublish(resource);
	}.bind(this));
};
WorkspaceTreeAdapter.prototype.exportProject = function(resource){
	if (resource.type === 'project') {
		return this.exportService.exportProject(resource.path);
	}
};
WorkspaceTreeAdapter.prototype.generateFile = function(resource, scope){
	var segments = resource.path.split('/');
	this.workspaceController.projectName = segments[2];
	if (resource.type === 'project' || resource.type === 'folder') {	
		segments = segments.splice(3, segments.length);
		this.workspaceController.fileName = new UriBuilder().path(segments).path("fileName").build();
		scope.$apply();
//		$('#generateFromTemplate').click();
        this.workspaceController.generateFromTemplate(scope);
	} else {
		this.workspaceController.fileName = segments[segments.length-1];
		scope.$apply();
//		$('#generateFromModel').click();
		this.workspaceController.generateFromModel(scope);
	}
};
WorkspaceTreeAdapter.prototype.uploadFileInPlace = function(resource, scope){
	var segments = resource.path.split('/');
	this.workspaceController.projectName = segments[2];
	if (resource.type === 'project' || resource.type === 'folder') {	
		segments = segments.splice(3, segments.length);
		this.workspaceController.fileName = new UriBuilder().path(segments).build();
		scope.$apply();
		$('#uploadFile').click();
	}
};

var TemplatesService = function($http, $window, TEMPLATES_SVC_URL) {
	this.$http = $http;
	this.$window = $window;
	this.TEMPLATES_SVC_URL = TEMPLATES_SVC_URL;
};
TemplatesService.prototype.listTemplates = function(){
	var url = new UriBuilder().path(this.TEMPLATES_SVC_URL.split('/')).build();
	return this.$http.get(url).then(function(response){ return response.data;});
};

angular.module('workspace.config', [])
	.constant('WS_SVC_URL','../../../../services/v4/ide/workspaces')
	.constant('WS_SVC_MANAGER_URL','../../../../services/v4/ide/workspace')
	.constant('PUBLISH_SVC_URL','../../../../services/v4/ide/publisher/request')
	.constant('EXPORT_SVC_URL','../../../../services/v4/transport/project')
	.constant('TEMPLATES_SVC_URL','../../../../services/v4/js/ide-core/services/templates.js')
	.constant('GENERATION_SVC_URL','../../../../services/v4/ide/generate');
	
angular.module('workspace', ['workspace.config', 'ideUiCore', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])
.factory('httpRequestInterceptor', function () {
	var csrfToken = null;
	return {
		request: function (config) {
			config.headers['X-Requested-With'] = 'Fetch';
			config.headers['X-CSRF-Token'] = csrfToken ? csrfToken : 'Fetch';
			return config;
		},
		response: function(response) {
			var token = response.headers()['x-csrf-token'];
			if (token) {
				csrfToken = token;
			}
			return response;
		}
	};
})
.config(['$httpProvider', function($httpProvider) {
	//check if response is error. errors currently are non-json formatted and fail too early
	$httpProvider.defaults.transformResponse.unshift(function(data, headersGetter, status){
		if(status>399){
			data = {
				"error": data
			};
			data = JSON.stringify(data);
		}
		return data;
	});
	$httpProvider.interceptors.push('httpRequestInterceptor');
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
	var announceFileDeleted = function(fileDescriptor){
		this.send('file.deleted', fileDescriptor);
	};
	var announceFileRenamed = function(fileDescriptor, oldName, newName){
		var data = {
			"file": fileDescriptor,
			"oldName": oldName,
			"newName": newName
		};
		this.send('file.renamed', data);
	};	
	var announceFileMoved = function(fileDescriptor, sourcepath, targetpath){
		var data = {
			"file": fileDescriptor,
			"sourcepath": sourcepath,
			"targetpath": targetpath
		};
		this.send('file.moved', data);
	};
	var announceFileCopied = function(fileDescriptor, sourcepath, targetpath){
		var data = {
			"file": fileDescriptor,
			"sourcepath": sourcepath,
			"targetpath": targetpath
		};
		this.send('file.copied', data);
	};
	var announceFilePropertiesOpen = function(fileDescriptor){
		this.send('file.properties', fileDescriptor);
	};
	var announcePublish = function(fileDescriptor){
		this.send('file.published', fileDescriptor);
	};
	var announceUnpublish = function(fileDescriptor){
		this.send('file.unpublished', fileDescriptor);
	};
	var announceExport = function(fileDescriptor){
		this.send('project.exported', fileDescriptor);
	};
	return {
		send: send,
		announceFileSelected: announceFileSelected,
		announceFileCreated: announceFileCreated,
		announceFileOpen: announceFileOpen,
		announceFileDeleted: announceFileDeleted,
		announceFileRenamed: announceFileRenamed,
		announceFileMoved: announceFileMoved,
		announceFileCopied: announceFileCopied,
		announceFilePropertiesOpen: announceFilePropertiesOpen,
		announcePublish: announcePublish,
		announceUnpublish: announceUnpublish,
		announceExport: announceExport,
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
			}
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
			if (!editors) {
				return;
			}
			if(editors.length > 1){
				ctxmenu.openWith =  {
					"label": "Open with...",
					"submenu": createOpenWithSubmenu.call(this, contentType)
				};			
			} else {
				ctxmenu.open = createOpenEditorMenuItem(editors[0], 'Open');
			}
		};		
	};
	
	var openMenuItemFactory = new OpenMenuItemFactory(Editors);
	
	return openMenuItemFactory;
}])
.factory('$treeConfig', ['$treeConfig.openmenuitem', function(openmenuitem){
	
	// get the new by template extensions
	var templates = $.ajax({
        type: "GET",
        url: '../../../../services/v4/js/ide-workspace/services/workspace-menu-new-templates.js',
        cache: false,
        async: false
    }).responseText;
    
    // get file extensions with available generation templates
    var fileExtensions = $.ajax({
        type: "GET",
        url: '../../../../services/v4/js/ide-core/services/templates.js/extensions',
        cache: false,
        async: false
    }).responseText;
    
    
    
	var priorityFileTemplates = JSON.parse(templates).filter(e => e.order !== undefined).sort((a, b) => a.order - b.order);
    var specificFileTemplates = JSON.parse(templates).filter(e => e.order === undefined);

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
				if (o === 'delete_node') {
                    if (!confirmRemove(n.text)) {
                        return false;
                    }
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
				} else {
					/*New*/
					ctxmenu.create = _ctxmenu.create;
					delete ctxmenu.create.action;
					ctxmenu.create.label = "New";
					ctxmenu.create.submenu = {
						/*Folder*/
						"create_folder" : {
							"label"				: "Folder",
							"action"			: function (data) {
								var tree = data.reference.jstree(true);
								var parentNode = tree.get_node(data.reference);
								var folderNode = {
									type: 'folder'
								};
								tree.create_node(parentNode, folderNode, "last", function (new_node) {
									tree.edit(new_node); 
								});
							}
						},
						/*File*/
						"create_file" : {
							"separator_after"	: true,
							"label"				: "File",
							"action"			: function (tree, data) {
								var parentNode = tree.get_node(data.reference);
								var fileNode = {
									type: 'file'
								};
								tree.create_node(parentNode, fileNode, "last", function (new_node) {
									tree.edit(new_node); 
								});
							}.bind(self, this)
						}
					};
				}
				
				if (ctxmenu.create) {
					for (let i = 0; i < priorityFileTemplates.length; i ++) {
						let fileTemplate = priorityFileTemplates[i];
						ctxmenu.create.submenu[fileTemplate.name] = {
							"separator_after"	: (i + 1 === priorityFileTemplates.length),
							"label"				: fileTemplate.label,
							"action"			: function (wnd, data) {
								var tree = $.jstree.reference(data.reference);
								var parentNode = tree.get_node(data.reference);
								var fileNode = {
									type: 'file'
								};
								fileNode.text = 'file.'+fileTemplate.extension;
								fileNode.data = fileTemplate.data;
								tree.create_node(parentNode, fileNode, "last", function (new_node) {
									tree.edit(new_node);
								});
							}.bind(self, this)
						};
					}

					specificFileTemplates.forEach(function(fileTemplate){
						ctxmenu.create.submenu[fileTemplate.name] = {
								"label"				: fileTemplate.label,
								"action"			: function (wnd, data) {
									var tree = $.jstree.reference(data.reference);
									var parentNode = tree.get_node(data.reference);
									var fileNode = {
										type: 'file'
									};
									fileNode.text = 'file.'+fileTemplate.extension;
									fileNode.data = fileTemplate.data;
									tree.create_node(parentNode, fileNode, "last", function (new_node) {
										tree.edit(new_node);
									});
								}.bind(self, this)
							};
					});
				}
				
				/*Copy*/
				ctxmenu.copy = {
					"separator_before": true,
					"label": "Copy",
					"action": function(data){
						var tree = $.jstree.reference(data.reference);
						var node = tree.get_node(data.reference);
						tree.element.trigger('jstree.workspace.copy', [node]);
					}.bind(this)
				};
				
				/*Paste*/
				ctxmenu.paste = {
					"separator_before": false,
					"label": "Paste",
					"action": function(data){
						var tree = $.jstree.reference(data.reference);
						var node = tree.get_node(data.reference);
						tree.element.trigger('jstree.workspace.paste', [node]);
					}.bind(this)
				};
				
				/*Rename*/
				ctxmenu.rename = _ctxmenu.rename;
				ctxmenu.rename.shortcut = 113;
				ctxmenu.rename.shortcut_label = 'F2';
				ctxmenu.rename.separator_before = true;
				
				/*Remove*/
				ctxmenu.remove = _ctxmenu.remove;
				ctxmenu.remove.shortcut = 46;
				ctxmenu.remove.shortcut_label = 'Del';
				
				if (this.get_type(node) !== "file") {
					/*Generate*/
					ctxmenu.generate = {
						"separator_before": true,
						"label": "Generate",
						"action": function(data){
							var tree = $.jstree.reference(data.reference);
							var node = tree.get_node(data.reference);
							tree.element.trigger('jstree.workspace.generate', [node.original._file]);
						}.bind(this)
					};
					
					/*Publish*/
					ctxmenu.publish = {
						"separator_before": true,
						"label": "Publish",
						"action": function(data){
							var tree = $.jstree.reference(data.reference);
							var node = tree.get_node(data.reference);
							tree.element.trigger('jstree.workspace.publish', [node.original._file]);
						}.bind(this)
					};
					/*Publish*/
					ctxmenu.unpublish = {
						"separator_before": false,
						"label": "Unpublish",
						"action": function(data){
							var tree = $.jstree.reference(data.reference);
							var node = tree.get_node(data.reference);
							tree.element.trigger('jstree.workspace.unpublish', [node.original._file]);
						}.bind(this)
					};
					/*Upload*/
					ctxmenu.upload = {
						"separator_before": true,
						"label": "Upload",
						"action": function(data){
							var tree = $.jstree.reference(data.reference);
							var node = tree.get_node(data.reference);
							tree.element.trigger('jstree.workspace.upload', [node.original._file]);
						}.bind(this)
					};
				}
				
				var ext = node.original._file.path.substring(node.original._file.path.lastIndexOf(".") + 1, node.original._file.path.length);
				if (this.get_type(node) === "file" &&  fileExtensions.includes(ext)) {
					/*Generate Model*/
					ctxmenu.generate = {
						"separator_before": true,
						"label": "Generate",
						"action": function(data){
							var tree = $.jstree.reference(data.reference);
							var node = tree.get_node(data.reference);
							tree.element.trigger('jstree.workspace.generate', [node.original._file]);
						}.bind(this)
					};
				}

				if (this.get_type(node) === "project") {
					/*Export*/
					ctxmenu.exportProject = {
						"separator_before": true,
						"label": "Export",
						"action": function(data){
							var tree = $.jstree.reference(data.reference);
							var node = tree.get_node(data.reference);
							tree.element.trigger('jstree.workspace.export', [node.original._file]);
						}.bind(this)
					};
				}

				return ctxmenu;
			}
		}
	};
}])
.factory('publishService', ['$http', 'PUBLISH_SVC_URL', function($http, PUBLISH_SVC_URL){
	return {
		publish : function(resourcePath){
			var url = new UriBuilder().path(PUBLISH_SVC_URL.split('/')).path(resourcePath.split('/')).build();
			return $http.post(url, {});
		},
		unpublish : function(resourcePath){
			var url = new UriBuilder().path(PUBLISH_SVC_URL.split('/')).path(resourcePath.split('/')).build();
			return $http.delete(url, {});
		}
	};
}])
.factory('exportService', ['$http', '$window', 'EXPORT_SVC_URL', function($http, $window, EXPORT_SVC_URL){
	return {
		exportProject : function(resourcePath){
			var url = new UriBuilder().path(EXPORT_SVC_URL.split('/')).path(resourcePath.split('/')).build();
			$window.open(url);
		}
	};
}])
.factory('generationService', ['$http', 'GENERATION_SVC_URL', function($http, GENERATION_SVC_URL){
	return {
		generateFromTemplate : function(workspace, project, file, template, parameters, wsTree) {
			var url = new UriBuilder().path(GENERATION_SVC_URL.split('/')).path('file').path(workspace).path(project).path(file.split('/')).build();
			parameters = parameters === undefined || parameters === null ? [] : parameters;
			return $http.post(url, {"template":template, "parameters":parameters})
					.then(function(response){
						wsTree.refresh();
						return response.data;
					});
		},
		generateFromModel : function(workspace, project, file, template, parameters, wsTree) {
			var url = new UriBuilder().path(GENERATION_SVC_URL.split('/')).path('model').path(workspace).path(project).path(file.split('/')).build();
			parameters = parameters === undefined || parameters === null ? [] : parameters;
			return $http.post(url, {"template":template, "parameters":parameters, "model":file})
					.then(function(response){
						wsTree.refresh();
						return response.data;
					});
		}
	};
}])
.factory('templatesService', ['$http', '$window', 'TEMPLATES_SVC_URL', function($http, $window, TEMPLATES_SVC_URL){
	return new TemplatesService($http, $window, TEMPLATES_SVC_URL);
}])
.factory('workspaceService', ['$http', '$window', 'WS_SVC_MANAGER_URL', 'WS_SVC_URL', '$treeConfig', function($http, $window, WS_SVC_MANAGER_URL, WS_SVC_URL, $treeConfig){
	return new WorkspaceService($http, $window, WS_SVC_MANAGER_URL, WS_SVC_URL, $treeConfig);
}])
.factory('workspaceTreeAdapter', ['$treeConfig', 'workspaceService', 'publishService', 'exportService', 'messageHub', function($treeConfig, WorkspaceService, publishService, exportService, messageHub){
	return new WorkspaceTreeAdapter($treeConfig, WorkspaceService, publishService, exportService, messageHub);
}])
.controller('WorkspaceController', ['workspaceService', 'workspaceTreeAdapter', 'publishService', 'exportService', 'templatesService', 'generationService', 'messageHub', '$scope', function (workspaceService, workspaceTreeAdapter, publishService, exportService, templatesService, generationService, messageHub, $scope) {

	this.wsTree;
	this.workspaces;
	this.selectedWorkspace;
	this.selectedTemplate;

	this.refreshTemplates = function() {
		templatesService.listTemplates()
			.then(function(data) {
				this.templates = data;
				this.modelTemplates = [];
				this.genericTemplates = [];
				this.templateParameters = [];
				for (var i = 0 ; i < this.templates.length; i++) {
					this.templateParameters[this.templates[i].id] = this.templates[i].parameters;
//					if (this.templates[i].model) {
//						this.modelTemplates.push(this.templates[i]);
//					}
				}
			}.bind(this));
	};
	this.refreshTemplates();
	
	this.filterModelTemplates = function(ext) {
		this.modelTemplates.length = 0;
		this.templates.forEach(template => {if (template.extension === ext) this.modelTemplates.push(template);});
	};

	this.filterGenericTemplates = function() {
		this.genericTemplates.length = 0;
		this.templates.forEach(template => {if (template.extension === undefined || template.extension === null) this.genericTemplates.push(template);});
	};
	
	this.refreshWorkspaces = function() {
		workspaceService.listWorkspaceNames()
			.then(function(workspaceNames) {
				this.workspaces = workspaceNames;
				if(this.workspaceName) {
					this.selectedWorkspace = this.workspaceName;
					this.workspaceSelected();
				} else {
					var storedWorkspace = JSON.parse(localStorage.getItem('DIRIGIBLE.workspace'));
					if (storedWorkspace !== null) {
						this.selectedWorkspace = storedWorkspace.name;
						this.workspaceSelected();
					} else if(this.workspaces[0]) {
						this.selectedWorkspace = this.workspaces[0];
						this.workspaceSelected();
					}
				} 
			}.bind(this));
	};
	this.refreshWorkspaces();
	
	this.workspaceSelected = function(){
		if (this.wsTree) {
			this.wsTree.workspaceName = this.selectedWorkspace;
			localStorage.setItem('DIRIGIBLE.workspace', JSON.stringify({"name": this.selectedWorkspace}));
			this.wsTree.refresh();
			return;
		}
		this.wsTree = workspaceTreeAdapter.init($('.workspace'), this, $scope);
		if(!workspaceService.typeMapping)
			workspaceService.typeMapping = $treeConfig[types];
		this.wsTree.refresh();
	};
	
	this.createWorkspace = function(){
		$('#createWorkspace').click();
	};
	this.okCreateWorkspace = function() {
		if (this.workspaceName) {
			workspaceService.createWorkspace(this.workspaceName);
			this.refreshWorkspaces();
		}
	};
	
	this.createProject = function(){
		$('#createProject').click();
	};
	this.okCreateProject = function() {
		if (this.projectName) {
			workspaceService.createProject(this.selectedWorkspace, this.projectName, this.wsTree);
		}
	};

	this.linkProject = function(){
		$('#linkProject').click();
	};
	this.okLinkProject = function() {
		if (this.projectName && this.linkedPath) {
			workspaceService.linkProject(this.selectedWorkspace, this.projectName, this.linkedPath, this.wsTree);
		}
	};
	
	this.generateFromTemplate = function(scope){
		this.filterGenericTemplates();
		scope.$apply();
		$('#generateFromTemplate').click();
	};
	this.okGenerateFromTemplate = function() {
		if (this.projectName) {
			generationService.generateFromTemplate(this.selectedWorkspace, this.projectName, this.fileName, this.selectedTemplate, this.parameters, this.wsTree);
		}
	};
	
	this.generateFromModel = function(scope) {
		var ext = getFileExtension(this.fileName);
		this.filterModelTemplates(ext);
		scope.$apply();
		$('#generateFromModel').click();
	};
	this.okGenerateFromModel = function() {
		if (this.projectName) {
			generationService.generateFromModel(this.selectedWorkspace, this.projectName, this.fileName, this.selectedTemplate, this.parameters, this.wsTree);
		}
	};
	this.shouldShow = function(property) {
		var shouldShow = true;
		if(property.ui && property.ui.hide && this.parameters) {
			if (this.parameters[property.ui.hide.property] !== undefined) {				
				shouldShow = this.parameters[property.ui.hide.property] !== property.ui.hide.value;
			} else {
				shouldShow = property.ui.hide.value;
			}
		} else if (property.ui && property.ui.hide && this.parameters === undefined) {
			shouldShow = property.ui.hide.value;
		}
		return shouldShow;
	};
	this.shouldShowText = function(property) {
		return property.type === undefined || property.type === 'text';
	};
	this.shouldShowCheckbox = function(property) {
		return property.type === 'checkbox';
	};
	
	this.publish = function(){
		publishService.publish(this.selectedWorkspace + '/*')
			.then(function(){
				return messageHub.announcePublish(this.selectedWorkspace + '/*');
			}.bind(this));
	};

	this.unpublish = function(){
		publishService.unpublish(this.selectedWorkspace + '/*')
			.then(function(){
				return messageHub.announceUnpublish(this.selectedWorkspace + '/*');
			}.bind(this));
	};
	
	this.exportWorkspace = function(){
		exportService.exportProject(this.selectedWorkspace + '/*');
	};
	
	this.refresh = function(){
		this.wsTree.refresh();
	};
	
	this.saveAll = function(){
		messageHub.send('workbench.editor.save', {data: ""}, true);
	};
	
	this.uploadFile = function(){
		$('#uploadFile').click();
	};
	this.okUploadFile = function() {
		var f = document.getElementById('uploadFileField').files[0],
        r = new FileReader();
        var name = f.name;
        var path =  '/' + this.selectedWorkspace + '/' + this.projectName + (this.fileName ? '/' + this.fileName : '');
	
	    r.onloadend = function(e) {
	      var data = e.target.result;
	      var node = {};
	      node.type = 'file';
	      node.data = data;
	      workspaceService.uploadFile(name, path, node);
	      messageHub.send('workspace.file.uploaded', {data: ""}, true);
	    };
	
	    r.readAsBinaryString(f);
	};
	
	messageHub.on('workbench.theme.changed', function(msg){
		var themeUrl = msg.data;
		
		$('a[href="../../../../services/v4/core/theme/jstree.css"]').remove();
		$('<link id="theme-stylesheet" href="../../../../services/v4/core/theme/jstree.css" rel="stylesheet" />').appendTo('head');
		
		$('a[href="../../../../services/v4/core/theme/ide.css"]').remove();
		$('<link href="../../../../services/v4/core/theme/ide.css" rel="stylesheet" />').appendTo('head');
		
		$('#theme-stylesheet').remove();
		$('<link id="theme-stylesheet" href="'+themeUrl +'" rel="stylesheet" />').appendTo('head');
	}.bind(this));
	
	messageHub.on('editor.file.saved', function(msg){
		var filePath = msg.data;
		// TODO auto-publish configuration
		publishService.publish(filePath).then(function(filePath){
			return messageHub.announcePublish();
		}.bind(this));
	}.bind(this), true);
	
	messageHub.on('workspace.create.workspace', function(msg){
		$('#createWorkspace').click();
	}.bind(this), true);
	
	messageHub.on('workspace.create.project', function(msg){
		$('#createProject').click();
	}.bind(this), true);

	messageHub.on('workspace.link.project', function(msg){
		$('#linkProject').click();
	}.bind(this), true);
	
	messageHub.on('workspace.publish.all', function(msg){
		publishService.publish(this.selectedWorkspace + '/*');
	}.bind(this), true);
	
	messageHub.on('workspace.export.all', function(msg){
		exportService.exportProject(this.selectedWorkspace + '/*');
	}.bind(this), true);
	
	messageHub.on('workspace.file.uploaded', function(msg){
		workspaceTreeAdapter.refresh();
	}.bind(this), true);

	messageHub.on('workspace.refresh', function(msg){
		workspaceTreeAdapter.refresh();
	}.bind(this), true);
	
	//$.jstree.defaults.unique.case_sensitive = true;

}]);

function confirmRemove(name) {
	return confirm("Do you really want to delete: " + name);//$confirmDialog.dialog('open');
}

const images = ['png', 'jpg', 'jpeg', 'gif'];
const models = ['extension', 'extensionpoint', 'edm', 'model', 'dsm', 'schema', 'bpmn', 'job', 'listener', 'websocket', 'roles', 'constraints', 'table', 'view'];

function getIcon(f) {
	var icon;
	if (f.type === 'project' && f.git) {
		icon = "fa fa-git-square";
	} else if (f.type === 'file') {
		var ext = getFileExtension(f.name);
		if (ext === 'js') {
			icon = "fa fa-file-code-o";
		} else if (ext === 'html') {
			icon = "fa fa-html5";
		} else if (ext === 'css') {
			icon = "fa fa-css3";
		} else if (ext === 'txt'|| ext === 'json') {
			icon = "fa fa-file-text-o";
		} else if (images.indexOf(ext) !== -1) {
			icon = "fa fa-file-image-o";
		} else if (models.indexOf(ext) !== -1) {
			icon = "fa fa-file-text";
		} else {
			icon = "fa fa-file-o";
		}
	}
	return icon;
}

function getFileExtension(f) {
	return f.substring(f.lastIndexOf(".") + 1, f.length);
}
