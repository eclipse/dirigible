(function(angular){
"use strict";

angular.module('i18n', ['ngI18n'])
.value('ngI18nConfig', {
    //defaultLocale should be in lowercase and is required!!
    defaultLocale:'en',
    //supportedLocales is required - all locales should be in lowercase!!
    supportedLocales:['en', 'bg'],
    //without leading and trailing slashes, default is i18n
    basePath:'i18n',
    //default is false
    cache:true
})
.service('i18n', ['$q', 'ngI18nConfig', 'ngI18nResourceBundle', function($q, ngI18nConfig, ngI18nResourceBundle){
	
	var bundle;

	var load = function(_locale){
		 _locale = _locale ?  {locale: _locale} : undefined;
		 return $q.all([ngI18nResourceBundle.get({locale:ngI18nConfig.defaultLocale}), ngI18nResourceBundle.get(_locale)])
				.then(function(responses){
					var bundles = responses.map(function(response) {
					    return response.data;
					});
					var defaultBundle = bundles[0];
					var localeBundle = bundles[1];
					if(!localeBundle)
						return defaultBundle;
					return Object.assign(defaultBundle, localeBundle);
				});
	};
	
	bundle = load();	
	
	var getMessage = function(key, _bundle){
		var path = key.split('.');
		var msg = _bundle;
		for(var i=0; i<path.length; i++){
			if(msg.hasOwnProperty(path[i]))
				msg = msg[path[i]];
		}
		if(msg.constructor !== String)
			throw Error('Unknown resource bundle key: ' + key);
		return msg;
	};
	
	var resolve = function(msg, formatArgs){
		if(formatArgs && formatArgs.length>0){
			msg = msg.replace(/{\d+}/g, function(param){
				var idx = param.substring(1,2);
    			return formatArgs[idx];
    		});
		}
		return msg;
	};
	
	return $q.when(bundle)
		   .then(function(_bundle){
		   		return {
			    	format: function(key){
			    		var msg = getMessage(key, _bundle);
			    		var formatArgs = Array.prototype.slice.call(arguments);
						formatArgs.splice(0, 1);
						return resolve.call(this, msg, formatArgs);
			    	},
			    	set: function(locale){
			    		load(locale);
			    	},
			    	bundle: _bundle
		   		};
		   });
}]);

})(angular);
