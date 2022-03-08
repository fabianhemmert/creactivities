package de.ips.creactivities.chatbot.cms.dto.level;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.ips.creactivities.chatbot.cms.dto.AbstractObjectDeserializer;
import de.ips.creactivities.chatbot.cms.dto.CmsReferenceDto;
import de.ips.creactivities.chatbot.cms.dto.challenge.ChallengeDto;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LevelDto {

    private Integer id;

    @JsonProperty("level_name")
    private String name;

    @JsonProperty("level_description")
    private String description;

    @JsonProperty("level_active")
    private Boolean active;

    @JsonProperty("level_image")
    private String image;

    @JsonDeserialize(using = LevelPrologueElement.Deserializer.class)
    @JsonProperty("level_prologue")
    private List<LevelPrologueElement> levelPrologue;

    @JsonDeserialize(using = CmsReferenceDto.Deserializer.class)
    @JsonProperty("level_challenges")
    private List<CmsReferenceDto> challenges;

    @JsonDeserialize(using = LevelEpilogueElement.Deserializer.class)
    @JsonProperty("level_epilogue")
    private List<LevelEpilogueElement> levelEpilogue;


    public static class ObjectDeserializer extends AbstractObjectDeserializer<LevelDto> {
        @Override
        public LevelDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, LevelDto.class);
        }
    }
}
