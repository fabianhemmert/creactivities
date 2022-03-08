package de.ips.creactivities.chatbot.cms.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmsPostDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("format")
    private String format;

    @JsonProperty("status")
    private String status;

    @JsonProperty("content")
    private String content;

    @JsonProperty("caption")
    private byte[] caption;

    @JsonProperty("comment_status")
    private String commentStatus;

}
