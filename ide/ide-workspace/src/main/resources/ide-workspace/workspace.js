/**
 * Utility URL builder
 */
var UriBuilder = function UriBuilder(baseUri){
	this.baseUri = baseUri;
	if(this.baseUri.length && this.baseUri.charAt(this.baseUri.length-1) !=='/')
		this.baseUri+='/';
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
	var uriPath = this.pathSegments.join('/');
	return this.baseUri + uriPath;
}

/**
 * Bridge between jstree and workspace API
 */
var WorkspaceService = function(workspaceManagerServiceUrl, workspacesServiceUrl, fileTypesCfg, $http){

	this.workspaceManagerServiceUrl = workspaceManagerServiceUrl;
	this.workspacesServiceUrl = workspacesServiceUrl;
	this.typeMapping = fileTypesCfg;
	this.$http = $http;
	
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

WorkspaceService.prototype.createFile = function(name, path, isDirectory){
	var url = new UriBuilder(this.workspacesServiceUrl+path).path(name).build();
	if(isDirectory)
		url+="/";
	return this.$http.post(url)
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
	var url = new UriBuilder(this.workspacesServiceUrl).path(filepath.split('/')).build();
	return this.$http['delete'](url);
};
WorkspaceService.prototype.rename = function(oldName, newName, path){
	var pathSegments = path.split('/');
	if(pathSegments.length > 0){
		var workspaceName = path.split('/')[1];
		var url = new UriBuilder(this.workspaceManagerServiceUrl).path(workspaceName).path('rename').build();
		var sourcepath = new UriBuilder(path).path(oldName).build();
		var targetpath = new UriBuilder(path).path(newName).build();
		return this.$http.post(url, {
					source: sourcepath,
					target: targetpath
				})
				.then(function(response){
					return response.data;
				});
	}
};
WorkspaceService.prototype.copy = function(){};
WorkspaceService.prototype.move = function(){};

WorkspaceService.prototype.load = function(selectedWs){
	return this.$http.get(this.workspacesServiceUrl + '/' + selectedWs)
	.then(function(response){
		return response.data;
	})
}

var WorkspaceTreeAdapter = function(containerEl, treeConfig, $messageHub, wsManagerBaseUrl, wsServiceBaseUrl, $http, workspaceName){
	this.containerEl = containerEl;
	this.treeConfig = treeConfig;
	this.$messageHub = $messageHub;
	this.$http = $http;
	this.workspaceName = workspaceName;
	this.workspaceSvc = new WorkspaceService(wsManagerBaseUrl, wsServiceBaseUrl, this.treeConfig.types, $http);
	this.jstree;
	this.init();

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
			"_file": f,
			"icon": f.type=='file'?'fa fa-file-o':undefined
		}
	};
	
	return this;
}
WorkspaceTreeAdapter.prototype.init = function(){
	var self = this;
	this.treeConfig.contextmenu = {
		'items' : function(node) {
			var ctxmenu = $.jstree.defaults.contextmenu.items();
			if(this.get_type(node) === "file") {
				delete ctxmenu.create;
			} else {
				delete ctxmenu.create.action;
				ctxmenu.create.label = "New";
				ctxmenu.create.submenu = {
					"create_folder" : {
						"separator_after"	: true,
						"label"				: "Folder",
						"action"			: function (data) {
							var tree = data.reference.jstree(true);
							var parentNode = tree.get_node(data.reference);
							self.createNode(parentNode, 'folder', 'folder');
						}
					},
					"create_file" : {
						"label"				: "File",
						"action"			: function (tree, data) {
							var parentNode = tree.get_node(data.reference);
							self.createNode(parentNode);
						}.bind(self, this)
					},
					"create_file_txt" : {
						"label"				: "Text File",
						"action"			: function (tree, data) {
							var parentNode = tree.get_node(data.reference);
							self.createNode(parentNode, 'txt');
						}.bind(self, this)
					}
				};
			}										
			ctxmenu.remove.shortcut = 46;
			ctxmenu.remove.shortcut_label = 'Del';
			ctxmenu.rename.shortcut = 113;
			ctxmenu.rename.shortcut_label = 'F2';
			return ctxmenu;
		}
	};
	
	var jstree = this.containerEl.jstree(this.treeConfig);
	
	//subscribe event listeners
	jstree.on('select_node.jstree', function (e, data) {
		//messageHub.post({data: data.node.original._file});
	})
	.on('dblclick.jstree', function (evt) {
		var tree = $.jstree.reference(jstree);
		var selectedNodes = tree.get_selected(true);
		var type = tree.get_node(evt.target).original.type;
		if(['folder','project'].indexOf(type)<0)
			this.$messageHub.fireFileOpen(selectedNodes[0].original._file);
	}.bind(this))
	.on('open_node.jstree', function(evt, data) {
		data.instance.set_icon(data.node, 'fa fa-folder-open-o');
	})
	.on('close_node.jstree', function(evt, data) {
		data.instance.set_icon(data.node, 'fa fa-folder-o');
	})
	.on('delete_node.jstree', function (e, data) {
		this.deleteNode(data.node);
	}.bind(this))
	.on('create_node.jstree', function (e, data) {})
	.on('rename_node.jstree', function (e, data) {
		if(data.old !== data.text){
			this.renameNode(data.node, data.old, data.text);
		}
	}.bind(this))
	.on('move_node.jstree', function (e, data) {
		var parentNode = data.instance.get_node(data.parent);
		$.get(this.wsManagerBaseUrl+'/'+this.workspaceName+'/move', { 
			source: data.node.original._file.path,
			target: data.text//FIXME
		})
		.then(function (d) {
			//data.instance.load_node(data.parent);
		})
		.finally(function () {
			jstree.refresh();
		});
	})
	.on('copy_node.jstree', function (e, data) {
		var parentNode = data.instance.get_node(data.parent);
		$.get(this.wsManagerBaseUrl+'/'+this.workspaceName+'/copy', { 
			source: data.node.original._file.path,
			target: data.text//FIXME
		})
		.then(function (d) {
			//data.instance.load_node(data.parent);
		})
		.finally(function () {
			jstree.refresh();
		});
	}.bind(this));
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
		text: this.workspaceSvc.newFileName(defaultName, type, filenames)
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
		return this.workspaceSvc.remove.apply(this.workspaceSvc, [path])
				.finally(function () {
					this.refresh(undefined, true);
				}.bind(this));	
	}
}
WorkspaceTreeAdapter.prototype.renameNode = function(node, oldName, newName){
	var fpath;
	if(!node.original._file){
		var parentNode = this.jstree.get_node(node.parent);
		var fpath = parentNode.original._file.path;
		this.workspaceSvc.createFile.apply(this.workspaceSvc, [newName, fpath, node.type=='folder'])
			.catch(function(node, err){
				this.jstree.delete_node(node);
				this.refresh(undefined, true);
				throw err;
			}.bind(this, node));
	} else {
		this.workspaceSvc.rename.apply(this.workspaceSvc, [oldName, newName, node.original._file.path])
			.then(function(data){
				//this.jstree.reference(node).select_node(node);
			}.bind(this))
			.finally(function() {
				this.jstree.refresh();
			}.bind(this));		
	}
}
WorkspaceTreeAdapter.prototype.raw = function(){
	return this.jstree;
}
WorkspaceTreeAdapter.prototype.refresh = function(workspaceName, keepState){
	//TODO: This is reliable but a bit intrusive. Find out a more subtle way to update on demand
	return this.workspaceSvc.load((workspaceName||this.workspaceName))
			.then(function(_data){
				if(!Array.isArray(_data.projects))
					data = [_data.projects];
				else
					data = _data.projects;
				data = data.map(this._buildTreeNode.bind(this));
				this.jstree.settings.core.data = data;
				if(!keepState)
					this.jstree.refresh();
			}.bind(this));
}

angular.module('workspace', [])
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
.factory('$messageHub', [function(){
	var messageHub = new FramesMessageHub();
	var fireFileOpen = function(fileDescriptor){
		messageHub.post({data: fileDescriptor}, 'fileselected');
	}
	return {
		fireFileOpen: fireFileOpen
	}
}])
.factory('$treeConfig', [function(){
	return {
		'core' : {
			'themes': {
				"name": "default-dark",
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
			duplicate : function (name, counter) {
				return name + ' (' + counter + ')';
			}
		},
		'types': {
			'default': {
				'icon': "fa fa-file",
				'template_new_name': "{name}{increment}",
			},
			'folder': {
				'icon': "fa fa-folder"
			},
			'file': {
				'template_new_name': "{name}{increment}"
			},
			'txt': {
				'template_new_name': "{name}{increment}.txt",
			},
			'project': {
				'icon': "fa fa-folder"
			}
		}
	}
}])
.controller('WorkspaceController', ['$http', '$messageHub', '$treeConfig', function ($http, $messageHub, $treeConfig) {
					
	var workspacesSvcUrl = "/services/v3/ide/workspaces";
	var workspacesManagerSvcUrl = "/services/v3/ide/workspace";
	this.wsTree;
	this.workspaces;
	this.selectedWs;	
	
	$http.get(workspacesSvcUrl)
		.then(function(response) {
			this.workspaces = response.data;
			if(this.workspaces[0]) {
				this.selectedWs = this.workspaces[0];
				this.workspaceSelected()
			} 
		}.bind(this));
	
	this.workspaceSelected = function(evt){
		//if(this.selectedWs !== ){
			this.wsTree = new WorkspaceTreeAdapter($('.workspace'), $treeConfig, $messageHub, workspacesManagerSvcUrl, workspacesSvcUrl, $http, this.selectedWs);
			this.wsTree.refresh();
		//}
	};

}]);