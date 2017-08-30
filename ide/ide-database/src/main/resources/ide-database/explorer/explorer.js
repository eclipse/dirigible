var messageHub = new FramesMessageHub();

messageHub.fireFileOpen = function(fileDescriptor){
	messageHub.post({data: fileDescriptor}, 'fileselected');
}

angular.module('database', []).controller('DatabaseController', function ($scope, $http) {
					
	var databasesSvcUrl = "/services/v3/ide/databases";
	$scope.selectedDatabase;
	$scope.jstree;
	
	$http.get(databasesSvcUrl)
		.success(function(data) {
			$scope.databases = data;
			if(data[0]) {
				$scope.selectedDatabase = data[0];
				$http.get(databasesSvcUrl + "/" + $scope.selectedDatabase).success(function(data) {
					$scope.datasources = data;
					if(data[0]) {
						$scope.selectedDatasource = data;
						$scope.refreshDatabase();
					}
				});
			}
			$scope.refreshDatabase();
	});
	
	$scope.refreshDatabase = function() {
		if($scope.selectedDatabase && $scope.selectedDatasource){
				$http.get(databasesSvcUrl + '/' + $scope.selectedDatabase + '/' + $scope.selectedDatasource)
					.success(function(data) {
						$scope.datasource = data;
						this.baseUrl = databasesSvcUrl + '/' + $scope.selectedDatabase + '/' + $scope.selectedDatasource;
						var schemas = $scope.datasource.schemas.map(function(schemas){
							return build(schemas);
						})
						if ($scope.jstree) {
							$('.database').jstree(true).settings.core.data = schemas;
							$('.database').jstree(true).refresh();
						} else {
						  $scope.jstree = $('.database').jstree({
							"core" : {
							  "data" : schemas,
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
							//
						  })
						 .on('dblclick.jstree', function (evt, node) {
							 var data= $('.database').jstree().get_selected(true);
							 var kind = $('.database').jstree().get_node(evt.target).original.kind;
							 // if(['table'].indexOf(type)<0)
// 								messageHub.fireFileOpen(data[0].original._file);
						  })
						  .on('open_node.jstree', function(evt, data) {
							//data.instance.set_icon(data.node, 'fa fa-folder-open-o');
						  })
						  .on('close_node.jstree', function(evt, data) {
							//data.instance.set_icon(data.node, 'fa fa-folder-o');
						  });
						}
				}.bind(this));
			}
	};
	
	var build = function(f){
		var children = [];
		var icon = 'fa fa-th-large';
		if(f.kind=='schema') {
			children = f.tables.map(function(_table){
				return build(_table)
			});
			icon = 'fa fa-database';
		} else if(f.kind=='table') {
			children = f.columns.map(function(_column){
				return build(_column)
			});
			icon = 'fa fa-table';
		}
		f.label = f.name;
		return {
			"text": f.name,
			"children": children,
			"type": f.kind,
			"_file": f,
			"icon": icon
		}
	}
	
	$scope.selected = function(evt){
		$http.get(databasesSvcUrl + '/' + $scope.selectedDatabase)
				.success(function(data) {
						$scope.databases = data.databases;
						$scope.refreshDatabase();
		});
	};

});
	
