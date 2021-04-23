var spark = require("spark/client");
var sparkSession = spark.getSession("spark://192.168.32.1:7077");
var dataset = sparkSession.readFormat("src/test/resources/data/UserData.csv", "csv");
var result = dataset.getRowAsString(0)

// console.log(JSON.parse(result))
result === "[id;name;familyname;personalNumber;age]";