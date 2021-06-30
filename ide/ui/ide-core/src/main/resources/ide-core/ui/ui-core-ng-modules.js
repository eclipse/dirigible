/*
 * Copyright (c) 2010-2021 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
/**
 * Provides key microservices for constructing and managing the IDE UI
 *
 */
angular.module('ideUiCore', ['ngResource'])
	.provider('messageHub', function MessageHubProvider() {
		this.evtNamePrefix = '';
		this.evtNameDelimiter = '.';
		this.$get = [function messageHubFactory() {
			var messageHub = new FramesMessageHub();
			//normalize prefix if any
			this.evtNamePrefix = this.evtNamePrefix || '';
			this.evtNamePrefix = this.evtNamePrefix ? (this.evtNamePrefix + this.evtNameDelimiter) : this.evtNamePrefix;
			var send = function (evtName, data, absolute) {
				if (!evtName)
					throw Error('evtname argument must be a valid string, identifying an existing event');
				messageHub.post({ data: data }, (absolute ? '' : this.evtNamePrefix) + evtName);
			}.bind(this);
			var on = function (evtName, callbackFunc) {
				if (typeof callbackFunc !== 'function')
					throw Error('Callback argument must be a function');
				messageHub.subscribe(callbackFunc, evtName);
			};
			return {
				send: send,
				on: on
			};
		}];
	})
	.factory('Theme', ['$resource', function ($resource) {
		var themeswitcher = $resource('../../../../services/v4/js/theme/resources.js?name=:themeName', { themeName: 'default' });
		return {
			changeTheme: function (themeName) {
				return themeswitcher.get({ 'themeName': themeName });
			},
			reload: function () {
				location.reload();
			}
		}
	}])
	.service('Perspectives', ['$resource', function ($resource) {
		return $resource('../../js/ide-core/services/perspectives.js');
	}])
	.service('Menu', ['$resource', function ($resource) {
		return $resource('../../js/ide-core/services/menu.js');
	}])
	.service('User', ['$http', function ($http) {
		return {
			get: function () {
				var user = {};
				$http({
					url: '../../js/ide-core/services/user-name.js',
					method: 'GET'
				}).success(function (data) {
					user.name = data;
				});
				return user;
			}
		};
	}])
	.provider('Editors', function () {
		function getEditors(resourcePath) {
			var xhr = new XMLHttpRequest();
			xhr.open('GET', '../../js/ide-core/services/editors.js', false);
			xhr.send();
			if (xhr.status === 200) {
				return JSON.parse(xhr.responseText);
			}
		}
		var editorProviders = {};
		var editorsForContentType = {};
		var editorsList = getEditors();
		editorsList.forEach(function (editor) {
			editorProviders[editor.id] = editor.link;
			editor.contentTypes.forEach(function (contentType) {
				if (!editorsForContentType[contentType]) {
					editorsForContentType[contentType] = [editor.id];
				} else {
					editorsForContentType[contentType].push(editor.id);
				}
			});
		});

		var defaultEditorId = this.defaultEditorId = "monaco";
		this.$get = [function editorsFactory() {

			return {
				defaultEditorId: defaultEditorId,
				editorProviders: editorProviders,
				editorsForContentType: editorsForContentType
			};
		}];
	})
	/**
	 * Creates a map object associating a view factory function with a name (id)
	 */
	.provider('ViewFactories', function () {
		var editors = this.editors;
		var self = this;
		this.factories = {
			"frame": function (container, componentState) {
				container.setTitle(componentState.label || 'View');
				$('<iframe>').attr('src', componentState.path).appendTo(container.getElement().empty());
			},
			"editor": function (container, componentState) {
				/* Improvement hint: Instead of hardcoding ?file=.. use URL template for the editor provider values
				 * and then replace the placeholders in the template with matching properties from the componentState.
				 * This will make it easy to replace the query string property if needed or provide additional
				 * (editor-specific) parameters easily.
				 */
				(function (componentState) {
					var src, editorPath;
					if (!componentState.editorId || Object.keys(self.editors.editorProviders).indexOf(componentState.editorId) < 0) {
						if (Object.keys(self.editors.editorsForContentType).indexOf(componentState.contentType) < 0) {
							editorPath = self.editors.editorProviders[self.editors.defaultEditorId];
						} else {
							if (self.editors.editorsForContentType[componentState.contentType].length > 1) {
								var formEditors = self.editors.editorsForContentType[componentState.contentType].filter(function (e) {
									switch (e) {
										case "orion":
										case "monaco":
										case "ace":
											return false;
										default:
											return true;
									}
								});
								if (formEditors.length > 0) {
									componentState.editorId = formEditors[0];
								} else {
									componentState.editorId = self.editors.editorsForContentType[componentState.contentType][0];
								}
							} else {
								componentState.editorId = self.editors.editorsForContentType[componentState.contentType][0];
							}
							editorPath = self.editors.editorProviders[componentState.editorId];
						}
					}
					else
						editorPath = self.editors.editorProviders[componentState.editorId];
					if (componentState.path) {
						if (componentState.editorId === 'flowable')
							src = editorPath + componentState.path;
						else
							src = editorPath + '?file=' + componentState.path;
						if (componentState.contentType && componentState.editorId !== 'flowable')
							src += "&contentType=" + componentState.contentType;
					} else {
						container.setTitle("Welcome");
						var brandingInfo = {};
						getBrandingInfo(brandingInfo);
						src = brandingInfo.branding.welcomePage;
					}
					$('<iframe>').attr('src', src).appendTo(container.getElement().empty());
				})(componentState, this);
			}.bind(self)
		};
		this.$get = ['Editors', function viewFactoriesFactory(Editors) {
			this.editors = Editors;
			return this.factories;
		}];
	})
	/**
	 * Wrap the ViewRegistry class in an angular service object for dependency injection
	 */
	.service('ViewRegistrySvc', ViewRegistry)
	/**
	 * A view registry instance factory, using remote service for intializing the view definitions
	 */
	.factory('viewRegistry', ['ViewRegistrySvc', '$resource', 'ViewFactories', function (ViewRegistrySvc, $resource, ViewFactories) {
		Object.keys(ViewFactories).forEach(function (factoryName) {
			ViewRegistrySvc.factory(factoryName, ViewFactories[factoryName]);
		});
		var get = function () {
			return $resource('../../js/ide-core/services/views.js').query().$promise
				.then(function (data) {
					data = data.map(function (v) {
						v.id = v.id || v.name.toLowerCase();
						v.label = v.label || v.name;
						v.factory = v.factory || 'frame';
						v.settings = {
							"path": v.link
						}
						v.region = v.region || 'left-top';
						return v;
					});
					//no extension point. provisioned "manually"
					data.push({ "id": "editor", "factory": "editor", "region": "center-middle", "label": "Editor", "settings": {} });
					//no extension point yet
					data.push({ "id": "result", "factory": "frame", "region": "center-bottom", "label": "Result", "settings": { "path": "../ide-database/sql/result.html" } });
					data.push({ "id": "properties", "factory": "frame", "region": "center-bottom", "label": "Properties", "settings": { "path": "../ide/properties.html" } });
					data.push({ "id": "sql", "factory": "frame", "region": "center-middle", "label": "SQL", "settings": { "path": "../ide-database/sql/editor.html" } });
					//register views
					data.forEach(function (viewDef) {
						ViewRegistrySvc.view(viewDef.id, viewDef.factory, viewDef.region, viewDef.label, viewDef.settings);
					});
					return ViewRegistrySvc;
				});
		};

		return {
			get: get
		};
	}])
	.factory('Layouts', [function () {
		return {
			manager: undefined
		};
	}])
	.directive('brandtitle', [function () {
		return {
			restrict: 'AE',
			transclude: true,
			replace: 'true',
			scope: {
				perspectiveName: '@perspectiveName'
			},
			link: function (scope, el, attrs) {
				getBrandingInfo(scope);
			},
			templateUrl: '../../../../services/v4/web/ide-core/ui/tmpl/brandTitle.html'
		};
	}])
	.directive('brandicon', [function () {
		return {
			restrict: 'AE',
			transclude: true,
			replace: 'true',
			link: function (scope, el, attrs) {
				getBrandingInfo(scope);
			},
			templateUrl: '../../../../services/v4/web/ide-core/ui/tmpl/brandIcon.html'
		};
	}])
	.directive('menu', ['$resource', 'Theme', 'User', 'Layouts', 'messageHub', function ($resource, Theme, User, Layouts, messageHub) {
		return {
			restrict: 'AE',
			transclude: true,
			replace: 'true',
			scope: {
				url: '@menuDataUrl',
				menu: '=menuData'
			},
			link: function (scope, el, attrs) {
				var url = scope.url;
				function loadMenu() {
					scope.menu = $resource(url).query();
				}
				getBrandingInfo(scope);

				messageHub.on('ide-core.closeEditor', function (msg) {
					Layouts.manager.closeEditor(msg.fileName);
				});

				messageHub.on('ide-core.closeOtherEditors', function (msg) {
					Layouts.manager.closeOtherEditors(msg.fileName);
				});

				messageHub.on('ide-core.closeAllEditors', function (msg) {
					Layouts.manager.closeAllEditors();
				});

				messageHub.on('ide-core.openView', function (msg) {
					Layouts.manager.openView(msg.viewId);
				});

				messageHub.on('ide-core.openPerspective', function (msg) {
					let url = msg.data.link;
					if ('parameters' in msg.data) {
						let urlParams = '';
						for (const property in msg.data.parameters) {
							urlParams += `${property}=${encodeURIComponent(msg.data.parameters[property])}&`
						}
						url += `?${urlParams.slice(0, -1)}`;
					}
					window.location.href = url;
				});

				messageHub.on('workspace.set', function (msg) {
					localStorage.setItem('DIRIGIBLE.workspace', JSON.stringify({ "name": msg.data.workspace }));
				});

				messageHub.on('workspace.file.deleted', function (msg) {
					Layouts.manager.closeEditor(msg.data.path);
				});

				messageHub.on('workspace.file.renamed', function (msg) {
					Layouts.manager.closeEditor(msg.data.file.path);
				});

				messageHub.on('workspace.file.moved', function (msg) {
					Layouts.manager.closeEditor("/" + msg.data.workspace + msg.data.sourcepath + "/" + msg.data.file);
				});

				if (!scope.menu && url)
					loadMenu.call(scope);
				scope.menuClick = function (item, subItem) {
					if (item.name === 'Show View') {
						// open view
						Layouts.manager.openView(subItem.name.toLowerCase());
					} else if (item.name === 'Open Perspective') {
						// open perspective`
						window.open(subItem.onClick.substring(subItem.onClick.indexOf('(') + 2, subItem.onClick.indexOf(',') - 1));//TODO: change the menu service ot provide paths instead
					} else if (item.event === 'openView') {
						// open view
						Layouts.manager.openView(item.name.toLowerCase());
					} else if (item.name === 'Reset') {
						scope.resetViews();
					} else {
						if (item.event === 'open') {
							window.open(item.data, '_blank');
						} else {
							//eval(item.onClick);
							if (subItem) {
								messageHub.send(subItem.event, subItem.data, true);
							} else {
								messageHub.send(item.event, item.data, true);
							}
						}
					}
				};

				getThemes(scope);

				scope.selectTheme = function (themeName) {
					Theme.changeTheme(themeName);
					setTimeout(function () {
						Theme.reload();
					}, 2000);
				};

				scope.resetTheme = function () {
					Theme.changeTheme('default');
					scope.resetViews();
					Theme.reload();
				};

				scope.resetViews = function () {
					localStorage.clear();
				};

				scope.user = User.get();
			},
			templateUrl: '../../../../services/v4/web/ide-core/ui/tmpl/menu.html'
		}
	}])
	.directive('sidebar', ['Perspectives', function (Perspectives) {
		return {
			restrict: 'AE',
			transclude: true,
			replace: 'true',
			scope: {
				active: '@'
			},
			link: function (scope, el, attrs) {
				scope.perspectives = Perspectives.query();
			},
			templateUrl: '../../../../services/v4/web/ide-core/ui/tmpl/sidebar.html'
		}
	}])
	.directive('alert', ['messageHub', function (messageHub) {
		return {
			restrict: 'AE',
			transclude: true,
			replace: 'true',
			scope: {
				active: '@'
			},
			link: function (scope, el, attrs) {
				let alertModalId = "#alertModal";

				scope.clearAlerts = function () {
					scope.alerts = [];
					$(alertModalId).modal('hide')
				};

				messageHub.on('ide.alert', function (msg) {
					let alert = {
						messageType: "info",
						title: msg.data.title,
						message: msg.data.message
					};

					if (msg.data.type) {
						switch (msg.data.type.toLowerCase()) {
							case "success":
							case "ok":
								alert.messageType = "success";
								break;
							case "warning":
								alert.messageType = "warning";
								break;
							case "info":
								alert.messageType = "info";
								break;
							case "error":
							case "danger":
								alert.messageType = "danger";
								break;
						}
					}
					if (!scope.alerts) {
						scope.alerts = [];
					}
					scope.alerts.push(alert);
					$(alertModalId).modal('show')
					scope.$apply();
				});
			},
			templateUrl: '../../../../services/v4/web/ide-core/ui/tmpl/alert.html'
		}
	}])
	.directive('statusBar', ['messageHub', function (messageHub) {
		return {
			restrict: 'AE',
			transclude: true,
			replace: 'true',
			scope: {
				statusBarTopic: '@',
				message: '@message',
				line: '@caret',
				error: '@error'
			},
			link: function (scope, el, attrs) {
				messageHub.on(scope.statusBarTopic || 'status.message', function (msg) {
					scope.message = msg.data;
					scope.$apply();
				});
				messageHub.on('status.caret', function (msg) {
					scope.caret = msg.data;
					scope.$apply();
				});
				messageHub.on('status.error', function (msg) {
					scope.error = msg.data;
					scope.$apply();
				});

				scope.cleanStatusMessages = function () {
					scope.message = null;
					scope.$apply();
				};

				scope.cleanErrorMessages = function () {
					scope.error = null;
					scope.$apply();
				};
			},
			templateUrl: '../../../../services/v4/web/ide-core/ui/tmpl/statusbar.html'
		}
	}])
	.directive('viewsLayout', ['viewRegistry', 'Layouts', function (viewRegistry, Layouts) {
		return {
			restrict: 'AE',
			scope: {
				viewsLayoutModel: '=',
				viewsLayoutViews: '@',
			},
			link: function (scope, el, attrs) {
				var views;
				if (scope.layoutViews) {
					views = scope.layoutViews.split(',');
				} else {
					views = scope.viewsLayoutModel.views;
				}
				var eventHandlers = scope.viewsLayoutModel.events;
				var viewSettings = scope.viewsLayoutModel.viewSettings;
				var layoutSettings = scope.viewsLayoutModel.layoutSettings;

				viewRegistry.get().then(function (registry) {
					scope.layoutManager = new LayoutController(registry);
					if (eventHandlers) {
						Object.keys(eventHandlers).forEach(function (evtName) {
							var handler = eventHandlers[evtName];
							if (typeof handler === 'function')
								scope.layoutManager.addListener(evtName, handler);
						});
					}
					$(window).resize(function () { scope.layoutManager.layout.updateSize() });
					scope.layoutManager.init(el, views, undefined, undefined, viewSettings, layoutSettings);
					Layouts.manager = scope.layoutManager;
				});
			}
		}
	}]);

function getBrandingInfo(scope) {
	scope.branding = JSON.parse(localStorage.getItem('DIRIGIBLE.branding'));
	if (scope.branding === null) {
		var xhr = new XMLHttpRequest();
		xhr.open('GET', '../../js/ide-branding/api/branding.js', false);
		xhr.send();
		if (xhr.status === 200) {
			var data = JSON.parse(xhr.responseText);
			scope.branding = data;
			localStorage.setItem('DIRIGIBLE.branding', JSON.stringify(data));
		}
	}
}

function getThemes(scope) {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', '../../js/theme/resources.js/themes', false);
	xhr.send();
	if (xhr.status === 200) {
		var data = JSON.parse(xhr.responseText);
		scope.themes = data;
	}
}
