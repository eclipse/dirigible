var ftp = require("io/v4/ftp");

var host = "test.rebex.net";
var port = 21;
var userName = "demo";
var password = "password";

var ftpClient = ftp.getClient(host, port, userName, password);
var fileText = ftpClient.getFileText("/", "readme.txt");

fileText !== undefined && fileText !== null;