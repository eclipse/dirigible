const DataService  = require('http/rs-data').DataService;
var MyDAO = function(){};
MyDAO.prototype.list = function(){
    return [{id:1, text:'b'}];
};
MyDAO.prototype.count = function(){
    return this.list().length;
};
var svc = new DataService(new MyDAO());
svc.service();