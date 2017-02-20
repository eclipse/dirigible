import json
import os

class RegistryContent:
    def update(self):
        # TODO Traverse recursivly the 'content' folder
        self.updateContent('content/api.json')
        self.updateContent('content/registry.json')
        self.updateContent('content/misc.json')
        self.updateContent('content/templates/DataStructures.json')
        self.updateContent('content/templates/ScriptingServices.json')
        self.updateContent('content/templates/WebContent.json')
        self.updateContent('content/templates/WebContentForEntity.json')

    def updateContent(self, fileName):
		with open(fileName) as data_file:
		    templates = json.load(data_file)

		for i in range(0, len(templates)):
		    os.system("if cd " + templates[i]['path'] + "; then git pull; cd ../..; else git clone " + templates[i]['repository'] + " " + templates[i]['path'] + "; fi")
		    for j in range(0, len(templates[i]['content'])):
		        os.system("cp -r " + templates[i]['content'][j]['source'] + " " + templates[i]['content'][j]['target'])

registryContent = RegistryContent()
registryContent.update()
