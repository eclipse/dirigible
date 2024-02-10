package org.eclipse.dirigible.components.tenants.tenant;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class TenantResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.getParameterAnnotation(Tenant.class) != null && parameter.getParameterType() == String.class)
                || (parameter.getParameterAnnotation(TenantId.class) != null && parameter.getParameterType()
                                                                                         .getTypeName()
                                                                                         .equals("long"));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        if (parameter.getParameterAnnotation(Tenant.class) != null) {
            return TenantContext.getCurrentTenant();
        }
        return TenantContext.getCurrentTenantId();
    }
}
