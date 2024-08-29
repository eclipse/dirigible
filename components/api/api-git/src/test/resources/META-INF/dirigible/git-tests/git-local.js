import { Client as git } from 'sdk/git/client';
import { Assert } from 'test/assert';
import { Workspace as workspaceManager } from 'sdk/platform/workspace';

const user = 'dirigible';
const email = 'dirigible@eclipse.com';
const workspaceName = 'test-workspace';
const projectName = 'test-project';
const repositoryName = projectName;

const workspace = workspaceManager.createWorkspace(workspaceName);
const project = workspace.createProject(projectName);
let firstFile = project.createFile("firstFile.js");
firstFile.setText('first file content');

git.initRepository(user, email, workspaceName, projectName, repositoryName, 'initial commit');

let repos = git.getGitRepositories(workspaceName);
console.log("Repository name " + repos[0].getName())
Assert.assertTrue(repos[0].getName() === repositoryName);

let secondFile = project.createFile("secondFile.js");
secondFile.setText('second file content');
console.log("Getting staged changes...");
let staged = git.getStagedChanges(workspaceName, repositoryName);
console.log("Staged changes count: " + staged.size())
Assert.assertTrue(staged.size() === 0);

console.log("Getting unstaged changes...");
let unstaged = git.getUnstagedChanges(workspaceName, repositoryName);
console.log("Unstaged changes count: " + unstaged.size())
Assert.assertTrue(unstaged.size() === 1);

git.commit(user, email, workspaceName, repositoryName, "second file added", true);
const his = git.getHistory(repositoryName, workspaceName, projectName);

console.log("History size: " + his.size())
Assert.assertTrue(his.size() === 2);

let branches = git.getLocalBranches(workspaceName, repositoryName);
console.log("Local branches size: " + branches.size());
Assert.assertTrue(branches.size() === 1);

git.createBranch(workspaceName, repositoryName, 'new-branch', 'master');
branches = git.getLocalBranches(workspaceName, repositoryName);
console.log("New local branches size: " + branches.size());
Assert.assertTrue(branches.size() === 2);

const status = git.status(workspaceName, repositoryName);
console.log("Status is clean: " + status.isClean());
Assert.assertTrue(status.isClean() === true);

console.log("Deleting test repo...");
git.deleteRepository(workspaceName, repositoryName);
console.log("Repositories size at the end: " + git.getGitRepositories(workspaceName).size());
Assert.assertTrue(git.getGitRepositories(workspaceName).size() === 0);