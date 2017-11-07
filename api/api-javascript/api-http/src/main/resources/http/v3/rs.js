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
	var isMimeTypeCompatible = this.isMimeTypeCompatible = function(source, target){
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
		var contentTypeMediaTypes = normalizeMediaTypeHeaderValue(request.getContentType());
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
	
	var catchErrorHandler = function(logctx, ctx, err, request, response){
		if(ctx.suppressStack){
			var detailsMsg = (ctx.errorName || "") + (ctx.errorCode ? " ["+ctx.errorCode+"]": "") + (ctx.errorMessage ? ": "+ctx.errorMessage : ""); 
			this.logger.info('Serving resource[{}], Verb[{}], Content-Type[{}], Accept[{}] finished in error. {}', logctx.path, logctx.method, logctx.contentType, logctx.accepts, detailsMsg);
		} else
			this.logger.error('Serving resource['+logctx.path+'], Verb['+logctx.method+'], Content-Type['+logctx.contentType+'], Accept['+logctx.accepts+'] finished in error', err);
		
		var httpErrorCode = ctx.httpErrorCode || response.INTERNAL_SERVER_ERROR;
		var errorMessage = ctx.errorMessage || (err && err.message);
		var errorName = ctx.errorName || (err && err.name);
		var errorCode = ctx.errorCode;
		this.sendError(httpErrorCode, errorCode, errorName, errorMessage);
	};
	
  	this.execute = function(request, response){
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
		var queryParams = request.getQueryParametersMap() || {};		
		var acceptsHeader = normalizeMediaTypeHeaderValue(request.getHeader('Accept')) || '[]';
		var contentTypeHeader = normalizeMediaTypeHeaderValue(request.getHeader('Content-Type')) || '[]';
		var resourcePath = requestPath;
		
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
			_catch = resourceHandler.catch || catchErrorHandler.bind(self, {
				path: resourcePath,
				method: method.toUpperCase(),
				contentType: contentTypeHeader, 
				accepts: acceptsHeader
			});
			_finally = resourceHandler.finally || noop;
			var callbackArgs = [ctx, request, response, resourceHandler, this];
		 	try{
		 		self.logger.trace('Before serving request for resource [{}], Verb[{}], Content-Type[{}], Accept[{}]', resourcePath, method.toUpperCase(), contentTypeHeader, acceptsHeader);
				_before.apply(self, callbackArgs);
				self.logger.trace('Serving request for resource [{}], Verb[{}], Content-Type[{}], Accept[{}]', resourcePath, method.toUpperCase(), contentTypeHeader, acceptsHeader);
				_serve.apply(this, callbackArgs);
				self.logger.trace('Serving request for resource [{}], Verb[{}], Content-Type[{}], Accept[{}] finished', resourcePath, method.toUpperCase(), contentTypeHeader, acceptsHeader);
			} catch(err){
				try{
					callbackArgs.splice(1, 0, err);
					_catch.apply(self, callbackArgs);	
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
			self.sendError(response.BAD_REQUEST, undefined, 'Bad Request', 'No suitable processor for this request.');
		}
  	};
};

HttpController.prototype.sendError = function(httpErrorCode, applicationErrorCode, errorName, errorDetails) {
	var request = require("http/v3/request");
	var clientAcceptMediaTypes = this.normalizeMediaTypeHeaderValue(request.getHeader('Accept')) || ['application/json'];
	var isHtml = clientAcceptMediaTypes.some(function(acceptMediaType){
					return this.isMimeTypeCompatible( '*/html', acceptMediaType);
				}.bind(this));
	var response = require("http/v3/response");				
	response.setStatus(httpErrorCode || response.INTERNAL_SERVER_ERROR);
	if(isHtml){
		var message = errorName + (applicationErrorCode!==undefined ? '['+applicationErrorCode+']' : '') + (errorDetails ? ': ' + errorDetails : '');
		response.print(message);
		//response.sendError(httpCode, errMessage);
	} else {
	    var body = {
	    	"code": applicationErrorCode,
		  	"error": errorName,
			"details": errorDetails
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
		this.configuration()[sHandlerCfgName] = fHandler;
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

		if(!this.configuration()[mimeSettingName])
			this.configuration()[mimeSettingName] = [];
		//deduplicate entries
		mimeTypes = mimeTypes.filter(function(mimeType){
			return this.configuration()[mimeSettingName].indexOf(mimeType) < 0;
		}.bind(this));
		
		this.configuration()[mimeSettingName] = this.configuration()[mimeSettingName].concat(mimeTypes);
	}

	return this;
};	

/**
 * Constructs a new ResourceVerbHandler instance. 
 * @param {Object} [oConfiguration]
 * @returns {ResourceVerbHandler} 
 */
var ResourceVerbHandler = function(oConfiguration){
	this.cfg = oConfiguration;
	return this;
};

/**
 * Returns the configuration for this resource verb handler.
 * @returns {Object} 
 */
ResourceVerbHandler.prototype.configuration = function(){
	return this.cfg;
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
 * A note about method argument multiplicity (stirng vs array of strings). 
 * The argument of the produce method will translate to the response Content-Type property, which is knwon to be a 
 * single value header by [specification](https://tools.ietf.org/html/rfc7231#section-3.1.1.5). However, this method accepts also array of stirngs as argument.
 * The reason is because produces has sligtly different semantics than a value for Content-Type. It is a declaration for the content types of the 
 * response payload that a handler may produce. Though in most cases a handler function will produce payload in single format (media type), it is 
 * quite possible to desgin it also as a controller that procudes alternative payload in different formats. In these cases you need produces that declares
 * all supported media types so that the request with accept header matching any of them can land in this handler. That makes the routing a bit less transparent
 * but may prove valuable for certian cases.
 * 
 * @param {Function} Callback function for the finally phase of procesing matched resource requests
 * @returns {ResourceVerbHandler} the verb handler instance for method chaining
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
	return handlerFunction.apply(this, ['before', fHandler, 'before']);
};
/**
 * Applies a callback function for the serve phase of processing a matched resource request. Mandatory for valid resource handling specifications.
 * 
 * @param {Function} Callback function for the serve phase of procesing matched resource requests
 * @returns {ResourceVerbHandler} the verb handler instance for method chaning
 */
ResourceVerbHandler.prototype.serve = function(fHandler){
	return handlerFunction.apply(this, ['serve', fHandler, 'serve']);
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
 * Constructs a new Resource instance, initialized with the supplied path parameter and optionally with the second, configuration object parameter.
 * 
 * @param {String} sPath
 * @param {Object} [oConfiguration]
 * @returns {Resource} the resource instance for method chaining
 */
var Resource = function(sPath, oConfiguration){
	this.sPath = sPath;
	this.cfg = oConfiguration || {};
	return this;
};

/**
 * Sets the URL path for this resource, overriding the one specified upon its construction.
 * 
 * @returns {Resource} the resource instance for method chaining
 */
Resource.prototype.path = function(sPath){
	if(arguments.length === 0)
		return this.sPath;
	this.sPath = sPath;
	return this;
}

/**
 * Creates a new HTTP verb handling specification.
 * The second, optional argument is a specification object or array of such specification objects. It allows to initialize 
 * the verb handlers before manually setting up specifications and to setup multiple handler specifications in one call.
 * 
 * @param {String} sHttpVerb - the HTTP verb (method)
 * @param {Object|Array} oConfiguration - the handler specification(s) for this HTTP verb. Can be a single object or array.
 * @returns {ResourceVerbHandler|Array} 
 */
Resource.prototype.method = Resource.prototype.verb = function(sHttpVerb, oConfiguration){
	if(sHttpVerb===undefined)
		throw new Error('Illegal sHttpVerb argument: ' + sHttpVerb);	

	var verb = sHttpVerb.toLowerCase();	
	
	if(!this.cfg[verb])
		this.cfg[verb] = [];
	
	var arrConfig = oConfiguration || {};
	if(!Array.isArray(arrConfig)){
		arrConfig = [arrConfig];
	}
	var handlers = [];
	arrConfig.forEach(function(handlerSpec){
		var _h = this.find(sHttpVerb, handlerSpec.consumes, handlerSpec.produces);
		if(!_h) {
			//create new
			this.cfg[verb].push(handlerSpec);
		} else {
			//update
			for(var propName in handlerSpec)
				_h[propName] = handlerSpec[propName];
		}
		handlers.push(new ResourceVerbHandler(handlerSpec));
	}.bind(this));
	
	return handlers.length > 1 ? handlers : handlers[0];

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

Resource.prototype.find = function(sVerb, arrConsumesMimeTypeStrings, arrProducesMimeTypeStrings){
	var hit;
	Object.keys(this.cfg).filter(function(sVerbName){
		return sVerb === undefined || (sVerb!==undefined && sVerb === sVerbName);
	}).forEach(function(sVerbName){
		this.cfg[sVerbName].forEach(function(verbHandlerSpec){
			if(arrayEquals(verbHandlerSpec.consumes, arrConsumesMimeTypeStrings) && arrayEquals(verbHandlerSpec.produces, arrProducesMimeTypeStrings)){
				hit  =  new ResourceVerbHandler(verbHandlerSpec);
				return;
			}
		});
		if(hit)
			return;
	}.bind(this));
	return hit;
};

/**
 * Returns the configuration of this resource.
 * 
 */
Resource.prototype.configuration = function(){
	return this.cfg;
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

Resource.prototype.disable = function(sVerb, arrConsumesTypeStrings, arrProducesTypeStrings){
	Object.keys(this.cfg).filter(function(sVerbName){
		return !(sVerb === undefined || (sVerb!==undefined && sVerb === sVerbName));
	}).forEach(function(sVerbName){
		this.cfg[sVerbName].forEach(function(verbHandlerSpec, i, verbHandlerSpecs){
			if(arrayEquals(verbHandlerSpec.consumes, arrConsumesTypeStrings) && arrayEquals(verbHandlerSpec.produces, arrProducesTypeStrings))
				verbHandlerSpecs.splice(i, 1);
		});
	});
	return this;
};

Resource.prototype.readonly = function(){
	delete this.cfg['get']
	return this;
};

/**
 * Constructs a REST API definition instance.
 * 
 * @param {Object} [oConfiguration]
 */
var RestAPI = exports.RestAPI = function(oConfiguration){
	this.resources = {};
	if(oConfiguration){
		Object.keys(oConfiguration).forEach(function(sPath){
			this.resources[sPath] = this.resource(sPath, oConfiguration[sPath]);
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
	if(this.resources[sPath] === undefined)
		this.resources[sPath] = new Resource(sPath, oConfiguration);
	return this.resources[sPath];
};

/**
 * Returns the configuration of this rest API.
 */
RestAPI.prototype.configuration = function(){
	var _cfg = {};
	Object.keys(this.resources).forEach(function(sPath){
		_cfg[sPath] = this.resources[sPath].configuration();
	}.bind(this));
	return _cfg;
};

/**
 * Removes all but GET resource handlers.
 */
RestAPI.prototype.readonly = function(){
	Object.keys(this.resources).forEach(function(sPath){
		Object.keys(this.resources[sPath]).forEach(function(resource){
			resource.readonly();
		}.bind(this));
	}.bind(this));
	return this;
};

/**
 * Disables resource handling specifications mathcing the arguments, effectively removing them from this API.
 */
RestAPI.prototype.disable = function(sPath, sVerb, arrConsumes, arrProduces){
	Object.keys(this.resources[sPath]).forEach(function(resource){
		resource.disable(sVerb, arrConsumes, arrProduces);
	}.bind(this));
	return this;
};

/**
 * Provides a reference to a handler specification matching the supplied arguments.
 */
RestAPI.prototype.find = function(sPath, sVerb, arrConsumes, arrProduces){
	if(this.resources[sPath]){
		var hit = this.resources[sPath].find(sVerb, arrConsumes, arrProduces);
		if(hit)
			return hit;
	}
	return;
};


/**
 * Creates a new REST API instance. The oConfiguration parameter can be used to initialize the API instance.
 * 
 * @param {Object} [oConfiguration]
 * 
 */
exports.mappings = function(oConfiguration){
	var api = new RestAPI(oConfiguration); 
	return api;
};

/**
 * Creates a service with the configuration of this REST API that can handle HTTP requests
 * 
 * @param {Object} oMappings
 *
 */
exports.service = function(oMappings){
	var config;
	if(oMappings instanceof RestAPI){
		config = oMappings.configuration();
	} else {
		config = oMappings;
	}
	var controller = new HttpController(config);
	return controller;
};