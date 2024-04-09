package org.eclipse.dirigible.components.tenants.endpoint;

import java.util.List;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "tenants")
public class TenantEndpoint {


    private final TenantService tenantService;

    /**
     * Instantiates a new tenants endpoint.
     *
     * @param tenantService the tenant service
     */
    @Autowired
    public TenantEndpoint(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Gets the all.
     *
     * @return the all
     */
    @GetMapping
    public ResponseEntity<List<Tenant>> getAll() {
        return ResponseEntity.ok(tenantService.getAll());
    }

}
