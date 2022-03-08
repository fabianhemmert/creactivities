package de.ips.creactivities.chatbot.cms.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmsPostResponseDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("guid")
    private CmsGuidDto guid;

}
