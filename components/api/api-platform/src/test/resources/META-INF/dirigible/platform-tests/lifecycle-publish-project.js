import { Workspace as workspace } from "sdk/platform/workspace";
import { Lifecycle as lifecycle } from "sdk/platform/lifecycle";
import { Bytes as bytes } from "sdk/io/bytes";
import { Assert } from 'test/assert';

const user = "dirigible";
const workspaceName = "workspace";
const projectName = "project";

const myWorkspace = workspace.createWorkspace(workspaceName);
const myProject = myWorkspace.createProject("project");
const myFile = myProject.createFile(projectName);
myFile.setContent(bytes.textToByteArray("console.log('Hello World!');"));

const publishResult = lifecycle.publish(user, workspaceName, projectName);
const unpublishResult = lifecycle.unpublish(user, workspaceName, projectName);

Assert.assertTrue(publishResult);
Assert.assertTrue(unpublishResult);