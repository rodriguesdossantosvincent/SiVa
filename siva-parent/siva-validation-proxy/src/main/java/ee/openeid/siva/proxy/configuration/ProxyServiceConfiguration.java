package ee.openeid.siva.proxy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({ProxyConfigurationProperties.class})
public class ProxyServiceConfiguration {
    private ProxyConfigurationProperties properties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .rootUri(properties.getXroadUrl())
                .build();
    }

    @Autowired
    public void setProperties(ProxyConfigurationProperties properties) {
        this.properties = properties;
    }
}
