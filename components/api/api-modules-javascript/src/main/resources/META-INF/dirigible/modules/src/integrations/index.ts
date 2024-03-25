const SpringBeanProvider = Java.type("org.eclipse.dirigible.components.spring.SpringBeanProvider");
const Invoker = Java.type('org.eclipse.dirigible.components.engine.camel.invoke.Invoker');
const invoker = SpringBeanProvider.getBean(Invoker.class);
const CamelMessage = Java.type('org.apache.camel.Message');

class Integrations{

    public static invokeRoute(routeId: string, payload: any, headers: any): any {
        return invoker.invokeRoute(routeId, payload, headers);
    }

    public static getInvokingRouteMessage(): any {
        return __context.camelMessage;
    }
}

export interface HeadersMap {
    [key: string]: string | string[];
}

export interface IntegrationMessage {

    constructor(message: any);

    getBody(): any;

    getBodyAsString(): string;

    setBody(body: any): void;

    getHeaders(): HeadersMap;

    getHeader(key: string): string | string[];

    setHeaders(headers: HeadersMap): void;

    setHeader(key: string, value: string | string[]): void;

    getCamelMessage(): typeof CamelMessage;
}
