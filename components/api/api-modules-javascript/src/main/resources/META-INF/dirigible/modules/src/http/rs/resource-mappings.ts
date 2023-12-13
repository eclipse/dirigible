import { Resource } from "./resource";
import { HttpController } from "./resource-http-controller";


export class ResourceMappings {
    resources: {} = {};
    controller: HttpController;
    execute: Function;

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
    constructor(oConfiguration: Object, controller: HttpController) {
        if (oConfiguration) {
            Object.keys(oConfiguration).forEach((sPath) => {
                this.resources[sPath] = this.resource(sPath, oConfiguration[sPath]);
            });
        }

        if (controller) {
            this.controller = controller;
            this.execute = controller.execute.bind(controller);
        }
    }

    /**
 * Creates new Resource object. The second, optional argument can be used to initialize the resource prior to manipulating it.
 *
 * @param {String} sPath
 * @param {Object} [oConfiguration]
 *
 * @returns {Resource}
 */
    path(sPath: string, oConfiguration: Object): Resource {
        if (sPath !== "" && sPath[0] === "/") {
            sPath = sPath.substring(1); // transform "/test" into "test"
        }

        if (this.resources[sPath] === undefined) {
            this.resources[sPath] = new Resource(sPath, oConfiguration, this.controller, this);
        }

        return this.resources[sPath];
    };
    
    resourcePath(sPath: string, oConfiguration: Object): Resource {
        return this.path(sPath, oConfiguration);
    }

    resource(sPath: string, oConfiguration: Object): Resource {
        return this.path(sPath, oConfiguration);
    }

    /**
     * Returns the configuration object for this ResourceMappings.
     */
    configuration(): Object {
        const _cfg = {};
        Object.keys(this.resources).forEach((sPath) => {
            _cfg[sPath] = this.resources[sPath].configuration();
        });
        return _cfg;
    };

    /**
     * Removes all but GET resource handlers.
     */
    readonly(): ResourceMappings {
        Object.keys(this.resources).forEach((sPath) => {
            this.resources[sPath].readonly();
        });
        return this;
    };

    /**
     * Disables resource handling specifications mathcing the arguments, effectively removing them from this API.
     */
    disable(sPath: string, sVerb: string, arrConsumes: [], arrProduces: string[]): ResourceMappings {
        Object.keys(this.resources[sPath]).forEach(function (resource) {
            resource.disable(sVerb, arrConsumes, arrProduces);
        }.bind(this));
        return this;
    };

    /**
     * Provides a reference to a handler specification matching the supplied arguments.
     */
    find(sPath: string, sVerb: string, arrConsumes: [], arrProduces: []): Resource {
        if (this.resources[sPath]) {
            const hit = this.resources[sPath].find(sVerb, arrConsumes, arrProduces);
            if (hit)
                return hit;
        }
        return;
    };
}