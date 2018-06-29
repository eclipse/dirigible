var listTemplate = require('template-application-angular/template/ui/list');
var manageTemplate = require('template-application-angular/template/ui/manage');
var masterDetailsListTemplate = require('template-application-angular/template/ui/masterDetailsList');
var masterDetailsManageTemplate = require('template-application-angular/template/ui/masterDetailsManage');
var perspective = require('template-application-angular/template/ui/perspective');
var launchpad = require('template-application-angular/template/ui/launchpad');

exports.getSources = function(parameters) {
    var sources = [];
    sources = sources.concat(listTemplate.getSources(parameters));
    sources = sources.concat(manageTemplate.getSources(parameters));
    sources = sources.concat(masterDetailsListTemplate.getSources(parameters));
    sources = sources.concat(masterDetailsManageTemplate.getSources(parameters));
    sources = sources.concat(perspective.getSources(parameters));
    sources = sources.concat(launchpad.getSources(parameters));
    return sources;
};