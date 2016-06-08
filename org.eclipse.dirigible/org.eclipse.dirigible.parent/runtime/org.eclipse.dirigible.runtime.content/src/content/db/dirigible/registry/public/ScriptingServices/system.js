// deprecated: use console.log() and core/env
exports.print = function(s){
	out.print(s + '');
};

exports.println = function(s){
	out.println(s + '');
};

exports.env = function() {
	return java.lang.System.getProperties();
};
