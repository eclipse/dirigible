#!/bin/bash

#exec 2>/dev/null



####### Clone or Pull Templates



#### ScriptingServices

# template_sql_sample
if cd target/template_sql_sample; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_sql_sample.git target/template_sql_sample; fi
cp -r target/template_sql_sample/template_sql_sample/ScriptingServices src/content/db/dirigible/templates

# template_js_database_access
if cd target/template_js_database_access; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_js_database_access.git target/template_js_database_access; fi
cp -r target/template_js_database_access/template_js_database_access/ScriptingServices src/content/db/dirigible/templates

# template_js_database_read
if cd target/template_js_database_read; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_js_database_read.git target/template_js_database_read; fi
cp -r target/template_js_database_read/template_js_database_read/ScriptingServices src/content/db/dirigible/templates

# template_js_guid_lib
if cd target/template_js_guid_lib; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_js_guid_lib.git target/template_js_guid_lib; fi
cp -r target/template_js_guid_lib/template_js_guid_lib/ScriptingServices src/content/db/dirigible/templates

# template_js_database_crud
if cd target/template_js_database_crud; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_js_database_crud.git target/template_js_database_crud; fi
cp -r target/template_js_database_crud/template_js_database_crud/ScriptingServices src/content/db/dirigible/templates

# template_js_hello_world
if cd target/template_js_hello_world; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_js_hello_world.git target/template_js_hello_world; fi
cp -r target/template_js_hello_world/template_js_hello_world/ScriptingServices src/content/db/dirigible/templates

# template_terminal_uname
if cd target/template_terminal_uname; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_terminal_uname.git target/template_terminal_uname; fi
cp -r target/template_terminal_uname/template_terminal_uname/ScriptingServices src/content/db/dirigible/templates

# template_js_database_crud_extended
if cd target/template_js_database_crud_extended; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_js_database_crud_extended.git target/template_js_database_crud_extended; fi
cp -r target/template_js_database_crud_extended/template_js_database_crud_extended/ScriptingServices src/content/db/dirigible/templates

# template_js_database_crud_dependent
if cd target/template_js_database_crud_dependent; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_js_database_crud_dependent.git target/template_js_database_crud_dependent; fi
cp -r target/template_js_database_crud_dependent/template_js_database_crud_dependent/ScriptingServices src/content/db/dirigible/templates


#### WebContent

# template_web_launchpad
# if cd target/template_web_launchpad; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_launchpad.git target/template_web_launchpad; fi
# cp -r target/template_web_launchpad/template_web_launchpad/WebContent src/content/db/dirigible/templates

# template_web_launchpad_item
# if cd target/template_web_launchpad_item; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_launchpad_item.git target/template_web_launchpad_item; fi
# cp -r target/template_web_launchpad_item/template_web_launchpad_item/WebContent src/content/db/dirigible/templates



#### WebContentForEntity

# template_web_list_and_manage
if cd target/template_web_list_and_manage; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_list_and_manage.git target/template_web_list_and_manage; fi
cp -r target/template_web_list_and_manage/template_web_list_and_manage/WebContentForEntity src/content/db/dirigible/templates

# template_web_new_or_edit
if cd target/template_web_new_or_edit; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_new_or_edit.git target/template_web_new_or_edit; fi
cp -r target/template_web_new_or_edit/template_web_new_or_edit/WebContentForEntity src/content/db/dirigible/templates

# template_web_list_and_details
if cd target/template_web_list_and_details; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_list_and_details.git target/template_web_list_and_details; fi
cp -r target/template_web_list_and_details/template_web_list_and_details/WebContentForEntity src/content/db/dirigible/templates

# template_web_master_details_master
if cd target/template_web_master_details_master; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_master_details_master.git target/template_web_master_details_master; fi
cp -r target/template_web_master_details_master/template_web_master_details_master/WebContentForEntity src/content/db/dirigible/templates

# template_web_master_details_details
if cd target/template_web_master_details_details; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_master_details_details.git target/template_web_master_details_details; fi
cp -r target/template_web_master_details_details/template_web_master_details_details/WebContentForEntity src/content/db/dirigible/templates




####### Prepare content.zip

# zip the content
cd src/content/db/
rm ../../../content.zip
zip -r ../../../content.zip *

# copy to the init plugin
cd ../../..
cp content.zip ../org.eclipse.dirigible.runtime.init/src