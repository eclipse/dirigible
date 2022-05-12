var request = require("http/v4/request");
var response = require("http/v4/response");
var registry = require("platform/v4/registry");

let id = request.getParameter("id");
if (id) {
    let namedScripts = new Map();
    namedScripts.set(
        "ide-core-ui",
        [
            "/ide-core/ui/message-hub.js",
            "/ide-core/ui/ui-bootstrap-tpls-0.14.3.min.js",
            "/ide-core/ui/ui-bootstrap.1.3.3.min.js",
            "/ide-core/ui/ui-core-ng-modules.js",
            "/ide-core/ui/ui-layout.js"
        ]
    );

    let namedScript = namedScripts.get(id);
    if (namedScript) {
        namedScript.forEach(function (item) {
            response.println(registry.getText(item));
        });
    } else {
        response.println("Script with 'id': " + id + " is not known.");
    }

} else {
    response.println("Provide the 'id' parameter of the script");
}

response.flush();
response.close();