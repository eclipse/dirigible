// print in system output
var systemLib = require('system');
systemLib.println("Hello World!");
// print in response
response.setContentType("text/html");
response.getWriter().println("Hello World!");
response.getWriter().flush();
response.getWriter().close();