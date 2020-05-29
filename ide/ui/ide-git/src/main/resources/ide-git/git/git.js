/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
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
	return this.pathSegments.join('/');
}

/**
 * Workspace Service API delegate
 */
var WorkspaceService = function($http, workspaceManagerServiceUrl, workspacesServiceUrl, treeCfg){

	this.workspaceManagerServiceUrl = workspaceManagerServiceUrl;
	this.workspacesServiceUrl = workspacesServiceUrl;
	this.typeMapping = treeCfg['types'];
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


/**
 * Workspace Tree Adapter mediating the workspace service REST api and the jst tree componet working with it
 */
var WorkspaceTreeAdapter = function($http, treeConfig, workspaceSvc, gitService, $messageHub){
	this.treeConfig = treeConfig;
	this.gitService = gitService;
	this.workspaceSvc = workspaceSvc;
	this.$http = $http;
	this.$messageHub = $messageHub;

	this.mapWorkingDir = function(rootFolder, projectName) {
		let workingDir = {
			text: rootFolder.name,
			type: "folder",
			icon: "fa fa-folder-o",
			path: rootFolder.path,
			projectName: projectName,
			isGit: true,
			children: []
		};
		for (let i = 0; i < rootFolder.folders.length; i ++) {
			let folder = rootFolder.folders[i];
			workingDir.children.push(this.mapWorkingDir(folder, projectName));
		}
		for (let i = 0; i < rootFolder.files.length; i ++) {
			let file = rootFolder.files[i];
			workingDir.children.push({
				text: file.name,
				type: "file",
				icon: "fa fa-file-o",
				path: rootFolder.path + "/" + file.name,
				projectName: projectName,
				isGit: true,
			});
		}
		return workingDir;
	};

	this._buildTreeNode = function(f){
		var children = [];
		// if(f.type=='folder' || f.type=='project'){
		if(f.type=='project' && f.git){
			// children = f.folders.map(this._buildTreeNode.bind(this));
			// var _files = f.files.map(this._buildTreeNode.bind(this))
			// children = children.concat(_files);

			let projectName = f.name;
			let workingDir = [];
			for (let i = 0; i < f.folders.length; i ++) {
				let folder = f.folders[i];
				workingDir.push(this.mapWorkingDir(folder, projectName));
			}

			for (let i = 0; i < f.files.length; i ++) {
				let file = f.files[i];
				workingDir.push({
					text: file.name,
					type: "file",
					icon: "fa fa-file-o",
					projectName: projectName,
					path: f.path + "/" + file.name,
					isGit: true,
				});
			}

			children = [
				{text:"local", type: "local", "icon": "fa fa-check-circle-o", children: ['Loading local branches...']},
				{text:"remote", type: "remote", "icon": "fa fa-circle-o", children: ['Loading remote branches...']},
				{text:"working tree", type: "working-tree", "icon": "fa fa-clone", children: workingDir, projectName: projectName, isGit: true}
			];
		}
		var icon;
		if (f.type=='project') {
			icon = (f.git) ? "fa fa-git-square" : "fa fa-hdd-o";
		}

		f.label = f.name;
		return {
			"text": f.name,
			"children": children,
			"type": f.type,
			"git": f.git,
			"icon": icon,
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

WorkspaceTreeAdapter.prototype.init = function(containerEl, workspaceName, workspaceController, gitService){
	this.containerEl = containerEl;
	this.workspaceName = workspaceName;
	this.gitService = gitService;
	
	var self = this;
	var jstree = this.containerEl.jstree(this.treeConfig);
	
	//subscribe event listeners
	jstree.on('select_node.jstree', function (e, data) {
		if (data.type === 'project') {
			workspaceController.selectedProject = data.name;
			
		} else if (data.type === 'local' || data.type === 'remote') {
			workspaceController.selectedProject = data.node.parent.name;
		}

		this.clickNode(this.jstree.get_node(data.node));
	}.bind(this))
	.on('dblclick.jstree', function (evt) {
		this.dblClickNode(this.jstree.get_node(evt.target))
	}.bind(this))
	.on('open_node.jstree', function(evt, data) {
		if (data.node.children.length === 1 && $('.workspace').jstree().get_node(data.node.children[0]).original === "Loading local branches...") {
			var parent = $('.workspace').jstree().get_node(data.node);
			var projectParent = $('.workspace').jstree().get_node(data.node.parent);
			
			var url = new UriBuilder().path(workspaceController.gitService.gitServiceUrl.split('/'))
					.path(workspaceController.selectedWorkspace)
					.path(projectParent.text)
					.path("branches").path("local").build();

			$('.workspace').jstree("delete_node", $('.workspace').jstree().get_node(data.node.children[0]));
			var position = 'last';

			workspaceController.http.get(url)
				.success(function(data) {
					data.local.forEach(function(branch) {
						var nodeText = branch.name + ': ' + branch.commitShortId + " " + branch.commitMessage + " " + "(" + branch.commitAuthor + " on " + branch.commitDate + ")";
						var newNode = { state: "open", "text": nodeText, "id": parent.id + "$" + branch.name, 
							"type": "branch", "name": branch.name, "current": branch.current, "project": projectParent.text,
							"icon": branch.current ? "fa fa-caret-right" : "fa fa-code-fork"};
						var child = $('.workspace').jstree("create_node", parent, newNode, position, false, false);
					})
				});
		} else if (data.node.children.length === 1 && $('.workspace').jstree().get_node(data.node.children[0]).original === "Loading remote branches...") {
			
			var parent = $('.workspace').jstree().get_node(data.node);
			var projectParent = $('.workspace').jstree().get_node(data.node.parent);
			
			var url = new UriBuilder().path(workspaceController.gitService.gitServiceUrl.split('/'))
					.path(workspaceController.selectedWorkspace)
					.path(projectParent.text)
					.path("branches").path("remote").build();
			$('.workspace').jstree("delete_node", $('.workspace').jstree().get_node(data.node.children[0]));
			var position = 'last';
			workspaceController.http.get(url)
				.success(function(data) {
					data.remote.forEach(function(branch) {
						var nodeText = branch.name + ': ' + branch.commitShortId + " " + branch.commitMessage + " " + "(" + branch.commitAuthor + " on " + branch.commitDate + ")";
						var newNode = { state: "open", "text": nodeText, "id": parent.id + "$" + branch.name,
							"type": "branch", "name": branch.name, "current": branch.current, "project": projectParent.text,
							"icon": "fa fa-code-fork"};
						var child = $('.workspace').jstree("create_node", parent, newNode, position, false, false);
					})
				});
		}
	})
	.on('close_node.jstree', function(evt, data) {
		//
	})
	.on('delete_node.jstree', function (e, data) {
		//this.deleteNode(data.node)
	}.bind(this))
	.on('jstree.workspace.pull', function (e, data) {
		workspaceController.selectedProject = (data.type === 'project') ? data.name : null;
		$('#pull').click();
	}.bind(this))
	.on('jstree.workspace.push', function (e, data) {		
		workspaceController.selectedProject = (data.type === 'project') ? data.name : null;
		$('#push').click();
	}.bind(this))
	.on('jstree.workspace.reset', function (e, data) {		
		workspaceController.selectedProject = (data.type === 'project') ? data.name : null;
		$('#reset').click();
	}.bind(this))
	.on('jstree.workspace.delete', function (e, data) {		
		workspaceController.selectedProject = (data.type === 'project') ? data.name : null;
		$('#delete').click();
	}.bind(this))
	.on('jstree.workspace.share', function (e, data) {		
		workspaceController.selectedProject = (data.type === 'project') ? data.name : null;
		$('#share').click();
	}.bind(this))
	.on('jstree.workspace.checkout', function (e, data) {		
		workspaceController.selectedProject = data.project;
		workspaceController.selectedBranch = data.branch;
		$('#checkout').click();
	}.bind(this))
	.on('jstree.workspace.commit', function (e, data) {		
		workspaceController.selectedProject = (data.type === 'project') ? data.name : null;
		$('#commit').click();
	}.bind(this));
	
	this.jstree = $.jstree.reference(jstree);	
	return this;
};

WorkspaceTreeAdapter.prototype.dblClickNode = function(node){
	// var type = node.original.type;
	// if(['folder','project'].indexOf(type)<0)
	// 	this.$messageHub.announceFileOpen(node.original._file);
}
WorkspaceTreeAdapter.prototype.clickNode = function(node){
	if (node.original._file && node.original._file.type === "project") {
		let projectName = node.original._file.name;
		let isGit = node.original._file.git;
		this.$messageHub.announceRepositorySelected(this.workspaceName, projectName, isGit);
	} else if (node.original.type === "working-tree") {
		let projectName = node.original.projectName;
		let isGit = node.original.isGit;
		this.$messageHub.announceRepositorySelected(this.workspaceName, projectName, isGit);
	} else if (node.original.type === "folder" || node.original.type === "file") {
		let projectName = node.original.projectName;
		let projectPath = projectName + "/";
		let path = node.original.path;
		let fileName = path.substring(path.indexOf(projectPath) + projectPath.length);
		let isGit = node.original.isGit;
		this.$messageHub.announceRepositoryFileSelected(this.workspaceName, projectName, isGit, fileName)
	}
	//this.$messageHub.announceFileSelected(node.original._file);
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
	return this.gitService.load(resourcepath)
			.then(function(_data){
				var data = _data;
				// var data = [];
				// if(_data.type == 'workspace'){
				// 	data = _data.projects;
				// } else if(_data.type == 'folder' || _data.type == 'project'){
				// 	data = [_data];
				// }

				data = data.map(this._buildTreeNode.bind(this));

				this.jstree.settings.core.data = data;
				this.jstree.refresh();
				// if(!this.jstree.settings.core.data || _data.type === 'workspace')
				// 	this.jstree.settings.core.data = data;
				// else{
				// 	//find and replace the loaded node
				// 	var self  = this;
				// 	this.jstree.settings.core.data = this.jstree.settings.core.data.map(function(node){
				// 		data.forEach(function(_node, replacement){
				// 			if(self._fnr(_node, replacement))
				// 				return;
				// 		}.bind(self, node));
				// 		return node;
				// 	});
				// }
				// if(!keepState)
				// 	this.jstree.refresh();
			}.bind(this));
};
WorkspaceTreeAdapter.prototype.pull = function(resource){
	return this.gitService.pull(resource.path)
		.then(function(){
			return this.$messageHub.announcePublish(resource);
		}.bind(this));
};
WorkspaceTreeAdapter.prototype.push = function(resource){
	return this.gitService.push(resource.path)
		.then(function(){
			return this.$messageHub.announcePublish(resource);
		}.bind(this));
};
WorkspaceTreeAdapter.prototype.reset = function(resource){
	return this.gitService.reset(resource.path)
		.then(function(){
			return this.$messageHub.announcePublish(resource);
		}.bind(this));
};
WorkspaceTreeAdapter.prototype.share = function(resource){
	return this.gitService.share(resource.path)
		.then(function(){
			return this.$messageHub.announcePublish(resource);
		}.bind(this));
};
WorkspaceTreeAdapter.prototype.checkout = function(resource){
	return this.gitService.checkout(resource.path)
		.then(function(){
			return this.$messageHub.announcePublish(resource);
		}.bind(this));
};
WorkspaceTreeAdapter.prototype.commit = function(resource){
	return this.gitService.commit(resource.path)
		.then(function(){
			return this.$messageHub.announcePublish(resource);
		}.bind(this));
};



/**
 * Git Service API delegate
 */
var GitService = function($http, gitServiceUrl, treeCfg){
	this.gitServiceUrl = gitServiceUrl;
	this.typeMapping = treeCfg['types'];
	this.$http = $http;
}
GitService.prototype.load = function(wsResourcePath){
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(wsResourcePath.split('/')).build();
	return this.$http.get(url, {headers: { 'describe': 'application/json'}})
			.then(function(response){
				return response.data;
			});
}
GitService.prototype.cloneProject = function(wsTree, workspace, repository, branch, username, password, projectName) {
	var gitBranch = branch ? branch : "master";
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path("clone").build();
	return this.$http.post(url, {
		"repository": repository,
		"branch": gitBranch,
		"publish": true,
		"username": username,
		"password": btoa(password),
		"projectName": projectName
	})
	.then(function(response) {
		wsTree.refresh();
		return response.data;
	});
}
GitService.prototype.pullAllProjects = function(wsTree, workspace, username, password, branch){
    var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path("pull").build();
	return this.$http.post(url, {
		"publish": true,
		"username": username,
		"password": btoa(password),
		"branch": branch
	})
	.then(function(response) {
		wsTree.refresh();
		return response.data;
	});
}
GitService.prototype.pullProject = function(wsTree, workspace, project, username, password, branch){
    var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("pull").build();
	return this.$http.post(url, {
		"publish": true,
		"username": username,
		"password": btoa(password),
		"branch": branch
	})
	.then(function(response) {
		wsTree.refresh();
		return response.data;
	});
}
GitService.prototype.pushAllProjects = function(wsTree, workspace, username, password, email){
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path("push").build();
	return this.$http.post(url, {
		"username": username,
		"password": btoa(password),
		"email": email
	})
	.then(function(response) {
		wsTree.refresh();
		return response.data;
	});
}
GitService.prototype.pushProject = function(wsTree, workspace, project, username, password, email, branch){
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("push").build();
	return this.$http.post(url, {
		"username": username,
		"password": btoa(password),
		"email": email,
		"branch": branch,
	})
	.then(function(response) {
		wsTree.refresh();
		return response.data;
	});
}
GitService.prototype.resetProject = function(wsTree, workspace, project){
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("reset").build();
	return this.$http.post(url, {})
	.then(function(response){
		wsTree.refresh();
		return response.data;
	});
}
GitService.prototype.deleteRepository = function(wsTree, workspace, repositoryName){
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(repositoryName).path("delete").build();
	return this.$http.delete(url)
	.then(function(response){
		wsTree.refresh();
		return response.data;
	});
}
GitService.prototype.shareProject = function(wsTree, workspace, project, repository, branch, commitMessage, username, password, email){
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("share").build();
	return this.$http.post(url, {
		"project": project,
		"repository": repository,
		"branch": branch,
		"commitMessage": commitMessage,
		"username": username,
		"password": btoa(password),
		"email": email,
	})
	.then(function(response){
		wsTree.refresh();
		return response.data;
	});
}
GitService.prototype.checkoutBranch = function(wsTree, workspace, project, branch, username, password){
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("checkout").build();
	return this.$http.post(url, {
		"project": project,
		"branch": branch,
		"username": username,
		"password": btoa(password)
	})
	.then(function(response){
		wsTree.refresh();
		return response.data;
	});
}
GitService.prototype.commitProject = function(wsTree, workspace, project, commitMessage, username, password, email, branch){
	var url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path("commit").build();
	return this.$http.post(url, {
		"commitMessage": commitMessage,
		"username": username,
		"password": btoa(password),
		"email": email,
		"branch": branch
	})
	.then(function(response){
		wsTree.refresh();
		return response.data;
	});
}

/**
 * Env Service API delegate
 */
var EnvService = function($http, envServiceUrl){
	this.envServiceUrl = envServiceUrl;
	this.$http = $http;
};
EnvService.prototype.setEnv = function(env) {
	return this.$http.post(this.envServiceUrl, {
		"env": env,
	});
};

angular.module('workspace.config', [])
	.constant('WS_SVC_URL','../../../../../services/v4/ide/workspaces')
	.constant('WS_SVC_MANAGER_URL','../../../../../services/v4/ide/workspace')
	.constant('GIT_SVC_URL','../../../../../services/v4/ide/git')
	.constant('ENV_SVC_URL','../../../../../services/v4/js/ide-git/services/env.js');
	
angular.module('workspace', ['workspace.config', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])
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
			}
			data = JSON.stringify(data);
		}
		return data;
	});
	$httpProvider.interceptors.push('httpRequestInterceptor');
}])
.factory('$messageHub', [function(){
	var messageHub = new FramesMessageHub();	
	var message = function(evtName, data){
		messageHub.post({data: data}, 'workspace.' + evtName);
	};
	var announceFileSelected = function(fileDescriptor){
		this.message('file.selected', fileDescriptor);
	};
	var announceFileCreated = function(fileDescriptor){
		this.message('file.created', fileDescriptor);
	};
	var announceFileOpen = function(fileDescriptor){
		this.message('file.open', fileDescriptor);
	};
	var announcePull = function(fileDescriptor){
		this.message('file.pull', fileDescriptor);
	};
	var announceRepositorySelected = function(workspace, project, isGitProject){
		messageHub.post({data: {"workspace": workspace, "project": project, "isGitProject": isGitProject}}, 'git.repository.selected');
	};
	var announceRepositoryFileSelected = function(workspace, project, isGitProject, file){
		messageHub.post({data: {"workspace": workspace, "project": project, "isGitProject": isGitProject, "file": file}}, 'git.repository.file.selected');
	};
	return {
		message: message,
		announceFileSelected: announceFileSelected,
		announceFileCreated: announceFileCreated,
		announceFileOpen: announceFileOpen,
		announcePull: announcePull,
		announceRepositorySelected: announceRepositorySelected,
		announceRepositoryFileSelected: announceRepositoryFileSelected,
		on: function(evt, cb){
			messageHub.subscribe(cb, evt);
		}
	};
}])
.factory('$treeConfig', [function(){
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
				var ctxmenu = {};
				if (this.get_type(node) === "project") {
					if (node.original._file.git) {
						ctxmenu.commit = {
							"separator_before": false,
							"label": "Commit",
							"action": function(data){
								var tree = $.jstree.reference(data.reference);
								var node = tree.get_node(data.reference);
								tree.element.trigger('jstree.workspace.commit', [node.original._file]);
							}.bind(this)
						};
						ctxmenu.pull = {
							"separator_before": false,
							"label": "Pull",
							"action": function(data){
								var tree = $.jstree.reference(data.reference);
								var node = tree.get_node(data.reference);
								tree.element.trigger('jstree.workspace.pull', [node.original._file]);
							}.bind(this)
						};
						ctxmenu.push = {
							"separator_before": false,
							"label": "Push",
							"action": function(data){
								var tree = $.jstree.reference(data.reference);
								var node = tree.get_node(data.reference);
								tree.element.trigger('jstree.workspace.push', [node.original._file]);
							}.bind(this)
						};
						ctxmenu.reset = {
							"separator_before": false,
							"label": "Reset",
							"action": function(data){
								var tree = $.jstree.reference(data.reference);
								var node = tree.get_node(data.reference);
								tree.element.trigger('jstree.workspace.reset', [node.original._file]);
							}.bind(this)
						};
						ctxmenu.delete = {
							"separator_before": true,
							"label": "Delete",
							"action": function(data){
								var tree = $.jstree.reference(data.reference);
								var node = tree.get_node(data.reference);
								tree.element.trigger('jstree.workspace.delete', [node.original._file]);
							}.bind(this)
						};
					} else {
						ctxmenu.share = {
							"separator_before": false,
							"label": "Share",
							"action": function(data){
								var tree = $.jstree.reference(data.reference);
								var node = tree.get_node(data.reference);
								tree.element.trigger('jstree.workspace.share', [node.original._file]);
							}.bind(this)
						};
					}
				} else if (node.original.type === "branch" && !node.original.current) {
					ctxmenu.checkout = {
							"separator_before": false,
							"label": "Checkout",
							"action": function(data){
								var tree = $.jstree.reference(data.reference);
								var node = tree.get_node(data.reference);
								tree.element.trigger('jstree.workspace.checkout', [
									{"project": node.original.project, "branch": node.original.name}]);
							}.bind(this)
						};
				}
				
				return ctxmenu;
			}
		}
	}
}])
.factory('gitService', ['$http', 'GIT_SVC_URL', '$treeConfig', function($http, GIT_SVC_URL, $treeConfig){
	return new GitService($http, GIT_SVC_URL, $treeConfig);
}])
.factory('envService', ['$http', 'ENV_SVC_URL', function($http, ENV_SVC_URL){
	return new EnvService($http, ENV_SVC_URL);
}])
.factory('workspaceService', ['$http', 'WS_SVC_MANAGER_URL', 'WS_SVC_URL', '$treeConfig', function($http, WS_SVC_MANAGER_URL, WS_SVC_URL, $treeConfig){
	return new WorkspaceService($http, WS_SVC_MANAGER_URL, WS_SVC_URL, $treeConfig);
}])
.factory('workspaceTreeAdapter', ['$http', '$treeConfig', 'workspaceService', 'gitService', '$messageHub', function($http, $treeConfig, WorkspaceService, GitService, $messageHub){
	return new WorkspaceTreeAdapter($http, $treeConfig, WorkspaceService, GitService, $messageHub);
}])
.controller('WorkspaceController', ['workspaceService', 'workspaceTreeAdapter', 'gitService', 'envService', '$messageHub', '$http', function (workspaceService, workspaceTreeAdapter, gitService, envService, $messageHub, $http) {

	this.wsTree;
	this.workspaces;
	this.selectedWorkspace;
	this.gitService = gitService;
	this.http = $http;
	
	workspaceService.listWorkspaceNames()
		.then(function(workspaceNames) {
			this.workspaces = workspaceNames;
			var storedWorkspace = JSON.parse(localStorage.getItem('DIRIGIBLE.workspace'));
			if (storedWorkspace !== null) {
				this.selectedWorkspace = storedWorkspace.name;
				this.workspaceSelected();
			} else if(this.workspaces[0]) {
				this.selectedWorkspace = this.workspaces[0];
				this.workspaceSelected();					
			} 
		}.bind(this));
	
	this.workspaceSelected = function(){
		if (this.wsTree) {
			this.wsTree.workspaceName = this.selectedWorkspace;
			localStorage.setItem('DIRIGIBLE.workspace', JSON.stringify({"name": this.selectedWorkspace}));
			this.wsTree.refresh();
			return;
		}
		this.wsTree = workspaceTreeAdapter.init($('.workspace'), this.selectedWorkspace, this, gitService);
		if(!workspaceService.typeMapping)
			workspaceService.typeMapping = $treeConfig[types];
		this.wsTree.refresh();
	};
	
	this.okClone = function() {
		if (this.clone.url) {
			gitService.cloneProject(this.wsTree, this.selectedWorkspace, this.clone.url, this.branch, this.username, this.password, this.projectName);
		}
	};

	this.okPullAll = function() {
		gitService.pullAllProjects(this.wsTree, this.selectedWorkspace, this.username, this.password, this.branch);
	};

	this.okPull = function() {
		gitService.pullProject(this.wsTree, this.selectedWorkspace, this.selectedProject, this.username, this.password, this.branch);
	};
	
	this.okPushAll = function() {
		gitService.pushAllProjects(this.wsTree, this.selectedWorkspace, this.username, this.password, this.email);
	};

	this.okPush = function() {
		gitService.pushProject(this.wsTree, this.selectedWorkspace, this.selectedProject, this.username, this.password, this.email, this.branch);
	};
	
	this.okReset = function() {
		gitService.resetProject(this.wsTree, this.selectedWorkspace, this.selectedProject, this.username, this.password, this.branch);
	};

	this.okDelete = function() {
		gitService.deleteRepository(this.wsTree, this.selectedWorkspace, this.selectedProject);
	};

	this.okShare = function() {
		gitService.shareProject(this.wsTree, this.selectedWorkspace, this.selectedProject, this.repository, this.branch, this.commitMessage, this.username, this.password, this.email);
	};

	this.okCheckout = function() {
		gitService.checkoutBranch(this.wsTree, this.selectedWorkspace, this.selectedProject, this.selectedBranch, this.username, this.password);
	};

	this.okCommit = function() {
		gitService.commitProject(this.wsTree, this.selectedWorkspace, this.selectedProject, this.commitMessage, this.username, this.password, this.email, this.branch);
	};
	
	this.refresh = function(){
		this.wsTree.refresh();
	};
	
	$messageHub.on('git.repository.run', function(msg) {
		if (msg.data.env) {
			envService.setEnv(msg.data.env);
		}
		gitService.cloneProject(this.wsTree, this.selectedWorkspace, msg.data.repository, msg.data.branch, msg.data.username, msg.data.password, msg.data.projectName, msg.data.branch);
		if (msg.data.uri) {
			run();
		}
		
		function sleep(ms) {
		  return new Promise(resolve => setTimeout(resolve, ms));
		}
			
		async function run() {
		  await sleep(2000);
		  window.open(msg.data.uri, '_parent');
		}
	}.bind(this));
	
}]);
