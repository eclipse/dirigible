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

var HttpController = exports.HttpController = function(oConfiguration){
	this.logger = require('log/logging').getLogger('http.rs.controller');
	//var xss = require("utils/xss");
	this._oConfiguration = oConfiguration || {};
	var self =this;
	
	var matchRequestUrl = function(requestPath, method, cfg){
		var pathDefs = Object.keys(cfg);
		var matches = [];
		for(var i=0; i<pathDefs.length; i++){
			var pathDef = pathDefs[i];
			var resolvedPath;
			if(pathDef === requestPath){
				resolvedPath = pathDef;
				matches.push({w:1, p: resolvedPath, d: pathDef});
			} else {
				var pathDefSegments = pathDef.split('/');					
				var reqPathSegments;
				if(requestPath.trim().length>0)
					reqPathSegments = requestPath.split('/');
				else
					reqPathSegments = [];
				if(pathDefSegments.length === reqPathSegments.length){
					var verbHandlers = Object.keys(cfg[pathDef]);
					if(verbHandlers && verbHandlers.length>0 && verbHandlers.indexOf(method)>-1){
						var pathParams = {};
						var resolvedPathDefSegments = pathDefSegments.map(function(pSeg, i){
							pSeg = pSeg.trim();
							if(pSeg.indexOf('{') === 0 && pSeg.indexOf('}') === pSeg.length-1) {
								var param = pSeg.substring(pSeg.indexOf('{')+1, pSeg.indexOf('}'));
								pathParams[param] = reqPathSegments[i];
								return reqPathSegments[i];
							} else {
								return pSeg;
							}
						});
						var p = resolvedPathDefSegments.join('/');
						if(p === requestPath){
							resolvedPath = p;
							var match = {w:0, p: resolvedPath, d: pathDef};
							if(Object.keys(pathParams).length>0){
								match.pathParams = pathParams;
							}
							matches.push(match);
						}
					}
				}
			}
		}
		//sort matches by weight
		matches = matches.sort(function(p, n){
			if(n.w === p.w){
				//the one with less placeholders wins
				var placeholdersCount1 = p.d.split('/').filter(function(segment){
					return new java.lang.String(segment).startsWith('{');
				}).length;
				var placeholdersCount2 = n.d.split('/').filter(function(segment){
					return java.lang.String(segment).startsWith('{');
				}).length;
				if(placeholdersCount1 > placeholdersCount2){
					n.w = n.w+1;
				} else if(placeholdersCount1 < placeholdersCount2){
					p.w = p.w+1;
				}
			}
			return n.w - p.w;
		});
		return matches;
	};
	
	//  content-type, consumes
	//  accepts, produces
	var isMimeTypeCompatible = function(source, target){
		if(source === target)
			return true;
		var targetM = target.split('/');
		var sourceM = source.split('/');
		if(targetM[0] === '*' && targetM[1] === sourceM[1])
			return true;
		if(targetM[1] === '*' && targetM[0] === sourceM[0])
			return true;		
	};
	
	var normalizeMediaTypeHeaderValue = this.normalizeMediaTypeHeaderValue = function(sMediaType){
		if(sMediaType === undefined || sMediaType === null)
			return;
		sMediaType = sMediaType.split(',');//convert to array
		sMediaType = sMediaType.map(function(mimeTypeEntry){
			return mimeTypeEntry.replace('\\','').split(';')[0].trim();//remove escaping, remove quality or other atributes
		});
		return sMediaType;
	};
	
	//find MIME types intersections
	var matchMediaType = function(request, producesMediaTypes, consumesMediaTypes){
		var isProduceMatched = false;	
		var acceptsMediaTypes = normalizeMediaTypeHeaderValue(request.getHeader('Accept'));
		if(!acceptsMediaTypes || acceptsMediaTypes.indexOf('*/*')>-1){ //output media type is not restricted
			isProduceMatched = true;
		} else  {
			var matchedProducesMIME;
			if(producesMediaTypes && producesMediaTypes.length){
				matchedProducesMIME = acceptsMediaTypes.filter(function(acceptsMediaType) {
				    return producesMediaTypes.filter(function(producesMediaType){
				    	return isMimeTypeCompatible(acceptsMediaType, producesMediaType)
				    }).length > 0;
				});
				isProduceMatched = matchedProducesMIME && matchedProducesMIME.length>0;
			}
		}
		
		var isConsumeMatched = false;
		var contentTypeMediaTypes = normalizeMediaTypeHeaderValue(request.getHeader('Content-Type'));		
		if(!consumesMediaTypes || consumesMediaTypes.indexOf('*/*')>-1){ //input media type is not restricted
			isConsumeMatched = true;
		} else  {
			var matchedConsumesMIME;
			if(contentTypeMediaTypes && consumesMediaTypes && consumesMediaTypes.length){
				matchedConsumesMIME = contentTypeMediaTypes.filter(function(contentTypeMediaType) {
				    return consumesMediaTypes.filter(function(consumesMediaType){
				    	return isMimeTypeCompatible(contentTypeMediaType, consumesMediaType);
				    }).length > 0;
				});
				isConsumeMatched = matchedConsumesMIME && matchedConsumesMIME.length>0;
			}
		}
		return isProduceMatched && isConsumeMatched;
	};
			
	var queryStringToMap = function(queryString){
		if(!queryString)
			return;
		queryString = decodeURI(queryString);
		//Note: Rhino has strange ways of handling ampersand replace/splits
		//queryString = xss.unescapeHtml(queryString).replace(/&amp;/g, '&');
		var queryStringSegments = queryString.split('&');
		var queryParams = {};
		if(queryStringSegments.length>0){
			for(var i=0; i< queryStringSegments.length; i++){
				var seg = queryStringSegments[i];
				seg = seg.replace('amp;','');
				var kv = seg.split('=');
				var key = kv[0].trim();
				var value = kv[1]===undefined ? true : kv[1].trim();
				queryParams[key] = value;
			}
		}
		return queryParams;
	};
	
	var catchErrorHandler = function(logctx, ctx, err, request, response){
		this.logger.error('Serving resource[' + logctx.path + '], Verb['+logctx.method+'], Content-Type['+logctx.contentType+'], Accept['+logctx.accepts+'] finished in error', err);
		this.sendError(response.INTERNAL_SERVER_ERROR, 'Internal Server Error: ' + err.message);
	};
	
  	this.execute = this.service = function(request, response){
  
  		request = request || require("http/v3/request");
		var requestPath = request.getResourcePath();
		var method = request.getMethod().toLowerCase();
		
		var matches = matchRequestUrl(requestPath, method, self._oConfiguration);
		var resourceHandler;
		if(matches && matches[0]){
			var verbHandlers = self._oConfiguration[matches[0].d][method];
			if(verbHandlers){
				resourceHandler = verbHandlers.filter(function(handlerDef){
					return matchMediaType(request, handlerDef.produces, handlerDef.consumes);
				})[0];
			}
		}
		
		response = response || require("http/v3/response");
		var queryParams = queryStringToMap(request.getQueryString()) || {};		
		var acceptsHeader = normalizeMediaTypeHeaderValue(request.getHeader('Accept')) || '[]';
		var contentTypeHeader = normalizeMediaTypeHeaderValue(request.getHeader('Content-Type')) || '[]';
		var resourcePath = requestPath;
		var io = {request: request, response:response};
		
		if(resourceHandler){
			var ctx = {
				"pathParameters": {},
				"queryParameters": {}
			};
			if(matches[0].pathParams){
				ctx.pathParameters =  matches[0].pathParams;
			}
			ctx.queryParameters = queryParams;
						
			var noop = function(){};
			var _before, _serve, _catch, _finally;
			_before = resourceHandler.beforeHandle || noop;
			_serve = resourceHandler.handler || resourceHandler.serve || noop;
			//TODO: move default catch handler globally.
			_catch = resourceHandler.catch || catchErrorHandler.bind(self, {
				path: resourcePath,
				method: method.toUpperCase(),
				contentType: contentTypeHeader, 
				accepts: acceptsHeader
			})
			_finally = resourceHandler.finally || noop;
			
		 	try{
		 		self.logger.trace('Before serving request for resource [{}], Verb[{}], Content-Type[{}], Accept[{}]', resourcePath, method.toUpperCase(), contentTypeHeader, acceptsHeader);
				_before.apply(self, [ctx, request, response]);
				self.logger.trace('Serving request for resource [{}], Verb[{}], Content-Type[{}], Accept[{}]', resourcePath, method.toUpperCase(), contentTypeHeader, acceptsHeader);
				_serve.apply(self, [ctx, request, response]);
				self.logger.trace('Serving request for resource [{}], Verb[{}], Content-Type[{}], Accept[{}] finished', resourcePath, method.toUpperCase(), contentTypeHeader, acceptsHeader);
			} catch(err){
				try{ 
					_catch.apply(self, [ctx, err, request, response]);	
				} catch(_catchErr){
					self.logger.error('Serving request for resource [{}], Verb[{}], Content-Type[{}], Accept[{}] error handler threw error', _catchErr);
					throw _catchErr;
				}
			} finally{
				HttpController.prototype.closeResponse.call(this);				
				try{
					_finally.apply(self, []);
				} catch(_finallyErr){
					self.logger.error('Serving request for resource [{}], Verb[{}], Content-Type[{}], Accept[{}] post handler threw error', _finallyErr);
				}
			}
		} else {
			self.logger.error('No suitable resource handler for resource [' + resourcePath + '], Verb['+method.toUpperCase()+'], Content-Type['+contentTypeHeader+'], Accept['+acceptsHeader+'] found');
			self.sendError(io.response.BAD_REQUEST, 'Bad Request');
		}
  	};
};

HttpController.prototype.getResourceHandlersMap = function(){
	return this._oConfiguration;
};

HttpController.prototype.addResourceHandlers = function(handlersMap){
	if(handlersMap === undefined || handlersMap === null || handlersMap.constructor !== Object)
		throw Error('Illegal argument exception: handlersMap[' + handlersMap + ']');
	var aResourcePaths = Object.keys(handlersMap);	
	for(var i=0; i<aResourcePaths.length; i++){
		var verbHandlerNames = Object.keys(handlersMap[aResourcePaths[i]]);
		for(var j=0; j<verbHandlerNames.length;j++){
			var verbHandlerDefs = handlersMap[aResourcePaths[i]][verbHandlerNames[j]];
			if(verbHandlerDefs.constructor !== Array){//Accept objects for backwards compatibility
				this.logger.warn('Verb handler value must be an array of objects. Objects will not be supported in near future');
				verbHandlerDefs = [verbHandlerDefs]
			}
			for(var k = 0; k<verbHandlerDefs.length; k++){
				var verbHandlerDef = verbHandlerDefs[k];
				HttpController.prototype.addResourceHandler.call(this, aResourcePaths[i], verbHandlerNames[j], verbHandlerDef['handler'], verbHandlerDef['consumes'], verbHandlerDef['produces'], verbHandlerDef['beforeHandler']);
			}
		}
	}
	return this;
};

HttpController.prototype.addResourceHandler = function(sPath, sMethod, fHandler, aConsumesMediaTypes, aProducesMediaTypes, fBeforeHandler){
	//validate mandatory input
	if(sPath===undefined || sPath===null)
		throw Error('Illegal argument exception: sPath['+sPath+']');
	if(sMethod===undefined || sMethod===null){
		throw Error('Illegal argument exception: sMethod['+sMethod+']');//TODO: validate method against the standard HTTP set or allow non standard too?
	}
	sMethod = sMethod.toLowerCase();
	if(fHandler===undefined || fHandler===null || fHandler.constructor !== Function)
		throw Error('Illegal argument exception: fHandler['+fHandler+']');
	//validate optionals
	if(fBeforeHandler!==undefined && fBeforeHandler!==null && fBeforeHandler.constructor !== Function)
		throw Error('Illegal argument exception: fBeforeHandler['+fBeforeHandler+']');
	if(aConsumesMediaTypes!==undefined && aConsumesMediaTypes!==null && aConsumesMediaTypes.constructor === String)//TODO: validate for conformance with mime type spec
		aConsumesMediaTypes = [aConsumesMediaTypes];
	if(aProducesMediaTypes!==undefined && aProducesMediaTypes!==null && aProducesMediaTypes.constructor === String)//TODO: validate for conformance with mime type spec
		aProducesMediaTypes = [aProducesMediaTypes];
	//construct
	var handlerDef = {};
	handlerDef['handler'] = fHandler;
	if(fBeforeHandler)
		handlerDef['beforeHandler'] = fBeforeHandler;		
	if(aConsumesMediaTypes)
		handlerDef['consumes'] = aConsumesMediaTypes;
	if(aProducesMediaTypes)
		handlerDef['produces'] = aProducesMediaTypes;
	if(this._oConfiguration[sPath] === undefined){
		this._oConfiguration[sPath] = {};
	}
	var verbHandlers = this._oConfiguration[sPath][sMethod];
	if(verbHandlers === undefined){
		this._oConfiguration[sPath][sMethod] = [];
	}
	//TODO: shoud we check for overlapping resourceHandler definitions by consumes/produces media types?
	this._oConfiguration[sPath][sMethod].push(handlerDef);
	return this;
};

HttpController.prototype.sendError = function(httpCode, errMessage) {
	var request = require("http/v3/request");
	var response = require("http/v3/response");
	var contentTypeHeader = this.normalizeMediaTypeHeaderValue(request.getHeader('Accept')) || ['application/json'];
	var isHtml = ['text/html']
				.some(function(mediaType){
					return contentTypeHeader.indexOf(mediaType)>-1;
				});
	response.setStatus(httpCode || response.INTERNAL_SERVER_ERROR);
	if(isHtml){
		response.sendError(httpCode, errMessage);
	} else {
	    var body = {
	    	'err': {
	    		'code': httpCode, 
	    		'message': errMessage
	    	}
	    };
	   	response.setHeader("Content-Type", "application/json");
	    response.print(JSON.stringify(body, null, 2));
	}
	this.closeResponse();
};

HttpController.prototype.closeResponse = function(){
	var response = require("http/v3/response");
	response.flush();
	response.close();
};

exports.get = function(oConfiguration){
	return new HttpController(oConfiguration);
};


/**
 * 
 * Resource API
 * 
 */

var arrayEquals = function(source, target){
	if(source===target)
		return true;
	if(!Array.isArray(source) || !Array.isArray(source))
		return false;
	if(source.length !== target.length)
		return false;
	for(var i=0; i<source.length; i++){
		if(source[i]!==target[i])
			return false;
	}
	return true;
}

/**
 * Commmon function for initializng the callback functions in the resource verb handler specification
 */
var handlerFunction = function(sHandlerFuncName, fHandler, sHandlerCfgName){
	if(fHandler !== undefined){
		if(typeof fHandler !== 'function'){
			throw Error('Invalid argument: ' + sHandlerFuncName + ' method argument must be valid javascript function, but instead is ' + (typeof fHandler));
		}
		if(!sHandlerCfgName)
			sHandlerCfgName = sHandlerFuncName;
		this._oSpec[sHandlerCfgName] = fHandler;
	}
	
	return this;
};

/**
 * Commmon function for initializng the 'consumes' and 'produces' arrays in the resource verb handler specification
 */
var mimeSetting = function(mimeSettingName, mimeTypes){
	
	if(mimeTypes !== undefined){
		if(typeof mimeTypes === 'string'){
			mimeTypes = [mimeTypes];
		} else if(!Array.isArray(mimeTypes)){
			throw Error('Invalid argument: '+mimeSettingName+' mime type argument must be valid MIME type string or array of such strings, but instead is ' + (typeof mimeTypes));
		}
		
		mimeTypes.forEach(function(mimeType){
			var mt = mimeType.split('/');
			if(mt === null || mt.length < 2)
				throw Error('Invalid argument. Not a valid MIME type format type/subtype: '+mimeType);
			//TODO: stricter checks
		});

		if(!this._oSpec[mimeSettingName])
			this._oSpec[mimeSettingName] = [];
		//deduplicate entries
		mimeTypes = mimeTypes.filter(function(mimeType){
			return this._oSpec[mimeSettingName].indexOf(mimeType) < 0;
		}.bind(this));
		
		this._oSpec[mimeSettingName] = this._oSpec[mimeSettingName].concat(mimeTypes);
	}

	return this;
};	

/**
 * Constructs a new ResourceVerbHandler instance. 
 * 
 */
var ResourceVerbHandler = function(oSpec){
	this._oSpec = oSpec;
	return this;
};

/**
 * Defines the MIME types that this resource verb handler consumes. Together with the definition of those that it will produce, they constitute
 * the target against which requests with this verb will be matched to enact handler specification.
 * 
 * @param {Function} Callback function for the finally phase of procesing matched resource requests
 * @returns {ResourceVerbHandler} the verb handler instance for method chaning
 */

ResourceVerbHandler.prototype.consumes = function(mimeTypes){
	return mimeSetting.apply(this, ['consumes', mimeTypes]);
};

/**
 * Defines the MIME types that this resource verb handler produces. Together with the definition of those that it will consume, they constitute
 * the target against which requests with this verb will be matched to enact handler specification.
 * 
 * @param {Function} Callback function for the finally phase of procesing matched resource requests
 * @returns {ResourceVerbHandler} the verb handler instance for method chaning
 */
ResourceVerbHandler.prototype.produces = function(mimeTypes){
	return mimeSetting.apply(this, ['produces', mimeTypes]);
};
/**
 * Applies a callback function for the before phase of processing a matched resource request.
 * 
 * @param {Function} Callback function for the before phase of procesing matched resource requests
 * @returns {ResourceVerbHandler} the verb handler instance for method chaning
 */
ResourceVerbHandler.prototype.before = function(fHandler){
	return handlerFunction.apply(this, ['before', fHandler, 'beforeHandle']);
};
/**
 * Applies a callback function for the serve phase of processing a matched resource request. Mandatory for valid resource handling specifications.
 * 
 * @param {Function} Callback function for the serve phase of procesing matched resource requests
 * @returns {ResourceVerbHandler} the verb handler instance for method chaning
 */
ResourceVerbHandler.prototype.serve = function(fHandler){
	return handlerFunction.apply(this, ['serve', fHandler, 'handler']);
};
/**
 * Applies a callback function for the catch errors phase of processing a matched resource request.
 * 
 * @param {Function} Callback function for the catch errors phase of procesing matched resource requests
 * @returns {ResourceVerbHandler} the verb handler instance for method chaning
 */
ResourceVerbHandler.prototype.catch = function(fHandler){
	return handlerFunction.apply(this, ['catch', fHandler]);
};
/**
 * Applies a callback function for the finally phase of processing a matched resource request.
 * 
 * @param {Function} Callback function for the finally phase of procesing matched resource requests
 * @returns {ResourceVerbHandler} the verb handler instance for method chaning
 */
ResourceVerbHandler.prototype.finally = function(fHandler){
	return handlerFunction.apply(this, ['finally', fHandler]);
};

/**
 * Constructs a new Resource instance initialized with the supplied path parameter.
 * 
 * @param {String} sPath
 * @returns {Resource} the resource instance for method chaining
 */
var Resource = function(sPath){
	this._buildCtx = {
		"path": sPath
	};
	return this;	
};

/**
 * Sets the URL path for this resource, overriding the one specified upon its construction.
 * 
 * @returns {Resource} the resource instance for method chaining
 */
Resource.prototype.path = function(sPath){
	if(arguments.length === 0)
		return this._buildCtx.path;
	this._buildCtx = {
		"path": sPath
	};
	return this;
}

/**
 * Creates a new HTTP verb handling specification.
 * The second, optional argument is a specification object or array of such specification objects. It allows to initialize 
 * the verb handlers before manually setting up specifications and to setup multiple handler specifications in one call.
 * 
 * @param {String} sHttpVerb - the HTTP verb (method)
 * @param {Object|Array} oConfiguration - the handler specification(s) for this HTTP verb
 * @returns {ResourceVerbHandler} 
 */
Resource.prototype.method = Resource.prototype.verb = function(sHttpVerb, oConfiguration){
	if(sHttpVerb===undefined)
		throw new Error('Illegal sHttpVerb argument: ' + sHttpVerb);	

	var verb = sHttpVerb.toLowerCase();	
	
	if(!this._buildCtx[verb])
		this._buildCtx[verb] = [];
	
	if(oConfiguration){
		var arrConfig = oConfiguration;
		if(!Array.isArray(oConfiguration)){
			arrConfig = [oConfiguration];
		}
		arrConfig.forEach(function(handlerSpec){
			this._buildCtx[verb].push(new ResourceVerbHandler(handlerSpec));
		}.bind(this));
	}
	
	var handlerSpec = {};
	this._buildCtx[verb].push(handlerSpec);
		
	return new ResourceVerbHandler(handlerSpec);

};
/**
 * Creates a handling specification for the HTTP verb "GET".
 * 
 * Same as invoking method("get") on a resource.
 */
Resource.prototype.get = function(){
	return this.method('get');
};
/**
 * Creates a handling specification for the HTTP verb "POST".
 * 
 * Same as invoking method("post") on a resource.
 */
Resource.prototype.post = function(){
	return this.method('post');
};
/**
 * Creates a handling specification for the HTTP verb "PUT".
 * 
 * Same as invoking method("put") on a resource.
 */
Resource.prototype.put = function(){
	return this.method('put');
};
/**
 * Creates a handling specification for the HTTP verb "DELETE".
 * 
 * Same as invoking method("delete") on a resource.
 */
Resource.prototype["delete"] = Resource.prototype.remove = function(){
	return this.method('delete');
};

/**
 * Returns the configuration of this resource.
 * 
 */
Resource.prototype.configuration = function(){
	var _cfg = {};
	Object.keys(this._buildCtx).forEach(function(entry){
		if(entry!=='path'){
			var verb = _cfg[entry] = this._buildCtx[entry];
			//check for overlapping verb specs
			if(verb.length>1){
				var allConsumeDefinitions = verb.map(function(verbHandlerSpec){
					return verbHandlerSpec.consumes;
				});
				var allProduceDefinitions = verb.map(function(verbHandlerSpec){
					return verbHandlerSpec.consumes;
				});
				for(var i=0; i<allConsumeDefinitions.length; i++){
					var cons = allConsumeDefinitions[i]!==undefined?allConsumeDefinitions[i].sort(): undefined;
					var prod = allProduceDefinitions[i]!==undefined?allProduceDefinitions[i].sort(): undefined;
					for(var j=i+1; j<allConsumeDefinitions.length; j++){
						var nextCons = allConsumeDefinitions[j]!==undefined?allConsumeDefinitions[j].sort():undefined;
						var nextProd = allProduceDefinitions[j]!==undefined?allProduceDefinitions[j].sort():undefined;
						if(arrayEquals.apply(this, [cons, nextCons]))
							console.warn('Overlapping "consumption" definitions detected - '+cons+'. This may shadow handlers.');
						if(arrayEquals.apply(this, [prod, nextProd]))
							console.warn('Overlapping "produces" definitions detected - '+prod+'. This may shadow handlers.');
					}
				}
			}
		}

	}.bind(this));
	
	return _cfg;
};

/**
 * Instructs redirection of the request base don the parameter. If it is a stirng representing URI, the request will be
 * redirected to this URI for any verb. If it's a function it will be invoked and epxected to return a URI string to redirect to.
 * 
 * @param {Function|String} 
 */
Resource.prototype.redirect = function(fRedirector){
	if(typeof fRedirector === 'string'){
		fRedirector = function(){
			return fRedirector;
		}
	}	
	return handlerFunction.apply(this, ['redirect', fRedirector]);
};

/**
 * Constructs a REST API definition instance.
 * 
 * @param {Object} [oConfiguration]
 */
var RestAPI = function(oConfiguration){
	this._resources = {};
	if(oConfiguration){
		Object.keys(oConfiguration).forEach(function(sPath){
			this._resources[sPath] = this.resource(sPath, oConfiguration[sPath]);
		}.bind(this));
	}
};

/**
 * Creates new Resource object. The second, optional argument can be used to initialize the resource prior to manipulating it.
 * 
 * @param {String} sPath
 * @param {Object} [oConfiguration]
 * 
 * @returns {Resource} 
 */
RestAPI.prototype.resource = function(sPath, oConfiguration){
	if(!this._resources[sPath])
		this._resources[sPath] = new Resource(sPath);
	if(typeof oConfiguration === 'object'){
		Object.keys(oConfiguration).forEach(function(verb){
			var _verbSpecs = oConfiguration[verb];
			if(_verbSpecs){
				_verbSpecs.forEach(function(verbSpec){
					var _resourceVerb = this._resources[sPath].method(verb);
					if(verbSpec['consumes'])
						_resourceVerb.consumes(verbSpec['consumes']);
					if(verbSpec['produces'])
						_resourceVerb.produces(verbSpec['produces']);
					if(verbSpec['beforeHandle'])
						_resourceVerb.before(verbSpec['beforeHandle']);
					if(verbSpec['before'])
						_resourceVerb.before(verbSpec['before']);
					if(verbSpec['serve'])
						_resourceVerb.serve(verbSpec['serve']);
					if(verbSpec['handler'])
						_resourceVerb.serve(verbSpec['handler']);
					if(verbSpec['catch'])
						_resourceVerb.catch(verbSpec['catch']);
					if(verbSpec['finally'])
						_resourceVerb.serve(verbSpec['finally']);
					if(verbSpec['redirce'])
						_resourceVerb.serve(verbSpec['redirect']);
				}.bind(this));
			}
		}.bind(this));
	}
	return this._resources[sPath];
};

/**
 * Returns the configuration of this rest API.
 */
RestAPI.prototype.configuration = function(){
	var _cfg = {};
	Object.keys(this._resources).forEach(function(path){
		_cfg[path] = this._resources[path].configuration();
	}.bind(this));
	return _cfg;
};

/**
 * Creates a service with the configuration of this REST API that can handle HTTP requests
 */
RestAPI.prototype.service = function(){
	return new HttpController(this.configuration());
};

/**
 * Creates a new REST API instance. The oConfiguraiton parameter can be used to initialize the API instance.
 * 
 * @param {Object} [oConfiguration]
 * 
 */
exports.api = function(oConfiguration){
	this.api = new RestAPI(oConfiguration); 
	return this.api;
};