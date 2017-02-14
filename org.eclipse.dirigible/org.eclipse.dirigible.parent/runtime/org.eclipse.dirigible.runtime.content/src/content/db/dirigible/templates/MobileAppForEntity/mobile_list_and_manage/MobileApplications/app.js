var PAGE_MARGIN = 16;

var entities = [];
var service = "${serviceFileName}";
var primaryKey;
#foreach ($tableColumn in $tableColumns) 
#if ($tableColumn.isKey())
primaryKey = "${tableColumn.getName()}";
#end 
#end

function makeRequest(url, method, callback, body){
	var request = new tabris.XMLHttpRequest();
	request.onreadystatechange = function(){
		if(request.readyState === request.DONE){
			callback(JSON.parse(request.responseText));
		}
	}
	request.open(method, url);
	if(body !== null){
		request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		request.send(JSON.stringify(body));
	}else{
		request.send();
	}
};

makeRequest(service, "GET", function(response){
	 entities = response;
	  	
	 var maxElements = Object.keys(entities[0]).length;
	
	 function getItems(){
	  return new tabris.CollectionView({
	    layoutData: {left: 0, right: 0, top: 0, bottom: 0},
	    itemHeight: 90,
	    items: entities,
	    refreshEnabled: true,
	    initializeCell: function(cell) {
	     var container = new tabris.Composite({
	        background: 'white',
	        layoutData: {left: 0, top: 0, bottom: 0, right: 0}
	      }).on('pan:horizontal', function(container, event) {
	        this.set("transform", {translationX:event.translation.x});
	        if (event.state === "end") {
	            if (Math.abs(event.translation.x) > 100) {
	                this.animate({transform:{translationX:(event.translation.x<0 ? -1 : 1)*1000}},{duration:1000});
	                var confirmationComposite = new tabris.Composite({
	                    layoutData: {centerY:0, centerX:0}
	                }).appendTo(this.parent());
	                var removeButton = new tabris.Button({
	                    layoutData: {centerY:0, left:confirmationComposite.children().last()},
	                    text: "Remove"
	                }).on("select", function() {
						var entity = cell.get("item");
						var url = service + "?"+primaryKey+"="+ entity[primaryKey];
						makeRequest(url, "DELETE", function(){
							cell.parent().remove(cell.get("itemIndex"));
						});
	                }).appendTo(confirmationComposite);
	                new tabris.Button({
	                    layoutData: {centerY:0, left:confirmationComposite.children().last()},
	                    text: "Undo"
	                }).on("select", function() {
	                    container.animate({transform:{translationX:0}},{duration:500});
	                  	this.dispose();
	                  	removeButton.dispose();
	                }).appendTo(confirmationComposite);
	            } else {
	                this.animate({transform:{translationX:0}},{duration:1000});
	            }
	        }
	      }).appendTo(cell);
	      for(var i = 0; i<entities.length; i++){
	        var textViewMain = new tabris.TextView({
	          layoutData: {left: "80%", right: PAGE_MARGIN, top: PAGE_MARGIN}
	        }).appendTo(container);
	        var textViewSecondary = new tabris.TextView({
	          id: "textViewSecond",
	          font: "bold 18px",
	          layoutData: {left: 30, right: PAGE_MARGIN, top: PAGE_MARGIN},
	        }).appendTo(container);
	        var textViewThird = new tabris.TextView({
	           layoutData: {left: 30, right: PAGE_MARGIN, top: ["#textViewSecond", 2*PAGE_MARGIN]}
	        }).appendTo(container);
	      }
	      cell.on("change:item", function(widget, entity){
	        for(var i = 0; i<maxElements; i++){
	          var mainValue = entity[primaryKey];
	          textViewMain.set("text", mainValue + " >");
	          
	          var secondaryKey = Object.keys(entity)[1];
	          var secondaryValue = entity[secondaryKey];
	          textViewSecondary.set("text", secondaryValue);
	          
	          var thirdKey = Object.keys(entity)[2];
	          var thirdValue = entity[thirdKey];
	          textViewThird.set("text", thirdValue);
	        }
	      });
	    }
	  }).on("select", function(target, value){
	    createEditPage(value).open();
	  }).on("refresh", function(view){
	  	makeRequest(service, "GET", function(response){
	  		entities = response;
 			 view.set({
			      items: entities,
			      refreshIndicator: false,
			      refreshMessage: ""
		    });
	  	});
	  });
	}
	
	function createEditPage(entity){
	  var page = new tabris.Page({
	    title: "Editing " + entity[primaryKey]
	  });
	  
	  var scrollView = new tabris.ScrollView({left: 0, top: 0, right: 0, bottom: 0}).appendTo(page);
	  
	  for(var i = 0; i<maxElements; i++){
	    var keyName = Object.keys(entity)[i];
	    new tabris.TextView({
	      id:"inputLabel"+i,
	      text: keyName,
	      layoutData: {left: 20, top : ["#inputLabel"+(i-1), 28]}
	    }).appendTo(scrollView);
	    new tabris.TextInput({
	      id: "input"+i,
	      text: entity[keyName],
	      layoutData: {left: ["#inputLabel"+i, 50], top : ["#input"+(i-1), 18]},
	      centerX: 40,
	      width: 200
	    }).appendTo(scrollView);	
	  }
	  new tabris.Button({
	    text: "Save",
	    textColor: "white",
	    background: "green",
	    layoutData : {top:["#input"+(maxElements-1), 20]},
	    width: 240,
	    centerX: 0
	  }).on("select", function(){
	    for(var i = 0; i<maxElements; i++){
	      var key = scrollView.find("#inputLabel"+i).get("text");
	      var value = scrollView.find("#input"+i).get("text");
	      entity[key]=value;
    	}
	    makeRequest(service, "PUT", function(){
	    	mainPage.open();
	    }, entity);
	  }).appendTo(scrollView);
	  return page;
	}
	
	function createItemsPage(title){
	  return new tabris.Page({
	    title: title,
	    topLevel: true
	  }).on("appear", function(){
	    this.children().dispose();
	    this.append(getItems());
	  })
	};
	
	var mainPage = createItemsPage("${pageTitle}");
	mainPage.open();
});