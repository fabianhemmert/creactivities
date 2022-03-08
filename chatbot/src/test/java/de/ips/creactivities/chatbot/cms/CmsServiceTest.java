package de.ips.creactivities.chatbot.cms;


import de.ips.creactivities.chatbot.ChatbotProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class CmsServiceTest {

    @Disabled
    @Test
    public void testUploadSolution() throws IOException {
        ChatbotProperties properties = new ChatbotProperties();
        properties.setCmsBaseUrl("https://creactivities.docker.as-labs.com/wp-json/");
        properties.setCmsUser("your-username-here");
        properties.setCmsPassword("your-password-here");

        RestTemplateConfiguration restTemplateConfiguration = new RestTemplateConfiguration();
        RestTemplate template = restTemplateConfiguration.cmsRestTemplate();

        byte[] solution = Files.readAllBytes(Paths.get("src/test/resources/cms/test-solution.png"));

        CmsService cmsService = new CmsService(template, properties);
        cmsService.uploadSolution("user", "challenge", "Text", Arrays.asList(solution));

    }

}
