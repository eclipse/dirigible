/*
 * Generated by Eclipse Dirigible based on model and template.
 *
 * Do not modify the content as it may be re-generated again.
 */
import * as listTemplate from "template-application-ui-angular/template/ui/list";
import * as manageTemplate from "template-application-ui-angular/template/ui/manage";
import * as masterDetailsListTemplate from "template-application-ui-angular/template/ui/masterDetailsList";
import * as masterDetailsManageTemplate from "template-application-ui-angular/template/ui/masterDetailsManage";
import * as reportTemplate from "template-application-ui-angular/template/ui/report";
import * as reportChartTemplate from "template-application-ui-angular/template/ui/reportChart";
import * as reportTableTemplate from "template-application-ui-angular/template/ui/reportTable";
// import * as perspective from "template-application-ui-angular/template/ui/perspective";
import * as launchpad from "template-application-ui-angular/template/ui/launchpad";
// import * as tiles from "template-application-ui-angular/template/ui/tiles";
// import * as menu from "template-application-ui-angular/template/ui/menu";

export function getSources(parameters) {
    var sources = [];
    sources = sources.concat(launchpad.getSources(parameters));
    sources = sources.concat(listTemplate.getSources(parameters));
    sources = sources.concat(manageTemplate.getSources(parameters));
    sources = sources.concat(masterDetailsListTemplate.getSources(parameters));
    sources = sources.concat(masterDetailsManageTemplate.getSources(parameters));
    sources = sources.concat(reportTemplate.getSources(parameters));
    sources = sources.concat(reportChartTemplate.getSources(parameters));
    sources = sources.concat(reportTableTemplate.getSources(parameters));
    // sources = sources.concat(perspective.getSources(parameters));
    // sources = sources.concat(tiles.getSources(parameters));
    // sources = sources.concat(menu.getSources(parameters));
    return sources;
};
