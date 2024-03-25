package org.eclipse.dirigible.components.engine.cms;

public interface CmsProviderFactory {
    CmsProvider create() throws CmsProviderInitializationException;
}
