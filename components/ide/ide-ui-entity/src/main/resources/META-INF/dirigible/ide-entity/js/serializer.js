/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
function createModel(graph) {
	let model = [];
	model.push('<model>\n');
	model.push(' <entities>\n');
	let parent = graph.getDefaultParent();
	let childCount = graph.model.getChildCount(parent);

	for (let i = 0; i < childCount; i++) {
		let child = graph.model.getChildAt(parent, i);

		if (!graph.model.isEdge(child)) {
			child.value.dataName = child.value.dataName ? child.value.dataName : JSON.stringify(child.value.name).replace(/\W/g, '').toUpperCase();
			child.value.dataCount = child.value.dataCount ? child.value.dataCount : "SELECT COUNT(*) AS COUNT FROM \"${tablePrefix}" + JSON.stringify(child.value.name).replace(/\W/g, '').toUpperCase() + "\"";
			child.value.title = child.value.title ? child.value.title : child.value.name;
			child.value.caption = child.value.caption ? child.value.caption : "Manage entity " + child.value.name;
			child.value.tooltip = child.value.tooltip ? child.value.tooltip : child.value.name;
			child.value.menuKey = child.value.menuKey ? child.value.menuKey : JSON.stringify(child.value.name).replace(/\W/g, '').toLowerCase();
			child.value.menuLabel = child.value.menuLabel ? child.value.menuLabel : child.value.name;
			child.value.disableGeneration = child.value.disableGeneration ? child.value.disableGeneration : "false";
			let entityContent = '  <entity name="' + _.escape(child.value.name) +
				'" dataName="' + _.escape(child.value.dataName) +
				'" dataCount="' + _.escape(child.value.dataCount) +
				'" dataQuery="' + _.escape(child.value.dataQuery) +
				'" disableGeneration="' + _.escape(child.value.disableGeneration) +
				'" type="' + _.escape(child.value.entityType ? child.value.entityType : 'PRIMARY') +
				'" title="' + _.escape(child.value.title) +
				'" caption="' + _.escape(child.value.caption) +
				'" tooltip="' + _.escape(child.value.tooltip) +
				'" icon="' + _.escape(child.value.icon) +
				'" menuKey="' + _.escape(child.value.menuKey) +
				'" menuLabel="' + _.escape(child.value.menuLabel) +
				'" menuIndex="' + _.escape(child.value.menuIndex) +
				'" layoutType="' + _.escape(child.value.layoutType) +
				'" navigationPath="' + _.escape(child.value.navigationPath) +
				'" perspectiveName="' + _.escape(child.value.perspectiveName) +
				'" perspectiveIcon="' + getPerspectiveIcon(graph, child) +
				'" perspectiveOrder="' + getPerspectiveOrder(graph, child) + '"';

			if (child.value.feedUrl && child.value.feedUrl !== "") {
				child.value.feedUrl = btoa(child.value.feedUrl);
				entityContent += ' feedUrl="' + child.value.feedUrl + '"';
			}
			if (child.value.feedUsername && child.value.feedUsername !== "") {
				child.value.feedUsername = btoa(child.value.feedUsername);
				entityContent += ' feedUsername="' + child.value.feedUsername + '"';
			}
			if (child.value.feedPassword && child.value.feedPassword !== "") {
				child.value.feedPassword = btoa(child.value.feedPassword);
				entityContent += ' feedPassword="' + child.value.feedPassword + '"';
			}
			if (child.value.feedSchedule && child.value.feedSchedule !== "") {
				entityContent += ' feedSchedule="' + child.value.feedSchedule + '"';
			}
			if (child.value.feedPath && child.value.feedPath !== "") {
				entityContent += ' feedPath="' + child.value.feedPath + '"';
			}
			if (child.value.roleRead && child.value.roleRead !== "") {
				entityContent += ' roleRead="' + child.value.roleRead + '"';
			}
			if (child.value.roleWrite && child.value.roleWrite !== "") {
				entityContent += ' roleWrite="' + child.value.roleWrite + '"';
			}
			if (child.value.projectionReferencedModel && child.value.projectionReferencedModel !== "") {
				entityContent += ' projectionReferencedModel="' + child.value.projectionReferencedModel + '"';
			}
			if (child.value.projectionReferencedEntity && child.value.projectionReferencedEntity !== "") {
				entityContent += ' projectionReferencedEntity="' + child.value.projectionReferencedEntity + '"';
			}

			entityContent += '>\n';
			model.push(entityContent);

			let propertyCount = graph.model.getChildCount(child);
			if (propertyCount > 0) {
				for (let j = 0; j < propertyCount; j++) {
					let property = graph.model.getChildAt(child, j).value;

					property.dataName = property.dataName ? property.dataName : _.escape(child.value.dataName).toUpperCase() + "_" + JSON.stringify(property.name).replace(/\W/g, '').toUpperCase();

					model.push('    <property name="' + _.escape(property.name) +
						'" isCalculatedProperty="' + _.escape(property.isCalculatedProperty) +
						'" calculatedPropertyExpression="' + _.escape(property.calculatedPropertyExpression) +
						'" dataName="' + _.escape(property.dataName) +
						'" dataType="' + _.escape(property.dataType) + '"');
					if (property.dataLength !== null) {
						model.push(' dataLength="' + _.escape(property.dataLength) + '"');
					}
					if (property.dataNotNull) {
						model.push(' dataNullable="' + (property.dataNotNull == "false") + '"');
					} else {
						model.push(' dataNullable="true"');
					}
					if (property.dataPrimaryKey) {
						model.push(' dataPrimaryKey="' + (property.dataPrimaryKey == "true") + '"');
					} else {
						model.push(' dataPrimaryKey="false"');
					}
					if (property.dataAutoIncrement) {
						model.push(' dataAutoIncrement="' + (property.dataAutoIncrement == "true") + '"');
					} else {
						model.push(' dataAutoIncrement="false"');
					}
					if (property.dataUnique) {
						model.push(' dataUnique="' + (property.dataUnique == "true") + '"');
					} else {
						model.push(' dataUnique="false"');
					}
					if (property.dataDefaultValue !== null) {
						model.push(' dataDefaultValue="' + _.escape(property.dataDefaultValue) + '"');
					}
					if (property.dataPrecision !== null) {
						model.push(' dataPrecision="' + _.escape(property.dataPrecision) + '"');
					}
					if (property.dataScale !== null) {
						model.push(' dataScale="' + _.escape(property.dataScale) + '"');
					}
					if (property.relationshipType !== null) {
						model.push(' relationshipType="' + _.escape(property.relationshipType ? property.relationshipType : 'ASSOCIATION') + '"');
					}
					if (property.relationshipCardinality !== null) {
						model.push(' relationshipCardinality="' + _.escape(property.relationshipCardinality ? property.relationshipCardinality : '1_n') + '"');
					}
					if (property.relationshipName !== null) {
						model.push(' relationshipName="' + _.escape(property.relationshipName) + '"');
					}
					if (property.widgetType !== null) {
						model.push(' widgetType="' + _.escape(property.widgetType) + '"');
					}
					if (property.widgetLength !== null) {
						model.push(' widgetLength="' + _.escape(property.widgetLength) + '"');
					}
					if (property.widgetLabel !== null) {
						model.push(' widgetLabel="' + _.escape(property.widgetLabel) + '"');
					}
					if (property.widgetShortLabel !== null) {
						model.push(' widgetShortLabel="' + _.escape(property.widgetShortLabel) + '"');
					}
					if (property.widgetPattern !== null) {
						model.push(' widgetPattern="' + _.escape(property.widgetPattern) + '"');
					}
					if (property.widgetFormat !== null) {
						model.push(' widgetFormat="' + _.escape(property.widgetFormat) + '"');
					}
					if (property.widgetSection !== null) {
						model.push(' widgetSection="' + _.escape(property.widgetSection) + '"');
					}
					if (property.widgetService !== null) {
						model.push(' widgetService="' + _.escape(property.widgetService) + '"');
					}
					if (property.widgetIsMajor) {
						model.push(' widgetIsMajor="' + (property.widgetIsMajor == "true") + '"');
					} else {
						model.push(' widgetIsMajor="false"');
					}
					if (property.feedPropertyName !== null) {
						model.push(' feedPropertyName="' + _.escape(property.feedPropertyName) + '"');
					}
					if (property.roleRead !== null) {
						model.push(' roleRead="' + _.escape(property.roleRead) + '"');
					}
					if (property.roleWrite !== null) {
						model.push(' roleWrite="' + _.escape(property.roleWrite) + '"');
					}
					if (property.widgetDropDownKey !== null) {
						model.push(' widgetDropDownKey="' + _.escape(property.widgetDropDownKey) + '"');
					}
					if (property.widgetDropDownValue !== null) {
						model.push(' widgetDropDownValue="' + _.escape(property.widgetDropDownValue) + '"');
					}
					if (property.relationshipEntityPerspectiveName !== null) {
						model.push(' relationshipEntityPerspectiveName="' + _.escape(property.relationshipEntityPerspectiveName) + '"');
					}

					model.push('></property>\n');
				}
			}
			model.push('  </entity>\n');
		} else {
			let relationName = child.name ? child.name : child.source.parent.value.name + '_' + child.target.parent.value.name;
			model.push('  <relation name="' + _.escape(child.source.parent.value.name) + '_'
				+ _.escape(child.target.parent.value.name) + '" type="relation" ');
			model.push('entity="' + _.escape(child.source.parent.value.name) + '" ');
			model.push('relationName="' + _.escape(relationName) + '" ');
			model.push('relationshipEntityPerspectiveName="' + _.escape(child.target.parent.value.perspectiveName) + '" ');
			model.push('property="' + _.escape(child.source.value.name) + '" ' +
				'referenced="' + _.escape(child.target.parent.value.name) + '" ' +
				'referencedProperty="' + _.escape(child.target.value.name) + '">\n');
			model.push('  </relation>\n');
		}
	}
	model.push(' </entities>\n');

	if (graph.getModel().perspectives) {
		model.push(' <perspectives>\n');
		for (let i = 0; i < graph.getModel().perspectives.length; i++) {
			model.push('  <perspective><name>' + _.escape(graph.getModel().perspectives[i].id) + '</name><label>' + _.escape(graph.getModel().perspectives[i].label) + '</label><icon>' + _.escape(graph.getModel().perspectives[i].icon) + '</icon><order>' + _.escape(graph.getModel().perspectives[i].order) + '</order></perspective>\n');
		}
		model.push(' </perspectives>\n');
	}

	if (graph.getModel().navigations) {
		model.push(' <navigations>\n');
		for (let i = 0; i < graph.getModel().navigations.length; i++) {
			model.push('  <item><path>' + _.escape(graph.getModel().navigations[i].path) + '</path><label>' + _.escape(graph.getModel().navigations[i].label) + '</label><icon>' + _.escape(graph.getModel().navigations[i].icon) + '</icon><url>' + _.escape(graph.getModel().navigations[i].url) + '</url></item>\n');
		}
		model.push(' </navigations>\n');
	}

	let enc = new mxCodec(mxUtils.createXmlDocument());
	let node = enc.encode(graph.getModel());
	let mxGraph = mxUtils.getXml(node);
	model.push(' ' + mxGraph);
	model.push('\n</model>');

	for (let i = 0; i < childCount; i++) {
		let child = graph.model.getChildAt(parent, i);

		if (!graph.model.isEdge(child)) {

			if (child.value.feedUrl) {
				child.value.feedUrl = atob(child.value.feedUrl);
			}
			if (child.value.feedUsername) {
				child.value.feedUsername = atob(child.value.feedUsername);
			}
			if (child.value.feedPassword) {
				child.value.feedPassword = atob(child.value.feedPassword);
			}
		}
	}

	return model.join('');
}

function getPerspectiveIcon(graph, child) {
	let perspectiveName = _.escape(child.value.perspectiveName);
	let perspectiveIcon = _.escape(child.value.perspectiveIcon);
	let perspectives = graph.getModel().perspectives || [];
	for (let i = 0; i < perspectives.length; i++) {
		if (perspectiveName === _.escape(perspectives[i].id)) {
			perspectiveIcon = perspectives[i].icon;

		}
	}
	return perspectiveIcon;
}

function getPerspectiveOrder(graph, child) {
	let perspectiveName = _.escape(child.value.perspectiveName);
	let perspectiveOrder = _.escape(child.value.perspectiveOrder);
	let perspectives = graph.getModel().perspectives || [];
	for (let i = 0; i < perspectives.length; i++) {
		if (perspectiveName === _.escape(perspectives[i].id)) {
			perspectiveOrder = perspectives[i].order;

		}
	}
	return perspectiveOrder;
}

// function createModelJson(graph) {
// 	var root = {};
// 	root.model = {};
// 	root.model.entities = [];
// 	var parent = graph.getDefaultParent();
// 	var childCount = graph.model.getChildCount(parent);
// 	var compositions = {};

// 	for (var i=0; i<childCount; i++) {
// 		var child = graph.model.getChildAt(parent, i);
// 		if (graph.model.isEdge(child)) {
// 			// Relationship Properties
// 			var relationName = child.name ? child.name : child.source.parent.value.name+'_'+ child.target.parent.value.name;
// 			child.source.value.relationshipName = _.escape(relationName);
// 			child.source.value.relationshipEntityName = _.escape(child.target.parent.value.name);
// 			child.source.value.relationshipEntityPerspectiveName = _.escape(child.target.parent.value.perspectiveName);
// 			child.source.value.widgetDropDownKey = _.escape(child.source.value.widgetDropDownKey ? child.source.value.widgetDropDownKey : child.target.value.name);
// 			child.source.value.widgetDropDownValue = _.escape(child.source.value.widgetDropDownValue ? child.source.value.widgetDropDownValue : child.target.value.name);
// 			child.source.value.feedPropertyName = _.escape(child.target.parent.value.feedPropertyName);

// 			if (child.source.value.relationshipType === 'COMPOSITION') {
// 				var composition = {};
// 				composition.entityName = _.escape(child.source.parent.value.name);
// 				composition.entityProperty = _.escape(child.source.value.name);
// 				composition.localProperty = _.escape(child.target.value.name);
// 				if (!compositions[child.target.parent.value.name]) {
// 					compositions[child.target.parent.value.name] = [];
// 				}
// 				compositions[child.target.parent.value.name].push(composition);
// 			}
// 		}
// 	}

// 	for (var i=0; i<childCount; i++) {
// 		var child = graph.model.getChildAt(parent, i);
// 		if (!graph.model.isEdge(child)) {
// 			var entity = {};
// 			entity.name = _.escape(child.value.name);
// 			entity.dataName = _.escape(child.value.dataName ? child.value.dataName : JSON.stringify(child.value.name).replace(/\W/g, '').toUpperCase());
// 			entity.dataCount = _.escape(child.value.dataCount ? child.value.dataCount : 'SELECT COUNT(*) FROM ' + JSON.stringify(child.value.name).replace(/\W/g, '').toUpperCase());
// 			entity.dataQuery = _.escape(child.value.dataQuery);
// 			entity.type = _.escape(child.value.entityType ? child.value.entityType : "PRIMARY");
// 			entity.title = _.escape(child.value.title ? child.value.title : child.value.name);
// 			entity.tooltip = _.escape(child.value.tooltip ? child.value.tooltip : child.value.name);
// 			entity.icon = _.escape(child.value.icon);
// 			entity.menuKey = _.escape(child.value.menuKey ? child.value.menuKey : JSON.stringify(child.value.name).replace(/\W/g, '').toLowerCase());
// 			entity.menuLabel = _.escape(child.value.menuLabel ? child.value.menuLabel : child.value.name);
// 			entity.menuIndex = child.value.menuIndex ? child.value.menuIndex : 100;
// 			entity.layoutType = child.value.layoutType;
// 			entity.perspectiveName = _.escape(child.value.perspectiveName);
// 			entity.perspectiveIcon = _.escape(child.value.perspectiveIcon);
// 			entity.perspectiveOrder = _.escape(child.value.perspectiveOrder);
// 			entity.feedUrl = child.value.feedUrl ? btoa(child.value.feedUrl) : null;
// 			entity.feedUsername = child.value.feedUsername ? btoa(child.value.feedUsername) : null;
// 			entity.feedPassword = child.value.feedPassword ? btoa(child.value.feedPassword) : null;
// 			entity.feedSchedule = child.value.feedSchedule ? _.escape(child.value.feedSchedule) : null;
// 			entity.feedPath = child.value.feedPath ? _.escape(child.value.feedPath) : null;
// 			entity.properties = [];

// 			if (compositions[entity.name]) {
// 				entity.compositions = compositions[entity.name];
// 			}

// 			var propertyCount = graph.model.getChildCount(child);
// 			if (propertyCount > 0) {
// 				for (var j=0; j<propertyCount; j++) {
// 					var childProperty = graph.model.getChildAt(child, j).value;
// 					var property = {};

// 					// General
// 					property.name = _.escape(childProperty.name);
// 					property.isCalculatedProperty = _.escape(childProperty.isCalculatedProperty);
// 					property.calculatedPropertyExpression = _.escape(childProperty.calculatedPropertyExpression);

// 					// Data Properties
// 					property.dataName = _.escape(childProperty.dataName ? childProperty.dataName : JSON.stringify(childProperty.name).replace(/\W/g, '').toUpperCase());
// 					property.dataType = childProperty.dataType;
// 					property.dataLength = childProperty.dataLength;
// 					property.dataDefaultValue = _.escape(childProperty.dataDefaultValue);
// 					property.dataPrimaryKey = childProperty.dataPrimaryKey ? childProperty.dataPrimaryKey : "false";
// 					property.dataAutoIncrement = childProperty.dataAutoIncrement ? childProperty.dataAutoIncrement : "false";
// 					property.dataNotNull = childProperty.dataNotNull ? childProperty.dataNotNull : "false";
// 					property.dataUnique = childProperty.dataUnique ? childProperty.dataUnique : "false";
// 					property.dataPrecision = childProperty.dataPrecision;
// 					property.dataScale = childProperty.dataScale;

// 					// Relationship Properties
// 					property.relationshipType = childProperty.relationshipType;
// 					property.relationshipCardinality = childProperty.relationshipCardinality;
// 					property.relationshipName = _.escape(childProperty.relationshipName);
// 					property.relationshipEntityName = _.escape(childProperty.relationshipEntityName);
// 					property.relationshipEntityPerspectiveName = _.escape(childProperty.relationshipEntityPerspectiveName);

// 					// Widget Properties
// 					property.widgetType = childProperty.widgetType;
// 					property.widgetLength = childProperty.widgetLength;
// 					property.widgetLabel = _.escape(childProperty.widgetLabel ? childProperty.widgetLabel : childProperty.name);
// 					property.widgetShortLabel = _.escape(childProperty.widgetShortLabel ? childProperty.widgetShortLabel : childProperty.name);
// 					property.widgetPattern = _.escape(childProperty.widgetPattern);
// 					property.widgetFormat = _.escape(childProperty.widgetFormat);
// 					property.widgetSection = _.escape(childProperty.widgetSection);
// 					property.widgetService = _.escape(childProperty.widgetService);
// 					property.widgetIsMajor = childProperty.widgetIsMajor ? childProperty.widgetIsMajor : "false";
// 					property.widgetDropDownKey = _.escape(childProperty.widgetDropDownKey);
// 					property.widgetDropDownValue = _.escape(childProperty.widgetDropDownValue);

// 					// Feed Properties
// 					property.feedPropertyName = _.escape(childProperty.feedPropertyName);

// 					entity.properties.push(property);
// 				}
// 			}

// 			root.model.entities.push(entity);
// 		}
// 	}

// 	var serialized = JSON.stringify(root, null, 4);

// 	return serialized;
// }
