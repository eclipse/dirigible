declare module "@dirigible/net" {
    interface Message {
        /**
         * Returns the Part object of this Message
         */
        getPart(): Part;

        /**
         * Returns the MimeHeaders object of this Message
         */
        getMimeHeaders(): MimeHeaders;

        /**
         * Save the changes made on the Message and its components
         */
        save();

        /**
         * Returns a text representation of the Message
         */
        getText(): string;

    }

    interface Part {
        /**
         * Returns the Envelope object of this Part
         */
        getEnvelope(): Envelope;
    }

    interface MimeHeaders {
        /**
         * Creates and add a new MIME header
         * @param name
         * @param value
         */
        addHeader(name: string, value);

        addBasicAuthenticationHeader(username, password);
    }

    interface Envelope {
        /**
         * Creates and add a namespace attribute
         * @param prefix
         * @param url
         */
        addNamespaceDeclaration(prefix, url);

        /**
         * Returns the Body object of this Envelope
         * @param prefix
         * @param url
         */
        getBody(): Body;

        /**
         * Returns the Header object of this Envelope
         */
        getHeader(): Header;

        /**
         * Creates a Name object to be used further
         * @param localName
         * @param prefix
         * @param uri
         */
        createName(localName, prefix, uri): Name;
    }

    interface Body {
        /**
         * Creates and add a child Element
         * @param localName
         * @param prefix
         */
        addChildElement(localName, prefix): Element;

        /**
         * Returns an array of the child Elements
         */
        getChildElements(): Element[];
    }

    interface Header {
        /**
         * Creates and add a Header Element with a Name
         * @param name
         */
        addHeaderElement(name: string): Element;
    }

    interface Name {
        getLocalName(): string;

        getPrefix(): string;

        getQualifiedName(): string

        getURI();
    }

    interface Element {
        /**
         * Creates and add a child Element
         * @param localName
         * @param prefix
         */
        addChildElement(localName: string, prefix: string): Element;

        /**
         * Creates and add a text node
         * @param text
         */
        addTextNode(text: string): Element;

        /**
         * Creates and add an attribute
         * @param name
         * @param value
         */
        addAttribute(name: string, value: string): Element;

        /**
         * Returns an array of the child Elements
         */
        getChildElements(): Element[];

        /**
         * Returns the name of the Element
         */
        getElementName(): Name;

        /**
         * Returns the value of the Element if any
         */
        getValue(): string;

        /**
         * Returns true if the Element is SOAP Element and false otherwise (e.g. CDATA, PDATA, etc.)
         */
        isSOAPElement(): string;
    }

    interface WebsocketClient {
        /**
         * Sends a text message via the Websocket connection
         * @param text
         */
        send(text);

        /**
         * Closes the Websocket connection
         */
        close();
    }

    module soap {
        /**
         * Creates an empty SOAP Message
         */
        function createMessage(): Message;

        /**
         * Creates a message by a given MIME Headers and by parsing of the provided input stream
         */
        function parseMessage(mimeHeaders: MimeHeaders, inputStream): Message;

        /**
         * Creates a message by parsing the standard Request input and empty headers
         */
        function parseRequest(): Message;

        /**
         * Creates an empty MimeHeaders
         */
        function createMimeHeaders(): MimeHeaders;

        /**
         * Calls an end-point of a SOAP Web Service with a request Message and returns the response Message
         * @param message
         * @param uri
         */
        function call(message: string, uri: string): Message;

    }

    module websockets {
        /**
         * Creates a WebsocketClient by URI, handler and engine type
         * @param uri
         * @param handler
         * @param engine
         */
        function createWebsocket(uri: string, handler, engine): WebsocketClient;

        /**
         * Returns the list of the created WebsocketClients
         */
        function getClients(): WebsocketClient[];

        /**
         * Returns the client by its id, if exists or null otherwise
         * @param id
         */
        function getClient(id): WebsocketClient;

        /**
         * Returns the client by its handler, if exists or null otherwise
         * @param handler
         */
        function getClientByHandler(handler): WebsocketClient;

        /**
         * Returns the message in context of OnMessage handler
         */
        function getMessage(): string;

        /**
         * Returns the error in context of OnError handler
         */
        function getError(): string;

        /**
         * Returns the method type in context of the handler
         */
        function getMethod(): string;

        /**
         * Returns true in context of OnOpen handler
         */
        function isOnOpen(): boolean;

        /**
         * Returns true in context of OnMessage handler
         */
        function isOnMessage(): boolean;

        /**
         * Returns true in context of OnError handler
         */
        function isOnError(): boolean;

        /**
         * Returns true in context of OnClose handler
         */
        function isOnClose(): boolean;

    }
}
