import { query } from "@dirigible/db";
import { Utils } from "../Utils.mjs";

export const getArtefacts = () => {
    const sql = `
        SELECT 
        ARTEFACT_TYPE, ARTEFACT_LOCATION, ARTEFACT_NAME, ARTEFACT_PHASE, ARTEFACT_RUNNING, CSV_IMPORTED
        FROM DIRIGIBLE_CSV
    `;
    const resultset = query.execute(sql, [], "SystemDB");
    return resultset.map(e => {
        const artefactStatus = Utils.getArtefactStatus(e);
        artefactStatus.status = e.CSV_IMPORTED === true ? 'Imported' : 'Not Imported'
        return artefactStatus;
    });
};