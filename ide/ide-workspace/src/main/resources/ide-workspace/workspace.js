var messageHub = new FramesMessageHub();

messageHub.fireFileOpen = function(fileDescriptor){
	messageHub.post({data: fileDescriptor}, 'fileselected');
}

angular.module('workspace', []).controller('WorkspaceController', function ($scope, $http) {
					
	var workspacesSvcUrl = "/services/v3/ide/workspaces";
	$scope.selectedWs;
	$scope.jstree;
	
	$http.get(workspacesSvcUrl)
		.success(function(data) {
			$scope.workspaces = data;
			if(data[0]) {
				$scope.selectedWs = data[0];
			} else {
				$http.post(workspacesSvcUrl + "/workspace").success(function(data) {
					$scope.selectedWs = data.name;
					$scope.refreshWorkspace();
				});
			}
			$scope.refreshWorkspace();
	});
	
	$scope.refreshWorkspace = function() {
		if($scope.selectedWs){
				$http.get(workspacesSvcUrl + '/' + $scope.selectedWs)
					.success(function(data) {
						$scope.projects = data.projects;
						this.baseUrl = workspacesSvcUrl + '/' + $scope.selectedWs;
						var projects = $scope.projects.map(function(project){
							return build(project);
						})
						if ($scope.jstree) {
							$('.workspace').jstree(true).settings.core.data = projects;
							$('.workspace').jstree(true).refresh();
						} else {
						  $scope.jstree = $('.workspace').jstree({
							"core" : {
							  "data" : projects,
							  "themes": {
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
							  'types': {
								  'default': {
									  'icon': "fa fa-file"
								  },
								  'folder': {
									  'icon': "fa fa-folder"
								  },
								  'file': {
									  'icon': "fa fa-file"
								  },
								  'project': {
									  'icon': "fa fa-folder"
								  }
							  },
					          'contextmenu' : {
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
														var inst = $.jstree.reference(data.reference),
															obj = inst.get_node(data.reference);
														inst.create_node(obj, { type : "default" }, "last", function (new_node) {
															setTimeout(function () { inst.edit(new_node); },0);
														});
													}
												},
												"create_file" : {
													"label"				: "File",
													"action"			: function (data) {
														var inst = $.jstree.reference(data.reference),
															obj = inst.get_node(data.reference);
														inst.create_node(obj, { type : "file" }, "last", function (new_node) {
															setTimeout(function () { inst.edit(new_node); },0);
														});
													}
												}
											};
											
										}										
										ctxmenu.remove.shortcut = 46;
										ctxmenu.remove.shortcut_label = 'Del';
										ctxmenu.rename.shortcut = 113;
										ctxmenu.rename.shortcut_label = 'F2';
										return ctxmenu;
									}
								},
								"plugins": ['state','dnd','sort','types','contextmenu','unique']
						  })
						 .on('select_node.jstree', function (e, data) {
							//messageHub.post({data: data.node.original._file});
						  })
						 .on('dblclick.jstree', function (evt, node) {
							 var data= $('.workspace').jstree().get_selected(true);
							 var type = $('.workspace').jstree().get_node(evt.target).original.type;
							 if(['folder','project'].indexOf(type)<0)
								messageHub.fireFileOpen(data[0].original._file);
						  })
						  .on('open_node.jstree', function(evt, data) {
							data.instance.set_icon(data.node, 'fa fa-folder-open-o');
						  })
						  .on('close_node.jstree', function(evt, data) {
							data.instance.set_icon(data.node, true);
						  })
						  .on('delete_node.jstree', function (e, data) {
						  		$http.delete(workspacesSvcUrl + data.node.original._file.path);
								$.get('?operation=delete_node', { 'id' : data.node.id })
									.fail(function () {
										data.instance.refresh();
									});
							})
							.on('create_node.jstree', function (e, data) {
								$.get('?operation=create_node', { 'type' : data.node.type, 'id' : data.node.parent, 'text' : data.node.text })
									.done(function (d) {
										data.instance.set_id(data.node, d.id);
									})
									.fail(function () {
										data.instance.refresh();
									});
							})
							.on('rename_node.jstree', function (e, data) {
								$.get('?operation=rename_node', { 'id' : data.node.id, 'text' : data.text })
									.done(function (d) {
										data.instance.set_id(data.node, d.id);
									})
									.fail(function () {
										data.instance.refresh();
									});
							}.bind(this))
							.on('move_node.jstree', function (e, data) {
								$.get('?operation=move_node', { 'id' : data.node.id, 'parent' : data.parent })
									.done(function (d) {
										//data.instance.load_node(data.parent);
										data.instance.refresh();
									})
									.fail(function () {
										data.instance.refresh();
									});
							})
							.on('copy_node.jstree', function (e, data) {
								$.get('?operation=copy_node', { 'id' : data.original.id, 'parent' : data.parent })
									.done(function (d) {
										//data.instance.load_node(data.parent);
										data.instance.refresh();
									})
									.fail(function () {
										data.instance.refresh();
									});
							});
						}
				}.bind(this));
			}
	};
	
	var build = function(f){
		var children = [];
		if(f.type=='folder' || f.type=='project'){
			children = f.folders.map(function(_folder){
				return build(_folder)
			});
			var _files = f.files.map(function(_file){
				return build(_file)
			})
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
	}
	
	$scope.selected = function(evt){
		//$scope.selectedWs = this;
		$http.get(workspacesSvcUrl + '/' + $scope.selectedWs)
				.success(function(data) {
						$scope.projects = data.projects;
						$scope.refreshWorkspace();
		});
	};

});
	
