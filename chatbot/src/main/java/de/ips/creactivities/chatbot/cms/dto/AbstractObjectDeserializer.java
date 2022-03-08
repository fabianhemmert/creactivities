package de.ips.creactivities.chatbot.cms.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public abstract class AbstractObjectDeserializer<T> extends JsonDeserializer<T> {

    public T internalDeserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Class<T> clazz) throws IOException {

        T result = null;
        String text = jsonParser.getText();

        if (!"[".equalsIgnoreCase(text) && !text.isEmpty() && !text.isBlank()) {
            result = deserializationContext.readValue(jsonParser, clazz);
        }

        return result;
    }
}
