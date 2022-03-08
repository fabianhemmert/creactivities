package de.ips.creactivities.chatbot.cms;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate cmsRestTemplate() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustAllStrategy());

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build());
            CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);

            int timeout = 60_000;
            factory.setConnectTimeout(timeout);
            factory.setConnectionRequestTimeout(timeout);
            factory.setReadTimeout(timeout);

            return new RestTemplate(factory);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Can not configure REST access for CMS System." + e.getMessage());
        }
    }

}
