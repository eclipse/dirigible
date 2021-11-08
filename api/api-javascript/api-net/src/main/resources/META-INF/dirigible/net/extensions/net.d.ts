declare module "@dirigible/net" {
    interface Message {
        getPart(): Part;

        getMimeHeaders(): MimeHeaders;

        save();

        getText();

    }

    interface Part {
        getEnvelope(): Envelope;
    }

    interface MimeHeaders {
        addHeader(name, value);

        addBasicAuthenticationHeader(username, password);
    }

    interface Envelope {
        addNamespaceDeclaration(prefix, url);

        getBody(): Body;

        getHeader(): Header;

        createName(localName, prefix, uri): Name;
    }

    interface Body {
        addChildElement(localName, prefix): Element;

        getChildElements(): Element[];
    }

    interface Header {
        addHeaderElement(name): Element;
    }

    interface Name {
        getLocalName(): string;

        getPrefix(): string;

        getQualifiedName(): string

        getURI();
    }

    interface Element {
        addChildElement(localName, prefix): Element;

        addTextNode(text): Element;

        addAttribute(name, value);

        getChildElements(): Element[];

        getElementName(): string;

        getValue();

        isSOAPElement(): boolean;
    }

    interface WebsocketClient {
        send(text);

        close();
    }

    module soap {
        function createMessage(): Message;

        function parseMessage(): Message | Error

        function parseRequest();

        function createMimeHeaders();

        function call(message, uri): Message;

    }

    module websockets {
        function createWebsocket(uri, handler, engine): WebsocketClient;

        function getClients(): JSON;

        function getClient(id): WebsocketClient;

        function getClientByHandler(handler): WebsocketClient;

        function getMessage(): string;

        function getError(): string;

        function getMethod(): string;

        function isOnOpen(): boolean;

        function isOnMessage(): boolean;

        function isOnError(): boolean;

        function isOnClose(): boolean;

    }
}