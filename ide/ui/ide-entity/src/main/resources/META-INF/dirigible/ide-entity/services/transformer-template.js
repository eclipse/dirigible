exports.getTemplate = function() {
	    var template = {
            "name":"Entity Data to JSON Model Transformer",
            "description":"Model transformer template",
            "extension":"edm",
            "sources": [
                   {
                       "location": "/ide-entity/template/source.model.template", 
                       "action": "generate",
                       "rename": "{{fileNameBase}}.model",
                       "engine": "javascript",
                       "handler": "/ide-entity/template/transformer.js"
		    }]
        };
        return template;
}