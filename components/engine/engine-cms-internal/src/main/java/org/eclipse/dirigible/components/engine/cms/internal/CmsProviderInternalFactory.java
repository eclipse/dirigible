package org.eclipse.dirigible.components.engine.cms.internal;

import org.eclipse.dirigible.components.engine.cms.CmsProvider;
import org.eclipse.dirigible.components.engine.cms.CmsProviderFactory;
import org.eclipse.dirigible.components.engine.cms.internal.provider.CmsProviderInternal;
import org.springframework.stereotype.Component;

@Component("cms-provider-internal")
class CmsProviderInternalFactory implements CmsProviderFactory {
    @Override
    public CmsProvider create() {
        return new CmsProviderInternal();
    }
}
