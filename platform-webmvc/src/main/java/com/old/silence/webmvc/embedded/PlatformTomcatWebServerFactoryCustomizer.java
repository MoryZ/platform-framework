package com.old.silence.webmvc.embedded;

import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

public class PlatformTomcatWebServerFactoryCustomizer
        implements WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory>, Ordered {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void customize(ConfigurableTomcatWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractHttp11Protocol) {
                ((AbstractHttp11Protocol<?>) handler).setMaxSavePostSize(-1);
            }
        });
    }
}
