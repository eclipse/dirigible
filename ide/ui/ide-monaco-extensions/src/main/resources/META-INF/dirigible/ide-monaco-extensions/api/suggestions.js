let response = require("http/v4/response");
let request = require("http/v4/request");
let moduleInfoCache = require("ide-monaco-extensions/api/utils/moduleInfoCache");

let moduleInfo = moduleInfoCache.get(request.getParameter("moduleName"));

response.print(JSON.stringify(moduleInfo.suggestions));
response.flush();
response.close();