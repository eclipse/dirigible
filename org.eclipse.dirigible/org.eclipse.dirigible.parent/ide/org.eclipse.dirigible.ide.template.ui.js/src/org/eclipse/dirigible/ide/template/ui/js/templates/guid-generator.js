/* globals $ */
/* eslint-env node */

exports.generateGuid = function() {
    var guid = $.getUuidUtils().randomUUID();
    return guid;
};