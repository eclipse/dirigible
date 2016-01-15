/* globals $ */
/* eslint-env node, dirigible */

// print in system output
var systemLib = require('system');
systemLib.println("Hello World!");
// print in response
$\.getResponse().setContentType("text/html; charset=UTF-8");
$\.getResponse().setCharacterEncoding("UTF-8");
$\.getResponse().getWriter().println("Hello World!");
$\.getResponse().getWriter().flush();
$\.getResponse().getWriter().close();