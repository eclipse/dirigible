{
    "description":"Test command printing the OS name and version",
    "contentType":"plain/text",
    "useContent":"false",
    "workDir":".",
    "commands":[
    	{
    		"osName": "Linux",
    		"command":"bash -c \"cat /proc/version\""
		}, {
			"osName": "Windows",
			"command":"cmd /c \"ver\""
		}, {
			"osName": "Mac",
			"command":"bash -c \"uname -s -v\""
		}
	],
    "envAdd":[
    	{
    		"name":"java.env1",
    		"value":"toBeAdded"
    	}
	],
    "envRemove":[
    	{
    		"name":"java.env2"
		}
	]
}