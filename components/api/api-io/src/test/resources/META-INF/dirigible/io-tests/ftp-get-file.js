
var ftp = require("io/ftp");
var assertTrue = require('test/assert').assertTrue;

var host = "test.rebex.net";
var port = 21;
var userName = "demo";
var password = "password";

var ftpClient = ftp.getClient(host, port, userName, password);
var fileText = ftpClient.getFileText("/", "readme.txt");

assertTrue(fileText !== undefined && fileText !== null);