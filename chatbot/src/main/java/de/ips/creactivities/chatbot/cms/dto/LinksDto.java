package de.ips.creactivities.chatbot.cms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LinksDto {

    private List<LinkDto> self;
    private List<LinkDto> collection;
    private List<LinkDto> about;
    private List<LinkDto> author;
    private List<LinkDto> replies;

    @JsonProperty("version-history")
    private List<LinkDto> versionHistory;

    @JsonProperty("predecessor-version")
    private List<LinkDto> predecessorVersion;

    @JsonProperty("wp:attachment")
    private List<LinkDto> wpAttachment;

    @JsonProperty("wp:term")
    private List<LinkDto> wpTerm;

    private List<LinkDto> curies;

}
