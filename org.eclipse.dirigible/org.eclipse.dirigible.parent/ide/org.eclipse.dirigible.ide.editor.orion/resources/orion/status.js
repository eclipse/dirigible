/*******************************************************************************
 * @license
 * Copyright (c) 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 *
 * Contributors: IBM Corporation - _initial API and implementation
 *******************************************************************************/
/*eslint-env browser, amd*/
define([
	'i18n!orion/nls/messages',
	'orion/webui/littlelib',
	'orion/globalCommands',
	'marked/marked',
], function(messages, lib, mGlobalCommands, marked) {
	var SEV_ERROR = "Error", SEV_WARNING = "Warning", SEV_INFO = "Info", SEV_OK = "Ok"; //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-0$

	// this is a cheat, all dom ids should be passed in
	var closeButtonDomId = "closeNotifications"; //$NON-NLS-0$

	/**
	 * Service for reporting status
	 * @class Service for reporting status
	 * @name orion.status.StatusReportingService
	 * @param {orion.serviceregistry.ServiceRegistry} serviceRegistry
	 * @param {orion.operationclient.OperationsClient} operationsClient
	 * @param {String} domId ID of the DOM node under which status will be displayed.
	 * @param {String} progressDomId ID of the DOM node used to display progress messages.
	 */
	function StatusReportingService(serviceRegistry, operationsClient, domId, progressDomId, notificationContainerDomId) {
		this._serviceRegistry = serviceRegistry;
		this._serviceRegistration = serviceRegistry.registerService("orion.page.message", this); //$NON-NLS-0$
		this._operationsClient = operationsClient;
		this.notificationContainerDomId = notificationContainerDomId;
		this.domId = domId;
		this.progressDomId = progressDomId || domId;
		this._hookedClose = false;
		this._timer = null;
		this._clickToDisMiss = true;
		this._cancelMsg = null;
		this.statusMessage = this.progressMessage = null;
	}
 
	StatusReportingService.prototype = /** @lends orion.status.StatusReportingService.prototype */ {
	
		_init: function() {
			var closeButton = lib.node(closeButtonDomId); //$NON-NLS-0$
			if (closeButton && !this._hookedClose) {
				closeButton.style.cursor = "pointer"; //$NON-NLS-0$
				this._hookedClose = true;
				var container = lib.node(this.notificationContainerDomId);
				lib.addAutoDismiss([container],  function(){
					if(this._clickToDisMiss) {
						this.close();
					}
				}.bind(this));
				closeButton.addEventListener("click", function() { //$NON-NLS-0$
					this.close();
				}.bind(this));
				container.addEventListener("click", function(evt) { //$NON-NLS-0$
					if(evt && evt.target && evt.target.nodeName.toLowerCase() === "a") { //$NON-NLS-0$)
						var anchors = lib.$$("a", container);//$NON-NLS-0$) //lib.container.getElementsByTagName("a");
						if(anchors && anchors.length && anchors.length === 1) {// We should only auto dismiss the status bar if there is only one "a" element in the bar.
							this.close();
						}
					}
				}.bind(this));
			}
		},
		
		close: function(){
			window.clearTimeout(this._timer);
			var closeButton = lib.node(closeButtonDomId); //$NON-NLS-0$
			if(this._cancelMsg && this._cancelFunc && closeButton) {
				closeButton.innerHTML = "";
				closeButton.classList.remove("cancelButton"); //$NON-NLS-0$
				closeButton.classList.add("dismissButton"); //$NON-NLS-0$
				closeButton.classList.add("core-sprite-close"); //$NON-NLS-0$
				closeButton.classList.add("imageSprite"); //$NON-NLS-0$
				this._cancelFunc();
				this._cancelMsg = null;
			} else {
				this.setProgressMessage(""); 
			}
		},
		
		_getNotifierElements: function() {
			if (!this._notifierElements) {
				this._notifierElements = mGlobalCommands.getToolbarElements(this.notificationContainerDomId);
			}
			return this._notifierElements;
		},
		/**
		 * Displays a status message to the user.
		 * @param {String} msg Message to display.
		 * @param {Number} [timeout] Time to display the message before hiding it.
		 * @param {Boolean} [isAccessible] If <code>true</code>, a screen reader will read this message.
		 * Otherwise defaults to the domNode default.
		 */
		setMessage : function(msg, timeout, isAccessible) {
			this._init();
			this.statusMessage = msg;
			var node = lib.node(this.domId);
			if(typeof(isAccessible) === "boolean") { //$NON-NLS-0$
				// this is kind of a hack; when there is good screen reader support for aria-busy,
				// this should be done by toggling that instead
				var readSetting = node.getAttribute("aria-live"); //$NON-NLS-0$
				node.setAttribute("aria-live", isAccessible ? "polite" : "off"); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-0$
				window.setTimeout(function() {
					if (msg === this.statusMessage) {
						lib.empty(node);
						node.appendChild(document.createTextNode(msg));
						window.setTimeout(function() { node.setAttribute("aria-live", readSetting); }, 100); //$NON-NLS-0$
					}
				}.bind(this), 100);
			}
			else { 
				lib.empty(node);
				node.appendChild(document.createTextNode(msg));
			}
			if (typeof(timeout) === "number") { //$NON-NLS-0$
				window.setTimeout(function() {
					if (msg === this.statusMessage) {
						lib.empty(node);
						node.appendChild(document.createTextNode("")); //$NON-NLS-0$
					}
				}.bind(this), timeout);
			}
		},
	
		/**
		 * Displays an error message to the user.
		 *
		 * @param {String|orionError} st The error to display. Can be a simple String,
		 * or an error object from a  XHR error callback, or the body of an error response 
		 * from the Orion server.
		 */
		setErrorMessage : function(st) {
			this._clickToDisMiss = true;
			this.statusMessage = st;
			this._init();
			//could be: responseText from xhrGet, deferred error object, or plain string
			var status = st.responseText || st.message || st;
			//accept either a string or a JSON representation of an IStatus
			if (typeof status === "string") {
				try {
					status = JSON.parse(status);
				} catch(error) {
					//it is not JSON, just continue;
				}
			}
			var message = status.Message || status;
			var color = "red"; //$NON-NLS-0$
			if (status.Severity) {
				switch (status.Severity) {
				case SEV_WARNING: //$NON-NLS-0$
					color = "#FFCC00"; //$NON-NLS-0$
					break;
				case SEV_ERROR: //$NON-NLS-0$
					color = "red"; //$NON-NLS-0$
					break;
				case SEV_INFO: //$NON-NLS-0$
				case SEV_OK: //$NON-NLS-0$
					color = "green"; //$NON-NLS-0$
					break;
				}
			}
			var span = document.createElement("span");  //$NON-NLS-0$
			span.style.color = color;
			span.appendChild(document.createTextNode(message));
			var node = lib.node(this.domId);
			lib.empty(node);
			node.appendChild(span);
		},
		
		/**
		 * Set a message that will be shown in the progress reporting area on the page.
		 * @param {String} message The progress message to display. 
		 */
		setProgressMessage : function(message) {
			this._clickToDisMiss = false;
			this._init();
			this.progressMessage = message;
			
			var node = lib.node(this.progressDomId);
			lib.empty(node);
			node.appendChild(document.createTextNode(message));

			var container = lib.node(this.notificationContainerDomId);
			container.classList.remove("notificationHide"); //$NON-NLS-0$
			if (message && message.length > 0) {
				container.classList.add("progressNormal"); //$NON-NLS-0$
				container.classList.add("notificationShow"); //$NON-NLS-0$
			} else if(this._progressMonitors && this._progressMonitors.length > 0){
				return this._renderOngoingMonitors();
			}else{
				container.classList.remove("notificationShow"); //$NON-NLS-0$
				container.classList.add("notificationHide"); //$NON-NLS-0$
			}
		},
		
		/**
		 * Set a callback function for a cancellation, which can be triggered by the close button
		 * @param {Function} cancelFunc The callback back function for the cancellation. 
		 */
		setCancelFunction: function(cancelFunc) {
			this._cancelFunc = cancelFunc;
		},
		
		/**
		 * Set a message that indicates that a long-running (progress) operation is complete.
		 * @param {String|orionError} message The error to display. Can be a simple String,
		 * or an error object from a XHR error callback, or the body of an error response 
		 * from the Orion server.
		 */
		setProgressResult : function(message, cancelMsg) {
			this._clickToDisMiss = false;
			this._cancelMsg = cancelMsg;
			this.progressMessage = message;
			//could either be responseText from xhrGet or just a string
			var status = message.responseText || message;
			//accept either a string or a JSON representation of an IStatus
			if (typeof status === "string") {
				try {
					status = JSON.parse(status);
				} catch(error) {
					//it is not JSON, just continue;
				}
			}
			this._init();

			// Create the message
			var msg = status.Message || status.toString();
			if (msg === Object.prototype.toString()) {
				// Last ditch effort to prevent user from seeing meaningless "[object Object]" message
				msg = messages.UnknownError;
			}
			var node = lib.node(this.progressDomId);
			lib.empty(node);
			node.appendChild(this.createMessage(status, msg));

			// Given the severity, add/remove the appropriate classes from the notificationContainerDomId
			var extraClass = "progressNormal"; //$NON-NLS-0$
			var removedClasses = [];
			if (status.Severity) {
				switch (status.Severity) {
				case SEV_WARNING: //$NON-NLS-0$
					extraClass="progressWarning"; //$NON-NLS-0$
					removedClasses.push("progressInfo");
					removedClasses.push("progressError");
					removedClasses.push("progressNormal"); //$NON-NLS-0$
					this._clickToDisMiss = true;
					break;
				case SEV_ERROR: //$NON-NLS-0$
					extraClass="progressError"; //$NON-NLS-0$
					removedClasses.push("progressWarning");
					removedClasses.push("progressInfo");
					removedClasses.push("progressNormal"); //$NON-NLS-0$
					this._clickToDisMiss = true;
					break;
				default:
					extraClass="progressNormal"; //$NON-NLS-0$
					removedClasses.push("progressWarning");
					removedClasses.push("progressError");
					removedClasses.push("progressNormal");
				}
			}
			removedClasses.push("notificationHide");
			var container = lib.node(this.notificationContainerDomId);
			if (extraClass && this.progressDomId !== this.domId) {
				container.classList.add(extraClass);
				for (var i=0; i<removedClasses.length; i++) {
					container.classList.remove(removedClasses[i]);
				}
			}
			container.classList.add("notificationShow"); //$NON-NLS-0$

			// Set up the close button
			var closeButton = lib.node(closeButtonDomId); //$NON-NLS-0$
			if(closeButton){
				if(this._cancelMsg) {
					closeButton.classList.add("cancelButton"); //$NON-NLS-0$
					closeButton.classList.remove("dismissButton"); //$NON-NLS-0$
					closeButton.classList.remove("core-sprite-close"); //$NON-NLS-0$
					closeButton.classList.remove("imageSprite"); //$NON-NLS-0$
					closeButton.innerHTML = this._cancelMsg;
				} else {
					closeButton.innerHTML = "";
					closeButton.classList.remove("cancelButton"); //$NON-NLS-0$
					closeButton.classList.add("dismissButton"); //$NON-NLS-0$
					closeButton.classList.add("core-sprite-close"); //$NON-NLS-0$
					closeButton.classList.add("imageSprite"); //$NON-NLS-0$
				}
			}
		},

		/**
		 * @private
		 * @returns {Element}
		 */
		createMessage: function(status, msg) {
			if (status.HTML) {
				// msg is HTML to be inserted directly
				var span = document.createElement("span"); //$NON-NLS-0$
				span.innerHTML = msg;
				return span;
			}
			// Check for Markdown
			var markdownSource;
			if (status.type === "markdown") { //$NON-NLS-0$
				markdownSource = status.content || msg;
			} else {
				// Attempt to parse the msg field as Markdown
				// TODO this is deprecated - should be removed in favor of explicit `type`
				markdownSource = msg;
			}

			var html= null;
			try {
				html = marked(markdownSource, {
					sanitize: true,
				});
			} catch (e) {
			}

			var msgNode, links = [];
			if (html) {
				msgNode = document.createElement("div"); //$NON-NLS-0$
				msgNode.innerHTML = html;
				// All status links open in new window
				links = lib.$$("a", msgNode); //$NON-NLS-0$
				Array.prototype.forEach.call(links, function(link) { //$NON-NLS-0$
					link.target = "_blank"; //$NON-NLS-0$
				});
			} else {
				// Treat as plain text
				msgNode = document.createTextNode(msg);
			}
			if (!links.length && status.Severity !== SEV_WARNING && status.Severity !== SEV_ERROR && !this._cancelMsg) {
				// Message has no links in it, and is not a Warning or Error severity. Therefore we consider
				// it unimportant and will auto hide it in 5 seconds.
				if(this._timer){
					window.clearTimeout(this._timer);
				}
				this._timer = window.setTimeout(function(){
					this.setProgressMessage("");
					this._timer = null;
				}.bind(this), 5000);
			}
			return msgNode;
		},

		/**
		 * Shows a progress message in the progress area until the given deferred is resolved.
		 */
		showWhile: function(deferred, message) {
			var that = this;
			if(message){
				that.setProgressMessage(message);
				var finish = function(){
					if(message === that.progressMessage){
						that.setProgressMessage(""); //$NON-NLS-0$
					}
				};
				deferred.then(finish, finish);
			}
			return deferred;
		},
		
		_lastProgressId: 0,
		_progressMonitors: {length: 0},
		
		/**
		 * Creates a ProgressMonitor that will be displayed on the status area.
		 * @param {Deferred} deferred [optional] that updates this monitor
		 * @param {String} message [optional] messaged to be shown until deferred is not resolved
		 * @returns {ProgressMonitor}
		 */
		createProgressMonitor: function(deferred, message){
			return new ProgressMonitor(this, ++this._lastProgressId, deferred, message);
		},
		
		_renderOngoingMonitors: function(){
			if(this._progressMonitors.length > 0){
				var msg = "";
				var isFirst = true;
				for(var progressMonitorId in this._progressMonitors){
					if(this._progressMonitors[progressMonitorId].status){
						if(!isFirst)
							msg+=", "; //$NON-NLS-0$
						msg+=this._progressMonitors[progressMonitorId].status;
						isFirst = false;
					}
				}
				this.setProgressMessage(msg);
			} else {
				this.setProgressMessage("");
			}
		},
		
		_beginProgressMonitor: function(monitor){
			this._progressMonitors[monitor.progressId] = monitor;
			this._progressMonitors.length++;
			this._renderOngoingMonitors();
		},
		
		_workedProgressMonitor: function(monitor){
			this._progressMonitors[monitor.progressId] = monitor;
			this._renderOngoingMonitors();
		},
		
		_doneProgressMonitor: function(monitor){
			delete this._progressMonitors[monitor.progressId];
			this._progressMonitors.length--;
			if(monitor.status){
				this.setProgressResult(monitor.status);
			}else{				
				this._renderOngoingMonitors();
			}
		}
		
	};
	StatusReportingService.prototype.constructor = StatusReportingService;
	
	function ProgressMonitor(statusService, progressId, deferred, message){
		this.statusService = statusService;
		this.progressId = progressId;
		if(deferred){
			this.deferred = deferred;
			this.begin(message);
			var that = this;
			deferred.then(
					function(/*response, secondArg*/){
						that.done.bind(that)();
					},
					function(/*error, secondArg*/){
						that.done.bind(that)();
					});
		}
	}
	
	/**
	 * Starts the progress monitor. Message will be shown in the status area.
	 * @param {String} message
	 */
	ProgressMonitor.prototype.begin = function(message){
				this.status = message;
				this.statusService._beginProgressMonitor(this);
			};
	/**
	 * Sets the progress monitor as done. If no status is provided the message will be
	 * removed from the status.
	 * @param {String|orionError} status [optional] The error to display. Can be a simple String,
	 * or an error object from a XHR error callback, or the body of an error response 
	 * from the Orion server.
	 */
	ProgressMonitor.prototype.done = function(status){
				this.status = status;
				this.statusService._doneProgressMonitor(this);
			};
	/**
	 * Changes the message in the monitor.
	 * @param {String} message
	 */
	ProgressMonitor.prototype.worked = function(message){
				this.status = message;
				this.statusService._workedProgressMonitor(this);
			};
	
	ProgressMonitor.prototype.constructor = ProgressMonitor;

	//return module exports
	return {StatusReportingService: StatusReportingService,
			ProgressMonitor: ProgressMonitor};
});
