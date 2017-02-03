/* globals $ */
/* eslint-env node, dirigible */
(function(){
"use strict";

var HandlersProvider = exports.HandlersProvider = function(loggerName){
	this.logger = require('log/loggers').get((loggerName||'DAO Provider'));
};
HandlersProvider.prototype.getHandlers = function(){return {}};

var DAOHandlersProvider = exports.DAOHandlersProvider = function(dao, oHttpController, loggerName){
	HandlersProvider.call(this, loggerName);
	this.logger = require('log/loggers').get((loggerName||'Default DAO Provider'));
	var _oHttpController = this.oHttpController = oHttpController;
	this.dao = dao;
	var self = this;
		
	var parseIntStrict = function (value) {
	  if(/^(\-|\+)?([0-9]+|Infinity)$/.test(value))
	    return Number(value);
	  return NaN;
	};
	
	var notify = function(event){
		var func = this[event];
		if(!this[event])
			return;
		if(typeof func !== 'function')
			throw Error('Illegal argument. Not a function: ' + func);
		var args = [].slice.call(arguments);
		func.apply(this, args.slice(1));
	};
	
	var create = function(context, io){
		var input = io.request.readInputText();
	    var entity = JSON.parse(input);
	    notify('onEntityInsert', entity);
	    try{
			entity[dao.orm.getPrimaryKey()] = dao.insert(entity, context.queryParams.cascaded);
			io.response.setStatus(io.response.OK);
			io.response.setHeader('Location', $.getRequest().getRequestURL().toString() + '/' + entity[dao.orm.getPrimaryKey()]);
		} catch(e) {
    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
    	    self.logger.error(e.message, e);
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;
		}		
	};
	
	var remove = function(context, io){
		var id = context.pathParams.id;
		var cascaded = context.queryParams.cascaded;			
	 	try{
			dao.remove(id, cascaded);
			notify('onAfterRemove', id);
			io.response.setStatus(io.response.NO_CONTENT);
		} catch(e) {
    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
    	    self.logger.error(e.message, e);
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;
		}
	};
		
	var update = function(context, io){
		var id = context.pathParams.id;
		var input = io.request.readInputText();
	    var entity = JSON.parse(input);
	    //check for potential mismatch in path id and id in input
	    notify('onEntityUpdate', entity);
	    try{
			entity[dao.orm.getPrimaryKey()] = dao.update(entity);
			io.response.setStatus(io.response.NO_CONTENT);
		} catch(e) {
    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
    	    self.logger.error(e.message, e);
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;
		}
	};
	
	var get = function(context, io){
		var id = context.pathParams.id;
		var expanded = context.queryParams.expanded;
		var select = context.queryParams.select || context.queryParams['$expand'];
		if(!select && expanded !== undefined && expanded){
			if(dao.orm.associationSets)
				select = Object.keys(dao.orm.associationSets).join(',');
			else
				select = expanded;//old style daos
		}
		if(select){
			select = String(new java.lang.String(""+select));
			select = select.split(',').map(function(sel){
				return sel.trim();
			});
		}
		//id is mandatory parameter and an integer
		if(id === undefined || isNaN(parseIntStrict(id))) {
			_oHttpController.sendError(io.response.BAD_REQUEST, "Invalid id parameter: " + id);
			return;
		}

	    try{
			var entity = dao.find.apply(dao.ctx || dao, [id, select]);
			notify('onAfterFind', entity);
			if(!entity){
				self.logger.error("Record with id: " + id + " does not exist.");
        		_oHttpController.sendError(io.response.NOT_FOUND, "Record with id: " + id + " does not exist.");
        		return;
			}
			var jsonResponse = JSON.stringify(entity, null, 2);
			io.response.setContentType("application/json; charset=UTF-8");//TODO: read this from context as defined in config
	        io.response.println(jsonResponse);
		} catch(e) {
    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
			self.logger.error(e.message, e);		    	    
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;
		}	
	};
		
	var validateQueryInputs = function(context, io){
		var offset = context.queryParams.offset;
		if (offset === undefined || offset === null) {
			context.queryParams.offset = 0;
		} else if(isNaN(parseIntStrict(offset)) || offset < 0) {
			self.logger.error("Invallid offset parameter: " + offset + ". Must be a positive integer.");				
			_oHttpController.sendError(io.response.BAD_REQUEST, "Invallid offset parameter: " + offset + ". Must be a positive integer.");
			context.err = {
				httpCode: io.response.BAD_REQUEST, 
				errCode: 1, 
				message: "Invallid offset parameter: " + offset + ". Must be a positive integer."
			};
		}
		
		var limit = context.queryParams.limit;
		if (limit === undefined || limit === null) {
			context.queryParams.limit = 10;
		}  else if(isNaN(parseIntStrict(limit)) || limit < 0) {
			self.logger.error("Invallid limit parameter: " + limit + ". Must be a positive integer.");
			_oHttpController.sendError(io.response.BAD_REQUEST, "Invallid limit parameter: " + limit + ". Must be a positive integer.");
			context.err = {
				httpCode: io.response.BAD_REQUEST, 
				errCode: 1, 
				message: "Invallid offset parameter: " + offset + ". Must be a positive integer."
			};
		}

		var sort = context.queryParams.sort || null;			
		if( sort !== null && self.validSortPropertyNames && self.validSortPropertyNames.indexOf(sort)<0){
			_oHttpController.sendError(io.response.BAD_REQUEST, "Invalid sort by property name: " + sort);
			context.err = {
				httpCode: io.response.BAD_REQUEST, 
				errCode: 1, 
				message: "Invalid sort by property name: " + sort
			};
		}
		var order = context.queryParams.order || null;
		if(order!==null){
			if(sort === null){
				_oHttpController.sendError(io.response.BAD_REQUEST, "Parameter order is invalid without paramter sort to order by.");
				context.err = {
					httpCode: io.response.BAD_REQUEST, 
					errCode: 1, 
					message: "Parameter order is invalid without paramter sort to order by."
				};
			} else if(['asc', 'desc'].indexOf(order.trim().toLowerCase())<0){
				_oHttpController.sendError(io.response.BAD_REQUEST, "Invallid order parameter: " + order + ". Must be either ASC or DESC.");
				context.err = {
					httpCode: io.response.BAD_REQUEST, 
					errCode: 1, 
					message: "Invallid order parameter: " + order + ". Must be either ASC or DESC."
				};
			}
		} else if(sort !== null){
			context.queryParams.order = 'asc';
		}
	};
	
	var query = function(context, io){
		
		validateQueryInputs(context, io);
		if(context.err)
			return;
	
		var offset = context.queryParams.offset;
		var limit = context.queryParams.limit;
		var sort = context.queryParams.sort;
		var order = context.queryParams.order;			
		var id = context.pathParams.id;

		var expanded = context.queryParams.expanded;
		var select = context.queryParams.select || context.queryParams['$expand'];
		if(!select && expanded !== undefined && expanded){
			if(dao.orm.associationSets)
				select = Object.keys(dao.orm.associationSets).join(',');
			else {
				select = expanded;//old style daos
			}
		}
		var args = [];
		if(dao.orm.associationSets){
			args = [context.queryParams];
		} else {
			args = Array.prototype.slice.call(arguments);
			if(args.length>5){
				args = [limit, offset, sort, order, expanded].concat(args.slice(args.length-1));
			} else {
				args = [limit, offset, sort, order, expanded];
			}
		}

	    try{
			var entities = dao.list.apply(dao, args) || [];
			notify('onAfterList', entities);
	        var jsonResponse = JSON.stringify(entities, null, 2);
	    	io.response.println(jsonResponse);
		} catch(e) {
    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
    	    self.logger.error(e.message, e);
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;
		}		
	};	
		
	var count = function(context, io){
	    try{
			var entitiesCount = dao.count() || 0;
			io.response.setHeader("Content-Type", "application/json");
			var payload = {
				"count": entitiesCount
			};
	    	io.response.println(JSON.stringify(payload, null, 2)); 
		} catch(e) {
    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
    	    self.logger.error(e.message, e);
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;
		}
	};
		
	var metadata = function(context, io){
 		try{
			var entityMetadata = dao.metadata();
			io.response.setHeader("Content-Type", "application/json");
			io.response.println(JSON.stringify(entityMetadata, null, 2));
		} catch(e) {
    	    var errorCode = io.response.INTERNAL_SERVER_ERROR ;
    	    self.logger.error(e.message, e);
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;        	
		}		
	};

	//Associations handlers
	var associationListGetHandler = function(context, io){
	    try{
	    	var associationName = context.pathParams.associationName;
	    	if(!dao.orm.associationSets[associationName]){
		    	var errorCode = io.response.BAD_REQUEST;
			    self.logger.error('Invalid association  set name requested: ' + associationName);
		    	_oHttpController.sendError(errorCode, 'Invalid association set name requested: ' + associationName);
				return;
	    	}
		    var args = context.queryParams;
	    	args[dao.orm.getPrimaryKey().name] = context.pathParams.id;
			var expansionPath = [associationName];//Tmp solution with array of one component until handler can be installed for paths with multiple segments
			var associationSetEntities = dao.expand.apply(dao, [expansionPath, context.pathParams.id]) || [];
			io.response.setStatus(io.response.OK);
			io.response.println(JSON.stringify(associationSetEntities, null, 2));
		} catch(e) {
		    var errorCode = io.response.INTERNAL_SERVER_ERROR;
		    self.logger.error(e.message, e);
	    	_oHttpController.sendError(errorCode, e.message);
	    	throw e;
		}
	};
		
	var associationListCreateHandler = function(context, io){
	    try{
	    	var associationName = context.pathParams.associationName;
	    	if(!dao.orm.associationSets[associationName]){
		    	var errorCode = io.response.BAD_REQUEST;
			    self.logger.error('Invalid association set name requested: ' + associationName);
		    	_oHttpController.sendError(errorCode, 'Invalid association set name requested: ' + associationName);
				return;
	    	}
	    	var associationType = dao.orm.associationSets[associationName].associationType;
	    	if('one-to-many'!==associationType){
		    	var errorCode = io.response.BAD_REQUEST;
			    self.logger.error('Invalid operation \'create\' requested for association set \''+associationName+'\' with association type ' + associationType + '. Association type must be one-to-many.');
		    	_oHttpController.sendError(errorCode, 'Invalid operation \'create\' requested for association set \''+associationName+'\' with association type ' + associationType + '. Association type must be one-to-many.');
	    	}
	    	
	    	var joinKey = dao.orm.associationSets[associationName].joinKey;
	    	if(joinKey === undefined){
		    	var errorCode = io.response.INTERNAL_SERVER_ERROR;
			    self.logger.error('Invalid configuration: missing join key in configuration for association \'' + associationName + '\'.');
		    	_oHttpController.sendError(errorCode, 'Invalid configuration: missing join key in configuration for association \'' + associationName + '\'.');
	    	}
		    	
	    	var dependendDao = dao.orm.associationSets[associationName].dao;
	    	if(dependendDao === undefined){
		    	var errorCode = io.response.INTERNAL_SERVER_ERROR;
			    self.logger.error('Invalid configuration: missing dao factory in configuraiton for association \'' + associationName + '\'.');
		    	_oHttpController.sendError(errorCode, 'Invalid configuration: missing dao factory in configuration for association \'' + associationName + '\'.');
		    }
			var input = io.request.readInputText();
			var dependendEntity = JSON.parse(input);
			if(self.onEntityInsert){
			    self.onEntityInsert(dependendEntity);
			}
			dependendEntity[dependendDao.orm.getPrimaryKey()] = dependendDao.insert(dependendEntity, context.queryParams.cascaded);
			io.response.setStatus(io.response.OK);
			io.response.setHeader('Location', $.getRequest().getRequestURL().toString() + '/' + dependendEntity[dao.orm.getPrimaryKey()]);
		} catch(e) {
		    var errorCode = io.response.INTERNAL_SERVER_ERROR;
		    self.logger.error(e.message, e);
	    	_oHttpController.sendError(errorCode, e.message);
	    	throw e;
		}
	};
		
	this.getHandlers = function(){		
		return {
			"create"			: create,
			"update"			: update,
			"remove"			: remove,
			"query"				: query,
			"get"				: get,
			"count"				: count,
			"metadata"			: metadata,
			"associationList" 	: associationListGetHandler,
			"associationCreate" : associationListCreateHandler
		};
	};
};

DAOHandlersProvider.prototype.getHandlers = function(){
	return this.getHandlers();
};

DAOHandlersProvider.prototype = Object.create(HandlersProvider.prototype);
DAOHandlersProvider.prototype.constructor = DAOHandlersProvider;

var HttpController = require('arestme/http').HttpController;
	
var DataService = exports.DataService = function(dao, loggerName){
	if(arguments[0]===undefined)
		throw Error('Illegal argument exception: arguments[0] is undefined');

	this.logger = require('log/loggers').get((loggerName||'Data Service'));
	
	HttpController.call(this, {});

	this.handlersProvider;
	if(arguments[0] instanceof HandlersProvider){
		this.handlersProvider = arguments[0];
		this.handlersProvider.oHttpController = this;
	} else {
		this.handlersProvider = new DAOHandlersProvider(arguments[0], this);
	}
	this.handlers = this.handlersProvider.getHandlers();
	if(this.handlers['query'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"": {
				"get": {
					produces: ['application/json'],
					handler: this.handlers.query
				}
			}
		});
	if(this.handlers['create'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"": {
				"post": {
					consumes: ['application/json'],
					handler: this.handlers.create
				}
			}
		});	
	if(this.handlers['get'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"{id}": {
				"get": {
					produces: ['application/json'],
					handler: this.handlers.get
				}
			}
		});	
	if(this.handlers['update'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"{id}": {
				"put": {
					consumes: ['application/json'],
					handler: this.handlers.update
				}
			}
		});				
	if(this.handlers['remove'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"{id}": {
				"delete": {
					handler: this.handlers.remove
				}
			}
		});	
	if(this.handlers['count'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"count": {
				"get": {
					produces: ['application/json'],
					handler: this.handlers.count
				}
			}
		});
	if(this.handlers['metadata'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"metadata": {
				"get": {
					produces: ['application/json'],
					handler: this.handlers.metadata
				}
			}
		});
  	
	//setup configuration for handling associations
	if(this.handlers['associationList']){
		HttpController.prototype.addResourceHandlers.call(this, {
			"{id}/{associationName}": {
				"get": {
					produces: ["application/json"],
					handler: this.handlers['associationList']
				}
			}
		});
	}
	if(this.handlers['associationCreate']){
		HttpController.prototype.addResourceHandlers.call(this, {
			"{id}/{associationName}": {
				"post": {
					consumes: ["application/json"],
					handler: this.handlers['associationCreate']
				}
			}
		});
	}
  	
};

DataService.prototype = Object.create(HttpController.prototype);
DataService.prototype.constructor = DataService;
		
})();
