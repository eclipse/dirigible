/*******************************************************************************
 * @license
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*eslint-env amd*/
/*globals Tautologistics */
define([
	'orion/Deferred',
	'orion/objects',
	'javascript/lru',
	'csslint',
	'orion/metrics'
], function(Deferred, Objects, LRU, CSSLint, Metrics) {

	/**
	 * Provides a shared AST.
	 * @class Provides a shared parsed AST.
	 * @since 8.0
	 */
	function CssResultManager() {
		this.cache = new LRU.LRU(10);
	}
	
	Objects.mixin(CssResultManager.prototype, /** @lends webtools.CssResultManager.prototype */ {
		/**
		 * @param {orion.editor.EditorContext} editorContext
		 * @returns {orion.Promise} A promise resolving to the CSS parse / checking result or null if called 
		 * with an incomplete config
		 */
		getResult: function(editorContext, config) {
		    if(typeof(config) === 'undefined') {
		        config = Object.create(null);
		    }
		    if(typeof(config.getRuleSet) === 'undefined') {
		        config.getRuleSet = function() {return null;};
			}
			var _self = this;
			return editorContext.getFileMetadata().then(function(metadata) {
				metadata = metadata || {};
				var loc = _self._getKey(metadata);
				var result = _self.cache.get(loc);
				if (result) {
					return new Deferred().resolve(result);
				}
				return editorContext.getText().then(function(text) {
				    var start = Date.now();
					result = CSSLint.verify(text, config.getRuleSet());
					var end = Date.now() - start;
					Metrics.logTiming('language tools', 'parse', end, 'text/css');
					_self.cache.put(loc, result);
					if(metadata.location) {
					    //only set this if the original metadata has a real location
					    result.fileLocation = metadata.location;
					}
					return result;
				});
			});
		},
		/**
		 * Returns the key to use when caching
		 * @param {Object} metadata The file infos 
		 */
		_getKey: function _getKey(metadata) {
		      if(!metadata.location) {
		          return 'unknown';
		      }    
		      return metadata.location;
		},
		
		/**
		 * Callback from the orion.edit.model service
		 * @param {Object} event An <tt>orion.edit.model</tt> event.
		 * @see https://wiki.eclipse.org/Orion/Documentation/Developer_Guide/Plugging_into_the_editor#orion.edit.model
		 */
		onModelChanging: function(event) {
		    if(this.inputChanged) {
		        //TODO haxxor, eat the first model changing event which immediately follows
		        //input changed
		        this.inputChanged = null;
		    } else {
		        this.cache.remove(this._getKey(event.file));
		    }
		},
		/**
		 * Callback from the orion.edit.model service
		 * @param {Object} event An <tt>orion.edit.model</tt> event.
		 * @see https://wiki.eclipse.org/Orion/Documentation/Developer_Guide/Plugging_into_the_editor#orion.edit.model
		 */
		onInputChanged: function(event) {
		    this.inputChanged = event;
		    //TODO will add to mult-env
		}
	});
	return {
		CssResultManager : CssResultManager,
	};
});
