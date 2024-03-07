package org.eclipse.dirigible.components.base.synchronizer;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
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

public abstract class BaseSynchronizer<A extends Artefact, ID> implements Synchronizer<A, ID> {

    private static final Logger logger = LoggerFactory.getLogger(BaseSynchronizer.class);

    @Override
    public final boolean complete(TopologyWrapper<A> wrapper, ArtefactPhase flow) {
        A artefact = wrapper.getArtefact();
        ArtefactLifecycle lifecycle = artefact.getLifecycle();

        if (!multitenantExecution() || !isMultitenantArtefact(artefact)) {
            logger.debug("[{} will complete artefact with lifecycle [{}] in phase [{}]]...\nArtefact:[{}]", this, lifecycle, flow,
                    artefact);
            return completeImpl(wrapper, flow);
        }

        TenantContext tenantContext = BeanProvider.getTenantContext();
        List<TenantResult<Boolean>> results = tenantContext.executeForEachTenant(() -> {
            logger.debug("[{} will complete artefact with lifecycle [{}] in phase [{}]] for tenant [{}]...\\nArtefact:[{}]", this,
                    lifecycle, flow, tenantContext.getCurrentTenant(), artefact);
            artefact.setLifecycle(lifecycle);
            return completeImpl(wrapper, flow);
        });

        return results.stream()
                      .map(TenantResult<Boolean>::getResult)
                      .allMatch(r -> Boolean.TRUE.equals(r));
    }

    @Override
    public boolean multitenantExecution() {
        return false;
    }

    protected boolean isMultitenantArtefact(Artefact artefact) {
        return false;
    }

    protected abstract boolean completeImpl(TopologyWrapper<A> wrapper, ArtefactPhase flow);

    @Override
    public boolean isAccepted(Path file, BasicFileAttributes attrs) {
        return file.toString()
                   .endsWith(getFileExtension());
    }
}
