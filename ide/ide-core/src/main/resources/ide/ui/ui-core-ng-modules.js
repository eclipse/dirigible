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
	};
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
.service('User', ['$http', '$timeout', function($http, $timeout){
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
}]);