/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */
(function(){
"use strict";

var HandlersProvider = exports.HandlersProvider = function(loggerName){
	this.logger = require('log/logging').getLogger((loggerName || 'rs.data.dao.provider'));
};
HandlersProvider.prototype.getHandlers = function(){return {}};

var DAOHandlersProvider = exports.DAOHandlersProvider = function(dao, oHttpController, loggerName){
	HandlersProvider.call(this, loggerName);
	this.logger = require('log/logging').getLogger((loggerName || 'rs.data.dao.provider.default'));
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
		return func.apply(this, args.slice(1));
	};
	
	var create = this.create = function(context, request, response){
		var input = request.getText();
	    var entity = JSON.parse(input);
		notify.call(self, 'onEntityInsert', entity, context);
	  	if(!context.err){	
		    try{
				var ids = dao.insert(entity, context.queryParameters.$cascaded || true);
				notify.call(self, 'onAfterEntityInsert', entity, ids, context);
				if(ids && ids.constructor!== Array)	{
					response.setHeader('Location', $.getRequest().getRequestURL().toString() + '/' + ids);
					response.setStatus(response.NO_CONTENT);
				} else {
					response.println(JSON.stringify(ids, null, 2));
					response.setStatus(response.OK);
				}
			} catch(e) {
	    	    var errorCode = response.INTERNAL_SERVER_ERROR;
	    	    self.logger.error(e.message, e);
	        	_oHttpController.sendError(errorCode, e.message);
	        	throw e;
			}
		}
	};
	
	var remove = this.remove = function(context, request, response){
		var id = context.pathParameters.id;	
		notify.call(self, 'onBeforeRemove', id, context);
		if(!context.err){
			try{
				dao.remove(id);
				notify.call(self, 'onAfterRemove', id, context);
				if(!context.err){	
					response.setStatus(response.NO_CONTENT);
				}
			} catch(e) {
	    	    var errorCode = response.INTERNAL_SERVER_ERROR;
	    	    self.logger.error(e.message, e);
	        	_oHttpController.sendError(errorCode, e.message);
	        	throw e;
			}
		}
	};
		
	var update = this.update = function(context, request, response){
		var id = context.pathParameters.id;
		var input = request.readInputText();
	    var entity = JSON.parse(input);
	    //check for potential mismatch in path id and id in input
	    var ctx = {};
	    try{
	    	notify.call(self, 'onEntityUpdate', entity, id, ctx);	
			entity[dao.orm.getPrimaryKey()] = dao.update(entity);
			response.setStatus(response.NO_CONTENT);
	    } catch (er){
    	    var errorCode = response.INTERNAL_SERVER_ERROR ;
    	    self.logger.error(e.message, e);	    	
	   		_oHttpController.sendError(errorCode, e.message);
	      	throw e;
	    }

	};
	
	var get = this.get = function(context, request, response){
		var id = context.pathParameters.id;
		//id is mandatory parameter and an integer
		if(id === undefined || isNaN(parseIntStrict(id))) {
			_oHttpController.sendError(response.BAD_REQUEST, "Invalid id parameter: " + id);
			return;
		}
		var $expand = context.queryParameters['$expand'];
		if($expand){
			if($expand===true || $expand.toLowerCase() === '$all') {
				$expand = dao.orm.getAssociationNames().join(',');
			} else {
				$expand = String(new java.lang.String(""+$expand));
				$expand = $expand.split(',').map(function(exp){
					return exp.trim();
				});
			}
		}
		var $select = context.queryParameters['$select'];
		if($select){
			if($select===true || $select.toLowerCase() === '$all') {
				$select = dao.orm.getAssociationNames().join(',');
			} else {
				$select = String(new java.lang.String(""+$select));
				$select = $select.split(',').map(function(sel){
					return sel.trim();
				});
			}
		}		
		
	    try{
			var entity = dao.find.apply(dao, [id, $expand, $select]);
			notify.call(self, 'onAfterFind', entity, context);
			if(context.err){
				return;//TODO?
			}
			if(!entity){
				self.logger.error("Record with id: " + id + " does not exist.");
        		_oHttpController.sendError(response.NOT_FOUND, "Record with id: " + id + " does not exist.");
        		return;
			}
			var jsonResponse = JSON.stringify(entity, null, 2);
			response.setContentType("application/json; charset=UTF-8");//TODO: read this from context as defined in config
	        response.println(jsonResponse);
		} catch(e) {
    	    var errorCode = response.INTERNAL_SERVER_ERROR ;
			self.logger.error(e.message, e);		    	    
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;
		}	
	};
		
	var validateQueryInputs = this.validateQueryInputs = function(context, request, response){	

		var limit = context.queryParameters.$limit || context.queryParameters.limit;
		if (limit === undefined || limit === null) {
			context.queryParameters.limit = 10000;//default constraint
		}  else if(isNaN(parseIntStrict(limit)) || limit < 0) {
			self.logger.error("Invallid limit parameter: " + limit + ". Must be a positive integer.");
			_oHttpController.sendError(response.BAD_REQUEST, "Invallid limit parameter: " + limit + ". Must be a positive integer.");
			context.err = {
				httpCode: response.BAD_REQUEST, 
				errCode: 1, 
				message: "Invallid limit parameter: " + limit + ". Must be a positive integer."
			};
		}
		
		var offset = context.queryParameters.$offset || context.queryParameters.offset;
		if (offset === undefined || offset === null) {
			context.queryParameters.offset = 0;
		} else if(isNaN(parseIntStrict(offset)) || offset < 0) {
			self.logger.error("Invallid offset parameter: " + offset + ". Must be a positive integer.");				
			_oHttpController.sendError(response.BAD_REQUEST, "Invallid offset parameter: " + offset + ". Must be a positive integer.");
			context.err = {
				httpCode: response.BAD_REQUEST, 
				errCode: 1, 
				message: "Invallid offset parameter: " + offset + ". Must be a positive integer."
			};
		}		

		var sort = context.queryParameters.$sort || context.queryParameters.sort || null;
		if(sort !== undefined && sort !== null){
			sort = String(new java.lang.String(""+sort));
			var sortPropertyNames = sort.split(',').map(function(srt){
				return srt.trim();
			});
			for(var i=0; i<sortPropertyNames.length;i++){
				var prop = self.dao.orm.getProperty(sortPropertyNames[i]);
				if(!prop){
					_oHttpController.sendError(response.BAD_REQUEST, "Invalid sort by property name: " + sortPropertyNames[i]);
					context.err = {
						httpCode: response.BAD_REQUEST, 
						errCode: 1, 
						message: "Invalid sort by property name: " + sortPropertyNames[i]
					};				
				}
			}
			context.queryParameters.$sort = sortPropertyNames;			
		}
		
		var order = context.queryParameters.order || context.queryParameters.$order || null;
		if(order!==null){
			if(sort === null){
				_oHttpController.sendError(response.BAD_REQUEST, "Parameter order is invalid without paramter sort to order by.");
				context.err = {
					httpCode: response.BAD_REQUEST, 
					errCode: 1, 
					message: "Parameter order is invalid without paramter sort to order by."
				};
			} else if(['asc', 'desc'].indexOf(order.trim().toLowerCase())<0){
				_oHttpController.sendError(response.BAD_REQUEST, "Invallid order parameter: " + order + ". Must be either ASC or DESC.");
				context.err = {
					httpCode: response.BAD_REQUEST, 
					errCode: 1, 
					message: "Invallid order parameter: " + order + ". Must be either ASC or DESC."
				};
			}
		} else if(sort !== null){
			context.queryParameters.order = 'asc';
		}
		
		var $expand = context.queryParameters['$expand'];
		if($expand!==undefined) {
			var associationNames = self.dao.orm.getAssociationNames();
			if($expand===true || $expand.toLowerCase() === '$all') {
				$expand = associationNames.join(',');
			} else {
				$expand = String(new java.lang.String(""+$expand));
				$expand = $expand.split(',').map(function(sel){
					return sel.trim();
				});
				for(var i=0;i<$expand.length; i++){
					if(associationNames.indexOf($expand[i])<0)
						throw Error('Invalid expand association name - ' + $expand[i]);
				}
			}
			context.queryParameters['$expand'] = $expand;
		}
		
		var select = context.queryParameters['$select'];
		if(select!==undefined){
			select = String(new java.lang.String(""+select));
			var selectedFieldNames = select.split(',').map(function(sel){
				return sel.trim();
			});
			for(var i=0;i<selectedFieldNames.length; i++){
				if(self.dao.orm.getProperty(selectedFieldNames[i])<0)
					throw Error('Invalid select property name - ' + selectedFieldNames[i]);
			}
			context.queryParameters['$select'] = selectedFieldNames;
		}
	};
	
	var query = this.query = function(context, request, response){		
		validateQueryInputs(context, request, response);
		if(context.err)
			return;	
		var args = [context.queryParameters];
		for(var propName in context.queryParameters){
			var val = context.queryParameters[propName];
			if(val==='$null')
				context.queryParameters[propName] = null;
		}
	    try{
			var entities = dao.list.apply(dao, args) || [];
			var _entities = notify.call(self, 'postQuery', entities, context);
			if(_entities===undefined){
				_entities = entities;
			}
			var $count = dao.count.apply(dao) || 0;
			response.addHeader('X-dservice-list-count', $count);
	        var jsonResponse = JSON.stringify(_entities, null, 2);
	    	response.println(jsonResponse);
		} catch(e) {
    	    var errorCode = response.INTERNAL_SERVER_ERROR ;
    	    self.logger.error(e.message, e);
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;
		}		
	};	
		
	var count = this.count = function(context, request, response){
	    try{
			var entitiesCount = dao.count() || 0;
			response.setHeader("Content-Type", "application/json");
			var payload = {
				"count": entitiesCount
			};
	    	response.println(JSON.stringify(payload, null, 2)); 
		} catch(e) {
    	    var errorCode = response.INTERNAL_SERVER_ERROR ;
    	    self.logger.error(e.message, e);
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;
		}
	};
		
	var metadata = this.metadata = function(context, request, response){
 		try{
			var entityMetadata = dao.metadata();
			response.setHeader("Content-Type", "application/json");
			response.println(JSON.stringify(entityMetadata, null, 2));
		} catch(e) {
    	    var errorCode = response.INTERNAL_SERVER_ERROR ;
    	    self.logger.error(e.message, e);
        	_oHttpController.sendError(errorCode, e.message);
        	throw e;        	
		}		
	};

	//Associations handlers
	var associationListGetHandler = this.associationListGetHandler = function(context, request, response){
	    try{
	    	var associationName = context.pathParameters.associationName;
	    	if(!dao.orm.getAssociation(associationName)){
		    	var errorCode = response.BAD_REQUEST;
			    self.logger.error('Invalid association  set name requested: ' + associationName);
		    	_oHttpController.sendError(errorCode, 'Invalid association set name requested: ' + associationName);
				return;
	    	}
		    var args = context.queryParameters;
	    	args[dao.orm.getPrimaryKey().name] = context.pathParameters.id;
			var expansionPath = [associationName];//Tmp solution with array of one component until handler can be installed for paths with multiple segments
			var associationSetEntities = dao.expand.apply(dao, [expansionPath, context.pathParameters.id]) || [];
			response.setStatus(response.OK);
			response.println(JSON.stringify(associationSetEntities, null, 2));
		} catch(e) {
		    var errorCode = response.INTERNAL_SERVER_ERROR;
		    self.logger.error(e.message, e);
	    	_oHttpController.sendError(errorCode, e.message);
	    	throw e;
		}
	};
		
	var associationListCreateHandler = this.associationListCreateHandler = function(context, request, response){
	    try{
	    	var associationName = context.pathParameters.associationName;
	    	var associationDef = dao.orm.getAssociation(associationName);
	    	if(!associationDef){
		    	var errorCode = response.BAD_REQUEST;
			    self.logger.error('Invalid association set name requested: ' + associationName);
		    	_oHttpController.sendError(errorCode, 'Invalid association set name requested: ' + associationName);
				return;
	    	}
	    	var associationType = associationDef.type;
	    	//create works only for one-to-many
	    	if(dao.orm.ASSOCIATION_TYPES['ONE-TO-MANY']!==associationType){
		    	var errorCode = response.BAD_REQUEST;
			    self.logger.error('Invalid operation \'create\' requested for association set \''+associationName+'\' with association type ' + associationType + '. Association type must be one-to-many.');
		    	_oHttpController.sendError(errorCode, 'Invalid operation \'create\' requested for association set \''+associationName+'\' with association type ' + associationType + '. Association type must be one-to-many.');
	    	}
	    	
	    	var joinKey = associationDef.joinKey;
	    	if(joinKey === undefined){
		    	var errorCode = response.INTERNAL_SERVER_ERROR;
			    self.logger.error('Invalid configuration: missing join key in configuration for association \'' + associationName + '\'.');
		    	_oHttpController.sendError(errorCode, 'Invalid configuration: missing join key in configuration for association \'' + associationName + '\'.');
	    	}
		    	
	    	var dependendDao = associationDef.targetDao;
	    	if(dependendDao === undefined){
		    	var errorCode = response.INTERNAL_SERVER_ERROR;
			    self.logger.error('Invalid configuration: missing dao factory in configuraiton for association \'' + associationName + '\'.');
		    	_oHttpController.sendError(errorCode, 'Invalid configuration: missing dao factory in configuration for association \'' + associationName + '\'.');
		    }
			var input = request.readInputText();
			var dependendEntity = JSON.parse(input);
			if(self.onEntityInsert){
			    self.onEntityInsert(dependendEntity);
			}
			dependendEntity[dependendDao.orm.getPrimaryKey()] = dependendDao.insert(dependendEntity, context.queryParameters.cascaded);
			response.setStatus(response.OK);
			response.setHeader('Location', $.getRequest().getRequestURL().toString() + '/' + dependendEntity[dao.orm.getPrimaryKey()]);
		} catch(e) {
		    var errorCode = response.INTERNAL_SERVER_ERROR;
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

var HttpController = require('http/v3/rs').HttpController;
	
var DataService = exports.DataService = function(dao, loggerName){
	if(arguments[0]===undefined)
		throw Error('Illegal argument exception: arguments[0] is undefined');

	this.logger = require('log/logging').getLogger((loggerName||'rs.data.svc'));
	
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
				"get": [{
					produces: ['application/json'],
					handler: this.handlers.query
				}]
			}
		});
	if(this.handlers['create'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"": {
				"post": [{
					consumes: ['application/json'],
					handler: this.handlers.create
				}]
			}
		});	
	if(this.handlers['get'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"{id}": {
				"get": [{
					produces: ['application/json'],
					handler: this.handlers.get
				}]
			}
		});	
	if(this.handlers['update'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"{id}": {
				"put": [{
					consumes: ['application/json'],
					handler: this.handlers.update
				}]
			}
		});				
	if(this.handlers['remove'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"{id}": {
				"delete": [{
					handler: this.handlers.remove
				}]
			}
		});	
	if(this.handlers['count'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"count": {
				"get": [{
					produces: ['application/json'],
					handler: this.handlers.count
				}]
			}
		});
	if(this.handlers['metadata'])
		HttpController.prototype.addResourceHandlers.call(this, {
			"metadata": {
				"get": [{
					produces: ['application/json'],
					handler: this.handlers.metadata
				}]
			}
		});
  	
	//setup configuration for handling associations
	if(this.handlers['associationList']){
		HttpController.prototype.addResourceHandlers.call(this, {
			"{id}/{associationName}": {
				"get": [{
					produces: ["application/json"],
					handler: this.handlers['associationList']
				}]
			}
		});
	}
	if(this.handlers['associationCreate']){
		HttpController.prototype.addResourceHandlers.call(this, {
			"{id}/{associationName}": {
				"post": [{
					consumes: ["application/json"],
					handler: this.handlers['associationCreate']
				}]
			}
		});
	}
  	
};

DataService.prototype = Object.create(HttpController.prototype);
DataService.prototype.constructor = DataService;
		
})();
