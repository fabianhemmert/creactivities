package de.ips.creactivities.chatbot.cms.dto.challenge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import de.ips.creactivities.chatbot.cms.dto.AbstractListDeserializer;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallengeSolutionDto {

    @JsonProperty("challenge_solution_text")
    private String text;

    @JsonProperty("challenge_solution_image")
    private String image;

    public static class Deserializer extends AbstractListDeserializer<ChallengeSolutionDto> {

        @Override
        public List<ChallengeSolutionDto> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, ChallengeSolutionDto.class);
        }
    }
}
