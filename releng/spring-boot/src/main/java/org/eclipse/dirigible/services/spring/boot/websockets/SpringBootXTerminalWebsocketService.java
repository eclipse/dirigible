package org.eclipse.dirigible.services.spring.boot.websockets;

import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.runtime.ide.terminal.service.XTerminalWebsocketService;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint(
		value = "/websockets/v4/ide/xterminal",
		subprotocols = {"tty"}
		)
public class SpringBootXTerminalWebsocketService extends XTerminalWebsocketService {

}
