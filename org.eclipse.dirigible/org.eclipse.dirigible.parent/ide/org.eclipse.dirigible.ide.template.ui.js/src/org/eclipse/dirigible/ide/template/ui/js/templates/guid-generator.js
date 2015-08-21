/* globals $ */
/* eslint-env node, dirigible */

exports.generateGuid = function() {
    var guid = $\.getUuidUtils().randomUUID();
    return guid;
};