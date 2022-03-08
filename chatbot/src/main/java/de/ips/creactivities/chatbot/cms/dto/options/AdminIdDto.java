package de.ips.creactivities.chatbot.cms.dto.options;

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
public class AdminIdDto {

    @JsonProperty("creactivities_admin_ids_username")
    private String username;

    @JsonProperty("creactivities_admin_ids_telegramid")
    private String identifier;


    public static class Deserializer extends AbstractListDeserializer<AdminIdDto> {

        @Override
        public List<AdminIdDto> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, AdminIdDto.class);
        }
    }
}
