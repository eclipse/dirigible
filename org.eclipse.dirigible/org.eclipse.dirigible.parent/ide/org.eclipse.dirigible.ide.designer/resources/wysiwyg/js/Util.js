define(function(){
	
	return {
		calculateOffset : function(element) {

			var offset = {
				top : element.offsetTop,
				left : element.offsetLeft
			};
			while (element.offsetParent) {
				element = element.offsetParent;
				offset.top += element.offsetTop;
				offset.left += element.offsetLeft;
			}
			return offset;
		},
		createClone: function(el){
			var clone = el.cloneNode(true);
		    
		    clone.style.position = "absolute";
		    clone.style.left = "9999999px";
		    
		    clone.style.width = el.offsetWidth + 'px';
			clone.style.height = el.offsetHeight + 'px';
			return clone;
		},
		indexOfNode: function (parentEl,childEl){
			
			return Array.prototype.indexOf.call(parentEl.childNodes, childEl);
		},
		calculateEventOffset: function(evt, el){
			
			var left = evt.pageX - el.offsetLeft; 
			var top = evt.pageY - el.offsetTop; 
			return {
				left: left,
				top: top
			};
		},

		calculateDragDirection: function(q){
			var dragVector =  {
					x: q.tail().x - q.head().x,
					y: q.tail().y - q.head().y
			};
			
			if(Math.abs(dragVector.x) > Math.abs(dragVector.y)){ // horizontal drag
				
				return (dragVector.x >= 0) ? "left":"right";
	
			} else { // vertical drag
				return (dragVector.y >= 0) ? "top":"bottom";
			}
		}
		
	};
	
});