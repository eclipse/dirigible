/* globals $ */
/* eslint-env node, dirigible */

var uuid = require('utils/uuid');

exports.generateGuid = function() {
    var guid = uuid.randomUUID();
    return guid;
};