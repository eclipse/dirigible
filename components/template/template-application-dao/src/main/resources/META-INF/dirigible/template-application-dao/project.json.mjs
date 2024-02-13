import { workspace } from "sdk/platform";

export function generate(json) {
	const parameters = JSON.parse(json);

	let currenctWorkspace = workspace.getWorkspace(parameters.workspaceName);
	let currentProject = currenctWorkspace.getProject(parameters.projectName);
	let maybeProjectFile = currentProject.getFile("project.json");
	if (maybeProjectFile.exists()) {
		let projectFile = JSON.parse(maybeProjectFile.getText());
		if (!projectFile.actions) {
			projectFile.actions = [];
			projectFile.actions.push({
				"name": "Build TypeScript",
				"commands": [
					{
						"os": "unix",
						"command": "tsc"
					},
					{
						"os": "windows",
						"command": "cmd /c tsc"
					}
				],
				"registry": "true"
			});
			return JSON.stringify(projectFile);
		}
	} else {
		let projectFile = {
			"guid": parameters.projectName,
			"actions": [
				{
					"name": "Build TypeScript",
					"commands": [
						{
							"os": "unix",
							"command": "tsc"
						},
						{
							"os": "windows",
							"command": "cmd /c tsc"
						}
					],
					"registry": "true"
				}
			]
		}
		return JSON.stringify(projectFile);
	}
}