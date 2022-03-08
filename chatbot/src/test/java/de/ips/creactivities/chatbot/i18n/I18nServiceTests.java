package de.ips.creactivities.chatbot.i18n;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class I18nServiceTests {

    @Test
    void testExceptionHandling() {
        I18nService service = new I18nService();
        service.initializeLanguage("fr", "abc");
        service.localize("tr", I18n.TEST);
        Assertions.assertTrue(true);
    }

    @Test
    void testIndividualLanguages() {
        I18nService service = new I18nService();
        service.initializeLanguage("de", "src/test/resources/i18n/messages_de.properties");
        service.initializeLanguage("en", "src/test/resources/i18n/messages_en.properties");
        Assertions.assertEquals("test_DE", service.localize("de", I18n.TEST));
        Assertions.assertEquals("test_EN", service.localize("en", I18n.TEST));
    }

    void testDefault() {
        I18nService service = new I18nService();
        service.initializeLanguage("de", "src/test/resources/i18n/messages_de.properties");
        Assertions.assertEquals("test_DE", service.localize("de", I18n.TEST));
        Assertions.assertEquals("#Translation not found!#", service.localize("de", I18n.TEST2));
    }
}
