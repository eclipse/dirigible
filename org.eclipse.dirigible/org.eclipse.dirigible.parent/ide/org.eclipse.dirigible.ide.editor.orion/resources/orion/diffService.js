/*******************************************************************************
 * @license
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*eslint-env browser, amd*/

define([
	'orion/extensionCommands', //$NON-NLS-0$
	'orion/compare/diffParser', //$NON-NLS-0$
	'orion/edit/editorContext' //$NON-NLS-0$
], function(mExtensionCommands, mDiffParser, EditorContext) {

	function DiffService(serviceRegistry, inputManager, editor) {
		this._serviceRegistry = serviceRegistry;
		this._inputManager = inputManager;
		this._editor = editor;
		this._enabled = false;
		this.init();
	}

	DiffService.prototype = {
		init: function(){
			var self = this;
			this._changeListener = function() {
				if (self._enabled) {
					if (self.occurrenceTimer) {
						window.clearTimeout(self.occurrenceTimer);
					}
					self.occurrenceTimer = window.setTimeout(function() {
						self.occurrenceTimer = null;
						self.doDiff();
					}, 500);
				}
			};
			this._inputManager.addEventListener("InputChanged", function() { //$NON-NLS-0$
				var service = self.service = self.getDiffServices();
				if (service) {
					var textView = self._editor.getTextView();
					if (textView) {
						textView.removeEventListener("ModelChanged", self._changeListener); //$NON-NLS-0$
						textView.addEventListener("ModelChanged", self._changeListener); //$NON-NLS-0$
					}
				}
			});
		},

		getDiffServices: function(){
			var metadata = this._inputManager.getFileMetadata();
			var diffServices = this._serviceRegistry.getServiceReferences("orion.edit.diff"); //$NON-NLS-0$
			for (var i = 0; i < diffServices.length; i++) {
				var serviceReference = diffServices[i];
				var info = {};
				info.validationProperties = serviceReference.getProperty("validationProperties"); //$NON-NLS-0$
				info.forceSingleItem = true;
				var validator = mExtensionCommands._makeValidator(info, this._serviceRegistry);
				if (validator.validationFunction.bind(validator)(metadata)) {
					return this._serviceRegistry.getService(serviceReference);
				}
			}
			return null;
		},

		showAnnotations: function(service){
			var self = this;
			var diffParser = new mDiffParser.DiffParser();
			var context = {
				metadata: this._inputManager.getFileMetadata()
			};
			service.computeDiff(EditorContext.getEditorContext(this._serviceRegistry), context).then(function(diffContent) {
				diffParser.setLineDelim("\n"); //$NON-NLS-0$
				diffParser.parse("", diffContent);
				var oBlocks = diffParser.getOriginalBlocks();
				var nBlocks = diffParser.getNewBlocks();
				var diffArray = diffParser.getDiffArray();
				var diffRanges = self.getDiffRanges(oBlocks, nBlocks, diffArray);
				if(self._enabled){//for cases when user turns off diffService computing diff
					self._editor.showDiffAnnotations(diffRanges);
				}
			});
		},

		_getOriginalText: function(originalHunkBlock, diffArray, diffArraySubstrIndex, lineDelim) {
			var oldSize = originalHunkBlock[1];
			var oldStartInDiffArra = originalHunkBlock[2];
			var oText = "";
			if(oldSize > 0) {
				for(var i = 0; i < oldSize; i++) {
					var lineText = diffArray[oldStartInDiffArra + i];
					if(lineText) {
						oText = oText + lineText.substring(diffArraySubstrIndex) + lineDelim;
					} else {
						oText = oText + lineDelim;
					}
				}
			}
			return oText;
		},
		
		getDiffRanges: function(originalHunkBlocks, newHunkBlocks, diffArray){
			var diffResult = [];
			for (var i = 0; i < newHunkBlocks.length; i++) {
				var lineStart = newHunkBlocks[i][0];
				var oldSize = originalHunkBlocks[i][1];
				var newSize = newHunkBlocks[i][1];
				if (newSize === 0) {
					diffResult.push({
						lineStart: lineStart - 1,
						lineEnd: lineStart,
						type:"deleted" //$NON-NLS-0$
					});
				} else if (oldSize === 0) {
					diffResult.push({
						lineStart: lineStart - 1,
						lineEnd: lineStart + newSize - 1,
						type:"added" //$NON-NLS-0$
					});
				} else {
					//this._getOriginalText(originalHunkBlocks[i], diffArray.array, diffArray.index, "\n");
					diffResult.push({
						lineStart: lineStart - 1,
						lineEnd: lineStart + newSize - 1,
						type:"modified" //$NON-NLS-0$
					});
				}
			}
			return diffResult;
		},

		toggleEnabled: function(){
			this.setEnabled(!this.isEnabled());
		},

		setEnabled: function(state){
			this._enabled = state;
			this.doDiff();
		},

		isEnabled: function(){
			return this._enabled;
		},

		doDiff: function() {
			if (!this._enabled) {
				this._editor.showDiffAnnotations([]);
			} else {
				if (this.service) {
					var self = this;
					this._inputManager.save().then(function() {
						self.showAnnotations(self.service);
					});
				}
			}
		}

	};

	DiffService.prototype.constructor = DiffService;

	//return the module exports
	return {DiffService: DiffService};
});
