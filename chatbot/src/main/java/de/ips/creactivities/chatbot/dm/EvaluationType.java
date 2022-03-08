package de.ips.creactivities.chatbot.dm;

import de.ips.creactivities.chatbot.i18n.I18n;

public enum EvaluationType {
    VERSTEHEN(I18n.DIAGRAM_CRITERIA_UNDERSTAND, "understand"),
    VORSTELLEN(I18n.DIAGRAM_CRITERIA_IMAGINE, "imagine"),
    MACHEN(I18n.DIAGRAM_CRITERIA_MAKE, "create"),
    KRITISIEREN(I18n.DIAGRAM_CRITERIA_CRITICISM, "criticise");


    private final I18n translationEntry;
    private final String cmsRepresentation;

    private EvaluationType(I18n translationEntry, String cmsRepresentation) {
        this.translationEntry = translationEntry;
        this.cmsRepresentation = cmsRepresentation;
    }

    public I18n getAspect() {
        return translationEntry;
    }

    public static EvaluationType findByCmsString(String cms) {
        for (EvaluationType et : values()) {
            if (et.cmsRepresentation.equalsIgnoreCase(cms)) {
                return et;
            }
        }
        return null;
    }

    public String getCmsRepresentation() {
        return cmsRepresentation;
    }
}

