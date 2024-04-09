
var workspace = require("platform/workspace");
var lifecycle = require("platform/lifecycle");
var bytes = require("io/bytes");
var assertTrue = require('test/assert').assertTrue;

var user = "dirigible";
var workspaceName = "workspace";
var projectName = "project";

var myWorkspace = workspace.createWorkspace(workspaceName);
var myProject = myWorkspace.createProject("project");
var myFile = myProject.createFile(projectName);
myFile.setContent(bytes.textToByteArray("console.log('Hello World!');"));

var publishResult = lifecycle.publish(user, workspaceName, projectName);
var unpublishResult = lifecycle.unpublish(user, workspaceName, projectName);

assertTrue(publishResult);
assertTrue(unpublishResult);