package org.eclipse.dirigible.services.spring.boot.websockets;

import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.runtime.websockets.service.WebsocketsService;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint("/websockets/v4/service/{endpoint}")
public class SpringBootWebsocketsService extends WebsocketsService {

}
