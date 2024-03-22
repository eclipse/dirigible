package org.eclipse.dirigible.components.engine.cms.s3.provider;

import org.eclipse.dirigible.components.engine.cms.CmsProvider;
import org.eclipse.dirigible.components.engine.cms.CmsProviderFactory;
import org.eclipse.dirigible.components.engine.cms.CmsProviderInitializationException;
import org.eclipse.dirigible.components.engine.cms.s3.repository.CmisS3Session;
import org.springframework.stereotype.Component;

@Component("cms-provider-s3")
class CmsProviderS3Factory implements CmsProviderFactory {

    @Override
    public CmsProvider create() throws CmsProviderInitializationException {
        CmisS3Session session = new CmisS3Session();
        return new CmsProviderS3(session);
    }
}
