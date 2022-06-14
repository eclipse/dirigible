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
};
UriBuilder.prototype.path = function (_pathSegments) {
	if (!Array.isArray(_pathSegments)) _pathSegments = [_pathSegments];
	_pathSegments = _pathSegments
		.filter(function (segment) {
			return segment;
		})
		.map(function (segment) {
			if (segment.length) {
				if (segment.charAt(segment.length - 1) === '/') segment = segment.substring(0, segment.length - 2);
				segment = encodeURIComponent(segment);
			}
			return segment;
		});
	this.pathSegments = this.pathSegments.concat(_pathSegments);
	return this;
};
UriBuilder.prototype.build = function (isBasePath = true) {
	if (isBasePath) return '/' + this.pathSegments.join('/');
	return this.pathSegments.join('/');
};

/**
 * Workspace Service API delegate
 */
let WorkspaceService = function ($http, workspaceManagerServiceUrl, workspacesServiceUrl, treeCfg) {
	this.workspaceManagerServiceUrl = workspaceManagerServiceUrl;
	this.workspacesServiceUrl = workspacesServiceUrl;
	this.typeMapping = treeCfg['types'];
	this.$http = $http;

	this.newFileName = function (name, type, siblingFilenames) {
		type = type || 'default';
		//check for custom new file name template in the global configuration
		if (type && this.typeMapping[type] && this.typeMapping[type].template_new_name) {
			let nameIncrementRegex = this.typeMapping[type].name_increment_regex;
			siblingFilenames = siblingFilenames || [];
			let suffix = nextIncrementSegment(siblingFilenames, name, nameIncrementRegex);
			suffix = suffix < 0 ? ' ' : suffix;
			let parameters = {
				'{name}': name || 'file',
				'{ext}': this.typeMapping[type].ext,
				'{increment}': '-' + suffix
			};
			let tmpl = this.typeMapping[type].template_new_name;
			let regex = new RegExp(Object.keys(parameters).join('|'), 'g');
			let fName = tmpl.replace(regex, function (m) {
				return parameters[m] !== undefined ? parameters[m] : m;
			});
			name = fName.trim();
		}
		return name;
	};

	let startsWith = function (stringToTest, prefixToTest) {
		let startsWithRegEx = new RegExp('^' + prefixToTest);
		let matches = stringToTest.match(startsWithRegEx);
		return matches != null && matches.length > 0;
	};

	let strictInt = function (value) {
		if (/^(\-|\+)?([0-9]+|Infinity)$/.test(value)) return Number(value);
		return NaN;
	};

	let toInt = function (value) {
		if (value === undefined) return;
		let _result = value.trim();
		_result = strictInt(_result);
		if (isNaN(_result)) _result = undefined;
		return _result;
	};

	//processes an array of sibling string filenames to calculate the next incrmeent suffix segment
	let nextIncrementSegment = function (filenames, filenameToMatch, nameIncrementRegex) {
		let maxIncrement = filenames
			.map(function (siblingFilename) {
				//find out incremented file name matches (such as {file-name} {i}.{extension} or {file-name}-{i}.{extension})
				let incr = -2;
				//in case we have a regex configured to find out the increment direclty, use it
				if (nameIncrementRegex) {
					let regex = new Regex(nameIncrementRegex);
					let result = siblingFilename.match(regex);
					if (result !== null) {
						incr = toInt(result[0]);
					}
				} else {
					//try heuristics
					let regex = /(.*?)(\.[^.]*$|$)/;
					let siblingTextSegments = siblingFilename.match(regex); //matches filename and extension segments of a filename
					let siblingTextFileName = siblingTextSegments[1];
					let siblingTextExtension = siblingTextSegments[2];
					let nodeTextSegments = filenameToMatch.match(regex);
					let nodeTextFileName = nodeTextSegments[1];
					let nodeTextExtension = nodeTextSegments[2];
					if (siblingTextExtension === nodeTextExtension) {
						if (siblingTextFileName === nodeTextFileName) return -1;
						if (startsWith(siblingTextFileName, nodeTextFileName)) {
							//try to figure out the increment segment from the name part. Starting backwards, exepcts that the increment is the last numeric segment in the name
							let _inc = '';
							for (let i = siblingTextFileName.length - 1; i > -1; i--) {
								let code = siblingTextFileName.charCodeAt(i);
								if (
									code < 48 ||
									code > 57 //decimal numbers only
								)
									break;
								_inc = siblingTextFileName[i] + _inc;
							}
							if (_inc) {
								incr = toInt(_inc);
							}
						}
					}
				}
				return incr;
			})
			.sort(function (a, b) {
				return a - b;
			})
			.pop();
		return ++maxIncrement;
	};
};
WorkspaceService.prototype.load = function (wsResourcePath) {
	let url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).path(wsResourcePath.split('/')).build();
	return this.$http.get(url, { headers: { describe: 'application/json' } }).then(function (response) {
		return response.data;
	});
};
WorkspaceService.prototype.listWorkspaceNames = function () {
	let url = new UriBuilder().path(this.workspacesServiceUrl.split('/')).build();
	return this.$http.get(url).then(function (response) {
		return response.data;
	});
};

/**
 * Workspace Tree Adapter mediating the workspace service REST api and the jst tree componet working with it
 */
let WorkspaceTreeAdapter = function ($http, treeConfig, workspaceSvc, gitService, $messageHub) {
	this.treeConfig = treeConfig;
	this.gitService = gitService;
	this.workspaceSvc = workspaceSvc;
	this.$http = $http;
	this.$messageHub = $messageHub;

	this.mapWorkingDir = function (rootFolder, projectName) {
		let workingDir = {
			text: rootFolder.name,
			type: 'folder',
			icon: 'fa fa-folder-o',
			path: rootFolder.path,
			projectName: projectName,
			isGit: true,
			children: []
		};
		for (let i = 0; i < rootFolder.folders.length; i++) {
			let folder = rootFolder.folders[i];
			workingDir.children.push(this.mapWorkingDir(folder, projectName));
		}
		for (let i = 0; i < rootFolder.files.length; i++) {
			let file = rootFolder.files[i];
			workingDir.children.push({
				text: file.name,
				type: 'file',
				icon: 'fa fa-file-o',
				path: rootFolder.path + '/' + file.name,
				projectName: projectName,
				isGit: true
			});
		}
		return workingDir;
	};

	this._buildTreeNode = function (f) {
		let children = [];
		// if(f.type=='folder' || f.type=='project'){
		if (f.type == 'project' && f.git) {
			// children = f.folders.map(this._buildTreeNode.bind(this));
			// var _files = f.files.map(this._buildTreeNode.bind(this))
			// children = children.concat(_files);

			let projectName = f.name;
			let workingDir = [];
			for (let i = 0; i < f.folders.length; i++) {
				let folder = f.folders[i];
				workingDir.push(this.mapWorkingDir(folder, projectName));
			}

			for (let i = 0; i < f.files.length; i++) {
				let file = f.files[i];
				workingDir.push({
					text: file.name,
					type: 'file',
					icon: 'fa fa-file-o',
					projectName: projectName,
					path: f.path + '/' + file.name,
					isGit: true
				});
			}

			children = [
				{
					text: 'local',
					type: 'local',
					icon: 'fa fa-check-circle-o',
					children: ['Loading local branches...']
				},
				{ text: 'remote', type: 'remote', icon: 'fa fa-circle-o', children: ['Loading remote branches...'] },
				{
					text: 'working tree',
					type: 'working-tree',
					icon: 'fa fa-clone',
					children: workingDir,
					projectName: projectName,
					isGit: true
				}
			];
		}
		let icon;
		if (f.type == 'project') {
			icon = f.git ? 'fa fa-git-square' : 'fa fa-hdd-o';
		}

		f.label = f.name;
		return {
			text: f.name,
			children: children,
			type: f.type,
			git: f.git,
			icon: icon,
			_file: f
		};
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
			if (done) return true;
			node.children.forEach(
				function (c) {
					return this._fnr(c, replacement);
				}.bind(this)
			);
		}
		return;
	};
};

WorkspaceTreeAdapter.prototype.init = function (containerEl, workspaceName, workspaceController, gitService) {
	this.containerEl = containerEl;
	this.workspaceName = workspaceName;
	this.gitService = gitService;

	let self = this;
	let jstree = this.containerEl.jstree(this.treeConfig);

	//subscribe event listeners
	jstree
		.on(
			'select_node.jstree',
			function (e, data) {
				if (data.node.type === 'project') {
					workspaceController.selectedProject = data.node.text;
				} else if (data.node.type === 'local' || data.node.type === 'remote') {
					workspaceController.selectedProject = data.node.parent.text;
				}

				this.clickNode(this.jstree.get_node(data.node));
			}.bind(this)
		)
		.on(
			'dblclick.jstree',
			function (evt) {
				this.dblClickNode(this.jstree.get_node(evt.target));
			}.bind(this)
		)
		.on('open_node.jstree', function (evt, data) {
			if (
				data.node.children.length === 1 &&
				$('.workspace').jstree().get_node(data.node.children[0]).original === 'Loading local branches...'
			) {
				let parent = $('.workspace').jstree().get_node(data.node);
				let projectParent = $('.workspace').jstree().get_node(data.node.parent);

				let url = new UriBuilder()
					.path(workspaceController.gitService.gitServiceUrl.split('/'))
					.path(workspaceController.selectedWorkspace)
					.path(projectParent.text)
					.path('branches')
					.path('local')
					.build();

				$('.workspace').jstree('delete_node', $('.workspace').jstree().get_node(data.node.children[0]));
				let position = 'last';

				workspaceController.http.get(url).then(function (response) {
					response.data.local.forEach(function (branch) {
						let nodeText =
							branch.name +
							': ' +
							branch.commitShortId +
							' ' +
							branch.commitMessage +
							' ' +
							'(' +
							branch.commitAuthor +
							' on ' +
							branch.commitDate +
							')';
						let newNode = {
							state: 'open',
							text: nodeText,
							id: parent.id + '$' + branch.name,
							type: 'branch',
							name: branch.name,
							current: branch.current,
							project: projectParent.text,
							icon: branch.current ? 'fa fa-caret-right' : 'fa fa-code-fork'
						};
						let child = $('.workspace').jstree('create_node', parent, newNode, position, false, false);
					});
				});
			} else if (
				data.node.children.length === 1 &&
				$('.workspace').jstree().get_node(data.node.children[0]).original === 'Loading remote branches...'
			) {
				let parent = $('.workspace').jstree().get_node(data.node);
				let projectParent = $('.workspace').jstree().get_node(data.node.parent);

				let url = new UriBuilder()
					.path(workspaceController.gitService.gitServiceUrl.split('/'))
					.path(workspaceController.selectedWorkspace)
					.path(projectParent.text)
					.path('branches')
					.path('remote')
					.build();
				$('.workspace').jstree('delete_node', $('.workspace').jstree().get_node(data.node.children[0]));
				let position = 'last';
				workspaceController.http.get(url).then(function (response) {
					response.data.remote.forEach(function (branch) {
						let nodeText =
							branch.name +
							': ' +
							branch.commitShortId +
							' ' +
							branch.commitMessage +
							' ' +
							'(' +
							branch.commitAuthor +
							' on ' +
							branch.commitDate +
							')';
						let newNode = {
							state: 'open',
							text: nodeText,
							id: parent.id + '$' + branch.name,
							type: 'branch',
							name: branch.name,
							current: branch.current,
							project: projectParent.text,
							icon: 'fa fa-code-fork'
						};
						let child = $('.workspace').jstree('create_node', parent, newNode, position, false, false);
					});
				});
			}
		})
		.on('close_node.jstree', function (evt, data) {
			//
		})
		.on(
			'delete_node.jstree',
			function (e, data) {
				//this.deleteNode(data.node)
			}.bind(this)
		)
		.on(
			'jstree.workspace.pull',
			function (e, data) {
				workspaceController.selectedProject = data.type === 'project' ? data.name : null;
				$('#pull').click();
			}.bind(this)
		)
		.on(
			'jstree.workspace.push',
			function (e, data) {
				workspaceController.selectedProject = data.type === 'project' ? data.name : null;
				$('#push').click();
			}.bind(this)
		)
		.on(
			'jstree.workspace.reset',
			function (e, data) {
				workspaceController.selectedProject = data.type === 'project' ? data.name : null;
				$('#reset').click();
			}.bind(this)
		)
		.on(
			'jstree.workspace.import',
			function (e, data) {
				workspaceController.selectedProject = data.type === 'project' ? data.name : null;
				$('#import').click();
			}.bind(this)
		)
		.on(
			'jstree.workspace.delete',
			function (e, data) {
				workspaceController.selectedProjectData = data.type === 'project' ? data : null;
				workspaceController.selectedProject = data.type === 'project' ? data.name : null;
				workspaceController.showDeleteDialog();
			}.bind(this)
		)
		.on(
			'jstree.workspace.share',
			function (e, data) {
				workspaceController.selectedProject = data.type === 'project' ? data.name : null;
				$('#share').click();
			}.bind(this)
		)
		.on(
			'jstree.workspace.checkout',
			function (e, data) {
				workspaceController.selectedProject = data.project;
				workspaceController.selectedBranch = data.branch;
				$('#checkout').click();
			}.bind(this)
		)
		.on(
			'jstree.workspace.commit',
			function (e, data) {
				workspaceController.selectedProject = data.type === 'project' ? data.name : null;
				$('#commit').click();
			}.bind(this)
		);

	this.jstree = $.jstree.reference(jstree);
	return this;
};

WorkspaceTreeAdapter.prototype.dblClickNode = function (node) {
	// var type = node.original.type;
	// if(['folder','project'].indexOf(type)<0)
	// 	this.$messageHub.announceFileOpen(node.original._file);
};
WorkspaceTreeAdapter.prototype.clickNode = function (node) {
	if (node.original._file && node.original._file.type === 'project') {
		let projectName = node.original._file.name;
		let isGit = node.original._file.git;
		this.$messageHub.announceRepositorySelected(this.workspaceName, projectName, isGit);
	} else if (node.original.type === 'working-tree') {
		let projectName = node.original.projectName;
		let isGit = node.original.isGit;
		this.$messageHub.announceRepositorySelected(this.workspaceName, projectName, isGit);
	} else if (node.original.type === 'folder' || node.original.type === 'file') {
		let projectName = node.original.projectName;
		let projectPath = projectName + '/';
		let path = node.original.path;
		let fileName = path.substring(path.indexOf(projectPath) + projectPath.length);
		let isGit = node.original.isGit;
		this.$messageHub.announceRepositoryFileSelected(this.workspaceName, projectName, isGit, fileName);
	}
	//this.$messageHub.announceFileSelected(node.original._file);
};
WorkspaceTreeAdapter.prototype.raw = function () {
	return this.jstree;
};
WorkspaceTreeAdapter.prototype.refresh = function (node, keepState, $messageHub) {
	let messageHub = new FramesMessageHub();
	//TODO: This is reliable but a bit intrusive. Find out a more subtle way to update on demand
	let resourcepath;
	if (node) {
		resourcepath = node.original._file.path;
	} else {
		resourcepath = this.workspaceName;
	}
	return this.gitService.load(resourcepath).then(
		function (_data) {
			let data = _data;
			// var data = [];
			// if(_data.type == 'workspace'){
			// 	data = _data.projects;
			// } else if(_data.type == 'folder' || _data.type == 'project'){
			// 	data = [_data];
			// }

			data = data.map(this._buildTreeNode.bind(this));
			messageHub.post({ data: 'clone.project' }, 'workspace.action.complete');
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
		}.bind(this)
	);
};
WorkspaceTreeAdapter.prototype.pull = function (resource) {
	return this.gitService.pull(resource.path).then(
		function () {
			return this.$messageHub.announcePublish(resource);
		}.bind(this)
	);
};
WorkspaceTreeAdapter.prototype.push = function (resource) {
	return this.gitService.push(resource.path).then(
		function () {
			return this.$messageHub.announcePublish(resource);
		}.bind(this)
	);
};
WorkspaceTreeAdapter.prototype.reset = function (resource) {
	return this.gitService.reset(resource.path).then(
		function () {
			return this.$messageHub.announcePublish(resource);
		}.bind(this)
	);
};
WorkspaceTreeAdapter.prototype.share = function (resource) {
	return this.gitService.share(resource.path).then(
		function () {
			return this.$messageHub.announcePublish(resource);
		}.bind(this)
	);
};
WorkspaceTreeAdapter.prototype.checkout = function (resource) {
	return this.gitService.checkout(resource.path).then(
		function () {
			return this.$messageHub.announcePublish(resource);
		}.bind(this)
	);
};
WorkspaceTreeAdapter.prototype.commit = function (resource) {
	return this.gitService.commit(resource.path).then(
		function () {
			return this.$messageHub.announcePublish(resource);
		}.bind(this)
	);
};

/**
 * Git Service API delegate
 */
let GitService = function ($http, $messageHub, gitServiceUrl, treeCfg) {
	this.gitServiceUrl = gitServiceUrl;
	this.typeMapping = treeCfg['types'];
	this.$http = $http;
	this.$messageHub = $messageHub;
	this.listOfProjects = [];
};
GitService.prototype.load = function (wsResourcePath) {
	let messageHub = this.$messageHub;
	let myGitService = this;
	let url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(wsResourcePath.split('/')).build();
	return this.$http.get(url, { headers: { describe: 'application/json' } }).then(
		function (response) {
			myGitService.listProjects(response.data);
			return response.data;
		},
		function (response) {
			let errorMessage = JSON.parse(response.data.error).message;
			messageHub.announceAlertError('Loading Git Repositories Error', errorMessage);
		}
	);
};
GitService.prototype.listProjects = function (data) {
	let known_names = [];
	for (let i = 0; i < data.length; i++) {
		let val = data[i];
		if (val.type && val.type === 'project')
			known_names.push(val.name);
	}
	this.listOfProjects = known_names;
	return known_names;
};
GitService.prototype.cloneProject = function (
	wsTree,
	workspace,
	repository,
	branch = '',
	username,
	password
) {
	let messageHub = this.$messageHub;
	let gitBranch = branch;
	let url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path('clone').build();
	return this.$http
		.post(url, {
			repository: repository,
			branch: gitBranch,
			publish: true,
			username: username,
			password: btoa(password)
		})
		.then(
			function (response) {
				wsTree.refresh();
				return response.data;
			},
			function (response) {
				wsTree.refresh();
				let errorMessage = JSON.parse(response.data.error).message;
				messageHub.announceAlertError('Git Clone Error', errorMessage);
			}
		);
};
GitService.prototype.pullManyProjects = function (wsTree, workspace, username, password, branch, projects2pull) {
	for (let i = 0; i < projects2pull.length; i++) {
		this.pullProject(wsTree, workspace, projects2pull[i], username, password, branch);
	}
};
GitService.prototype.pullProject = function (wsTree, workspace, project, username, password, branch) {
	let messageHub = this.$messageHub;
	let url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path('pull').build();
	return this.$http
		.post(url, {
			publish: true,
			username: username,
			password: btoa(password),
			branch: branch
		})
		.then(
			function (response) {
				wsTree.refresh();
				return response.data;
			},
			function (response) {
				wsTree.refresh();
				let errorMessage = JSON.parse(response.data.error).message;
				messageHub.announceAlertError('Git Pull Project Error', errorMessage, 'error');
			}
		);
};
GitService.prototype.pushAllProjects = function (wsTree, workspace, username, password, email) {
	let messageHub = this.$messageHub;
	let url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path('push').build();
	return this.$http
		.post(url, {
			username: username,
			password: btoa(password),
			email: email
		})
		.then(
			function (response) {
				wsTree.refresh();
				return response.data;
			},
			function (response) {
				wsTree.refresh();
				let errorMessage = JSON.parse(response.data.error).message;
				messageHub.announceAlertError('Git Push All Projects Error', errorMessage);
			}
		);
};
GitService.prototype.pushProject = function (wsTree, workspace, project, username, password, email, branch) {
	let messageHub = this.$messageHub;
	let url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path('push').build();
	return this.$http
		.post(url, {
			username: username,
			password: btoa(password),
			email: email,
			branch: branch
		})
		.then(
			function (response) {
				wsTree.refresh();
				return response.data;
			},
			function (response) {
				wsTree.refresh();
				let errorMessage = JSON.parse(response.data.error).message;
				messageHub.announceAlertError('Git Push Project Error', errorMessage);
			}
		);
};
GitService.prototype.resetProject = function (wsTree, workspace, project) {
	let messageHub = this.$messageHub;
	let url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path('reset').build();
	return this.$http.post(url, {}).then(
		function (response) {
			wsTree.refresh();
			return response.data;
		},
		function (response) {
			wsTree.refresh();
			let errorMessage = JSON.parse(response.data.error).message;
			messageHub.announceAlertError('Git Reset Project Error', errorMessage);
		}
	);
};
GitService.prototype.importProjects = function (wsTree, workspace, repository) {
	let messageHub = this.$messageHub;
	let url = new UriBuilder()
		.path(this.gitServiceUrl.split('/'))
		.path(workspace)
		.path(repository)
		.path('import')
		.build();
	return this.$http.post(url, {}).then(
		function (response) {
			wsTree.refresh();
			return response.data;
		},
		function (response) {
			wsTree.refresh();
			let errorMessage = JSON.parse(response.data.error).message;
			messageHub.announceAlertError('Git Import Projects Error', errorMessage);
		}
	);
};
GitService.prototype.deleteRepository = function (wsTree, workspace, repositoryName) {
	let messageHub = this.$messageHub;
	let url = new UriBuilder()
		.path(this.gitServiceUrl.split('/'))
		.path(workspace)
		.path(repositoryName)
		.path('delete')
		.build();
	return this.$http.delete(url).then(
		function (response) {
			wsTree.refresh();
			return response.data;
		},
		function (response) {
			wsTree.refresh();
			let errorMessage = JSON.parse(response.data.error).message;
			messageHub.announceAlertError('Git Delete Project Error', errorMessage);
		}
	);
};
GitService.prototype.shareProject = function (
	wsTree,
	workspace,
	project,
	repository,
	branch,
	commitMessage,
	username,
	password,
	email
) {
	let messageHub = this.$messageHub;
	let url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path('share').build();
	return this.$http
		.post(url, {
			project: project,
			repository: repository,
			branch: branch,
			commitMessage: commitMessage,
			username: username,
			password: btoa(password),
			email: email
		})
		.then(
			function (response) {
				wsTree.refresh();
				return response.data;
			},
			function (response) {
				wsTree.refresh();
				let errorMessage = JSON.parse(response.data.error).message;
				messageHub.announceAlertError('Git Share Project Error', errorMessage);
			}
		);
};
GitService.prototype.checkoutBranch = function (wsTree, workspace, project, branch, username, password) {
	let messageHub = this.$messageHub;
	let url = new UriBuilder()
		.path(this.gitServiceUrl.split('/'))
		.path(workspace)
		.path(project)
		.path('checkout')
		.build();
	return this.$http
		.post(url, {
			project: project,
			branch: branch,
			username: username,
			password: btoa(password)
		})
		.then(
			function (response) {
				wsTree.refresh();
				return response.data;
			},
			function (response) {
				wsTree.refresh();
				let errorMessage = JSON.parse(response.data.error).message;
				messageHub.announceAlertError('Git Checkout Branch Error', errorMessage);
			}
		);
};
GitService.prototype.commitProject = function (
	wsTree,
	workspace,
	project,
	commitMessage,
	username,
	password,
	email,
	branch
) {
	let messageHub = this.$messageHub;
	let url = new UriBuilder().path(this.gitServiceUrl.split('/')).path(workspace).path(project).path('commit').build();
	return this.$http
		.post(url, {
			commitMessage: commitMessage,
			username: username,
			password: btoa(password),
			email: email,
			branch: branch
		})
		.then(
			function (response) {
				wsTree.refresh();
				return response.data;
			},
			function (response) {
				wsTree.refresh();
				let errorMessage = JSON.parse(response.data.error).message;
				messageHub.announceAlertError('Git Commit Error', errorMessage);
			}
		);
};

/**
 * Env Service API delegate
 */
let EnvService = function ($http, envServiceUrl) {
	this.envServiceUrl = envServiceUrl;
	this.$http = $http;
};
EnvService.prototype.setEnv = function (env) {
	return this.$http.post(this.envServiceUrl, {
		env: env
	});
};

angular
	.module('workspace.config', [])
	.constant('WS_SVC_URL', '/services/v4/ide/workspaces')
	.constant('WS_SVC_MANAGER_URL', '/services/v4/ide/workspace')
	.constant('GIT_SVC_URL', '/services/v4/ide/git')
	.constant('PUBLISH_SVC_URL', '/services/v4/ide/publisher/request')
	.constant('ENV_SVC_URL', '/services/v4/js/ide-git/services/env.js');

angular
	.module('workspace', ['workspace.config', 'ngAnimate', 'ngSanitize', 'ui.bootstrap'])
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
	.config([
		'$httpProvider',
		function ($httpProvider) {
			//check if response is error. errors currently are non-json formatted and fail too early
			$httpProvider.defaults.transformResponse.unshift(function (data, headersGetter, status) {
				if (status > 399) {
					data = {
						error: data
					};
					data = JSON.stringify(data);
				}
				return data;
			});
			$httpProvider.interceptors.push('httpRequestInterceptor');
		}
	])
	.factory('$messageHub', [
		function () {
			let messageHub = new FramesMessageHub();
			let message = function (evtName, data) {
				messageHub.post({ data: data }, 'workspace.' + evtName);
			};
			let announceFileSelected = function (fileDescriptor) {
				this.message('file.selected', fileDescriptor);
			};
			let announceFileCreated = function (fileDescriptor) {
				this.message('file.created', fileDescriptor);
			};
			let announceFileOpen = function (fileDescriptor) {
				this.message('file.open', fileDescriptor);
			};
			let announcePull = function (fileDescriptor) {
				this.message('file.pull', fileDescriptor);
			};
			let announceRepositorySelected = function (workspace, project, isGitProject) {
				messageHub.post(
					{ data: { workspace: workspace, project: project, isGitProject: isGitProject } },
					'git.repository.selected'
				);
			};
			let announceRepositoryFileSelected = function (workspace, project, isGitProject, file) {
				messageHub.post(
					{ data: { workspace: workspace, project: project, isGitProject: isGitProject, file: file } },
					'git.repository.file.selected'
				);
			};
			let announceAlert = function (title, message, type) {
				messageHub.post(
					{
						data: {
							title: title,
							message: message,
							type: type
						}
					},
					'ide.alert'
				);
			};
			let announceAlertSuccess = function (title, message) {
				announceAlert(title, message, 'success');
			};
			let announceAlertInfo = function (title, message) {
				announceAlert(title, message, 'info');
			};
			let announceAlertWarning = function (title, message) {
				announceAlert(title, message, 'warning');
			};
			let announceAlertError = function (title, message) {
				announceAlert(title, message, 'error');
			};
			let announceUnpublish = function (fileDescriptor) {
				this.message('file.unpublished', fileDescriptor);
			};
			return {
				message: message,
				announceFileSelected: announceFileSelected,
				announceFileCreated: announceFileCreated,
				announceFileOpen: announceFileOpen,
				announcePull: announcePull,
				announceRepositorySelected: announceRepositorySelected,
				announceRepositoryFileSelected: announceRepositoryFileSelected,
				announceAlert: announceAlert,
				announceAlertSuccess: announceAlertSuccess,
				announceAlertInfo: announceAlertInfo,
				announceAlertWarning: announceAlertWarning,
				announceAlertError: announceAlertError,
				announceUnpublish: announceUnpublish,
				on: function (evt, cb) {
					messageHub.subscribe(cb, evt);
				}
			};
		}
	])
	.factory('$treeConfig', [
		function () {
			return {
				core: {
					themes: {
						name: 'default',
						responsive: false,
						dots: false,
						icons: true,
						variant: 'small',
						stripes: true
					},
					check_callback: function (o, n, p, i, m) {
						if (m && m.dnd && m.pos !== 'i') {
							return false;
						}
						if (o === 'move_node' || o === 'copy_node') {
							if (this.get_node(n).parent === this.get_node(p).id) {
								return false;
							}
						}
						return true;
					}
				},
				plugins: ['state', 'dnd', 'sort', 'types', 'contextmenu', 'unique'],
				types: {
					default: {
						icon: 'fa fa-file-o',
						default_name: 'file',
						template_new_name: '{name}{counter}'
					},
					file: {
						valid_children: []
					},
					folder: {
						default_name: 'folder',
						icon: 'fa fa-folder-o'
					},
					project: {
						icon: 'fa fa-clone'
					}
				},
				contextmenu: {
					items: function (node) {
						let ctxmenu = {};
						if (this.get_type(node) === 'project') {
							if (node.original._file.git) {
								ctxmenu.commit = {
									separator_before: false,
									label: 'Commit',
									action: function (data) {
										let tree = $.jstree.reference(data.reference);
										let node = tree.get_node(data.reference);
										tree.element.trigger('jstree.workspace.commit', [node.original._file]);
									}.bind(this)
								};
								ctxmenu.pull = {
									separator_before: false,
									label: 'Pull',
									action: function (data) {
										let tree = $.jstree.reference(data.reference);
										let node = tree.get_node(data.reference);
										tree.element.trigger('jstree.workspace.pull', [node.original._file]);
									}.bind(this)
								};
								ctxmenu.push = {
									separator_before: false,
									label: 'Push',
									action: function (data) {
										let tree = $.jstree.reference(data.reference);
										let node = tree.get_node(data.reference);
										tree.element.trigger('jstree.workspace.push', [node.original._file]);
									}.bind(this)
								};
								ctxmenu.reset = {
									separator_before: false,
									label: 'Reset',
									action: function (data) {
										let tree = $.jstree.reference(data.reference);
										let node = tree.get_node(data.reference);
										tree.element.trigger('jstree.workspace.reset', [node.original._file]);
									}.bind(this)
								};
								ctxmenu.import = {
									separator_before: true,
									label: 'Import Project(s)',
									action: function (data) {
										let tree = $.jstree.reference(data.reference);
										let node = tree.get_node(data.reference);
										tree.element.trigger('jstree.workspace.import', [node.original._file]);
									}.bind(this)
								};
								ctxmenu.delete = {
									separator_before: true,
									label: 'Delete',
									action: function (data) {
										let tree = $.jstree.reference(data.reference);
										let node = tree.get_node(data.reference);
										tree.element.trigger('jstree.workspace.delete', [node.original._file]);
									}.bind(this)
								};
							} else {
								ctxmenu.share = {
									separator_before: false,
									label: 'Share',
									action: function (data) {
										let tree = $.jstree.reference(data.reference);
										let node = tree.get_node(data.reference);
										tree.element.trigger('jstree.workspace.share', [node.original._file]);
									}.bind(this)
								};
							}
						} else if (node.original.type === 'branch' && !node.original.current) {
							ctxmenu.checkout = {
								separator_before: false,
								label: 'Checkout',
								action: function (data) {
									let tree = $.jstree.reference(data.reference);
									let node = tree.get_node(data.reference);
									tree.element.trigger('jstree.workspace.checkout', [
										{ project: node.original.project, branch: node.original.name }
									]);
								}.bind(this)
							};
						}

						return ctxmenu;
					}
				}
			};
		}
	])
	.factory('gitService', [
		'$http',
		'$messageHub',
		'GIT_SVC_URL',
		'$treeConfig',
		function ($http, $messageHub, GIT_SVC_URL, $treeConfig) {
			return new GitService($http, $messageHub, GIT_SVC_URL, $treeConfig);
		}
	])
	.factory('envService', [
		'$http',
		'ENV_SVC_URL',
		function ($http, ENV_SVC_URL) {
			return new EnvService($http, ENV_SVC_URL);
		}
	])
	.factory('publishService', [
		'$http',
		'PUBLISH_SVC_URL',
		function ($http, PUBLISH_SVC_URL) {
			return {
				publish: function (resourcePath) {
					let url = new UriBuilder().path(PUBLISH_SVC_URL.split('/')).path(resourcePath.split('/')).build();
					return $http.post(url, {});
				},
				unpublish: function (resourcePath) {
					let url = new UriBuilder().path(PUBLISH_SVC_URL.split('/')).path(resourcePath.split('/')).build();
					return $http.delete(url, {});
				}
			};
		}
	])
	.factory('workspaceService', [
		'$http',
		'WS_SVC_MANAGER_URL',
		'WS_SVC_URL',
		'$treeConfig',
		function ($http, WS_SVC_MANAGER_URL, WS_SVC_URL, $treeConfig) {
			return new WorkspaceService($http, WS_SVC_MANAGER_URL, WS_SVC_URL, $treeConfig);
		}
	])
	.factory('workspaceTreeAdapter', [
		'$http',
		'$treeConfig',
		'workspaceService',
		'gitService',
		'$messageHub',
		function ($http, $treeConfig, WorkspaceService, GitService, $messageHub) {
			return new WorkspaceTreeAdapter($http, $treeConfig, WorkspaceService, GitService, $messageHub);
		}
	])
	.controller('WorkspaceController', [
		'workspaceService',
		'workspaceTreeAdapter',
		'gitService',
		'publishService',
		'envService',
		'$messageHub',
		'$http',
		'$scope',
		function (
			workspaceService,
			workspaceTreeAdapter,
			gitService,
			publishService,
			envService,
			$messageHub,
			$http,
			$scope
		) {
			this.wsTree;
			this.workspaces;
			this.selectedWorkspace;
			this.gitService = gitService;
			this.http = $http;
			$scope.loaderOn = false;
			$scope.unpublishOnDelete = true;

			let loadingOverview = document.getElementsByClassName('loading-overview')[0];
			let loadingMessage = document.getElementsByClassName('loading-message')[0];

			workspaceService.listWorkspaceNames().then(
				function (workspaceNames) {
					this.workspaces = workspaceNames;
					let storedWorkspace = JSON.parse(localStorage.getItem('DIRIGIBLE.workspace') || '{}');
					if ('name' in storedWorkspace) {
						this.selectedWorkspace = storedWorkspace.name;
						this.workspaceSelected();
					} else if (this.workspaces[0]) {
						this.selectedWorkspace = 'workspace'; // Default
						localStorage.setItem('DIRIGIBLE.workspace', JSON.stringify({ name: this.selectedWorkspace }));
						this.workspaceSelected();
					}
				}.bind(this)
			);

			if (loadingOverview) loadingOverview.classList.add('hide');

			this.setLoaderState = function (value) {
				$scope.loaderOn = value;
			};

			this.showDeleteDialog = function () {
				$scope.unpublishOnDelete = true;
				$scope.$apply(); // Because of JQuery and the bootstrap modal
				$('#delete').click();
			};

			this.workspaceSelected = function () {
				if (this.wsTree) {
					this.wsTree.workspaceName = this.selectedWorkspace;
					this.wsTree.refresh();
					return;
				}
				this.wsTree = workspaceTreeAdapter.init($('.workspace'), this.selectedWorkspace, this, gitService);
				if (!workspaceService.typeMapping) workspaceService.typeMapping = $treeConfig[types];
				this.wsTree.refresh();
			};

			this.okClone = function () {
				if (this.clone.url) {
					loadingMessage.innerText = 'Clone...';
					if (loadingOverview) loadingOverview.classList.remove('hide');
					gitService.cloneProject(
						this.wsTree,
						this.selectedWorkspace,
						this.clone.url,
						this.branch,
						this.username,
						this.password
					);
				}
			};

			this.okPullAll = function () {
				loadingMessage.innerText = 'Pull all...';
				if (loadingOverview) loadingOverview.classList.remove('hide');
				gitService.pullManyProjects(
					this.wsTree,
					this.selectedWorkspace,
					this.username,
					this.password,
					this.branch,
					gitService.listOfProjects
				);
			};

			this.okPull = function () {
				loadingMessage.innerText = 'Pull ...';
				if (loadingOverview) loadingOverview.classList.remove('hide');
				gitService.pullProject(
					this.wsTree,
					this.selectedWorkspace,
					this.selectedProject,
					this.username,
					this.password,
					this.branch
				);
			};

			this.okPushAll = function () {
				loadingMessage.innerText = 'Push all...';
				if (loadingOverview) loadingOverview.classList.remove('hide');
				gitService.pushAllProjects(
					this.wsTree,
					this.selectedWorkspace,
					this.username,
					this.password,
					this.email
				);
			};

			this.okPush = function () {
				gitService.pushProject(
					this.wsTree,
					this.selectedWorkspace,
					this.selectedProject,
					this.username,
					this.password,
					this.email,
					this.branch
				);
			};

			this.okReset = function () {
				gitService.resetProject(
					this.wsTree,
					this.selectedWorkspace,
					this.selectedProject,
					this.username,
					this.password,
					this.branch
				);
			};

			this.okImport = function () {
				gitService.importProjects(this.wsTree, this.selectedWorkspace, this.selectedProject);
			};

			this.okDelete = function () {
				if ($scope.unpublishOnDelete) {
					for (let i = 0; i < this.selectedProjectData.folders.length; i++) {
						let resourcePath = `/${this.selectedWorkspace}/${this.selectedProjectData.folders[i].name}`;
						publishService.unpublish(resourcePath).then(
							function () {
								return $messageHub.announceUnpublish(this.selectedProjectData);
							}.bind(this)
						);
					}
				}
				gitService.deleteRepository(this.wsTree, this.selectedWorkspace, this.selectedProject);
			};

			this.okShare = function () {
				gitService.shareProject(
					this.wsTree,
					this.selectedWorkspace,
					this.selectedProject,
					this.repository,
					this.branch,
					this.commitMessage,
					this.username,
					this.password,
					this.email
				);
			};

			this.okCheckout = function () {
				gitService.checkoutBranch(
					this.wsTree,
					this.selectedWorkspace,
					this.selectedProject,
					this.selectedBranch,
					this.username,
					this.password
				);
			};

			this.okCommit = function () {
				gitService.commitProject(
					this.wsTree,
					this.selectedWorkspace,
					this.selectedProject,
					this.commitMessage,
					this.username,
					this.password,
					this.email,
					this.branch
				);
			};

			this.refresh = function () {
				this.wsTree.refresh();
				this.listProjects(this.workspace);
			};

			$messageHub.on(
				'git.repository.run',
				function (msg) {
					if (msg.data.env) {
						envService.setEnv(msg.data.env);
					}
					gitService.cloneProject(
						this.wsTree,
						this.selectedWorkspace,
						msg.data.repository,
						msg.data.branch,
						msg.data.username,
						msg.data.password,
						msg.data.projectName,
						msg.data.branch
					);
					if (msg.data.uri) {
						run();
					}

					function sleep(ms) {
						return new Promise((resolve) => setTimeout(resolve, ms));
					}

					async function run() {
						await sleep(2000);
						window.open(msg.data.uri, '_parent');
					}
				}.bind(this)
			);

			$messageHub.on(
				'workspace.action.complete',
				function (msg) {
					if (msg.data === 'clone.project') {
						$scope.$apply(() => {
							if (loadingOverview) loadingOverview.classList.add('hide');
						});
					}
				}.bind(this)
			);
		}
	]);
