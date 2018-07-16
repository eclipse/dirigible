var listTemplate = require('template-application-angular/template/ui/list');
var manageTemplate = require('template-application-angular/template/ui/manage');
var masterDetailsListTemplate = require('template-application-angular/template/ui/masterDetailsList');
var masterDetailsManageTemplate = require('template-application-angular/template/ui/masterDetailsManage');
var reportBarTemplate = require('template-application-angular/template/ui/reportBar');
var reportLineTemplate = require('template-application-angular/template/ui/reportLine');
var reportPieTemplate = require('template-application-angular/template/ui/reportPie');
var reportTableTemplate = require('template-application-angular/template/ui/reportTable');
var perspective = require('template-application-angular/template/ui/perspective');
var launchpad = require('template-application-angular/template/ui/launchpad');
var tiles = require('template-application-angular/template/ui/tiles');
var menu = require('template-application-angular/template/ui/menu');

exports.getSources = function(parameters) {
    var sources = [];
    sources = sources.concat(listTemplate.getSources(parameters));
    sources = sources.concat(manageTemplate.getSources(parameters));
    sources = sources.concat(masterDetailsListTemplate.getSources(parameters));
    sources = sources.concat(masterDetailsManageTemplate.getSources(parameters));
    sources = sources.concat(reportBarTemplate.getSources(parameters));
    sources = sources.concat(reportLineTemplate.getSources(parameters));
    sources = sources.concat(reportPieTemplate.getSources(parameters));
    sources = sources.concat(reportTableTemplate.getSources(parameters));
    sources = sources.concat(perspective.getSources(parameters));
    sources = sources.concat(launchpad.getSources(parameters));
    sources = sources.concat(tiles.getSources(parameters));
    sources = sources.concat(menu.getSources(parameters));
    return sources;
};
