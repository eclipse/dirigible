var FramesMessageHub = (function () {

	function FramesMessageHub(settings) {
		this.settings = settings || {};
		this.settings.hubWindow = this.settings.hubWindow || top;
		this.settings.topic = this.settings.topic || '*';
		this.settings.allowedOrigins = this.settings.allowedOrigins || [location.origin];
		this.settings.targetOrigin = this.settings.targetOrigin || '*';
	}

	FramesMessageHub.prototype.post = function(msg){
		if(msg === undefined)
			msg = {};
		if(typeof msg === 'string'){
			msg = {
				data: msg
			};
		}
		if(this.settings.topic!=='*')
			msg.topic = this.settings.topic;
		this.settings.hubWindow.postMessage(msg, this.settings.targetOrigin);
	};
	
	FramesMessageHub.prototype.subscribe = function(messageHandler){
		//TODO: use stadnard jsonschema and json schema validation
		//settings.dataSchema = settings.dataSchema;
		//if we expect messages delegate them to message handler
		if(messageHandler && (typeof messageHandler === 'function')){
			this.settings.hubWindow.addEventListener("message", function(e){
				if(this.settings.allowedOrigins.indexOf(e.origin)<0){
					console.warn('[FramesMessageHub] message blocked from non-whitelisted origin: ' + e.origin);
					return; 
				}
				if(this.settings.topic!=='*' && e.data && e.data.topic !== this.settings.topic){
					console.warn('[FramesMessageHub] message is not for this subscription topic: ' + e.data.topic);
					return; 
				}
				var message = e.data;
				messageHandler.apply(this, [message, e]);
			}.bind(this), false);	
		}
		return this;
	};
	
	return FramesMessageHub;
}());

var RpcServiceOperation = (function () {

	var RpcServiceOperation = Object.create(FramesMessageHub);

	function RpcServiceOperation(settings) {
		FramesMessageHub.call(this, settings);
		this.settings.messageHandler = settings.messageHandler;
		this.subscribe(function(msg, originalEvent){
			var returnValue = this.settings.messageHandler.apply(this, [msg, originalEvent]);
			if(returnValue){
				var client = new FramesMessageHub.call(this, {
					topic: this.settings.topic,
					targetOrigin: originalEvent.origin
				});
				client.post({
					data: returnValue,
					corellationId: msg.corellationId					
				});
			}
		}.bind(this));
	};
	
	RpcServiceOperation.prototype.constructor = RpcServiceOperation;
	
	return RpcServiceOperation;
}());

var RpcServiceClientOperation = (function () {

	var RpcServiceClientOperation = Object.create(FramesMessageHub);
	function RpcServiceClientOperation(settings) {
		FramesMessageHub.call(this, settings);
		this.corellationid = '123';
		this.subscribe(function(msg, originalEvent){
			var returnValue = this.settings.messageHandler.apply(this, [msg, originalEvent]);
			if(returnValue){
				var client = new FramesMessageHub.call(this, {
					topic: this.settings.topic,
					targetOrigin: originalEvent.origin
				});
				client.post({
					data: returnValue,
					corellationId: msg.corellationId					
				});
			}
		}.bind(this));
	}
	RpcServiceClientOperation.prototype.constructor = RpcServiceClientOperation;
	
	this.operation1 = function(){
		this.post('hey');
		this.timeoutid = window.setTimeout(function(){
			return this.response;
		}.bind(this), 20);
	}
	
	return RpcServiceOperation;
}());

/*
var xFrameRpcService = (function (svcname, svc) {
	var propNames = Object.keys(svcStub);
	var operations = {};
	propNames.forEach(function(propName){
		if(typeof svcStub[propName] === 'function')
			operations[propName] = svcStub[propName];
	})
	this.service = function(){
		var hub = new FramesMessageHub();
		var opNames = Object.keys(operations);
		opNames.forEach(function(opName){
			hub.subscribe(function(msg){
				var args = msg.arguments || [];
				operations[opName].apply(operations, args);
			})
			window.addEventListener("message", function (e) {
				if(e.origin !== location.origin){
					return; 
				}
				var message = e.data;
				if(message){
					if(message.action === opName){
						var arguments = message.arguments;
						operations[opName].apply(operations, aguments)
					}
				}
			}.bind(this), false);
		}.bind(this))
	};
	this.request = function(){}
	this.metadata = function(){
		var opNames = Object.keys(operations);
		opNames.forEach(function(opName){
			window.addEventListener("message", function (e) {
				if(e.origin !== location.origin){
					return; 
				}
				var message = e.data;
				if(message && message.topic && message.topic===svcname){
					
				}
			}.bind(this), false);
		}.bind(this))
	};
}());
var xFrameRpcClient = (function () {
	this.client = function(){
		operations.forEach(function(opName){
			operations[opName].;
		}
	}
}());
*/