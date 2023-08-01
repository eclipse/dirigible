declare namespace "dirigible/camel" {
    /**
     * Converts the provided payload and headers into a route using the given routeId.
     * @param routeId - The ID of the route to invoke.
     * @param payload - The payload to pass to the route.
     * @param headers - The headers to include in the route invocation.
     * @returns The result of invoking the route.
     */
    export function invokeRoute(routeId: string, payload: any, headers: object): any;

    /**
     * Retrieves the message from the route.
     * @returns The message from the route.
     */
    export function getInvokingRouteMessage(): any;
}