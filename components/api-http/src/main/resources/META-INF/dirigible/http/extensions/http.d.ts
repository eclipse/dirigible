declare module "@dirigible/http" {

    const Controller;
    function Get(path: string);
    function Post(path: string);
    function Put(path: string);
    function Patch(path: string);
    function Delete(path: string);
    function Head(path: string);
    function Options(path: string);

    function Produces(mimeTypes: string | string[]);
    function Consumes(mimeTypes: string | string[]);

    module clientAsync {
        /**
         * Returns HttpAsyncClient instance
         */
        function getInstance(): HttpAsyncClient;
    }

    module request {
        interface HttpCookie {
            /**
             * The HttpCookie name
             */
            name: string;
            /**
             * The HttpCookie value
             */
            value: string;
            /**
             * The HttpCookie comment section
             */
            comment: string
            /**
             * The HttpCookie maximum age
             */
            maxAge: number;
            /**
             * The URI path to which the client should return the HttpCookie
             */
            path: string;
            /**
             * The domain name set to this HttpCookie
             */
            domain: string;
            /**
             * Returns true if the client is sending HttpCookie only over a secure protocol
             */
            secure: string;
            /**
             * Returns the version of the protocol this cookie complies with
             */
            version: string;
            /**
             * The HttpCookie will not be exposed to the client-side scripting code if true
             */
            httpOnly: boolean
        }

        interface HttpHeader {
            /**
             * The name of the header
             */
            name: string;
            /**
             * The value of the header
             */
            value: string;
        }

        /**
         * Returns true if the current execution context is in a HTTP call
         */
        function isValid(): boolean;

        /**
         * Returns the HTTP request method - GET, POST, PUT, DELETE, HEAD, TRACE
         */
        function getMethod(): string;

        /**
         * Returns the user name performing the request
         */
        function getRemoteUser(): string;

        /**
         * Returns the path info section of the URL
         */
        function getPathInfo(): string;

        /**
         * Returns the translated path
         */

        function getPathTranslated(): string;

        /**
         * Returns the value of the header by name, if any
         * @param name
         */
        function getHeader(name: string): string;

        /**
         * Returns true if the user has the given role and false otherwise
         * @param role
         */
        function isUserInRole(role: string): boolean;

        /**
         * Returns the value of the attribute by name, if any
         */
        function getAttribute(name: string): string;

        /**
         * Returns the authentication type
         */
        function getAuthType(): string;

        /**
         * Returns the content length
         */
        function getContentLength(): bigint;

        /**
         * Returns the names of all the attribute
         */
        function getAttributeNames(): string[];

        /**
         * Returns all the cookies from the request
         */
        function getCookies(): HttpCookie[];

        /**
         * Returns the character encoding
         */
        function getCharacterEncoding(): string;

        /**
         * Returns the array of headers
         */
        function getHeaders(): HttpHeader[];

        /**
         * Returns the content type
         */

        function getContentType(): string;

        /**
         * Returns the content as byte array
         */
        function getBytes(): byte[];

        /**
         * Returns the content as text
         */
        function getText(): string;

        /**
         * Returns a JSON object, after parsing the content as text
         */
        function getJSON(): JSON;

        /**
         * Returns the value of the parameter by name, if any
         * @param name
         */
        function getParameter(name: string): string;

        /**
         * Returns the all the parameters - name and value pairs
         * @param name
         */
        function getParameters(name: string): string[];

        /**
         *
         * @param name
         */
        function getResourcePath(name: string): string;

        /**
         * Returns the names of all the headers
         */
        function getHeaderNames(): string[];

        /**
         * Returns the names of all the parameters
         */
        function getParameterNames(): string[];

        /**
         * Returns the values of the parameter by name
         * @param name
         */
        function getParameterValues(name: string): string[];

        /**
         * Returns the protocol
         */
        function getProtocol(): string;

        /**
         * Returns the scheme
         */
        function getScheme(): string;

        /**
         * Returns the context path
         */
        function getContextPath(): string;

        /**
         * Returns the server name
         */
        function getServerName(): string;

        /**
         *
         */
        function getQueryParametersMap(): JSON;

        /**
         * Returns the remote address
         */
        function getRemoteAddress(): string;

        /**
         * Returns the remote host
         */
        function getRemoteHost(): string;

        /**
         * Sets the value of the attribute by name
         * @param name
         * @param value
         */
        function setAttribute(name: string, value: any);

        /**
         * Sets the value of the attribute by name
         * @param name
         */
        function removeAttribute(name: string);

        /**
         * Returns the locale string
         */
        function getLocale(): string;

        /**
         * Returns the request URL
         */
        function getRequestURI(): string;

        function isSecure(): boolean;

        /**
         * Returns the request URI
         */
        function getRequestURL(): string;

        /**
         * Returns the service path
         */
        function getServicePath(): string;

        /**
         * Returns the remote port
         */
        function getRemotePort(): string;

        /**
         * Returns the local name
         */
        function getLocalName(): string;

        /**
         * Returns the local address
         */
        function getLocalAddress(): string;

        /**
         * Returns the local port
         */
        function getLocalPort(): string;

        function getInputStream(): string;
    }
    module response {

        interface HttpCodesReasons {

        }

        /**
         * Returns true if the current execution context is in a HTTP call
         */
        function isValid(): boolean;

        /**
         * Prints the text to the response body
         * @param text
         */
        function print(text: string);

        /**
         * Prints the text to the response body with line separator at the end
         * @param text
         */
        function println(text: string);

        /**
         * Prints the bytes array to the response body
         * @param bytes
         */
        function write(bytes: []);

        /**
         * Whether response is already committed
         */
        function isCommitted(): boolean;

        /**
         * Sets the content type
         * @param contentType
         */
        function setContentType(contentType: string);

        /**
         * Flushes the content to the response to the client
         */
        function flush();

        /**
         * Closes the response stream to the client
         */
        function close();

        /**
         * Adds a HttpCookie to the response
         * @param cookie
         */
        function addCookie(cookie);

        /**
         * Checks existence of the header by name
         * @param name
         */
        function containsHeader(name: string): boolean;

        /**
         * Returns the encoded url parameter
         * @param url
         */
        function encodeURL(url: string): string;

        /**
         * Returns the character encoding of the response
         */
        function getCharacterEncoding(): string;

        /**
         * Returns the encoded redirect URL
         * @param url
         */
        function encodeRedirectURL(url: string): string;

        /**
         * Returns the content type of the response
         */
        function getContentType(): string;

        /**
         * Sends an error instruction to the client with the given code and message. The message parameter is optional
         * @param status
         * @param message
         */
        function sendError(status: string, message?: string);

        /**
         * Sets the character encoding of the response
         * @param charset
         */
        function setCharacterEncoding(charset: string);

        /**
         * Sends a redirect instruction to the client to the given location
         * @param location
         */
        function sendRedirect(location: string);

        /**
         * Sets the content length of the response
         * @param length
         */
        function setContentLength(length: number);

        /**
         * Updates a header name/value pair to the response
         * @param name
         * @param value
         */
        function setHeader(name: string, value: any);

        /**
         * Adds a header name/value pair to the response
         * @param name
         * @param value
         */
        function addHeader(name: string, value: any);

        /**
         * Sets the status of the response
         * @param status
         */
        function setStatus(status: string);

        /**
         * Resets the response
         */
        function reset();

        /**
         * Returns the header value by name
         * @param name
         */
        function getHeader(name: string): string;

        /**
         * Sets the locale to the response
         * @param language
         * @param country
         * @param variant
         */
        function setLocale(language: string, country: string, variant: string);

        /**
         * Returns the array of header values by name
         * @param name
         */
        function getHeaders(name: string): string[];

        /**
         * Returns the names of all the headers
         */
        function getHeaderNames(): string[];

        /**
         * Returns the locale of the response
         */
        function getLocale(): string;

        /**
         * Returns the OutputStream of the response
         */
        function getOutputStream(): string;

        /**
         * Status code (202) indicating that a request was accepted for processing, but was not completed.
         */
        const ACCEPTED = 202;
        /**
         * Status code (502) indicating that the HTTP server received an invalid response from a server it consulted when acting as a proxy or gateway.
         */
        const BAD_GATEWAY = 502;
        /**
         *    Status code (400) indicating the request sent by the client was syntactically incorrect
         */
        const BAD_REQUEST = 400;
        /**
         * Status code (409) indicating that the request could not be completed due to a conflict with the current state of the resource.
         */
        const CONFLICT = 409;
        /**
         * Status code (100) indicating the client can continue.
         */
        const CONTINUE = 100;
        /**
         * Status code (201) indicating the request succeeded and created a new resource on the server.
         */
        const CREATED = 201;
        /**
         * Status code (417) indicating that the server could not meet the expectation given in the Expect request header.
         */
        const EXPECTATION_FAILED = 417;
        /**
         * Status code (403) indicating the server understood the request but refused to fulfill it.
         */
        const FORBIDDEN = 403;
        /**
         * Status code (302) indicating that the resource reside temporarily under a different URI.
         */
        const FOUND = 302;
        /**
         * Status code (504) indicating that the server did not receive a timely response from the upstream server while acting as a gateway or proxy.
         */
        const GATEWAY_TIMEOUT = 504;
        /**
         * Status code (410) indicating that the resource is no longer available at the server and no forwarding address is known.
         */
        const GONE = 410;
        /**
         * Status code (505) indicating that the server does not support or refuses to support the HTTP protocol version that was used in the request message.
         */
        const HTTP_VERSION_NOT_SUPPORTED = 505;
        /**
         * Status code (500) indicating an error inside the HTTP server which prevented it from fulfilling the request.
         */
        const INTERNAL_SERVER_ERROR = 500;
        /**
         * Status code (411) indicating that the request cannot be handled without a defined Content-Length.
         */
        const LENGTH_REQUIRED = 411;
        /**
         * Status code (405) indicating that the method specified in the Request-Line is not allowed for the resource identified by the Request-URI.
         */
        const METHOD_NOT_ALLOWED = 405;
        /**
         * Status code (301) indicating that the resource has permanently moved to a new location, and that future references should use a new URI with their requests.
         *
         */
        const MOVED_PERMANENTLY = 301;
        /**
         * Status code (302) indicating that the resource reside temporarily under a different URI.
         */
        const MOVED_TEMPORARILY = 302;
        const MULTIPLE_CHOICES = 300;
        const NO_CONTENT = 204;
        const NON_AUTHORITATIVE_INFORMATION = 203;
        const NOT_ACCEPTABLE = 406;
        const NOT_FOUND = 404;
        const NOT_IMPLEMENTED = 501;
        const NOT_MODIFIED = 304;
        const OK = 200;
        const PARTIAL_CONTENT = 206;
        const PAYMENT_REQUIRED = 402;
        const PRECONDITION_FAILED = 412;
        const PROXY_AUTHENTICATION_REQUIRED = 407;
        const REQUEST_ENTITY_TOO_LARGE = 413;
        const REQUEST_TIMEOUT = 408;
        const REQUEST_URI_TOO_LONG = 414;
        const REQUESTED_RANGE_NOT_SATISFIABLE = 416;
        const RESET_CONTENT = 205;
        const SEE_OTHER = 303;
        const SERVICE_UNAVAILABLE = 503;
        const SWITCHING_PROTOCOLS = 101;
        const TEMPORARY_REDIRECT = 307;
        const UNAUTHORIZED = 401;
        const UNSUPPORTED_MEDIA_TYPE = 415;
        const USE_PROXY = 305;
        let HttpCodesReasons: HttpCodesReasons;
    }

    /**
     *
     */
    module rs {

        const Controller;
        function Get(path: string);
        function Post(path: string);
        function Put(path: string);
        function Patch(path: string);
        function Delete(path: string);
        function Head(path: string);
        function Options(path: string);
        function service(oConfig?: ResourceMappings): HttpController

        interface HttpController {
            /**
             * Returns the mappings configured for this controller instance.
             */
            mappings(): ResourceMappings;

            /**
             * processes HTTP requests, to match path, method and constraints to resource mappings and invoke callback handler functions accordingly and generate response.
             * @param oRequest
             * @param oResponse
             */
            execute(oRequest?, oResponse?);

            /**
             * Returns Resource with oConfigurations if provided
             * @param oConfiguration
             */
            resource(oConfiguration?): Resource;

        }

        interface ResourceMappings {
            /**
             * Returns the resource configuration object optionally initialized with oConfiguration
             * @param oConfiguration
             */
            resource(oConfiguration?): Resource;

            /**
             * Returns the configuration for this ResourceMappings object
             */
            configuration(): Object;

            /**
             * Disables all but GET requests to this API
             */
            readonly(): ResourceMappings;

            /**
             * Disables the handling of requests sent to path sPath with HTTP method sVerb and with consumes media type arrConsumes and produces media type arrProduces media type constraints
             * @param sPath
             * @param sVerb
             * @param arrConsumes
             * @param arrProduces
             */
            disable(sPath, sVerb, arrConsumes, arrProduces): ResourceMappings;

            /**
             * Finds a request handler for requests sent to path sPath with HTTP method sVerb and with consumes media type arrConsumes and produces media type arrProduces media type constraints
             * @param sPath
             * @param sVerb
             * @param arrConsumes
             * @param arrProduces
             */

            find(sPath, sVerb, arrConsumes, arrProduces): ResourceMethod;

            /**
             * Executes the service
             * @param oRequest
             * @param oResponse
             */
            execute(oRequest?, oResponse?);
        }

        interface Resource {
            /**
             * Returns the get method configuration object, optionally configured with fServeCallback for serving requests
             * @param fServeCallback
             */
            get(fServeCallback?): ResourceMethod;

            /**
             * Returns the post method configuration object, optionally configured with fServeCallback for serving requests
             * @param fServeCallback
             */

            post(fServeCallback?): ResourceMappings;

            /**
             * Returns the put method configuration object, optionally configured with fServeCallback for serving requests
             * @param fServeCallback
             */
            put(fServeCallback?): ResourceMethod;

            /**
             * Returns the delete method configuration object, optionally configured with fServeCallback for serving requests
             * @param fServeCallback
             */
            delete(fServeCallback?): ResourceMethod;

            /**
             * Returns the delete method configuration object, optionally configured with fServeCallback for serving requests
             * @param fServeCallback
             */
            remove(fServeCallback?): ResourceMethod;

            /**
             * Returns the a method configuration object for the sHttpVerb HTTP method name and optionally initialized with oConfiguration object
             * @param sHttpVerb
             * @param oConfiguration
             */
            method(sHttpVerb, oConfiguration?): ResourceMethod;

            /**Returns the configuration for this Resource object
             *
             */
            configuration(): Object;

            /**
             * Disables all but GET requests to this resource
             */
            readonly(): ResourceMappings;

            /**
             * Disables the handling of requests sent to this resource path with HTTP method sVerb and with consumes media type arrConsumes and produces media type arrProduces media type constraints
             * @param sVerb
             * @param arrConsumesTypeStrings
             * @param arrProducesTypeStrings
             */
            disable(sVerb, arrConsumesTypeStrings, arrProducesTypeStrings): ResourceMappings;

            /**
             * Finds a request handler for requests sent to this resource path with HTTP method sVerb and with consumes media type arrConsumes and produces media type arrProduces media type constraints
             * @param sVerb
             * @param arrConsumesMimeTypeStrings
             * @param arrProducesMimeTypeStrings
             */
            find(sVerb, arrConsumesMimeTypeStrings: any[], arrProducesMimeTypeStrings: any[]): ResourceMethod;

            /**
             * Executes the service
             * @param oRequest
             * @param oResponse
             */
            execute(oRequest?, oResponse?);
        }

        interface ResourceMethod {
            /**
             * Returns the configuration for this ResourceMethod object
             */
            configuration(): Object

            /**
             * Assigns a consumes constraint for this verb handler configuration.
             * @param arrMediaTypeStrings
             */
            consumes(arrMediaTypeStrings): ResourceMethod;

            /**
             * Assigns a produces constraint for this verb handler configuration.
             * @param arrMediaTypeStrings
             */
            produces(arrMediaTypeStrings): ResourceMethod;

            /**
             * Assign a before callback function for this verb handler configuration
             * @param somefunc
             */
            before(somefunc): ResourceMethod;

            /**
             * Assign a verb handler function for this verb handler configuration
             * @param somefunc
             */
            serve(somefunc): ResourceMethod;

            /**
             * Assign a catch on error callback function for this verb handler configuration
             * @param somefunc
             */
            catch(somefunc): ResourceMethod

            /**
             * Assign a finally callback function for this verb handler configuration
             * @param somefunc
             */

            finally(somefunc): ResourceMethod

            /**
             * Executes the service
             * @param oRequest
             * @param oResponse
             */
            execute(oRequest?, oResponse?);
        }

        /**
         * Creates an HttpController instance, optionally initialized with a JS configuration or ResourceMappings object
         * @param oMappings
         */
        function service(oMappings?): HttpController;
    }
    /**
     * Client is used by scripting services to call external services via HTTP.
     */
    module client {
        /**
         * Makes a HTTP GET request to a remote service at the URL by the HttpOptions and returns the result
         * @param url
         * @param options
         */
        function get(url: string, options?: HttpOptions): HttpResponse;

        /**
         * Makes a HTTP POST request to a remote service at the URL by the HttpOptions and returns the result
         * @param url
         * @param options
         */

        function post(url: string, options?: HttpOptions): HttpResponse;

        /**
         * Makes a HTTP PUT request to a remote service at the URL by the HttpOptions and returns the result
         * @param url
         * @param options
         */

        function put(url: string, options?: HttpOptions): HttpResponse;

        /**
         * Makes a HTTP DELETE request to a remote service at the URL by the HttpOptions and returns the result
         * @param url
         * @param options
         */
        function patch(url: string, options?: HttpOptions): HttpResponse;

        /**
         * Makes a HTTP HEAD request to a remote service at the URL by the HttpOptions and returns the result
         * @param url
         * @param options
         */
        function head(url: string, options?: HttpOptions): HttpResponse;
        /**
         * Makes a HTTP TRACE request to a remote service at the URL by the HttpOptions and returns the result
         * @param url
         * @param options
         */
        function trace(url: string, options?: HttpOptions): HttpResponse;

        //TODO resolve name of the function delete because its reserved word in d.ts
        // /**
        //  * Makes a HTTP DELETE request to a remote service at the URL by the HttpOptions and returns the result
        //  */
        // function delete(url: string, options?: HttpOptions): HttpResponse;

    }
    module session {
        /**
         * Returns the HTTP session attribute by name
         * @param name
         */
        function getAttribute(name: string): string;

        /**
         * Returns true if the current execution context is in a HTTP call
         */
        function isValid(): boolean;

        /**
         * Returns all the HTTP session attributes names
         */
        function getAttributeNames(): string[];

        /**
         * Returns the time when the HTTP session has been initialized
         */
        function getCreationTime(): Date;

        /**
         * Returns the HTTP session ID
         */
        function getId(): string;

        /**
         * Returns the time when the HTTP session has been last accessed
         */
        function getLastAccessedTime(): Date;

        /**
         * Returns the maximum inactive interval of this HTTP session
         */
        function getMaxInactiveInterval(): number;

        /**
         * Invalidates this HTTP session
         */
        function invalidate();

        /**
         * Returns true, if the HTTP session is created during this HTTP call and false otherwise
         */
        function isNew(): boolean;

        /**
         * Sets the HTTP session attribute by name and value
         * @param name
         * @param value
         */
        function setAttribute(name: string, value: any): string;

        /**
         * Removes the HTTP session attribute by name
         * @param name
         */
        function removeAttribute(name: string);

        /**
         * Sets the maximum inactive interval of this HTTP session
         * @param interval
         */
        function setMaxInactiveInterval(interval: number);
    }
    /**
     * HTTP Upload is used to consume files posted as multipart request.
     */
    module upload {
        /**
         * Returns true if the HTTP request contains files content and false otherwise
         */
        function isMultipartContent(): boolean;

        /**
         * Returns a HttpFileItems object by parsing the HTTP request
         */
        function parseRequest(): HttpFileItems;
    }
    interface url {
    }
    class byte {
    }
    /**
     * HTTP Session object provided to the scripting services implementation to hold session attributes for multiple client requests.
     */

    /**
     * HttpFileItems object
     */
    interface HttpFileItems {
        /**
         * The HttpFileItem object by the index
         * @param index
         */
        get(index: number): HttpFileItem;

        /**
         * The size of the list of HttpFileItem objects
         */
        size(): number;
    }
    /**
     * HttpFileItem object
     */
    interface HttpFileItem {

        /**
         * Return the input stream of the HttpFileItem's content
         */
        getInputStream(): [];

        /**
         * The HttpFileItem's data content type
         */
        getContentType(): string;

        /**
         * The HttpFileItem's name
         */
        getName(): string;

        /**
         * The HttpFileItem's size
         */
        getSize(): number;

        /**
         * Return the HttpFileItem's content as byte array
         */
        getBytes(): [];

        getBytesNative();

        /**
         * Return the HttpFileItem's content as string
         */
        getText(): string;

        /**
         * Whether the HttpFileItem represents a form field
         */
        isFormField(): boolean;

        /**
         * The HttpFileItem's field name
         */
        getFieldName(): string;

        /**
         * The HttpFileItem's headers
         */
        getHeaders(): HttpFileItemHeaders;
    }
    /**
     * HttpFileItemHeaders object
     */
    interface HttpFileItemHeaders {
        /**
         * The HttpFileItemHeader's names
         */
        getHeaderNames(): HttpFileItemHeaderNames;

        /**
         * The HttpFileItemHeader's value for the given header name
         */
        getHeader(headerName: string): string;
    }
    /**
     * HttpFileItemHeaderNames object
     */
    interface HttpFileItemHeaderNames {
        /**
         * Size of HttpFileItemHeaderNames array
         */
        size(): number;

        /**
         * Get HeaderName by index
         * @param index
         */
        get(index): string;
    }
    interface HttpAsyncClient {
        execute();

        /**
         * Makes a HTTP GET Async request to a remote service at the URL by the HttpOptions and returns HttpResponse to the HttpResponseCallback
         * @param url
         * @param config
         * @param options
         */
        getAsync(url: string, config: object, options?: string);

        /**
         * Makes a HTTP POST Async request to a remote service at the URL by the HttpOptions and returns HttpResponse to the HttpResponseCallback
         * @param url
         * @param config
         * @param options
         */
        postAsync(url: url, config: string, options?: string);

        /**
         * Makes a HTTP PUT Async request to a remote service at the URL by the HttpOptions and returns HttpResponse to the HttpResponseCallback
         * @param url
         * @param config
         * @param options
         */
        patchAsync(url: url, config: string, options?: string);

        /**
         * Makes a HTTP DELETE Async request to a remote service at the URL by the HttpOptions and returns HttpResponse to the HttpResponseCallback
         * @param url
         * @param config
         * @param options
         */
        deleteAsync(url: url, config: string, options?: string);

        /**
         * Makes a HTTP HEAD Async request to a remote service at the URL by the HttpOptions and returns HttpResponse to the HttpResponseCallback
         * @param url
         * @param config
         * @param options
         */
        headAsync(url: url, config: string, options: string);

        /**
         * Makes a HTTP TRACE Async request to a remote service at the URL by the HttpOptions and returns HttpResponse to the HttpResponseCallback
         * @param url
         * @param config
         * @param options
         */
        traceAsync(url: url, config: string, options: string);

        execute(): any;

    }
    class HttpResponse {
        /**
         * The Response status code
         */
        statusCode: number;
        /**
         * The Response status message
         */
        statusMessage: string;
        /**
         * The Response data
         */
        data: byte[];
        /**
         * The Response data as text
         */
        text: string;
        /**
         * Whether the Response data is binary in data or string in text
         */
        binary: boolean;
        /**
         * The HTTP version of the Response
         */
        protocol: string;
        /**
         * The Response headers
         */
        headers: HttpHeader[];
    }
    interface HttpHeader {
        /**
         * The name of the header
         */
        name: string
        /**
         * The value of the header
         */
        value: string
    }
    class HttpParam {
        /**
         * The name of the param
         */
        name: string
        /**
         * The value of the param
         */
        value: string
    }
    class HttpOptions {
        /**
         * The body of the HTTP Request as binary
         */
        data?: byte[];
        /**
         * The body of the HTTP Request as text
         */
        text?: string;
        /**
         * The body of the HTTP Request as files (for POST)
         */
        files?: string[];
        /**
         * The body of the HTTP Request as form parameters
         */
        params?: HttpParam[];

        /**
         * Whether the body of the HTTP Request is binary
         */
        binary?: boolean;
        /**
         * The character encoding enabled parameter. Default is true
         */
        characterEncodingEnabled?: boolean;
        /**
         * The character encoding parameter. Default is UTF-8
         */
        characterEncoding?: string;
        /**
         * The content type parameter. Default is text/plain
         */
        contentType?: string;
        /**
         * The Response headers
         */
        headers?: HttpHeader[];
        /**
         * The proxy host parameter
         */
        proxyHost?: string;
        /**
         * The proxy port parameter
         */
        proxyPort?: number;
        /**
         * The continue enabled parameter
         */
        expectContinueEnabled?: boolean;
        /**
         * The cookieSpec parameter
         */
        cookieSpec?: string;
        /**
         * The redirects enabled parameter
         */
        redirectsEnabled?: boolean;
        /**
         * The relative redirects allowed parameter
         */
        relativeRedirectsAllowed?: boolean;
        /**
         * The circular redirects allowed parameter
         */
        circularRedirectsAllowed?: boolean;
        /**
         * The max redirects parameter
         */
        maxRedirects?: number;
        /**
         * The authentication enabled parameter
         */
        authenticationEnabled?: boolean;
        /**
         * The target preferred authentication schemes parameter
         */
        targetPreferredAuthSchemes?: string[];
        /**
         * The proxy preferred authentication schemes parameter
         */
        proxyPreferredAuthSchemes?: string[];
        /**
         * The connection request timeout parameter
         */
        connectionRequestTimeout?: number;
        /**
         * The connect timeout parameter
         */
        connectTimeout?: number;
        /**
         * The socket timeout parameter
         */
        socketTimeout?: number;
        /**
         * The content compression enabled parameter
         */
        contentCompressionEnabled?: boolean;
        /**
         * The SSL trust all enabled parameter
         */
        sslTrustAllEnabled?: boolean;
    }
}
