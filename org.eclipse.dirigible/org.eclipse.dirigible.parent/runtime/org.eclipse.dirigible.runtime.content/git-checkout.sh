#!/bin/bash

#exec 2>/dev/null

####### Updates Registry's content
#ßpython updateRegistryContent.py

####### Prepare content.zip files

### zip full content
cd src/content/db/
rm ../../../content-all.zip
zip -r ../../../content-all.zip *

### zip runtime content
rm ../../../content-runtime.zip
zip -r ../../../content-runtime.zip dirigible/registry/public dirigible/registry/conf

### zip minimal content
rm ../../../content-min.zip
zip -r ../../../content-min.zip dirigible/registry/public/ScriptingServices/core dirigible/registry/public/ScriptingServices/db dirigible/registry/public/ScriptingServices/doc dirigible/registry/public/ScriptingServices/io dirigible/registry/public/ScriptingServices/log dirigible/registry/public/ScriptingServices/net dirigible/registry/public/ScriptingServices/platform dirigible/registry/public/ScriptingServices/service dirigible/registry/public/ScriptingServices/utils

####### Copy content.zip files

### copy to releng
cd ../../..
cp content-all.zip ../../releng/all.tomcat/src/main/resources/content/repository.zip
cp content-runtime.zip ../../releng/runtime.tomcat/src/main/resources/content/repository.zip
cp content-runtime.zip ../../releng/air.tomcat/src/main/resources/content/repository.zip
cp content-min.zip ../../releng/mini.tomcat/src/main/resources/content/repository.zip