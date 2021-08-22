var transformer = require("ide-entity/template/transform-edm");
var workspaceManager = require("platform/v4/workspace");

var workspace = __context.get('workspace');
var project = __context.get('project');
var path = __context.get('path');

var modelPath = path.replace(".edm", ".model");
var content = transformer.transform(workspace, project, path);

if (content !== null) {
    var bytes = require("io/v4/bytes");
    input = bytes.textToByteArray(content);

    if (workspaceManager.getWorkspace(workspace)
        .getProject(project).getFile(path).exists()) {
            workspaceManager.getWorkspace(workspace)
                .getProject(project).createFile(modelPath, input);
    } else {
        workspaceManager.getWorkspace(workspace)
            .getProject(project).getFile(modelPath).setContent(input);
    }
}
