import { ResourceMethod } from "./resource-method";

/**
 * Commmon function for initializng the callback functions in the ResourceMethod instances.
 *
 * @param {String} sHandlerFuncName The name of the function that will be attached to the resource mappings configuration
 * @param {Function} fHandler The handler function that will be attached to the resource mappings configuration
 * @returns {ResourceMethod} The ResourceMethod instance to which the function is bound.
 * @private
 */
export function handlerFunction(thiz, configuration: Object, sHandlerFuncName: string, fHandler: Function): ResourceMethod{
    if (fHandler !== undefined) {
        configuration[sHandlerFuncName] = fHandler;
    }

    return thiz;
};