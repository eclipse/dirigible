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
	this.evtNamePrefix = this.evtNamePrefix ? (this.evtNamePrefix+this.evtNameDelimiter): this.evtNamePrefix;
	var message = function(evtName, data){
		if(!evtName)
			throw Error('evtname argument must be a valid string, identifying an existing event');
		messageHub.post({data: data}, this.evtNamePrefix + evtName);
	}.bind(this);
	var on = function(evtName, callbackFunc){
		if(typeof callbackFunc !== 'function')
			throw Error('Callback argument must be a function');
		messageHub.subscribe(callbackFunc, evtName);
	};
	return {
		message: message,
		on: on
	};
  }];
})
.factory('Theme', ['$resource', function($resource){
	var themeswitcher = $resource('/services/v3/core/theme?name=:themeName', {themeName: 'default'});
	var themes = {
		"default": "/services/v3/web/resources/themes/default/bootstrap.min.css",
		"wendy" : "/services/v3/web/resources/themes/wendy/bootstrap.min.css",
		"baroness" : "/services/v3/web/resources/themes/baroness/bootstrap.min.css"
	};
	return {
		changeTheme: function(themeName){
			return themeswitcher.get({'themeName':themeName});
		},
		themeUrl: function(themeName){
			return themes[themeName];
		}			
	}
}])
.service('Perspectives', ['$resource', function($resource){
	return $resource('../../js/ide/services/perspectives.js');
}])
.service('Menu', ['$resource', function($resource){
	return $resource('../../js/ide/services/menu.js');
}])
.service('User', ['$http', function($http){
	return {
		get: function(){
			var user = {};
			$http({
				url: '../../js/ide/services/user-name.js',
				method: 'GET'
			}).success(function(data){
				user.name = data;
			});
			return user;
		}
	};
}])
/**
 * Creates a map object associating a view factory function with a name (id)
 */
.factory('ViewFactories', function(){
	return {
		"frame": function(container, componentState){
			container.setTitle(componentState.label || 'View');
			container.getElement().empty().html( '<iframe src="'+componentState.path+'"></iframe>' );
		},
		"Editor": function(container, componentState){
			this.setContent = function(path){
				if (path) {
					container.getElement().empty().html( '<iframe src="../ide-orion/editor.html?file='+path+'"></iframe>' );
				} else {
					container.setTitle( 'Welcome' );
					container.getElement().empty().html( '<iframe src="welcome.html"></iframe>' );
				}
				
			};
			this.setContent(componentState.path);
		}
	}
})
/**
 * Wrap the ViewRegistry class in an angular service object for dependency injection
 */
.service('ViewRegistrySvc', ViewRegistry)
/**
 * A view registry instance factory, using remote service for intializing the view definitions
 */
.factory('viewRegistry', ['ViewRegistrySvc', '$resource', 'ViewFactories', function(ViewRegistrySvc, $resource, ViewFactories){
	Object.keys(ViewFactories).forEach(function(factoryName){
		ViewRegistrySvc.factory(factoryName, ViewFactories[factoryName]);
	});		
	var get = function(){
		return $resource('../../js/ide/services/views.js').query().$promise
				.then(function(data){
					//TODO: load everyhting from the sevice instead of transforming, once its features all data we need here
					data = data.map(function(v){
						v.id = v.name.toLowerCase();
						v.label = v.name;
						v.factory = 'frame';
						v.settings = {
							"path": v.link
						}
						if(['workspace','import'].indexOf(v.id)>-1)
							v.region = 'left-top';
						else if(['console','preview','properties','terminal'].indexOf(v.id)>-1)
							v.region = 'center-bottom';
						return v;
					});
					data.push({ "id": "editor", "factory": "Editor", "region": "center-middle", "label":"Editor", "settings": {}});
					data.push({ "id": "preview", "factory": "frame", "region": "center-bottom", "label":"Preview", "settings": {"path":  "../ide-preview/preview.html"}});
					data.push({ "id": "properties", "factory": "frame", "region": "center-bottom", "label":"Properties", "settings": {"path":  "../ide/properties.html"}});
					//register views
					data.forEach(function(viewDef){
						ViewRegistrySvc.view(viewDef.id, viewDef.factory, viewDef.region, viewDef.label,  viewDef.settings);
					});
					return ViewRegistrySvc;
				});
	};
	
	return {
		get: get
	};
}]);