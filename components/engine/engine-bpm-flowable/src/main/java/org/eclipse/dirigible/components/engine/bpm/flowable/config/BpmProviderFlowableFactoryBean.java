package org.eclipse.dirigible.components.engine.bpm.flowable.config;

import org.eclipse.dirigible.components.engine.bpm.flowable.provider.BpmProviderFlowable;
import org.eclipse.dirigible.repository.api.IRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
class BpmProviderFlowableFactoryBean implements FactoryBean<BpmProviderFlowable>, DisposableBean, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(BpmProviderFlowableFactoryBean.class);

    private BpmProviderFlowable bpmProviderFlowable;
    private ApplicationContext applicationContext;

    @Override
    public void destroy() {
        LOGGER.info("Destroying bean...");
        if (bpmProviderFlowable != null) {
            bpmProviderFlowable.cleanup();
            this.bpmProviderFlowable = null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public synchronized BpmProviderFlowable getObject() {
        if (null == bpmProviderFlowable) {
            DataSource datasource = applicationContext.getBean("SystemDB", DataSource.class);
            IRepository repository = applicationContext.getBean(IRepository.class);
            DataSourceTransactionManager dataSourceTransactionManager = applicationContext.getBean(DataSourceTransactionManager.class);

            bpmProviderFlowable = new BpmProviderFlowable(datasource, repository, dataSourceTransactionManager, applicationContext);
        }
        return bpmProviderFlowable;
    }

    @Override
    public Class<BpmProviderFlowable> getObjectType() {
        return BpmProviderFlowable.class;
    }

}
