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
exports.getTemplate = function() {
	var view = {
			"name":"bpmn",
			"label":"Business Process Model",
			"extension":"bpmn",
			"data":'<?xml version="1.0" encoding="UTF-8"?>\n<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:flowable="http://flowable.org/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" typeLanguage="http://www.w3.org/2001/XMLSchema" expressionLanguage="http://www.w3.org/1999/XPath" targetNamespace="http://www.flowable.org/processdef">\n\t<process id="myprocess" name="MyProcess" isExecutable="true">\n\t\t<startEvent id="sid-3334E861-7999-4B89-B8B0-11724BA17A3E"/>\n\t\t<serviceTask id="sayHello" name="MyServiceTask" flowable:class="org.eclipse.dirigible.components.bpm.flowable.delegate.DirigibleCallDelegate">\n\t\t\t<extensionElements>\n\t\t\t\t<flowable:field name="handler">\n\t\t\t\t\t<flowable:string><![CDATA[myproject/mydelegate.js]]></flowable:string>\n\t\t\t\t</flowable:field>\n\t\t\t</extensionElements>\n\t\t</serviceTask>\n\t\t<sequenceFlow id="sid-797626AE-B2F6-4C00-ABEE-FB30ADC177E4" sourceRef="sid-3334E861-7999-4B89-B8B0-11724BA17A3E" targetRef="sayHello"/>\n\t\t<endEvent id="sid-70B488C1-384A-4E19-8091-1B12D1AEC7FD"/>\n\t\t<sequenceFlow id="sid-645847E8-C959-48BD-816B-2E9CC4A2F08A" sourceRef="sayHello" targetRef="sid-70B488C1-384A-4E19-8091-1B12D1AEC7FD"/>\n\t</process>\n\t<bpmndi:BPMNDiagram id="BPMNDiagram_myprocess">\n\t\t<bpmndi:BPMNPlane bpmnElement="myprocess" id="BPMNPlane_myprocess">\n\t\t\t<bpmndi:BPMNShape bpmnElement="sid-3334E861-7999-4B89-B8B0-11724BA17A3E" id="BPMNShape_sid-3334E861-7999-4B89-B8B0-11724BA17A3E">\n\t\t\t\t<omgdc:Bounds height="30.0" width="30.0" x="103.0" y="78.0"/>\n\t\t\t</bpmndi:BPMNShape>\n\t\t\t<bpmndi:BPMNShape bpmnElement="sayHello" id="BPMNShape_sayHello">\n\t\t\t\t<omgdc:Bounds height="80.0" width="100.0" x="300.0" y="53.0"/>\n\t\t\t</bpmndi:BPMNShape>\n\t\t\t<bpmndi:BPMNShape bpmnElement="sid-70B488C1-384A-4E19-8091-1B12D1AEC7FD" id="BPMNShape_sid-70B488C1-384A-4E19-8091-1B12D1AEC7FD">\n\t\t\t\t<omgdc:Bounds height="28.0" width="28.0" x="562.0" y="78.0"/>\n\t\t\t</bpmndi:BPMNShape>\n\t\t\t<bpmndi:BPMNEdge bpmnElement="sid-797626AE-B2F6-4C00-ABEE-FB30ADC177E4" id="BPMNEdge_sid-797626AE-B2F6-4C00-ABEE-FB30ADC177E4">\n\t\t\t\t<omgdi:waypoint x="133.0" y="93.0"/>\n\t\t\t\t<omgdi:waypoint x="300.0" y="93.0"/>\n\t\t\t</bpmndi:BPMNEdge>\n\t\t\t<bpmndi:BPMNEdge bpmnElement="sid-645847E8-C959-48BD-816B-2E9CC4A2F08A" id="BPMNEdge_sid-645847E8-C959-48BD-816B-2E9CC4A2F08A">\n\t\t\t\t<omgdi:waypoint x="400.0" y="92.77876106194691"/>\n\t\t\t\t<omgdi:waypoint x="562.0001370486572" y="92.06194629624488"/>\n\t\t\t</bpmndi:BPMNEdge>\n\t\t</bpmndi:BPMNPlane>\n\t</bpmndi:BPMNDiagram>\n</definitions>',
			"order": 20
	};
	return view;
};
