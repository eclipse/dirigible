{
    "description":"test command printing the os name and version",
    "contentType":"plain/text",
    "useContent":"false",
    "workDir":".",
    "commands": 
        [
            {
                "osName": "Linux",
                "command":"bash -c \"cat /proc/version\""
            }
            ,
            {
                "osName": "Windows",
                "command":"cmd /c \"systeminfo\""
            }
        ],
    "envAdd":
        [
            {
            	"name":"java.env1",
                "value":"toBeAdded"
            }
        ],
    "envRemove":
        [
            {
                "name":"java.env2"
            }
        ]
}
