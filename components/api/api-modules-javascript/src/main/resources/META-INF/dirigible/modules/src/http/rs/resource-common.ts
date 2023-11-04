/**
 * Commmon function for initializng the callback functions in the ResourceMethod instances.
 *
 * @param {String} sHandlerFuncName The name of the function that will be attached to the resource mappings configuration
 * @param {Function} fHandler The handler function that will be attached to the resource mappings configuration
 * @returns {ResourceMethod} The ResourceMethod instance to which the function is bound.
 * @private
 */
export function handlerFunction(thiz, configuration, sHandlerFuncName, fHandler) {
    if (fHandler !== undefined) {
        if (typeof fHandler !== 'function') {
            throw Error('Invalid argument: ' + sHandlerFuncName + ' method argument must be valid javascript function, but instead is ' + (typeof fHandler));
        }
        configuration[sHandlerFuncName] = fHandler;
    }

    return thiz;
};