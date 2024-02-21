const SpringBeanProvider = Java.type("org.eclipse.dirigible.components.spring.SpringBeanProvider");
const Invoker = Java.type('org.eclipse.dirigible.components.engine.camel.invoke.Invoker');
const invoker = SpringBeanProvider.getBean(Invoker.class);

class Integrations{

    public static invokeRoute(routeId: string, payload: any, headers: any): any {
        return invoker.invokeRoute(routeId, payload, headers);
    }

    public static getInvokingRouteMessage(): any {
        return __context.camelMessage;
    }
}
