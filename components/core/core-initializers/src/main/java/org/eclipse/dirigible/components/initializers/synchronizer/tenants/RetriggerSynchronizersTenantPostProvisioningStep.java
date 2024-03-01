package org.eclipse.dirigible.components.initializers.synchronizer.tenants;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.tenant.TenantPostProvisioningStep;
import org.eclipse.dirigible.components.base.tenant.TenantProvisioningException;
import org.eclipse.dirigible.components.initializers.definition.DefinitionService;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class RetriggerSynchronizersTenantPostProvisioningStep implements TenantPostProvisioningStep {

    private static final Logger logger = LoggerFactory.getLogger(RetriggerSynchronizersTenantPostProvisioningStep.class);

    private final List<Synchronizer<Artefact>> multitenantSynchronizers;
    private final DefinitionService definitionService;
    private final SynchronizationProcessor synchronizationProcessor;

    RetriggerSynchronizersTenantPostProvisioningStep(List<Synchronizer<Artefact>> synchronizers, DefinitionService definitionService,
            SynchronizationProcessor synchronizationProcessor) {
        this.definitionService = definitionService;
        this.synchronizationProcessor = synchronizationProcessor;
        this.multitenantSynchronizers = synchronizers.stream()
                                                     .filter(Synchronizer::isMultitenant)
                                                     .collect(Collectors.toList());
    }

    @Override
    public void execute() throws TenantProvisioningException {
        Set<String> multitenantArtifactTypes = multitenantSynchronizers.stream()
                                                                       .map(Synchronizer::getArtefactType)
                                                                       .collect(Collectors.toSet());
        logger.info("Changing checksums for definitions with types in [{}]", multitenantArtifactTypes);
        definitionService.updateChecksums(StringUtils.EMPTY, multitenantArtifactTypes);

        logger.info("Retriggering synchronizers...");
        synchronizationProcessor.forceProcessSynchronizers();
        logger.info("Synchronizers have completed.");
    }

}
