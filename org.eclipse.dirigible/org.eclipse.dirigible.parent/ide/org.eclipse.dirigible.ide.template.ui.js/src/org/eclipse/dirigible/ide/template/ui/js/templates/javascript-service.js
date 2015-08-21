/* globals $ */
/* eslint-env node, dirigible */

// print in system output
var systemLib = require('system');
systemLib.println("Hello World!");
// print in response
$\.getResponse().setContentType("text/html");
$\.getResponse().getWriter().println("Hello World!");
$\.getResponse().getWriter().flush();
$\.getResponse().getWriter().close();