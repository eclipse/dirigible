package org.eclipse.dirigible.components.base.synchronizer;

import java.util.Optional;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.base.tenant.TenantContext;

public abstract class BaseSynchronizer<A extends Artefact> implements Synchronizer<A> {

    @Override
    public final boolean complete(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow) {
        if (!isMultitenant()) {
            return completeImpl(wrapper, flow);
        }
        ArtefactLifecycle lifecycle = wrapper.getArtefact()
                                             .getLifecycle();
        TenantContext tenantContext = BeanProvider.getTenantContext();
        Optional<Boolean> result = tenantContext.executeForEachTenant(() -> {

            wrapper.getArtefact()
                   .setLifecycle(lifecycle);
            return completeImpl(wrapper, flow);
        });
        return result.orElse(true);
    }

    @Override
    public boolean isMultitenant() {
        return false;
    }

    protected abstract boolean completeImpl(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow);

}
