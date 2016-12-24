#!/bin/bash

#exec 2>/dev/null

# template_web_new_or_edit
if cd target/template_web_new_or_edit; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_new_or_edit.git target/template_web_new_or_edit; fi
cp -r target/template_web_new_or_edit/template_web_new_or_edit/WebContentForEntity src/content/db/dirigible/templates

# zip the content
cd src/content/db/
zip -r ../../../content.zip *

# copy to the init plugin
cd ../../..
cp content.zip ../org.eclipse.dirigible.runtime.init/src