import { extensions } from "sdk/extensions";
import { response } from "sdk/http";

const widgetList = [];
const widgetExtensions = extensions.getExtensions("dashboard-widgets");

for (let i = 0; i < widgetExtensions.length; i++) {
    const extensionPath = widgetExtensions[i];

    let path = `../../../${extensionPath}`;

    const { getWidget } = await import(path);

    try {
        const widget = getWidget();
        widgetList.push(widget);
    } catch (err) {
        console.error(err)
    }
}

response.println(JSON.stringify(widgetList));
