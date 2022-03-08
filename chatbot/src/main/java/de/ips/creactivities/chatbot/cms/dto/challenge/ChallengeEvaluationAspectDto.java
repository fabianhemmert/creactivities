package de.ips.creactivities.chatbot.cms.dto.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeEvaluationAspectDto {

    @JsonProperty("label")
    private String label;

    @JsonProperty("value")
    private String value;

}
