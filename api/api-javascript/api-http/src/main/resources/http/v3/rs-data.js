/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

/**
 * Deep merge for JS objects (typeof o === 'object' is true).
 * Array members are copied as is without changes. That means that array in target object will overwrite a coresponding memeber with the same name in source
 */
/*var merge = function(target, source) {
    Object.keys(source).forEach(function(key) {
        if (source[key]){
			if(typeof source[key] === 'object' && !Array.isArray(source[key])) {
            	return merge(target[key] = target[key] || {}, source[key]);
        	}
        }
        target[key] = source[key];
    });
};
*/

var DataProtocolDefinition = function(){
	var rs = require('http/v3/rs');
	var mappings = this.mappings = new rs.ResourceMappings();
		
	mappings.collectionResource = mappings.resource("");
	mappings.entityResource = mappings.resource("{id}");
	
	//entity collection functions
	var _query = mappings.collectionResource.get().produces(['application/json']);	
	mappings.query = function(){
		return _query;
	};
	var _create = mappings.collectionResource.post().consumes(['*/json']);
	mappings.create = function(){
		return _create;
	};
	var _count = mappings.resource("count").get().produces(['application/json']); 
	mappings.count = function(){
		return _count;
	};
	//entity functions
	var _get = mappings.entityResource.get().produces(['application/json']);
	mappings.get = function(){
		return _get;
	};
	var _update = mappings.entityResource.put().consumes(['*/json']);	
	mappings.update = function(){
		return _update;
	};
	var _remove = mappings.entityResource.remove();
	mappings.remove = function(){
		return _remove;
	};
	//association functions
	var _associationList = mappings.resource("{id}/{associationName}").get().produces(['application/json']);
	mappings.associationList = function(){
		return _associationList;
	};
	var _associationCreate = mappings.resource("{id}/{associationName}").post().consumes(['*/json']);
	mappings.associationCreate = function(){
		return _associationCreate;
	};
	//api functions
	var _metadata = mappings.resource("metadata").get().produces(['application/json']);
	mappings.metadata = function(){
		return _metadata;
	};
	
	//TODO: automate finding resource config by name and make it applicable beyond the well known mehtod names
	mappings.disableByName = function(){
		for(var i=0; i< arguments.length; i++){
			if(arguments[i] === "query"){
				this.disable("", "get", undefined, ['application/json']);
			}
			if(arguments[i] === "get"){
				this.disable("{id}", "get", undefined, ['application/json']);
			}
			if(arguments[i] === "count"){
				this.disable("count", "get", undefined, ['application/json']);
			}
			if(arguments[i] === "metadata"){
				this.disable("metadata", "get", undefined, ['application/json']);
			}
			if(arguments[i] === "create"){
				this.disable("", "post", ['application/json']);
			}
			if(arguments[i] === "update"){
				this.disable("{id}", "post", ['application/json']);
			}
			if(arguments[i] === "delete" || arguments[i] === "remove"){
				this.disable("{id}", "delete");
			}
		}
		return this;
	}.bind(this);
	
	return this;
};

var ProtocolHandlerAdapter = function(oDataProtocolMappings){
	
	this.logger = require('log/logging').getLogger('rs.data.dao.provider.default');

	var protocolDef = oDataProtocolMappings || new DataProtocolDefinition().mappings;
	var _self = this;
	
	var parseIntStrict = function (value) {
	  if(/^(\-|\+)?([0-9]+|Infinity)$/.test(value))
	    return Number(value);
	  return NaN;
	};	
	
	this.adapt = function(){
		var protocolFunctionNames = ["query", "create", "update", "remove", "get", "count", "metadata", "associationList", "associationCreate"];
		for(var i = 0; i < protocolFunctionNames.length; i++){
			var functionName = protocolFunctionNames[i];
			if(protocolDef[functionName]){
				var resourceVerbHandlerDef;
				if(typeof protocolDef[functionName] === 'function')
					resourceVerbHandlerDef = protocolDef[functionName]();
				else
					resourceVerbHandlerDef = protocolDef[functionName];
				_self[functionName].call(_self, resourceVerbHandlerDef, this);
			}
		}
		return protocolDef;
	};
	
	var daos = require('db/v3/dao');
	//functions deifned on the api prototype will be weaved in the using class
	this.api = function(){
		this.dao = function(orm){
			//check if accessor requested
			if(arguments< 1){
				return this._dao;
			}
			this._dao = daos.create(orm);
			return this;
		};
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
		
	var throwBadRequestError = function(context, errorName, errorCode, errorMessage, error){
		context.suppressStack = true;
		context.httpErrorCode = 400;
		context.errorMessage = errorMessage;
		context.errorName = errorName;
		context.errorCode = errorCode;
		//re-throw or construct new
		throw (error || Error(errorMessage));
	};
	
	var installCallbackInVerbHandlerConfig = function(oResourceVerbHandler, sCbName){
		if(!oResourceVerbHandler[sCbName])
			oResourceVerbHandler[sCbName] = function(fCb){
				oResourceVerbHandler.configuration()[sCbName] = fCb;
				return this;
			};
	};
		
	this.create = function(oResourceVerbHandler, _this){
		oResourceVerbHandler.serve(function(context, request, response, handlerDef){
			var entity;
			try{
				entity = request.getJSON();	
			} catch(err){
				throwBadRequestError(context, "Invalid Client Input", undefined, "Invalid JSON in create entity request payload", err);
			}
			notify.call(this, 'onEntityInsert', entity, context);
			var ids = this._dao.insert(entity, context.queryParameters.$cascaded || true);
			notify.call(this, 'onAfterEntityInsert', entity, ids, context);
			if(ids && ids.constructor!== Array)	{
				response.setHeader('Location', request.getRequestURL().toString() + '/' + ids);
				response.setStatus(response.NO_CONTENT);
			} else {				
				var responseBodyJson = JSON.stringify(ids, null, 2);
				response.println(responseBodyJson);
	        	response.setContentType(handlerDef.produces[0]);
				response.setStatus(response.OK);
			}
		}.bind(_this));
		//expose specific callback setup methods
		installCallbackInVerbHandlerConfig(oResourceVerbHandler, "onEntityInsert");
		installCallbackInVerbHandlerConfig(oResourceVerbHandler, "onAfterEntityInsert");
		
	};
	
	this.remove = function(oResourceVerbHandler, _this){
		oResourceVerbHandler.serve(function(context, request, response){
			var id = context.pathParameters.id;	
			notify.call(this, 'onBeforeRemove', id, context);
			this._dao.remove(id);
			notify.call(this, 'onAfterRemove', id, context);
			response.setStatus(response.NO_CONTENT);
		}.bind(_this));
		//expose specific callback setup methods
		installCallbackInVerbHandlerConfig(oResourceVerbHandler, "onBeforeRemove");
		installCallbackInVerbHandlerConfig(oResourceVerbHandler, "onAfterRemove");		
	};
	
	this.update = function(oResourceVerbHandler, _this){
		oResourceVerbHandler.serve(function(context, request, response){
			var id = context.pathParameters.id;
			var entity;
			try{
				entity = request.getJSON();
			} catch (err){
				throwBadRequestError(context, "Invalid Client Input", undefined, "Invalid JSON in update request payload", err);
			}
		    //check for potential mismatch in path id and id in input
		    var entityIdName = this._dao.orm.getPrimaryKey().name;
		    if(entity[entityIdName]!==null && entity[entityIdName]!==undefined && id !== entity[entityIdName])
		    	throwBadRequestError(context, "Invalid Client Input", undefined, "The id parameter in the request path["+id+"] and the id in the payload["+entity[entityIdName]+"] do not match.");
		    entity[entityIdName] = id;
		    //prevent implicit type convertion
	       	if(this._dao.orm.getPrimaryKey().type !== 'string')
    	   		entity[entityIdName] = parseInt(entity[entityIdName], 10);

	    	notify.call(this, 'onEntityUpdate', entity, id);
			entity[this._dao.orm.getPrimaryKey()] = this._dao.update(entity);
			response.setStatus(response.NO_CONTENT);
		}.bind(_this));
		//expose specific callback setup methods
		installCallbackInVerbHandlerConfig(oResourceVerbHandler, "onEntityUpdate");
	};
	
	this.get = function(oResourceVerbHandler, _this){
		oResourceVerbHandler.serve(function(context, request, response, handlerDef){
			var id = context.pathParameters.id;
			//id is mandatory parameter and an integer
			if(id === undefined || isNaN(parseIntStrict(id))){
				throwBadRequestError(context, "Invalid Client Input", undefined, "Invalid id parameter: " + id);
			}
			var $expand = context.queryParameters['$expand'];
			if($expand){
				if($expand===true || $expand.toLowerCase() === '$all') {
					$expand = this._dao.orm.getAssociationNames().join(',');
				} else {
					$expand = String($expand);
					$expand = $expand.split(',').map(function(exp){
						return exp.trim();
					});
				}
			}
			var $select = context.queryParameters['$select'];
			if($select){
				if($select===true || $select.toLowerCase() === '$all') {
					$select = this._dao.orm.getAssociationNames().join(',');
				} else {
					$select = String($select);
					$select = $select.split(',').map(function(sel){
						return sel.trim();
					});
				}
			}		

			var entity = this._dao.find.apply(this._dao, [id, $expand, $select]);
			notify.call(this, 'onAfterFind', entity, context);
			if(!entity){
				_this.logger.error("Record with id: " + id + " does not exist.");
				context.httpErrorCode = response.NOT_FOUND;
				context.suppressStack = true;
				context.errorCode = context.httpErrorCode;
				context.errorName = response.HttpCodesReasons.getReason(context.errorCode);
        		throw Error("Record with id: " + id + " does not exist.");
			}
			var jsonResponse = JSON.stringify(entity, null, 2);
			response.setContentType(handlerDef.produces[0]);
	        response.println(jsonResponse);
		}.bind(_this));
		//expose specific callback setup methods		
		installCallbackInVerbHandlerConfig(oResourceVerbHandler, "onAfterFind");
	};
	
	var validateQueryInputs = this.validateQueryInputs = function(context){

		var limit = context.queryParameters.$limit || context.queryParameters.limit;
		if (limit === undefined || limit === null) {
			//context.queryParameters.limit = 10000;//default constraint
		}  else if(isNaN(parseIntStrict(limit)) || limit < 0) {
			throwBadRequestError(context, "Invalid Client Input", undefined, "Invallid limit parameter: " + limit + ". Must be a positive integer.");
			return false;
		}
		
		var offset = context.queryParameters.$offset || context.queryParameters.offset;
		if (offset === undefined || offset === null) {
			context.queryParameters.offset = 0;
		} else if(isNaN(parseIntStrict(offset)) || offset < 0) {
			throwBadRequestError(context, "Invalid Client Input", undefined, "Invallid offset parameter: " + offset + ". Must be a positive integer.");
			return false;
		}		

		var sort = context.queryParameters.$sort || context.queryParameters.sort || null;
		if(sort !== undefined && sort !== null){
			sort = String(sort);
			var sortPropertyNames = sort.split(',').map(function(srt){
				return srt.trim();
			});
			for(var i=0; i<sortPropertyNames.length;i++){
				var prop = this._dao.orm.getProperty(sortPropertyNames[i]);
				if(!prop){
					throwBadRequestError(context, "Invalid Client Input", undefined, "Invalid $sort by property name: " + sortPropertyNames[i]);
					return false;
				}
			}
			context.queryParameters.$sort = sortPropertyNames;			
		}
		
		var order = context.queryParameters.order || context.queryParameters.$order || null;
		if(order!==null){
			if(sort === null){
				throwBadRequestError(context, "Invalid Client Input", undefined, "Invalid Client Input", undefined, "Parameter $order is invalid without paramter sort to order by.");
				return false;				
			} else if(['asc', 'desc'].indexOf(order.trim().toLowerCase())<0){
				throwBadRequestError(context, "Invalid Client Input", undefined, "Invallid $order parameter: " + order + ". Must be either ASC or DESC.");
				return false;
			}
		} else if(sort !== null){
			context.queryParameters.order = 'asc';
		}
		
		var $expand = context.queryParameters['$expand'];
		if($expand!==undefined) {
			var associationNames = this._dao.orm.getAssociationNames();
			if($expand===true || $expand.toLowerCase() === '$all') {
				$expand = associationNames.join(',');
			} else {
				$expand = String($expand);
				$expand = $expand.split(',').map(function(sel){
					return sel.trim();
				});
				for(var i=0;i<$expand.length; i++){
					if(associationNames.indexOf($expand[i])<0){
						throwBadRequestError(context, "Invalid Client Input", undefined, 'Invalid expand association name - ' + $expand[i]);
						return false;
					}
				}
			}
			context.queryParameters['$expand'] = $expand;
		}
		
		var select = context.queryParameters['$select'];
		if(select!==undefined){
			select = String(select);
			var selectedFieldNames = select.split(',').map(function(sel){
				return sel.trim();
			});
			for(var i=0;i<selectedFieldNames.length; i++){
				if(this._dao.orm.getProperty(selectedFieldNames[i]) === undefined){
					throwBadRequestError(context, "Invalid Client Input", undefined, 'Invalid select property name - ' + selectedFieldNames[i]);
					return false;
				}
			}
			context.queryParameters['$select'] = selectedFieldNames;
		}
		return true;
	};
	
	this.query = function(oResourceVerbHandler, _this){
		oResourceVerbHandler.serve(function(context, request, response, handlerDef){
			if(typeof handlerDef["beforeQuery"] === 'function')
				handlerDef["beforeQuery"].call(_this, context);
			if(!validateQueryInputs.call(this, context, request, response))
				return;
			var args = [context.queryParameters];
			for(var propName in context.queryParameters){
				var val = context.queryParameters[propName];
				if(val==='$null')
					context.queryParameters[propName] = null;
			}
			
			var $count = this._dao.count.apply(this._dao) || 0;
			response.addHeader('X-dservice-list-count', String($count));
		
			var entities;
			if($count > 0){
				entities = this._dao.list.apply(this._dao, args) || [];					
				if(typeof handlerDef["afterQuery"] ==='function')
					handlerDef["afterQuery"].call(_this, entities, context);
				notify.call(this, 'postQuery', entities, context);								
			} else {
				entities = [];
			}
			
	        var jsonResponse = JSON.stringify(entities, null, 2);
	        response.setContentType(handlerDef.produces[0]);
	    	response.println(jsonResponse);
	    	response.setStatus(response.OK);
		}.bind(_this));
		//expose specific callback setup methods
		installCallbackInVerbHandlerConfig(oResourceVerbHandler, "beforeQuery");
		installCallbackInVerbHandlerConfig(oResourceVerbHandler, "afterQuery");	
	};
	
	this.count = function(oResourceVerbHandler, _this){
		oResourceVerbHandler.serve(function(context, request, response, handlerDef){
			var entitiesCount = this._dao.count() || 0;
			response.setHeader("Content-Type", "application/json");
			var payload = {
				"count": entitiesCount
			};
	    	response.println(JSON.stringify(payload, null, 2)); 
			response.setContentType(handlerDef.produces[0]);
	    	response.setStatus(response.OK);
    	}.bind(_this));
	};
	
	this.metadata = function(oResourceVerbHandler, _this){
		oResourceVerbHandler.serve(function(context, request, response, handlerDef){
			//TODO: in process
			var entityMetadata = this._dao.orm.orm;
			response.setContentType(handlerDef.produces[0]);
			response.println(JSON.stringify(entityMetadata, null, 2));
			response.setStatus(response.OK)
		}.bind(_this));
	};
	
	//Associations handlers
	this.associationList = function(oResourceVerbHandler, _this){
		oResourceVerbHandler.serve(function(context, request, response){
	    	var associationName = context.pathParameters.associationName;
	    	if(!this._dao.orm.getAssociation(associationName))
				throwBadRequestError(context, undefined, 'Invalid association set name requested: ' + associationName);
		    var args = context.queryParameters;
	    	args[this._dao.orm.getPrimaryKey().name] = context.pathParameters.id;
			var expansionPath = [associationName];//Tmp solution with array of one component until handler can be installed for paths with multiple segments
			var associationSetEntities = this._dao.expand.apply(this._dao, [expansionPath, context.pathParameters.id]) || [];
			response.setStatus(response.OK);
			response.println(JSON.stringify(associationSetEntities, null, 2));
		}.bind(_this));
	};
	
	this.associationCreate = function(oResourceVerbHandler, _this){
		oResourceVerbHandler.serve(function(context, request, response){
	    	var associationName = context.pathParameters.associationName;
	    	var associationDef = this._dao.orm.getAssociation(associationName);
	    	if(!associationDef)
				throwBadRequestError(context, undefined, 'Invalid association set name requested: ' + associationName);	
	    	var associationType = associationDef.type;
	    	//create works only for one-to-many
	    	if(this._dao.orm.ASSOCIATION_TYPES['ONE-TO-MANY']!==associationType){
			    _this.logger.error('Invalid operation \'create\' requested for association set \''+associationName+'\' with association type ' + associationType + '. Association type must be one-to-many.');
				throwBadRequestError(context, undefined, 'Invalid operation \'create\' requested for association set \''+associationName+'\' with association type ' + associationType + '. Association type must be one-to-many.');
	    	}
	    	
	    	var joinKey = associationDef.joinKey;
	    	if(joinKey === undefined){
			    _this.logger.error('Invalid configuration: missing join key in configuration for association \'' + associationName + '\'.');
				context.suppressStack = true;
				context.httpErrorCode = response.INTERNAL_SERVER_ERROR;
				throw Error('Invalid configuration: missing join key in configuration for association \'' + associationName + '\'.');
	    	}
		    	
	    	var dependendDao = associationDef.targetDao;
	    	if(dependendDao === undefined){
			    _this.logger.error('Invalid configuration: missing dao factory in configuraiton for association \'' + associationName + '\'.');
				context.suppressStack = true;
				context.httpErrorCode = response.INTERNAL_SERVER_ERROR;
		    	throw Error('Invalid configuration: missing dao factory in configuration for association \'' + associationName + '\'.');
		    }

			var dependendEntity;
			try{
				dependendEntity= request.getJSON();	
			} catch(err){
				throwBadRequestError(context, "Invalid Client Input", undefined, "Invalid JSON in create association request payload", err);
			}
			if(this.onEntityInsert){
			    this.onEntityInsert(dependendEntity);
			}
			dependendEntity[dependendDao.orm.getPrimaryKey()] = dependendDao.insert(dependendEntity, context.queryParameters.cascaded);
			response.setStatus(response.OK);
			response.setHeader('Location', request.getRequestURL().toString() + '/' + dependendEntity[this._dao.orm.getPrimaryKey()]);
		}.bind(_this));
	};
};

var HttpController = require('http/v3/rs').HttpController;

/**
 * Utility method to setup the prototipical inheritance chain.
 * credits: https://stackoverflow.com/a/4389429/2134990
 */
function extend(base, sub) {
  // Avoid instantiating the base class just to setup inheritance
  // Also, do a recursive merge of two prototypes, so we don't overwrite 
  // the existing prototype, but still maintain the inheritance chain
  // Thanks to @ccnokes
  var origProto = sub.prototype;
  sub.prototype = Object.create(base.prototype);
  for (var key in origProto)  {
     sub.prototype[key] = origProto[key];
  }
  // The constructor property was set wrong, let's fix it
  Object.defineProperty(sub.prototype, 'constructor', { 
    enumerable: false, 
    value: sub 
  });
}

/**
 * Constructs new DataService instances.
 * 
 * @constructs DataService
 * @param {Object} [oConfig] initial configuration that will be manipulated for building the protocol API. Defaults to an empty object {}.
 * @param {Object} [oProtocolHandlersAdapter] Defaults to a new ProtocolHandlerAdapter instance
 * @param {Object} [oDataProtocolDefinition]  oDataProtocolDefinition supplies the callback functions for each protocol method (e.g. query). Defaults to a new DataProtocolDefinition instance
 * @param {Object} [sLoggerName] An optional logger name to use with this instance. Defaults to 'http.rs.data.service'
 */
var DataService  = function(oConfig, oProtocolHandlersAdapter, oDataProtocolDefinition) {
	var _oProtocolHandlersAdapter = oProtocolHandlersAdapter;
	if(_oProtocolHandlersAdapter === undefined){
		_oProtocolHandlersAdapter = new ProtocolHandlerAdapter(oDataProtocolDefinition);
	}
	
	var _mappings = _oProtocolHandlersAdapter.adapt.call(this);
		
	if(oConfig !== undefined){
		Object.keys(oConfig).forEach(function(sPath){
			_mappings.resource(sPath, oConfig[sPath]);
		});
	}
	
	HttpController.call(this, _mappings);

	/*this.mappings = function(){
		return _mappings;
	};
*/
	//weave in methods from the oProtocolHandlersAdapter that it requires.
	_oProtocolHandlersAdapter.api.call(this);
	
	var loggerName;
	//use supplied loggername if any or use own
	for(var i=0; i<arguments.length; i++){
		if(typeof arguments[i] === 'string'){	
			loggerName = arguments[i];
			break;
		}
	}
	loggerName = loggerName || 'http.rs.data.service';
	this.logger = require('log/v3/logging').getLogger(loggerName);
	
	return this;
};

extend(HttpController, DataService);


/*DataService.prototype.execute = function(oRequest, oResponse) {
	var cfg = this.mappings();
	var rs = require('http/v3/rs');
	var httpSvc = rs.service(cfg);
	return httpSvc.execute(oRequest, oResponse);
};
*/
/**
 * Creates new DataService instances.
 * 
 * @param {Object} [oConfig] ] initial REST API configuration. Defaults to an empty object {}.
 * @param {Object} [oProtocolHandlersAdaptRestAPIer] a custom protocol handlers provider. Defaults to a new ProtocolHandlerAdapter instance
 * @param {Object} [oDataProtocolDefinition]  oDataProtocolDefinition supplies the callback functions for each protocol method (e.g. query). Defaults to a new DataProtocolDefinition instance 
 * @returns {DataService} 
 */
exports.service = function(oConfig, oProtocolHandlersAdapter, oDataProtocolDefinition, sLoggerName){
	var ds = new DataService(oConfig, oProtocolHandlersAdapter, oDataProtocolDefinition, sLoggerName);
	return ds;
};
