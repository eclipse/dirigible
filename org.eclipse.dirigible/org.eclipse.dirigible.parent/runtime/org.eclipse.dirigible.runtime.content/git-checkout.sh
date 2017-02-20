#!/bin/bash

#exec 2>/dev/null

####### Updates Registry's content
python updateRegistryContent.py

####### Prepare content.zip

# zip the content
cd src/content/db/
rm ../../../content.zip
zip -r ../../../content.zip *
#tar -czvf ../../../content.zip *

# copy to the init plugin
cd ../../..
cp content.zip ../org.eclipse.dirigible.runtime.init/src
