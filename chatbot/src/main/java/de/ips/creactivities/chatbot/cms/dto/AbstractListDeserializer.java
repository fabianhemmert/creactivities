package de.ips.creactivities.chatbot.cms.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListDeserializer<T> extends JsonDeserializer<List<T>> {

    public List<T> internalDeserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Class<T> clazz) throws IOException {

        List<T> objects = new ArrayList<>();
        String text = jsonParser.getText();

        if (!"false".equalsIgnoreCase(text) && !text.isEmpty() && !text.isBlank()) {
            objects = deserializationContext.readValue(jsonParser, deserializationContext.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
        }

        return objects;
    }
}
