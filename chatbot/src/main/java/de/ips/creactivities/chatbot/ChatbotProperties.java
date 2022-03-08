package de.ips.creactivities.chatbot;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "chatbot")
@Data
public class ChatbotProperties {

    @NotNull
    String name;
    @URL
    String api;
    String apiKey;

    @URL
    String cmsBaseUrl;

    String cmsUser;

    String cmsPassword;

    int numberOfRequiredEvaluations;

    int evaluationsMaxWaitTime;
}
