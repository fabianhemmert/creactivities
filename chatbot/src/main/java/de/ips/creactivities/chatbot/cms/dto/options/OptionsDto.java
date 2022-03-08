package de.ips.creactivities.chatbot.cms.dto.options;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.ips.creactivities.chatbot.cms.dto.AbstractObjectDeserializer;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionsDto {

    @JsonDeserialize(using = AdminIdDto.Deserializer.class)
    @JsonProperty("creactivities_admin_ids")
    private List<AdminIdDto> admins;

    /**
     * Datenschutz
     */
    @JsonProperty("creactivities_content_privacy")
    private String policy;

    @JsonProperty("creactivities_content_privacy_image")
    private String policyImage;

    /**
     * Nutzungsbedingungen
     */
    @JsonProperty("creactivities_content_privacy_toc")
    private String termsOfService;

    @JsonProperty("creactivities_content_privacy_toc_image")
    private String termsOfServiceImage;

    /**
     * Verwertungsrechte
     */
    @JsonProperty("creactivities_content_privacy_ip")
    private String rightsOfUse;

    @JsonProperty("creactivities_content_privacy_ip_image")
    private String rightsOfUseImage;

    @JsonProperty("admin_group_chat_id")
    private String adminGroupChatId;

    @JsonProperty("content_story_introduction")
    private String storyIntroduction;


    public static class Deserializer extends AbstractObjectDeserializer<OptionsDto> {
        @Override
        public OptionsDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, OptionsDto.class);
        }
    }

}

