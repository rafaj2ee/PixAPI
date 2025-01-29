package com.rafaj2ee.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rafaj2ee.util.Constant;

@Configuration
public class HttpAndHttpsConfig {

    @Value("${server.port}")
    private Integer port;

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

        // Configuração do conector HTTPS
        tomcat.addConnectorCustomizers(connector -> {
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(port);  // Porta HTTPS
        });

        // Configuração do conector HTTP para redirecionamento
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(Constant.DEFULT_PORT);  // Porta HTTP
        connector.setScheme("http");
        connector.setSecure(false);
        connector.setRedirectPort(port);  // Redireciona para HTTPS na mesma porta
        tomcat.addAdditionalTomcatConnectors(connector);

        return tomcat;
    }
}
