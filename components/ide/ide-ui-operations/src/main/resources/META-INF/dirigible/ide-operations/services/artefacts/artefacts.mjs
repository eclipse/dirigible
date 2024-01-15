import { extensions } from "@dirigible/extensions";
import { response } from "@dirigible/http";

const artefacts = [];
const artefactsExtensions = extensions.getExtensions("ide-operations-artefacts");
for (let i = 0; i < artefactsExtensions?.length; i++) {
    const { getArtefacts } = await import(`../../../${artefactsExtensions[i]}`);
    artefacts.push(...getArtefacts());
}

response.println(JSON.stringify(artefacts));