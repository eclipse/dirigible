#!/bin/bash

#exec 2>/dev/null


####### Clone or Pull APIs

# core_api
if cd target/core_api; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/core_api.git target/core_api; fi
cp -r target/core_api/core_api/ScriptingServices src/content/db/dirigible/registry/public

# registry
if cd target/registry; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/registry.git target/registry; fi
cp -r target/registry/registry/ScriptingServices src/content/db/dirigible/registry/public
cp -r target/registry/registry/WebContent src/content/db/dirigible/registry/public
cp -r target/registry/registry/ExtensionDefinitions src/content/db/dirigible/registry/public

# registry_develop
if cd target/registry_develop; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/registry_develop.git target/registry_develop; fi
cp -r target/registry_develop/registry_develop/ScriptingServices src/content/db/dirigible/registry/public
cp -r target/registry_develop/registry_develop/WebContent src/content/db/dirigible/registry/public
cp -r target/registry_develop/registry_develop/ExtensionDefinitions src/content/db/dirigible/registry/public

# registry_discover
if cd target/registry_discover; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/registry_discover.git target/registry_discover; fi
cp -r target/registry_discover/registry_discover/ScriptingServices src/content/db/dirigible/registry/public
cp -r target/registry_discover/registry_discover/WebContent src/content/db/dirigible/registry/public
cp -r target/registry_discover/registry_discover/ExtensionDefinitions src/content/db/dirigible/registry/public

# registry_operate
if cd target/registry_operate; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/registry_operate.git target/registry_operate; fi
cp -r target/registry_operate/registry_operate/ScriptingServices src/content/db/dirigible/registry/public
cp -r target/registry_operate/registry_operate/WebContent src/content/db/dirigible/registry/public
cp -r target/registry_operate/registry_operate/ExtensionDefinitions src/content/db/dirigible/registry/public

# registry_monitoring
if cd target/registry_monitoring; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/registry_monitoring.git target/registry_monitoring; fi
cp -r target/registry_monitoring/registry_monitoring/ScriptingServices src/content/db/dirigible/registry/public
cp -r target/registry_monitoring/registry_monitoring/WebContent src/content/db/dirigible/registry/public
cp -r target/registry_monitoring/registry_monitoring/ExtensionDefinitions src/content/db/dirigible/registry/public

# registry_ext_swagger_ui
if cd target/registry_ext_swagger_ui; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/registry_ext_swagger_ui.git target/registry_ext_swagger_ui; fi
cp -r target/registry_ext_swagger_ui/registry_ext_swagger_ui/ScriptingServices src/content/db/dirigible/registry/public
cp -r target/registry_ext_swagger_ui/registry_ext_swagger_ui/ExtensionDefinitions src/content/db/dirigible/registry/public

# swagger_ui
if cd target/swagger_ui; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/swagger_ui.git target/swagger_ui; fi
cp -r target/swagger_ui/swagger_ui/WebContent src/content/db/dirigible/registry/public

# core_test_runner
if cd target/core_test_runner; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/core_test_runner.git target/core_test_runner; fi
cp -r target/core_test_runner/core_test_runner/ScriptingServices src/content/db/dirigible/registry/public
cp -r target/core_test_runner/core_test_runner/WebContent src/content/db/dirigible/registry/public

# qunit
if cd target/qunit; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/qunit.git target/qunit; fi
cp -r target/qunit/qunit/ScriptingServices src/content/db/dirigible/registry/public

# jasmine
if cd target/jasmine; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/jasmine.git target/jasmine; fi
cp -r target/jasmine/jasmine/ScriptingServices src/content/db/dirigible/registry/public

# jsmockito
if cd target/jsmockito; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/jsmockito.git target/jsmockito; fi
cp -r target/jsmockito/jsmockito/ScriptingServices src/content/db/dirigible/registry/public

# arestme
if cd target/arestme; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/arestme.git target/arestme; fi
cp -r target/arestme/arestme/ScriptingServices src/content/db/dirigible/registry/public

# core_users
if cd target/core_users; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/core_users.git target/core_users; fi
cp -r target/core_users/core_users/DataStructures src/content/db/dirigible/registry/public
cp -r target/core_users/core_users/ScriptingServices src/content/db/dirigible/registry/public
cp -r target/core_users/core_users/SecurityConstraints src/content/db/dirigible/registry/public
cp -r target/core_users/core_users/WebContent src/content/db/dirigible/registry/public

# tags
if cd target/tags; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/tags.git target/tags; fi
cp -r target/tags/tags/DataStructures src/content/db/dirigible/registry/public
cp -r target/tags/tags/ScriptingServices src/content/db/dirigible/registry/public

# docs_explorer
if cd target/docs_explorer; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/docs_explorer.git target/docs_explorer; fi
cp -r target/docs_explorer/docs_explorer/ScriptingServices src/content/db/dirigible/registry/public
cp -r target/docs_explorer/docs_explorer/SecurityConstraints src/content/db/dirigible/registry/public
cp -r target/docs_explorer/docs_explorer/WebContent src/content/db/dirigible/registry/public



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
if cd target/template_web_launchpad; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_launchpad.git target/template_web_launchpad; fi
cp -r target/template_web_launchpad/template_web_launchpad/WebContent src/content/db/dirigible/templates

# template_web_launchpad_item
if cd target/template_web_launchpad_item; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_launchpad_item.git target/template_web_launchpad_item; fi
cp -r target/template_web_launchpad_item/template_web_launchpad_item/WebContent src/content/db/dirigible/templates

# template_web_index_page
if cd target/template_web_index_page; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_index_page.git target/template_web_index_page; fi
cp -r target/template_web_index_page/template_web_index_page/WebContent src/content/db/dirigible/templates

# template_web_sample_form
if cd target/template_web_sample_form; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_sample_form.git target/template_web_sample_form; fi
cp -r target/template_web_sample_form/template_web_sample_form/WebContent src/content/db/dirigible/templates


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

# template_web_markers_on_map
if cd target/template_web_markers_on_map; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_markers_on_map.git target/template_web_markers_on_map; fi
cp -r target/template_web_markers_on_map/template_web_markers_on_map/WebContentForEntity src/content/db/dirigible/templates


# template_web_discussions
if cd target/template_web_discussions; then git pull; cd ../..; else git clone https://github.com/dirigiblelabs/template_web_discussions.git target/template_web_discussions; fi
cp -r target/template_web_discussions/template_web_discussions/WebContent src/content/db/dirigible/templates


####### Prepare content.zip

# zip the content
cd src/content/db/
rm ../../../content.zip
zip -r ../../../content.zip *
#tar -czvf ../../../content.zip *

# copy to the init plugin
cd ../../..
cp content.zip ../org.eclipse.dirigible.runtime.init/src