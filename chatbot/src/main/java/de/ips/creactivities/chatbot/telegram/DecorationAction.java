package de.ips.creactivities.chatbot.telegram;

public enum DecorationAction {
    TYPING("typing"), UPLOAD_PHOTO("upload_photo"), UPLOAD_DOCUMENT("upload_document"), FIND_LOCATION("find_location");

    private final String action;

    private DecorationAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
