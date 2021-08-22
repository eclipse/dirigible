(function() {
    var transformer = require("ide-entity/template/transform-edm");
    return transformer.transform(__context.get('workspaceName'), __context.get('projectName'), __context.get('filePath'));;
})();