import { handlerFunction } from "./resource-common";
import { ResourceMethod } from "./resource-method";

/**
 * Constructs a new Resource instance, initialized with the supplied path parameter and optionally with the second, configuration object parameter.
 *
 * @param {String} sPath
 * @param {Object} [oConfiguration]
 * @returns {Resource} the resource instance for method chaining
 */
export class Resource {
	sPath: string;
	cfg: Object;
	controller: any;
	execute: any;
	mappings: any;

	constructor(sPath: string, oConfiguration: Object, controller, mappings) {
		this.sPath = sPath;
		this.cfg = oConfiguration || {};
		if (controller) {
			this.controller = controller;
			this.execute = controller.execute.bind(controller);
		}
		if (mappings) {
			this.mappings = mappings;
		}
	}

	/**
 * Sets the URL path for this resource, overriding the one specified upon its construction,
 * if a path string is provided as argument ot the method (i.e. acts as setter),
 * or returns the path set for this resource, if the method is invoked without arguments (i.e. acts as getter).
 *
 * @param {string} [sPath] the path property to be set for this resource
 * @returns {Resource|string} the resource instance for method chaining, or the path set for this resource
 */
	path(sPath: string): Resource|string {
		if (arguments.length === 0)
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
	//TODO: return type must only be ResourceMethod because of the "return this.method(sMethodName).serve(args[0]);" in the buildMethod method
	method(sHttpMethod: string, oConfiguration?: Object|Object[]): ResourceMethod { 
		if (sHttpMethod === undefined)
			throw new Error('Illegal sHttpMethod argument: ' + sHttpMethod);

		const method = sHttpMethod.toLowerCase();

		if (!this.cfg[method])
			this.cfg[method] = [];

		let arrConfig;
		if (!Array.isArray(oConfiguration)) {
			arrConfig = [arrConfig || {}];
		} else arrConfig = oConfiguration;
		const handlers: any[] = [];
		arrConfig.forEach((handlerSpec) => {
			const _h = this.find(sHttpMethod, handlerSpec.consumes, handlerSpec.produces);
			if (!_h) {
				//create new
				this.cfg[method].push(handlerSpec);
			} else {
				//update
				for (const propName in handlerSpec)
					_h[propName] = handlerSpec[propName];
			}
			handlers.push(new ResourceMethod(handlerSpec, this.controller, this, this.mappings));
		});

		return handlers.length > 1 ? handlers : handlers[0];
	};

	private buildMethod(sMethodName: string, ...args): ResourceMethod {
		if (args.length > 0) {
			if (typeof args[0] === 'function') {
				return this.method(sMethodName).serve(args[0]);
			} else if (typeof args[0] === 'object')
				return this.method(sMethodName, args[0]);
			else
				throw Error('Invalid argument: Resource.' + sMethodName + ' method first argument must be valid javascript function or configuration object, but instead is ' + (typeof args[0]) + ' ' + args[0]);
		} else {
			return this.method(sMethodName);
		}
	};

	/**
	 * Creates a handling specification for the HTTP method "GET".
	 *
	 * Same as invoking method("get") on a resource.
	 *
	 * @param {Function|Object} [fServeCb|oConfiguration] serve function callback or oConfiguraiton to initilaize the method
	 */
	get(): ResourceMethod {
		return this.buildMethod('get', ...arguments);
	};
	/**
	 * Creates a handling specification for the HTTP method "POST".
	 *
	 * Same as invoking method("post") on a resource.
	 *
	 * @param {Function|Object} [fServeCb|oConfiguration] serve function callback or oConfiguraiton to initilaize the method
	 */
	post(): ResourceMethod {
		return this.buildMethod('post', ...arguments);
	};
	/**
	 * Creates a handling specification for the HTTP method "PUT".
	 *
	 * Same as invoking method("put") on a resource.
	 *
	 * @param {Function|Object} [fServeCb|oConfiguration] serve function callback or oConfiguraiton to initilaize the method
	 */
	put(): ResourceMethod {
		return this.buildMethod('put', ...arguments);
	};
	/**
	 * Creates a handling specification for the HTTP method "DELETE".
	 *
	 * Same as invoking method("delete") on a resource.
	 *
	 * @param {Function|Object} [fServeCb|oConfiguration] serve function callback or oConfiguraiton to initilaize the method
	 */
	delete(): ResourceMethod {
		return this.buildMethod('delete', ...arguments);
	};

	remove(): ResourceMethod {
		return this.buildMethod('delete', ...arguments);
	}

	/**
	 * Finds a ResourceMethod with the given constraints.
	 *
	 * @param {String} sMethod the name of the method property of the ResourceMethod in search
	 * @param {Array} arrConsumesMimeTypeStrings the consumes constraint property of the ResourceMethod in search
	 * @param {Array} arrProducesMimeTypeStrings the produces constraint property of the ResourceMethod in search
	 */
	find(sVerb, arrConsumesMimeTypeStrings, arrProducesMimeTypeStrings) {
		let hit;
		Object.keys(this.cfg).filter((sVerbName) => {
			return sVerb === undefined || (sVerb !== undefined && sVerb === sVerbName);
		}).forEach((sVerbName) => {
			this.cfg[sVerbName].forEach((verbHandlerSpec) => {
				if (arrayEquals(verbHandlerSpec.consumes, arrConsumesMimeTypeStrings) && arrayEquals(verbHandlerSpec.produces, arrProducesMimeTypeStrings)) {
					hit = new ResourceMethod(verbHandlerSpec, this.controller, this, this.mappings);
					return;
				}
			});
			if (hit)
				return;
		});
		return hit;
	};

	/**
	 * Returns the configuration of this resource.
	 *
	 */
	configuration() {
		return this.cfg;
	};

	/**
	 * Instructs redirection of the request base don the parameter. If it is a stirng representing URI, the request will be
	 * redirected to this URI for any method. If it's a function it will be invoked and epxected to return a URI string to redirect to.
	 *
	 * @param {Function|String}
	 */
	redirect(fRedirector: Function|string): ResourceMethod {
		if (typeof fRedirector === 'string') {
			fRedirector = function () {
				return fRedirector;
			}
		}
		return handlerFunction(this, this.configuration(), 'redirect', fRedirector);
	};

	/**
	 * Disables the ResourceMethods that match the given constraints
	 */
	disable(sVerb, arrConsumesTypeStrings, arrProducesTypeStrings) {
		Object.keys(this.cfg).filter((sVerbName) => {
			return !(sVerb === undefined || (sVerb !== undefined && sVerb === sVerbName));
		}).forEach((sVerbName) => {
			this.cfg[sVerbName].forEach((verbHandlerSpec, i, verbHandlerSpecs) => {
				if (arrayEquals(verbHandlerSpec.consumes, arrConsumesTypeStrings) && arrayEquals(verbHandlerSpec.produces, arrProducesTypeStrings))
					verbHandlerSpecs.splice(i, 1);
			});
		});
		return this;
	};

	/**
	 * Disables all but 'read' HTTP methods in this resource.
	 */
	readonly() {
		Object.keys(this.cfg).forEach((method) => {
			if (['get', 'head', 'trace'].indexOf(method) < 0)
				delete this.cfg[method];
		});
		return this;
	};
}

/**
 * Compares two arrays for equality by inspecting if they are arrays, refer to the same instance,
 * have same length and contain equal components in the same order.
 *
 * @param {array} source The source array to compare to
 * @param {array} target The target array to compare with
 * @return {Boolean} true if the arrays are equal, false otherwise
 * @private
 */
function arrayEquals(source, target) {
	if (source === target)
		return true;
	if (!Array.isArray(source) || !Array.isArray(source))
		return false;
	if (source !== undefined && target === undefined || source === undefined && target !== undefined)
		return false;
	if (source.length !== target.length)
		return false;
	for (let i = 0; i < source.length; i++) {
		if (source[i] !== target[i])
			return false;
	}
	return true;
}

