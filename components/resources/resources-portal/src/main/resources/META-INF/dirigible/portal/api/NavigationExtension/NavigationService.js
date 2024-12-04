import { extensions } from "sdk/extensions";
import { response } from "sdk/http";

const navigationList = [];
const navigationExtensions = extensions.getExtensions("dashboard-navigations");

for (let i = 0; i < navigationExtensions.length; i++) {
    const extensionPath = navigationExtensions[i];

    let path = `../../../${extensionPath}`;

    const { getNavigation } = await import(path);

    try {
        const navigation = getNavigation();
        navigationList.push(navigation);
    } catch (err) {
        console.error(`Failed to load a navigation group in NavigationService: ${err}\npath: ${path}`);
    }
}

response.println(JSON.stringify(navigationList));
