/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * @module http/rs
 * @example
 * ```js
 * var rs = require("http/rs")
 * ```
 */

/************************************
 *   ResourceMappings builder API   *
 ************************************/

/**
 * Compares two arrays for equality by inspecting if they are arrays, refer to the same instance,
 * have same length and contain equal components in the same order.
 *
 * @param {array} source The source array to compare to
 * @param {array} target The target array to compare with
 * @return {Boolean} true if the arrays are equal, false otherwise
 * @private
 */
const { match } = require("http/path-to-regexp/6.2.1/index.js");

var arrayEquals = function(source, target){
	if(source===target)
		return true;
	if(!Array.isArray(source) || !Array.isArray(source))
		return false;
	if(source!==undefined && target===undefined || source===undefined && target!==undefined)
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
 * Commmon function for initializng the callback functions in the ResourceMethod instances.
 *
 * @param {String} sHandlerFuncName The name of the function that will be attached to the resource mappings configuration
 * @param {Function} fHandler The handler function that will be attached to the resource mappings configuration
 * @returns {ResourceMethod} The ResourceMethod instance to which the function is bound.
 * @private
 */
var handlerFunction = function(sHandlerFuncName, fHandler){
	if(fHandler !== undefined){
		if(typeof fHandler !== 'function'){
			throw Error('Invalid argument: ' + sHandlerFuncName + ' method argument must be valid javascript function, but instead is ' + (typeof fHandler));
		}
		this.configuration()[sHandlerFuncName] = fHandler;
	}

	return this;
};

/**
 * Commmon function for initializng the 'consumes' and 'produces' arrays in the ResourceMethod instances.
 * Before finalizing the configuration setup the function will remove duplicates with exact match filtering.
 *
 * @param {String} mimeSettingName must be either 'consumes' or 'produces' depending on
 * 				   which configuraiton property is being set with this method.
 * @param {String[]} mimeTypes An array of strings formatted as mime types (type/subtype)
 * @returns {ResourceMethod} The ResourceMethod instance to which the function is bound.
 * @private
 */
var mimeSetting = function(mimeSettingName, mimeTypes){

	if(mimeTypes !== undefined){
		if(typeof mimeTypes === 'string'){
			mimeTypes = [mimeTypes];
		} else if(!Array.isArray(mimeTypes)){
			throw Error('Invalid argument: '+mimeSettingName+' mime type argument must be valid MIME type string or array of such strings, but instead is ' + (typeof mimeTypes));
		}

		mimeTypes.forEach(function(mimeType){
			const mt = mimeType.split('/');
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


/************************
 * 	ResourceMethod API	*
 ************************/


/**
 * Constructor function for ResourceMethod instances.
 * All parameters of the function are optional.
 *
 * Providing oConfiguration will initialize this instance with some initial configuration instead of starting
 * entirely from scratch. Note that the configuration object schema must be compliant with the one produced by
 * the ResourceMethod itself. If this parameter is omited, setup will start from scratch.
 *
 * Provisioning controller, will inject a reference to the execute method of the controller so that it can be
 * fluently invoked in the scope of this ResourceMehtod instance as part of the method chaining flow. The execute
 * function scope is bound to the controller instance for this ResourceMethod.
 *
 * @example
 * ```js
 * rs.service()
 *  .resource('')
 * 		.get()
 * 	.execute();
 * ```
 *
 * Provisioning resource, will inject a reference ot the HTTP method functions of the Resource class (get, post,
 * put, delete, remove, method) so that they can be fluently invoked in the scope of this ResourceMethod instance
 * as part of the method chaining flow. The functions are bound to the resource instance for this ResourceMethod.
 *
 * @example
 * ```js
 * rs.service()
 *  .resource('')
 * 		.get(function(){})
 * 		.post(function(){})
 * 		.put(function(){})
 * 		.remove(function(){})
 * .execute();
 * ```
 *
 * Provisioning mappings, will inject a reference ot the resource method of the ResourceMappings class so that
 * it can be fluently invoked in the scope of this ResourceMethod instance as part of the method chaining flow.
 * The function is bound to the mappings instance for this ResourceMethod.
 *
 * @example
 * ```js
 * rs.service()
 *  .resource('')
 * 		.get(function(){})
 * 	.resource('{id}')
 * 		.get(function(){})
 * .execute();
 * ```
 *
 * @class
 * @param {Object} [oConfiguration]
 * @param {HttpController} [controller] The controller instance, for which this ResourceMethod handles configuration
 * @param {Resource} [resource] The resource instance, for which this ResourceMethod handles configuration
 * @param {ResourceMappings} [mappings] The mappings instance, for which this ResourceMethod handles configuration
 * @returns {ResourceMethod}
 */
var ResourceMethod = function(oConfiguration, controller, resource, mappings){
	this.cfg = oConfiguration;
	this._resource = resource;
	if(controller)
		this.execute = controller.execute.bind(controller);
	if(resource){
		['get','post','put','delete','remove','method'].forEach(function(methodName){
			if(this._resource[methodName])
				this[methodName] = this._resource[methodName].bind(this._resource);
		}.bind(this));
	}
	if(mappings){
		this.resource = mappings.resource.bind(mappings);
		this.resourcePath = this.path = this.resource;//aliases
	}
	return this;
};

/**
 * Returns the configuration for this ResourceMethod instance.
 *
 * @returns {Object}
 */
ResourceMethod.prototype.configuration = function(){
	return this.cfg;
};

/**
 * Defines the content MIME type(s), which this ResourceMethod request processing function expects as input from the
 * client request, i.e. those that it 'consumes'. At runtime, the Content-Type request header will be matched for
 * compatibility with this setting to elicit request processing functions.
 * Note that the matching is performed by compatibility, not strict equality, i.e. the MIME type format wildcards are
 * considered too. For example, a request Content-Type header "text\/json" will match a consumes setting "*\/json".
 *
 * @example
 * ```js
 * rs.service()
 *	.resource("")
 * 		.post(function(){})
 * 			.consumes(["*\/json"])
 * .execute();
 * 	.
 * ```
 *
 * Although it's likely that most implementations will resort to single, or a range of compatible input MIME types, this is
 * entirely up to the request processing function implementation. For example it may be capable of processing content with
 * various, possibly incompatible MIME types. Take care to make sure that the consumes constraint will constrain the requests
 * only to those that the request processing function can really process.
 *
 * @param {String[]} mimeTypes Sets the mime types that this ResourceMethod request processing function is capable to consume.
 * @returns {ResourceMethod} The ResourceMethod instance to which the function invocation is bound, for mehtod chaining.
 */

ResourceMethod.prototype.consumes = function(mimeTypes){
	return mimeSetting.apply(this, ['consumes', mimeTypes]);
};

/**
 * Defines the HTTP response payload MIME type(s), which this ResourceMethod request processing function outputs, i.e.
 * those that it 'produces'. At runtime, the Accept request header will be matched for compatibility with this setting
 * to elicit request processing functions.
 * Note that the matching is performed by compatibility, not strict equality, i.e. the MIME type format wildcards are
 * considered too. For example, a request Accept header "*\/json" will match a produces setting "application\/json".
 *
 * @example
 * ```js
 * rs.service()
 *	.resource("")
 * 		.get(function(){})
 * 			.produces(["application\/json"])
 * .execute();
 * 	.
 * ```
 *
 * Take care to make sure that the produces constraint correctly describes the response contenty MIME types that the request
 * processing function can produce so that only client request that can accept them land there.
 *
 * A note about method argument multiplicity (string vs array of strings).
 * One of the arguments of the produce method will translate to the response Content-Type property, which is known to be a
 * single value header by [specification](https://tools.ietf.org/html/rfc7231#section-3.1.1.5). There are two reasons why
 * the method accepts array and not a single value only:
 *
 * 1. Normally, when matched, content types are evaluated for semantic compatibility and not strict equality on both sides
 *  - client and server. Providing a range of compatible MIME types instead of single value, increases the range of acceptable
 * requests for procesing, while reducing the stricness of the requirements on the client making the request. For example,
 * declaring ["text/json,"application/json"] as produced types makes requests with any of these accept headers (or a combination
 * of them) acceptable for processing: "*\/json", "text/json", "application/json", "*\/*".
 *
 * 2. Although in most cases a handler function will produce payload in single format (media type), it is quite possible to
 * desgin it also as a controller that produces alternative payload in different formats. In these cases you need produces
 * that declares all supported media types so that the request with a relaxed Accept header matching any of them can land
 * in this function. That makes the routing a bit less transparent and dependent on the client, but may prove valuable for
 * certian cases.
 *
 * In any case it is responsibility of the request processing function to set the correct Content-Type header.
 *
 * @param {String[]} mimeTypes Sets the mime type(s) that this ResourceMethod request processing function may produce.
 * @returns {ResourceMethod} The ResourceMethod instance to which the function invocation is bound, for mehtod chaining.
 */
ResourceMethod.prototype.produces = function(mimeTypes){
	return mimeSetting.apply(this, ['produces', mimeTypes]);
};
/**
 * Applies a callback function for the before phase of processing a matched resource request. If a callback function
 * is supplied, it is executed right before the serve function. The before function may throw errors, which will move
 * the processing flow to the catch and then the finally functions (if any). The before function is suitable for processing
 * pre-conditions to the serve operation. They could implemented in the serve function just as well, but using before gives
 * a chance for clear spearation of concerns in the code and is easier to maintain.
 *
 * @example
 * ```js
 * rs.service()
 * 	.resource('')
 * 		.get(function(){})
 * 			.before(function(){
 *				if(request.getHeader('X-developer-key').value()===null)
 * 					this.controller.sendError(response.FORBIDDEN, undefined, response.HttpCodeReason.getReason(response.FORBIDDEN), "X-developer-key is missing from request headers");
 *			})
 *	.execute();
 * ```
 *
 * @param {Function} Callback function for the before phase of procesing matched resource requests
 * @returns {ResourceMethod} The ResourceMethod instance to which the function invocation is bound, for mehtod chaining.
 */
ResourceMethod.prototype.before = function(fHandler){
	return handlerFunction.apply(this, ['before', fHandler]);
};
/**
 * Applies a callback function for processing a matched resource request. Mandatory for valid resource handling specifications.
 *
 * @param {Function} Callback function for the serve phase of procesing matched resource requests
 * @returns {ResourceMethod} The ResourceMethod instance to which the function invocation is bound, for mehtod chaining.
 */
ResourceMethod.prototype.serve = function(fHandler){
	return handlerFunction.apply(this, ['serve', fHandler]);
};
/**
 * Applies a callback function for the catch errors phase of processing a matched resource request.
 *
 * @param {Function} Callback function for the catch errors phase of procesing matched resource requests
 * @returns {ResourceMethod} The ResourceMethod instance to which the function invocation is bound, for mehtod chaining.
 */
ResourceMethod.prototype.catch = function(fHandler){
	return handlerFunction.apply(this, ['catch', fHandler]);
};
/**
 * Applies a callback function for the finally phase of processing a matched resource request. This function (if supplied) is always invoked
 * regardles if the request processing yielded error or not.
 *
 * @param {Function} Callback function for the finally phase of procesing matched resource requests
 * @returns {ResourceMethod} The ResourceMethod instance to which the function invocation is bound, for mehtod chaining.
 */
ResourceMethod.prototype.finally = function(fHandler){
	return handlerFunction.apply(this, ['finally', fHandler]);
};



/********************
 * 	Resource API	*
 ********************/


/**
 * Constructs a new Resource instance, initialized with the supplied path parameter and optionally with the second, configuration object parameter.
 *
 * @param {String} sPath
 * @param {Object} [oConfiguration]
 * @returns {Resource} the resource instance for method chaining
 */
var Resource = function(sPath, oConfiguration, controller, mappings){
	this.sPath = sPath;
	this.cfg = oConfiguration || {};
	if(controller){
		this.controller = controller;
		this.execute = controller.execute.bind(controller);
	}
	if(mappings){
		this.mappings = mappings;
	}
	return this;
};

/**
 * Sets the URL path for this resource, overriding the one specified upon its construction,
 * if a path string is provided as argument ot the method (i.e. acts as setter),
 * or returns the path set for this resource, if the method is invoked without arguments (i.e. acts as getter).
 *
 * @param {string} [sPath] the path property to be set for this resource
 * @returns {Resource|string} the resource instance for method chaining, or the path set for this resource
 */
Resource.prototype.path = function(sPath){
	if(arguments.length === 0)
		return this.sPath;
	this.sPath = sPath;
	return this;
}

/**
 * Creates a new HTTP method handling specification.
 * The second, optional argument is a specification object or array of such specification objects. It allows to initialize
 * the method handlers before manually setting up specifications and to setup multiple handler specifications in one call.
 *
 * @param {String} sHttpMethod - the HTTP method (method)
 * @param {Object|Object[]} oConfiguration - the handler specification(s) for this HTTP method. Can be a single object or array.
 * @returns {ResourceMethod|Object[]}
 */
Resource.prototype.method = function(sHttpMethod, oConfiguration){
	if(sHttpMethod===undefined)
		throw new Error('Illegal sHttpMethod argument: ' + sHttpMethod);

	const method = sHttpMethod.toLowerCase();

	if(!this.cfg[method])
		this.cfg[method] = [];

	let arrConfig = oConfiguration || {};
	if(!Array.isArray(arrConfig)){
		arrConfig = [arrConfig];
	}
	const handlers = [];
	arrConfig.forEach(function(handlerSpec){
		var _h = this.find(sHttpMethod, handlerSpec.consumes, handlerSpec.produces);
		if(!_h) {
			//create new
			this.cfg[method].push(handlerSpec);
		} else {
			//update
			for(var propName in handlerSpec)
				_h[propName] = handlerSpec[propName];
		}
		handlers.push(new ResourceMethod(handlerSpec, this.controller, this, this.mappings));
	}.bind(this));

	return handlers.length > 1 ? handlers : handlers[0];

};

var buildMethod = function(sMethodName, args){
	if(args.length>0){
		if(typeof args[0] === 'function')
			return this.method(sMethodName).serve(args[0]);
		else if(typeof args[0] === 'object')
			return this.method(sMethodName, args[0]);
		else
			throw Error('Invalid argument: Resource.' + sMethodName + ' method first argument must be valid javascript function or configuration object, but instead is ' + (typeof args[0]) + ' ' + args[0]);
	} else {
		return this.method(sMethodName);
	}
	return;
};

/**
 * Creates a handling specification for the HTTP method "GET".
 *
 * Same as invoking method("get") on a resource.
 *
 * @param {Function|Object} [fServeCb|oConfiguration] serve function callback or oConfiguraiton to initilaize the method
 */
Resource.prototype.get = function(){
	return buildMethod.call(this, 'get', arguments);
};
/**
 * Creates a handling specification for the HTTP method "POST".
 *
 * Same as invoking method("post") on a resource.
 *
 * @param {Function|Object} [fServeCb|oConfiguration] serve function callback or oConfiguraiton to initilaize the method
 */
Resource.prototype.post = function(){
	return buildMethod.call(this, 'post', arguments);
};
/**
 * Creates a handling specification for the HTTP method "PUT".
 *
 * Same as invoking method("put") on a resource.
 *
 * @param {Function|Object} [fServeCb|oConfiguration] serve function callback or oConfiguraiton to initilaize the method
 */
Resource.prototype.put = function(){
	return buildMethod.call(this, 'put', arguments);
};
/**
 * Creates a handling specification for the HTTP method "DELETE".
 *
 * Same as invoking method("delete") on a resource.
 *
 * @param {Function|Object} [fServeCb|oConfiguration] serve function callback or oConfiguraiton to initilaize the method
 */
Resource.prototype["delete"] = Resource.prototype.remove = function(){
	return buildMethod.call(this, 'delete', arguments);
};

/**
 * Finds a ResourceMethod with the given constraints.
 *
 * @param {String} sMethod the name of the method property of the ResourceMethod in search
 * @param {Array} arrConsumesMimeTypeStrings the consumes constraint property of the ResourceMethod in search
 * @param {Array} arrProducesMimeTypeStrings the produces constraint property of the ResourceMethod in search
 */
Resource.prototype.find = function(sVerb, arrConsumesMimeTypeStrings, arrProducesMimeTypeStrings){
	let hit;
	Object.keys(this.cfg).filter(function(sVerbName){
		return sVerb === undefined || (sVerb!==undefined && sVerb === sVerbName);
	}).forEach(function(sVerbName){
		this.cfg[sVerbName].forEach(function(verbHandlerSpec){
			if(arrayEquals(verbHandlerSpec.consumes, arrConsumesMimeTypeStrings) && arrayEquals(verbHandlerSpec.produces, arrProducesMimeTypeStrings)){
				hit  =  new ResourceMethod(verbHandlerSpec, this.controller, this, this.mappings);
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
 * redirected to this URI for any method. If it's a function it will be invoked and epxected to return a URI string to redirect to.
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
 * Disables the ResourceMethods that match the given constraints
 */
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

/**
 * Disables all but 'read' HTTP methods in this resource.
 */
Resource.prototype.readonly = function(){
	Object.keys(this.cfg).forEach(function(method){
		if(['get','head','trace'].indexOf(method)<0)
			delete this.cfg[method];
	}.bind(this));
	return this;
};


/****************************
 *   ResourceMappings API   *
 ****************************/


/**
 * Constructor function for ResourceMappings instances.
 * A ResourceMapping abstracts the mappings between resource URL path templates and their corresponding resource handler
 * specifications. Generally, it's used internally by the HttpController exposed by the service factory function adn it is
 * where all settings provided by the fluent API ultimately end up. Another utilization of it is as initial configuration,
 * which is less error prone and config changes-friendly than constructing JSON manually for the same purpose.
 *
 * @class
 * @param {Object} [oConfiguration]
 * @param {HttpController} [controller] The controller instance, for which this ResourceMappings handles configuration
 * @returns {ResourceMappings}
 * @static
 */
var ResourceMappings = exports.ResourceMappings = function(oConfiguration, controller){
	this.resources = {};
	if(oConfiguration){
		Object.keys(oConfiguration).forEach(function(sPath){
			this.resources[sPath] = this.resource(sPath, oConfiguration[sPath], controller);
		}.bind(this));
	}

	if(controller){
		this.controller = controller;
		this.execute = controller.execute.bind(controller);
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
ResourceMappings.prototype.path = ResourceMappings.prototype.resourcePath = ResourceMappings.prototype.resource = function(sPath, oConfiguration){
	if(this.resources[sPath] === undefined)
		this.resources[sPath] = new Resource(sPath, oConfiguration, this.controller, this);
	return this.resources[sPath];
};

/**
 * Returns the configuration object for this ResourceMappings.
 */
ResourceMappings.prototype.configuration = function(){
	const _cfg = {};
	Object.keys(this.resources).forEach(function(sPath){
		_cfg[sPath] = this.resources[sPath].configuration();
	}.bind(this));
	return _cfg;
};

/**
 * Removes all but GET resource handlers.
 */
ResourceMappings.prototype.readonly = function(){
	Object.keys(this.resources).forEach(function(sPath){
		this.resources[sPath].readonly();
	}.bind(this));
	return this;
};

/**
 * Disables resource handling specifications mathcing the arguments, effectively removing them from this API.
 */
ResourceMappings.prototype.disable = function(sPath, sVerb, arrConsumes, arrProduces){
	Object.keys(this.resources[sPath]).forEach(function(resource){
		resource.disable(sVerb, arrConsumes, arrProduces);
	}.bind(this));
	return this;
};

/**
 * Provides a reference to a handler specification matching the supplied arguments.
 */
ResourceMappings.prototype.find = function(sPath, sVerb, arrConsumes, arrProduces){
	if(this.resources[sPath]){
		const hit = this.resources[sPath].find(sVerb, arrConsumes, arrProduces);
		if(hit)
			return hit;
	}
	return;
};



/**************************
 *   HttpController API   *
 **************************/

/**
 * Constructor function for HttpController instances.
 *
 * @class
 * @param {ResourceMappings|Object} [oMappings] the mappings configuration for this controller.
 *
 */
var HttpController = exports.HttpController = function(oMappings){
	this.logger = require('log/logging').getLogger('http.rs.controller');

	const self = this;

function matchRequestUrl(requestPath, method, cfg) {
	return Object.entries(cfg)
		.filter(([_, handlers]) => handlers && handlers[method])
		.map(([path, _]) => path)
		.reduce((matches, path) => matchingRouteDefinitionsReducer(matches, path, requestPath), [])
		.sort(matchedRouteDefinitionsSorter);
}

function matchingRouteDefinitionsReducer(matchedDefinitions, definedPath, requestPath) {
	const matches = match(transformPathParamsDeclaredInBraces(definedPath));
	const matched = matches(requestPath);
	if (matched) {
		const matchedDefinition = {
			p: requestPath,
			d: definedPath,
			pathParams: Array.isArray(matched.params) ? matched.params.join("/") : matched.params
		};
		matchedDefinitions.push(matchedDefinition);
	}
	return matchedDefinitions;
}

function matchedRouteDefinitionsSorter(p, n) {
	p.w = calculateMatchedRouteWeight(p);
	n.w = calculateMatchedRouteWeight(n);

	if (n.w === p.w) {
		//the one with less placeholders wins
		var m1 = p.d.match(/{(.*?)}/g);
		var placeholdersCount1 = m1 !== null ? m1.length : 0;
		var m2 = n.d.match(/{(.*?)}/g);
		var placeholdersCount2 = m2 !== null ? m2.length : 0;
		if (placeholdersCount1 > placeholdersCount2) {
			n.w = n.w + 1;
		} else if (placeholdersCount1 < placeholdersCount2) {
			p.w = p.w + 1;
		}
	}
	return n.w - p.w;
}

function calculateMatchedRouteWeight(matchedRoute) {
	return (matchedRoute.params && matchedRoute.params.length > 0) ? 0 : 1; // always prefer exact route definitions - set weight to 1
}

function transformPathParamsDeclaredInBraces(pathDefinition) {
	const pathParamsInBracesMatcher = /({(\w*\*?)})/g; // matches cases like '/api/{pathParam}' or '/api/{pathParam*}'
	return pathDefinition.replace(pathParamsInBracesMatcher, ":$2"); // transforms matched cases to '/api/:pathParam' or '/api/:pathParam*'
}

	//  content-type, consumes
	//  accepts, produces
	const isMimeTypeCompatible = this.isMimeTypeCompatible = function (source, target) {
		if (source === target)
			return true;
		var targetM = target.split('/');
		var sourceM = source.split('/');
		if ((targetM[0] === '*' && targetM[1] === sourceM[1]) || (source[0] === '*' && targetM[1] === sourceM[1]))
			return true;
		if ((targetM[1] === '*' && targetM[0] === sourceM[0]) || (sourceM[1] === '*' && targetM[0] === sourceM[0]))
			return true;
	};

	const normalizeMediaTypeHeaderValue = this.normalizeMediaTypeHeaderValue = function (sMediaType) {
		if (sMediaType === undefined || sMediaType === null)
			return;
		sMediaType = sMediaType.split(',');//convert to array
		sMediaType = sMediaType.map(function (mimeTypeEntry) {
			return mimeTypeEntry.replace('\\', '').split(';')[0].trim();//remove escaping, remove quality or other atributes
		});
		return sMediaType;
	};

	//find MIME types intersections
	const matchMediaType = function (request, producesMediaTypes, consumesMediaTypes) {
		var isProduceMatched = false;
		var acceptsMediaTypes = normalizeMediaTypeHeaderValue(request.getHeader('Accept'));
		if (!acceptsMediaTypes || acceptsMediaTypes.indexOf('*/*') > -1) { //output media type is not restricted
			isProduceMatched = true;
		} else {
			var matchedProducesMIME;
			if (producesMediaTypes && producesMediaTypes.length) {
				matchedProducesMIME = acceptsMediaTypes.filter(function (acceptsMediaType) {
					return producesMediaTypes.filter(function (producesMediaType) {
						return isMimeTypeCompatible(acceptsMediaType, producesMediaType)
					}).length > 0;
				});
				isProduceMatched = matchedProducesMIME && matchedProducesMIME.length > 0;
			}
		}

		var isConsumeMatched = false;
		var contentTypeMediaTypes = normalizeMediaTypeHeaderValue(request.getContentType());
		if (!consumesMediaTypes || consumesMediaTypes.indexOf('*') > -1) { //input media type is not restricted
			isConsumeMatched = true;
		} else {
			var matchedConsumesMIME;
			if (contentTypeMediaTypes && consumesMediaTypes && consumesMediaTypes.length) {
				matchedConsumesMIME = contentTypeMediaTypes.filter(function (contentTypeMediaType) {
					return consumesMediaTypes.filter(function (consumesMediaType) {
						return isMimeTypeCompatible(contentTypeMediaType, consumesMediaType);
					}).length > 0;
				});
				isConsumeMatched = matchedConsumesMIME && matchedConsumesMIME.length > 0;
			}
		}
		return isProduceMatched && isConsumeMatched;
	};

	const catchErrorHandler = function (logctx, ctx, err, request, response) {
		if (ctx.suppressStack) {
			var detailsMsg = (ctx.errorName || "") + (ctx.errorCode ? " [" + ctx.errorCode + "]" : "") + (ctx.errorMessage ? ": " + ctx.errorMessage : "");
			this.logger.info('Serving resource[{}], Verb[{}], Content-Type[{}], Accept[{}] finished in error. {}', logctx.path, logctx.method, logctx.contentType, logctx.accepts, detailsMsg);
		} else
			this.logger.error('Serving resource[' + logctx.path + '], Verb[' + logctx.method + '], Content-Type[' + logctx.contentType + '], Accept[' + logctx.accepts + '] finished in error', err);

		var httpErrorCode = ctx.httpErrorCode || response.INTERNAL_SERVER_ERROR;
		var errorMessage = ctx.errorMessage || (err && err.message);
		var errorName = ctx.errorName || (err && err.name);
		var errorCode = ctx.errorCode;
		this.sendError(httpErrorCode, errorCode, errorName, errorMessage);
	};

	this.execute = function(request, response){
  		request = request || require("http/request");
		var requestPath = request.getResourcePath();
		var method = request.getMethod().toLowerCase();
		var _oConfiguration = self.resourceMappings.configuration();

		var matches = matchRequestUrl(requestPath, method, _oConfiguration);
		var resourceHandler;
		if(matches && matches[0]){
			var verbHandlers = _oConfiguration[matches[0].d][method];
			if(verbHandlers){
				resourceHandler = verbHandlers.filter(function(handlerDef){
					return matchMediaType(request, handlerDef.produces, handlerDef.consumes);
				})[0];
			}
		}

		response = response || require("http/response");
		const queryParams = request.getQueryParametersMap() || {};
		const acceptsHeader = normalizeMediaTypeHeaderValue(request.getHeader('Accept')) || '[]';
		const contentTypeHeader = normalizeMediaTypeHeaderValue(request.getHeader('Content-Type')) || '[]';
		const resourcePath = requestPath;

		if(resourceHandler){
			const ctx = {
				"pathParameters": {},
				"queryParameters": {}
			};
			if(matches[0].pathParams){
				ctx.pathParameters = request.params =  matches[0].pathParams;
			}
			ctx.queryParameters = request.query = queryParams;

			const noop = function () {
			};
			let _before, _serve, _catch, _finally;
			_before = resourceHandler.before || noop;
			_serve = resourceHandler.handler || resourceHandler.serve || noop;
			_catch = resourceHandler.catch || catchErrorHandler.bind(self, {
				path: resourcePath,
				method: method.toUpperCase(),
				contentType: contentTypeHeader,
				accepts: acceptsHeader
			});
			_finally = resourceHandler.finally || noop;
			const callbackArgs = [ctx, request, response, resourceHandler, this];
			try{
		 		self.logger.trace('Before serving request for Resource[{}], Method[{}], Content-Type[{}], Accept[{}]', resourcePath, method.toUpperCase(), contentTypeHeader, acceptsHeader);
				_before.apply(self, callbackArgs);
				if(!response.isCommitted()){
					self.logger.trace('Serving request for Resource[{}], Method[{}], Content-Type[{}], Accept[{}]', resourcePath, method.toUpperCase(), contentTypeHeader, acceptsHeader);
					_serve.apply(this, callbackArgs);
					self.logger.trace('Serving request for Resource[{}], Method[{}], Content-Type[{}], Accept[{}] finished', resourcePath, method.toUpperCase(), contentTypeHeader, acceptsHeader);
				}
			} catch(err){
				try{
					callbackArgs.splice(1, 0, err);
					_catch.apply(self, callbackArgs);
				} catch(_catchErr){
					self.logger.error('Serving request for Resource[{}], Method[{}], Content-Type[{}], Accept[{}] error handler threw error', _catchErr);
					throw _catchErr;
				}
			} finally{
				HttpController.prototype.closeResponse.call(this);
				try{
					_finally.apply(self, []);
				} catch(_finallyErr){
					self.logger.error('Serving request for Resource[{}], Method[{}], Content-Type[{}], Accept[{}] post handler threw error', _finallyErr);
				}
			}
		} else {
			self.logger.error('No suitable resource handler for Resource[' + resourcePath + '], Method['+method.toUpperCase()+'], Content-Type['+contentTypeHeader+'], Accept['+acceptsHeader+'] found');
			self.sendError(response.BAD_REQUEST, undefined, 'Bad Request', 'No suitable processor for this request.');
		}
  	};

	this.listen = this.execute;


	if(oMappings instanceof ResourceMappings){
		this.resourceMappings = oMappings;
	} else if(typeof oMappings === 'object' || 'undefined') {
		 this.resourceMappings = new ResourceMappings(oMappings, this);
	}

	this.resource = this.resourcePath = this.resourceMappings.resourcePath.bind(this.resourceMappings);

	//weave-in HTTP method-based factory functions - shortcut for service().resource(sPath).method
	['get','post','put','delete','remove','method'].forEach(function(sMethodName){
			this[sMethodName] = function(sPath, sVerb, arrConsumes, arrProduces){
				if(arguments.length < 1)
					throw Error('Insufficient arguments provided to HttpController method ' + sMethodName + '.');
				if(sPath === undefined)
					sPath = "";
				const resource = this.resourceMappings.find(sPath, sVerb, arrConsumes, arrProduces) || this.resourceMappings.resource(sPath);
				resource[sMethodName]['apply'](resource, Array.prototype.slice.call(arguments, 1));
				return this;
			}.bind(this);
		}.bind(this));

};

HttpController.prototype.mappings = function() {
	return this.resourceMappings;
};

HttpController.prototype.sendError = function(httpErrorCode, applicationErrorCode, errorName, errorDetails) {
	const request = require("http/request");
	const clientAcceptMediaTypes = this.normalizeMediaTypeHeaderValue(request.getHeader('Accept')) || ['application/json'];
	const isHtml = clientAcceptMediaTypes.some(function (acceptMediaType) {
		return this.isMimeTypeCompatible('*/html', acceptMediaType);
	}.bind(this));
	const response = require("http/response");
	response.setStatus(httpErrorCode || response.INTERNAL_SERVER_ERROR);
	if(isHtml){
		const message = errorName + (applicationErrorCode !== undefined ? '[' + applicationErrorCode + ']' : '') + (errorDetails ? ': ' + errorDetails : '');
		response.sendError(httpErrorCode || response.INTERNAL_SERVER_ERROR, message);
	} else {
		const body = {
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
	const response = require("http/response");
	response.flush();
	response.close();
};




/****************************
 *   http/rs Module API   *
 ****************************/

/**
 * Creates a service, optionally initialized with oMappings
 *
 * @param {Object|ResourceMappings} [oMappings] configuration object or configuration builder with configuration() getter function
 *
 */
exports.service = function(oConfig){
	let config;
	if(oConfig!==undefined){
		if(typeof oConfig === 'object' || oConfig instanceof ResourceMappings){
			config = oConfig;
		} else {
			throw Error('Illegal argument type: oConfig['+(typeof oConfig)+']');
		}
	}
	return new HttpController(config);
};
