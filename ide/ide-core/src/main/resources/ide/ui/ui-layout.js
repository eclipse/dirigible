var ViewRegistry = function() {
	
	var _views = {};
	
	var _factories = {};
	
	this.factory = function(name, func){
		if(arguments.length == 1)
			return _factories[name];
			
		if(typeof func !== 'function')
			throw Error('Not a function');
		_factories[name] = func;
		return this;
	}
	
	this.factories = function(){
		return _factories;
	}
	
	this.view = function(viewid, factory, region, label, settings){
		if(viewid === undefined)
			throw Error('Illegal argument for viewid ' + viewid);
		//get view
		if(arguments.length == 1)
			return _views[viewid];
		//set view
		if(factory === undefined)
			throw Error('Illegal argument for factory ' + factory);
		var componentName;
		if(typeof factory === 'string')
			componentName = factory;
		else if(typeof factory === 'function'){
			this.factory(viewid, factory);
			componentName = viewid;
		}
		region = region || 'center-bottom';
		settings = settings || [];
		settings.label = label;
		
		_views[viewid] = {
			id: viewid,
			type: 'component',
			componentName: componentName,
			componentState: settings,
			defaultRegionId: region
		}
		return this;
	};
	
	this.views = function(){
		return _views;
	}
	
};

function LayoutController(viewRegistry){

	this.viewRegistry = viewRegistry;
	
	this.regions = {
		'main': {
			id: 'main',
			type: 'row',
			isClosable: false
		},
		'left': {
			id: 'left',
			type: 'column',
			width: 30,
			defaultRegionId: 'main'
		},
		'left-top': {
			id: 'left-top',
			type: 'stack',
			defaultRegionId: 'left'
		},
		'left-middle': {
			id: 'left-middle',
			type: 'stack',
			defaultRegionId: 'left'
		},
		'left-bottom': {
			id: 'left-bottom',
			type: 'stack',
			defaultRegionId: 'left'
		},
		'center': {
			id: 'center',
			type: 'column',
			defaultRegionId: 'main'
		},
		'center-top': {
			id: 'center-top',				
			type: 'stack',
			defaultRegionId: 'center'
		},
		'center-middle': {
			id: 'center-middle',				
			type: 'stack',
			defaultRegionId: 'center'
		},
		'center-bottom': {
			id: 'center-bottom',				
			type: 'stack',
			defaultRegionId: 'center'
		},
		'right': {
			id: 'right',
			type: 'column',
			defaultRegionId: 'main'
		},
		'right-top': {
			id: 'right-top',
			type: 'stack',
			defaultRegionId: 'right'
		},
		'right-middle': {
			id: 'right-middle',
			type: 'stack',
			defaultRegionId: 'right'
		},
		'right-bottom': {
			id: 'right-bottom',
			type: 'stack',
			defaultRegionId: 'right'
		}
	}
	
	var parentIds = function(node, regionId, arr){
		arr = arr || [];
		var _parentId = regionId || node.defaultRegionId;
		if(!_parentId)
			return arr;
		if(this.regions[_parentId]){
			arr.push(_parentId);
			return parentIds.call(this, this.regions[_parentId], undefined, arr);
		}
		return arr;
	};
	
	var copy = function(src){
		return JSON.parse(JSON.stringify(src));	
	};
	
	var gridItem = function(top, id){
		if(top.id === id)
			return top;
		if(top.content){
			var item = top.content.find(function(_item){
				return gridItem(_item, id);
			});
			return item && gridItem(item, id)
		}
	};
	
	var addView = function(views, view, grid){
		var node;
		var path = [view.id].concat(parentIds.call(this, view));
		//build branches top-bottom				
		path.reverse().forEach(function(compId, idx, arr){
			var _gridNode = gridItem(grid, compId);
			if(_gridNode){
				node = _gridNode;
				return;//next
			} else {
				var child = this.regions[compId] || views[compId];
				if(child){
					child = copy(child);
					if(!node.content)
						node.content = [];	
					if(!node.content.find(function(it){return it.id == child.id}))
						node.content.push(child);
					node = child;
				}
			}
		}.bind(this));
	}.bind(this);
	
	this.layoutViews = function(views){
		var grid = copy(this.regions.main);
		if(views){
			Object.values(views).map(function(view){
				view = copy(view);
				addView(views, view, grid);
				return view;
			}.bind(this));
		}	
		return grid;
	}.bind(this);
	
	this.messageHub = new FramesMessageHub();
	
	this.listeners = {};
	
	this.addListener = function(eventType, callback){
		if(!this.listeners[eventType]){
			this.listeners[eventType] = [];
		}
		var subscriber = this.messageHub.subscribe(callback.bind(this), eventType);

		var entry = {
			id: uuidv4(),
			handler: subscriber
		};
		this.listeners[eventType].push(entry);
		return entry.id;
	};		
	this.removeListener = function(id, eventType){
		if(!this.listeners[eventType])
			return;
		for (var i = this.listeners[eventType].length - 1; i >= 0; i--) {
		  if (this.listeners[eventType][i].id == id) {
			var subscriber = this.listeners[eventType][i].handler;
			this.messageHub.unsibscribe(subscriber);
			this.listeners[eventType].splice(i, 1);
		  }
		}
	};
	
	this.init = function(containerEl, viewNames){
		this.containerEl = containerEl;
		this.viewNames = viewNames;
		
		var views = {};
		viewNames.forEach(function(viewName){
			views[viewName] = this.viewRegistry.view(viewName);
		});
		this.config = {
			dimensions: {
				headerHeight: 26,
				borderWidth: 3
			},
			content: [this.layoutViews.call(this, views)]
		};
		
		var savedState = localStorage.getItem('DIRIGIBLE.IDE.GL.state');
		var cfg = this.config;
		if( savedState !== null ) {
			cfg = JSON.parse(savedState);
		}
		this.layout = new GoldenLayout(cfg, containerEl);
		
		Object.keys(this.viewRegistry.factories()).forEach(function(factoryname){
			this.layout.registerComponent(factoryname, this.viewRegistry.factory(factoryname));
		}.bind(this));
		
		this.layout.on('stateChanged', function(){
			//TODO: debounce or do taht only with save button! This fires a lot
			var state = JSON.stringify( this.layout.toConfig() );
			localStorage.setItem('DIRIGIBLE.IDE.GL.state', state );
		}.bind(this));
		
		this.layout.init();
	};
	
	this.openView = function(viewId/*, region*/){
		var viewDef = this.viewRegistry.view(viewId);
		if(viewDef){
			var node;
			var defaultPath = [viewId];
			defaultPath = defaultPath.concat(parentIds.call(this, viewDef/*, region*/));//TODO: consider custom region
			defaultPath.reverse().forEach(function(compId){
				if(!node)
					node = this.layout.root;
				else
					node = this.layout.root.getItemsById(node.id || node.config.id)[0];
				var child = node.getItemsById(compId)[0] || this.regions[compId] || this.viewRegistry.view(compId);
				if(!node.isRoot && child && this.layout.root.getItemsById(child.id || child.config.id).length<1){
					node.addChild(child);
				}
				node = child;
			}.bind(this));
		}
	};
	
	this.resize = function(){
		this.layout.updateSize();
	};
	
	this.reset = function(){
		this.layout.destroy();
		this.containerEl.empty();
		this.layout.init(this.containerEl, this.viewNames);
	};
};

function uuidv4() {
  return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
	(c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
  )
}