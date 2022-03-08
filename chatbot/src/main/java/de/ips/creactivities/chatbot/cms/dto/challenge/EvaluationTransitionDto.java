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
public class EvaluationTransitionDto {

    @JsonProperty("challenge_foreign_evalutions_transitions_transition")
    private String transition;

    public static class Deserializer extends AbstractListDeserializer<EvaluationTransitionDto> {

        @Override
        public List<EvaluationTransitionDto> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, EvaluationTransitionDto.class);
        }
    }
}
