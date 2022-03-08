package de.ips.creactivities.chatbot.cms.dto.options;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.ips.creactivities.chatbot.cms.dto.LinksDto;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmsOptionsDto {

    @JsonDeserialize(using = OptionsDto.Deserializer.class)
    @JsonProperty("acf")
    private OptionsDto option;

    @JsonProperty("_links")
    private LinksDto links;


}
