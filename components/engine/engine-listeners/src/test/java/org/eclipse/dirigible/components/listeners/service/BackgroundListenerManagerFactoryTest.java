package org.eclipse.dirigible.components.listeners.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BackgroundListenerManagerFactoryTest {

    @InjectMocks
    private BackgroundListenerManagerFactory factory;

    @Mock
    private ActiveMQConnectionArtifactsFactory connectionArtifactsFactory;

    @Mock
    private Listener listener;

    @Test
    void testCreate() {
        BackgroundListenerManager manager = factory.create(listener);

        assertThat(manager).isNotNull();
    }

}
