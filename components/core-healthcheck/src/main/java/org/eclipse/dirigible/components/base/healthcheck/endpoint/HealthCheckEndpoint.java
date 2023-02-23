package org.eclipse.dirigible.components.base.healthcheck.endpoint;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.base.healthcheck.status.HealthCheckStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "healthcheck")
public class HealthCheckEndpoint {
	
	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	@GetMapping
	public ResponseEntity<HealthCheckStatus> getStatus() {
		return ResponseEntity.ok(HealthCheckStatus.getInstance());

	}

}
