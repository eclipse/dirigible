package org.eclipse.dirigible.services.spring.boot.websockets;

import javax.websocket.server.ServerEndpoint;

import org.eclipse.dirigible.engine.js.graalvm.debugger.DebuggerWebsocketService;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint(value = "/websockets/v4/ide/debugger/{path}")
public class SpringBootDebuggerWebsocketService extends DebuggerWebsocketService {

}
