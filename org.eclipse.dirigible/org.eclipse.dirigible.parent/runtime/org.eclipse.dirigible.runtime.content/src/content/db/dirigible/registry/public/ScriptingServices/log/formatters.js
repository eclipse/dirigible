/* globals $ */
/* eslint-env node, dirigible */
(function(){

var Formatter = exports.Formatter = function(){};

Formatter.prototype.format = function(logRecord){
	var ctxSegment = logRecord.loggerName?'['+logRecord.loggerName+'] ':' ';
	var errSegment = logRecord.error ? ' ' + logRecord.error.message : '';
	return ctxSegment + logRecord.message + (errSegment? '\r\n' + errSegment : errSegment);
};

exports.getFormatters = function(){
	return [
		new Formatter()
	];
};
})(exports);
