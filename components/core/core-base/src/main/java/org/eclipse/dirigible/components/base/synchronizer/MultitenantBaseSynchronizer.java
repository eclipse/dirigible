package org.eclipse.dirigible.components.base.synchronizer;

import org.eclipse.dirigible.components.base.artefact.Artefact;

public abstract class MultitenantBaseSynchronizer<A extends Artefact> extends BaseSynchronizer<A> {

    @Override
    public final boolean multitenantExecution() {
        return true;
    }

    @Override
    protected boolean isMultitenantArtefact(Artefact artefact) {
        return true;
    }

}
