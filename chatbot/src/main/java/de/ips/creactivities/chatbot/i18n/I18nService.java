package de.ips.creactivities.chatbot.i18n;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class I18nService {

    private final static List<String> LANGUAGES = Arrays.asList("de", "en");
    private final Map<String, Map<String, String>> keys = new ConcurrentHashMap<>();

    public I18nService() {

        for (String lang : LANGUAGES) {
            initializeLanguage(lang, "i18n/messages_" + lang + ".properties");
        }
    }

    public void initializeLanguage(String code, String fileLocation) {
        try {
            keys.put(code, new HashMap<>());
            Properties props = new Properties();
            try(FileReader f = new FileReader(fileLocation, StandardCharsets.ISO_8859_1)) {
                props.load(f);
            }
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                keys.get(code).put(entry.getKey() + "", entry.getValue() + "");
            }
        } catch (IOException e) {
            log.error("Error occurred while trying to load translation file " + fileLocation + ", language " + code + " will not be available!", e);
        }
    }

    public String localize(String langCode, I18n key) {
        String value = "#Translation not found!#";
        if(langCode == null || !keys.containsKey(langCode)) {
            // Default to english when some other language is present.
            langCode = "en";
        }
        try {
            value = keys.get(langCode).get(key.toString());
        } catch (RuntimeException e) {
            log.info("Could not find translation for " + key.toString() + " in language " + langCode);
        }
        return value;
    }
}
