import { handlerFunction } from "./resource-common";

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
export class ResourceMethod {
    cfg: any;
    _resource: any;
    resource: any;
    resourcePath: any;
    path: any;
    controller: any;

    constructor(oConfiguration, controller, resource, mappings) {
        this.cfg = oConfiguration;
        this._resource = resource;
        this.controller = controller;
        if (mappings) {
            this.resource = mappings.resource.bind(mappings);
            this.resourcePath = this.path = this.resource;//aliases
        }
    }

    execute() {
        this.controller?.execute?.(...arguments)
    }

    get() {
        return this._resource?.["get"]?.(...arguments)
    }

    post() {
        return this._resource?.["get"]?.(...arguments)
    }

    put() {
        return this._resource?.["get"]?.(...arguments)
    }

    delete() {
        return this._resource?.["get"]?.(...arguments)
    }

    remove() {
        return this._resource?.["get"]?.(...arguments)
    }

    method() {
        return this._resource?.["get"]?.(...arguments)
    }

    /**
 * Returns the configuration for this ResourceMethod instance.
 *
 * @returns {Object}
 */
    configuration() {
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

    consumes(mimeTypes) {
        return this.mimeSetting('consumes', mimeTypes);
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
    produces(mimeTypes) {
        return this.mimeSetting('produces', mimeTypes);
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
    before(fHandler) {
        return handlerFunction(this, this.configuration(), 'before', fHandler);
    };
    /**
     * Applies a callback function for processing a matched resource request. Mandatory for valid resource handling specifications.
     *
     * @param {Function} Callback function for the serve phase of procesing matched resource requests
     * @returns {ResourceMethod} The ResourceMethod instance to which the function invocation is bound, for mehtod chaining.
     */
    serve(fHandler) {
        return handlerFunction(this, this.configuration(), 'serve', fHandler);
    };
    /**
     * Applies a callback function for the catch errors phase of processing a matched resource request.
     *
     * @param {Function} Callback function for the catch errors phase of procesing matched resource requests
     * @returns {ResourceMethod} The ResourceMethod instance to which the function invocation is bound, for mehtod chaining.
     */
    catch(fHandler) {
        return handlerFunction(this, this.configuration(), 'catch', fHandler);
    };
    /**
     * Applies a callback function for the finally phase of processing a matched resource request. This function (if supplied) is always invoked
     * regardles if the request processing yielded error or not.
     *
     * @param {Function} Callback function for the finally phase of procesing matched resource requests
     * @returns {ResourceMethod} The ResourceMethod instance to which the function invocation is bound, for mehtod chaining.
     */
    finally(fHandler) {
        return handlerFunction(this, this.configuration(), 'finally', fHandler);
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
    private mimeSetting(mimeSettingName, mimeTypes) {

        if (mimeTypes !== undefined) {
            if (typeof mimeTypes === 'string') {
                mimeTypes = [mimeTypes];
            } else if (!Array.isArray(mimeTypes)) {
                throw Error('Invalid argument: ' + mimeSettingName + ' mime type argument must be valid MIME type string or array of such strings, but instead is ' + (typeof mimeTypes));
            }

            mimeTypes.forEach((mimeType) => {
                const mt = mimeType.split('/');
                if (mt === null || mt.length < 2)
                    throw Error('Invalid argument. Not a valid MIME type format type/subtype: ' + mimeType);
                //TODO: stricter checks
            });

            if (!this.configuration()[mimeSettingName])
                this.configuration()[mimeSettingName] = [];
            //deduplicate entries
            mimeTypes = mimeTypes.filter((mimeType) => {
                return this.configuration()[mimeSettingName].indexOf(mimeType) < 0;
            });

            this.configuration()[mimeSettingName] = this.configuration()[mimeSettingName].concat(mimeTypes);
        }

        return this;
    };
}