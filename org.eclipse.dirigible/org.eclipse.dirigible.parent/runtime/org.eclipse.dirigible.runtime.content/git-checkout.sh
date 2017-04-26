#!/bin/bash

#exec 2>/dev/null

####### Updates Registry's content
python updateRegistryContent.py

####### Prepare content.zip files

### zip full content
cd src/content/db/
rm ../../../content.zip
zip -r ../../../content.zip *

### zip minimal content
rm ../../../content-min.zip
zip -r ../../../content-min.zip dirigible/registry/public dirigible/registry/conf

####### Copy content.zip files

### copy to releng
cd ../../..
cp content.zip ../../releng/all.tomcat/src/main/resources/content/repository.zip
cp content-min.zip ../../releng/runtime.tomcat/src/main/resources/content/repository.zip
cp content-min.zip ../../releng/air.tomcat/src/main/resources/content/repository.zip