/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
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
			}
			var tmpl = this.typeMapping[type].template_new_name;
			var regex = new RegExp(Object.keys(parameters).join('|'), 'g');
			var fName = tmpl.replace(regex, function(m) {
				return parameters[m]!==undefined?parameters[m]:m;
			});
			name = fName.trim();
		} 
		return name;
	}
			
	var startsWith = function (stringToTest, prefixToTest){
		var startsWithRegEx = new RegExp('^'+prefixToTest);
		var matches = stringToTest.match(startsWithRegEx);
		return matches != null && matches.length > 0;
	}
	
	var strictInt = function(value) {
	  if (/^(\-|\+)?([0-9]+|Infinity)$/.test(value))
		return Number(value);
	  return NaN;
	}
	
	var toInt = function(value){
		if(value ===undefined)
			return;
		var _result = value.trim();
		_result = strictInt(_result);
		if(isNaN(_result))
			_result = undefined;
		return _result;
	}
	
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
	}
};

WorkspaceService.prototype.createFolder = function(type){
	var inst = $.jstree.reference(data.reference),
		obj = inst.get_node(data.reference);
	var node_tmpl = {
		type: 'folder',
		text: this.newFileName('folder', 'folder')
	}
	inst.create_node(obj, node_tmpl, "last", function (new_node) {
		setTimeout(function () { inst.edit(new_node); },0);
	});
};

WorkspaceService.prototype.createFile = function(name, path, node){
	var isDirectory = node.type === 'folder';
	var url = new UriBuilder().path((this.workspacesServiceUrl+path).split('/')).path(name).build();
	if(isDirectory)
		url+="/";
	if (!node.data)
		node.data = '';
	return this.$http.post(url, JSON.stringify(node.data))
			.then(function(response){
				var filePath = response.headers('location');
				return this.$http.get(filePath, {headers: { 'describe': 'application/json'}})
					.then(function(response){ return response.data});
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
	return this.$http['delete'](url);
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
	})
};
WorkspaceService.prototype.copy = function(filename, sourcepath, targetpath, workspaceName){
	var url = new UriBuilder().path(this.workspaceManagerServiceUrl.split('/')).path(workspaceName).path('copy').build();
	//NOTE: The third argument is a temporary fix for the REST API issue that sending header  content-type: 'application/json' fails the move operation
	return this.$http.post(url, { 
		source: sourcepath + '/' + filename,
		target: targetpath + '/',
	})
};
WorkspaceService.prototype.load = function(wsResourcePath){
	var url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(wsResourcePath.split('/')).build();
	return this.$http.get(url, {headers: { 'describe': 'application/json'}})
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
WorkspaceService.prototype.createWorkspace = function(workspace){
	var url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(workspace).build();
	return this.$http.post(url)
			.then(function(response){
				return response.data;
			});
}
WorkspaceService.prototype.createProject = function(workspace, project, wsTree){
	var url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(workspace).path(project).build();
	return this.$http.post(url)
			.then(function(response){
				wsTree.refresh();
				return response.data;
			});
}

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
			var _files = f.files.map(this._buildTreeNode.bind(this))
			children = children.concat(_files);
		}
		f.label = f.name;
		return {
			"text": f.name,
			"children": children,
			"type": f.type,
			"_file": f
		}
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
	.on('open_node.jstree', function(evt, data) {
		if (data.node.type !== 'project')
			data.instance.set_icon(data.node, 'fa fa-folder-open-o');
	})
	.on('close_node.jstree', function(evt, data) {
		if (data.node.type !== 'project')
			data.instance.set_icon(data.node, 'fa fa-folder-o');
	})
	.on('delete_node.jstree', function (e, data) {
		this.deleteNode(data.node)
	}.bind(this))
	.on('create_node.jstree', function (e, data) {})
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
	.on('jstree.workspace.export', function (e, data) {		
		this.exportProject(data);
	}.bind(this))
	.on('jstree.workspace.generate', function (e, data) {		
		this.generateFile(data, scope);
	}.bind(this))
	.on('jstree.workspace.openWith', function (e, data, editor) {		
		this.openWith(data, editor);
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
	}
	
	var ctxPath = parentNode.original._file.path;
	
	var self = this;
	this.jstree.create_node(parentNode, node_tmpl, "last", 
		function (new_node) {
			var name = node_tmpl.text;
			self.jstree.edit(new_node); 
		});
}
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
}
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
				this.jstree.delete_node(node);
				this.refresh();
				throw err;
			}.bind(this, node));
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
	var tagetParentNode = this.jstree.get_node(node.parent);
	var targetpath = tagetParentNode.original._file.path.substring(this.workspaceName.length+1);
	var self = this;
	return this.workspaceService.copy(node.text, sourcepath, targetpath, this.workspaceName)
			.then(function(sourceParentNode, tagetParentNode){
				self.refresh(sourceParentNode, true);
				self.refresh(tagetParentNode, true).then(function(){
					self.messageHub.announceFileCopied(targetpath+'/'+node.text, sourcepath, targetpath);
				});
			}.bind(this, sourceParentNode, tagetParentNode))
			.finally(function() {
				this.refresh();
			}.bind(this));
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
				if(_data.type == 'workspace'){
					data = _data.projects;
				} else if(_data.type == 'folder' || _data.type == 'project'){
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
		$('#generateFromTemplate').click();
	} else {
		this.workspaceController.fileName = segments[segments.length-1];
		scope.$apply();
		$('#generateFromModel').click();
	}
};

var TemplatesService = function($http, $window, TEMPLATES_SVC_URL) {
	this.$http = $http;
	this.$window = $window;
	this.TEMPLATES_SVC_URL = TEMPLATES_SVC_URL;
}
TemplatesService.prototype.listTemplates = function(){
	var url = new UriBuilder().path(this.TEMPLATES_SVC_URL.split('/')).build();
	return this.$http.get(url).then(function(response){ return response.data});
}

angular.module('workspace.config', [])
	.constant('WS_SVC_URL','/services/v3/ide/workspaces')
	.constant('WS_SVC_MANAGER_URL','/services/v3/ide/workspace')
	.constant('PUBLISH_SVC_URL','/services/v3/ide/publisher/request')
	.constant('EXPORT_SVC_URL','/services/v3/transport/project')
	.constant('TEMPLATES_SVC_URL','/services/v3/js/ide/services/templates.js')
	.constant('GENERATION_SVC_URL','/services/v3/ide/generate');
	
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

	var specificFileTemplates = [];
	specificFileTemplates.push({'name':'table', 'label':'Database Table', 'extension':'table', 'data':JSON.stringify(JSON.parse('{"name":"MYTABLE","type":"TABLE","columns":[{"name":"ID","type":"INTEGER","length":"0","nullable":"false","primaryKey":"true","defaultValue":""}],"dependencies":[]}'), null, 2)});
	specificFileTemplates.push({'name':'view', 'label':'Database View', 'extension':'view', 'data':JSON.stringify(JSON.parse('{"name":"MYVIEW","type":"VIEW","query":"SELECT * FROM MYTABLE","dependencies":[{"name":"MYTABLE","type":"TABLE"}]}'), null, 2)});
	specificFileTemplates.push({'name':'schema', 'label':'Database Schema Model', 'extension':'dsm', 'data':'<schema><structures></structures><mxGraphModel><root></root></mxGraphModel></schema>'});
	specificFileTemplates.push({'name':'entity', 'label':'Entity Data Model', 'extension':'edm', 'data':'<model><entities></entities><mxGraphModel><root></root></mxGraphModel></model>'});
	specificFileTemplates.push({'name':'js', 'label':'JavaScript Service', 'extension':'js', 'data':'var response = require("http/v3/response");\n\nresponse.println("Hello World!");\nresponse.flush();\nresponse.close();'});
	specificFileTemplates.push({'name':'html', 'label':'HTML5 Page', 'extension':'html', 'data':'<!DOCTYPE html>\n<head>\n</head>\n<body>\n</body>\n</html>'});
	specificFileTemplates.push({'name':'job', 'label':'Scheduled Job', 'extension':'job', 'data':JSON.stringify(JSON.parse('{"expression":"0/10 * * * * ?","group":"dirigible-defined","handler":"myproject/myhandler.js","description":"My Job"}'), null, 2)});
	specificFileTemplates.push({'name':'listener', 'label':'Message Listener', 'extension':'listener', 'data':JSON.stringify(JSON.parse('{"name":"/myproject/mylistener","type":"Q","handler":"myproject/myhandler.js","description":"My Listener"}'), null, 2)});
	specificFileTemplates.push({'name':'bpmn', 'label':'Business Process Model', 'extension':'bpmn', 'data':'<?xml version="1.0" encoding="UTF-8"?>\n<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:flowable="http://flowable.org/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" typeLanguage="http://www.w3.org/2001/XMLSchema" expressionLanguage="http://www.w3.org/1999/XPath" targetNamespace="http://www.flowable.org/processdef">\n\t<process id="myprocess" name="MyProcess" isExecutable="true">\n\t\t<startEvent id="sid-3334E861-7999-4B89-B8B0-11724BA17A3E"/>\n\t\t<serviceTask id="sayHello" name="MyServiceTask" flowable:class="org.eclipse.dirigible.bpm.flowable.DirigibleCallDelegate">\n\t\t\t<extensionElements>\n\t\t\t\t<flowable:field name="handler">\n\t\t\t\t\t<flowable:string><![CDATA[myproject/mydelegate.js]]></flowable:string>\n\t\t\t\t</flowable:field>\n\t\t\t</extensionElements>\n\t\t</serviceTask>\n\t\t<sequenceFlow id="sid-797626AE-B2F6-4C00-ABEE-FB30ADC177E4" sourceRef="sid-3334E861-7999-4B89-B8B0-11724BA17A3E" targetRef="sayHello"/>\n\t\t<endEvent id="sid-70B488C1-384A-4E19-8091-1B12D1AEC7FD"/>\n\t\t<sequenceFlow id="sid-645847E8-C959-48BD-816B-2E9CC4A2F08A" sourceRef="sayHello" targetRef="sid-70B488C1-384A-4E19-8091-1B12D1AEC7FD"/>\n\t</process>\n\t<bpmndi:BPMNDiagram id="BPMNDiagram_myprocess">\n\t\t<bpmndi:BPMNPlane bpmnElement="myprocess" id="BPMNPlane_myprocess">\n\t\t\t<bpmndi:BPMNShape bpmnElement="sid-3334E861-7999-4B89-B8B0-11724BA17A3E" id="BPMNShape_sid-3334E861-7999-4B89-B8B0-11724BA17A3E">\n\t\t\t\t<omgdc:Bounds height="30.0" width="30.0" x="103.0" y="78.0"/>\n\t\t\t</bpmndi:BPMNShape>\n\t\t\t<bpmndi:BPMNShape bpmnElement="sayHello" id="BPMNShape_sayHello">\n\t\t\t\t<omgdc:Bounds height="80.0" width="100.0" x="300.0" y="53.0"/>\n\t\t\t</bpmndi:BPMNShape>\n\t\t\t<bpmndi:BPMNShape bpmnElement="sid-70B488C1-384A-4E19-8091-1B12D1AEC7FD" id="BPMNShape_sid-70B488C1-384A-4E19-8091-1B12D1AEC7FD">\n\t\t\t\t<omgdc:Bounds height="28.0" width="28.0" x="562.0" y="78.0"/>\n\t\t\t</bpmndi:BPMNShape>\n\t\t\t<bpmndi:BPMNEdge bpmnElement="sid-797626AE-B2F6-4C00-ABEE-FB30ADC177E4" id="BPMNEdge_sid-797626AE-B2F6-4C00-ABEE-FB30ADC177E4">\n\t\t\t\t<omgdi:waypoint x="133.0" y="93.0"/>\n\t\t\t\t<omgdi:waypoint x="300.0" y="93.0"/>\n\t\t\t</bpmndi:BPMNEdge>\n\t\t\t<bpmndi:BPMNEdge bpmnElement="sid-645847E8-C959-48BD-816B-2E9CC4A2F08A" id="BPMNEdge_sid-645847E8-C959-48BD-816B-2E9CC4A2F08A">\n\t\t\t\t<omgdi:waypoint x="400.0" y="92.77876106194691"/>\n\t\t\t\t<omgdi:waypoint x="562.0001370486572" y="92.06194629624488"/>\n\t\t\t</bpmndi:BPMNEdge>\n\t\t</bpmndi:BPMNPlane>\n\t</bpmndi:BPMNDiagram>\n</definitions>'});
	specificFileTemplates.push({'name':'access', 'label':'Access Constraints', 'extension':'access', 'data':JSON.stringify(JSON.parse('{"constraints":[{"uri":"/myproject/myfolder/myservice.js","method":"GET","roles":["administrator","operator"]}]}'), null, 2)});
	specificFileTemplates.push({'name':'roles', 'label':'Roles Definitions', 'extension':'roles', 'data':JSON.stringify(JSON.parse('[{"name":"administrator","description":"Administrator Role"},{"name":"Operator","description":"Operator Role"}]'), null, 2)});

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
				"icon": "fa fa-clone"
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
				}
				
				if (this.get_type(node) === "file" && node.original._file.path.endsWith('.model')) {
					/*Generate Model*/
					ctxmenu.generate = {
						"separator_before": true,
						"label": "Generate",
						"action": function(data){
							var tree = $.jstree.reference(data.reference);
							var node = tree.get_node(data.reference);
							tree.element.trigger('jstree.workspace.generate', [node.original._file]);
						}.bind(this)
					}
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
	}
}])
.factory('publishService', ['$http', 'PUBLISH_SVC_URL', function($http, PUBLISH_SVC_URL){
	return {
		publish : function(resourcePath){
			var url = new UriBuilder().path(PUBLISH_SVC_URL.split('/')).path(resourcePath.split('/')).build();
			return $http.post(url);
		}
	}
}])
.factory('exportService', ['$http', '$window', 'EXPORT_SVC_URL', function($http, $window, EXPORT_SVC_URL){
	return {
		exportProject : function(resourcePath){
			var url = new UriBuilder().path(EXPORT_SVC_URL.split('/')).path(resourcePath.split('/')).build();
			$window.open(url);
		}
	}
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
	}
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
				this.templateParameters = [];
				for (var i = 0 ; i < this.templates.length; i++) {
					this.templateParameters[this.templates[i].id] = this.templates[i].parameters;
					if (this.templates[i].model) {
						this.modelTemplates.push(this.templates[i]);
					}
				}
			}.bind(this));
	};
	this.refreshTemplates();
	
	this.refreshWorkspaces = function() {
		workspaceService.listWorkspaceNames()
			.then(function(workspaceNames) {
				this.workspaces = workspaceNames;
				if(this.workspaceName) {
					this.selectedWorkspace = this.workspaceName;
					this.workspaceSelected();
				} else if(this.workspaces[0]) {
					this.selectedWorkspace = this.workspaces[0];
					this.workspaceSelected();
				} 
			}.bind(this));
	};
	this.refreshWorkspaces();
	
	this.workspaceSelected = function(){
		if (this.wsTree) {
			this.wsTree.workspaceName = this.selectedWorkspace;
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
	
	this.generateFromTemplate = function(){
		$('#generateFromTemplate').click();
	};
	this.okGenerateFromTemplate = function() {
		if (this.projectName) {
			generationService.generateFromTemplate(this.selectedWorkspace, this.projectName, this.fileName, this.selectedTemplate, this.parameters, this.wsTree);
		}
	};
	
	this.generateFromModel = function(){
		$('#generateFromModel').click();
	};
	this.okGenerateFromModel = function() {
		if (this.projectName) {
			generationService.generateFromModel(this.selectedWorkspace, this.projectName, this.fileName, this.selectedTemplate, this.parameters, this.wsTree);
		}
	};
	
	this.publish = function(){
		publishService.publish(this.selectedWorkspace + '/*');
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
	
	messageHub.on('workbench.theme.changed', function(msg){
		var themeUrl = msg.data;
		
		$('a[href="/services/v3/core/theme/jstree.css"]').remove();
		$('<link id="theme-stylesheet" href="/services/v3/core/theme/jstree.css" rel="stylesheet" />').appendTo('head');
		
		$('a[href="/services/v3/core/theme/ide.css"]').remove();
		$('<link href="/services/v3/core/theme/ide.css" rel="stylesheet" />').appendTo('head');
		
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

}]);
