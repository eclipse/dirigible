import { extensions } from "sdk/extensions";
import { response } from "sdk/http";

const groupList = [];
const groupExtensions = extensions.getExtensions("dashboard-navigation-groups");

for (let i = 0; i < groupExtensions.length; i++) {
    const extensionPath = groupExtensions[i];

    let path = `../../../${extensionPath}`;

    const { getGroup } = await import(path);

    try {
        const group = getGroup();
        groupList.push(group);
    } catch (err) {
        console.error(`Failed to load a navigation in NavigationGroupsService: ${err}\npath: ${path}`);
    }
}

response.println(JSON.stringify(groupList));
