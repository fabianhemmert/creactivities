package de.ips.creactivities.chatbot.cms.dto.level;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.ips.creactivities.chatbot.cms.dto.LinksDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmsLevelDto {

    @JsonProperty("id")
    @NotNull
    private Integer id;

    @JsonDeserialize(using = LevelDto.ObjectDeserializer.class)
    @JsonProperty("acf")
    private LevelDto level;

    @JsonProperty("_links")
    private LinksDto links;

}
