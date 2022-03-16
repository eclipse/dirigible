let constraintsProcessor = require("ide-documents/api/processors/constraintsProcessor");

let accessDefinitions = constraintsProcessor.getAccessDefinitions();
constraintsProcessor.updateAccessDefinitions(accessDefinitions);

console.log("Access Definitions successfully synchronized by [ide-documents-security] Job");