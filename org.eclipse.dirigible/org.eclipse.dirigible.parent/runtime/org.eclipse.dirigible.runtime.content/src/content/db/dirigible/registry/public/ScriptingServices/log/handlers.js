/* globals $ */
/* eslint-env node, dirigible */
(function(){
var LEVELS = require('log/levels').LEVELS;
var formatters = require('log/formatters').getFormatters();
var Handler = exports.Handler = function(formatter){
	this.formatter = formatter;
};
Handler.prototype.handle = function(logRecord){};

var ConsoleHandler = function(formatter){
	Handler.call(this, (formatter || (formatters && formatters[0])));
	this.levelToConsoleMethodMap = {};
	this.levelToConsoleMethodMap[LEVELS.ERROR] = 'error';
	this.levelToConsoleMethodMap[LEVELS.WARN] = 'warn';	
	this.levelToConsoleMethodMap[LEVELS.DEBUG] = 'debug';
	this.levelToConsoleMethodMap[LEVELS.TRACE] = this.levelToConsoleMethodMap[LEVELS.INFO] = 'info';
};
ConsoleHandler.prototype = Object.create(Handler.prototype);

ConsoleHandler.prototype.handle = function(logRecord){
	var message = this.formatter.format(logRecord);
	console[this.levelToConsoleMethodMap[logRecord.level]](message);
	if(logRecord.error && logRecord.error.stack)
		console.error(logRecord.error.stack);
};

exports.getHandlers = function(){
	return [
		new ConsoleHandler()
	];
};
})(exports);
