define(["Util"],function(Util){
	
	
	var Overlay = function(){
		
		var thiz = this;
		
		this.decoratedEl = null;
		this.el = document.createElement("div");
		this.el.dirigibleOverlay = this;
		this.el.classList.add("dirigible-overlay");
		this.el.draggable = true;
		this.el.innerHTML = "<span class='dirigible-overlay-label'></span>" +
							"<span class='dirigible-overlay-delete'>X</span>";
		
		this.label = this.el.querySelector(".dirigible-overlay-label");
		this.deleteButton = this.el.querySelector(".dirigible-overlay-delete");
		
		this.hide();
		
		this.el.addEventListener('mouseout', function(evt) {

			//console.log("overlay.mouseout: " + thiz.decoratedEl.nodeName)
			if (thiz.isReallyOut(evt)) {
				
				thiz.onMouseOut();
			}
		}, true);
		
		this.el.addEventListener('click', function(evt) {
			
			thiz.toggleSelection();
		}, true);
		
		
		this.deleteButton.addEventListener('click', function(evt) {
			
			thiz.remove();

		}, true);
		
		this.el.addEventListener('dragstart', function(evt) {
			
			var clone = Util.createClone(thiz.decoratedEl);
			document.body.appendChild(clone);
			
		    setTimeout(function(){
				clone.parentNode.removeChild(clone);
				thiz.hide();
 				thiz.nextSibling = thiz.decoratedEl.nextSibling;
				thiz.parentNode = thiz.decoratedEl.parentNode;
				thiz.decoratedEl.parentNode.removeChild(thiz.decoratedEl);
			},0);
			evt.dataTransfer.setDragImage(clone, evt.offsetX, evt.offsetY);
			
			evt.dataTransfer.setData("text/html", thiz.decoratedEl.outerHTML);
			evt.dataTransfer.effectAllowed = "move";
			evt.dataTransfer.dropEffect = "move";

			Overlay.currentlyDragged = thiz;
			
		}, true);
		
		this.el.addEventListener('dragend', function(evt) {
			
			var parent = thiz.parentNode;
			var nextSibling = thiz.nextSibling;
			
			
			delete thiz.parentNode;
			delete thiz.nextSibling;
			delete Overlay.currentlyDragged;
			
			Overlay.disableParents();
			
			if(thiz.decoratedEl.parentNode){
				return; // Dropped inside some parent

			} else{ // not dropped, put it back where it was
				
				if(nextSibling){
					
					parent.insertBefore(thiz.decoratedEl, nextSibling);
				} else {
					parent.appendChild(thiz.decoratedEl);
				}
			}

		}, true);
		
		
	}
	
	Overlay.prototype.stretchOver = function(el){
		
		var offset = Util.calculateOffset(el);
		this.el.style.top = offset.top + 'px';
		this.el.style.left = offset.left + 'px';
		this.el.style.width = el.offsetWidth + 'px';
		this.el.style.height = el.offsetHeight + 'px';
		
	};
	
	Overlay.prototype.decorate = function(el, widget){
	
		this.decoratedEl = el;
		this.widget = widget;
		this.label.innerText = widget.name;

		el.dirigibleOverlay = this;
		//overlay.el.dirigibleOverlay = overlay;
		
		this.stretchOver(el);
		
	};
	
	Overlay.prototype.hide = function(){
		this.el.classList.add("hidden");
	};
	
	Overlay.prototype.show = function(){
		
		this.el.classList.remove("hidden");
	};
	
	Overlay.prototype.isReallyOut = function(evt){
		
		var result = evt.offsetX < 0 || evt.offsetX >= this.el.offsetWidth || evt.offsetY < 0 || evt.offsetY >= this.el.offsetHeight;
		if(!result){
			//console.log("offsetX=" + evt.offsetX + " offsetWidth=" + this.el.offsetWidth + " offsetY=" + evt.offsetY + " offsetHeight=" + this.el.offsetHeight);
		}
		return result;
	};
	
	Overlay.prototype.toggleSelection = function(){
		
		var selected = this.el.classList.toggle("dirigible-overlay-selected");
		if(selected){
			this.decoratedEl.setAttribute("contenteditable", true);
			this.deselectOthers();
		} else {
			this.decoratedEl.removeAttribute("contenteditable");
		}
	};
	
	Overlay.prototype.isSelected = function(){
		return this.el.classList.contains("dirigible-overlay-selected");
	};
	
	Overlay.prototype.onMouseOut = function(){
		
		//console.log("overlay.onMouseOut: " + this.decoratedEl.nodeName)
		if(!this.isSelected()){
			//console.log("overlay.hide: " + this.decoratedEl.nodeName)
			this.hide();
		}
	};
	
	Overlay.prototype.deselectOthers = function(){
		
		var thiz = this;
		Overlay.forEach(function(o){

			if(o !== thiz && o.isSelected()){
				o.toggleSelection();
				o.hide();
			}
			
		})
	};
	
	Overlay.prototype.remove = function(){
		this.decoratedEl.parentNode.removeChild(this.decoratedEl);
		this.el.parentNode.removeChild(this.el);
	};

	Overlay.prototype.setParent = function(){

		this.el.classList.add("dirigible-overlay-parent");
	};
	
	//static methods
	Overlay.forEach = function(callback){
		
		var overlayEls = document.querySelectorAll(".dirigible-overlay");
		Array.prototype.forEach.apply(overlayEls,[function(overlayEl){
			
			callback(overlayEl.dirigibleOverlay);
			
		}]);
		
	};
	
	Overlay.getSelection = function(){
		
		var result = [];
		Overlay.forEach(function(o){
			if(o.isSelected()){
				result.push(o);
			}
		});
		return result;
	};
	
	Overlay.clearSelection = function(){
		
		Overlay.forEach(function(o){

			if(o.isSelected()){
				o.toggleSelection();
				o.hide();
			}
			
		})
	};
	
	Overlay.disableParents = function(){
		// disable previous parents
		var allParents = document.querySelectorAll(".dirigible-overlay-parent");
		Array.prototype.forEach.apply(allParents,[function(overlayEl){
			
			overlayEl.classList.remove("dirigible-overlay-parent");
			overlayEl.dirigibleOverlay.hide();
		}]);
	}
	
	return Overlay;
});
