package de.ips.creactivities.chatbot.cms.dto.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.ips.creactivities.chatbot.cms.dto.LinksDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmsChallengeDto {

    @JsonProperty("id")
    @NotNull
    private Integer id;

    @JsonDeserialize(using = ChallengeDto.ObjectDeserializer.class)
    @JsonProperty("acf")
    private ChallengeDto challenge;

    @JsonProperty("_links")
    private LinksDto links;

}
