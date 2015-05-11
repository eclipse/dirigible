define(function(){
	
	return {
		save : function() {

			var iframe = document.querySelector("iframe");
			var style = iframe.contentDocument.querySelector("#dirigible-wysiwyg-patches");
			var parentNode = style.parentNode
			parentNode.removeChild(style);
			
			var result = iframe.contentDocument.querySelector("html").outerHTML;
			
			parentNode.appendChild(style);
			return result;
		}		
	};
	
});
