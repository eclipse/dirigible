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
 * Repository Service API delegate
 */
let RepositoryService = function ($http, $window, repositoryServiceUrl, treeCfg) {

	this.$http = $http;
	this.$window = $window;
	this.repositoryServiceUrl = repositoryServiceUrl;
	this.typeMapping = treeCfg['types'];

	this.newResourceName = function (name, type, siblingResourcenames) {
		type = type || 'default';
		//check for custom new resource name template in the global configuration
		if (type && this.typeMapping[type] && this.typeMapping[type].template_new_name) {
			let nameIncrementRegex = this.typeMapping[type].name_increment_regex;
			siblingResourcenames = siblingResourcenames || [];
			let suffix = nextIncrementSegment(siblingResourcenames, name, nameIncrementRegex);
			suffix = suffix < 0 ? " " : suffix;
			let parameters = {
				"{name}": name || 'resource',
				"{ext}": this.typeMapping[type].ext,
				"{increment}": "-" + suffix
			}
			let tmpl = this.typeMapping[type].template_new_name;
			let regex = new RegExp(Object.keys(parameters).join('|'), 'g');
			let fName = tmpl.replace(regex, function (m) {
				return parameters[m] !== undefined ? parameters[m] : m;
			});
			name = fName.trim();
		}
		return name;
	}

	let startsWith = function (stringToTest, prefixToTest) {
		let startsWithRegEx = new RegExp('^' + prefixToTest);
		let matches = stringToTest.match(startsWithRegEx);
		return matches != null && matches.length > 0;
	}

	let strictInt = function (value) {
		if (/^(\-|\+)?([0-9]+|Infinity)$/.test(value))
			return Number(value);
		return NaN;
	}

	let toInt = function (value) {
		if (value === undefined)
			return;
		let _result = value.trim();
		_result = strictInt(_result);
		if (isNaN(_result))
			_result = undefined;
		return _result;
	}

	//processes an array of sibling string resourcenames to calculate the next incrmeent suffix segment
	let nextIncrementSegment = function (resourcenames, resourcenameToMatch, nameIncrementRegex) {
		let maxIncrement = resourcenames.map(function (siblingResourcename) {
			//find out incremented resource name matches (such as {resource-name} {i}.{extension} or {resource-name}-{i}.{extension})
			let incr = -2;
			//in case we have a regex configured to find out the increment direclty, use it
			if (nameIncrementRegex) {
				let regex = new Regex(nameIncrementRegex);
				let result = siblingResourcename.match(regex);
				if (result !== null) {
					incr = toInt(result[0]);
				}
			} else {
				//try heuristics
				let regex = /(.*?)(\.[^.]*$|$)/;
				let siblingTextSegments = siblingResourcename.match(regex);//matches resourcename and extension segments of a resourcename
				let siblingTextResourceName = siblingTextSegments[1];
				let siblingTextExtension = siblingTextSegments[2];
				let nodeTextSegments = resourcenameToMatch.match(regex);
				let nodeTextResourceName = nodeTextSegments[1];
				let nodeTextExtension = nodeTextSegments[2];
				if (siblingTextExtension === nodeTextExtension) {
					if (siblingTextResourceName === nodeTextResourceName)
						return -1;
					if (startsWith(siblingTextResourceName, nodeTextResourceName)) {
						//try to figure out the increment segment from the name part. Starting backwards, exepcts that the increment is the last numeric segment in the name
						let _inc = "";
						for (let i = siblingTextResourceName.length - 1; i > -1; i--) {
							let code = siblingTextResourceName.charCodeAt(i);
							if (code < 48 || code > 57)//decimal numbers only
								break;
							_inc = siblingTextResourceName[i] + _inc;
						}
						if (_inc) {
							incr = toInt(_inc);
						}
					}
				}
			}
			return incr;
		}).sort(function (a, b) {
			return a - b;
		}).pop();
		return ++maxIncrement;
	}
};

RepositoryService.prototype.createCollection = function (type) {
	let inst = $.jstree.reference(data.reference),
		obj = inst.get_node(data.reference);
	let node_tmpl = {
		type: 'collection',
		text: this.newResourceName('collection', 'collection')
	}
	inst.create_node(obj, node_tmpl, "last", function (new_node) {
		setTimeout(function () { inst.edit(new_node); }, 0);
	});
};

RepositoryService.prototype.createResource = function (name, path, isDirectory) {
	let url = new UriBuilder().path((this.repositoryServiceUrl + path).split('/')).path(name).build();
	if (isDirectory)
		url += "/";
	return this.$http.post(url)
		.then(function (response) {
			let resourcePath = response.headers('location');
			return this.$http.get(resourcePath, { headers: { 'describe': 'application/json' } })
				.then(function (response) { return response.data });
		}.bind(this))
		.catch(function (response) {
			let msg;
			if (response.data && response.data.error)
				msg = response.data.error;
			else
				msg = response.data || response.statusText || 'Unspecified server error. HTTP Code [' + response.status + ']';
			throw msg;
		});
};
RepositoryService.prototype.remove = function (resourcepath) {
	let url = new UriBuilder().path(this.repositoryServiceUrl.split('/')).path(resourcepath.split('/')).build();
	return this.$http['delete'](url);
};
RepositoryService.prototype.load = function (repositoryResourcePath) {
	let url = new UriBuilder().path(this.repositoryServiceUrl.split('/')).path(repositoryResourcePath.split('/')).build();
	return this.$http.get(url, { headers: { 'describe': 'application/json' } })
		.then(function (response) {
			return response.data;
		});
}


/**
 * Repository Tree Adapter mediating the repository service REST api and the jst tree component working with it
 */
let RepositoryTreeAdapter = function (treeConfig, repositorySvc, exportService, $messageHub) {
	this.treeConfig = treeConfig;
	this.repositorySvc = repositorySvc;
	this.exportService = exportService;
	this.$messageHub = $messageHub;

	this._buildTreeNode = function (f) {
		let children = [];
		if (f.type == 'collection' || f.type == 'project') {
			children = f.collections.map(this._buildTreeNode.bind(this));
			let _resources = f.resources.map(this._buildTreeNode.bind(this))
			children = children.concat(_resources);
		}
		f.label = f.name;
		return {
			"text": f.name,
			"children": children,
			"type": f.type,
			"_resource": f
		}
	};

	this._fnr = function (node, replacement) {
		if (node.children) {
			let done;
			node.children = node.children.map(function (c) {
				if (!done && c._resource.path === replacement._resource.path) {
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

RepositoryTreeAdapter.prototype.init = function (containerEl, repositoryName) {
	this.containerEl = containerEl;
	this.repositoryName = repositoryName;

	let self = this;
	let jstree = this.containerEl.jstree(this.treeConfig);

	//subscribe event listeners
	jstree.on('select_node.jstree', function (e, data) {
		this.clickNode(this.jstree.get_node(data.node));
	}.bind(this))
		.on('dblclick.jstree', function (evt) {
			this.dblClickNode(this.jstree.get_node(evt.target))
		}.bind(this))
		.on('create_node.jstree', function (e, data) { })
		.on('rename_node.jstree', function (e, data) {
			if (data.old !== data.text || !data.node.original._resource) {
				this.renameNode(data.node, data.old, data.text);
			}
		}.bind(this))
		.on('open_node.jstree', function (evt, data) {
			if (data.node.type !== 'project')
				data.instance.set_icon(data.node, 'fa fa-folder-open');
		})
		.on('close_node.jstree', function (evt, data) {
			if (data.node.type !== 'project')
				data.instance.set_icon(data.node, 'fa fa-folder');
		})
		.on('delete_node.jstree', function (e, data) {
			this.deleteNode(data.node)
		}.bind(this))
		.on('jstree.repository.inspect', function (e, data) {
			this.inspect(data);
		}.bind(this))
		;

	this.jstree = $.jstree.reference(jstree);
	return this;
};
RepositoryTreeAdapter.prototype.createNode = function (parentNode, type, defaultName) {
	if (type === undefined)
		type = 'resource';
	let siblingIds = parentNode.children || [];
	let resourcenames = siblingIds.map(function (id) {
		if (this.jstree.get_node(id).original.type !== type)
			return;
		return this.jstree.get_node(id).text;
	}.bind(this))
		.filter(function (siblingFName) {
			return siblingFName !== undefined;
		});

	if (!defaultName) {
		defaultName = type === 'collection' ? 'collection' : 'resource';
	}

	let node_tmpl = {
		type: type,
		text: this.repositorySvc.newResourceName(defaultName, type, resourcenames)
	}

	let ctxPath = parentNode.original._resource.path;

	let self = this;
	this.jstree.create_node(parentNode, node_tmpl, "last",
		function (new_node) {
			let name = node_tmpl.text;
			self.jstree.edit(new_node);
		});
}
RepositoryTreeAdapter.prototype.deleteNode = function (node) {
	if (node.original && node.original._resource) {
		let path = node.original._resource.path;
		let self = this;
		return this.repositorySvc.remove.apply(this.repositorySvc, [path])
			.then(function () {
				self.$messageHub.announceResourceDeleted(node.original._resource);
			})
			.finally(function () {
				self.refresh();
			});
	}
}
RepositoryTreeAdapter.prototype.renameNode = function (node, oldName, newName) {
	let fpath;
	if (!node.original._resource) {
		let parentNode = this.jstree.get_node(node.parent);
		let fpath = parentNode.original._resource.path;
		this.repositorySvc.createResource.apply(this.repositorySvc, [newName, fpath, node.type == 'collection'])
			.then(function (f) {
				node.original._resource = f;
				node.original._resource.label = node.original._resource.name;
				this.$messageHub.announceResourceCreated(f);
			}.bind(this))
			.catch(function (node, err) {
				this.jstree.delete_node(node);
				this.refresh();
				throw err;
			}.bind(this, node));
	} else {
		this.repositorySvc.rename.apply(this.repositorySvc, [oldName, newName, node.original._resource.path])
			.then(function (data) {
				this.$messageHub.announceResourceRenamed(node.original._resource, oldName, newName);
				//this.jstree.reference(node).select_node(node);
			}.bind(this))
			.finally(function () {
				this.refresh();
			}.bind(this));
	}
};
RepositoryTreeAdapter.prototype.dblClickNode = function (node) {
	let type = node.original.type;
	if ('resource' === type)
		this.$messageHub.announceResourceOpen(node.original._resource);
}
RepositoryTreeAdapter.prototype.clickNode = function (node) {
	this.$messageHub.announceResourceSelected(node.original._resource);
};
RepositoryTreeAdapter.prototype.raw = function () {
	return this.jstree;
}
RepositoryTreeAdapter.prototype.refresh = function (node, keepState) {
	//TODO: This is reliable but a bit intrusive. Find out a more subtle way to update on demand
	let resourcepath;
	if (node) {
		resourcepath = node.original._resource.path;
	} else {
		resourcepath = this.repositoryName;
	}
	return this.repositorySvc.load(resourcepath)
		.then(function (_data) {
			let data = [];
			if (_data.type == 'repository') {
				data = _data.collections;
			} else if (_data.type == 'collection' || _data.type == 'project') {
				data = [_data];
			}

			data = data.map(this._buildTreeNode.bind(this));

			if (!this.jstree.settings.core.data || _data.type === 'repository')
				this.jstree.settings.core.data = data;
			else {
				//find and replace the loaded node
				let self = this;
				this.jstree.settings.core.data = this.jstree.settings.core.data.map(function (node) {
					data.forEach(function (_node, replacement) {
						if (self._fnr(_node, replacement))
							return;
					}.bind(self, node));
					return node;
				});
			}
			if (!keepState)
				this.jstree.refresh();
		}.bind(this));
};
RepositoryTreeAdapter.prototype.inspect = function (resource) {
	this.$messageHub.announceResourceOpen(resource);
}

angular.module('repository.config', [])
	.constant('REPOSITORY_SVC_URL', '/services/v4/core/repository')
	.constant('EXPORT_SVC_URL', '/services/v4/transport/snapshot')

angular.module('repository', ['repository.config'])
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
		$httpProvider.defaults.transformResponse.unshift(function (data, headersGetter, status) {
			if (status > 399) {
				data = {
					"error": data
				}
				data = JSON.stringify(data);
			}
			return data;
		});
		$httpProvider.interceptors.push('httpRequestInterceptor');
	}])
	.factory('$messageHub', [function () {
		let messageHub = new FramesMessageHub();
		let send = function (evtName, data, absolute) {
			messageHub.post({ data: data }, 'repository.' + evtName);
		};
		let announceResourceSelected = function (resourceDescriptor) {
			this.send('resource.selected', resourceDescriptor);
		};
		let announceResourceCreated = function (resourceDescriptor) {
			this.send('resource.created', resourceDescriptor);
		};
		let announceResourceOpen = function (resourceDescriptor) {
			this.send('resource.open', resourceDescriptor);
		};
		let announceResourceDeleted = function (resourceDescriptor) {
			this.send('resource.deleted', resourceDescriptor);
		};

		return {
			send: send,
			announceResourceSelected: announceResourceSelected,
			announceResourceCreated: announceResourceCreated,
			announceResourceOpen: announceResourceOpen,
			announceResourceDeleted: announceResourceDeleted
		};
	}])
	.factory('$treeConfig', [function () {
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
			'plugins': ['state', 'sort', 'types', 'contextmenu', 'unique'],
			'unique': {
				'newNodeName': function (node, typesConfig) {
					let typeCfg = typesConfig[node.type] || typesConfig['default'];
					let name = typeCfg.default_name || typesConfig['default'].default_name || 'resource';
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
					name = typeCfg.default_name || typesConfig['default'].default_name || 'resource';
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
					"icon": "fa fa-file",
					"default_name": "resource",
					"template_new_name": "{name}{counter}"
				},
				'resource': {
					"valid_children": []
				},
				'collection': {
					"default_name": "collection",
					'icon': "fa fa-folder"
				}
			},
			"contextmenu": {
				"items": function (node) {
					let ctxmenu = $.jstree.defaults.contextmenu.items();
					delete ctxmenu.ccp;
					delete ctxmenu.rename;
					if (this.get_type(node) === "resource") {
						delete ctxmenu.create;
						/*Inspect*/
						ctxmenu.inspect = {
							"separator_before": true,
							"label": "Inspect",
							"action": function (data) {
								let tree = $.jstree.reference(data.reference);
								let node = tree.get_node(data.reference);
								tree.element.trigger('jstree.repository.inspect', [node.original._resource]);
							}.bind(this)
						}
					} else {
						delete ctxmenu.create.action;
						ctxmenu.create.label = "New";
						ctxmenu.create.submenu = {
							"create_collection": {
								"separator_after": true,
								"label": "Collection",
								"action": function (data) {
									let tree = data.reference.jstree(true);
									let parentNode = tree.get_node(data.reference);
									let collectionNode = {
										type: 'collection'
									};
									tree.create_node(parentNode, collectionNode, "last", function (new_node) {
										tree.edit(new_node);
									});
								}
							},
							"create_resource": {
								"label": "Resource",
								"action": function (tree, data) {
									let parentNode = tree.get_node(data.reference);
									let resourceNode = {
										type: 'resource'
									};
									tree.create_node(parentNode, resourceNode, "last", function (new_node) {
										tree.edit(new_node);
									});
								}.bind(self, this)
							}
						};
					}
					ctxmenu.remove.shortcut = 46;
					ctxmenu.remove.shortcut_label = 'Del';

					return ctxmenu;
				}
			}
		}
	}])
	.factory('exportService', ['$http', '$window', 'EXPORT_SVC_URL', function ($http, $window, EXPORT_SVC_URL) {
		return {
			exportRepository: function () {
				$window.open(EXPORT_SVC_URL);
			}
		}
	}])
	.factory('repositoryService', ['$http', '$window', 'REPOSITORY_SVC_URL', '$treeConfig', function ($http, $window, REPOSITORY_SVC_URL, $treeConfig) {
		return new RepositoryService($http, $window, REPOSITORY_SVC_URL, $treeConfig);
	}])
	.factory('repositoryTreeAdapter', ['$treeConfig', 'repositoryService', 'exportService', '$messageHub', function ($treeConfig, RepositoryService, exportService, $messageHub) {
		return new RepositoryTreeAdapter($treeConfig, RepositoryService, exportService, $messageHub);
	}])
	.controller('RepositoryController', ['repositoryService', 'repositoryTreeAdapter', 'exportService', function (repositoryService, repositoryTreeAdapter, exportService) {

		this.repositoryTree;
		this.repository;


		this.repositoryTree = repositoryTreeAdapter.init($('.repository'), "/");
		if (!repositoryService.typeMapping)
			repositoryService.typeMapping = $treeConfig[types];
		this.repositoryTree.refresh();

		this.exportRepository = function () {
			exportService.exportRepository();
		};

		this.refresh = function () {
			this.repositoryTree.refresh();
		};

	}]);
