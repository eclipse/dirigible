package org.eclipse.dirigible.components.base.synchronizer;

import org.eclipse.dirigible.components.base.artefact.Artefact;

public abstract class MultitenantBaseSynchronizer<A extends Artefact, ID> extends BaseSynchronizer<A, ID> {

    @Override
    public final boolean multitenantExecution() {
        return true;
    }

    @Override
    protected boolean isMultitenantArtefact(A artefact) {
        return true;
    }

}
