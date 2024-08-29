import { Workspace as manager } from 'sdk/platform/workspace';
import { Assert } from 'test/assert';

manager.createWorkspace('testworkspace');
const workspace = manager.getWorkspace('testworkspace');

Assert.assertTrue(workspace.exists());
