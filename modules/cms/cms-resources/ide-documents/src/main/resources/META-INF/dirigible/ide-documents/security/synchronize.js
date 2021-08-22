var accessUtils = require("ide-documents/security/accessUtils");

let accessDefinitions = accessUtils.getAccessDefinitions();
accessUtils.updateAccessDefinitions(accessDefinitions);

console.log("Access Definitions successfully synchronized by [ide-documents-security] Job");