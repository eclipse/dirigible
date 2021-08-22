var response = require("http/v4/response");
var modulesParser = require("ide-monaco-extensions/api/utils/modulesParser");

let modules = modulesParser.getModules();

response.println(JSON.stringify(modules));