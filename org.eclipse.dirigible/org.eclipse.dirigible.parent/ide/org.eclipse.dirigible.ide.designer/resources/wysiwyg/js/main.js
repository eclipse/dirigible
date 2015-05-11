require([ "Overlay", "Util", "Widget", "BoundedQueue", "Serializer", "./js/mousetrap.min.js" ], function(Overlay, Util, Widget,BoundedQueue, Serializer) {

	var iframe = document.querySelector("iframe");
	
	
	window.setEditorContent = function(content) {
	  
		iframe.src = "";
	    iframe.contentDocument.write(content)
	   
	    var style = iframe.contentDocument.createElement("style");
		style.id = "dirigible-wysiwyg-patches";
		style.innerHTML = "div.controls { min-height: 30px;} body {min-height: 700px;}";
		iframe.contentDocument.head.appendChild(style);
	}
	
	window.getEditorContent = function() {
	  
	    return Serializer.save();
	}
	
	
	var mask = document.querySelector("#mask");
	var dragoverOffset = {}
	var dragoverDisabled = false;
	var dragQueue = new BoundedQueue(4);

	mask.addEventListener('mousemove', function(evt) {

		var elQueue = findWidgetElFromEvent(evt);
		
		if (!elQueue) {
			Overlay.forEach(function(o){
				o.onMouseOut();
			});
			return;
		}

		var el = elQueue[elQueue.length-1];
		var overlay = el.dirigibleOverlay;
		overlay.show();

		// hack - hide all overlays that are not selected (sometimes they miss the mouseout event)
		Overlay.forEach(function(o){
			if(o !== overlay){
				o.onMouseOut();
			}
		});

	}, true);
	
	
	mask.addEventListener('dragover', function(evt) {
		
		evt.preventDefault();
		if(dragoverDisabled){
			return;
		}
		dragoverDisabled = true;
		setTimeout(function(){
			dragoverDisabled = false;
		},200);
		if(dragoverOffset.x == evt.pageX && dragoverOffset.y == evt.pageY){
//			console.log("skipping drag over the same coords")
			return;
		}
		
		dragoverOffset.x = evt.pageX;
		dragoverOffset.y = evt.pageY;
		
		dragQueue.push({x:evt.pageX, y: evt.pageY });
		var dragDirection = Util.calculateDragDirection(dragQueue);
		
		var elQueue = findWidgetElFromEvent(evt, true);
		if(!elQueue || elQueue.indexOf(Overlay.currentlyDragged.decoratedEl) > -1){
//			console.log("cannot compose into one's self");
			return;
		}
		
		var parentOverlay = elQueue && elQueue[elQueue.length-1] && elQueue[elQueue.length-1].dirigibleOverlay;
		if(!parentOverlay) {
			return;
		}
		
		var draggedOverSibling = elQueue && elQueue.length > 1 && elQueue[elQueue.length - 2];
		
		Overlay.disableParents();
		Overlay.clearSelection();
		
		if(!draggedOverSibling || !Widget.getWidgetFromEl(draggedOverSibling)){
			var parentEl = parentOverlay.decoratedEl;
			var index = parentEl.childNodes && Array.prototype.indexOf.apply(parentEl.childNodes, [Overlay.currentlyDragged.decoratedEl])
			if(parentEl.childNodes.length > 0 && index < 0){
				if(dragDirection == "top" || dragDirection == "left"){
					console.log(dragDirection + " compose at the beginning of parent: " + parentEl.nodeName);
					parentOverlay.decoratedEl.insertBefore(Overlay.currentlyDragged.decoratedEl, parentEl.childNodes[0]);
				}else {
					console.log("compose at the end of parent");
					parentOverlay.decoratedEl.appendChild(Overlay.currentlyDragged.decoratedEl);	
				}
				
			}else {
				console.log("skip unnecessary insertion into parent: " + parentEl.nodeName)
			}
		} else {
			
			if(dragDirection == "top" || dragDirection == "left"){
				console.log(dragDirection + " compose before sibling: " + draggedOverSibling.nodeName);
				parentOverlay.decoratedEl.insertBefore(Overlay.currentlyDragged.decoratedEl, draggedOverSibling);
				
			} else { 
				console.log(dragDirection + " compose after sibling: " + draggedOverSibling.nodeName);
				parentOverlay.decoratedEl.insertBefore(Overlay.currentlyDragged.decoratedEl, draggedOverSibling.nextSibling);
			}
		}
		parentOverlay.stretchOver(parentOverlay.decoratedEl);
		parentOverlay.setParent();
		parentOverlay.show();
		
		dirtyChanged(true);
		
	});
	
	
	setupPalette()
	
	
	
	Mousetrap.bind("del",function(e){
		var selection = Overlay.getSelection();
		selection.forEach(function(o){
			o.remove();
		});
	})
	
	Mousetrap.bind("ctrl+s",function(e){
	    e.preventDefault();
	    e.stopPropagation();
		saveCalled();
	})
	
	
	function setupPalette(){
		Array.prototype.forEach.call(
				document.querySelectorAll("div#palette ul > li"), 
				function(palettItem){
					palettItem.draggable=true;
					palettItem.addEventListener('dragstart', function(evt) {
						
						var widgetMarkup = this.querySelector(".widget-markup").innerHTML;
						
						var wrapper = iframe.contentDocument.createElement("div");
						wrapper.innerHTML = widgetMarkup;
						var el =  wrapper.children[0];
						iframe.contentDocument.body.appendChild(el);
						
						var clone = Util.createClone(el);
						iframe.contentDocument.body.appendChild(clone);
						
					    setTimeout(function(){
							clone.parentNode.removeChild(clone);
							el.parentNode.removeChild(el);
						},0);
						evt.dataTransfer.setDragImage(clone, evt.offsetX, evt.offsetY);
						
						evt.dataTransfer.effectAllowed = "move";
						evt.dataTransfer.dropEffect = "move";

						
						var widget = Widget.getWidgetFromEl(el);
						var overlay = new Overlay();
						mask.appendChild(overlay.el);
						
						overlay.decorate(el, widget);
						
						Overlay.currentlyDragged = overlay;
						
						Overlay.disableParents();
						Overlay.clearSelection();
					}, true);
				}
		);
	}

	
	function findWidgetElFromEvent(evt, parentsOnly){

		var offset = Util.calculateEventOffset(evt, mask);
		var el = iframe.contentDocument.elementFromPoint(offset.left, offset.top); 
		
		var elQueue = [];
		var widget = crawlUp(el,parentsOnly, elQueue);
		
		if(widget){
			
			var lastEl = elQueue[elQueue.length-1];
			var overlay = lastEl.dirigibleOverlay || new Overlay();
			mask.appendChild(overlay.el);
			
			overlay.decorate(lastEl, widget);
			return elQueue;
		} else {
			return null;
		}
	}
	
	function crawlUp(el,parentsOnly, elQueue){
		
		if(!el || !el.matches){
			return null;
		}
		elQueue.push(el);
		
		var widget = Widget.getWidgetFromEl(el);
		if(!widget){
			return crawlUp(el.parentNode, parentsOnly, elQueue);
		}
		
		if(!parentsOnly){
			return widget;
		}
		
		if(widget.accepts(Overlay.currentlyDragged.decoratedEl)){
			return widget;
		} else {
			return crawlUp(el.parentNode, parentsOnly, elQueue);
		}
	}
	
	function isDraggedOverLeftHandSide(el, evt){
		
		var offset = Util.calculateEventOffset(evt, mask);
		return offset.left - el.offsetLeft < el.offsetWidth/2;
	}
	
	function isDraggedOverTopSide(el, evt){
		
		var offset = Util.calculateEventOffset(evt, mask);
		return offset.top - el.offsetTop < el.offsetHeight/2;
	}
	
});
