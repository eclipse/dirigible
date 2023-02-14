package org.eclipse.dirigible.components.ide.console.service;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * The Class DataTransferWebsocketConfig.
 */
@Configuration
@EnableWebSocket
public class ConsoleWebsocketConfig implements WebSocketConfigurer {
	
    /**
     * Register web socket handlers.
     *
     * @param registry the registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getConsoleWebsocketHandler(), BaseEndpoint.PREFIX_ENDPOINT_WEBSOCKETS + "ide/console");
    }

    /**
     * Gets the data transfer websocket handler.
     *
     * @return the data transfer websocket handler
     */
    @Bean
    public WebSocketHandler getConsoleWebsocketHandler() {
        return new ConsoleWebsocketHandler();
    }

}