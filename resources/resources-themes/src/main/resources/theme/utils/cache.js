var java = require('core/v3/java');
   
exports.getCache = function() {
	var cacheInstnace = java.call("org.eclipse.dirigible.commons.config.ResourcesCache", "getThemeCache", [], true);
	var cache = new Cache();
	cache.uuid = cacheInstnace.uuid;
  	return cache;
};

function Cache() {

	this.getTag = function(tag) {
		return java.invoke(this.uuid, 'getTag', [tag]);
	};

	this.setTag = function(id, tag) {
		return java.invoke(this.uuid, 'setTag', [id, tag]);
	};

	this.generateTag = function() {
		return java.invoke(this.uuid, 'generateTag', []);
	};

	this.clear = function() {
		return java.invoke(this.uuid, 'clear', []);
	};
}