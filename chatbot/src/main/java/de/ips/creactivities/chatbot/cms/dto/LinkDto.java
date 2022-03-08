package de.ips.creactivities.chatbot.cms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkDto {

    private String id;

    private String name;

    private String href;

    private String taxonomy;

    private Boolean embeddable;

    private Boolean templated;

}
