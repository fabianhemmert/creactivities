package de.ips.creactivities.chatbot.cms.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmsGuidDto {

    @JsonProperty("rendered")
    private String rendered;

    @JsonProperty("raw")
    private String raw;

}
