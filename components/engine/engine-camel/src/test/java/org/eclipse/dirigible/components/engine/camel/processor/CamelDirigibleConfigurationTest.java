package org.eclipse.dirigible.components.engine.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.component.platform.http.PlatformHttpComponent;
import org.apache.camel.component.platform.http.spi.PlatformHttpEngine;
import org.apache.camel.component.platform.http.springboot.CamelRequestHandlerMapping;
import org.apache.camel.component.platform.http.springboot.SpringBootPlatformHttpAutoConfiguration;
import org.eclipse.dirigible.components.engine.camel.config.CamelDirigibleConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CamelDirigibleConfigurationTest {

    @Test
    void createCamelRequestHandlerMapping() {
        CamelContext camelContext = Mockito.mock(CamelContext.class);
        CamelRequestHandlerMapping camelRequestHandlerMapping = Mockito.mock(CamelRequestHandlerMapping.class);
        PlatformHttpEngine httpEngine = Mockito.mock(PlatformHttpEngine.class);
        PlatformHttpComponent httpComponent = Mockito.mock(PlatformHttpComponent.class);
        when(camelContext.getComponent("platform-http", PlatformHttpComponent.class)).thenReturn(httpComponent);

        CamelRequestHandlerMapping res =
                new CamelDirigibleConfiguration().createCamelRequestHandlerMapping(camelContext, httpEngine, camelRequestHandlerMapping);

        verify(httpComponent, times(1)).removePlatformHttpListener(camelRequestHandlerMapping);
        assertNotNull(res, "CamelRequestHandlerMapping should not be null");
    }

    @Test
    public void testCamelConfigurationHasBeanFactoryMethodWeDependOn() {
        try {
            var platformHttpEngineRequestMappingMethod =
                    SpringBootPlatformHttpAutoConfiguration.class.getMethod("platformHttpEngineRequestMapping", PlatformHttpEngine.class);
            assertEquals(CamelRequestHandlerMapping.class, platformHttpEngineRequestMappingMethod.getReturnType(),
                    "SpringBootPlatformHttpAutoConfiguration::platformHttpEngineRequestMapping does not return expected type");
        } catch (NoSuchMethodException e) {
            fail("SpringBootPlatformHttpAutoConfiguration::platformHttpEngineRequestMapping does not exist", e);
        }
    }

    @Test
    public void testSpringBootPlatformHttpAutoConfigurationFullName() {
        assertEquals("org.apache.camel.component.platform.http.springboot.SpringBootPlatformHttpAutoConfiguration",
                SpringBootPlatformHttpAutoConfiguration.class.getName(),
                "Unexpected SpringBootPlatformHttpAutoConfiguration full class name");
    }

    @Test
    public void testCamelDirigibleConfigurationCorrectSpringAnnotations() {
        var classAnnotations = CamelDirigibleConfiguration.class.getAnnotations();
        assertEquals(1, classAnnotations.length, "Unexpected number of annotations");

        var configuration = firstAnnotationWithClass(classAnnotations, Configuration.class);
        assertTrue(configuration.isPresent(), "No @Configuration found on CamelDirigibleConfiguration");
    }

    @Test
    public void testCamelDirigibleConfigurationCorrectSpringBeanAnnotations() {
        Method beanFactoryMethod = getBeanFactoryMethodOrFail();

        var beanFactoryMethodAnnotations = beanFactoryMethod.getAnnotations();
        assertEquals(2, beanFactoryMethodAnnotations.length, "Unexpected number of annotations");

        var bean = firstAnnotationWithClass(beanFactoryMethodAnnotations, Bean.class);
        assertTrue(bean.isPresent(), "No @Bean found on CamelDirigibleConfiguration");

        var primary = firstAnnotationWithClass(beanFactoryMethodAnnotations, Primary.class);
        assertTrue(primary.isPresent(), "No @Primary found on CamelDirigibleConfiguration");
    }

    @Test
    public void testCamelDirigibleConfigurationBeanFactoryMethodReturnType() {
        Method beanFactoryMethod = getBeanFactoryMethodOrFail();
        assertEquals(CamelRequestHandlerMapping.class, beanFactoryMethod.getReturnType(),
                "Unexpected CamelDirigibleConfiguration::createCamelRequestHandlerMapping return type");
    }

    private static Optional<Annotation> firstAnnotationWithClass(Annotation[] annotations, Class<?> annotationClass) {
        return Arrays.stream(annotations)
                     .filter(a -> a.annotationType()
                                   .equals(annotationClass))
                     .findFirst();
    }

    private static Method getBeanFactoryMethodOrFail() {
        try {
            return CamelDirigibleConfiguration.class.getMethod("createCamelRequestHandlerMapping", CamelContext.class,
                    PlatformHttpEngine.class, CamelRequestHandlerMapping.class);
        } catch (Exception e) {
            fail("CamelDirigibleConfiguration::createCamelRequestHandlerMapping does not exist");
            throw new RuntimeException(e);
        }
    }
}
