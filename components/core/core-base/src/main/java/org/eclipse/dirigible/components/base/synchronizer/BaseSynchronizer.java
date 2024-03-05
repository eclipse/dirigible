package org.eclipse.dirigible.components.base.synchronizer;

import java.util.List;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.base.tenant.TenantResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseSynchronizer<A extends Artefact> implements Synchronizer<A> {

    private static final Logger logger = LoggerFactory.getLogger(BaseSynchronizer.class);

    @Override
    public final boolean complete(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow) {
        Artefact artefact = wrapper.getArtefact();
        ArtefactLifecycle lifecycle = artefact.getLifecycle();

        if (!isMultitenant() || !isMultitenantArtefact(wrapper.getArtefact())) {
            logger.debug("[{} will complete artifact with lifecycle [{}] in phase [{}]]", this, lifecycle, flow);
            return completeImpl(wrapper, flow);
        }

        TenantContext tenantContext = BeanProvider.getTenantContext();
        List<TenantResult<Boolean>> results = tenantContext.executeForEachTenant(() -> {
            logger.debug("[{} will complete artifact with lifecycle [{}] in phase [{}]] for tenant [{}]", this, lifecycle, flow,
                    tenantContext.getCurrentTenant());
            artefact.setLifecycle(lifecycle);
            return completeImpl(wrapper, flow);
        });

        return results.stream()
                      .map(TenantResult<Boolean>::getResult)
                      .allMatch(r -> Boolean.TRUE.equals(r));
    }

    @Override
    public boolean isMultitenant() {
        return false;
    }

    protected boolean isMultitenantArtefact(Artefact artefact) {
        return false;
    }

    protected abstract boolean completeImpl(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow);

}
