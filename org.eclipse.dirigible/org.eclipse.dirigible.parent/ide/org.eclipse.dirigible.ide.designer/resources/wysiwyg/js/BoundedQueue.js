define([],function(){
	
	
	var BoundedQueue =  function(size){
		
		this.size = size;
		this.arr = []
	}
	
	BoundedQueue.prototype.push = function(obj){
		
		if(this.size == this.arr.length){
			this.arr.shift();
		}
		this.arr.push(obj);
	};
	
	BoundedQueue.prototype.head = function(obj){
		return this.arr[this.arr.length - 1];
	};
	
	BoundedQueue.prototype.tail = function(obj){
		return this.arr[0];
	};

	
	return BoundedQueue;
});