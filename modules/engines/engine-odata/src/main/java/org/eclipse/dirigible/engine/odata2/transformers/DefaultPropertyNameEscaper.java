package org.eclipse.dirigible.engine.odata2.transformers;

class DefaultPropertyNameEscaper implements ODataPropertyNameEscaper {

    /**
     * Replace unsupported olingo symbols with underscore symbol
     * The olingo do not allow dot symbol to be part of the property name.
     *
     * @param propertyName
     *          entity property name
     * @return replaced string
     * @see  org.apache.olingo.odata2.core.edm.provider.EdmNamedImplProv
     */
    @Override
    public String escape(String propertyName) {
        return propertyName.replace('.', '_');
    }
}
