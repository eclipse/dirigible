define(["Util"],function(Util){
	
	
	var Widget =  function(props){
		
		for(var key in props){
			if(props.hasOwnProperty(key)){
				
				this[key]=props[key];
			}
		}
	}
	
	Widget.prototype.accepts = function(childEl){
		
		if(!this.children){
			return false;
		}
		
		for(var i = 0; i < this.children.length; i++){
			if( childEl.matches(this.children[i]) ){
				return true;
			}
		}
		return false;
	};
	
	Widget.widgets = [
		       new Widget({name: "Primary Button / Bootstrap" , selector: "button.btn.btn-primary"}),
		       new Widget({name: "Danger Button / Bootstrap" , selector: "button.btn.btn-danger"}),
		       new Widget({name: "Succes Button / Bootstrap" , selector: "button.btn.btn-success"}),
		       new Widget({name: "Button / Bootstrap" , selector: "button.btn"}),
		       new Widget({name: "Checkbox / Bootstrap" , selector: "label.checkbox" }),
		       new Widget({name: "Radio / Bootstrap" , selector: "label.radio" }),
		       new Widget({name: "Control group / Bootstrap" , selector: "div.control-group", children: ["div.controls", "label.control-label"] }),
		       new Widget( {name: "Controls / Bootstrap" , selector: "div.controls", children: ["button.btn", "label.checkbox", "label.radio", "select"]}),
			   new Widget({name: "Form / Bootstrap" , selector: "form", children:["fieldset", "div.control-group","p"]}),
			   new Widget({name: "Fieldset / Bootstrap" , selector: "fieldset", children:["div.control-group","p"]}),
			   new Widget({name: "Label / Bootstrap" , selector: "label.control-label"}),
		       
			   
    //plain html tags must come after the more specific bootstrap selectors
			   
			   new Widget({name: "INPUT" , selector: "INPUT"}),
			   new Widget({name: "TABLE" , selector: "TABLE"}),
		       new Widget(  {name: "P" , selector: "p"}),
		       new Widget(  {name: "Select" , selector: "select"}),
		       new Widget(  {name: "Text area" , selector: "textarea"}),
		       /*	   new Widget({name: "BUTTON", selector: "button", leaf: true}),
		       new Widget({name: "H1" , selector: "h1",leaf: true}),
		       new Widget({name: "H2" , selector: "h2", leaf: true}),
		       new Widget({name: "H3" , selector: "h3", leaf: true}),
		       new Widget({name: "H4" , selector: "h4", leaf: true}),
		       
               new Widget({name: "DIV" , selector: "DIV"}),  
               */
			   
                new Widget({name: "BODY" , selector: "body", children:["form", "table"]})
		                
	];
		
	//static methods
	Widget.getWidgetFromEl = function(el){
		for(var i = 0; i<Widget.widgets.length; i++){
			if(el.matches(Widget.widgets[i].selector)){
				return Widget.widgets[i];
			}
		}
		return null;
	};
	
	Widget.findBySelector = function(selector){
		for(var i = 0; i<Widget.widgets.length; i++){
			if( Widget.widgets[i].selector == selector){
				return Widget.widgets[i];
			}
		}
		return null;
	};
	
	return Widget;
});