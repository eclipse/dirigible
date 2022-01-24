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
};
UriBuilder.prototype.build = function (isBasePath = true) {
	if (isBasePath) return '/' + this.pathSegments.join('/');
	return this.pathSegments.join('/');
}

/**
 * Registry Service API delegate
 */
let RegistryService = function ($http, $window, registryServiceUrl, repositoryServiceUrl, treeCfg) {

	this.$http = $http;
	this.$window = $window;
	this.registryServiceUrl = registryServiceUrl;
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

RegistryService.prototype.remove = function (resourcepath) {
	resourcepath = resourcepath.replace('/registry/', '/registry/public/');
	let url = new UriBuilder().path(this.repositoryServiceUrl.split('/')).path(resourcepath.split('/')).build();
	return this.$http['delete'](url);
};
RegistryService.prototype.load = function (registryResourcePath) {
	let url = new UriBuilder().path(this.registryServiceUrl.split('/')).path(registryResourcePath.split('/')).build();
	return this.$http.get(url, { headers: { 'describe': 'application/json' } })
		.then(function (response) {
			return response.data;
		});
}


/**
 * Registry Tree Adapter mediating the registry service REST api and the jst tree component working with it
 */
let RegistryTreeAdapter = function (treeConfig, registrySvc, $messageHub) {
	this.treeConfig = treeConfig;
	this.registrySvc = registrySvc;
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

RegistryTreeAdapter.prototype.init = function (containerEl, registryName) {
	this.containerEl = containerEl;
	this.registryName = registryName;

	let self = this;
	let jstree = this.containerEl.jstree(this.treeConfig);

	//subscribe event listeners
	jstree.on('select_node.jstree', function (e, data) {
		this.clickNode(this.jstree.get_node(data.node));
	}.bind(this))
		.on('dblclick.jstree', function (evt) {
			this.dblClickNode(this.jstree.get_node(evt.target))
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
		.on('jstree.registry.inspect', function (e, data) {
			this.inspect(data);
		}.bind(this))
		;

	this.jstree = $.jstree.reference(jstree);
	return this;
};
RegistryTreeAdapter.prototype.deleteNode = function (node) {
	if (node.original && node.original._resource) {
		let path = node.original._resource.path;
		let self = this;
		return this.registrySvc.remove.apply(this.registrySvc, [path])
			.then(function () {
				self.$messageHub.announceResourceDeleted(node.original._resource);
			})
			.finally(function () {
				self.refresh();
			});
	}
}
RegistryTreeAdapter.prototype.dblClickNode = function (node) {
	let type = node.original.type;
	if ('resource' === type)
		this.$messageHub.announceResourceOpen(node.original._resource);
}
RegistryTreeAdapter.prototype.clickNode = function (node) {
	this.$messageHub.announceResourceSelected(node.original._resource);
};
RegistryTreeAdapter.prototype.raw = function () {
	return this.jstree;
}
RegistryTreeAdapter.prototype.refresh = function (node, keepState) {
	//TODO: This is reliable but a bit intrusive. Find out a more subtle way to update on demand
	let resourcepath;
	if (node) {
		resourcepath = node.original._resource.path;
	} else {
		resourcepath = this.registryName;
	}
	return this.registrySvc.load(resourcepath)
		.then(function (_data) {
			let data = [];
			if (_data.type == 'registry') {
				data = _data.collections;
			} else if (_data.type == 'collection' || _data.type == 'project') {
				data = [_data];
			}

			data = data.map(this._buildTreeNode.bind(this));

			if (!this.jstree.settings.core.data || _data.type === 'registry')
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
RegistryTreeAdapter.prototype.inspect = function (resource) {
	this.$messageHub.announceResourceOpen(resource);
}

angular.module('registry.config', [])
	.constant('REGISTRY_SVC_URL', '/services/v4/core/registry')
	.constant('REPOSITORY_SVC_URL', '/services/v4/core/repository')

angular.module('registry', ['registry.config'])
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
			messageHub.post({ data: data }, 'registry.' + evtName);
		};

		let transformDescriptor = function (resourceDescriptor) {
			let resourceDescriptorRepository = JSON.parse(JSON.stringify(resourceDescriptor));
			resourceDescriptorRepository.path = resourceDescriptorRepository.path.replace('/registry/', '/');
			return resourceDescriptorRepository;
		};

		let announceResourceSelected = function (resourceDescriptor) {
			let resourceDescriptorRepository = transformDescriptor(resourceDescriptor);
			this.send('resource.selected', resourceDescriptorRepository);
		};
		let announceResourceOpen = function (resourceDescriptor) {
			let resourceDescriptorRepository = transformDescriptor(resourceDescriptor);
			this.send('resource.open', resourceDescriptorRepository);
		};
		let announceResourceDeleted = function (resourceDescriptor) {
			let resourceDescriptorRepository = transformDescriptor(resourceDescriptor);
			this.send('resource.deleted', resourceDescriptorRepository);
		};

		return {
			send: send,
			announceResourceSelected: announceResourceSelected,
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
								tree.element.trigger('jstree.registry.inspect', [node.original._resource]);
							}.bind(this)
						}
					} else {
						delete ctxmenu.create;
					}
					ctxmenu.remove.shortcut = 46;
					ctxmenu.remove.shortcut_label = 'Del';

					return ctxmenu;
				}
			}
		}
	}])
	.factory('registryService', ['$http', '$window', 'REGISTRY_SVC_URL', 'REPOSITORY_SVC_URL', '$treeConfig', function ($http, $window, REGISTRY_SVC_URL, REPOSITORY_SVC_URL, $treeConfig) {
		return new RegistryService($http, $window, REGISTRY_SVC_URL, REPOSITORY_SVC_URL, $treeConfig);
	}])
	.factory('registryTreeAdapter', ['$treeConfig', 'registryService', '$messageHub', function ($treeConfig, RegistryService, $messageHub) {
		return new RegistryTreeAdapter($treeConfig, RegistryService, $messageHub);
	}])
	.controller('RegistryController', ['registryService', 'registryTreeAdapter', function (registryService, registryTreeAdapter) {

		this.registryTree;
		this.registry;

		this.registryTree = registryTreeAdapter.init($('.registry'), "/");
		if (!registryService.typeMapping)
			registryService.typeMapping = $treeConfig[types];
		this.registryTree.refresh();

		this.refresh = function () {
			this.registryTree.refresh();
		};

	}]);