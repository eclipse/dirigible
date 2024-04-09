
var manager = require('platform/workspace');
var assertTrue = require('test/assert').assertTrue;

manager.createWorkspace('testworkspace');
var workspace = manager.getWorkspace('testworkspace');

assertTrue(workspace.exists());