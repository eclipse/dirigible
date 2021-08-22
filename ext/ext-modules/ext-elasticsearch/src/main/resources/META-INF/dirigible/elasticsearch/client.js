exports.getClient = function() {
    var native = org.eclipse.dirigible.api.elasticsearch.ElasticsearchFacade.getClient();

    return new Client(native);
} 

function Client(native) {
    this.native = native;

    this.documents = new DocumentApi(this.native);

    this.indexes = new IndexAPI(this.native);

    this.close = function() {
        this.native.close();
    }
}

function DocumentApi(native) {
    this.native = native;

    this.index = function(index, id, documentSource, xContentType = "JSON") {
        return org.eclipse.dirigible.api.elasticsearch.ElasticsearchFacade.indexDocument(this.native, index, id, documentSource, xContentType);
    }

    this.get = function(index, id) {
        var getResult = org.eclipse.dirigible.api.elasticsearch.ElasticsearchFacade.getDocument(this.native, index, id);

        return getResult.getSourceAsString();
    }

    this.exists = function(index, id) {
        return org.eclipse.dirigible.api.elasticsearch.ElasticsearchFacade.documentExists(this.native, index, id);
    }

    this.delete = function(index, id) {
        return org.eclipse.dirigible.api.elasticsearch.ElasticsearchFacade.deleteDocument(this.native, index, id);
    }
}

function IndexAPI(native) {
    this.native = native;

    this.create = function(name) {
        return org.eclipse.dirigible.api.elasticsearch.ElasticsearchFacade.createIndex(this.native, name);
    }

    this.delete = function(name) {
        return org.eclipse.dirigible.api.elasticsearch.ElasticsearchFacade.deleteIndex(this.native, name);
    }

    this.exists = function(name) {
        return org.eclipse.dirigible.api.elasticsearch.ElasticsearchFacade.indexExists(this.native, name);
    }
}