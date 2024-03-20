package org.eclipse.dirigible.components.engine.cms.s3.provider;

import org.eclipse.dirigible.components.engine.cms.CmsProvider;
import org.eclipse.dirigible.components.engine.cms.CmsProviderFactory;
import org.eclipse.dirigible.components.engine.cms.CmsProviderInitializationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("cms-provider-s3")
class CmsProviderS3Factory implements CmsProviderFactory {
    @Override
    public CmsProvider create() {
        try {
            return new CmsProviderS3();
        } catch (IOException ex) {
            throw new CmsProviderInitializationException("Failed to create S3 CMS provider.", ex);
        }
    }
}
