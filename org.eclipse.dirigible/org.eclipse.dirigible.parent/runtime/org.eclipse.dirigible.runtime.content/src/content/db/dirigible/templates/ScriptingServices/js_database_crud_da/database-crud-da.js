/* globals $ */
/* eslint-env node, dirigible */

var ${fileNameNoExtension}Dao = require('${packageName}/dao/${fileNameNoExtension}Dao').get();

var DataService = require('arestme/data_service').DataService;

new DataService(${fileNameNoExtension}Dao).service();

