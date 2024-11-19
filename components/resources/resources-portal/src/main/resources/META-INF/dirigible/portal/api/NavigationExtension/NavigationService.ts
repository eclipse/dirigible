import { extensions } from "sdk/extensions";
import { response } from "sdk/http";

const navigationList = [];
const navigationExtensions = extensions.getExtensions("dashboard-navigations");

for (let i = 0; i < navigationExtensions.length; i++) {
    const extensionPath = navigationExtensions[i];

    let path = `../../../${extensionPath}`;

    const { getNavigation } = await import(path);

    const navigation = getNavigation();
    navigationList.push(navigation);
}

response.println(JSON.stringify(navigationList));