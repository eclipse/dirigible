$ = (function(){
    var Enumeration = {
        hasMoreElements : function() {return true;},
        nextElement : function() {return "";}
    }

    var Cookie = {
        getName : function() {return "";},
        getPath : function() {return "";},
        getValue :  function() {return "";},
        getVersion :  function() {return 0;},
        setName : function(name){},
        setPath : function(path){},
        setValue :  function(value){},
        setVersion :  function(index){}
    }
    var ResultSet = {
        next : function() {return true},
        execute : function() {},
        getString : function(index) {return ""},
        getLong : function(index) {return 0},
        getInt : function(index) {return 0},
        getDouble : function(index) {return 0.0},
        getFloat : function(index) {return 0.0},
        getDate : function(index) {return Date},
        getTime : function(index) {return Date},
        getTimestamp : function(index) {return Date},
        close : function() {}
    }

    var PrintWritter = {
        println : function(input) {},
        print : function(input) {},
        flush :  function() {},
        close : function() {}
    }

    var PrintStream = {
        println : function(input) {},
        print : function(input) {},
        flush :  function() {},
        close : function() {}
    }

    var Collection = {
        add : function(value) {},
        remove :  function(value) {},
        contains : function(value) {return true;},
        isEmpty : function() {return true;},
        size : function() {return 0;},
        keySet : function() { return this; },
        values : function() {},
        clear : function() {},
        toArray : function() {}
    }

    var Map = {
        put : function(key, value) {},
        get :  function(key) {},
        containsKey : function(key) {return true;},
        containsValue : function(value) {return true;},
        isEmpty : function() {return true;},
        size : function() {return 0;},
        keySet : function() { return Collection },
        values : function() { return Collection; },
        clear : function() {}
    }

    var Session = {
        getAttribute : function(key) {return ""},
        setAttribute : function(key, value) {},
        getAttributeNames : function() { return new Enumeration(); },
        removeAttribute : function(key) {},
        invalidate : function() {}
    }

    var Response = {
        addCookie : function(cookie) {},
        containsHeader : function(name) {return true;},
        sendError : function(code, message) {},
        sendRedirect : function(location) {},
        setHeader : function(name, value) {},
        addHeader : function(name, value) {},
        setStatus : function(code) {},
        getWriter : function() { return PrintWritter; },
        getContentType : function() {return "";},
        getContentLength : function() {return "";},
        getContentType : function() {return "";},
        getCharacterEncoding : function() {return "";},
        setContentType : function(contentType) {},
        setContentLength : function(length) {},
        setContentType : function(contentType) {},
        setCharacterEncoding : function(characterEncoding) {},
        SC_CONTINUE : 100,
        SC_SWITCHING_PROTOCOLS : 101,
        SC_OK : 200,
        SC_CREATED : 201,
        SC_ACCEPTED : 202,
        SC_NON_AUTHORITATIVE_INFORMATION : 203,
        SC_NO_CONTENT : 204,
        SC_RESET_CONTENT : 205,
        SC_PARTIAL_CONTENT : 206,
        SC_MULTIPLE_CHOICES : 300,
        SC_MOVED_PERMANENTLY : 301,
        SC_MOVED_TEMPORARILY : 302,
        SC_FOUND : 302,
        SC_SEE_OTHER : 303,
        SC_NOT_MODIFIED : 304,
        SC_USE_PROXY : 305,
        SC_TEMPORARY_REDIRECT : 307,
        SC_BAD_REQUEST : 400,
        SC_UNAUTHORIZED : 401,
        SC_PAYMENT_REQUIRED : 402,
        SC_FORBIDDEN : 403,
        SC_NOT_FOUND : 404,
        SC_METHOD_NOT_ALLOWED : 405,
        SC_NOT_ACCEPTABLE : 406,
        SC_PROXY_AUTHENTICATION_REQUIRED : 407,
        SC_REQUEST_TIMEOUT : 408,
        SC_CONFLICT : 409,
        SC_GONE : 410,
        SC_LENGTH_REQUIRED : 411,
        SC_PRECONDITION_FAILED : 412,
        SC_REQUEST_ENTITY_TOO_LARGE : 413,
        SC_REQUEST_URI_TOO_LONG : 414,
        SC_UNSUPPORTED_MEDIA_TYPE : 415,
        SC_REQUESTED_RANGE_NOT_SATISFIABLE : 416,
        SC_EXPECTATION_FAILED : 417,
        SC_INTERNAL_SERVER_ERROR : 500,
        SC_NOT_IMPLEMENTED : 501,
        SC_BAD_GATEWAY : 502,
        SC_SERVICE_UNAVAILABLE : 503,
        SC_GATEWAY_TIMEOUT : 504,
        SC_HTTP_VERSION_NOT_SUPPORTED : 505
    }
    var DataSource = {
        getConnection : function() { return Connection; }
    }

    var Connection = {
    	createStatement : function() {return Statement; },
        prepareStatement : function(sql) {return PreparedStatement; },
        setAutoCommit : function(autoCommit) {},
        commit : function() {},
        rollback : function() {},
        close : function() {}
    }
    var Statement = {
        executeQuery : function(sql) { return ResultSet; },
        executeUpdate : function(sql) {},
        execute : function(sql) {},
        close : function() {}
    }
    var PreparedStatement = {
        executeQuery : function() { return ResultSet; },
        executeUpdate : function() {},
        execute : function() {},
        setString : function(index, value) {},
        setLong : function(index, value) {},
        setInt : function(index, value) {},
        setDouble : function(index, value) {},
        setFloat : function(index, value) {},
        setDate : function(index, value) {},
        setTime : function(index, value) {},
        setTimestamp : function(index, value) {},
        close : function() {}
    }
    var Request = {
        getCookies : function() {return [Cookie]; },
        getHeader : function(name) {return "";},
        getHeaders : function() { return new Enumeration(); },
        getHeaderNames : function() { new Enumeration(); },
        getMethod : function() {return "";},
        getPathInfo : function() {return "";},
        getContextPath : function() {return "";},
        getQueryString : function() {return "";},
        isUserInRole : function(role) {return true;},
        getParameter : function(name) {return "";},
        getParameterMap : function() { return Map; },
        getParameterNames : function() { return new Enumeration(); },
        getProtocol : function() {return "";},
        getScheme : function() {return "";},
        getServerName : function() {return "";},
        getServerPort : function() {return 0;}
    }
    
    
    
    
    
    
    var InitialContext = {
	    bind : function(key, value) {},
	    lookup : function(key) {},
	    rebind : function(key, value) {},
	    unbind : function(key) {}
	}
	
	var Repository = {
		SEPARATOR : "/",
		getRoot : function() { return ICollection; },
		createCollection : function(path) { return ICollection; },
		getCollection : function(path) { return ICollection; },
		removeCollection : function(path) {},
		hasCollection : function(path) { return true; },
		createResource : function(path, content, isBinary, contentType) { return IResource; },
		getResource : function(path) { return IResource; },
		removeResource : function(path) {},
		hasResource : function(path) { return true; },
		searchName : function(parameter, caseInsensitive) { return Collection; },
		searchPath : function(parameter, caseInsensitive) { return Collection; },
		searchText : function(parameter, caseInsensitive) { return Collection; }
	}
	
	var ICollection = {
	    getCollections : function() { return Collection; },
	    getCollectionsNames : function() { return Collection; },
		createCollection : function(name) { return ICollection; },
		getCollection : function(name) { return ICollection; },
		removeCollection : function(name) {},
		removeCollection : function(collection) {},
		getResources : function() { return Collection; },
		getResourcesNames : function() { return Collection; },
		getResource : function() { return IResource; },
		removeResource : function(name) {},
		getChildren : function() { return Collection(); },
		createResource : function( name, content, isBinary, contentType) { return IResource},
	    getName : function() { return ""; },
		getPath : function() { return ""; },
		getParent : function() { return ICollection; },
		create : function() {},
		delete : function() {},
		renameTo : function(name) {},
		exists : function() { return true; },
		isEmpty : function() { return true; }
	}
	
	var IResource = {
	    getContent : function() { return []; },
		setContent : function(content, isBinary, contentType) {},
		isBinary : function() { return true;},
		getContentType : function() { return "";},
	    getName : function() { return ""; },
		getPath : function() { return ""; },
		getParent : function() { return ICollection; },
		create : function() {},
		delete : function() {},
		renameTo : function(name) {},
		exists : function() { return true; },
		isEmpty : function() { return true; }
	}
	
	var IEntity = {
	    getName : function() { return ""; },
		getPath : function() { return ""; },
		getParent : function() { return ICollection; },
		create : function() {},
		delete : function() {},
		renameTo : function(name) {},
		exists : function() { return true; },
		isEmpty : function() { return true; }
	}
	
	var IStorage = {
	    exists : function() { return true; },
	    clear : function() {},
	    delete : function(path) {},
	    put : function(path, data, contentType) {},
	    get : function(path) { return []; }
	}
    
    var IMailService = {
    	sendMail : function(from, to, subject, content) {}
    }
    
    var IExtensionService = {
    	getExtensions : function(extensionPoint) { return [""]},
    	getExtension : function(extension, extensionPoint) { return ExtensionDefinition; },
    	getExtensionPoint : function(extensionPoint) { return ExtensionPointDefinition; },
    	getExtensionPoints : function() { return [""]},
    	createExtension : function(extension, extensionPoint, description) {},
    	updateExtension : function(extension, extensionPoint, description) {},
    	createExtensionPoint : function(extensionPoint, description) {},
    	updateExtensionPoint : function(extensionPoint, description) {},
    	removeExtension : function(extension, extensionPoint) {},	
    	removeExtensionPoint : function(extensionPoint) {}
    }
    
    var ExtensionDefinition = {
    	getLocation : function() { return ""},
    	getExtensionPoint : function() { return ""},
    	getDescription : function() { return ""},
    	getCreatedBy : function() { return ""},
    	getCreatedAt : function() { return Date;}
    };
    
    var ExtensionPointDefinition = {
		getLocation : function() { return ""},
    	getDescription : function() { return ""},
    	getCreatedBy : function() { return ""},
    	getCreatedAt : function() { return Date;}
    }
    
    var Date = {
    	getTime = function() { return 0},
    	toString() = function() { return ""}
    }
    
    var IIndexingService = {
    	getIndex : function(indexName) { return IIndex;}
    }
    
    var IIndex = {
    	clearIndex : function() {},
    	search : function(term) { return Collection; },
    	createDocument : function(id, content) { return IDocument},
    	indexDocument : function(document){},
    	deleteDocument : function(document){},
    	updateDocument : function(document){},
    	getLastIndexed : function(){return Date;}
    }
    
    var IDocument = {
    		
    }
    
    var IConnectivityService = {
    	getConnectivityConfiguration : function() {}
    }
    
    var IMessagingService = {
    	/** Registers a Client by Name or does nothing if such a Client exists */
    	registerClient : function(clientName) {},
    	/** Unregisters a Client by Name */
    	unregisterClient : function(clientName) {},
    	/** Checks the existence of a Client by Name */
    	isClientExists : function(clientName) { return true;},
    	/** Registers a Topic by Name or does nothing if such a Topic exists */
    	registerTopic : function(topicName) {},
    	/** Unregisters a CTopiclient by Name */
    	unregisterTopic : function(topicName) {},
    	/** Checks the existence of a Topic by Name */
    	isTopicExists : function(topicName) { return true;},
    	/** Subscribes a given Client for a given Topic,
    	 * so that this Client will get the new messages 
    	 * from this Topic after the Routing Process */
    	subscribe : function(client, topic) {},
    	/** Un-subscribes a given Client from a given Topic */
    	unsubscribe : function(client, topic) {},
    	/** Checks whether subscription of a given Client to a given Topic exists */
    	isSubscriptionExists : function(subscriber, topic) { return true;},
    	/** Sends a message to the hub */
    	send : function(sender, topic, subject, body) {},
    	/** Sends a message to the hub */
    	sendMessage : function(messageDefinition) {},
    	/** Get all the new messages for this Client for all the Topics */
    	receive : function(receiver) { return Collection;},
    	/** Get all the new messages for this Client for a given Topic */
    	receiveByTopic : function(receiver, topic) { return Collection;},
    	/**
    	 * Triggers the Routing Process.
    	 * Takes new Incoming messages and creates
    	 * Outgoing links for all subscribed Clients,
    	 * so that they can retrieve them by calling receive() method
    	 */
    	route : function() {},
    	/** Removes older messages */
    	cleanup : function() {}
    }
    
    var MessageDefinition = {
   		getId : function() { return 0;},
   		getTopic : function() { return "";},
   		setTopic : function(topic) {},
   		getSubject : function() { return "";},
   		setSubject : function(topic) {},
   		getBody : function() { return "";},
   		setBody : function(topic) {},
   		getSender : function() { return "";},
   		setSender : function(topic) {},
   		getCreatedBy : function() { return "";},
   		getCreatedAt : function() { return Date;}
    }
    
    var IOUtils = {
    	toString : function(inputStream) { return ""; }
    }
    
    var HttpUtils= {
    	createGet : function(url) {},
    	createPost : function(url) {},
    	createPut : function(url) {},
    	createDelete : function(url) {},
    	createHttpClient : function(trustAll) {},
    	consume : function(entity) {},
    	createBasicScheme : function() {},
    	createDigestScheme : function() {},
    	createBasicHeader : function(name, value) {}
    }
    
    var Base64Utils= {
    	encodeBase64 : function(input) {return []},
    	decodeBase64 : function(input) {return []}
    }
    
    var HexUtils= {
		encodeHex : function(input) {return []},
    	decodeHex : function(input) {return []}
    }
    
    var DigestUtils= {
		md5 : function(input) {return []},
		md5Hex : function(input) {return []},
		sha : function(input) {return []},
		sha256 : function(input) {return []},
		sha384 : function(input) {return []},
		sha512 : function(input) {return []},
		shaHex : function(input) {return []},
    }
    
    var UrlUtils= {
		encode : function(url, enc) {return []},
    	decode : function(url, enc) {return []}
    }
    
    var UploadUtils= {
    	
    }
    
    var UuidUtils= {
		randomUUID : function() {return UUID; },
    	fromString : function(uuid) {return UUID; }
    }
    
    var UUID = {
    	toString : function() { return ""; }
    }
    
    var DatabaseUtils= {
    	getNext : function(sequenceName){ return 0; },
    	createSequence : function(sequenceName, start){ return 0; },
    	dropSequence : function(sequenceName){ return 0; },
    	existSequence : function(sequenceName){ return true; },
    	createLimitAndOffset : function(limit, offset){ return ""; },
    	createTopAndStart : function(limit, offset){ return ""; },
    	createDate : function(milliseconds){ return Date; },
    	createTime : function(milliseconds){ return Date; },
    	createTimestamp : function(milliseconds){ return Date; }
    }
    
    var XssUtils= {
    	escapeCsv : function(input){ return ""; },
    	escapeHtml : function(input){ return ""; },
    	escapeJava : function(input){ return ""; },
    	escapeJavaScript : function(input){ return ""; },
    	escapeSql : function(input){ return ""; },
    	escapeXml : function(input){ return ""; },
    	unescapeCsv : function(input){ return ""; },
    	unescapeHtml : function(input){ return ""; },
    	unescapeJava : function(input){ return ""; },
    	unescapeJavaScript : function(input){ return ""; },
    	unescapeSql : function(input){ return ""; },
    	unescapeXml : function(input){ return ""; }
    }
    
    var XmlUtils= {
		fromJson : function(json){ return ""; },
    	toJson : function(xml){ return ""; }
    }
    

    return {
    	/** Execution context holds the stack parameters during the call */
        getExecutionContext: function() { return Map; },
        /** Standard system out */
        getSystemOutput: function() { return PrintStream; },
        /** Default DataSource assigned to this instance */
        getDatasource: function() { return DataSource; },
        /** Standard Request object */
        getRequest: function() { return Request; },
        /** Standard Response object */
        getResponse: function() { return Response; },
        /** Standard Session object */
        getSession: function() { return Session; },
        /** Current logged-in user, if any */
        getUserName: function(){return ""}
        /** Standard Initial Context of this instance */
        getInitialContext: function(){ return InitialContext; },
        /** Content Repository holding the executable artifacts */
    	getRepository: function(){ return Repository; },
    	/** Default Binary Storage */
    	getBinaryStorage: function(){ return IStorage; },
    	/** Default File Storage */
    	getFileStorage: function(){ return IStorage; },
    	/** Default Configuration Storage */
    	getConfigurationStorage: function(){ return IStorage; },
    	/** Default Mail Service assigned to this instance, if any */
    	getMailService: function(){ return IMailService; },
    	
    	
    	/** Extension Service holding the registered extensions and extension-points */
    	getExtensionService: function(){ return IExtensionService; },
    	/** Default Indexing Service for creating text based indexes */
    	getIndexingService: function(){ return IIndexingService; },
    	/** Default Connectivity Service assigned to this instance, if any */
    	getConnectivityService: function(){ return IConnectivityService; },
    	/** Passive Messaging Service enabling asynchronous execution, if required */
    	getMessagingService: function(){ return IMessagingService; },
    	/** IO utility operations */
    	getIOUtils: function(){ return IOUtils; },
    	/** HTTP utility operations */
    	getHttpUtils: function(){ return HttpUtils; },
    	/** Base64 utility operations */
    	getBase64Utils: function(){ return Base64Utils; },
    	/** HEX utility operations */
    	getHexUtils: function(){ return HexUtils; },
    	/** Digest utility operations */
    	getDigestUtils: function(){ return DigestUtils; },
    	/** URL utility operations */
    	getUrlUtils: function(){ return UrlUtils; },
    	/** Upload utility operations */
    	getUploadUtils: function(){ return UploadUtils; },
    	/** UUID utility operations */
    	getUuidUtils: function(){ return UuidUtils; },
    	/** Database utility operations */
    	getDatabaseUtils: function(){ return DatabaseUtils; },
    	/** XSS utility operations */
    	getXssUtils: function(){ return XssUtils; },
    	/** XML utility operations */
    	getXmlUtils: function(){ return XmlUtils; },
    	/** Getter for the instance parameters */
    	get: function(key){ return ""},
    	/** Setter for the instance parameters */
    	set: function(key, value){}
    };

})();
/**
 * Build the json definition with
 * npm install tern
 * git clone orion.client
 * git reset --hard origin/stable_20150817
 * replace ternWorkerCore.js
 * orion.client/bundles/org.eclipse.orion.client.javascript/web/node_modules/tern/bin/condense --name dirigible --no-spans --plugin doc_comment --def ecma5 --def browser  dirigible.js > orion.client/bundles/org.eclipse.orion.client.javascript/web/tern/defs/dirigible.json
 * mvn clean install
 * copy orion.client/build-js/codeEdit > resources
 */
