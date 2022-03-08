package de.ips.creactivities.chatbot.cms.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmsReferenceDto {

    @JsonProperty("ID")
    private Integer id;

    public static class Deserializer extends AbstractListDeserializer<CmsReferenceDto> {

        @Override
        public List<CmsReferenceDto> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return internalDeserialize(jsonParser, deserializationContext, CmsReferenceDto.class);
        }
    }

}
