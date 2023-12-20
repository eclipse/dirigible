const SpringBeanProvider = Java.type("org.eclipse.dirigible.components.spring.SpringBeanProvider");
const Invoker = Java.type('org.eclipse.dirigible.components.engine.camel.invoke.Invoker');
const invoker = SpringBeanProvider.getBean(Invoker.class);
const String = Java.type('java.lang.String');

export function invokeRoute(routeId, payload, headers) {
    return invoker.invokeRoute(routeId, payload, headers);
}

export function getInvokingRouteMessage() {
    return __context.camelMessage;
}

// Define an interface for the headers
export interface Headers {
    [key: string]: any;
}

// Wrapper class for Camel Message
export class IntegrationMessage {
    private message: any; // Replace 'any' with the actual type of Camel Message

    constructor(message: any) {
        this.message = message;
    }

    // Get the body of the message
    getBody(): String {
        return this.message.getBody(String.class);
    }

    // Set the body of the message
    setBody(body: String): void {
        this.message.setBody(body);
    }

    // Get headers of the message
    getHeaders(): Headers {
        const headers: Headers = {};
        const headerNames = this.message.getHeaders().keySet().toArray();
        for (const headerName of headerNames) {
            headers[headerName] = this.message.getHeader(headerName);
        }
        return headers;
    }

    // Set headers of the message
    setHeaders(headers: Headers): void {
        for (const [key, value] of Object.entries(headers)) {
            this.message.setHeader(key, value);
        }
    }

    // Set header of the message
    setHeader(key: String, value: any): void {
        this.message.setHeader(key, value);
    }

    // Returns the original camel message
    getCamelMessage(): any {
        return this.message;
    }
}
