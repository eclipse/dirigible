/* globals $ */
/* eslint-env node, dirigible */
var globals = require('core/globals');
var handlers = require('log/handlers').getHandlers();
var LEVELS = exports.LEVELS = require('log/levels').LEVELS;

var Logger = exports.Logger = function(loggerName){
	this.ctx = this.name = this.loggerName = loggerName;//ctx is deprecated and will be removed gradually
};

exports.setLevel = function(level){
	globals.set('core.logging.root.level', level);
};

exports.getLevel = function(){
	return globals.get('core.logging.root.level') || LEVELS.OFF;
};

Logger.prototype.getHandlers = function(){
	return require('log/handlers').getHandlers();
};

Logger.prototype.log = function(message, level, err){
	var loggerLevel = exports.getLevel(); 
	if(loggerLevel!==LEVELS.OFF && loggerLevel >= level){
		var logRecord = {
			loggerName	: this.loggerName || this.name || this.ctx,
			message		: message,
			level		: level,
			error		: err
		};
		if(handlers){
			for(var i=0; i<handlers.length; i++){
				try {
					handlers[i].handle(logRecord);
				} catch(handlingError) {
					//TODO: report to a specific handler error manager instead
					console.error(handlingError);
					console.trace(handlingError.stack);
				}
			}		
		}
	}
};

Logger.prototype.error = function(message, error){
	this.log(message, LEVELS.ERROR, error);
};

Logger.prototype.warn = function(message){
	this.log(message, LEVELS.WARN);
};

Logger.prototype.info = function(message){
	this.log(message, LEVELS.INFO);
};

Logger.prototype.debug = function(message){
	this.log(message, LEVELS.DEBUG);
};

Logger.prototype.trace= function(message){
	this.log(message, LEVELS.TRACE);
};

exports.get = function(loggerName, level){
/*	var logger = globals.get(loggerName);
	if(!logger){
		logger = new Logger(loggerName, level);
		globals.set(loggerName, logger);
	}*/
	var logger = new Logger(loggerName, level);
	return logger;
};
