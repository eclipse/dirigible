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
angular.module('ideLayout', ['idePerspective', 'ideEditors', 'ideMessageHub'])
    .constant('SplitPaneState', {
        EXPANDED: 0,
        COLLAPSED: 1
    })
    .constant('perspective', perspectiveData)
    .factory('Views', ['$resource', function ($resource) {
        let get = function () {
            return $resource('/services/v4/js/ide-core/services/views.js').query().$promise
                .then(function (data) {
                    data = data.map(function (v) {
                        if (!v.id) {
                            console.error(`Views: view '${v.label || 'undefined'}' does not have an id`);
                            return;
                        }
                        if (!v.label) {
                            console.error(`Views: view '${v.id}' does not have a label`);
                            return;
                        }
                        if (!v.link) {
                            console.error(`Views: view '${v.id}' does not have a link`);
                            return;
                        }
                        v.factory = v.factory || 'frame';
                        v.settings = {
                            path: v.link
                        };
                        v.region = v.region || 'left-top';
                        return v;
                    });
                    return data;
                });
        };

        return {
            get: get
        };
    }])
    .directive('view', ['Views', 'perspective', function (Views, perspective) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                name: '@', // Shouldn't this be id?
                settings: '=',
            },
            link: function (scope) {
                Views.get().then(function (views) {
                    const view = views.find(v => v.id === scope.name);
                    if (view) {
                        scope.path = view.settings.path;
                        if (!view.params) {
                            scope.params = {
                                container: 'layout',
                                perspectiveId: perspective.id,
                            };
                        } else {
                            scope.params = view.params;
                            scope.params['container'] = 'layout';
                            scope.params['perspectiveId'] = perspective.id;
                        }
                    }
                });

                scope.getParams = function () {
                    return JSON.stringify(scope.params);
                }
            },
            template: '<iframe loading="lazy" src="{{path}}" data-parameters="{{getParams()}}"></iframe>'
        }
    }])
    .directive('ideLayout', ['Views', 'Editors', 'SplitPaneState', 'messageHub', 'perspective', function (Views, Editors, SplitPaneState, messageHub, perspective) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                viewsLayoutModel: '='
            },
            controller: ['$scope', '$element', function ($scope) {
                if (!perspective.id || !perspective.name)
                    console.error('<ide-layout> requires perspective service data');

                const VIEW = 'view';
                const EDITOR = 'editor';

                $scope.views = [];
                $scope.explorerTabs = [];
                $scope.bottomTabs = [];
                $scope.centerSplittedTabViews = {
                    direction: 'horizontal',
                    panes: [
                        {
                            tabs: [],
                            selectedTab: null
                        }
                    ]
                };

                $scope.layoutSettings = $scope.viewsLayoutModel.layoutSettings || {};
                $scope.selection = {
                    selectedBottomTab: null
                };
                $scope.splitPanesState = {
                    main: []
                }

                $scope.initialOpenViews = $scope.viewsLayoutModel.views;
                $scope.focusedTabView = null;

                let closingFileArgs;
                let reloadingFileArgs;
                let eventHandlers = $scope.viewsLayoutModel.events;
                // let viewSettings = $scope.viewsLayoutModel.viewSettings;

                if (perspective.id && perspective.name) {
                    Views.get().then(function (views) {
                        $scope.views = views;

                        const viewExists = (v) => views.some(x => x.id === v.id);
                        const viewById = (ret, viewId) => {
                            const v = $scope.views.find(v => v.id === viewId);
                            if (v) ret.push(v);
                            return ret;
                        };
                        const byLeftRegion = view => view.region.startsWith('left')
                        const byBottomRegion = view => view.region === 'center-bottom' || view.region === 'bottom';
                        const byCenterRegion = view => view.region === 'center-top' || view.region === 'center-middle' || view.region === 'center';

                        const savedState = loadLayoutState();
                        if (savedState) {
                            const restoreCenterSplittedTabViews = function (state, removedViewsIds) {
                                if (state.panes) {
                                    state.panes.forEach(pane => restoreCenterSplittedTabViews(pane, removedViewsIds));
                                } else {
                                    state.tabs = state.tabs.filter(v => v.type === EDITOR || (viewExists(v) && (!removedViewsIds || !removedViewsIds.includes(v.id))));
                                    if (!state.tabs.some(x => x.id === state.selectedTab)) {
                                        state.selectedTab = null;
                                    }
                                }

                                return state;
                            }

                            $scope.explorerTabs = savedState.explorer.tabs.filter(viewExists);
                            $scope.bottomTabs = savedState.bottom.tabs.filter(viewExists);

                            let newlyAddedViews, removedViewsIds;
                            let initialOpenViewsChanged = !angular.equals(savedState.initialOpenViews, $scope.initialOpenViews);
                            if (initialOpenViewsChanged) {
                                newlyAddedViews = $scope.initialOpenViews.filter(x => savedState.initialOpenViews.every(y => x !== y)).reduce(viewById, []);
                                removedViewsIds = savedState.initialOpenViews.filter(x => $scope.initialOpenViews.every(y => x !== y));

                                $scope.explorerTabs = $scope.explorerTabs
                                    .filter(x => !removedViewsIds.includes(x.id))
                                    .concat(newlyAddedViews.filter(byLeftRegion).map(mapViewToTab));

                                $scope.bottomTabs = $scope.bottomTabs
                                    .filter(x => !removedViewsIds.includes(x.id))
                                    .concat(newlyAddedViews.filter(byBottomRegion).map(mapViewToTab));
                            }

                            $scope.centerSplittedTabViews = restoreCenterSplittedTabViews(savedState.center, removedViewsIds);

                            if (newlyAddedViews) {
                                $scope.centerSplittedTabViews.panes[0].tabs.push(...newlyAddedViews.filter(byCenterRegion).map(mapViewToTab));
                            }

                            if ($scope.bottomTabs.some(x => x.id === savedState.bottom.selected))
                                $scope.selection.selectedBottomTab = savedState.bottom.selected;

                            if (initialOpenViewsChanged) {
                                saveLayoutState();
                            }

                            shortenCenterTabsLabels();

                        } else {
                            let openViews = $scope.initialOpenViews.reduce(viewById, []);

                            $scope.explorerTabs = openViews
                                .filter(byLeftRegion)
                                .map(mapViewToTab);

                            $scope.bottomTabs = openViews
                                .filter(byBottomRegion)
                                .map(mapViewToTab);

                            $scope.centerSplittedTabViews.panes[0].tabs = openViews
                                .filter(byCenterRegion)
                                .map(mapViewToTab);
                        }

                        $scope.focusedTabView = getFirstCenterSplittedTabViewPane($scope.centerSplittedTabViews);

                        $scope.$watch('selection', function (newSelection, oldSelection) {
                            if (!angular.equals(newSelection, oldSelection)) {
                                saveLayoutState();
                            }
                        }, true);

                        $scope.$watch('centerSplittedTabViews', function (newValue, oldValue) {
                            if (!angular.equals(newValue, oldValue)) {
                                saveLayoutState();
                            }
                        }, true);

                        $scope.$watch('explorerTabs', function (newValue, oldValue) {
                            const collectionsEqual = (a, b, ...props) => {
                                if (a && !b || !a && b) return false;
                                if (!a && !b) return true;
                                if (a.length !== b.length) return false;
                                for (let i = 0; i < a.length; i++) {
                                    const x = a[i];
                                    const y = b[i];
                                    for (let p of props) {
                                        if (!angular.equals(x[p], y[p]))
                                            return false;
                                    }
                                }
                                return true;
                            }

                            if (!collectionsEqual(newValue, oldValue, 'hidden')) {
                                saveLayoutState();
                            }
                        }, true);

                        if (eventHandlers) {
                            Object.keys(eventHandlers).forEach(function (evtName) {
                                let handler = eventHandlers[evtName];
                                if (typeof handler === 'function')
                                    messageHub.onDidReceiveMessage(evtName, handler);
                            });
                        }
                    });
                }

                $scope.closeCenterTab = function (tab) {
                    tryCloseCenterTabs([tab]);
                }

                $scope.splitCenterTabs = function (direction, pane) {
                    splitTabs(direction, pane);
                };

                $scope.collapseBottomPane = function () {
                    updateSplitPanesState({
                        editorsPaneState: SplitPaneState.EXPANDED,
                        bottomPanesState: SplitPaneState.COLLAPSED
                    });
                }

                $scope.expandBottomPane = function () {
                    updateSplitPanesState({
                        editorsPaneState: SplitPaneState.EXPANDED,
                        bottomPanesState: SplitPaneState.EXPANDED
                    });
                }

                $scope.toggleEditorsPane = function () {
                    let editorsPaneCollapsed = $scope.isEditorsPaneCollapsed();

                    updateSplitPanesState({
                        editorsPaneState: editorsPaneCollapsed ? SplitPaneState.EXPANDED : SplitPaneState.COLLAPSED,
                        bottomPanesState: SplitPaneState.EXPANDED
                    });
                }

                $scope.isEditorsPaneCollapsed = function () {
                    return $scope.splitPanesState.main[0] == SplitPaneState.COLLAPSED;
                }

                $scope.isBottomPaneCollapsed = function () {
                    return $scope.splitPanesState.main.length < 2 || $scope.splitPanesState.main[1] == SplitPaneState.COLLAPSED;
                }

                function loadLayoutState() {
                    let savedState = localStorage.getItem(`DIRIGIBLE.IDE.LAYOUT.state.${perspective.id}`);
                    if (savedState !== null) {
                        return JSON.parse(savedState);
                    }

                    return null;
                }

                function saveLayoutState() {

                    const saveCenterSplittedTabViews = function (parent) {
                        let ret;
                        if (parent.panes) {
                            ret = {
                                direction: parent.direction,
                                panes: []
                            };
                            for (let i = 0; i < parent.panes.length; i++) {
                                const pane = parent.panes[i];
                                ret.panes.push(saveCenterSplittedTabViews(pane));
                            }
                        } else {
                            ret = {
                                tabs: parent.tabs.map(x => ({ id: x.id, type: x.type, label: x.label, path: x.path, params: x.params })),
                                selectedTab: parent.selectedTab
                            };
                        }
                        return ret;
                    }

                    let state = {
                        initialOpenViews: $scope.initialOpenViews,
                        explorer: {
                            tabs: $scope.explorerTabs.map(({ id, type, label, path, hidden, params }) => ({ id, type, label, path, hidden, params }))
                        },
                        bottom: {
                            tabs: $scope.bottomTabs.map(({ id, type, label, path, params }) => ({ id, type, label, path, params })),
                            selected: $scope.selection.selectedBottomTab
                        },
                        center: saveCenterSplittedTabViews($scope.centerSplittedTabViews)
                    };

                    // console.debug(`Saving DIRIGIBLE.IDE.LAYOUT state`);

                    localStorage.setItem(`DIRIGIBLE.IDE.LAYOUT.state.${perspective.id}`, JSON.stringify(state));
                }

                function updateSplitPanesState(args) {
                    if ($scope.splitPanesState.main.length > 1) {
                        $scope.splitPanesState.main[0] = args.editorsPaneState;
                        $scope.splitPanesState.main[1] = args.bottomPanesState;
                    }
                }

                function findView(views, view) {
                    return views.find(v => v.id === view.id);
                }

                function mapViewToTab(view) {
                    return {
                        id: view.id,
                        type: VIEW,
                        label: view.label,
                        path: view.settings.path,
                        params: view.params,
                    };
                }

                function findCenterSplittedTabView(id, pane = null, parent = null, indexInParent = -1) {

                    let currentPane = pane || $scope.centerSplittedTabViews;

                    if (currentPane.tabs) {
                        const index = currentPane.tabs.findIndex(f => f.id === id);
                        if (index >= 0)
                            return { tabsView: currentPane, parent, index };

                    } else if (currentPane.panes) {
                        for (let i = 0; i < currentPane.panes.length; i++) {
                            let childPane = currentPane.panes[i];
                            let result = findCenterSplittedTabView(id, childPane, { parent, indexInParent, ...currentPane }, i);
                            if (result)
                                return result;
                        }
                    }

                    return null;
                }

                function getFirstCenterSplittedTabViewPane(parent) {
                    let pane = parent;
                    while (pane.panes) {
                        pane = pane.panes[0];
                    }
                    return pane;
                }

                function getCurrentCenterSplittedTabViewPane() {
                    return $scope.focusedTabView || getFirstCenterSplittedTabViewPane($scope.centerSplittedTabViews);
                }

                function forEachCenterSplittedTabView(callback, parent) {
                    let parentNode = parent || $scope.centerSplittedTabViews;

                    if (parentNode.tabs) {
                        callback(parentNode);
                    } else if (parentNode.panes) {
                        for (let pane of parentNode.panes) {
                            forEachCenterSplittedTabView(callback, pane);
                        }
                    }
                }

                function splitTabs(direction, pane) {
                    const { selectedTab } = pane;
                    const result = findCenterSplittedTabView(selectedTab);
                    if (result) {
                        const splitView = result.parent;
                        const srcTabsView = result.tabsView;
                        const tabIndex = result.index;
                        const srcTabs = srcTabsView.tabs;

                        if (srcTabs.length === 1)
                            return;

                        const tab = srcTabs[tabIndex];

                        srcTabs.splice(tabIndex, 1);

                        const destTabsView = {
                            tabs: [tab],
                            selectedTab
                        };

                        const index = splitView.panes.indexOf(srcTabsView);

                        if (splitView.panes.length === 1 || splitView.direction === direction) {
                            splitView.direction = direction;
                            splitView.panes.splice(index + 1, 0, destTabsView);
                        } else {
                            const selectedTabIndex = tabIndex < srcTabs.length ? tabIndex : srcTabs.length - 1;
                            srcTabsView.selectedTab = srcTabs[selectedTabIndex].id;

                            splitView.panes[index] = {
                                direction,
                                panes: [
                                    srcTabsView,
                                    destTabsView
                                ]
                            }
                        }
                    }
                }

                function removeSplitPane(splitView, splitPane) {
                    if (splitView.panes.length > 1) {
                        const index = splitView.panes.indexOf(splitPane);
                        splitView.panes.splice(index, 1);

                        let focusedSplitView;
                        let focusedPaneIndex;

                        if (splitView.parent && splitView.panes.length === 1) {
                            //if this is the last split pane then remove the wrapping split view
                            const pane = splitView.panes[0];
                            if (pane.panes && pane.direction === splitView.parent.direction) {
                                //if the last split pane contains another split view and it's direction is the same as the parent split view
                                //then concatenate both split views
                                splitView.parent.panes.splice(splitView.indexInParent, 1, ...pane.panes);
                            } else {
                                //... otherwise just replace the wrapping split view with the tabs view 
                                splitView.parent.panes[splitView.indexInParent] = pane;
                            }

                            focusedSplitView = splitView.parent;
                            focusedPaneIndex = splitView.indexInParent;
                        } else {
                            focusedSplitView = splitView;
                            focusedPaneIndex = index < splitView.panes.length ? index : splitView.panes.length - 1;
                        }

                        $scope.focusedTabView = getFirstCenterSplittedTabViewPane(focusedSplitView.panes[focusedPaneIndex]);
                    }
                }

                function moveTab(tabId) {
                    const result = findCenterSplittedTabView(tabId);
                    if (result) {
                        const splitView = result.parent;
                        const srcTabsView = result.tabsView;

                        if (srcTabsView.tabs.length === 1 && splitView.panes.length === 1)
                            return;

                        const tab = srcTabsView.tabs[result.index];

                        srcTabsView.tabs.splice(result.index, 1);

                        let destTabsView;
                        if (splitView.panes.length === 1) {
                            destTabsView = {
                                tabs: [tab],
                                selectedTab: tabId
                            }
                            splitView.panes.push(destTabsView);
                        } else {
                            const srcIndex = splitView.panes.indexOf(srcTabsView);
                            destTabsView = splitView.panes[srcIndex === 0 ? 1 : 0];
                            destTabsView.selectedTab = tabId;
                            destTabsView.tabs.push(tab);

                            if (srcTabsView.tabs.length === 0) {
                                splitView.panes.splice(srcIndex, 1);
                            }
                        }
                    }
                }

                function showFileSaveDialog(fileName, filePath, args = {}) {
                    return new Promise((resolve, reject) => {
                        messageHub.showDialogAsync(
                            'You have unsaved changes',
                            `Do you want to save the changes you made to ${fileName}?`,
                            [{
                                id: { id: 'save', file: filePath, ...args },
                                type: 'normal',
                                label: 'Save',
                            }, {
                                id: { id: 'ignore', file: filePath, ...args },
                                type: 'normal',
                                label: 'Don\'t Save',
                            }, {
                                id: { id: 'cancel' },
                                type: 'transparent',
                                label: 'Cancel',
                            }]
                        ).then(({ data }) => {
                            const { id, file } = data;
                            switch (id) {
                                case 'save':
                                    messageHub.postMessage('editor.file.save', { file }, true);
                                    break;
                                case 'ignore':
                                    $scope.setEditorDirty(file, false);
                                    break;
                            }
                            resolve(data);

                        }).catch(reject);
                    });
                }

                function tryReloadCenterTab(tab, editorPath, params) {
                    if (tab.dirty) {
                        showFileSaveDialog(tab.label, tab.id, { editorPath, params })
                            .then(args => {
                                switch (args.id) {
                                    case 'save':
                                        reloadingFileArgs = args;
                                        break;
                                    case 'ignore':
                                        reloadCenterTab(args.file, args.editorPath, args.params);
                                        $scope.$digest();
                                        break;
                                }
                            });
                    } else {
                        reloadCenterTab(tab.id, editorPath, params);
                    }
                }

                function tryCloseCenterTabs(tabs) {
                    let dirtyFiles = tabs.filter(tab => tab.dirty);
                    if (dirtyFiles.length > 0) {

                        let tab = dirtyFiles[0];
                        let result = findCenterSplittedTabView(tab.id);
                        if (result) {
                            result.tabsView.selectedTab = tab.id;
                        }

                        showFileSaveDialog(tab.label, tab.id, { tabs })
                            .then(args => {
                                switch (args.id) {
                                    case 'save':
                                        closingFileArgs = args;
                                        break;
                                    case 'ignore':
                                        closeCenterTab(args.file)

                                        let rest = args.tabs.filter(x => x.id !== args.file);
                                        if (rest.length > 0)
                                            if (tryCloseCenterTabs(rest)) {
                                                $scope.$digest();
                                            }

                                        break;
                                }
                            });
                    } else {
                        for (let i = 0; i < tabs.length; i++) {
                            removeCenterTab(tabs[i].id);
                        }

                        return true;
                    }

                    return false;
                }

                function removeCenterTab(id) {
                    let result = findCenterSplittedTabView(id);
                    if (result) {
                        const { tabsView, parent: splitView } = result;
                        tabsView.tabs.splice(result.index, 1);
                        if (tabsView.tabs.length === 0) {
                            //that's the last tab in the tabs view -> remove the wrapping split pane
                            removeSplitPane(splitView, tabsView);
                        }

                        shortenCenterTabsLabels();
                        return true;
                    }

                    return false;
                }

                function closeCenterTab(id) {
                    if (removeCenterTab(id)) {
                        $scope.$digest();
                    }
                }

                function reloadCenterTab(id, editorPath, params) {
                    let result = findCenterSplittedTabView(id);
                    if (result) {
                        const tab = result.tabsView.tabs[result.index];
                        tab.path = editorPath;
                        tab.params = params;
                    }
                }

                messageHub.onDidReceiveMessage(
                    'ide-core.openEditor',
                    function (data) {
                        $scope.$apply(
                            $scope.openEditor(
                                data.resourcePath,
                                data.resourceLabel,
                                data.contentType,
                                data.editorId,
                                data.extraArgs
                            )
                        );
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    'ide-core.setEditorDirty',
                    function (data) {
                        $scope.$apply($scope.setEditorDirty(data.resourcePath, data.isDirty));
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    'ide-core.closeEditor',
                    function (data) {
                        $scope.$apply($scope.closeEditor(data.resourcePath));
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    'ide-core.closeOtherEditors',
                    function (data) {
                        $scope.$apply($scope.closeOtherEditors(data.resourcePath));
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    'ide-core.closeAllEditors',
                    function () {
                        $scope.$apply($scope.closeAllEditors());
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    'ide-core.openView',
                    function (data) {
                        $scope.$apply($scope.openView(data.viewId, data.params));
                    },
                    true
                );

                messageHub.onDidReceiveMessage(
                    'ide-core.openPerspective',
                    function (data) {
                        let url = data.link;
                        if (data.params) {
                            let urlParams = '';
                            for (const property in data.params) {
                                urlParams += `${property} = ${encodeURIComponent(data.params[property])
                                    }& `
                            }
                            url += `? ${urlParams.slice(0, -1)} `;
                        }
                        window.location.href = url;
                    },
                    true
                );

                messageHub.onFileSaved(function (fileDescriptor) {
                    if (closingFileArgs) {
                        let fileName = `/${fileDescriptor.workspace}${fileDescriptor.path}`;
                        if (fileName === closingFileArgs.file) {
                            closeCenterTab(fileName);

                            let rest = closingFileArgs.tabs.filter(x => x.id !== closingFileArgs.file);
                            if (rest.length > 0) {
                                if (tryCloseCenterTabs(rest)) {
                                    $scope.$digest();
                                }
                            }

                            closingFileArgs = null;
                        }
                    }

                    if (reloadingFileArgs) {
                        const fileName = msg.data;
                        const { file, editorPath, params } = reloadingFileArgs;

                        if (fileName === file) {
                            reloadCenterTab(file, editorPath, params);
                            $scope.$digest();
                            reloadingFileArgs = null;
                        }
                    }
                });

                messageHub.onDidReceiveMessage('editor.focus.gained', function (msg) {
                    const file = msg.data.file;
                    $scope.focusedTabView = findCenterSplittedTabView(file).tabsView;
                    $scope.$digest();
                }, true);

                function shortenCenterTabsLabels() {

                    const getTabPath = tab => {
                        const index = tab.id.lastIndexOf('/');
                        return tab.id.substring(0, index > 0 ? index : x.id.length);
                    }

                    const allTabs = [];
                    forEachCenterSplittedTabView(pane => allTabs.push(...pane.tabs.filter(x => x.type === EDITOR)), $scope.centerSplittedTabViews);

                    const duplicatedTabs = allTabs.reduce((ret, tab) => {
                        let duplicates = ret.get(tab.label);
                        if (duplicates === undefined) {
                            duplicates = [];
                            ret.set(tab.label, duplicates);
                        }

                        duplicates.push(tab);

                        return ret;
                    }, new Map());

                    duplicatedTabs.forEach(tabs => {
                        if (tabs.length == 1) {
                            //no duplication so just reset the description
                            tabs[0].description = '';
                            return;
                        }

                        const paths = tabs.map(getTabPath);
                        const shortenedPaths = shortenPaths(paths);

                        tabs.forEach((tab, index) => tab.description = shortenedPaths[index]);
                    });
                }

                function shortenPaths(paths) {
                    const shortenedPaths = [];
                    const pathSeparator = '/';
                    const ellipsis = '..';

                    let match;
                    for (let pathIndex = 0; pathIndex < paths.length; pathIndex++) {
                        let path = paths[pathIndex];

                        if (path.indexOf(pathSeparator) === 0) {
                            prefix = path.substring(0, path.indexOf(pathSeparator) + pathSeparator.length);
                            path = path.substring(path.indexOf(pathSeparator) + pathSeparator.length);
                        }

                        match = true;

                        const segments = path.split(pathSeparator);
                        for (let subpathLength = 1; match && subpathLength <= segments.length; subpathLength++) {
                            for (let start = segments.length - subpathLength; match && start >= 0; start--) {
                                match = false;

                                let subpath = segments.slice(start, start + subpathLength).join(pathSeparator);

                                for (let otherPathIndex = 0; !match && otherPathIndex < paths.length; otherPathIndex++) {

                                    if (otherPathIndex !== pathIndex && paths[otherPathIndex] && paths[otherPathIndex].indexOf(subpath) > -1) {
                                        const isSubpathEnding = (start + subpathLength === segments.length);
                                        const subpathWithSep = (start > 0 && paths[otherPathIndex].indexOf(pathSeparator) > -1) ? pathSeparator + subpath : subpath;
                                        const isOtherPathEnding = paths[otherPathIndex].endsWith(subpathWithSep);

                                        match = !isSubpathEnding || isOtherPathEnding;
                                    }
                                }

                                if (!match) {
                                    let result = '';

                                    if (start > 0) {
                                        result += ellipsis + pathSeparator;
                                    }

                                    result += subpath;

                                    if (start + subpathLength < segments.length) {
                                        result += pathSeparator + ellipsis;
                                    }

                                    shortenedPaths[pathIndex] = result;
                                }
                            }
                        }

                        if (match) {
                            shortenedPaths[pathIndex] = path;
                        }
                    }

                    return shortenedPaths;
                }

                $scope.openEditor = function (resourcePath, resourceLabel, contentType, editorId, extraArgs = null) {
                    if (resourcePath) {
                        let editorPath = Editors.editorProviders[editorId];
                        let eId = editorId;
                        if (!editorPath) {
                            let editors = Editors.editorsForContentType[contentType];
                            if (editors && editors.length > 0) {
                                for (let i = 0; i < editors.length; i++) {
                                    if (editors[i].id !== Editors.defaultEditor.id) {
                                        eId = editors[i].id;
                                        break;
                                    }
                                }
                                if (!eId) eId = Editors.defaultEditor.id;
                            } else {
                                eId = Editors.defaultEditor.id;
                            }
                            editorPath = Editors.editorProviders[eId];
                        }

                        let params = Object.assign({
                            file: resourcePath,
                            contentType: contentType
                        }, extraArgs || {});

                        if (eId === 'flowable')
                            editorPath += resourcePath;

                        let result = findCenterSplittedTabView(resourcePath);
                        let currentTabsView = result ? result.tabsView : getCurrentCenterSplittedTabViewPane();
                        if (result) {
                            currentTabsView.selectedTab = resourcePath;
                            let fileTab = currentTabsView.tabs[result.index];
                            if (fileTab.path !== editorPath) {
                                tryReloadCenterTab(fileTab, editorPath, params);
                            }
                        } else {
                            let fileTab = {
                                id: resourcePath,
                                type: EDITOR,
                                label: resourceLabel,
                                path: editorPath,
                                params: params
                            };

                            currentTabsView.selectedTab = resourcePath;
                            currentTabsView.tabs.push(fileTab);
                        }

                        shortenCenterTabsLabels();

                        $scope.$digest();
                    } else {
                        console.error('openEditor: resourcePath is undefined');
                    }
                };
                $scope.closeEditor = function (resourcePath) {
                    let result = findCenterSplittedTabView(resourcePath);
                    if (result) {
                        let tab = result.tabsView.tabs[result.index];
                        if (tryCloseCenterTabs([tab])) {
                            $scope.$digest();
                        }
                    }
                };
                $scope.closeOtherEditors = function (resourcePath) {
                    let result = findCenterSplittedTabView(resourcePath);
                    if (result) {
                        let rest = result.tabsView.tabs.filter(x => x.id !== resourcePath);
                        if (rest.length > 0) {
                            if (tryCloseCenterTabs(rest)) {
                                $scope.$digest();
                            }
                        }
                    }
                };
                $scope.closeAllEditors = function () {
                    forEachCenterSplittedTabView(pane => {
                        if (tryCloseCenterTabs(pane.tabs.slice())) {
                            $scope.$digest();
                        }
                    }, $scope.centerSplittedTabViews);
                };
                $scope.setEditorDirty = function (resourcePath, dirty) {
                    let result = findCenterSplittedTabView(resourcePath);
                    if (result) {
                        let fileTab = result.tabsView.tabs[result.index];
                        fileTab.dirty = dirty;
                        $scope.$digest();
                    }
                };
                $scope.openView = function (viewId, params = {}) {
                    if (params !== undefined && !(typeof params === 'object' && !Array.isArray(params) && params !== null))
                        throw Error("openView: params must be an object");
                    let view = $scope.views.find(v => v.id === viewId);
                    if (view) {
                        view.params = params;
                        if (view.region.startsWith('left')) {
                            let explorerViewTab = findView($scope.explorerTabs, view);
                            if (explorerViewTab) {
                                explorerViewTab.hidden = false;
                                explorerViewTab.expanded = true;
                            } else {
                                explorerViewTab = mapViewToTab(view);
                                explorerViewTab.expanded = true;
                                $scope.explorerTabs.push(explorerViewTab);
                            }

                        } else if (view.region === 'center-middle' || view.region === 'center-top' || view.region === 'center') {
                            let result = findCenterSplittedTabView(view.id);
                            let currentTabsView = result ? result.tabsView : getCurrentCenterSplittedTabViewPane();
                            if (result) {
                                currentTabsView.selectedTab = view.id;
                            } else {
                                let centerViewTab = mapViewToTab(view);
                                currentTabsView.selectedTab = view.id;
                                currentTabsView.tabs.push(centerViewTab);
                            }
                        } else {
                            let bottomViewTab = findView($scope.bottomTabs, view);
                            if (bottomViewTab) {
                                $scope.selection.selectedBottomTab = bottomViewTab.id;
                            } else {
                                bottomViewTab = mapViewToTab(view);
                                $scope.selection.selectedBottomTab = bottomViewTab.id;
                                $scope.bottomTabs.push(bottomViewTab);
                            }

                            if ($scope.isBottomPaneCollapsed())
                                $scope.expandBottomPane();
                        }
                    }
                };
            }],
            templateUrl: '/services/v4/web/ide-core/ui/templates/layout.html'
        };
    }])
    .directive('split', ['SplitPaneState', function (SplitPaneState) {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            scope: {
                direction: '@',
                width: '@',
                height: '@',
                state: '=?'
            },
            controller: ['$scope', '$element', function ($scope, $element) {
                $scope.panes = [];
                $scope.state = $scope.state || [];

                this.addPane = function (pane) {
                    $scope.panes.push(pane);
                    $scope.state.push(SplitPaneState.EXPANDED);

                    $scope.panes.sort((a, b) => {
                        let elementA = a.element[0];
                        let elementB = b.element[0];
                        if (elementA.previousElementSibling === null || elementB.nextElementSibling === null) return -1;
                        if (elementA.nextElementSibling === null || elementB.previousElementSibling === null) return 1;
                        if (elementA.nextElementSibling === elementB || elementB.previousElementSibling === elementA) return -1;
                        if (elementB.nextElementSibling === elementA || elementA.previousElementSibling === elementB) return 1;
                        return 0;
                    });
                };

                this.removePane = function (pane) {
                    let index = $scope.panes.indexOf(pane);
                    if (index !== -1) {
                        $scope.panes.splice(index, 1);
                    }
                };

                function normalizeSizes(sizes, index = -1) {
                    let isOpen = (size, i) => {
                        return Math.floor(size) > 0 && (index === -1 || index !== i);
                    };

                    let totalSize = sizes.reduce((x, y) => x + y, 0);
                    if (totalSize !== 100) {
                        let openCount = sizes.reduce((count, size, i) => isOpen(size, i) ? count + 1 : count, 0);
                        if (openCount > 0) {
                            let d = (100 - totalSize) / openCount;
                            for (let i = 0; i < sizes.length; i++) {
                                if (isOpen(sizes[i], i))
                                    sizes[i] += d;
                            }
                        }
                    }
                }

                $scope.$watch('direction', function (newDirection, oldDirection) {
                    if (oldDirection)
                        $element.removeClass(oldDirection);

                    $element.addClass(['dg-split', newDirection || 'horizontal']);
                });

                $scope.$watchCollection('panes', function () {
                    if ($scope.split) {
                        $scope.split.destroy();
                        $scope.split = null;
                    }

                    if ($scope.panes.length === 0 || $scope.panes.some(a => a.element === undefined)) {
                        return;
                    }

                    if ($scope.panes.length === 1) {
                        $scope.panes[0].element.css('width', '100%');
                        $scope.panes[0].element.css('height', '100%');
                        return;
                    }

                    let sizes = $scope.panes.map(pane => pane.size || 0);

                    normalizeSizes(sizes);

                    let minSizes = $scope.panes.map(pane => pane.minSize);
                    let elements = $scope.panes.map(pane => pane.element[0]);
                    let snapOffsets = $scope.panes.map(pane => pane.snapOffset);

                    $scope.split = Split(elements, {
                        direction: $scope.direction,
                        sizes: sizes,
                        minSize: minSizes,
                        expandToMin: true,
                        gutterSize: 1,
                        gutterAlign: 'start',
                        snapOffset: snapOffsets,
                        onDragEnd: function (newSizes) {
                            for (let i = 0; i < newSizes.length; i++) {
                                $scope.state[i] = Math.floor(newSizes[i]) === 0 ? SplitPaneState.COLLAPSED : SplitPaneState.EXPANDED;
                            }
                            $scope.$apply();
                        },
                    });
                });

                $scope.$watchCollection('state', function (newState, oldState) {
                    if (newState.length === oldState.length) {
                        //Process the collapsing first
                        for (let i = 0; i < newState.length; i++) {
                            if (newState[i] !== oldState[i]) {
                                if (newState[i] === SplitPaneState.COLLAPSED) {
                                    let sizes = $scope.split.getSizes();
                                    let size = Math.floor(sizes[i]);
                                    if (size > 0) {
                                        $scope.panes[i].lastSize = size;
                                        $scope.split.collapse(i);
                                    }
                                }
                            }
                        }
                        // ... and then the expanding/restore if necessary
                        for (let i = 0; i < newState.length; i++) {
                            if (newState[i] !== oldState[i]) {
                                if (newState[i] === SplitPaneState.EXPANDED) {
                                    let sizes = $scope.split.getSizes();
                                    let size = Math.floor(sizes[i]);
                                    if (size === 0) {
                                        let pane = $scope.panes[i];
                                        sizes[i] = pane.lastSize || pane.size;
                                        normalizeSizes(sizes, i);
                                        $scope.split.setSizes(sizes);
                                    }
                                }
                            }
                        }
                    }
                });
            }]
        };
    }])
    .directive('splitPane', function () {
        return {
            restrict: 'E',
            require: '^split',
            replace: true,
            transclude: true,
            scope: {
                size: '@',
                minSize: '@',
                snapOffset: '@'
            },
            link: function (scope, element, attrs, bgSplitCtrl) {
                element.addClass('dg-split-pane');

                let paneData = scope.paneData = {
                    element: element,
                    size: Number(scope.size),
                    minSize: Number(scope.minSize),
                    snapOffset: Number(scope.snapOffset)
                };

                bgSplitCtrl.addPane(paneData);

                scope.$on('$destroy', function () {
                    bgSplitCtrl.removePane(paneData);
                });
            }
        }
    })
    .directive('explorerToolbar', ['perspective', function (perspective) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                items: '='
            },
            link: function (scope) {
                scope.hidden = true;
                scope.name = perspective.name;

                scope.toggle = function () {
                    scope.hidden = !scope.hidden;
                };

                scope.hide = function () {
                    scope.hidden = true;
                };

                scope.isLastVisibleItem = function (item) {
                    return !item.hidden && scope.items.reduce((c, x) => {
                        if (x.id !== item.id && !x.hidden)
                            c++;
                        return c;
                    }, 0) === 0;
                };

                scope.toggleVisibility = function (item) {
                    // TODO
                };
            },
            templateUrl: '/services/v4/web/ide-core/ui/templates/toolbar.html',
        };
    }])
    .directive('accordion', ['$window', function ($window) {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            scope: {},
            controller: function ($scope, $element) {
                let views = $scope.views = [];

                let availableHeight;

                function updateContentHeights(collapsingView = null) {
                    let expandedViews = $scope.views.filter(view => view.expanded);
                    let expandedViewsCount = expandedViews.length;
                    if (collapsingView) expandedViewsCount--;

                    let panelHeight = expandedViewsCount > 0 ? availableHeight / expandedViewsCount : 0;

                    for (let i = 0; i < $scope.views.length; i++) {
                        let view = $scope.views[i];
                        view.style = {
                            height: view.expanded && view !== collapsingView ? panelHeight + 'px' : '0'
                        };
                    }
                }

                function updateSize() {
                    let totalHeight = $element[0].clientHeight;
                    let headersHeight = getHeadersHeight();

                    availableHeight = totalHeight - headersHeight;

                    updateContentHeights();
                }

                function getHeadersHeight() {
                    let headers = $element[0].querySelectorAll('.fd-panel__header');

                    let h = 0;
                    for (let i = 0; i < headers.length; i++) {
                        h += headers[i].offsetHeight;
                    }
                    return h;
                }

                this.addView = function (view) {
                    if (views.length === 0)
                        view.expanded = true;

                    views.push(view);

                    updateContentHeights();
                }

                this.removeView = function (view) {
                    let index = views.indexOf(view);
                    if (index >= 0)
                        views.splice(index, 1);

                    updateSize();
                }

                this.updateHeights = function (view) {
                    updateContentHeights(view);
                }

                this.updateSizes = function () {
                    updateSize();
                }

                angular.element($window).on('resize', function () {
                    $scope.$apply(updateSize);
                });

                $scope.$on('$destroy', function () {
                    angular.element($window).off('resize');
                });
            },
            template: '<div class="dg-accordion" ng-transclude></div>'
        };
    }])
    .directive('accordionPane', ['$timeout', 'perspective', function ($timeout, perspective) {
        return {
            restrict: 'E',
            replace: true,
            require: '^accordion',
            scope: {
                view: '<'
            },
            link: function (scope, element, attrs, accordionCtrl) {
                accordionCtrl.addView(scope.view);

                scope.toggleView = function (view) {
                    if (!view.expanded) {
                        view.expanded = true;
                        $timeout(accordionCtrl.updateHeights);
                    } else {
                        accordionCtrl.updateHeights(view);
                        $timeout(function () {
                            view.expanded = false;
                        }, 200);
                    }
                }

                scope.getParams = function () {
                    if (!scope.view.params) {
                        scope.view.params = {
                            container: 'layout',
                            perspectiveId: perspective.id,
                        };
                    } else {
                        scope.view.params['container'] = 'layout';
                        scope.view.params['perspectiveId'] = perspective.id;
                    }
                    return JSON.stringify(scope.view.params);
                }

                scope.$watch('view', function () {
                    accordionCtrl.updateSizes();
                }, true);

                scope.$on('$destroy', function () {
                    accordionCtrl.removeView(scope.view);
                });
            },
            templateUrl: '/services/v4/web/ide-core/ui/templates/accordionPane.html'
        };
    }])
    .directive('tabs', function () {
        return {
            restrict: 'E',
            transclude: {
                'buttons': '?buttons',
                'panes': 'panes'
            },
            replace: true,
            scope: {
                selectedPane: '=',
                focused: '<',
                closable: '@',
                removeTab: '&',
            },
            controller: ['$scope', '$element', 'messageHub', function ($scope, $element, messageHub) {
                let panes = $scope.panes = [];

                $scope.isPaneSelected = function (pane) {
                    return pane.id === $scope.selectedPane;
                }

                $scope.select = function (pane) {
                    if (this.isPaneSelected(pane)) {
                        requestFocus();
                        return;
                    }

                    $scope.selectedPane = pane.id;
                }

                $scope.tabClick = function (pane, $event) {
                    if ($event.target.classList.contains('fd-button')) {
                        $scope.removeTab({ pane: pane });
                        return;
                    }

                    this.select(pane);
                };

                $scope.moreTabsItemClick = function (pane) {
                    this.select(pane);
                    $scope.moreTabsExpanded = false;
                }

                $scope.moreTabsExpanded = false;

                this.addPane = function (pane) {
                    if (!$scope.selectedPane && panes.length == 0) {
                        $scope.select(pane);
                    }
                    panes.push(pane);
                    updateTabsVisibilityDelayed();
                }

                this.removePane = function (pane) {
                    let index = panes.indexOf(pane);
                    if (index >= 0)
                        panes.splice(index, 1);

                    let nextSelectedPane;
                    if ($scope.isPaneSelected(pane)) {
                        if ($scope.lastSelectedPane)
                            nextSelectedPane = panes.find(p => p.id === $scope.lastSelectedPane);

                        if (!nextSelectedPane && panes.length > 0) {
                            if (index < 0)
                                index = 0
                            else if (index >= panes.length)
                                index = panes.length - 1;

                            nextSelectedPane = panes[index];
                        }
                    }

                    if (nextSelectedPane) {
                        $scope.select(nextSelectedPane);
                        $scope.lastSelectedPane = null;
                    }

                    updateTabsVisibilityDelayed();
                }

                this.getSelectedPane = function () {
                    return $scope.selectedPane;
                }

                const requestFocus = function () {
                    messageHub.postMessage('editor.focus.gain', { file: $scope.selectedPane }, true);
                }

                const updateTabsVisibility = (containerWidth = -1) => {
                    if (containerWidth === -1)
                        containerWidth = tabsListEl[0].clientWidth;

                    const tabElements = $element.find('li.fd-tabs__item[role="tab"]');
                    const selectedTabEl = $element.find('li.fd-tabs__item[aria-selected="true"]');
                    const moreButtonEl = $element.find('li.fd-tabs__item[role="button"]');
                    const moreTabsPopoverBody = $element.find('li.fd-tabs__item[role="button"] .fd-popover__body');
                    const moreTabsBtnElements = moreTabsPopoverBody.find('.fd-button');

                    selectedTabEl.removeClass('dg-hidden');
                    moreButtonEl.removeClass('dg-hidden');

                    let width = selectedTabEl.length > 0 ? selectedTabEl.outerWidth(true) : 0;
                    let moreBtnWidth = moreButtonEl.outerWidth(true);

                    let tabVisible = true;
                    let selectedTabVisibile = true;

                    if (width > containerWidth - moreBtnWidth) {
                        tabVisible = false;
                        selectedTabVisibile = false;
                        moreTabsPopoverBody.removeClass('fd-popover__body--right').addClass('fd-popover__body--left');
                    } else {
                        moreTabsPopoverBody.removeClass('fd-popover__body--left').addClass('fd-popover__body--right');
                    }

                    let nonSelectedTabsIndex = 0;
                    for (let i = 0; i < tabElements.length; i++) {
                        const tabEl = tabElements[i];
                        const moreTabEl = moreTabsBtnElements[i];
                        const $tabEl = $(tabEl);
                        const $moreTabEl = $(moreTabEl);

                        if (selectedTabVisibile && $tabEl.attr('aria-selected') === 'true') {
                            $moreTabEl.addClass('dg-hidden');
                            continue;
                        }

                        if (!tabVisible) {
                            $tabEl.addClass('dg-hidden');
                            $moreTabEl.removeClass('dg-hidden');
                            continue;
                        }

                        $tabEl.removeClass('dg-hidden');

                        width += $tabEl.outerWidth(true);

                        let availableWidth = containerWidth;
                        if (nonSelectedTabsIndex < tabElements.length - 2)
                            availableWidth -= moreBtnWidth;

                        if (width > availableWidth) {
                            tabVisible = false;
                            $tabEl.addClass('dg-hidden');
                            $moreTabEl.removeClass('dg-hidden');
                        } else {
                            $moreTabEl.addClass('dg-hidden');
                        }

                        nonSelectedTabsIndex++;
                    }

                    if (tabVisible) {
                        moreButtonEl.addClass('dg-hidden');
                    }
                }

                const updateTabsVisibilityDelayed = () => {
                    setTimeout(updateTabsVisibility, 0);
                }

                const ro = new ResizeObserver(entries => {
                    const width = entries[0].contentRect.width;
                    updateTabsVisibility(width);
                });

                const tabsListEl = $element.find('ul.fd-tabs');
                ro.observe(tabsListEl[0]);

                $scope.$watch('selectedPane', function (newValue, oldValue) {
                    $scope.lastSelectedPane = oldValue;
                    requestFocus();
                    updateTabsVisibilityDelayed();
                });

                $scope.$on('$destroy', function () {
                    ro.unobserve(tabsListEl[0]);
                });
            }],
            templateUrl: '/services/v4/web/ide-core/ui/templates/tabs.html'
        };
    })
    .directive('tabPane', ['perspective', function (perspective) {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            require: '^tabs',
            scope: {
                tab: '='
            },
            link: function (scope, element, attrs, tabsCtrl) {
                tabsCtrl.addPane(scope.tab);

                scope.isPaneSelected = function () {
                    return scope.tab.id === tabsCtrl.getSelectedPane();
                }

                scope.getParams = function () {
                    if (!scope.tab.params) {
                        scope.tab.params = {
                            container: 'layout',
                            perspectiveId: perspective.id,
                        };
                    } else {
                        scope.tab.params['container'] = 'layout';
                        scope.tab.params['perspectiveId'] = perspective.id;
                    }
                    return JSON.stringify(scope.tab.params);
                }

                scope.$on('$destroy', function () {
                    tabsCtrl.removePane(scope.tab);
                });
            },
            template: `<div aria-expanded="{{isPaneSelected()}}" class="fd-tabs__panel" role="tabpanel" ng-transclude>
                <iframe loading="lazy" ng-src="{{tab.path}}" data-parameters="{{getParams()}}"></iframe>
            </div>`
        };
    }])
    .directive('splittedTabs', function () {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            scope: {
                direction: '=',
                panes: '=',
                focusedPane: '<',
                removeTab: '&',
                moveTab: '&',
                splitTabs: '&'
            },
            link: function (scope) {
                scope.onRemoveTab = function (pane) {
                    scope.removeTab({ pane: pane });
                };

                scope.onSplitTabs = function (direction, pane) {
                    scope.splitTabs({ direction, pane });
                }

                scope.splitHorizontally = function (pane) {
                    scope.splitTabs({ direction: 'horizontal', pane });
                };

                scope.splitVertically = function (pane) {
                    scope.splitTabs({ direction: 'vertical', pane });
                }

                scope.canSplit = function (pane) {
                    if (pane.tabs.length < 2)
                        return false;

                    const tab = pane.tabs.find(x => x.id === pane.selectedTab);
                    return tab && !tab.dirty;
                };

                scope.isFocused = function (pane) {
                    return pane === scope.focusedPane;
                }
            },
            templateUrl: '/services/v4/web/ide-core/ui/templates/splittedTabs.html'
        };
    });