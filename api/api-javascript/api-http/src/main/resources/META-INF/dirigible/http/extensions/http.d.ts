declare module "@dirigible/http" {

    interface FileItems {
        get(index): FileItem;

        size(): number;
    }

    interface FileItem {
        getInputSStream(): string;

        getContentType(): string;

        getName(): string;

        getSize(): number;

        getBytes(): [];

        getBytesNative();

        getText(): string;

        isFormField(): boolean;

        getFieldName(): string;

        getHeaders(): Headers;
    }

    interface Headers {
        getHeaders(): string;

        getHeader(headerName: string): string;
    }

    interface url {
    }

    interface HttpAsyncClient {
        getAsync(url: url, config: string, options: string): JSON;

        postAsync(url: url, config: string, options: string): JSON;

        patchAsync(url: url, config: string, options: string): JSON;

        deleteAsync(url: url, config: string, options: string): JSON;

        headAsync(url: url, config: string, options: string): JSON;

        traceAsync(url: url, config: string, options: string): JSON;

        execute(): any;

    }

    module rsdata {

        interface DataService {

            mappings()


            dao(ormConfig?): DataService;


            logger();


            execute(oRequest?, oResponse?);
        }

        function service(oConfiguration?, oProtocolHandlersAdapter?, oDataProtocolDefinition?, sLoggerName?): DataService;
    }

    module rs {
        interface HttpController {
            resource(oConfiguration?): Resource;

            mappings(): ResourceMappings;

            execute(oRequest?, oResponse?);
        }

        interface ResourceMappings {
            resource(oConfiguration?): Resource;

            configuration(): Object;

            readonly(): ResourceMappings;

            disable(sPath, sVerb, arrConsumes, arrProduces): ResourceMappings;

            find(sPath, sVerb, arrConsumes, arrProduces): ResourceMethod;

            execute(oRequest?, oResponse?);
        }

        interface Resource {
            get(fServeCallback?): ResourceMethod;

            post(fServeCallback?): ResourceMethod;

            put(fServeCallback?): ResourceMethod;

            delete(fServeCallback?): ResourceMethod;

            remove(fServeCallback?): ResourceMethod;

            method(sHttpVerb, oConfiguration?): ResourceMethod;

            configuration(): Object;

            readonly(): ResourceMappings;

            disable(sVerb, arrConsumesTypeStrings, arrProducesTypeStrings): ResourceMappings;

            find(sVerb, arrConsumesMimeTypeStrings: any[], arrProducesMimeTypeStrings: any[]): ResourceMethod;

            execute(oRequest?, oResponse?);
        }

        interface ResourceMethod {

            configuration(): Object


            consumes(arrMediaTypeStrings): ResourceMethod;

            produces(arrMediaTypeStrings): ResourceMethod;


            before(somefunc): ResourceMethod;

            serve(somefunc): ResourceMethod;


            catch(somefunc): ResourceMethod


            finally(somefunc): ResourceMethod


            execute(oRequest?, oResponse?);
        }


        function service(oConfig?): HttpController;
    }


    module client {
        function get(url: string, options: string): object;

        function post(url: string, options: string): object;

        function put(url: string, options: string): object;

        function patch(url: string, options: string): object;

        // function delete(url: string, options: string): object;
        function head(url: string, options: string): object;

        function trace(url: string, options: string): object;

        function buildUrl(url: string, options: string): object;
    }
    module clientAsync {
        function getInstance(): HttpAsyncClient;

        //need to be deleted
        function getInstnace(): HttpAsyncClient;
    }
    module request {
        function isValid(): boolean;

        function getMethod(): string;

        function getRemoteUser(): string;

        function getPathInfo(): string;

        function getPathTranslated(): string;

        function getHeader(name: string): string;

        function isUserInRole(role: string): string;

        function getAttribute(name: string): string;

        function getAuthType(): string;

        function getContentLength(): bigint;

        function getAttributeNames(): string [];

        function getCookies(): string [];

        function getCharacterEncoding(): string;

        function getHeaders(): string [];

        function getContentType(): string;

        function getBytes(): string;

        function getText(): string;

        function getJSON(): JSON;

        function getParameter(name: string): string;

        function getParameters(name: string): string[];

        function getResourcePath(name: string): string;

        function getHeaderNames(name: string): string;

        function getParameterNames(): string[];

        function getParameterValues(name: string): string[];

        function getProtocol(): string;

        function getScheme(): string;

        function getContextPath(): string;

        function getServerName(): string;

        function getQueryParametersMap(): JSON;

        function getRemoteAddress(): string;

        function getRemoteHost(): string;

        function setAttribute(name: string, value: any);

        function removeAttribute(name: string);

        function getLocale(): string;

        function getRequestURI(): string;

        function isSecure(): boolean;

        function getRequestURL(): string;

        function getServicePath(): string;

        function getRemotePort(): string;

        function getLocalName(): string;

        function getLocalAddress(): string;

        function getLocalPort(): string;

        function getInputStream(): string;
    }
    module response {
        interface HttpCodesReasons {
        }

        function isValid(): boolean;

        function print(text: string);

        function println(text: string);

        function write(bytes: string);

        function isCommitted(): boolean;

        function setContentType(contentType: string);

        function flush();

        function close();

        function addCookie(cookie: string);

        function containsHeader(name: string);

        function encodeURL(url: string): string;

        function getCharacterEncoding(): string;

        function encodeRedirectURL(url: string): string;

        function getContentType(): string;

        function sendError(status: string, message: string);

        function setCharacterEncoding(charset: string);

        function sendRedirect(location: string);

        function setContentLength(length: number);

        function setHeader(name: string, value: any);

        function addHeader(name: string, value: any);

        function setStatus(status: string);

        function reset();

        function getHeader(name: string): string;

        function setLocale(language: string, country: string, variant: string);

        function getHeaders(name: string): string;

        function getHeaderNames(): string;

        function getLocale(): string;

        function getOutputStream(): string;

        const ACCEPTED = 202;
        const BAD_GATEWAY = 502;
        const BAD_REQUEST = 400;
        const CONFLICT = 409;
        const CONTINUE = 100;
        const CREATED = 201;
        const EXPECTATION_FAILED = 417;
        const FORBIDDEN = 403;
        const FOUND = 302;
        const GATEWAY_TIMEOUT = 504;
        const GONE = 410;
        const HTTP_VERSION_NOT_SUPPORTED = 505;
        const INTERNAL_SERVER_ERROR = 500;
        const LENGTH_REQUIRED = 411;
        const METHOD_NOT_ALLOWED = 405;
        const MOVED_PERMANENTLY = 301;
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
    module session {
        function getAttribute(name: string): string;

        function isValid(): boolean;

        function getAttributeNames(): string [];

        function getCreationTime(): Date;

        function getId(): string;

        function getLastAccessedTime(): Date;

        function getMaxInactiveInterval(): number;

        function invalidate();

        function isNew(): boolean;

        function setAttribute(name: string, value: any);

        function removeAttribute(name: string);

        function setMaxInactiveInterval(interval: number)
    }
    module upload {
        function isMultipartContent(): boolean;

        function parseRequest(): FileItems;
    }
}